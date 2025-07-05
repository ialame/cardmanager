package com.pcagrade.painter.image.tracer;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SVGUtils {

	private static float roundToDec(float val, float places) {
		return (float) (Math.round(val * Math.pow(10, places)) / Math.pow(10, places));
	}


	// Getting SVG url element string from a traced url
	private static void svgPathString(StringBuilder sb, List<Double[]> segments, String colorStr, float scale, float roundCoords) {
		sb.append("<url ").append(colorStr).append("d=\"").append("M ").append(segments.get(0)[1] * scale).append(" ").append(segments.get(0)[2] * scale).append(" ");
		if (roundCoords == -1) {
			for (Double[] segment : segments) {
				if (segment[0] == 1.0) {
					sb.append("L ")
							.append(segment[3] * scale).append(" ")
							.append(segment[4] * scale).append(" ");
				} else {
					sb.append("Q ")
							.append(segment[3] * scale).append(" ")
							.append(segment[4] * scale).append(" ")
							.append(segment[5] * scale).append(" ")
							.append(segment[6] * scale).append(" ");
				}
			}
		} else {
			for (Double[] segment : segments) {
				if (segment[0] == 1.0) {
					sb.append("L ")
							.append(roundToDec((float) (segment[3] * scale), roundCoords)).append(" ")
							.append(roundToDec((float) (segment[4] * scale), roundCoords)).append(" ");
				} else {
					sb.append("Q ")
							.append(roundToDec((float) (segment[3] * scale), roundCoords)).append(" ")
							.append(roundToDec((float) (segment[4] * scale), roundCoords)).append(" ")
							.append(roundToDec((float) (segment[5] * scale), roundCoords)).append(" ")
							.append(roundToDec((float) (segment[6] * scale), roundCoords)).append(" ");
				}
			}
		}

		sb.append("Z\" />");
	}
	
	// Converting tracedata to an SVG string, paths are drawn according to a Z-index
	// the optional lcpr and qcpr are linear and quadratic control point radiuses
	public static String getSvgString(IndexedImage ii, float scale, boolean viewBox, float roundCoords) {
		// SVG start
		var w = (int) (ii.width * scale);
		var h = (int) (ii.height * scale);
		var viewboxorviewport = viewBox ? "viewBox=\"0 0 " + w + " " + h + "\" " : "width=\"" + w + "\" height=\"" + h + "\" ";
		var svgstr = new StringBuilder("<svg " + viewboxorviewport + "version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">");

		// creating Z-index
		var zindex = new TreeMap<Double, Integer[]>();
		double label;
		// Layer loop
		for (int k = 0; k < ii.layers.size(); k++) {

			// Path loop
			for (int pcnt = 0; pcnt < ii.layers.get(k).size(); pcnt++) {

				// Label (Z-index key) is the startpoint of the url, linearized
				label = (ii.layers.get(k).get(pcnt).get(0)[2] * w) + ii.layers.get(k).get(pcnt).get(0)[1];
				// Creating new list if required
				zindex.computeIfAbsent(label, l -> new Integer[2]);
				// Adding layer and url number to list
				zindex.get(label)[0] = k;
				zindex.get(label)[1] = pcnt;
			}// End of url loop

		}// End of layer loop

		// Sorting Z-index is not required, TreeMap is sorted automatically

		// Drawing
		// Z-index loop
		for (Entry<Double, Integer[]> entry : zindex.entrySet()) {
			svgPathString(svgstr,
					ii.layers.get(entry.getValue()[0]).get(entry.getValue()[1]),
					toSvgColorStr(ii.palette[entry.getValue()[0]]),
					scale,
					roundCoords);
		}

		// SVG End
		svgstr.append("</svg>");

		return svgstr.toString();
	}

	private static String toSvgColorStr(byte[] c) {
		return "fill=\"rgb(" + (c[0] + 128) + "," + (c[1] + 128) + "," + (c[2] + 128) + ")\" stroke=\"rgb(" + (c[0] + 128) + "," + (c[1] + 128) + "," + (c[2] + 128) + ")\" stroke-width=\"1\" opacity=\"" + ((c[3] + 128) / 255.0) + "\" ";
	}

}
