/*
 * Software Name : ATK
 *
 * Copyright (C) 2007 - 2012 France Télécom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------
 * File Name   : RealtimeGraph.java
 *
 * Created     : 07/09/2009
 * Author(s)   : Yvain Leyral
 */
package com.orange.atk.graphAnalyser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.AxisLocation;

import com.orange.atk.platform.Platform;


/**
 *
 * @author ywil8421
 */
@SuppressWarnings("serial")
public class RealtimeGraph extends javax.swing.JFrame  {

	private ChartPanel chartPanel;
	CreateGraph JaTKCharts;
	String JATKpath = "";
	private DefaultListModel listModelGraph;
	Map<String, PerformanceGraph> mapPerfGraph;
	Map<String, GraphMarker> mapAction = null;
	DefaultComboBoxModel comboModelLeft = null;
	DefaultComboBoxModel comboModelRight = null;
	private listenPerfGraphHandler listenPerf;
	private JPanel toolPane;


	/** Creates new form Paneljavalauncher */
	public RealtimeGraph(CreateGraph JaTKCharts) {
		this.JaTKCharts = JaTKCharts;
		chartPanel = JaTKCharts.getChartpanel();

		listModelGraph = new DefaultListModel();
		comboModelLeft = new DefaultComboBoxModel();
		comboModelRight = new DefaultComboBoxModel();
		mapPerfGraph = JaTKCharts.getMapPerfGraph();
		mapAction = JaTKCharts.getMapAction();

		chartPanel.setDomainZoomable(true);
		chartPanel.setRangeZoomable(true);
		chartPanel.setAutoscrolls(true);

		JATKpath =	Platform.getInstance().getJATKPath();

		initComponents();
		jListPerfGraph.setCellRenderer(new MyGraphCellRenderer());
		jComboBoxLeft.setRenderer(new MyGraphCellRenderer());
		jComboBoxRight.setRenderer(new MyGraphCellRenderer());
		jListGraph.setCellRenderer(new MyCellRenderer());

		addPerformanceslist();
		jListPerfGraph.getSelectionModel().addListSelectionListener(new listenPerfGraphHandler());
		jListPerfGraph.setSelectionInterval(0,jListPerfGraph.getModel().getSize()-1);
		this.setTitle("Real Time Graphics");
	}


	public void setListenerMarkerGraph(){
		listenPerf=  new listenPerfGraphHandler();
		jListGraph.getSelectionModel().addListSelectionListener(listenPerf);

	}



	public void close()
	{
		this.dispose();
	}

	public void addPerformanceslist() {
		int index = 0;
		listModelGraph.removeAllElements();
		if (mapPerfGraph != null) {
			Set<String> cles = mapPerfGraph.keySet();
			Iterator<String> it = cles.iterator();
			while (it.hasNext()) {
				String cle = (String) it.next();
				listModelGraph.insertElementAt(cle, index);
				index++;
			}
		}
	}

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jListGraph = new JList(listModelGraph);
		jPanelroot = new JPanel();
		jScrollPaneListGraph = new JScrollPane();
		jListPerfGraph = new JList(listModelGraph);
		jComboBoxLeft = new JComboBox(comboModelLeft);
		jComboBoxRight = new JComboBox(comboModelRight);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		jScrollPaneListGraph.setViewportView(jListPerfGraph);

		jComboBoxLeft.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxLeftActionPerformed(evt);
			}
		});

		jComboBoxRight.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jComboBoxRightActionPerformed(evt);
			}
		});


		jPanelroot.setLayout(new BorderLayout());
		jPanelroot.add(chartPanel, BorderLayout.CENTER);

		toolPane = new JPanel();
		toolPane.setLayout(new FlowLayout());

		toolPane.add(jComboBoxLeft);

		Box graphlistbox = Box.createVerticalBox();
		graphlistbox.add(new JLabel("List of Graph"));
		graphlistbox.add(jScrollPaneListGraph);
		toolPane.add(graphlistbox);

		toolPane.add(jComboBoxRight);
		
		
		jPanelroot.add(toolPane, BorderLayout.NORTH);

		setContentPane(jPanelroot);

		//a small size for small screen
		//  setMaximumSize(new Dimension(600,500));
		pack();

	}// </editor-fold>

	public void addUrlMarkerCheckBox(){
		urlMarkersCheckBox = new JCheckBox("Display Url markers");
		urlMarkersCheckBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				JaTKCharts.displayUrlMarkers(selected);
			}
		});
		toolPane.add(urlMarkersCheckBox);
		urlMarkersCheckBox.setSelected(true);
		toolPane.invalidate();
	}
	
	private void jComboBoxLeftActionPerformed(ActionEvent evt) {
		if (mapPerfGraph != null) {
			if (comboModelLeft.getSize() != 0 && comboModelLeft.getSelectedItem() != null) {
				String cle = comboModelLeft.getSelectedItem().toString();
				PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
				graph.setAxis(false, AxisLocation.BOTTOM_OR_RIGHT);
				graph.setAxis(true, AxisLocation.BOTTOM_OR_LEFT);
			}
		}
	}

	private void jComboBoxRightActionPerformed(ActionEvent evt) {
		if (mapPerfGraph != null) {
			if (comboModelRight.getSize() != 0 && comboModelRight.getSelectedItem() != null) {
				String cle = comboModelRight.getSelectedItem().toString();
				PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
				graph.setAxis(false, AxisLocation.BOTTOM_OR_LEFT);
				graph.setAxis(true, AxisLocation.BOTTOM_OR_RIGHT);
			}
		}
	}

	// Variables declaration - do not modify
	private JComboBox jComboBoxLeft;
	private JComboBox jComboBoxRight;
	private JList jListPerfGraph;
	private javax.swing.JList jListGraph;
	private javax.swing.JPanel jPanelroot;
	private javax.swing.JScrollPane jScrollPaneListGraph;
	// End of variables declaration
	private JCheckBox urlMarkersCheckBox;

	class listenPerfGraphHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			//TODO usage ?
			//ArrayList<PerformanceGraph> listGraphCPU = new ArrayList<PerformanceGraph>();
			//ArrayList<PerformanceGraph> listGraphMEM = new ArrayList<PerformanceGraph>();

			int index = 0;
			if (mapPerfGraph != null) {
				Set<String> cles = mapPerfGraph.keySet();
				Iterator<String> it = cles.iterator();
				while (it.hasNext()) {
					String cle = (String) it.next();

					PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
					if (lsm.isSelectedIndex(index)) {
						//Now Active
						if (!graph.isActive()) {
							// graph.drawnewline();
							JaTKCharts.drawGraph(cle);
							comboModelLeft.insertElementAt(cle, comboModelLeft.getSize());
							comboModelRight.insertElementAt(cle, comboModelRight.getSize());
						}
						//graph.setAxix(true, AxisLocation.BOTTOM_OR_RIGHT);
						/*if(cle.toLowerCase().contains(PerformanceGraph.memoryLabel)){
							if(listGraphMEM.size()>0){
								graph.setAxix(false, null);
								listGraphMEM.add(graph);
								listGraphMEM.get(0).getNumberaxis().setLabel("Memory ("+graph.getUnit()+")");
								listGraphMEM.get(0).getNumberaxis().setAxisLinePaint(Color.BLACK);
								listGraphMEM.get(0).getNumberaxis().setLabelPaint(Color.black);
								listGraphMEM.get(0).getNumberaxis().setTickLabelPaint(Color.black);
							}else{
								graph.getNumberaxis().setLabel(graph.getSerieName());
								graph.getNumberaxis().setAxisLinePaint(graph.getColor());
								graph.getNumberaxis().setTickLabelPaint(graph.getColor());
								graph.getNumberaxis().setLabelPaint(graph.getColor());
								listGraphMEM.add(graph);
							}
						}
						if(cle.toLowerCase().contains(PerformanceGraph.cpuLabel)){
							if(listGraphCPU.size()>0){
								graph.setAxix(false, null);
								listGraphCPU.add(graph);
								listGraphCPU.get(0).getNumberaxis().setLabel("CPU ("+graph.getUnit()+")");
								listGraphCPU.get(0).getNumberaxis().setAxisLinePaint(Color.BLACK);
								listGraphCPU.get(0).getNumberaxis().setLabelPaint(Color.black);
								listGraphCPU.get(0).getNumberaxis().setTickLabelPaint(Color.black);
							}else{
								graph.getNumberaxis().setLabel(graph.getSerieName());
								graph.getNumberaxis().setAxisLinePaint(graph.getColor());
								graph.getNumberaxis().setTickLabelPaint(graph.getColor());
								graph.getNumberaxis().setLabelPaint(graph.getColor());
								listGraphCPU.add(graph);
							}
						}*/
						
					} else {
						//now inactive 
						if (graph.isActive()) {
							JaTKCharts.undrawGraph(cle);
							comboModelLeft.removeElement(cle);
							comboModelRight.removeElement(cle);
						}
					}
					index++;
				}
				/*Range rangeCPU = computeRange(listGraphCPU);
				if(rangeCPU!=null)
					PerformanceGraph.setRangeCPU(rangeCPU);
				Range rangeMEM = computeRange(listGraphMEM);
				if(rangeMEM!=null)
					PerformanceGraph.setRangeMEM(rangeMEM);
*/

			}
		}

		/*public Range computeRange(ArrayList<PerformanceGraph> listGraph){
			Range range=null;
			double maxRange=0, minRange=0,diff=0;
			for(PerformanceGraph graph : listGraph){
				PlotList plts = graph.getPlts();
				if(plts==null)
					return null;
				double max = plts.getMax()/plts.getScale();
				double min = plts.getMin()/plts.getScale();

				if(max<min)//probably no points in the graph
					return null;

				if(range==null){
					maxRange = max;
					minRange = min;
				}
				else{
					maxRange = Math.max(max, maxRange);
					minRange = Math.min(min, minRange);
				}
				diff = (maxRange-minRange)*0.02;
				if(diff == 0)
					diff = max * 0.0001;
				range = new Range((double)minRange-diff,(double)maxRange+diff);
				if (range.getLength()==0) range = Range.expand(new Range(0.0, 0.5),0.03,0.0);

			}
			for(PerformanceGraph graph : listGraph)
				graph.getNumberaxis().setRange(range);
			return range;
		}*/
	}

	@SuppressWarnings("serial")
	class MyGraphCellRenderer extends JLabel implements ListCellRenderer {

		public MyGraphCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus)
		{
			
			if(value!=null){
				setText(value.toString());
				if(isSelected ){
					setBackground(Color.LIGHT_GRAY);
					setForeground(mapPerfGraph.get(value).getColor());
				}
				else{
					setBackground(Color.WHITE);
					setForeground(Color.BLACK);
				}

				if (mapPerfGraph != null) {
					Set<String> cles = mapPerfGraph.keySet();
					Iterator<String> it = cles.iterator();
					while (it.hasNext()) {
						String cle = (String) it.next();
						PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
						if(value.equals(graph.getSerieName().replace("Series ", "")))
						{
							setForeground(graph.getColor());
						}
					}
				}
			}
			return this;

		}
	}


	@SuppressWarnings("serial")
	class MyCellRenderer extends JLabel implements ListCellRenderer {

		public MyCellRenderer() {
			setOpaque(true);
		}	

		public Component getListCellRendererComponent(JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus)
		{

			if(value!=null){
				setText(value.toString());         
				if(isSelected )
					setBackground(Color.LIGHT_GRAY);
				else
					setBackground(Color.white);


				if (mapPerfGraph != null) {
					Set<String> cles = mapPerfGraph.keySet();
					Iterator<String> it = cles.iterator();
					while (it.hasNext()) {
						String cle = (String) it.next();
						PerformanceGraph graph = (PerformanceGraph) mapPerfGraph.get(cle);
						if(value.equals(graph.getSerieName().replace("Series ", "")))
						{
							setForeground(graph.getColor());
						}
					}
				}
			}
			return this;
		}
	}

}

