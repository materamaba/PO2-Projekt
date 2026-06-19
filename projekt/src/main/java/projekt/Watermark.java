package projekt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

public class Watermark {

    public static void main(String[] args) {
        File plikZrodlowy = new File("C:\\Users\\Mateusz\\Desktop\\zdjecia\\projekt\\DSC_4053.jpg");
        File plikZnaku = new File("C:\\Users\\Mateusz\\Desktop\\zdjecia\\watermark\\bialy.png");
        File plikWyjsciowy = new File("zdjecie_z_watermarkiem.jpg");

        double rozmiar = 12.0;
        float krycie = 1.0f;
        double marginesSzerokosc = 1.0;
        double marginesWysokosc = -2.0;

        try {
            applyWatermark(plikZrodlowy, plikZnaku, plikWyjsciowy, rozmiar, krycie, marginesSzerokosc, marginesWysokosc);
            System.out.println("Znak wodny nałożony pomyślnie!");
        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas przetwarzania obrazu: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void applyWatermark(File sourceFile, File watermarkFile, File outputFile, 
                                      double sizePercentage, float opacity, 
                                      double hInsetPercentage, double vInsetPercentage) throws IOException {
        
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        int targetWatermarkWidth = Math.max(1, (int) Math.round(sourceWidth * (sizePercentage / 100.0)));
        
        BufferedImage resizedWatermark = Thumbnails.of(watermarkFile)
                .width(targetWatermarkWidth)
                .asBufferedImage();

        int maxX = sourceWidth - resizedWatermark.getWidth();
        int maxY = sourceHeight - resizedWatermark.getHeight();

        int posX = (int) Math.round(maxX * (hInsetPercentage / 100.0));
        int posY = maxY - (int) Math.round(maxY * (vInsetPercentage / 100.0));

        Thumbnails.of(sourceImage)
                .scale(1.0) // Skala 1.0 gwarantuje brak utraty jakości tła
                .watermark(new Coordinate(posX, posY), resizedWatermark, opacity)
                .toFile(outputFile);
    }
}