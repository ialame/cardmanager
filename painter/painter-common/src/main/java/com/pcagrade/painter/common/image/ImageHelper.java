package com.pcagrade.painter.common.image;

import jakarta.annotation.Nonnull;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageHelper {

    public static final Color PCA_RED = new Color(0xffc4161c, true);

    private ImageHelper() {}

    @Nonnull
    public static byte[] toByteArray(@Nonnull BufferedImage bufferedImage, @Nonnull String format) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();

        if (!ImageIO.write(bufferedImage, format, byteArrayOutputStream)) {
            throw new IOException("Failed to write image");
        }
        return byteArrayOutputStream.toByteArray();

    }

    @Nonnull
    public static BufferedImage toBufferedImage(@Nonnull byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    @Nonnull
    public static BufferedImage convertToRed(@Nonnull BufferedImage sourceImage) {
        var image = convertTo4byteImage(sourceImage);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                var c = new Color(image.getRGB(x, y), true);
                var a = c.getAlpha();

                if (a > 50) {
                    var mixed = new Color(
                            ceilColor(c.getRed() + PCA_RED.getRed()),
                            ceilColor(c.getGreen() + PCA_RED.getGreen()),
                            ceilColor(c.getBlue() + PCA_RED.getBlue()),
                            a);

                    image.setRGB(x, y, mixed.getRGB());
                }
            }
        }
        return image;
    }

    @Nonnull
    private static BufferedImage convertTo4byteImage(@Nonnull BufferedImage sourceImage) {
        var image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        image.getGraphics().drawImage(sourceImage, 0, 0, null);
        return image;
    }

    private static int ceilColor(int r) {
        return Math.min(r, 255);
    }
}
