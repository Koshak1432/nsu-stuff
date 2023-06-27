package kosh.util;

import kosh.BandsAsRGB;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Util {
    public static int[] getGeneralToLocalDistribution(boolean[] activeBands, int activeNum) {
        int[] distribution = new int[activeBands.length];
        Arrays.fill(distribution, -1);
        for (int i = 0, bInd = 0; i < activeBands.length; ++i) {
            if (activeBands[i]) {
                distribution[i] = bInd++;
            }
        }
        return distribution;
    }

    public static BandsAsRGB transformDistribution(BandsAsRGB generalColorDistribution, int[] bandsDistribution) {
        System.out.println("general: red: " + generalColorDistribution.red() + ", green: " + generalColorDistribution.green() + ", blue: " + generalColorDistribution.blue());
        int red = bandsDistribution[generalColorDistribution.red()];
        int green = bandsDistribution[generalColorDistribution.green()];
        int blue = bandsDistribution[generalColorDistribution.blue()];
        assert (red >= 0 && green >= 0 && blue >= 0);
        System.out.println("local: red: " + red + ", green: " + green + ", blue: " + blue);
        return new BandsAsRGB(red, green, blue);
    }

    public static int[] getRandomColors(int clustersNumber) {
        Random random = new Random();
        int[] colors = new int[clustersNumber];
        for (int i = 0; i < clustersNumber; ++i) {
            colors[i] = Util.getRGBColor(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        return colors;
    }
    public static int getRGBColor(int r, int g, int b) {
        return ((0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF));
    }

    public static boolean saveImg(File outFile, RenderedImage img) {
        boolean ok = true;
        try {
            ImageIO.write(img, "png", outFile);
        } catch (IOException e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }
}
