package ch.zhaw.wordle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Einstiegspunkt der Wordle-App.
 * Erstellt das Fenster und übergibt die Steuerung an den Controller.
 */
public class WordleApplication extends Application {

    @Override
    public void start(Stage stage) {
        WordleController controller = new WordleController();
        Scene scene = new Scene(controller.getRoot(), 500, 650);
        controller.setupKeyboardListener(scene);
        stage.setTitle("Wordle");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
        // Root-Node muss fokussierbar sein, damit Tastatureingaben ankommen
        controller.getRoot().setFocusTraversable(true);
        controller.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
