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
 * File Name   : Model.java
 *
 * Created     : 04/06/2009
 * Author(s)   : France Telecom
 */

package com.orange.atk.compModel;

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.orange.atk.platform.Platform;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;



/**
 * class for the Model in MVC architecture for images comparison
 * 
 * @author bvqj2105
 */

public class Model extends Observable {

	public static final String AUTOMATIC_PASS ="Passed";	
	public static final String MANUALLY_PASS ="Manually Passed";
	public static final String FAIL ="Failed";

	private String refDirectory="src/images/ref";
	private String testDirectory="src/images/test";

	private Document resultDoc;
	private Element resultRootElement;
	private Document useddoc;
	private static final String xmlReportName =	"ComparisonScreenShotReport.xml";
	private static final String maskXmlFileName = "masks.xml";
	private static final String refXmlFileName = "refDescription.xml";
	private static final String pdfReportName = "ComparisonScreenShotReport.pdf";
	private HashMap<String, ImageFileMask> refImage;
	private HashMap<Integer, Mask> refMask;
	private int maskHeight;
	private int maskWidth;
	private int imageHeight;
	private int imageWidth;
	private HashMap<String,String> refScDesc;
	private ArrayList<ComparaisonCouple> couples;
	private ProgressListener progressListener; 

	/** Creates a new instance of Model3
	 * @throws IOException */
	public Model(String refDir, String testDir, ProgressListener progressListener) throws IllegalArgumentException {

		refDirectory=refDir;
		testDirectory=testDir;
		this.progressListener = progressListener;
		couples = new ArrayList<ComparaisonCouple>();
		refImage = new HashMap<String, ImageFileMask>();
		refMask = new HashMap<Integer, Mask>();
		refScDesc =  new HashMap<String, String>();
		
		// initialize the model DOM and the ListModels
		initModel();

		//create the XML report file in the test directory
		printToFile(resultDoc, getTestDirectory()+Platform.FILE_SEPARATOR+xmlReportName);

	}

	public Model(String refDir, String testDir) throws IllegalArgumentException {
		this(refDir, testDir, null);
	}

	public void setProgressListener(ProgressListener listener) {
		progressListener = listener;
	}
	/**
	 * @param name
	 * @return the Element from the model DOM corresponding with the image whose name is in parameter
	 * @return null if not exists
	 */
	public Element getElementFromDOM(String name){
		NodeList nl = resultRootElement.getElementsByTagName("Test");
		int i = 0;
		while ( i < nl.getLength() && !nl.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(name)) {
			//	//Logger.getLogger(this.getClass() ).debug(nl.item(i).getAttributes().getNamedItem("name").getNodeValue());
			i++;

		}
		if (nl.item(i)!=null)
			return (Element) nl.item(i).getParentNode();// The <img> Element corresponding.
		else return null;
	}

	/**
	 * @return the number of failed couples
	 */
	public int getNbFail() {
		int nbFail=0;
		for (ComparaisonCouple cp : couples)
			if(cp.getPass().equals(FAIL))
				nbFail++;

		return nbFail;
	}

	/**
	 * @return the number of images couples
	 */
	public int getNbImages() {
		return couples.size();
	}

	/**
	 * @return the path to reference directory
	 */
	public String getRefDirectory(){
		//Logger.getLogger(this.getClass() ).debug("Model.getRefDiredtory");
		return refDirectory;
	}

	/**
	 * re-initiate the model with this new parameter
	 * @param rep - the path to reference directory
	 */
	public void setDirectories(String ref, String test) throws IllegalArgumentException {
		//Logger.getLogger(this.getClass() ).debug("Model.setRefDirectory");
		refDirectory =ref;
		testDirectory=test;
		initModel();
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the path to reference directory
	 */
	public String getTestDirectory(){
		return testDirectory;
	}

	public String getPdfReportName() {
		return pdfReportName;
	}



	public int getMaskHeight() {
		return maskHeight;
	}


	public int getMaskWidth() {
		return maskWidth;
	}


	public ImageFileMask getRefImage(String id) {
		return refImage.get(id);
	}
	public Set<String> getListRefImage() {
		return refImage.keySet();
	}

	public Mask getRefMask(Integer id) {
		return refMask.get(id);
	}
	public void addRefMask(Mask mask) {
		refMask.put(mask.getId(), mask);
	}
	
	public void setRefScDescription(String id, String description) {
		if (refScDesc.get(id) == null || !refScDesc.get(id).equals(description)) {
			refScDesc.put(id, description);
			this.saveRefDescriptions();
		}
	}
	
	public String getRefScDescription(String id) {
		String desc = refScDesc.get(id);
		if (desc == null) return "";
		else return desc;
	}

	public void removeRefMask(Mask mask) {
		refMask.remove(mask);
	}
	public Set<Integer> getListKeysetMask() {
		return refMask.keySet();
	}

	public Mask[] getListMask() {
		Mask[] listMask = new Mask[getListKeysetMask().size()];
		int i = 0;
		for(int Id : getListKeysetMask()){
			listMask[i] = getRefMask(Id);
			i++;
		}
		return listMask;
	}

	public int getImageHeight() {
		return imageHeight;
	}


	/**
	 * recompute differences between the two images from the i-th couple
	 * @param cp ComparaisonCouple
	 * @return 0 if there is no differences, >0 else
	 * @throws IOException
	 */
	public double recompute(ComparaisonCouple cp) throws IOException {
		//Logger.getLogger(this.getClass() ).debug("Model.recalcul");

		return calcDistance(ImageIO.read(cp.getImgRef().getImage()),
				ImageIO.read(cp.getImgTest()),
				cp.getMaskSum(),cp.getDif());

	}

	/**initialize the model :</br>
	 *  - listModels</br>
	 *  - DOM File</br>
	 *  - arrays masked and different set the currentIndex to 0
	 */
	private void initModel() throws IllegalArgumentException {
		//Logger.getLogger(this.getClass() ).debug("Model.initModel");
		File [] ref= getImageFilesFromDirectory(refDirectory);
		File [] test= getImageFilesFromDirectory(testDirectory);

		//First we need to compare to be sure all the image have the same size
		//We compare all the images to the first one, and report error if an image is different
		for(int i = 1; i < ref.length; i++){
			if(!fileSameSize(ref[0], ref[i])){
				String message="2 files have different sizes, " +
				"you must change the folders to compare.\n"+
				ref[0].getName()+" and\n"+ref[i].getName()+" have different size.";
				JOptionPane.showMessageDialog(null,message);
				Logger.getLogger(this.getClass()).error(message);
				throw new IllegalArgumentException(message);
			}
		}
		for(int i = 0; i < test.length; i++){
			if(!fileSameSize(ref[0], test[i])){
				String message="2 files have different sizes, " +
				"you must change the folders to compare.\n"+
				ref[0].getName()+" and\n"+test[i].getName()+" have different size.";
				JOptionPane.showMessageDialog(null,message);
				Logger.getLogger(this.getClass()).error(message);
				throw new IllegalArgumentException(message);
			}
		}

		//We had all the reference image in the table
		for(File refFile : ref)
			refImage.put(refFile.getName(), new ImageFileMask(refFile,this));

		initRefDescriptions(refDirectory);
		initMaskOfReferencesImage(refDirectory,ref);
		//saveMaskAssociations();

		try {
			resultDoc=DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().newDocument();
			resultRootElement=resultDoc.createElement("Report");

			if(ref.length==0){	
				Logger.getLogger(this.getClass() ).warn("Warning : one of the directory is empty.");
				throw new IllegalArgumentException("one of directory is empty");
			}

			int [] testToRef=new int[test.length];


			String testName;

			progressListener.setProgressValue(0);
			
			for (int i=0;i<test.length; i++){
				testName = test[i].getName().split("-")[0];

				int j = 0;
				boolean refFound = false;
				while(j<ref.length && !refFound){
					String refName = ref[j].getName();
					String refNamePrefix = refName;

					// refere-000.png -->refere
					Pattern pat  = Pattern.compile("([^-]*)-?[0-9]*\\.(png|jpg|jpeg|gif)");
					Matcher mtc = pat.matcher(refName);
					if(mtc.matches())
						refNamePrefix = mtc.group(1);

					if (refNamePrefix.equals(testName)){
						//found the couple

						testToRef[i]=j;
						refFound=true;

						//add an element in Doc report
						Element e = resultDoc.createElement("img");

						Element eRef=resultDoc.createElement("Ref");
						eRef.setAttribute("name", ref[j].getName());
						eRef.setTextContent(ref[j].getAbsolutePath());
						e.appendChild(eRef);

						Element eTest=resultDoc.createElement("Test");
						eTest.setAttribute("name", test[i].getName());
						eTest.setTextContent(test[i].getAbsolutePath());
						e.appendChild(eTest);

						Element ePass=resultDoc.createElement("Pass");
						String pass ="";
						Boolean2D different = new Boolean2D(maskWidth,maskHeight);
						try {
							if (calcDistance( 
									ImageIO.read(ref[j]), 
									ImageIO.read(test[i]) , 
									refImage.get(refName).getMaskSum() , 
									different)!=0){
								pass=FAIL;				
							}
							else{
								pass=AUTOMATIC_PASS;
							}
						}catch (IOException exception) {
							exception.printStackTrace();
						}
						ePass.setTextContent(pass);
						e.appendChild(ePass);

						Element eComm=resultDoc.createElement("Comment");
						eComm.setTextContent(""+test[i].getName());
						e.appendChild(eComm);

						Element eMask=resultDoc.createElement("Mask");
						eMask.setTextContent(""+refImage.get(refName).getMaskSum().getId()+" - "+refImage.get(refName).getMaskSum().getLabel());
						e.appendChild(eMask);

						resultRootElement.appendChild(e);

						couples.add(new ComparaisonCouple(refName, test[i], different ,pass, this));

					}
					j++;
				}
				if (!refFound){
					Logger.getLogger(this.getClass() ).warn("Warning : No Reference image was found for this image <"+testName+" : "+test[i].getAbsolutePath()+">." +
					"This image will not be considered in the comparator. The association is made with names.");
				}

				if (progressListener !=null) {
					int pourcent = (int)(((double)100 / (double)test.length) * (double)(i+1));
					progressListener.setProgressValue(pourcent);
				}
			}

			resultDoc.appendChild(resultRootElement);

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Compare the size of two images from a file
	 * @param File1 pointing to an image
	 * @param File2 pointing to an image
	 * @return <b>True</b> if the two images have the same size.
	 */
	Boolean fileSameSize(File File1,File File2){

		RenderedImage Image1 = null;
		RenderedImage Image2 = null;
		try {
			Image1 = ImageIO.read(File1);
			Image2 = ImageIO.read(File2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return imageSameSize(Image1, Image2);
	}

	/**
	 * Compare the size of two images
	 * @param Image1
	 * @param Image2
	 * @return <b>True</b> if the two images have the same size.
	 */
	Boolean imageSameSize(RenderedImage Image1,RenderedImage Image2){
		return (Image1.getHeight()==Image2.getHeight() && 
				Image1.getWidth()==Image2.getWidth());
	}

	
	/**
	 * read XML
	 * @param refDirectory
	 */
	private void initRefDescriptions(String refDirectory) {

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File reffile = new File(refDirectory+Platform.FILE_SEPARATOR+refXmlFileName); 
			if (reffile.exists() ){

				Document refdoc= builder.parse(reffile);

				//find masks  
				NodeList scNode = refdoc.getElementsByTagName("Screenshot");

				for (int i=0; i<scNode.getLength(); i++) {
					Node n = scNode.item(i);
					NodeList l = n.getChildNodes();
					String name = null;
					String desc = null;
					for (int j =0; j < l.getLength(); j++) {
						Node r= l.item(j);
						if (r.getNodeName().equals("Name")) name = r.getTextContent();
						else if (r.getNodeName().equals("Description")) desc = r.getTextContent();
					}
					if (name!=null && desc!=null) refScDesc.put(name,desc);
				}
		
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * read XML
	 * @param refDirectory
	 */
	private void initMaskOfReferencesImage(String refDirectory, File[] image) {

		// Initialize the DOM with masks references in association with each couple of images
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File maskfile = new File(refDirectory+Platform.FILE_SEPARATOR+maskXmlFileName); 
			File[] ref = getImageFilesFromDirectory(refDirectory);
			refMask = new HashMap<Integer, Mask>();

			RenderedImage imageRef = null;
			imageRef = ImageIO.read(ref[0]);

			//We calculate the size of mask corresponding to the image
			imageWidth = imageRef.getWidth();
			double maskRefWidth = (double)imageWidth/(double)(2*Mask.getCELL_HALF_SIZE());
			if (maskRefWidth - (int)maskRefWidth > 0) 
				maskRefWidth++;

			imageHeight = imageRef.getHeight();
			double maskRefHeight = (double)imageHeight/(double)(2*Mask.getCELL_HALF_SIZE());
			if (maskRefHeight - (int)maskRefHeight > 0) 
				maskRefHeight++;

			if (maskfile.exists() ){

				Document maskdoc= builder.parse(maskfile);

				//Get the size of the mask
				NodeList maskSize = maskdoc.getElementsByTagName("UsedMask");
				maskHeight = (int) Integer.parseInt(maskSize.item(0).getAttributes().getNamedItem("height").getNodeValue());
				maskWidth = (int) Integer.parseInt(maskSize.item(0).getAttributes().getNamedItem("width").getNodeValue());

				//Check if they have the correct size for the masks
				if(((int)maskRefWidth!=maskWidth)||((int)maskRefHeight!=maskHeight)){
					Logger.getLogger(this.getClass() ).debug("The size of the mask is incorrect: x="+
							maskWidth+" - y="+maskHeight+" The image is:"+maskRefWidth+" - y="+maskRefHeight);
					maskWidth = (int)maskRefWidth;
					maskHeight = (int)maskRefHeight;
					return;
				}

				//find masks  
				NodeList masksNode = maskdoc.getElementsByTagName("Mask");

				for (int i=0; i<masksNode.getLength(); i++) {
					Node n = masksNode.item(i);

					int id = (int) Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
					String label= n.getAttributes().getNamedItem("label").getNodeValue();

					NodeList listCell = n.getChildNodes();
					Mask mask = new Mask(label, id, maskWidth, maskHeight);

					for (int j =0; j < listCell.getLength(); j++) {
						Node c= listCell.item(j);
						if(c.hasAttributes()){
							int x=(int) Integer.parseInt(c.getAttributes().getNamedItem("x").getNodeValue());
							int y=(int) Integer.parseInt(c.getAttributes().getNamedItem("y").getNodeValue());
							if((x>maskWidth)||(x<0)||(y>maskHeight)||(y<0))
								Logger.getLogger(this.getClass() ).debug("Cell (x="+x+" - y="+y+" in mask "+label+" out of range");
							else								
								mask.setCell(x, y, true);
						}
					}
					refMask.put(id,mask);
				}

				//associate image reference and mask
				NodeList imgs = maskdoc.getElementsByTagName("img");
				for (int i = 0; i < imgs.getLength(); i++) {

					String imgId = imgs.item(i).getAttributes().getNamedItem("id").getNodeValue();
					String maskValues = imgs.item(i).getAttributes().getNamedItem("mask").getNodeValue();

					if(!maskValues.equals("")){
						String [] maskId = maskValues.split(",+");
						for(String Id : maskId){
							refImage.get(imgId).addMask(Integer.valueOf(Id));
						}
					}
				}
			}else{
				maskWidth = (int)maskRefWidth;
				maskHeight = (int)maskRefHeight;
				return;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method get all image files in the same directory as the reference.
	 * Just for kicks include also the reference image.
	 */
	private File[] getImageFilesFromDirectory(String dirPath)
	{
		File dir = new File(dirPath);
		// List all the image files in that directory.
		File[] files = dir.listFiles(new ImageFileFilter());
		
		for (int i=1; i<files.length; i++) {
			String filename = files[i].getName();
			String timestamp = getImageTimestamp(filename);
			if (timestamp != null) {
				int j=i-1;
				while (j>=0 && timestamp.compareTo(getImageTimestamp(files[j].getName()))<0) j--;
				if (j<i-1) {
					File fileToSort = files[i];
					for (int k=i; k>j+1; k--) {
						files[k] = files[k-1];
					}
					files[j+1] = fileToSort;	
				}
			}
		}
		
		return files;
	}

	private String getImageTimestamp(String imageName) {
		int begin = imageName.lastIndexOf("-");
		int end = imageName.lastIndexOf(".");
		if (begin!= -1 && end != -1) return imageName.substring(begin, end);
		return null;
	}
	/*
	 * This method calculates and returns signature vectors for the input image.
	 */
	private Color[][] calcSignature(RenderedImage i)
	{
		// Get memory for the signature.
		Color[][] sig = new Color[i.getWidth()/(2*Mask.getCELL_HALF_SIZE())+1][i.getHeight()/(2*Mask.getCELL_HALF_SIZE())+1];
		// For each of the 25 signature values average the pixels around it.
		// Note that the coordinate of the central pixel is in proportions.
		//float[] prop = new float[]
		//                       {1f / 10f, 3f / 10f, 5f / 10f, 7f / 10f, 9f / 10f};
		for (int x = 0; x< i.getWidth()/(2*Mask.getCELL_HALF_SIZE())+1; x++)
			for (int y = 0; y < i.getHeight()/(2*Mask.getCELL_HALF_SIZE())+1; y++)
				sig[x][y] = averageAround(i, (x*2+1)*Mask.getCELL_HALF_SIZE(), (y*2+1)*Mask.getCELL_HALF_SIZE());
		return sig;
	}

	/**
	 * This method averages the pixel values around a central point and return the
	 * average as an instance of Color. The point coordinates are proportional to
	 * the image.
	 */
	private Color averageAround(RenderedImage img, double px, double py)
	{
		// Get an iterator for the image.
		RandomIter iterator = RandomIterFactory.create(img, null);
		// Get memory for a pixel and for the accumulator.
		double[] pixel = new double[4];
		double[] accum = new double[3];
		// The size of the sampling area.

		// Sample the pixels.
		for (double x = px - Mask.getCELL_HALF_SIZE(); x < Math.min(px + Mask.getCELL_HALF_SIZE(),img.getWidth()); x++)
		{
			for (double y = py - Mask.getCELL_HALF_SIZE(); y < Math.min(py + Mask.getCELL_HALF_SIZE(),img.getHeight()); y++)
			{
				iterator.getPixel((int) x, (int) y, pixel);
				accum[0] += pixel[0];
				accum[1] += pixel[1];
				accum[2] += pixel[2];
			}
		}
		// Average the accumulated values.
		accum[0] /= Mask.getCELL_HALF_SIZE() * Mask.getCELL_HALF_SIZE() * 4;
		accum[1] /= Mask.getCELL_HALF_SIZE() * Mask.getCELL_HALF_SIZE() * 4;
		accum[2] /= Mask.getCELL_HALF_SIZE() * Mask.getCELL_HALF_SIZE() * 4;
		return new Color((int) accum[0], (int) accum[1], (int) accum[2]);
	}

	/*
	 * This method calculates the distance between the signatures of an image and
	 * the reference one. The signatures for the image passed as the parameter are
	 * calculated inside the method.
	 */
	private double calcDistance(RenderedImage refImage, RenderedImage testImage, Mask mask,  Boolean2D different)
	{
		//Logger.getLogger(this.getClass() ).debug("calcDistance"+i);
		if (imageSameSize(refImage, testImage)){
			// Calculate the signature for that image.
			Color[][] sigRef = calcSignature(refImage);
			Color[][] sigTest = calcSignature(testImage);

			// There are several ways to calculate distances between two vectors,
			// we will calculate the sum of the distances between the RGB values of
			// pixels in the same positions.
			double dist = 0;
			for (int x = 0; x < maskWidth; x++)
				for (int y = 0; y < maskHeight; y++)
				{
					int r1 = sigRef[x][y].getRed();
					int g1 = sigRef[x][y].getGreen();
					int b1 = sigRef[x][y].getBlue();
					int r2 = sigTest[x][y].getRed();
					int g2 = sigTest[x][y].getGreen();
					int b2 = sigTest[x][y].getBlue();
					double tempDist = Math.sqrt((r1 - r2)*(r1 - r2) + 
							(g1 - g2)*(g1 - g2) + (b1 - b2)*(b1 - b2));
					//We compare with 5 since the images are using some lossy compression like jpeg or png
					//We want to estimate as equal images with very small differences
					if (tempDist>10){
						different.set(x, y, true);
					}else{
						tempDist = 0;
					}
					if (! mask.getCell(x, y)){
						dist += tempDist;
					}
				}
			return dist;
		}
		return Double.MAX_VALUE;
	}



	public void saveMaskAssociations() {
		Logger.getLogger(this.getClass() ).debug("saveMaskAssociation");
		//update useddoc
		try {
			useddoc=DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().newDocument();

			Element usedRootElement = useddoc.createElement("UsedMask");
			usedRootElement.setAttribute("height",""+maskHeight);
			usedRootElement.setAttribute("width",""+maskWidth);
			ArrayList<Integer> printedMask = new ArrayList<Integer>();

			//First we had all the images

			for(String image : refImage.keySet()){
				addNewImg(usedRootElement, refImage.get(image));
			}		

			//Then we had all the masks
			for (ComparaisonCouple cp : couples){
				for(Integer Id : cp.getMaskList())
					if(!printedMask.contains(Id)) {
						usedRootElement.appendChild(refMask.get(Id).toXML(useddoc));
						printedMask.add(Id);
					}
			}
			useddoc.appendChild(usedRootElement);

			printToFile(useddoc, refDirectory+Platform.FILE_SEPARATOR+maskXmlFileName);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void saveRefDescriptions() {
		Logger.getLogger(this.getClass() ).debug("saveRefDescription");
		try {
		useddoc=DocumentBuilderFactory.newInstance()
		.newDocumentBuilder().newDocument();

			Element usedRootElement = useddoc.createElement("RefDescription");
	
			Iterator refNames = refScDesc.keySet().iterator();
			while (refNames.hasNext()) {
				String name = (String) refNames.next();
				String desc = refScDesc.get(name);
				Element e = useddoc.createElement("Screenshot");		
				Element eName=useddoc.createElement("Name");
				eName.setTextContent(name);
				e.appendChild(eName);
				Element eDesc=useddoc.createElement("Description");
				eDesc.setTextContent(desc);
				e.appendChild(eDesc);
				usedRootElement.appendChild(e);			
			}
			useddoc.appendChild(usedRootElement);
			printToFile(useddoc, refDirectory+Platform.FILE_SEPARATOR+refXmlFileName);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}


	private Element addNewImg(Element usedRootElement, ImageFileMask imgRef) {
		Element e = useddoc.createElement("img");
		e.setAttribute("id",imgRef.getId());
		String listMask = "";
		for(Integer Id: imgRef.getMaskListId()){
			if(listMask!="")
				listMask+=",";
			listMask+=Id;
		}
		e.setAttribute("mask",listMask);
		usedRootElement.appendChild(e);
		return e;
	}

	/**
	 *print an XML report into the test directory 
	 */
	public void saveReport(){
		try {
			resultDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();


			resultRootElement=resultDoc.createElement("Report");
			for (ComparaisonCouple cp : couples) {
				Element e = resultDoc.createElement("img");		
				Element eRef=resultDoc.createElement("Ref");
				eRef.setAttribute("name", cp.getImgRefId());
				eRef.setTextContent(cp.getImgRef().getImage().getAbsolutePath());
				e.appendChild(eRef);

				Element eTest=resultDoc.createElement("Test");
				eTest.setAttribute("name", cp.getImgTest().getName());
				eTest.setTextContent(cp.getImgTest().getAbsolutePath());
				e.appendChild(eTest);

				Element ePass=resultDoc.createElement("Pass");
				String pass =cp.getPass();
				ePass.setTextContent(pass);
				e.appendChild(ePass);

				Element eRefDesc=resultDoc.createElement("RefDescription");
				String refDesc =refScDesc.get(cp.getImgRefId());
				eRefDesc.setTextContent(refDesc);
				e.appendChild(eRefDesc);

				Element eComm=resultDoc.createElement("Comment");
				eComm.setTextContent(cp.getComment());
				e.appendChild(eComm);

				for(Integer maskId : cp.getMaskList()){
					Element eMask=resultDoc.createElement("Mask");
					eMask.setTextContent(maskId+" - "+refMask.get(maskId).getLabel());
					e.appendChild(eMask);
				}
				resultRootElement.appendChild(e);
			}

			resultDoc.appendChild(resultRootElement);

			printToFile(resultDoc, getTestDirectory()+Platform.FILE_SEPARATOR+xmlReportName);

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * print a PDF report into the test directory
	 */
	public void printPDFReport() {
		try {

			PDFGenerator gen = new PDFGenerator(new FileOutputStream(getTestDirectory()+Platform.FILE_SEPARATOR+pdfReportName), getTestDirectory() , "JG", "FT R&D", "scriptname", true);
			gen.dumpInStream(false, resultDoc, this);
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass() ).warn("Error in PDF printing");
			e.printStackTrace();
		}

	}


	private void printToFile(Document dom, String fileName){
		//Logger.getLogger(this.getClass() ).debug("Model.PrintToFile");
		try
		{
			//print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);


			//to generate a file output use fileoutputstream instead of //System.out
			XMLSerializer serializer = new XMLSerializer(
					new FileOutputStream(new File(fileName)), format);

			serializer.serialize(dom);

		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Recompute differences in all couple of images
	 * @param m - the Mask you want to associate to all (not passed) images for recomputing
	 * @return the number of passed images help to re-computation 
	 */
	public int recomputeAll() {
		int nbFailed=0;
		double nbTotal = couples.size();
		int i=0;
		for (ComparaisonCouple cp : couples){
			try {
				if (recompute(cp)==0){
					cp.setPass( AUTOMATIC_PASS);
				}
				else {
					cp.setPass( FAIL);
					nbFailed++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (progressListener !=null) {
				i++;
				int pourcent = (int)(((double)100 / nbTotal) * (double)i);
				progressListener.setProgressValue(pourcent);
				progressListener.setNbFailed(nbFailed);
				
			}

		}
		return nbFailed;
	}

	public boolean testIsValid() {

		return getNbFail()==0;
	}

	public ArrayList<ComparaisonCouple> getCouplesComparaison() {
		return couples;
	}

}
