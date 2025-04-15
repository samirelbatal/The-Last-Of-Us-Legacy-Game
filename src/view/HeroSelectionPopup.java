package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HeroSelectionPopup extends Stage {

    private final Stage owner;
    private boolean isTargetSelected = false;

    public HeroSelectionPopup(Stage owner, String statement) {
        this.owner = owner;
        setTitle("The Last Of Us: Legacy");
        setResizable(false);
        
        Effects effects = new Effects(); 

        // Create a label to display the statement
        Label statementLabel = new Label(statement);
        statementLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Create buttons for selecting target and hero
        Button targetButton = new Button("My Target");
        effects.addHoverEffect(targetButton);
        targetButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: green; -fx-text-fill: white;");
        targetButton.setOnAction(e -> {
            isTargetSelected = true;
            close();
        });

        Button heroButton = new Button("My Hero");
        effects.addHoverEffect(heroButton);
        heroButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: blue; -fx-text-fill: white;");
        heroButton.setOnAction(e -> {
            isTargetSelected = false;
            close();
        });

        // Create an HBox to hold the buttons
        HBox buttonBox = new HBox(19);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(targetButton, heroButton);

        // Create a VBox to hold the statement and buttons
        VBox vbox = new VBox(25);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(statementLabel, buttonBox);

        // Create a StackPane to hold the VBox
        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #333399; -fx-padding: 20px;");

        // Create a scene with the StackPane as its root
        Scene scene = new Scene(root, 500, 150);

        // Set the scene and modality
        setScene(scene);
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        initStyle(StageStyle.UTILITY);
    }

    public boolean showPopup() {
        // Show the popup and wait for it to close
        showAndWait();
        // Return whether target button was selected
        return isTargetSelected;
    }
}
