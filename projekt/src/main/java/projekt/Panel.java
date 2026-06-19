package projekt;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Panel extends VBox {

    private Label fileInfoLabel;
    private Slider sizeSlider, opacitySlider, hInsetSlider, vInsetSlider;
    private RadioButton radioWhite, radioBlack;

    private final Runnable updatePreviewAction;
    private final Runnable saveAndNextAction;
    private final Runnable skipAction;
    private final Runnable whiteLogoAction;
    private final Runnable blackLogoAction;

    public Panel(Runnable updatePreviewAction, Runnable saveAndNextAction, 
                 Runnable skipAction, Runnable whiteLogoAction, Runnable blackLogoAction) {
        
        this.updatePreviewAction = updatePreviewAction;
        this.saveAndNextAction = saveAndNextAction;
        this.skipAction = skipAction;
        this.whiteLogoAction = whiteLogoAction;
        this.blackLogoAction = blackLogoAction;

        buildPanel();
    }

    private void buildPanel() {
        fileInfoLabel = new Label();
        fileInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + Kolory.TEKST_AKCENT + ";");

        HBox colorBox = createColorBox();
        GridPane slidersGrid = createSlidersGrid();
        VBox actionBox = createActionBox();

        this.setSpacing(25);
        this.getChildren().addAll(fileInfoLabel, colorBox, slidersGrid, actionBox);
        this.setStyle("-fx-background-color: " + Kolory.TLO_PANELU + "; -fx-padding: 20px;");
        this.setMinWidth(320);
        this.setMaxWidth(320);
    }

    private HBox createColorBox() {
        ToggleGroup colorGroup = new ToggleGroup();
        
        radioWhite = new RadioButton("Białe logo");
        radioWhite.setToggleGroup(colorGroup);
        radioWhite.setSelected(true);
        radioWhite.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        radioWhite.setOnAction(e -> whiteLogoAction.run());

        radioBlack = new RadioButton("Czarne logo");
        radioBlack.setToggleGroup(colorGroup);
        radioBlack.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        radioBlack.setOnAction(e -> blackLogoAction.run());

        Label colorLabel = new Label("Kolor:");
        colorLabel.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");

        return new HBox(15, colorLabel, radioWhite, radioBlack);
    }

    private GridPane createSlidersGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(15); 
        grid.setHgap(10);
        grid.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");

        sizeSlider = createSlider(1, 80, 12);
        opacitySlider = createSlider(0, 100, 100);
        hInsetSlider = createSlider(0, 100, 2); 
        vInsetSlider = createSlider(0, 100, 2); 

        addSliderRow(grid, "Rozmiar (%):", sizeSlider, 0);
        addSliderRow(grid, "Krycie (%):", opacitySlider, 1);
        addSliderRow(grid, "Poz. pozioma (%):", hInsetSlider, 2);
        addSliderRow(grid, "Poz. pionowa (%):", vInsetSlider, 3);
        
        return grid;
    }

    private VBox createActionBox() {
        Button btnSaveAndNext = new Button("Zapisz i Następne");
        btnSaveAndNext.setStyle("-fx-background-color: #00AF00; -fx-text-fill: " + Kolory.TEKST_AKCENT + "; -fx-font-weight: bold; -fx-padding: 12;");
        btnSaveAndNext.setMaxWidth(Double.MAX_VALUE);
        btnSaveAndNext.setOnAction(e -> saveAndNextAction.run());

        Button btnSkip = new Button("Pomiń zdjęcie");
        btnSkip.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + "; -fx-text-fill: " + Kolory.TEKST_STANDARDOWY + "; -fx-font-weight: bold; -fx-padding: 12;");
        btnSkip.setMaxWidth(Double.MAX_VALUE);
        btnSkip.setOnAction(e -> skipAction.run());

        return new VBox(10, btnSaveAndNext, btnSkip);
    }

    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((obs, oldV, newV) -> updatePreviewAction.run());
        return slider;
    }

    private void addSliderRow(GridPane grid, String labelText, Slider slider, int rowIndex) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        
        grid.add(label, 0, rowIndex);     // Kolumna 0: Etykieta
        grid.add(slider, 1, rowIndex);    // Kolumna 1: Suwak
    }

    public void setFileInfo(String text) {
        fileInfoLabel.setText(text);
    }

    public boolean isWhiteLogoSelected() {
        return radioWhite.isSelected();
    }

    public double getSizeValue() { return sizeSlider.getValue(); }
    public double getOpacityValue() { return opacitySlider.getValue(); }
    public double getHInsetValue() { return hInsetSlider.getValue(); }
    public double getVInsetValue() { return vInsetSlider.getValue(); }
}