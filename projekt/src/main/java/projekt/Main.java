// plik: src/projekt/Main.java
package projekt;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {

    private File inputDirectory;
    private Label folderLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Start");

        Label titleLabel = new Label("Konfiguracja Sesji");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Kolory.TEKST_AKCENT + ";");

        Button btnInput = new Button("1. Wybierz folder ze zdjęciami");
        btnInput.setStyle("-fx-background-color: " + Kolory.TLO_PANELU + "; -fx-text-fill: " + Kolory.TEKST_STANDARDOWY + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 5;");
        btnInput.setMaxWidth(Double.MAX_VALUE);
        btnInput.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            File dir = dc.showDialog(primaryStage);
            if (dir != null) {
                inputDirectory = dir;
                folderLabel.setText("Wybrano: " + dir.getName());
                folderLabel.setStyle("-fx-text-fill: #00AF00; -fx-font-weight: bold;");
            }
        });

        folderLabel = new Label("Brak wybranego folderu");
        folderLabel.setStyle("-fx-text-fill: #FF0000;");

        Label subfolderLabel = new Label("2. Nazwa podfolderu zapisu:");
        subfolderLabel.setStyle("-fx-text-fill: " + Kolory.TEKST_STANDARDOWY + ";");
        
        TextField outputFolderField = new TextField("oznaczone");
        outputFolderField.setStyle(
            "-fx-background-color: " + Kolory.TLO_NAJCIEMNIEJSZE + ";" +
            "-fx-text-fill: " + Kolory.TEKST_AKCENT + ";" +
            "-fx-border-color: " + Kolory.TLO_PANELU + ";" +
            "-fx-border-radius: 3; -fx-background-radius: 3; -fx-padding: 5px;"
        );

        Button btnStart = new Button("Uruchom Edytor");
        btnStart.setStyle("-fx-background-color: " + Kolory.TLO_PANELU + "; -fx-text-fill: " + Kolory.TEKST_STANDARDOWY + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 5;");
        btnStart.setMaxWidth(Double.MAX_VALUE);
        
        btnStart.setOnAction(e -> {
            if (inputDirectory == null) {
                EditorWindow.generujAlert(Alert.AlertType.WARNING, "Brak folderu", "Musisz najpierw wybrać folder ze zdjęciami!");
                return;
            }

            File[] files = inputDirectory.listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
            });

            if (files == null || files.length == 0) {
                EditorWindow.generujAlert(Alert.AlertType.ERROR, "Błąd plików", "Brak zdjęć JPG/PNG w wybranym folderze!");
                return;
            }

            List<File> imageFiles = Arrays.asList(files);
            String subfolderName = outputFolderField.getText().trim();
            if (subfolderName.isEmpty()) subfolderName = "oznaczone";

            EditorWindow editor = new EditorWindow(inputDirectory, imageFiles, subfolderName);
            editor.show();
            primaryStage.close();
        });

        VBox root = new VBox(15, titleLabel, btnInput, folderLabel, subfolderLabel, outputFolderField, btnStart);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        root.setStyle("-fx-background-color: " + Kolory.TLO_GLOWNE + ";");

        Scene scene = new Scene(root, 350, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}