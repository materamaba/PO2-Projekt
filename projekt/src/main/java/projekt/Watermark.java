package projekt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

public class Watermark {

    public static void main(String[] args) {
        // Metoda testowa ze zaktualizowanymi zmiennymi
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

    public static void applyWatermark(File plikZrodlowy, File plikZnaku, File plikWyjsciowy, 
                                      double procentRozmiaru, float krycie, 
                                      double procentMarginesuPoziom, double procentMarginesuPion) throws IOException {
        
        BufferedImage zdjZrodlowe = ImageIO.read(plikZrodlowy);
        int szerokoscZdjecia = zdjZrodlowe.getWidth();
        int wysokoscZdjecia = zdjZrodlowe.getHeight();

        // Przeliczenie procentów z suwaków na konkretne piksele
        int docelowaSzerokoscZnaku = Math.max(1, przeliczPiksele(szerokoscZdjecia, procentRozmiaru));
        int marginesPoziomy = przeliczPiksele(szerokoscZdjecia, procentMarginesuPoziom);
        int marginesPionowy = przeliczPiksele(wysokoscZdjecia, procentMarginesuPion);

        // Skalowanie znaku wodnego za pomocą Thumbnailator
        BufferedImage nowyZnakWodny = Thumbnails.of(plikZnaku).width(docelowaSzerokoscZnaku).asBufferedImage();

        // Obliczenie współrzędnych umiejscowienia logo
        int pozycjaX = marginesPoziomy;
        int pozycjaY = wysokoscZdjecia - nowyZnakWodny.getHeight() - marginesPionowy;

        // Nałożenie znaku na grafikę i zapis do pliku
        Thumbnails.of(zdjZrodlowe)
                .scale(1.0)
                .watermark(new Coordinate(pozycjaX, pozycjaY), nowyZnakWodny, krycie)
                .toFile(plikWyjsciowy);
    }

    private static int przeliczPiksele(int wartoscBazy, double procent) {
        return (int) Math.round(wartoscBazy * (procent / 100.0));
    }
}