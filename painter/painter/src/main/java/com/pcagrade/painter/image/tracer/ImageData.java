package com.pcagrade.painter.image.tracer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public record ImageData(
        int width,
        int height,
        byte[] data

) {
    public static ImageData load(String filename) throws IOException {
        return load(ImageIO.read(new File(filename)));
    }

    public static ImageData load(BufferedImage image) {
        var width = image.getWidth();
        var height = image.getHeight();
        var rawData = image.getRGB(0, 0, width, height, null, 0, width);
        var data = new byte[rawData.length * 4];

        for (int i=0; i<rawData.length; i++) {
            data[(i*4)+3] = byteTrans((byte)(rawData[i] >>> 24));
            data[i*4] = byteTrans((byte)(rawData[i] >>> 16));
            data[(i*4)+1] = byteTrans((byte)(rawData[i] >>> 8));
            data[(i*4)+2] = byteTrans((byte)(rawData[i]));
        }
        return new ImageData(width, height, data);
    }

    // Gaussian kernels for blur
    private static final double[][] GAUSSIAN_KERNELS = {
            {0.27901,0.44198,0.27901},
            {0.135336,0.228569,0.272192,0.228569,0.135336},
            {0.086776,0.136394,0.178908,0.195843,0.178908,0.136394,0.086776},
            {0.063327,0.093095,0.122589,0.144599,0.152781,0.144599,0.122589,0.093095,0.063327},
            {0.049692,0.069304,0.089767,0.107988,0.120651,0.125194,0.120651,0.107988,0.089767,0.069304,0.049692}
    };

    // Selective Gaussian blur for preprocessing
    public ImageData blur(float rad, float del) {
        int i,j,k,d,idx;
        double racc,gacc,bacc,aacc,wacc;
        var data = new byte[this.width*this.height*4];

        // radius and delta limits, this kernel
        int radius = (int)Math.floor(rad); if(radius<1){ return this; } if(radius>5){ radius = 5; }
        int delta = (int)Math.abs(del); if(delta>1024){ delta = 1024; }
        double[] thisgk = GAUSSIAN_KERNELS[radius-1];

        // loop through all pixels, horizontal blur
        for( j=0; j < this.height; j++ ){
            for( i=0; i < this.width; i++ ){

                racc = 0; gacc = 0; bacc = 0; aacc = 0; wacc = 0;
                // gauss kernel loop
                for( k = -radius; k < (radius+1); k++){
                    // add weighted color values
                    if( ((i+k) > 0) && ((i+k) < this.width) ){
                        idx = ((j*this.width)+i+k)*4;
                        racc += this.data[idx  ] * thisgk[k+radius];
                        gacc += this.data[idx+1] * thisgk[k+radius];
                        bacc += this.data[idx+2] * thisgk[k+radius];
                        aacc += this.data[idx+3] * thisgk[k+radius];
                        wacc += thisgk[k+radius];
                    }
                }
                // The new pixel
                idx = ((j*this.width)+i)*4;
                data[idx  ] = (byte) Math.floor(racc / wacc);
                data[idx+1] = (byte) Math.floor(gacc / wacc);
                data[idx+2] = (byte) Math.floor(bacc / wacc);
                data[idx+3] = (byte) Math.floor(aacc / wacc);

            }// End of width loop
        }// End of horizontal blur

        // copying the half blurred imgd
        byte[] hthis = data.clone();

        // loop through all pixels, vertical blur
        for( j=0; j < this.height; j++ ){
            for( i=0; i < this.width; i++ ){

                racc = 0; gacc = 0; bacc = 0; aacc = 0; wacc = 0;
                // gauss kernel loop
                for( k = -radius; k < (radius+1); k++){
                    // add weighted color values
                    if( ((j+k) > 0) && ((j+k) < this.height) ){
                        idx = (((j+k)*this.width)+i)*4;
                        racc += hthis[idx  ] * thisgk[k+radius];
                        gacc += hthis[idx+1] * thisgk[k+radius];
                        bacc += hthis[idx+2] * thisgk[k+radius];
                        aacc += hthis[idx+3] * thisgk[k+radius];
                        wacc += thisgk[k+radius];
                    }
                }
                // The new pixel
                idx = ((j*this.width)+i)*4;
                data[idx  ] = (byte) Math.floor(racc / wacc);
                data[idx+1] = (byte) Math.floor(gacc / wacc);
                data[idx+2] = (byte) Math.floor(bacc / wacc);
                data[idx+3] = (byte) Math.floor(aacc / wacc);

            }// End of width loop
        }// End of vertical blur

        // Selective blur: loop through all pixels
        for( j=0; j < this.height; j++ ){
            for( i=0; i < this.width; i++ ){

                idx = ((j*this.width)+i)*4;
                // d is the difference between the blurred and the original pixel
                d = Math.abs(data[idx  ] - this.data[idx  ]) + Math.abs(data[idx+1] - this.data[idx+1]) +
                        Math.abs(data[idx+2] - this.data[idx+2]) + Math.abs(data[idx+3] - this.data[idx+3]);
                // selective blur: if d>delta, put the original pixel back
                if(d>delta){
                    data[idx  ] = this.data[idx  ];
                    data[idx+1] = this.data[idx+1];
                    data[idx+2] = this.data[idx+2];
                    data[idx+3] = this.data[idx+3];
                }
            }
        }// End of Selective blur

        return new ImageData(this.width,this.height,data);

    }// End of blur()
    
    private static byte byteTrans(byte b) {
        return (byte) (b < 0 ?  (b + 128) : (b - 128));
    }
}
