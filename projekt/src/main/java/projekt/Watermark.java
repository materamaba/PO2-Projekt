package projekt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

public class Watermark {

    public static void main(String[] args) {
        File sourceFile = new File("C:\\Users\\Mateusz\\Desktop\\zdjecia\\projekt\\DSC_4053.jpg");
        File watermarkFile = new File("C:\\Users\\Mateusz\\Desktop\\zdjecia\\watermark\\bialy.png");
        File outputFile = new File("zdjecie_z_watermarkiem.jpg");

        double size = 12.0;
        float opacity = 1.0f;
        double marginSzerokosc = 1.0;
        double marginWysokosc = -2.0;

        try {
            applyWatermark(sourceFile, watermarkFile, outputFile, size, opacity, marginSzerokosc, marginWysokosc);
            System.out.println("Znak wodny nałożony pomyślnie!");
        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas przetwarzania obrazu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void applyWatermark(File source, File watermark, File output, 
                                      double sizePercent, float opacity, 
                                      double marginSzerokoscPercent, double marginWysokoscPercent) throws IOException {
        
        BufferedImage zdjZrodlowe = ImageIO.read(source);
        int zdjSzerokosc = zdjZrodlowe.getWidth();
        int zdjWysokosc = zdjZrodlowe.getHeight();

        int tagSzerokosc = Math.max(1, przeliczPiksele(zdjSzerokosc, sizePercent));
        int marginSzerokosc = przeliczPiksele(zdjSzerokosc, marginSzerokoscPercent);
        int marginWysokosc = przeliczPiksele(zdjWysokosc, marginWysokoscPercent);

        BufferedImage nowyWatermark = Thumbnails.of(watermark).width(tagSzerokosc).asBufferedImage();

        int x = marginSzerokosc;
        int y = zdjWysokosc - nowyWatermark.getHeight() - marginWysokosc;

        Thumbnails.of(zdjZrodlowe).scale(1.0).watermark(new Coordinate(x, y), nowyWatermark, opacity).toFile(output);
    }


    private static int przeliczPiksele(int wartosc, double procent) {
        return (int) Math.round(wartosc * (procent / 100.0));
    }
}