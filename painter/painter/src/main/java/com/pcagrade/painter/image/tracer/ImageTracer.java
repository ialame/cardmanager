
package com.pcagrade.painter.image.tracer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageTracer {

	private float lThreshold = 10f;
	private float qThreshold = 10f;
	private int pathOmit = 1;
	private int colorCount = 128;
	private int colorQuantizationCycles = 15;

	private float scale = 1f;
	private float roundCoords = 1f;
	private boolean viewBox = false;

	private float blurRadius = 5f;
	private float blurDelta = 50f;

	public ImageTracer withLThreshold(float lThreshold) {
		this.lThreshold = lThreshold;
		return this;
	}

	public ImageTracer withQThreshold(float qThreshold) {
		this.qThreshold = qThreshold;
		return this;
	}

	public ImageTracer withPathOmit(int pathOmit) {
		this.pathOmit = pathOmit;
		return this;
	}

	public ImageTracer withColorCount(int colorCount) {
		this.colorCount = colorCount;
		return this;
	}

	public ImageTracer withScale(float scale) {
		this.scale = scale;
		return this;
	}

	public ImageTracer withRoundCoords(float roundCoords) {
		this.roundCoords = roundCoords;
		return this;
	}

	public ImageTracer withViewBox(boolean viewBox) {
		this.viewBox = viewBox;
		return this;
	}

	public ImageTracer withBlur(float radius, float delta) {
		this.blurRadius = radius;
		this.blurDelta = delta;
		return this;
	}

	public ImageTracer withColorQuantizationCycles(int colorQuantizationCycles) {
		this.colorQuantizationCycles = colorQuantizationCycles;
		return this;
	}

	private byte[][] getPalette(BufferedImage image) {
		int[][] pixels = new int[image.getWidth()][image.getHeight()];
		
		for(int i = 0; i < image.getWidth(); i++)
		    for(int j = 0; j < image.getHeight(); j++){
		        pixels[i][j] = image.getRGB(i, j);
		    }
		int[] palette = Quantize.quantizeImage(pixels, colorCount);
		byte[][] bytePalette = new byte[colorCount][4];
		
		for(int i = 0; i < palette.length; i++) {
			Color c = new Color(palette[i]);
		    bytePalette[i][0] = (byte) c.getRed();
		    bytePalette[i][1] = (byte) c.getGreen();
		    bytePalette[i][2] = (byte) c.getBlue();
		    bytePalette[i][3] = 0;
		}
		return bytePalette;
	}

	public String imageToSVG(String filename) throws IOException {
		return imageToSVG(ImageIO.read(new File(filename)));
	}

	public String imageToSVG(BufferedImage image) {
		return imageDataToSVG(ImageData.load(image), getPalette(image));
	}


	private String imageDataToSVG(ImageData imgd, byte[][] palette) {
		return SVGUtils.getSvgString(imageDataToTraceData(imgd, palette), scale, viewBox, roundCoords);
	}


	public String imageToPDF(String filename) throws IOException {
		return imageToPDF(ImageIO.read(new File(filename)));
	}

	public String imageToPDF(BufferedImage image) throws IOException {
		return imageDataToPDF(ImageData.load(image), getPalette(image));
	}

	private String imageDataToPDF(ImageData imgd, byte[][] palette) throws IOException {
		return  PDFUtils.getPDFString(imageDataToTraceData(imgd, palette), scale, roundCoords);
	}

	// Tracing ImageData, then returning IndexedImage with tracedata in layers
	private IndexedImage imageDataToTraceData(ImageData imgd, byte[][] palette) {
		if(this.blurRadius > 0) {
			imgd = imgd.blur(this.blurRadius, this.blurDelta);
		}
		// 1. Color quantization
		var ii = VectorizingUtils.colorquantization(imgd, palette, this.colorQuantizationCycles);
		// 2. Layer separation and edge detection
		var rawlayers = VectorizingUtils.layering(ii);
		// 3. Batch pathscan
		var bps = VectorizingUtils.batchpathscan(rawlayers, this.pathOmit);
		// 4. Batch interpollation
		var bis = VectorizingUtils.batchinternodes(bps);
		// 5. Batch tracing
		ii.layers = VectorizingUtils.batchtracelayers(bis, this.lThreshold, this.qThreshold);
		return ii;
	}
}
