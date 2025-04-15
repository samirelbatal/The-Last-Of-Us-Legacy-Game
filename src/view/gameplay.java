package view;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import javafx.util.Duration;

import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import model.characters.Character;
import model.characters.Direction;
import model.characters.Hero;
import model.characters.Zombie;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.CharacterCell;
import model.world.CollectibleCell;

public class gameplay extends Application implements EventHandler<ActionEvent> {
    private Stage window;
    private StackPane rootPane;
    private Scene mainScene;
    private Effects effects = new Effects();
	public static Hero selectedHero;
	
	GridPane mapgrid , pan; 
	Label except , heroInfoLabel;
	String heroName;
	Character selectedTarget;
	Button trapLabel ;
	Button cure,attack,specialAction,endTurn,left,up,down,right;
	ProgressBar healthBar; // Declare healthBar as a field
	Label healthLabel; // Declare healthLabel as a field

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage window) {
        this.window = window;
        rootPane = new StackPane();
        mainScene = new Scene(rootPane, 800, 600);
        window.setScene(mainScene);
        window.setMaximized(true);
        window.setFullScreen(true);
        window.setTitle("The Last Of Us: Legacy");

        Image iconImage = new Image(getClass().getResourceAsStream("/lastofuslogo.jpg"));
        window.getIcons().add(iconImage);

        window.setOnCloseRequest(e -> {
            e.consume();
            showQuitConfirmationPopup();
        });

        showMainMenu();

        window.show();
    }
    
    private void showNotif(String message) {
        Stage popupStage = new Stage();
        popupStage.setTitle("The Last Of Us: Legacy");
        popupStage.setResizable(false);

        // Create a label to display the message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Create a VBox to hold the message
        VBox vbox = new VBox(25);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(messageLabel);

        // Create a StackPane to hold the VBox
        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #333399; -fx-padding: 20px;");

        // Create a scene with the StackPane as its root
        Scene scene = new Scene(root, 470, 150);

        // Set the scene and modality
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(window);
        popupStage.initStyle(StageStyle.UTILITY);

        // Schedule the popup to close after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> popupStage.close());
        delay.play();

        popupStage.showAndWait();
    }
    private void showPopup(String message, boolean autoClose) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Invalid Action!");
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(window);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setResizable(false);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label errorLabel = new Label("\u274C");
        errorLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: red;");

        Effects hoverButton = new Effects();

        Button okButton = new Button("OK");
        hoverButton.addHoverEffect(okButton);
        okButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: red; -fx-text-fill: white;");
        okButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            popupStage.close();
        });

        HBox errorBox = new HBox(10);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.getChildren().addAll(errorLabel, messageLabel);

        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(errorBox, buttonBox);

        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #333399; -fx-padding: 20px;");

        Scene scene = new Scene(root, 500, 150);
        popupStage.setScene(scene);

        if (autoClose) {
            PauseTransition delay = new PauseTransition(Duration.seconds(4));
            delay.setOnFinished(event -> popupStage.close());
            delay.play();
        }

        popupStage.showAndWait();
    }
    
    private void showQuitConfirmationPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("The Last Of Us: Legacy");
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(window);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setResizable(false);

        Label messageLabel = new Label("Are you sure you want to quit?");
        messageLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white;");

        Effects hoverButton = new Effects();

        Button yesButton = new Button("Yes");
        hoverButton.addHoverEffect(yesButton);
        yesButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: green; -fx-text-fill: white;");
        yesButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            popupStage.close();
            window.close();
        });

        Button noButton = new Button("No");
        hoverButton.addHoverEffect(noButton);
        noButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: red; -fx-text-fill: white;");
        noButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            popupStage.close();
        });

        HBox buttonBox = new HBox(50);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(messageLabel, buttonBox);

        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #333399; -fx-padding: 20px;");

        Scene scene = new Scene(root, 500, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    
    private void showMainMenu() {
    	soundPlayer.gameSoundTrack();
        Image backgroundImage = new Image(getClass().getResourceAsStream("/player.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);

        Button play = new Button("Start Game");
        effects.addHoverEffect(play);
        play.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        play.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero1Selection();
            
        });

        Button quit = new Button("Quit Game");
        effects.addHoverEffect(quit);
        quit.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        quit.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showQuitConfirmationPopup();
        });
       

        HBox buttonBox = new HBox(30);
        buttonBox.getChildren().addAll(play, quit);
        buttonBox.setAlignment(Pos.CENTER);

        StackPane mainMenuPane = new StackPane(backgroundImageView, buttonBox);
        StackPane.setAlignment(buttonBox, Pos.CENTER);

        rootPane.getChildren().clear();
        rootPane.getChildren().add(mainMenuPane);
   
    }
    

    private void showHero1Selection() {
        try {
            Game.loadHeroes("Heroes.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);

        Button confirmButton = new Button("Start the Fight!");
        effects.addHoverEffect(confirmButton);
        confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
        confirmButton.setOnAction(e -> {
            soundPlayer.playSound("startBattle.wav");
            selectedHero = Game.availableHeroes.get(2);
            startGame();
           
        });

        Button returnToMenuButton = new Button("Return To Menu");
        effects.addHoverEffect(returnToMenuButton);
        returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        returnToMenuButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showMainMenu();
        });

        Button playerLabel = new Button("Select Your Hero");
        playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        playerLabel.setPrefWidth(300);
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setPadding(new Insets(10));

        Button heroCounter = new Button("1/8");
        heroCounter.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        heroCounter.setPrefWidth(80);
        heroCounter.setAlignment(Pos.CENTER);
        heroCounter.setPadding(new Insets(10));
        
        ImageView hero1 = new ImageView(new Image(getClass().getResourceAsStream("/Tess.jpg")));
        hero1.setFitWidth(220);
        hero1.setPreserveRatio(true);

        Button p2 = new Button("Name: Tess\nType: EXP\nHealth Points: 80 \u2764\nMax. Actions: 6 \u2694\nAttack Damage: 20 💥");
        p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        p2.setPrefWidth(220);
        p2.setWrapText(true);
        p2.setAlignment(Pos.CENTER);
        p2.setPadding(new Insets(10));

        String leftArrow = "\u25C0";
        String rightArrow = "\u25B6";

        Button previousButton = new Button(leftArrow);
        effects.addHoverEffect(previousButton);
        previousButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero7Selection();
        });
        previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;");

        Button nextButton = new Button(rightArrow);
        effects.addHoverEffect(nextButton);
        nextButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero2Selection();
        });
        nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;");

        VBox heroSelection = new VBox(hero1, p2);
        heroSelection.setAlignment(Pos.CENTER);
        heroSelection.setSpacing(20);

        VBox layout1 = new VBox(50, heroSelection, confirmButton);
        layout1.setAlignment(Pos.CENTER);
        layout1.setPadding(new Insets(20));

        VBox layout = new VBox(50, playerLabel, layout1);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        BorderPane heroSelectionPane = new BorderPane();
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        rootPane.getChildren().addAll(backgroundImageView, heroSelectionPane);

        heroSelectionPane.setCenter(layout);
        heroSelectionPane.setBottom(confirmButton);
        BorderPane.setAlignment(confirmButton, Pos.CENTER);
        heroSelectionPane.setTop(returnToMenuButton);
        BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT);
        heroSelectionPane.setLeft(previousButton);
        BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT);
        heroSelectionPane.setRight(nextButton);
        BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT);
        heroSelectionPane.setPadding(new Insets(20, 20, 70, 20));
    }
    

    private void showHero2Selection() {
    	
    	 try {
             Game.loadHeroes("Heroes.csv");
         } catch (IOException e1) {
             e1.printStackTrace();
         }
         
         
         Button returnToMenuButton = new Button("Return To Menu");
         effects.addHoverEffect(returnToMenuButton);
         returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
         returnToMenuButton.setOnAction(e -> {        	
        	 soundPlayer.playSound("mouseClick.wav");
             showMainMenu();
         });
         
         
         ImageView hero2 = new ImageView(new Image(getClass().getResourceAsStream("/David.jpg")));
         hero2.setFitWidth(220); // Adjust image width
         hero2.setPreserveRatio(true); // Preserve aspect ratio
         
         Button p2 = new Button("Name: Riley Abel" + "\n" + "Type: EXP\nHealth Points: 90 \u2764\nMax. Actions: 5 \u2694" + "\n" + "Attack Damage: 25 💥");
         p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         p2.setPrefWidth(230);
         p2.setWrapText(true);
         p2.setAlignment(Pos.CENTER);
         p2.setPadding(new Insets(10));
         
         // Set up background image
         Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
         ImageView backgroundImageView = new ImageView(backgroundImage);
         backgroundImageView.fitWidthProperty().bind(window.widthProperty());
         backgroundImageView.fitHeightProperty().bind(window.heightProperty());
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         
         Button playerLabel = new Button("Select Your Hero");
         playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         playerLabel.setPrefWidth(300);
         playerLabel.setAlignment(Pos.CENTER);
         playerLabel.setPadding(new Insets(10));
            
        
        // Unicode arrow characters for left and right arrows
         String leftArrow = "\u25C0"; // ◄
         String rightArrow = "\u25B6"; // ►

         Button previousButton = new Button(leftArrow);
         effects.addHoverEffect(previousButton);
         previousButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero1Selection();
         });       
         previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

         
         Button nextButton = new Button(rightArrow);
         effects.addHoverEffect(nextButton);
         nextButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero3Selection();
         });       
         nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
        
         
         Button confirmButton = new Button("Start the Fight!");
         effects.addHoverEffect(confirmButton);
         confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
         confirmButton.setOnAction(e -> {
             soundPlayer.playSound("startBattle.wav");
             selectedHero = Game.availableHeroes.get(3);
             startGame();
         });
         
         VBox heroSelection = new VBox(hero2, p2);
         heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
         heroSelection.setSpacing(20); // Add spacing between elements
         
         
         VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
         layout1.setAlignment(Pos.CENTER); // Center align the layout
         layout1.setPadding(new Insets(20)); // Add padding around the layout
         
         VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
         layout.setAlignment(Pos.CENTER); // Center align the layout
         layout.setPadding(new Insets(20)); // Add padding around the layout
         
         
         
         BorderPane root = new BorderPane();
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         rootPane.getChildren().addAll(backgroundImageView, root);
         
         root.setCenter(layout); // Set layout in the center of the BorderPane
         root.setBottom(confirmButton); // Set confirmButton at the bottom
         BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
         root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
         BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
         root.setLeft(previousButton); // Set arrowBox on the left
         BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
         root.setRight(nextButton); // Set arrowBox on the left
         BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
         root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
    	
    }
 
  
    private void showHero3Selection() {
    	try {
            Game.loadHeroes("Heroes.csv");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        ImageView hero3 = new ImageView(new Image(getClass().getResourceAsStream("/Tommy.jpg")));
        hero3.setFitWidth(220); // Adjust image width
        hero3.setPreserveRatio(true); // Preserve aspect ratio
        
        Button p2 = new Button("Name: Tommy Miller\nType: EXP\nHealth Points: 95 \u2764\nMax Actions: 5 \u2694\nAttack Damage: 25 💥");
        p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        p2.setPrefWidth(230);
        p2.setWrapText(true);
        p2.setAlignment(Pos.CENTER);
        p2.setPadding(new Insets(10));
        
        Effects effects = new Effects();
        
        
        // Set up background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        
        
        Button returnToMenuButton = new Button("Return To Menu");
        effects.addHoverEffect(returnToMenuButton);
        returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        returnToMenuButton.setOnAction(e -> {        	
        	soundPlayer.playSound("mouseClick.wav");
        	showMainMenu();       
        });
        
        Button playerLabel = new Button("Select Your Hero");
        playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        playerLabel.setPrefWidth(300);
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setPadding(new Insets(10));
           
       
       // Unicode arrow characters for left and right arrows
        String leftArrow = "\u25C0"; // ◄
        String rightArrow = "\u25B6"; // ►

        Button previousButton = new Button(leftArrow);
        effects.addHoverEffect(previousButton);
        previousButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero2Selection();
        });       
        previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

        
        Button nextButton = new Button(rightArrow);
        effects.addHoverEffect(nextButton);
        nextButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero4Selection();
        });       
        nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
       
        
        Button confirmButton = new Button("Start the Fight!");
        effects.addHoverEffect(confirmButton);
        confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
        confirmButton.setOnAction(e -> {
            soundPlayer.playSound("startBattle.wav");
            selectedHero = Game.availableHeroes.get(4);
            startGame();
        });
        
        VBox heroSelection = new VBox(hero3, p2);
        heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
        heroSelection.setSpacing(20); // Add spacing between elements
        
        
        VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
        layout1.setAlignment(Pos.CENTER); // Center align the layout
        layout1.setPadding(new Insets(20)); // Add padding around the layout
        
        VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
        layout.setAlignment(Pos.CENTER); // Center align the layout
        layout.setPadding(new Insets(20)); // Add padding around the layout
        
        BorderPane root = new BorderPane();
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        rootPane.getChildren().addAll(backgroundImageView, root);
        
        root.setCenter(layout); // Set layout in the center of the BorderPane
        root.setBottom(confirmButton); // Set confirmButton at the bottom
        BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
        root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
        BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
        root.setLeft(previousButton); // Set arrowBox on the left
        BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
        root.setRight(nextButton); // Set arrowBox on the left
        BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
        root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
        
    	
    }
   
   
    private void showHero4Selection() {
    	 try {
             Game.loadHeroes("Heroes.csv");
         } catch (IOException e1) {
             e1.printStackTrace();
         }

         
         ImageView hero4 = new ImageView(new Image(getClass().getResourceAsStream("/Dina.jpg")));
         hero4.setFitWidth(240); // Adjust image width
         hero4.setPreserveRatio(true); // Preserve aspect ratio
         
         
         Button p2 = new Button("Name: David\nType: FIGH\nHealth Points: 150 \u2764\nMax. Actions: 4 \u2694\nAttack Damage: 35 💥");
         p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         p2.setPrefWidth(230);
         p2.setWrapText(true);
         p2.setAlignment(Pos.CENTER);
         p2.setPadding(new Insets(10));
         
         Effects effects = new Effects();
         
         // Set up background image
         Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
         ImageView backgroundImageView = new ImageView(backgroundImage);
         backgroundImageView.fitWidthProperty().bind(window.widthProperty());
         backgroundImageView.fitHeightProperty().bind(window.heightProperty());
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         
         
         Button returnToMenuButton = new Button("Return To Menu");
         effects.addHoverEffect(returnToMenuButton);
         returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
         returnToMenuButton.setOnAction(e -> {        	
         	soundPlayer.playSound("mouseClick.wav");
            showMainMenu();
         });
         
         Button playerLabel = new Button("Select Your Hero");
         playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         playerLabel.setPrefWidth(300);
         playerLabel.setAlignment(Pos.CENTER);
         playerLabel.setPadding(new Insets(10));
            
        
        // Unicode arrow characters for left and right arrows
         String leftArrow = "\u25C0"; // ◄
         String rightArrow = "\u25B6"; // ►

         Button previousButton = new Button(leftArrow);
         effects.addHoverEffect(previousButton);
         previousButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero3Selection();
         });       
         previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

         
         Button nextButton = new Button(rightArrow);
         effects.addHoverEffect(nextButton);
         nextButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero5Selection();
         });       
         nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
        
         
         Button confirmButton = new Button("Start the Fight!");
         effects.addHoverEffect(confirmButton);
         confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
         confirmButton.setOnAction(e -> {
             soundPlayer.playSound("startBattle.wav");
             selectedHero = Game.availableHeroes.get(6);
             startGame();
         });
         
         VBox heroSelection = new VBox(hero4, p2);
         heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
         heroSelection.setSpacing(20); // Add spacing between elements
         
         
         VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
         layout1.setAlignment(Pos.CENTER); // Center align the layout
         layout1.setPadding(new Insets(20)); // Add padding around the layout
         
         VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
         layout.setAlignment(Pos.CENTER); // Center align the layout
         layout.setPadding(new Insets(20)); // Add padding around the layout
         
         
         
         BorderPane root = new BorderPane();
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         rootPane.getChildren().addAll(backgroundImageView, root);
         
         root.setCenter(layout); // Set layout in the center of the BorderPane
         root.setBottom(confirmButton); // Set confirmButton at the bottom
         BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
         root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
         BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
         root.setLeft(previousButton); // Set arrowBox on the left
         BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
         root.setRight(nextButton); // Set arrowBox on the left
         BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
         root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
 	
    }
    
 
    private void showHero5Selection() {
    	 try {
             Game.loadHeroes("Heroes.csv");
         } catch (IOException e1) {
             e1.printStackTrace();
         }
               
         
         ImageView hero5 = new ImageView(new Image(getClass().getResourceAsStream("/Lev.jpg")));
         hero5.setFitWidth(220); // Adjust image width
         hero5.setPreserveRatio(true); // Preserve aspect ratio
         
         
         
         Button p2 = new Button("Name: Henry Burell\nType: MED\n" + "Health Points: 105 \u2764\nMax. Actions: 6 \u2694\nAttack Damage: 15 💥");
         p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         p2.setPrefWidth(230);
         p2.setWrapText(true);
         p2.setAlignment(Pos.CENTER);
         p2.setPadding(new Insets(10));
         
         
         Effects effects = new Effects();
         
         // Set up background image
         Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
         ImageView backgroundImageView = new ImageView(backgroundImage);
         backgroundImageView.fitWidthProperty().bind(window.widthProperty());
         backgroundImageView.fitHeightProperty().bind(window.heightProperty());
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         
         
         Button returnToMenuButton = new Button("Return To Menu");
         effects.addHoverEffect(returnToMenuButton);
         returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
         returnToMenuButton.setOnAction(e -> {        	
         	soundPlayer.playSound("mouseClick.wav");
            showMainMenu();
         });
         
         Button playerLabel = new Button("Select Your Hero");
         playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         playerLabel.setPrefWidth(300);
         playerLabel.setAlignment(Pos.CENTER);
         playerLabel.setPadding(new Insets(10));
            
        
        // Unicode arrow characters for left and right arrows
         String leftArrow = "\u25C0"; // ◄
         String rightArrow = "\u25B6"; // ►

         Button previousButton = new Button(leftArrow);
         effects.addHoverEffect(previousButton);
         previousButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero4Selection();
         });       
         previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

         
         Button nextButton = new Button(rightArrow);
         effects.addHoverEffect(nextButton);
         nextButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero6Selection();
         });       
         nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
        
         
         Button confirmButton = new Button("Start the Fight!");
         effects.addHoverEffect(confirmButton);
         confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
         confirmButton.setOnAction(e -> {
             soundPlayer.playSound("startBattle.wav");
             selectedHero = Game.availableHeroes.get(7);
             startGame();
         });
         
         VBox heroSelection = new VBox(hero5, p2);
         heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
         heroSelection.setSpacing(20); // Add spacing between elements
         
         
         VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
         layout1.setAlignment(Pos.CENTER); // Center align the layout
         layout1.setPadding(new Insets(20)); // Add padding around the layout
         
         VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
         layout.setAlignment(Pos.CENTER); // Center align the layout
         layout.setPadding(new Insets(20)); // Add padding around the layout
         
         
         
         BorderPane root = new BorderPane();
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         rootPane.getChildren().addAll(backgroundImageView, root);
         
         root.setCenter(layout); // Set layout in the center of the BorderPane
         root.setBottom(confirmButton); // Set confirmButton at the bottom
         BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
         root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
         BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
         root.setLeft(previousButton); // Set arrowBox on the left
         BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
         root.setRight(nextButton); // Set arrowBox on the left
         BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
         root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
 	
    }
   
   
    private void showHero6Selection() {
    	try {
            Game.loadHeroes("Heroes.csv");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        
        
        ImageView hero6 = new ImageView(new Image(getClass().getResourceAsStream("/Bill.jpg")));
        hero6.setFitWidth(220); // Adjust image width
        hero6.setPreserveRatio(true); // Preserve aspect ratio
        
       
        
        Button p2 = new Button("Name: Bill\n"+"Type: MED\nHealth Points: 100 \u2764\nMax. Actions: 7 \u2694\nAttack Damage: 10 💥");
        p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        p2.setPrefWidth(230);
        p2.setWrapText(true);
        p2.setAlignment(Pos.CENTER);
        p2.setPadding(new Insets(10));
        
        Effects effects = new Effects();
        
        // Set up background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        
        
        Button returnToMenuButton = new Button("Return To Menu");
        effects.addHoverEffect(returnToMenuButton);
        returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        returnToMenuButton.setOnAction(e -> {        	
        	soundPlayer.playSound("mouseClick.wav");
            showMainMenu();
           
        });
        
        Button playerLabel = new Button("Select Your Hero");
        playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
        playerLabel.setPrefWidth(300);
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setPadding(new Insets(10));
           
       
       // Unicode arrow characters for left and right arrows
        String leftArrow = "\u25C0"; // ◄
        String rightArrow = "\u25B6"; // ►

        Button previousButton = new Button(leftArrow);
        effects.addHoverEffect(previousButton);
        previousButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero5Selection();
        });       
        previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

        
        Button nextButton = new Button(rightArrow);
        effects.addHoverEffect(nextButton);
        nextButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero7Selection();
        });       
        nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
       
        
        Button confirmButton = new Button("Start the Fight!");
        effects.addHoverEffect(confirmButton);
        confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
        confirmButton.setOnAction(e -> {
            soundPlayer.playSound("startBattle.wav");
            selectedHero = Game.availableHeroes.get(5);
            startGame();
        });
        
        VBox heroSelection = new VBox(hero6, p2);
        heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
        heroSelection.setSpacing(20); // Add spacing between elements
        
        
        VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
        layout1.setAlignment(Pos.CENTER); // Center align the layout
        layout1.setPadding(new Insets(20)); // Add padding around the layout
        
        VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
        layout.setAlignment(Pos.CENTER); // Center align the layout
        layout.setPadding(new Insets(20)); // Add padding around the layout
        
        
        
        BorderPane root = new BorderPane();
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        rootPane.getChildren().addAll(backgroundImageView, root);
        
        root.setCenter(layout); // Set layout in the center of the BorderPane
        root.setBottom(confirmButton); // Set confirmButton at the bottom
        BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
        root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
        BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
        root.setLeft(previousButton); // Set arrowBox on the left
        BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
        root.setRight(nextButton); // Set arrowBox on the left
        BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
        root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
        
    }
    
   
    private void showHero7Selection() {
    	 try {
             Game.loadHeroes("Heroes.csv");
         } catch (IOException e1) {
             e1.printStackTrace();
         }
         
      
         
         ImageView hero7 = new ImageView(new Image(getClass().getResourceAsStream("/Ellie.png")));
         hero7.setFitWidth(220); // Adjust image width
         hero7.setPreserveRatio(true); // Preserve aspect ratio
      
         Button p2 = new Button("Name: Ellie Williams\nType: MED\n" + "Health Points: 110 \u2764\nMax. Actions: 6 \u2694\nAttack Damage: 15 💥");
         p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         p2.setPrefWidth(230);
         p2.setWrapText(true);
         p2.setAlignment(Pos.CENTER);
         p2.setPadding(new Insets(10));
         
         Effects effects = new Effects();
         
         // Set up background image
         Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
         ImageView backgroundImageView = new ImageView(backgroundImage);
         backgroundImageView.fitWidthProperty().bind(window.widthProperty());
         backgroundImageView.fitHeightProperty().bind(window.heightProperty());
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         
         
         Button returnToMenuButton = new Button("Return To Menu");
         effects.addHoverEffect(returnToMenuButton);
         returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
         returnToMenuButton.setOnAction(e -> {        	
         	soundPlayer.playSound("mouseClick.wav");
            showMainMenu();
         });
         
         Button playerLabel = new Button("Select Your Hero");
         playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
         playerLabel.setPrefWidth(300);
         playerLabel.setAlignment(Pos.CENTER);
         playerLabel.setPadding(new Insets(10));
            
        
        // Unicode arrow characters for left and right arrows
         String leftArrow = "\u25C0"; // ◄
         String rightArrow = "\u25B6"; // ►

         Button previousButton = new Button(leftArrow);
         effects.addHoverEffect(previousButton);
         previousButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero6Selection();
         });       
         previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

         
         Button nextButton = new Button(rightArrow);
         effects.addHoverEffect(nextButton);
         nextButton.setOnAction(e -> {
             soundPlayer.playSound("mouseClick.wav");
             showHero8Selection();
         });       
         nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
        
         
         Button confirmButton = new Button("Start the Fight!");
         effects.addHoverEffect(confirmButton);
         confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
         confirmButton.setOnAction(e -> {
             soundPlayer.playSound("startBattle.wav");
             selectedHero = Game.availableHeroes.get(1);
             startGame();
         });
         
         VBox heroSelection = new VBox(hero7, p2);
         heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
         heroSelection.setSpacing(20); // Add spacing between elements
         
         
         VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
         layout1.setAlignment(Pos.CENTER); // Center align the layout
         layout1.setPadding(new Insets(20)); // Add padding around the layout
         
         VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
         layout.setAlignment(Pos.CENTER); // Center align the layout
         layout.setPadding(new Insets(20)); // Add padding around the layout
         
         
         
         BorderPane root = new BorderPane();
         StackPane.setAlignment(backgroundImageView, Pos.CENTER);
         rootPane.getChildren().addAll(backgroundImageView, root);
         
         root.setCenter(layout); // Set layout in the center of the BorderPane
         root.setBottom(confirmButton); // Set confirmButton at the bottom
         BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
         root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
         BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
         root.setLeft(previousButton); // Set arrowBox on the left
         BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
         root.setRight(nextButton); // Set arrowBox on the left
         BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
         root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
    }
    
   
    private void showHero8Selection() {
    	 try {
	            Game.loadHeroes("Heroes.csv");
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
	        
	        
	        ImageView hero8 = new ImageView(new Image(getClass().getResourceAsStream("/Abby.jpg")));
	        hero8.setFitWidth(210); // Adjust image width
	        hero8.setPreserveRatio(true); // Preserve aspect ratio
	      
	        Button p2 = new Button("Name: Joel Miller\nType: FIGH\nHealth Points: 140 \u2764\nMax. Actions: 5 \u2694\nAttack Damage: 30 💥");
	        p2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
	        p2.setPrefWidth(230);
	        p2.setWrapText(true);
	        p2.setAlignment(Pos.CENTER);
	        p2.setPadding(new Insets(10));
	        
		    Effects effects = new Effects();
	        
		    // Set up background image
	        Image backgroundImage = new Image(getClass().getResourceAsStream("/menuImage2.jpg"));
	        ImageView backgroundImageView = new ImageView(backgroundImage);
	        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
	        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
	        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
	        
	        
	        Button returnToMenuButton = new Button("Return To Menu");
	        effects.addHoverEffect(returnToMenuButton);
	        returnToMenuButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
	        returnToMenuButton.setOnAction(e -> {        	
	        	soundPlayer.playSound("mouseClick.wav");
	            showMainMenu();
	        });
	        
	        Button playerLabel = new Button("Select Your Hero");
	        playerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #007F73; -fx-background-radius: 10;");
	        playerLabel.setPrefWidth(300);
	        playerLabel.setAlignment(Pos.CENTER);
	        playerLabel.setPadding(new Insets(10));
	           
	       
	       // Unicode arrow characters for left and right arrows
	        String leftArrow = "\u25C0"; // ◄
	        String rightArrow = "\u25B6"; // ►

	        Button previousButton = new Button(leftArrow);
	        effects.addHoverEffect(previousButton);
	        previousButton.setOnAction(e -> {
	            soundPlayer.playSound("mouseClick.wav");
	            showHero7Selection();
	        });       
	        previousButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent

	        
	        Button nextButton = new Button(rightArrow);
	        effects.addHoverEffect(nextButton);
	        nextButton.setOnAction(e -> {
	            soundPlayer.playSound("mouseClick.wav");
	            showHero1Selection();	            
	        });       
	        nextButton.setStyle("-fx-font-size: 100px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: #00FF00;"); // Set background color to transparent
	       
	        
	        Button confirmButton = new Button("Start the Fight!");
	        effects.addHoverEffect(confirmButton);
	        confirmButton.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: red;");
	        confirmButton.setOnAction(e -> {
	            soundPlayer.playSound("startBattle.wav");
	            selectedHero = Game.availableHeroes.get(0);
	            startGame();	           
	        });
	        
	        VBox heroSelection = new VBox(hero8, p2);
	        heroSelection.setAlignment(Pos.CENTER); // Center align the VBox
	        heroSelection.setSpacing(20); // Add spacing between elements
	        
	        
	        VBox layout1 = new VBox(50, heroSelection, confirmButton); // Vertical layout to stack elements
	        layout1.setAlignment(Pos.CENTER); // Center align the layout
	        layout1.setPadding(new Insets(20)); // Add padding around the layout
	        
	        VBox layout = new VBox(50, playerLabel, layout1); // Vertical layout to stack elements
	        layout.setAlignment(Pos.CENTER); // Center align the layout
	        layout.setPadding(new Insets(20)); // Add padding around the layout
	        
	        
	        
	        BorderPane root = new BorderPane();
	        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
	        rootPane.getChildren().addAll(backgroundImageView, root);
	        
	        root.setCenter(layout); // Set layout in the center of the BorderPane
	        root.setBottom(confirmButton); // Set confirmButton at the bottom
	        BorderPane.setAlignment(confirmButton, Pos.CENTER); // Center confirmButton at the bottom
	        root.setTop(returnToMenuButton); // Set returnToMenuButton at the top
	        BorderPane.setAlignment(returnToMenuButton, Pos.TOP_LEFT); // Align returnToMenuButton to the top left corner
	        root.setLeft(previousButton); // Set arrowBox on the left
	        BorderPane.setAlignment(previousButton, Pos.CENTER_LEFT); // Align arrowBox to the left center
	        root.setRight(nextButton); // Set arrowBox on the left
	        BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT); // Align arrowBox to the left center
	        root.setPadding(new Insets(20, 20, 70, 20)); // Add padding to the left and right edges
    	
    }
    
   

    private void startGame() {
        // Initialize the pan GridPane variable
        mapgrid = new GridPane();
        
        Effects effects = new Effects();   
        
        heroInfoLabel = new Label();
      	
	    Game.startGame(selectedHero);  
    
    	mapgrid.setPadding(new Insets(5));
    	mapgrid.setVgap(2);
    	mapgrid.setHgap(2);    	
    	for(int i=0; i<15; i++){
    		for(int j=0; j<15; j++){
    			Button button= new Button();
    			button.setPrefSize(75, 36);
    			mapgrid.add(button , i, j);
    		}    		
    	}
    	
    	// Add event handlers to all buttons in the grid
        for (Node node : mapgrid.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(this); // Set the event handler to handle button clicks
            }
        }
    	
        healthBar = new ProgressBar();
        healthBar.setPrefWidth(900);
        healthBar.setMaxWidth(150); // You might want to adjust this according to your layout
        healthBar.setProgress(1); // Assuming 100% progress initially
  
        
        // Create a label to display the percentage and heart symbol
        healthLabel = new Label("\u2764 " + (int)(healthBar.getProgress() * 100) + "%"); // Unicode for heart symbol followed by percentage
        healthLabel.setStyle("-fx-text-fill: red; -fx-font-size: 28px;"); // Set color of the heart symbol

        // Set the CSS style to change color and display percentage
        String cssStyle = "-fx-accent: green;";
        healthBar.setStyle(cssStyle);
    	
        updateMap(mapgrid);
        
        if(selectedHero.getMaxHp() == 140 || selectedHero.getMaxHp() == 150){
        	heroName = "FIGH";       	  
        } else if(selectedHero.getMaxHp() == 100 || selectedHero.getMaxHp() == 105 || selectedHero.getMaxHp() == 110) {
        	heroName = "MED";
        } else if (selectedHero.getMaxHp() == 80 || selectedHero.getMaxHp() == 90 || selectedHero.getMaxHp() == 95){
        	heroName = "EXP";
        }
    	
        updateInfo(heroInfoLabel);
    	 
  
   	    cure = new Button("Cure");   	          
        // Call the addHoverEffect method on the button
        effects.addHoverEffect(cure);
        cure.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
   	    cure.setOnAction(e -> {
   	    	try {   	    		
   	    		selectedHero.cure();
   	    		updateMap(mapgrid);
   	    		updateInfo(heroInfoLabel);					
				
				if(Game.checkWin()) {
					victory();
				}
				else if(Game.checkGameOver()) {
					defeat();
				}else {					
					soundPlayer.playSound("zombieCured.wav");	
					int noOfAvailableHeroes = Game.heroes.size() - 1;
					int noOfRemainingHeroes = 5 - noOfAvailableHeroes;
					if(noOfAvailableHeroes != 5) {
						if(noOfAvailableHeroes == 1) {							
							showNotif("You have successfully cured " +noOfAvailableHeroes + " Zombie.");
					     
						}else if(noOfRemainingHeroes == 1) {
	       		            showNotif("You have successfully cured " +noOfAvailableHeroes + " Zombies.");
													
						}else {								
	       		            showNotif("You have successfully cured " +noOfAvailableHeroes + " Zombies.");
						}
					}
				}
				
			} catch (NoAvailableResourcesException e1) {
				soundPlayer.playSound("invalidAction.wav");
				showPopup(e1.getMessage(), false);
			} catch (NotEnoughActionsException e1) {		
				soundPlayer.playSound("invalidAction.wav");
				showPopup(e1.getMessage(), false);		
			} catch (InvalidTargetException e1) {
				soundPlayer.playSound("invalidAction.wav");			
				showPopup(e1.getMessage(), false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}   	    	
   	    	
   	    });
   	    
        specialAction = new Button("Special Action");            
        effects.addHoverEffect(specialAction);
        specialAction.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
   	    specialAction.setOnAction(e->{
   	    	try {  
   	    		if(selectedHero.getMaxHp()>=100 && selectedHero.getMaxHp()<=110 && selectedHero.getTarget()== null  ) {  
   	    			soundPlayer.playSound("invalidAction.wav");
   	   	            showPopup("You must select a target first to use Special Action", false);
   	    				
   	    		}
   	    		else if(selectedHero.getMaxHp()>=100 && selectedHero.getMaxHp()<=110 && selectedHero.getTarget() instanceof Zombie) {
   	    			soundPlayer.playSound("invalidAction.wav");
	   	    		 showPopup("Medic cannot perform special actions on Zombies.\n  It can only restore the health of Heroes. ", false);
   	    		}
   	    		else if(selectedHero.getMaxHp()>=100 && selectedHero.getMaxHp()<=110 && selectedHero.getTarget().getCurrentHp() == selectedHero.getTarget().getMaxHp() && selectedHero.getSupplyInventory().size() != 0) {
   	    			soundPlayer.playSound("invalidAction.wav");
	   	    		showPopup("Can't use Special Action as target has full health" , false);
   	    		} else {
   	    			selectedHero.useSpecial();
   	    			updateMap(mapgrid);
   	    			updateInfo(heroInfoLabel);
   					if (selectedHero.getMaxHp() == 80 || selectedHero.getMaxHp() == 90 || selectedHero.getMaxHp() == 95){
   						soundPlayer.playSound("EXPSpecialAction.wav");
   					} else if (selectedHero.getMaxHp() == 100 || selectedHero.getMaxHp() == 105 || selectedHero.getMaxHp() == 110) {
   						soundPlayer.playSound("rechargingHealth.wav");
   					} else {
   						soundPlayer.playSound("specialActionActivated.wav");
   					}
   					
   		            showNotif("Special Action has been activated");
   			          
   	                if(Game.checkWin()) {
   	                	victory();
   					} else if(Game.checkGameOver()) {
   						defeat();
   					}
   	    			
   	    		}
   	    		
			} catch (NoAvailableResourcesException e1) {
				soundPlayer.playSound("invalidAction.wav");
			    showPopup(e1.getMessage(), false);
			} catch (InvalidTargetException e1) {
				soundPlayer.playSound("invalidAction.wav");			
			    showPopup(e1.getMessage(), false);
			} catch (Exception e1) {				
				e1.printStackTrace();
			}
   	    });
   	    
        endTurn = new Button("End Turn");     
        effects.addHoverEffect(endTurn);
        endTurn.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
        endTurn.setOnAction(e ->{
   	    	try {
				Game.endTurn();				
				updateMap(mapgrid);
				updateInfo(heroInfoLabel);
				soundPlayer.playSound("endTurn.wav");				
                if(Game.checkWin()) {                	
                	victory();
		        } else if(Game.checkGameOver()) {		        	
		        	defeat();
		        } else if(selectedHero.getCurrentHp() == 0) {	
		        	soundPlayer.playSound("invalidAction.wav");
		        	showPopup(" Your hero has fallen in battle.\n Please select another hero to continue the game.", false);
		        }
			} catch (NotEnoughActionsException e1) {
				soundPlayer.playSound("invalidAction.wav");
				showPopup(e1.getMessage(), false);
			} catch (InvalidTargetException e1) {
				soundPlayer.playSound("invalidAction.wav");
			    showPopup(e1.getMessage(), false);
			} catch (Exception e1) {				
				e1.printStackTrace();
			}
   	    }) ;
        
        attack = new Button("Attack");
        effects.addHoverEffect(attack);        
        attack.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: blue;");
   	    attack.setOnAction(e ->{
   	     try {
   	    	 
   	    	 boolean confirm;
   	    	 String zombieName = null;
   	    	 if(selectedHero.getTarget() == null) {
   	    		 confirm = false;
   	    	 }else {
   	    		 confirm = true;
   	    		 zombieName = selectedHero.getTarget().getName();
   	    	 }
   	    	 
   	    	selectedHero.attack();
   	    	updateMap(mapgrid);
   	    	updateInfo(heroInfoLabel);
			soundPlayer.playSound("attackSound.wav");
		
			if(confirm && selectedHero.getTarget()== null) {	
				soundPlayer.playSound("zombieDeath.wav");
				showNotif(zombieName  + " has died.");
								
			}
			if(Game.checkWin()) {
				victory();
			}
			else if(Game.checkGameOver()) {
				defeat();
			}			
			if(selectedHero == null) {
            	showPopup(" Your hero has fallen in battle.\n Please select another hero to continue the game.", false);
            }
		} catch (NotEnoughActionsException e1) {
			soundPlayer.playSound("invalidAction.wav");			
			showPopup(e1.getMessage(), false);
		} catch (InvalidTargetException e1) {
			soundPlayer.playSound("invalidAction.wav");	
			showPopup(e1.getMessage(), false);
		} catch (Exception e1) {		
			e1.printStackTrace();
		}
   	    });


      Label health = new Label("Character's Health:");
      health.setStyle("-fx-font-size: 15px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-text-fill: black;");

      
      HBox bar = new HBox(8);
      bar.setAlignment(Pos.CENTER);     
      
      
      bar.getChildren().addAll(healthLabel , healthBar);
      
      Label act1 = new Label("Character Actions:");
      act1.setStyle("-fx-font-size: 22px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-text-fill: white;");
 
      HBox buttonsContainer2 = new HBox(18); // 10 is the spacing between buttons, adjust as needed
      buttonsContainer2.setAlignment(Pos.CENTER);
      buttonsContainer2.getChildren().addAll( cure, attack);
      
      HBox buttonsContainer3 = new HBox(12); // 10 is the spacing between buttons, adjust as needed
      buttonsContainer3.setAlignment(Pos.CENTER);
      buttonsContainer3.getChildren().addAll(endTurn, specialAction);
      
      VBox buttonsContainer123 = new VBox(12); // 10 is the spacing between buttons, adjust as needed
      buttonsContainer123.setAlignment(Pos.CENTER);
      buttonsContainer123.getChildren().addAll( act1 ,buttonsContainer2, buttonsContainer3);

      HBox buttonsContainer4 = new HBox(40); // 10 is the spacing between buttons, adjust as needed
      buttonsContainer4.getChildren().addAll( heroInfoLabel , bar, buttonsContainer123 );
      
      buttonsContainer4.setAlignment(Pos.CENTER);
      
      
      HBox hbox = new HBox();
      hbox.getChildren().add(mapgrid);
      hbox.setAlignment(Pos.CENTER);

      // Apply Insets to hbox for spacing from left, top, and right
      hbox.setPadding(new Insets(0, 100, 20, 100)); // Adjust the insets as needed

      
      // Use VBox for layout
      VBox mainContainer = new VBox(10);
      mainContainer.getChildren().addAll(hbox, buttonsContainer4);
      mainContainer.setAlignment(Pos.CENTER); // Center the VBox vertically

      StackPane.setAlignment(mainContainer, Pos.CENTER);
      rootPane.getChildren().clear();
      rootPane.getChildren().add(mainContainer);

      // Set background color for pane
      rootPane.setStyle("-fx-background-color: #808B96 ");
      
      // Add event handler for keyboard arrow key presses
      rootPane.setOnKeyPressed(this::handleKeyPress);
        
    }
    
	public void handleKeyPress(KeyEvent event) {
		
        KeyCode keyCode = event.getCode();      
        int vaccArrSize = selectedHero.getVaccineInventory().size();
        int currHealth = selectedHero.getCurrentHp();
        int supplyArrSize = selectedHero.getSupplyInventory().size();
        
        // Check which arrow key is pressed and perform corresponding action
        switch (keyCode) {
            case KeyCode.W:
                try {         
                	selectedHero.move(Direction.DOWN);
                    soundPlayer.playSound("heroMovement.wav");
                    updateMap(mapgrid);
                    updateInfo(heroInfoLabel);
                    if (currHealth != selectedHero.getCurrentHp()) {
                    	soundPlayer.playSound("invalidAction.wav");  
       		            showNotif("You have just entered a trap!");
                    }
                    if(Game.checkWin()) {
                    	victory();
        			} else if(Game.checkGameOver()) {
        				defeat();
        			} else  if(selectedHero.getCurrentHp() == 0) {		        	  
  		        	
        				showPopup("Your hero has fallen in battle.\nPlease select another hero to continue the game.", false);
                    }
                    
                    if(vaccArrSize != selectedHero.getVaccineInventory().size()) {
        				soundPlayer.playSound("vaccineCollected.wav");
        			}
                    
                    if(supplyArrSize != selectedHero.getSupplyInventory().size()) {
        				soundPlayer.playSound("supplyCollected.wav");
        			}
                    
                } catch (MovementException | NotEnoughActionsException e) {
                	soundPlayer.playSound("invalidAction.wav");   
                	showPopup( e.getLocalizedMessage() , false);  
         			
         		}
                
                break;
                
            case KeyCode.S:            	
            	try {  		 
            		selectedHero.move(Direction.UP);
          			soundPlayer.playSound("heroMovement.wav");
          			updateMap(mapgrid);
          			updateInfo(heroInfoLabel);
        			if(currHealth!= selectedHero.getCurrentHp()) {
        				soundPlayer.playSound("invalidAction.wav");        			
       		            showNotif("You have just entered a trap!");
        			}
        			
        			if(Game.checkWin()) {
        				victory();
        			}
        			else if(Game.checkGameOver()) {
        				defeat();
        			} else  if(selectedHero.getCurrentHp() == 0) {		        	  
    		        	
    		        	  showPopup("Your hero has fallen in battle.\nPlease select another hero to continue the game.", false);	
                    }        			
        			
        			if(vaccArrSize != selectedHero.getVaccineInventory().size()) {
        				soundPlayer.playSound("vaccineCollected.wav");
        			}
        			
        			if(supplyArrSize != selectedHero.getSupplyInventory().size()) {
        				soundPlayer.playSound("supplyCollected.wav");
        			}
        			
            	}catch (MovementException | NotEnoughActionsException e) {
            		soundPlayer.playSound("invalidAction.wav");
            		showPopup( e.getLocalizedMessage(), false);  
     			
         		}
             
                // Handle DOWN arrow key press
                break;
            case KeyCode.A:
            	try {
            		selectedHero.move(Direction.LEFT);
        			soundPlayer.playSound("heroMovement.wav");
        			updateMap(mapgrid);
        			updateInfo(heroInfoLabel);
        			if(currHealth!= selectedHero.getCurrentHp()) {  
        				soundPlayer.playSound("invalidAction.wav");        				
       		            showNotif("You have just entered a trap!");
        			}
        			
        			if(Game.checkWin()) {
        				victory();
        			}
        			else if(Game.checkGameOver()) {
        				defeat();
        			} else  if(selectedHero.getCurrentHp() == 0) {	        				        				
    		        	showPopup("Your hero has fallen in battle.\nPlease select another hero to continue the game.", false);                  
                    }
        			
        			if(vaccArrSize != selectedHero.getVaccineInventory().size()) {
        				soundPlayer.playSound("vaccineCollected.wav");
        			}
        			
        			if(supplyArrSize != selectedHero.getSupplyInventory().size()) {
        				soundPlayer.playSound("supplyCollected.wav");
        			}
        		        		
        		} catch (MovementException | NotEnoughActionsException e) {
        			soundPlayer.playSound("invalidAction.wav");
        			showPopup( e.getLocalizedMessage() , false);  
         			
         		}
            	
                // Handle LEFT arrow key press
                break;
            case KeyCode.D:
            	 try {            		  
            		 selectedHero.move(Direction.RIGHT);
           			soundPlayer.playSound("heroMovement.wav");
           			updateMap(mapgrid);
           			updateInfo(heroInfoLabel);
           			if(currHealth != selectedHero.getCurrentHp()) {
           				soundPlayer.playSound("invalidAction.wav");           				
       		            showNotif("You have just entered a trap!");
           			}
           		
           			if(Game.checkWin()) {
           				victory();
          			}
          			else if(Game.checkGameOver()) {
          				defeat();
          			} else  if(selectedHero.getCurrentHp() == 0) {	            				
      		            showPopup("Your hero has fallen in battle.\nPlease select another hero to continue the game.", false);
                        
                      }
           			
           			if(vaccArrSize != selectedHero.getVaccineInventory().size()) {
          				soundPlayer.playSound("vaccineCollected.wav");
          			}
           			
           			if(supplyArrSize != selectedHero.getSupplyInventory().size()) {
           				soundPlayer.playSound("supplyCollected.wav");
          			}          		           		        
         			
         		} catch (MovementException | NotEnoughActionsException e) {
         			soundPlayer.playSound("invalidAction.wav");
         			showPopup(e.getLocalizedMessage() , false);
         			
         		}
            	 
                break;
		default:
			break;
  
        }
   
	}
	

	private boolean showHeroSelectionPopup(String message) {
        HeroSelectionPopup popup = new HeroSelectionPopup(window, message);
        return popup.showPopup();
    }
	
	class HeroSelectionPopup extends Stage {
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
	
	
	@Override
	public void handle(ActionEvent event) {					

	    Button clickedButton = (Button) event.getSource();
	    int rowIndex = GridPane.getRowIndex(clickedButton);
	    int colIndex = GridPane.getColumnIndex(clickedButton);
	   
	    if (((CharacterCell) Game.map[colIndex][rowIndex]).isVisible()) {
	        if (Game.map[colIndex][rowIndex] instanceof CharacterCell) {
	            if (((CharacterCell) Game.map[colIndex][rowIndex]).getCharacter() instanceof Zombie ) {
	            	if(nearHero(selectedHero , colIndex , rowIndex )) {
	     
	            		for (int z = 0; z < Game.zombies.size(); z++) {
	            			if (Game.zombies.get(z).getLocation().x == colIndex && Game.zombies.get(z).getLocation().y == rowIndex) {
	            				Zombie selectedZombie = Game.zombies.get(z);
	            					            				
	            				if(selectedHero.getTarget() == selectedZombie ) {
	            					selectedHero.setTarget(null);	            					
	            				}else {
	            					selectedHero.setTarget(selectedZombie); 
	            				}
	            				  
	            				soundPlayer.playSound("mouseClick.wav");
	            				break;
	            			}
	            		}
	                
	            		}else {
	            			soundPlayer.playSound("invalidAction.wav");
	            			showPopup("You cannot select a target is not near you" , false);
	            		}
	        
	              
	            }else if(((CharacterCell) Game.map[colIndex][rowIndex]).getCharacter() instanceof Hero) {            	
	            	
	            	for (int i = 0; i < Game.heroes.size(); i++) {
	            		if(Game.heroes.get(i).getLocation().x == colIndex && Game.heroes.get(i).getLocation().y == rowIndex) {	           			
	
	            			if(selectedHero.getMaxHp() >= 100 &&  selectedHero.getMaxHp() <= 110 && nearHero(selectedHero , colIndex , rowIndex )) {
	            				if(selectedHero.getTarget() == Game.heroes.get(i)) {
	            					selectedHero.setTarget(null);	            					
	            				}else {	            				
			            			boolean result = showHeroSelectionPopup("Do you want to set this hero as a target or as your hero?");
			            			if(result) {
			            				selectedHero.setTarget(Game.heroes.get(i));	
			            			}
			            			else {
			            				selectedHero = Game.heroes.get(i);	
			            				selectedHero.setTarget(null);
			            			}
	            				}
	            				
	            			} else {
	            				selectedHero = Game.heroes.get(i);	
	            				
	            			}         			
	            			            			
	            			updateMap(mapgrid);
	            			soundPlayer.playSound("mouseClick.wav");
	            			updateInfo(heroInfoLabel);
	            			break;
	            		}
	            	
	            	}
	            }
	            
	        }
	    }
	    
	    updateMap(mapgrid);
	}
	
	
	public boolean nearHero(Hero hero, int row, int column) {
	    // Get the hero's current location
	    int i = hero.getLocation().x;
	    int j = hero.getLocation().y;
	    
	    if (Math.abs(i - row) > 1) {
	    	return false;
	    }else if(Math.abs(j - column) > 1) {
	    	
	    	return false;
	    }
	    return true;
	}

	
	private void updateHealthBar() {
	    // Update the health bar
	    double currentHealthPercentage = (double) selectedHero.getCurrentHp() / selectedHero.getMaxHp();
	    
	    // Set the progress of the health bar
	    healthBar.setProgress(currentHealthPercentage);
	    
	    // Update the text label accordingly
	    if (currentHealthPercentage >= 0.67) {    	
	    	
	    	// Create a label to display the percentage and heart symbol
	        //  healthLabel = new Label("\u2764 " + (int)(healthBar.getProgress() * 100) + "%"); // Unicode for heart symbol followed by percentage
	    	healthLabel.setText(String.format("\u2764 %.0f%%", currentHealthPercentage * 100));
	    	healthLabel.setStyle("-fx-text-fill: green; -fx-font-size: 28px;"); // Set color of the heart symbol
	        

	        // Set the CSS style to change color and display percentage
	        String cssStyle = "-fx-accent: green;";
	        healthBar.setStyle(cssStyle);
	        
	    } else if (currentHealthPercentage <= 0.33) {
	        // If health percentage is 33% or less, set the health bar and text to red
	    	healthLabel.setText(String.format("\u2764 %.0f%%", currentHealthPercentage * 100));
	    	healthLabel.setStyle("-fx-text-fill: red; -fx-font-size: 28px;"); // Set color of the heart symbol

	        // Set the CSS style to change color and display percentage
	        String cssStyle = "-fx-accent: red;";
	        healthBar.setStyle(cssStyle);
	        
	    } else {
	        // For other percentages, set the health bar and text to yellow
	    	healthLabel.setText(String.format("\u2764 %.0f%%", currentHealthPercentage * 100));
	    	healthLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 28px;"); // Set color of the heart symbol

	        // Set the CSS style to change color and display percentage
	        String cssStyle = "-fx-accent: yellow;";
	        healthBar.setStyle(cssStyle);
	       
	    }
	}

   
	public void updateMap(GridPane pan) {    	
    	
    	Effects hooverButton = new Effects();
		Image medic = new Image(getClass().getResourceAsStream("/Medic.jpeg"));
		Image figh = new Image(getClass().getResourceAsStream("/FIGH.jpeg"));
		Image exp = new Image(getClass().getResourceAsStream("/EXP.jpeg"));
		Image vaccine = new Image(getClass().getResourceAsStream("/v2.jpg"));
		Image zombie = new Image(getClass().getResourceAsStream("/Zombie.jpeg"));
		Image supply = new Image(getClass().getResourceAsStream("/supp1.jpeg"));
	

    	for(int i=0; i<15; i++){
    		for(int j=0; j<15; j++){      		 
    			Button mapButton = (Button) pan.getChildren().get(i*15+j);
    		    hooverButton.removeHoverEffect(mapButton);
    			mapButton.setGraphic(null);
    			mapButton.setStyle("-fx-background-color: #000000");
    			if(Game.map[i][j].isVisible()) {
    				mapButton = (Button) pan.getChildren().get(i*15+j);
    				mapButton.setStyle("-fx-background-color: #ECF0F1"); 
    				    			
    		        if(Game.map[i][j] instanceof CharacterCell) {    		        	
    			        if(((CharacterCell)Game.map[i][j]).getCharacter() instanceof Hero){ 
    			        	Hero hero = (Hero) ((CharacterCell)Game.map[i][j]).getCharacter() ;
    			        	
    			        	if(hero == selectedHero.getTarget()) {    			        		
    			        		mapButton.setStyle("-fx-background-color: #00FF00");   	
    	    				}   			        	
    			        	else if(hero == selectedHero) {
    	    						mapButton.setStyle("-fx-background-color: #0000FF");  
    			        	}
    			        	
    			        	if (hero.getName().equals("Tommy Miller") || hero.getName().equals("Tess") || hero.getName().equals("Riley Abel") ) {      			
	    			        	ImageView expImage = new ImageView(exp);
		    			        expImage.setFitHeight(20);    	
		    			       	expImage.setFitWidth(57);	    			    		   
		    			        mapButton.setPrefSize(75, 36);
		    			        mapButton.setGraphic(expImage);
		    			        
    			        	} else if(    			        			
    			        			hero.getName().equals("Joel Miller") ||        			        		    
    			        			hero.getName().equals("David") ){
	    			        		ImageView fighImage = new ImageView(figh);
	    			       			fighImage.setFitHeight(20);    	
	    			       			fighImage.setFitWidth(57);	    				    		  
	    				   		    mapButton.setPrefSize(75, 36);
	    				   		    mapButton.setGraphic(fighImage);	    			        		    
    			        	}else {  
        			            ImageView medicImage = new ImageView(medic);
	        			   	    medicImage.setFitHeight(20);    	
	        		   		    medicImage.setFitWidth(57);        			    		  
	        		   		    mapButton.setPrefSize(75, 36);
	        		   		    mapButton.setGraphic(medicImage);
    			        			
    			        	}			    		    
			    		    hooverButton.addHoverEffect(mapButton); 
    				  
    			   } else if(((CharacterCell)Game.map[i][j]).getCharacter() instanceof Zombie){ 
	   				   ImageView zombieImage = new ImageView(zombie);
	   				   zombieImage.setFitHeight(20);
	   				   zombieImage.setFitWidth(58);	   				 
	   				   mapButton.setPrefSize(75, 36);
	   				   mapButton.setGraphic(zombieImage);  
	   				   hooverButton.addHoverEffect(mapButton);	   				   
	   				   if(selectedHero.getTarget() == ((CharacterCell)Game.map[i][j]).getCharacter()){
	   					   mapButton.setStyle("-fx-background-color: #FF0000");
	   				   }
    				 
    			   }
    			       
    		   } else if (Game.map[i][j] instanceof CollectibleCell) {
    			   
    			   if(((CollectibleCell)Game.map[i][j]).getCollectible() instanceof Vaccine) {
      				   ImageView vaccineImage = new ImageView(vaccine);
      				   vaccineImage.setFitHeight(20);
      			       vaccineImage.setFitWidth(60);      				
      				   mapButton.setPrefSize(75, 36);
      				   mapButton.setGraphic(vaccineImage);
      				   
    			   } else if(((CollectibleCell)Game.map[i][j]).getCollectible() instanceof Supply){    				   
    				   if(Game.map[i][j].isVisible()) {  
          				   ImageView supplyImage = new ImageView(supply);
          				   supplyImage.setFitHeight(20);
          				   supplyImage.setFitWidth(60);          				 
          				   mapButton.setPrefSize(75, 36);
          				   mapButton.setGraphic(supplyImage);      			
        			   }
 
    			  }
    		  }
    		 
    		}   			
      }
    			
   	}
    	
    	updateHealthBar();
    	
	}	

    public void updateInfo(Label button) {
        if (selectedHero.getMaxHp() == 140 || selectedHero.getMaxHp() == 150) {
            heroName = "FIGH";
        } else if (selectedHero.getMaxHp() == 100 || selectedHero.getMaxHp() == 105 || selectedHero.getMaxHp() == 110) {
            heroName = "MED";
        } else if (selectedHero.getMaxHp() == 80 || selectedHero.getMaxHp() == 90 || selectedHero.getMaxHp() == 95) {
            heroName = "EXP";
        }

      
        // Set the text with appropriate symbols and add the health bar at the end
        button.setText("Hero's Information: \n" +
                "Name: " + selectedHero.getName() + " , " +
                "Type: " + heroName + " , " +
                "Health: " + selectedHero.getCurrentHp() + " \u2764\n" + // Heart symbol for health
                "Actions Available: " + selectedHero.getActionsAvailable() + " \u2694 , " +
                "Attack Damage: " + selectedHero.getAttackDmg() + " 💥\n" + // Explosion symbol for damage
                "Vaccines: " + selectedHero.getVaccineInventory().size() + " 💉 , " +
                "Supplies: " + selectedHero.getSupplyInventory().size() + " 📦");

        // Apply styling to match the game interface
        button.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: black ; -fx-text-fill: white; " +
                "-fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 10px; " +
                "-fx-padding: 10px;"); // Add padding for better readability and appearance
    }

  
    public void defeat() {
    	
    	soundPlayer.stopSound();
        soundPlayer.playSound("gameOver.wav");
        
        Effects effects = new Effects();
        
        // Set up background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/lose.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.fitWidthProperty().bind(window.widthProperty());
        backgroundImageView.fitHeightProperty().bind(window.heightProperty());
        
        Button playerLabe1l = new Button("Mission failed. You have died!");
        playerLabe1l.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #FF0000;");

        

        Button playerLabel = new Button("Do you want to play again?");
        playerLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: blue; -fx-background-color: white;");

       
        Button nextButton = new Button("Yes");
        nextButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showHero1Selection();
        });       
        nextButton.setStyle("-fx-font-size: 33px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white;"); // Change button color to black
        effects.addHoverEffect(nextButton);
        
        Button previousButton = new Button("No");
        previousButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showQuitConfirmationPopup();
           
        });       
        previousButton.setStyle("-fx-font-size: 33px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white;"); // Change button color to black
        effects.addHoverEffect(previousButton);

       
        HBox buttonBox = new HBox(nextButton, previousButton);
        buttonBox.setAlignment(Pos.CENTER); // Align the buttons to the center
        buttonBox.setSpacing(20); // Add spacing between buttons
        
        VBox layout1 = new VBox(70); // Vertical layout to stack elements
        layout1.getChildren().addAll(playerLabel, buttonBox); // Add label, image, and button to the layout
        layout1.setAlignment(Pos.CENTER); // Center align the layout
      
        
        VBox layout = new VBox(140); // Vertical layout to stack elements
        layout.getChildren().addAll(playerLabe1l ,layout1); // Add label, image, and button to the layout
        layout.setAlignment(Pos.CENTER); // Center align the layout
        
        BorderPane root = new BorderPane();
        StackPane.setAlignment(backgroundImageView, Pos.CENTER);
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(backgroundImageView, layout);
       
    }
     

    private void victory() {
    	soundPlayer.stopSound();
        soundPlayer.playSound("victory.wav");
        
        Effects effects = new Effects();
        
        Button wonLabel = new Button("Victory!");
        wonLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        Button playAgainLabel = new Button("Do you want to play again?");
        playAgainLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: blue; -fx-background-color: transparent; -fx-border-color: transparent;");
        
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e -> {
           soundPlayer.stopSound();
           soundPlayer.playSound("mouseClick.wav");
           soundPlayer.gameSoundTrack();
           showHero1Selection();
        });       
        yesButton.setStyle("-fx-font-size: 33px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white;"); // Change button color to green
        effects.addHoverEffect(yesButton);
        
        Button noButton = new Button("No");
        noButton.setOnAction(e -> {
            soundPlayer.playSound("mouseClick.wav");
            showQuitConfirmationPopup();
        
        });       
        noButton.setStyle("-fx-font-size: 33px; -fx-font-family: 'Arial White'; -fx-font-weight: bold; -fx-background-color: black; -fx-text-fill: white;"); // Change button color to green
        effects.addHoverEffect(noButton);
    
        HBox buttonBox = new HBox(yesButton, noButton);
        buttonBox.setAlignment(Pos.CENTER); // Align the buttons to the center
        buttonBox.setSpacing(50); // Add spacing between buttons
               
        VBox layout = new VBox(140); // Vertical layout to stack elements
        layout.getChildren().addAll(wonLabel, playAgainLabel, buttonBox); // Add label, image, and button to the layout
        layout.setAlignment(Pos.CENTER); // Center align the layout
        
        BorderPane root = new BorderPane();
        root.setCenter(layout); // Set layout in the center of the BorderPane
       

        rootPane.getChildren().clear();
        rootPane.getChildren().add(root);
        rootPane.setStyle("-fx-background-color: #7A33FF;"); // Light blue background color
        
    }

    
    
}

