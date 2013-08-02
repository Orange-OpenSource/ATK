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
 * File Name   : StatisticTool.java
 *
 * Created     : 30/05/2007
 * Author(s)   : Aurore PENAULT
 */
package com.orange.atk.atkUI.coregui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.SunJPEGEncoderAdapter;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import com.orange.atk.atkUI.corecli.Campaign;
import com.orange.atk.atkUI.corecli.Step;
import com.orange.atk.atkUI.corecli.Step.Verdict;

/**
 * 
 * @author Aurore PENAULT
 * @since JDK5.0
 */
public class StatisticTool {

	/**
	 * The campaign which corresponds to steps in the current check-list.
	 */
	private Campaign campaign;

	private Map<String, Integer> data = new HashMap<String, Integer>();

	/**
	 * The generated chart
	 */
	private JFreeChart jfreechart;

	public StatisticTool(Campaign campaign) {
		this.campaign = campaign;
		PieDataset piedataset = createSampleDataset();
		jfreechart = createChart(piedataset);
	}

	/**
	 * Creates data set with percentage of passed, failed, skipped and not
	 * analysed.
	 * 
	 * @return the data set
	 */
	private PieDataset createSampleDataset() {
		DefaultPieDataset defaultpiedataset = new DefaultPieDataset();
		int passed = 0;
		int failed = 0;
		int skipped = 0;
		int notAnalyzed = 0;
		for (int i = 0; i < campaign.size(); i++) {
			Step cmdLine = (Step) campaign.get(i);
			if (cmdLine.getVerdict() == Verdict.PASSED) {
				passed++;
			} else
				if (cmdLine.getVerdict() == Verdict.FAILED) {
					failed++;
				} else
					if (cmdLine.getVerdict() == Verdict.SKIPPED) {
						skipped++;
					} else
						if (cmdLine.getVerdict() == Verdict.NONE) {
							notAnalyzed++;
						}
		}
		int total = passed + failed + skipped + notAnalyzed;
		defaultpiedataset.setValue("Passed", new Double(passed * 100 / (double) total));
		data.put("Passed", passed);
		defaultpiedataset.setValue("Failed", new Double(failed * 100 / (double) total));
		data.put("Failed", failed);
		defaultpiedataset.setValue("Skipped", new Double(skipped * 100 / (double) total));
		data.put("Skipped", skipped);
		defaultpiedataset.setValue("Not analysed", new Double(notAnalyzed * 100 / (double) total));
		data.put("Not analysed", notAnalyzed);
		data.put("Total", total);
		return defaultpiedataset;
	}

	/**
	 * Creates the chart.
	 * 
	 * @param piedataset
	 *            the data set
	 * @return the created chart
	 */
	private JFreeChart createChart(PieDataset piedataset) {
		JFreeChart jfreechart = ChartFactory.createPieChart3D("", piedataset, true, true, false);
		jfreechart.setBackgroundPaint(Color.lightGray);
		PiePlot pie3dplot = (PiePlot) jfreechart.getPlot();
		pie3dplot.setStartAngle(0);
		pie3dplot.setDirection(Rotation.CLOCKWISE);
		pie3dplot.setForegroundAlpha(0.5F);
		pie3dplot.setNoDataMessage("No data to display");
		pie3dplot.setSectionPaint(0, Color.GREEN);// passed
		pie3dplot.setSectionPaint(1, Color.RED);// failed
		pie3dplot.setSectionPaint(2, Color.ORANGE);// skipped
		pie3dplot.setSectionPaint(3, Color.LIGHT_GRAY);// not analysed
		pie3dplot.setToolTipGenerator(new MyToolTipGenerator());
		pie3dplot.setLabelGenerator(new MySectionLabelGenerator());
		pie3dplot.setLegendLabelGenerator(new MySectionLabelGenerator());
		return jfreechart;
	}

	/**
	 * Customize tool tip's content for each section of the chart.
	 * 
	 * @author apenault
	 * 
	 */
	private class MyToolTipGenerator implements PieToolTipGenerator {
		public String generateToolTip(PieDataset dataset, Comparable key) {
			int percent = dataset.getValue(key).intValue();
			return key.toString() + " = " + percent + " %" + " (" + data.get(key) + "/"
					+ data.get("Total") + ")";
		}
	}

	/**
	 * Customize label's content for each section of the chart
	 * 
	 * @author apenault
	 * 
	 */
	private class MySectionLabelGenerator implements PieSectionLabelGenerator {
		public String generateSectionLabel(PieDataset dataset, Comparable key) {
			int percent = dataset.getValue(key).intValue();
			return key.toString() + " = " + percent + " %" + " (" + data.get(key) + "/"
					+ data.get("Total") + ")";
		}

		public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
			int percent = dataset.getValue(key).intValue();
			return new AttributedString(key.toString() + " = " + percent + " %" + " ("
					+ data.get(key) + "/" + data.get("Total") + ")");
		}
	}

	/**
	 * Creates a jpeg file with the generated chart
	 * 
	 * @param outFilePath
	 *            the path to the output file
	 */
	public void createJPEGFile(File outFile) {
		try {
			FileOutputStream outputStream = new FileOutputStream(outFile);
			BufferedImage bufferedImage = jfreechart.createBufferedImage(550, 270);
			SunJPEGEncoderAdapter encoder = new SunJPEGEncoderAdapter();
			encoder.encode(bufferedImage, outputStream);
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass()).error(e);
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	public JFreeChart getJfreechart() {
		return jfreechart;
	}
}
