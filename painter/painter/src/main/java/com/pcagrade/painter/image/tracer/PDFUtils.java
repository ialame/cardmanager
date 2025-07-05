package com.pcagrade.painter.image.tracer;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.pdf.PDFProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PDFUtils {

    private PDFUtils() {}

    public static String getPDFString(IndexedImage ii, float scale, float roundCoords) throws IOException {
        // Document setup
        int w = (int) (ii.width * scale);
        int h = (int) (ii.height * scale);
        VectorGraphics2D vg2d = new VectorGraphics2D();

        // creating Z-index
        var zindex = new TreeMap<Double,Integer[]>();
        double label;
        // Layer loop
        for(int k=0; k<ii.layers.size(); k++) {

            // Path loop
            for(int pcnt=0; pcnt<ii.layers.get(k).size(); pcnt++){

                // Label (Z-index key) is the startpoint of the url, linearized
                label = (ii.layers.get(k).get(pcnt).get(0)[2] * w) + ii.layers.get(k).get(pcnt).get(0)[1];
                // Creating new list if required
                zindex.computeIfAbsent(label, l -> new Integer[2]);
                // Adding layer and url number to list
                zindex.get(label)[0] = k;
                zindex.get(label)[1] = pcnt;
            }// End of url loop

        }// End of layer loop

        // Drawing
        // Z-index loop
        for(Map.Entry<Double, Integer[]> entry : zindex.entrySet()) {
            byte[] c = ii.palette[entry.getValue()[0]];

            drawPdfPath(vg2d,
                    ii.layers.get(entry.getValue()[0]).get(entry.getValue()[1]),
                    new Color(c[0]+128, c[1]+128, c[2]+128),
                    scale,
                    roundCoords);
        }

        // Write result
        PDFProcessor pdfProcessor = new PDFProcessor(false);
        CommandSequence commands = vg2d.getCommands();
        Document doc = pdfProcessor.getDocument(commands, new PageSize(0.0, 0.0, w, h));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        doc.writeTo(bos);
        return bos.toString();
    }

    public static void drawPdfPath(VectorGraphics2D graphics, List<Double[]> segments, Color color, float scale, float roundCoords) {
        graphics.setColor(color);

        final Path2D path = new Path2D.Double();
        path.moveTo(segments.get(0)[1]*scale, segments.get(0)[2]*scale);

        if( roundCoords == -1 ){
            for (Double[] segment : segments) {
                if (segment[0] == 1.0) {
                    path.lineTo(segment[3] * scale, segment[4] * scale);
                } else {
                    path.quadTo(
                            segment[3] * scale, segment[4] * scale,
                            segment[5] * scale, segment[6] * scale
                    );
                }
            }
        }else{
            for (Double[] segment : segments) {
                if (segment[0] == 1.0) {
                    path.lineTo(
                            roundtodec((float) (segment[3] * scale), roundCoords),
                            roundtodec((float) (segment[4] * scale), roundCoords)
                    );
                } else {
                    path.quadTo(
                            roundtodec((float) (segment[3] * scale), roundCoords),
                            roundtodec((float) (segment[4] * scale), roundCoords),
                            roundtodec((float) (segment[5] * scale), roundCoords),
                            roundtodec((float) (segment[6] * scale), roundCoords));
                }
            }
        }

        graphics.fill(path);
        graphics.draw(path);
    }

    public static float roundtodec(float val, float places){
        return (float)(Math.round(val*Math.pow(10,places))/Math.pow(10,places));
    }
}
