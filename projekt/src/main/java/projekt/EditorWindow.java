// plik: src/projekt/EditorWindow.java
package projekt;

import java.io.File;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    private Label fileInfoLabel;
    private Slider sizeSlider, opacitySlider, hInsetSlider, vInsetSlider;
    private RadioButton radioWhite, radioBlack;

    public EditorWindow(File inputDirectory, List<File> imageFiles, String outputSubfolder) {
        this.inputDirectory = inputDirectory;
        this.imageFiles = imageFiles;
        this.outputSubfolder = outputSubfolder;

        this.setTitle("Edytor");

        loadWatermarks();

        previewArea = createPreviewArea();
        VBox controlPanel = createControlPanel();

        HBox root = new HBox(previewArea, controlPanel);
        HBox.setHgrow(previewArea, Priority.ALWAYS);

        root.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + ";");

        previewArea.widthProperty().addListener((obs, oldVal, newVal) -> resizeLayers());
        previewArea.heightProperty().addListener((obs, oldVal, newVal) -> resizeLayers());

        Scene scene = new Scene(root, 1100, 650);
        this.setScene(scene);
        this.setOnCloseRequest(e -> System.exit(0));

        loadCurrentImageToPreview();
    }

    public static void generujAlert(Alert.AlertType typ, String tytul, String tekst) {
        Alert alert = new Alert(typ);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(tekst);
        alert.showAndWait();
    }

    private void loadWatermarks() {
        if (fileWatermarkWhite.exists()){
            imageWatermarkWhite = new Image(fileWatermarkWhite.toURI().toString());
        } else {
            generujAlert(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono białego watermarka");
            System.exit(0);
        }
        if (fileWatermarkBlack.exists()) {
            imageWatermarkBlack = new Image(fileWatermarkBlack.toURI().toString());
        } else {
            generujAlert(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono czarnego watermarka");
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

    private VBox createControlPanel() {
        fileInfoLabel = new Label();
        fileInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Kolory.TEKST_AKCENT + ";");

        HBox colorBox = createColorBox();
        GridPane slidersGrid = createSlidersGrid();
        VBox actionBox = createActionBox();

        VBox panel = new VBox(25, fileInfoLabel, colorBox, slidersGrid, actionBox);
        panel.setStyle("-fx-background-color: " + Kolory.TLO_PANELU + "; -fx-padding: 20px;");
        panel.setMinWidth(320);
        panel.setMaxWidth(320);
        return panel;
    }

    private HBox createColorBox() {
        ToggleGroup colorGroup = new ToggleGroup();
        
        radioWhite = new RadioButton("Białe logo");
        radioWhite.setToggleGroup(colorGroup);
        radioWhite.setSelected(true);
        radioWhite.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        radioWhite.setOnAction(e -> {
            watermarkView.setImage(imageWatermarkWhite);
            updatePreviewLayers();
        });

        radioBlack = new RadioButton("Czarne logo");
        radioBlack.setToggleGroup(colorGroup);
        radioBlack.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        radioBlack.setOnAction(e -> {
            watermarkView.setImage(imageWatermarkBlack);
            updatePreviewLayers();
        });

        Label colorLabel = new Label("Kolor:");
        colorLabel.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");

        return new HBox(15, colorLabel, radioWhite, radioBlack);
    }

    private GridPane createSlidersGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(15); 
        grid.setHgap(10);
        grid.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");

        sizeSlider = createSlider(1, 100, 12);
        opacitySlider = createSlider(0, 100, 100);
        hInsetSlider = createSlider(-10, 10, 1);
        vInsetSlider = createSlider(-10, 10, -2);

        addSliderRow(grid, "Rozmiar (%):", sizeSlider, 0);
        addSliderRow(grid, "Krycie (%):", opacitySlider, 1);
        addSliderRow(grid, "Poz. margines (%):", hInsetSlider, 2);
        addSliderRow(grid, "Pion. margines (%):", vInsetSlider, 3);
        
        return grid;
    }

    private VBox createActionBox() {
        Button btnSaveAndNext = new Button("Zapisz i Następne");
        btnSaveAndNext.setStyle("-fx-background-color: #00AF00; -fx-text-fill: " + Kolory.TEKST_AKCENT + "; -fx-font-weight: bold; -fx-padding: 12;");
        btnSaveAndNext.setMaxWidth(Double.MAX_VALUE);
        btnSaveAndNext.setOnAction(e -> saveCurrentAndLoadNext());

        Button btnSkip = new Button("Pomiń zdjęcie");
        btnSkip.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + "; -fx-text-fill: " + Kolory.TEKST_STANDARDOWY + "; -fx-font-weight: bold; -fx-padding: 12;");
        btnSkip.setMaxWidth(Double.MAX_VALUE);
        btnSkip.setOnAction(e -> skipToNext());

        return new VBox(10, btnSaveAndNext, btnSkip);
    }

    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldV, newV) -> updatePreviewLayers());
        return slider;
    }

    private void addSliderRow(GridPane grid, String labelText, Slider slider, int rowIndex) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        grid.add(label, 0, rowIndex);
        grid.add(slider, 1, rowIndex);
    }

    private void loadCurrentImageToPreview() {
        if (currentIndex >= imageFiles.size()) {
            generujAlert(Alert.AlertType.INFORMATION, "Zakończono pracę", "Wszystkie zdjęcia zostały pomyślnie przetworzone!");
            this.close();
            return;
        }

        File currentFile = imageFiles.get(currentIndex);
        fileInfoLabel.setText("Zdjęcie " + (currentIndex + 1) + " z " + imageFiles.size() + "\n" + currentFile.getName());

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

        double targetWatermarkWidth = currentW * (sizeSlider.getValue() / 100.0);
        watermarkView.setFitWidth(targetWatermarkWidth);
        watermarkView.setOpacity(opacitySlider.getValue() / 100.0);

        double watermarkRatio = watermarkView.getImage().getHeight() / watermarkView.getImage().getWidth();
        double currentWatermarkHeight = targetWatermarkWidth * watermarkRatio;

        watermarkView.setLayoutX(currentW * (hInsetSlider.getValue() / 100.0));
        watermarkView.setLayoutY(currentH - currentWatermarkHeight - (currentH * (vInsetSlider.getValue() / 100.0)));
    }

    private void saveCurrentAndLoadNext() {
        if (currentIndex >= imageFiles.size()) return;

        File currentSourceFile = imageFiles.get(currentIndex);
        File outputDir = new File(inputDirectory, outputSubfolder);
        if (!outputDir.exists()) outputDir.mkdirs();

        File outputFile = new File(outputDir, currentSourceFile.getName());
        File currentWatermark = radioWhite.isSelected() ? fileWatermarkWhite : fileWatermarkBlack;

        try {
            float calculatedOpacity = (float) (opacitySlider.getValue() / 100.0);
            Watermark.applyWatermark(
                    currentSourceFile, currentWatermark, outputFile,
                    sizeSlider.getValue(), calculatedOpacity, 
                    hInsetSlider.getValue(), vInsetSlider.getValue()
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