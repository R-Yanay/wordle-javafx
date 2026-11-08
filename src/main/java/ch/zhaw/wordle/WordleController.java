package ch.zhaw.wordle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Verknüpft die JavaFX-UI mit der Spiellogik.
 * Erstellt das 6×5 Grid, das virtuelle Keyboard und aktualisiert
 * die Farben nach jedem Versuch.
 */
public class WordleController {

    // Wordle-Farben
    private static final String COLOR_CORRECT = "#6aaa64";  // Grün
    private static final String COLOR_PRESENT = "#c9b458";  // Gelb
    private static final String COLOR_ABSENT = "#787c7e";   // Grau
    private static final String COLOR_DEFAULT = "#121213";  // Dunkel (Hintergrund)
    private static final String COLOR_BORDER = "#3a3a3c";   // Rahmen

    // Keyboard-Layout
    private static final String[] KEYBOARD_ROWS = {
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
    };

    private WordleGame game;
    private final VBox root;
    private final GridPane grid;
    private final Label messageLabel;
    private final Button restartButton;
    private Scene scene;

    // Grid-Buttons: 6 Reihen × 5 Spalten
    private final Button[][] gridButtons = new Button[WordleGame.getMaxAttempts()][WordleGame.getWordLength()];

    // Keyboard-Buttons für Farbspeicherung
    private final Map<Character, Button> keyboardButtons = new HashMap<>();

    // Aktueller Zustand
    private int currentRow;
    private String currentGuess;
    private boolean gameOver;

    // Gespeicherte Farben pro Buchstabe für das Keyboard
    private final Map<Character, WordleGame.LetterState> letterColors = new HashMap<>();

    public WordleController() {
        this.game = new WordleGame();
        this.root = new VBox();
        this.grid = new GridPane();
        this.messageLabel = new Label();
        this.restartButton = new Button("Neustart");
        this.currentRow = 0;
        this.currentGuess = "";
        this.gameOver = false;

        buildUI();
    }

    /**
     * @return die Wurzel der JavaFX-Szene
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Richtet den Tastatur-Listener für die physische Tastatur ein.
     * Buchstaben (A-Z), Enter und Backspace werden an die gleichen
     * Methoden weitergeleitet wie das virtuelle Keyboard.
     */
    public void setupKeyboardListener(Scene scene) {
        this.scene = scene;
        scene.setOnKeyPressed(event -> {
            if (gameOver) {
                return;
            }

            KeyCode code = event.getCode();

            // Buchstaben A-Z
            String text = event.getText();
            if (text.length() == 1 && Character.isLetter(text.charAt(0))) {
                char letter = Character.toUpperCase(text.charAt(0));
                handleKeyPress(letter);
                event.consume();
                return;
            }

            // Enter
            if (code == KeyCode.ENTER) {
                handleSubmit();
                event.consume();
                return;
            }

            // Backspace
            if (code == KeyCode.BACK_SPACE) {
                handleBackspace();
                event.consume();
                return;
            }
        });
    }

    /**
     * Erstellt die gesamte UI: Titel, Grid, Keyboard, Neustart-Button.
     */
    private void buildUI() {
        // Dunkler Hintergrund
        root.setStyle("-fx-background-color: " + COLOR_DEFAULT + ";");
        root.setSpacing(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Titel
        Label title = new Label("W O R D L E");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        title.setPadding(new Insets(10, 0, 10, 0));

        // Grid
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < WordleGame.getMaxAttempts(); row++) {
            for (int col = 0; col < WordleGame.getWordLength(); col++) {
                Button btn = new Button("");
                btn.setPrefSize(60, 60);
                btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                btn.setStyle("-fx-background-color: transparent; "
                        + "-fx-border-color: " + COLOR_BORDER + "; "
                        + "-fx-border-width: 2; "
                        + "-fx-text-fill: WHITE; "
                        + "-fx-alignment: center;");
                gridButtons[row][col] = btn;
                grid.add(btn, col, row);
            }
        }

        // Nachricht
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setFont(Font.font("Arial", 16));
        messageLabel.setPadding(new Insets(10, 0, 10, 0));

        // Keyboard
        VBox keyboard = buildKeyboard();

        // Neustart-Button
        restartButton.setFont(Font.font("Arial", 14));
        restartButton.setStyle("-fx-background-color: #818384; "
                + "-fx-text-fill: WHITE; "
                + "-fx-padding: 10 20; "
                + "-fx-cursor: hand;");
        restartButton.setOnAction(e -> restartGame());

        // Alles zusammenfügen
        root.getChildren().addAll(title, grid, messageLabel, keyboard, restartButton);
    }

    /**
     * Erstellt das virtuelle Keyboard mit 3 Reihen.
     * Layout:
     *          Q W E R T Y U I O P
     *    Enter  A S D F G H J K L  ⌫
     *          Z X C V B N M
     * 
     * Jede Reihe ist eine HBox, alle Reihen sind in einer VBox zentriert.
     */
    private VBox buildKeyboard() {
        VBox keyboard = new VBox();
        keyboard.setSpacing(5);
        keyboard.setAlignment(Pos.CENTER);
        keyboard.setPadding(new Insets(10, 0, 10, 0));

        // Tastengroesse
        final int KEY_SIZE = 45;
        final int KEY_WIDE = 75;

        // Reihe 0: QWERTYUIOP (10 Tasten)
        String row1 = "QWERTYUIOP";
        HBox hbox1 = new HBox(5);
        hbox1.setAlignment(Pos.CENTER);
        for (int col = 0; col < row1.length(); col++) {
            char letter = row1.charAt(col);
            Button btn = createKeyButton(String.valueOf(letter), KEY_SIZE, KEY_SIZE);
            final char finalLetter = letter;
            btn.setOnAction(e -> handleKeyPress(finalLetter));
            keyboardButtons.put(letter, btn);
            hbox1.getChildren().add(btn);
        }
        keyboard.getChildren().add(hbox1);

        // Reihe 1: Enter + ASDFGHJKL + Backspace (9 Tasten + 2 Sonderzeichen)
        String row2 = "ASDFGHJKL";
        HBox hbox2 = new HBox(5);
        hbox2.setAlignment(Pos.CENTER);
        
        Button enter = createKeyButton("Enter", KEY_WIDE, KEY_SIZE);
        enter.setOnAction(e -> handleSubmit());
        hbox2.getChildren().add(enter);
        
        for (int col = 0; col < row2.length(); col++) {
            char letter = row2.charAt(col);
            Button btn = createKeyButton(String.valueOf(letter), KEY_SIZE, KEY_SIZE);
            final char finalLetter = letter;
            btn.setOnAction(e -> handleKeyPress(finalLetter));
            keyboardButtons.put(letter, btn);
            hbox2.getChildren().add(btn);
        }
        
        Button backspace = createKeyButton("⌫", KEY_WIDE, KEY_SIZE);
        backspace.setOnAction(e -> handleBackspace());
        hbox2.getChildren().add(backspace);
        
        keyboard.getChildren().add(hbox2);

        // Reihe 2: ZXCVBNM (7 Tasten)
        String row3 = "ZXCVBNM";
        HBox hbox3 = new HBox(5);
        hbox3.setAlignment(Pos.CENTER);
        for (int col = 0; col < row3.length(); col++) {
            char letter = row3.charAt(col);
            Button btn = createKeyButton(String.valueOf(letter), KEY_SIZE, KEY_SIZE);
            final char finalLetter = letter;
            btn.setOnAction(e -> handleKeyPress(finalLetter));
            keyboardButtons.put(letter, btn);
            hbox3.getChildren().add(btn);
        }
        keyboard.getChildren().add(hbox3);

        return keyboard;
    }

    /**
     * Erstellt einen standardisierten Keyboard-Button.
     */
    private Button createKeyButton(String text, double width, double height) {
        Button btn = new Button(text);
        btn.setPrefSize(width, height);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btn.setStyle("-fx-background-color: #818384; "
                + "-fx-text-fill: WHITE; "
                + "-fx-cursor: hand; "
                + "-fx-alignment: center;");
        return btn;
    }

    /**
     * Behandelt einen Tastendruck (Buchstabe).
     */
    private void handleKeyPress(char letter) {
        if (gameOver || currentGuess.length() >= WordleGame.getWordLength()) {
            return;
        }

        currentGuess += letter;

        // Buchstaben im Grid anzeigen
        int col = currentGuess.length() - 1;
        gridButtons[currentRow][col].setText(String.valueOf(letter));
        gridButtons[currentRow][col].setStyle("-fx-background-color: transparent; "
                + "-fx-border-color: WHITE; "
                + "-fx-border-width: 2; "
                + "-fx-text-fill: WHITE; "
                + "-fx-font-size: 20; "
                + "-fx-font-weight: bold;");
    }

    /**
     * Behandelt Backspace (Löschen).
     */
    private void handleBackspace() {
        if (gameOver || currentGuess.isEmpty()) {
            return;
        }

        int col = currentGuess.length() - 1;
        currentGuess = currentGuess.substring(0, currentGuess.length() - 1);

        gridButtons[currentRow][col].setText("");
        gridButtons[currentRow][col].setStyle("-fx-background-color: transparent; "
                + "-fx-border-color: " + COLOR_BORDER + "; "
                + "-fx-border-width: 2; "
                + "-fx-text-fill: WHITE;");
    }

    /**
     * Behandelt Enter (Versuch absenden).
     */
    private void handleSubmit() {
        if (gameOver) {
            return;
        }

        if (currentGuess.length() < WordleGame.getWordLength()) {
            messageLabel.setText("Nicht genug Buchstaben!");
            messageLabel.setTextFill(Color.web("#ff6b6b"));
            return;
        }

        // Doppelte Versuche verhindern
        if (game.getGuesses().contains(currentGuess)) {
            messageLabel.setText("Schon versucht!");
            messageLabel.setTextFill(Color.web("#ff6b6b"));
            return;
        }

        // Wortvalidierung: Nur echte Woerter aus der Wortliste sind erlaubt
        if (!WordDictionary.containsWord(currentGuess)) {
            messageLabel.setText("Nicht in Wortliste!");
            messageLabel.setTextFill(Color.web("#ff6b6b"));
            return;
        }

        // Versuch prüfen
        WordleGame.LetterState[] states = game.checkGuess(currentGuess);

        // Grid färben
        for (int col = 0; col < WordleGame.getWordLength(); col++) {
            Button btn = gridButtons[currentRow][col];
            String color = getStateColor(states[col]);
            btn.setStyle("-fx-background-color: " + color + "; "
                    + "-fx-border-color: " + color + "; "
                    + "-fx-border-width: 2; "
                    + "-fx-text-fill: WHITE; "
                    + "-fx-font-size: 20; "
                    + "-fx-font-weight: bold;");

            // Keyboard-Farben aktualisieren (nur verbessern, nicht verschlechtern)
            char letter = currentGuess.charAt(col);
            updateKeyboardColor(letter, states[col]);
        }

        // Spielende prüfen
        if (game.isLastGuessCorrect()) {
            messageLabel.setText("🎉 Gewonnen! Das Wort war: " + game.getSecretWord());
            messageLabel.setTextFill(Color.web(COLOR_CORRECT));
            gameOver = true;
        } else if (game.isGameOver()) {
            messageLabel.setText("Verloren! Das Wort war: " + game.getSecretWord());
            messageLabel.setTextFill(Color.web("#ff6b6b"));
            gameOver = true;
        } else {
            messageLabel.setText("");
        }

        currentRow++;
        currentGuess = "";
    }

    /**
     * Gibt die CSS-Farbe für einen LetterState zurück.
     */
    private String getStateColor(WordleGame.LetterState state) {
        switch (state) {
            case CORRECT:
                return COLOR_CORRECT;
            case PRESENT:
                return COLOR_PRESENT;
            case ABSENT:
                return COLOR_ABSENT;
            default:
                return COLOR_DEFAULT;
        }
    }

    /**
     * Aktualisiert die Farbe eines Keyboard-Buttons.
     * Grün > Gelb > Grau (nur verbessern, nicht verschlechtern).
     */
    private void updateKeyboardColor(char letter, WordleGame.LetterState newState) {
        WordleGame.LetterState oldState = letterColors.getOrDefault(letter, WordleGame.LetterState.ABSENT);

        // Priorität: CORRECT > PRESENT > ABSENT
        if (newState == WordleGame.LetterState.CORRECT) {
            letterColors.put(letter, newState);
        } else if (newState == WordleGame.LetterState.PRESENT && oldState != WordleGame.LetterState.CORRECT) {
            letterColors.put(letter, newState);
        } else if (newState == WordleGame.LetterState.ABSENT && oldState == WordleGame.LetterState.ABSENT) {
            letterColors.put(letter, newState);
        }

        // Button-Farbe aktualisieren
        Button btn = keyboardButtons.get(letter);
        if (btn != null) {
            WordleGame.LetterState currentState = letterColors.get(letter);
            String color = getStateColor(currentState);
            btn.setStyle("-fx-background-color: " + color + "; "
                    + "-fx-text-fill: WHITE; "
                    + "-fx-cursor: hand;");
        }
    }

    /**
     * Startet ein neues Spiel.
     */
    private void restartGame() {
        // Neues Spiel erstellen
        game = new WordleGame();
        currentRow = 0;
        currentGuess = "";
        gameOver = false;
        letterColors.clear();
        messageLabel.setText("");

        // Tastaturfokus nach Neustart zurücksetzen
        if (scene != null) {
            root.setFocusTraversable(true);
            root.requestFocus();
        }

        // Grid zurücksetzen
        for (int row = 0; row < WordleGame.getMaxAttempts(); row++) {
            for (int col = 0; col < WordleGame.getWordLength(); col++) {
                Button btn = gridButtons[row][col];
                btn.setText("");
                btn.setStyle("-fx-background-color: transparent; "
                        + "-fx-border-color: " + COLOR_BORDER + "; "
                        + "-fx-border-width: 2; "
                        + "-fx-text-fill: WHITE;");
            }
        }

        // Keyboard zurücksetzen
        for (Button btn : keyboardButtons.values()) {
            btn.setStyle("-fx-background-color: #818384; "
                    + "-fx-text-fill: WHITE; "
                    + "-fx-cursor: hand;");
        }
    }
}
