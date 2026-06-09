package projekt;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {

    private File inputDirectory;
    private Label folderLabel;

    public static void main(String[] args) {
        launch(args);
    }

    Image image = new Image("file:background.jpg");

    BackgroundImage backgroundImage = new BackgroundImage(
        image,
        BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER,
        new BackgroundSize(
                BackgroundSize.AUTO,
                BackgroundSize.AUTO,
                false,
                false,
                true,
                true
        )
    );


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Watermark Creator - Start");

        // --- Elementy okna startowego ---
        Label titleLabel = new Label("Konfiguracja Sesji");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FAFAFA");

        Button btnInput = new Button("1. Wybierz folder ze zdjęciami");
        btnInput.setMaxWidth(Double.MAX_VALUE);
        btnInput.setOnAction(e -> {
            DirectoryChooser dc = new DirectoryChooser();
            File dir = dc.showDialog(primaryStage);
            if (dir != null) {
                inputDirectory = dir;
                folderLabel.setText("Wybrano: " + dir.getName());
                folderLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        });

        folderLabel = new Label("Brak wybranego folderu");
        folderLabel.setStyle("-fx-text-fill: red;");

        Label subfolderLabel = new Label("2. Nazwa podfolderu zapisu:");
        subfolderLabel.setStyle("-fx-text-fill: #FAFAFA");
        TextField outputFolderField = new TextField("oznaczone");

        Button btnStart = new Button("Uruchom Edytor");
        btnStart.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px;");
        btnStart.setMaxWidth(Double.MAX_VALUE);
        
        btnStart.setOnAction(e -> {
            if (inputDirectory == null) {
                folderLabel.setText("Musisz najpierw wybrać folder!");
                return;
            }

            // Filtrujemy pliki w folderze
            File[] files = inputDirectory.listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
            });

            if (files == null || files.length == 0) {
                folderLabel.setText("Brak zdjęć JPG/PNG w folderze!");
                folderLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Jeśli wszystko gra, uruchamiamy główne okno edytora
            List<File> imageFiles = Arrays.asList(files);
            String subfolderName = outputFolderField.getText().trim();
            if (subfolderName.isEmpty()) subfolderName = "oznaczone";

            // Tworzymy nowe okno (przekazując mu potrzebne dane)
            EditorWindow editor = new EditorWindow(inputDirectory, imageFiles, subfolderName);
            editor.show();

            // Zamykamy małe okno startowe
            primaryStage.close();
        });

        // --- Układ ---
        VBox root = new VBox(15, titleLabel, btnInput, folderLabel, subfolderLabel, outputFolderField, btnStart);
        
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        root.setBackground(new Background(backgroundImage));
        Scene scene = new Scene(root, 350, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}