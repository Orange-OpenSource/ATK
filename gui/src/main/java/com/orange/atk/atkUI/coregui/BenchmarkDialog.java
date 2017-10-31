package com.orange.atk.atkUI.coregui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.orange.atk.atkUI.corecli.Configuration;

import com.orange.atk.phone.PhoneInterface;
import com.orange.atk.phone.detection.AutomaticPhoneDetection;
import com.orange.atk.platform.Platform;

public class BenchmarkDialog extends JDialog {
	
	public static ImageIcon icon = null;
	private static final String icondescr = "ATK";

	private static final String BENCHMARK_PACKAGE_NAME = "com.antutu.ABenchMark";
	private static final Pattern patternScore = Pattern.compile("AnTuTuBenchmarkScore\\([ 0-9]+\\):\\s*(\\w+)\\s*:\\s*(\\d+)");
	private static final Pattern patternInfo = Pattern.compile("AnTuTuBenchmarkInfo\\([ 0-9]+\\):\\s*(\\d+%)\\s*:\\s*(.+)");

	private String outputDir;
	private String outputFile;

    private JPanel panel = new JPanel(new BorderLayout());
    private JLabel lblStatus = new JLabel();
    private String infoText;
    private JButton btnGo = new JButton("New benchmark");

    private JProgressBar progressBar = new JProgressBar();
    
	InfiniteProgressPanel glassPane=null;

	private DefaultListModel deviceListModel;
	PhoneInterface currentPhone=null;
	private JScrollPane scrollableDeviceList;
	private JList deviceList;
	private DefaultListModel perfListModel;
	private JList perfList;
	private JScrollPane scrollablePerfList;
	JFreeChart sbchart;
	DefaultCategoryDataset dataset;
	List selectedDevices=new ArrayList();
	List selectedPerfs=new ArrayList();
	HashMap<String, Color> perfColor;

	public BenchmarkDialog(){
		super(CoreGUIPlugin.mainFrame, true);
		
		outputDir=Configuration.getProperty("benchmarkDir");
		if(outputDir.length()==0){
			JOptionPane.showMessageDialog(this, "You have to configure a directory to store benchmark results","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		outputFile=outputDir+Platform.FILE_SEPARATOR+"benchmark.csv";
		File file = new File(outputFile);	
		if(!file.exists()){
			Logger.getLogger(this.getClass() ).info("initializing results database in "+outputFile);
			try {
				BufferedWriter os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
				os.write("date,model,version,memory,integer,float,score2d,score3d,database,sdwrite,sdread,score\n");
				os.write("2013-04-26 16:30,GT-I9100,4.0.3,1472,1605,1349,1403,4413,375,1370,0,10754\n");
				os.write("2013-05-02 16:51,Nexus 4,4.2.2,2344,3929,1751,1573,6320,535,1500,1900,16792\n");
				os.write("2013-05-31 11:02,Galaxy Nexus,4.2,1217,1565,1349,487,2014,410,77,190,7309\n");
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		setPreferredSize(new Dimension(1024, 600));
        URL iconURL = CoreGUIPlugin.getMainIcon();
		icon = new ImageIcon(iconURL, icondescr);
		setIconImage(icon.getImage());
		setTitle("Benchmark");
		
		
		deviceListModel = new DefaultListModel();
		scrollableDeviceList = new JScrollPane();
		scrollablePerfList = new JScrollPane();
		deviceList = new JList(deviceListModel);
		perfListModel = new DefaultListModel();
		perfList = new JList(perfListModel);
		
		currentPhone = AutomaticPhoneDetection.getInstance().getDevice();
		glassPane = new InfiniteProgressPanel( );
		glassPane.setOpaque(false);
		setGlassPane(glassPane);
		btnGo.setPreferredSize(new Dimension(60, 30));
		
		dataset = new DefaultCategoryDataset();
		initializeDataset(null,null);
		sbchart = createChart(dataset);
		
		final ChartPanel pnl = new ChartPanel(sbchart);
		pnl.setPreferredSize(new java.awt.Dimension(450, 350));

		Box deviceListBox = Box.createVerticalBox();
		deviceListBox.add(new JLabel("List of devices"));

		Box perfListBox = Box.createVerticalBox();
		perfListBox.add(new JLabel("Performance"));

		scrollableDeviceList.setViewportView(deviceList);
		deviceListBox.add(scrollableDeviceList);

		scrollablePerfList.setViewportView(perfList);
		perfListBox.add(scrollablePerfList);

		perfListModel.insertElementAt("cpu", 0);
		perfListModel.insertElementAt("mem", 1);
		perfListModel.insertElementAt("3d", 1);

		ActionListener launchBenchListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				new Thread(new SearchPackage()).start();
				new Thread(new ReadLogcat()).start();
				glassPane.setVisible(true);
				glassPane.start( );
			}
		};

		deviceList.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent evt) {
	            if (evt.getValueIsAdjusting()) {
	                return;
	            }
	            selectedDevices.clear();
	            Collections.addAll(selectedDevices, deviceList.getSelectedValues());
	            initializeDataset(selectedDevices,selectedPerfs);
				sbchart.fireChartChanged();
	            
	        }
	    });
		
		perfList.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent evt) {
	            if (evt.getValueIsAdjusting()) {
	                return;
	            }
	            selectedPerfs.clear();
	            Collections.addAll(selectedPerfs,perfList.getSelectedValues());
	            initializeDataset(selectedDevices,selectedPerfs);
				sbchart.fireChartChanged();
	        }
	    });
		btnGo.addActionListener(launchBenchListener);
		progressBar.setPreferredSize(new Dimension(150, 18));
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		JPanel rightBar = new JPanel(new BorderLayout(5, 0));
		rightBar.setLayout(new BoxLayout(rightBar, BoxLayout.PAGE_AXIS));
		rightBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		rightBar.add(btnGo);
		rightBar.add(deviceListBox);
		rightBar.add(perfListBox);

		JPanel statusBar = new JPanel(new BorderLayout(5, 0));
		statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		statusBar.add(lblStatus, BorderLayout.CENTER);
		statusBar.add(progressBar, BorderLayout.EAST);

		panel.add(rightBar, BorderLayout.EAST);
		panel.add(pnl, BorderLayout.CENTER);

		panel.add(statusBar, BorderLayout.SOUTH);

		getContentPane().add(panel);
	    pack();
	    setVisible(true);
	}
	
	private class ReadLogcat implements Runnable{
		/*I/AnTuTuBenchmarkScore(17928): memory : 710
		I/AnTuTuBenchmarkScore(17928): integer : 775
		I/AnTuTuBenchmarkScore(17928): float : 657
		I/AnTuTuBenchmarkScore(17928): 2d : 481
		I/AnTuTuBenchmarkScore(17928): 3d : 1956
		I/AnTuTuBenchmarkScore(17928): database : 415
		I/AnTuTuBenchmarkScore(17928): sdwrite : 102
		I/AnTuTuBenchmarkScore(17928): sdread : 192
		I/AnTuTuBenchmarkScore(17928): score : 5288
		 */
		boolean isRunning=false;
		public void run() {

			isRunning=true; 
			progressBar.setVisible(true);

			if(!(new File(outputDir).exists())){
				boolean success = new File(outputDir).mkdir();
				if (success) {
					Logger.getLogger(this.getClass() ).info(outputDir+" created");
				}
			}

			String line = "";
			BufferedReader bufferedReader=null;
			Process process = null;
			try {
				//Clear logcat to ensure we log the relevant test results
				process = Runtime.getRuntime().exec(new String[]{Platform.getInstance().getDefaultADBLocation(), "-s",currentPhone.getUID(),"logcat","-c"});
				process.waitFor();
				process = Runtime.getRuntime().exec(new String[]{Platform.getInstance().getDefaultADBLocation(), "-s",currentPhone.getUID(),"logcat","-v","time"});
				bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				Logger.getLogger(this.getClass() ).info("logcat running");

				while(isRunning) { 
					line = bufferedReader.readLine();
					if(line!=null){
						if(line.contains("AnTuTuBenchmarkScore")){
							Logger.getLogger(this.getClass() ).info(line);
							Matcher match = patternScore.matcher(line);
							if(match.find()){
								infoText+=match.group(1)+": "+match.group(2)+"<br>";
								lblStatus.setText("<html>"+infoText+"</html>");
							}else{
								Logger.getLogger(this.getClass() ).info("line doesn't match");
							}
						}else if(line.contains("AnTuTuBenchmarkInfo")){
							Logger.getLogger(this.getClass() ).info(line);
							Matcher match = patternInfo.matcher(line);
							if(match.find()){
								lblStatus.setText(match.group(1)+" "+match.group(2));
								progressBar.setValue(Integer.parseInt(match.group(1).split("%")[0]));
							}
							if(match.group(1).equals("100%")){
								Logger.getLogger(this.getClass() ).info("stop");
								isRunning=false;
								Thread.sleep(4000);
								
							}
						}
					}else{
						Thread.sleep(200);
					}
				}
				Logger.getLogger(this.getClass() ).info("stop logcat");
				process.destroy();
				glassPane.stop();
				
				lblStatus.setText("fetching results...");
				fetchResultFile();
			} catch (IOException e) {
				Logger.getLogger(this.getClass() ).error("could not read logcat");
			} catch (InterruptedException e) {
				Logger.getLogger(this.getClass() ).error("could not read logcat");
			} 		
		}

		
	}

	private class SearchPackage implements Runnable {

		private boolean installing=false;
		boolean foundBenchApk = false;

		@Override
		public void run() {

			if(currentPhone.getType() != PhoneInterface.TYPE_ANDROID)
			{	
				infoText="No device found<br>";
				lblStatus.setText("<html>"+infoText+"</html>");
				glassPane.stop();				
				return;
			}
			infoText="Benchmarking "+currentPhone.getName()+ " ("+currentPhone.getUID()+")";
			setTitle(infoText);
			while(foundBenchApk == false ){
				// look for com.antutu.ABenchMark
				for(String s: currentPhone.getMonitorList()){
					if(s.equals(BENCHMARK_PACKAGE_NAME)){
						foundBenchApk=true;
						break;
					}
				}
				if(foundBenchApk){
					String[] args = {Platform.getInstance().getDefaultADBLocation(), "-s",currentPhone.getUID(),"shell", "am", "start", "-a","android.intent.action.VIEW","com.antutu.ABenchMark/.ABenchMarkStartBench"};
					Process p;
					Runtime r =Runtime.getRuntime();
					BufferedReader in=null;
					try {
						p = r.exec(args);
					} catch (IOException e) {
						Logger.getLogger(this.getClass() ).error("exception while executing benchmark:"+e.getMessage());
						glassPane.stop();
						return;
					}
					return;
				}else{
					if( installing == false){
						int result=JOptionPane.showConfirmDialog(null, "AnTuTu Benchmark application not found. Would you like to install it ?","alert",JOptionPane.OK_CANCEL_OPTION);
						if(result == JOptionPane.OK_OPTION){
							installing=true;
							String[] args = {Platform.getInstance().getDefaultADBLocation(), "-s",currentPhone.getUID(),"shell", "am", "start", "market://details?id=com.antutu.ABenchMark"};
							try {
								Runtime.getRuntime().exec(args);
							} catch (IOException e) {
								Logger.getLogger(this.getClass() ).error("exception while launching Play: "+e.getMessage());
								glassPane.stop();
								return;
							}
						}else{
							glassPane.stop();
							return;
						}
					}else{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	 private void fetchResultFile() {
			BufferedWriter os = null;
			try {
				if(!(new File(outputDir).exists())){
					boolean success = new File(outputDir).mkdir();
					if (success) {
						Logger.getLogger(this.getClass() ).info(outputDir+" created");
					}
				}
				File file = new File(outputFile);	
				os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
				BufferedReader bufferedReader=null;
				Process process = null;
				Logger.getLogger(this.getClass() ).info("adb pull ");
				process = Runtime.getRuntime().exec(new String[]{Platform.getInstance().getDefaultADBLocation(), "-s",currentPhone.getUID(),"pull","/sdcard/.antutu/benchmark/history_scores/",outputDir});
				Logger.getLogger(this.getClass() ).info("waitFor "+process);
				//process.waitFor();
				bufferedReader = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				String l;
				while( (l = bufferedReader.readLine()) != null){
					Logger.getLogger(this.getClass() ).info(l);
				}
			} catch (IOException e) {
				Logger.getLogger(this.getClass() ).error(e);
			}
			Logger.getLogger(this.getClass() ).info("sorting");

			File files[]=(new File(outputDir)).listFiles(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".xml")) {
						return true;
					} else {
						return false;
					}
				}
			});
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2)
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
					
					try {
						Date d1 = sdf.parse(f1.getName().substring(0,18));
						Date d2 = sdf.parse(f2.getName().substring(0,18));
						return d2.compareTo(d1);
					} catch (ParseException e) {
						Logger.getLogger(this.getClass() ).info("error while sorting "+e);
					}
					return 0;
					
				} });
			File f=files[0];
			Logger.getLogger(this.getClass() ).info(f.getAbsolutePath());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			lblStatus.setText("saving results ...");
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);
				doc.getDocumentElement().normalize();

				if(doc.getDocumentElement().getNodeName().equals("scores")){
					NodeList nList = doc.getElementsByTagName("item");								 
					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;
							os.write(
									eElement.getElementsByTagName("date").item(0).getTextContent()+","+
											eElement.getElementsByTagName("model").item(0).getTextContent()+","+
											eElement.getElementsByTagName("version").item(0).getTextContent()+","+
											eElement.getElementsByTagName("memory").item(0).getTextContent()+","+
											eElement.getElementsByTagName("integer").item(0).getTextContent()+","+
											eElement.getElementsByTagName("float").item(0).getTextContent()+","+
											eElement.getElementsByTagName("score2d").item(0).getTextContent()+","+
											eElement.getElementsByTagName("score3d").item(0).getTextContent()+","+
											eElement.getElementsByTagName("database").item(0).getTextContent()+","+
											eElement.getElementsByTagName("sdwrite").item(0).getTextContent()+","+
											eElement.getElementsByTagName("sdread").item(0).getTextContent()+","+
											eElement.getElementsByTagName("score").item(0).getTextContent()+"\n"
									);
						}
					}
				}
				os.close();
				lblStatus.setText("results saved");
			} catch (ParserConfigurationException e) {
				Logger.getLogger(this.getClass() ).error(e);
			} catch (SAXException e) {
				Logger.getLogger(this.getClass() ).error(e);
			} catch (IOException e) {
				Logger.getLogger(this.getClass() ).error(e);
			}
			initializeDataset(null,null);
			sbchart.fireChartChanged();

		} 
	 /**
		 * Create chart
		 * @param dataset
		 * @return
		 */
		private JFreeChart createChart(final CategoryDataset dataset) {
	 
			 JFreeChart stackedChart = ChartFactory.createStackedBarChart("Device performance summary", "Category", "Value",
					dataset, PlotOrientation.VERTICAL, true, true, false);
	 
			CategoryPlot plot = (CategoryPlot) stackedChart.getPlot();

			
			 BarRenderer renderer = (BarRenderer) plot.getRenderer();
			 renderer.setDrawBarOutline(false);
			 // set up gradient paints for series...
			 GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.decode("#29A2C6"),
					 	0.0f, 0.0f, Color.decode("#29A2C6").darker());
			 GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.decode("#73B66B"),
					 0.0f, 0.0f, Color.decode("#73B66B").darker());
			 GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.decode("#EE0000"),
					 0.0f, 0.0f, Color.decode("#EE0000").darker());
			 renderer.setSeriesPaint(0, gp0);
			 renderer.setSeriesPaint(1, gp1);
			 renderer.setSeriesPaint(2, gp2);
			 renderer.setSeriesVisible(0, true);
			 renderer.setMaximumBarWidth(.20);
			return stackedChart;
		}
		
		/**
		 * Create dataset for chart in application usually get data from database
		 * with JDBC or collection
		 * 
		 * @return
		 */
		private void initializeDataset(List<String> devices, List<String> perfs) {
			
			try{
				dataset.clear();
			BufferedReader bReader =new BufferedReader(new FileReader(outputFile));
			String s;
			bReader.readLine(); //skip first line
			while ((s=bReader.readLine())!=null){
				//Logger.getLogger(this.getClass() ).debug(s);
				String datavalue [] = s.split(",");
				if(datavalue.length==12){
					if(devices==null || (devices!=null && devices.contains(datavalue[1]))){
						if(perfs==null || (perfs!=null && perfs.contains("cpu"))){
							dataset.addValue(Float.parseFloat(datavalue[4]),"cpu",datavalue[1]);
						}else{
							dataset.addValue(0,"cpu",datavalue[1]);
						}
						if(perfs==null || (perfs!=null && perfs.contains("mem"))){
							dataset.addValue(Float.parseFloat(datavalue[3]),"mem",datavalue[1]);
						}else{
							dataset.addValue(0,"mem",datavalue[1]);
						}
						if(perfs==null || (perfs!=null && perfs.contains("3d"))){
							dataset.addValue(Float.parseFloat(datavalue[7]),"3d",datavalue[1]);
						}else{
							dataset.addValue(0,"3d",datavalue[1]);
						}
					}
					if(!deviceListModel.contains(datavalue[1])){
						deviceListModel.addElement(datavalue[1]);
					}
				}
				
			}
			bReader.close();
			}catch(IOException e){
				Logger.getLogger(this.getClass() ).error(e);
			}
		}
}
