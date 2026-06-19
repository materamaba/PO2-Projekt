package projekt;

import java.io.File;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EditorWindow extends Stage {

    private final File inputDirectory;
    private final List<File> imageFiles;
    private final String outputSubfolder;
    private int currentIndex = 0;

    private final File fileWatermarkWhite = new File("watermark\\bialy.png");
    private final File fileWatermarkBlack = new File("watermark\\czarny.png");

    private ImageView baseView, watermarkView;
    private Image baseImage, imageWatermarkWhite, imageWatermarkBlack;
    private Pane imageLayers;
    private StackPane previewArea;

    private Panel sidePanel;

    public EditorWindow(File inputDirectory, List<File> imageFiles, String outputSubfolder) {
        this.inputDirectory = inputDirectory;
        this.imageFiles = imageFiles;
        this.outputSubfolder = outputSubfolder;

        this.setTitle("Edytor");

        loadWatermarks();

        //nasluchuje zmian na suwakach
        sidePanel = new Panel(
            () -> updatePreviewLayers(),
            () -> saveCurrentAndLoadNext(),
            () -> skipToNext(),
            () -> { watermarkView.setImage(imageWatermarkWhite); updatePreviewLayers(); },
            () -> { watermarkView.setImage(imageWatermarkBlack); updatePreviewLayers(); }
        );

        previewArea = createPreviewArea();

        HBox root = new HBox(previewArea, sidePanel);
        HBox.setHgrow(previewArea, Priority.ALWAYS);

        root.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + ";");

        previewArea.widthProperty().addListener((obs, oldVal, newVal) -> resizeLayers());
        previewArea.heightProperty().addListener((obs, oldVal, newVal) -> resizeLayers());

        Scene scene = new Scene(root, 1100, 650);
        this.setScene(scene);
        this.setOnCloseRequest(e -> System.exit(0));

        loadCurrentImageToPreview();
    }

    public static void generateAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void loadWatermarks() {
        if (fileWatermarkWhite.exists()){
            imageWatermarkWhite = new Image(fileWatermarkWhite.toURI().toString());
        } else {
            generateAlert(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono białego watermarka");
            System.exit(0);
        }
        
        if (fileWatermarkBlack.exists()) {
            imageWatermarkBlack = new Image(fileWatermarkBlack.toURI().toString());
        } else {
            generateAlert(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono czarnego watermarka");
            System.exit(0);
        }
        
        baseView = new ImageView();
        watermarkView = new ImageView(imageWatermarkWhite);
        watermarkView.setPreserveRatio(true);
    }

    private StackPane createPreviewArea() {
        imageLayers = new Pane(baseView, watermarkView);
        StackPane container = new StackPane(imageLayers);
        container.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + ";");
        return container;
    }

    private void loadCurrentImageToPreview() {
        if (currentIndex >= imageFiles.size()) {
            generateAlert(Alert.AlertType.INFORMATION, "Zakończono pracę", "Wszystkie zdjęcia zostały pomyślnie przetworzone!");
            this.close();
            return;
        }

        File currentFile = imageFiles.get(currentIndex);
        
        sidePanel.setFileInfo("Zdjęcie " + (currentIndex + 1) + " z " + imageFiles.size() + "\n" + currentFile.getName());

        baseImage = new Image(currentFile.toURI().toString(), 1920, 1080, true, true);
        baseView.setImage(baseImage);

        resizeLayers();
    }

    private void resizeLayers() {
        if (baseImage == null || previewArea.getWidth() == 0) return;

        double scale = Math.min(previewArea.getWidth() / baseImage.getWidth(), previewArea.getHeight() / baseImage.getHeight());
        double actualW = (baseImage.getWidth() * scale) - 20;
        double actualH = (baseImage.getHeight() * scale) - 20;

        imageLayers.setMaxSize(actualW, actualH);
        imageLayers.setMinSize(actualW, actualH);
        baseView.setFitWidth(actualW);
        baseView.setFitHeight(actualH);

        updatePreviewLayers();
    }

    private void updatePreviewLayers() {
        if (baseImage == null || watermarkView.getImage() == null) return;

        double currentW = baseView.getFitWidth();
        double currentH = baseView.getFitHeight();
        if (currentW <= 0 || currentH <= 0) return;

        double targetWatermarkWidth = currentW * (sidePanel.getSizeValue() / 100.0);
        watermarkView.setFitWidth(targetWatermarkWidth);
        watermarkView.setOpacity(sidePanel.getOpacityValue() / 100.0);

        double watermarkRatio = watermarkView.getImage().getHeight() / watermarkView.getImage().getWidth();
        double currentWatermarkHeight = targetWatermarkWidth * watermarkRatio;

        double maxX = currentW - targetWatermarkWidth;
        double maxY = currentH - currentWatermarkHeight;

        double calculatedX = maxX * (sidePanel.getHInsetValue() / 100.0);
        double calculatedY = maxY - (maxY * (sidePanel.getVInsetValue() / 100.0));

        calculatedX = Math.max(0, Math.min(calculatedX, maxX));
        calculatedY = Math.max(0, Math.min(calculatedY, maxY));

        watermarkView.setLayoutX(calculatedX);
        watermarkView.setLayoutY(calculatedY);
    }

    private void saveCurrentAndLoadNext() {
        if (currentIndex >= imageFiles.size()) return;

        File currentSourceFile = imageFiles.get(currentIndex);
        File outputDir = new File(inputDirectory, outputSubfolder);
        if (!outputDir.exists()) outputDir.mkdirs();

        File outputFile = new File(outputDir, currentSourceFile.getName());
        File currentWatermark = sidePanel.isWhiteLogoSelected() ? fileWatermarkWhite : fileWatermarkBlack;

        try {
            float calculatedOpacity = (float) (sidePanel.getOpacityValue() / 100.0);
            Watermark.applyWatermark(
                    currentSourceFile, currentWatermark, outputFile,
                    sidePanel.getSizeValue(), calculatedOpacity, 
                    sidePanel.getHInsetValue(), sidePanel.getVInsetValue()
            );
            System.out.println("Zapisano: " + outputFile.getAbsolutePath());
            skipToNext();
        } catch (Exception ex) {
            System.err.println("Błąd zapisu zdjęcia: " + ex.getMessage());
        }
    }

    private void skipToNext() {
        currentIndex++;
        loadCurrentImageToPreview();
    }
}