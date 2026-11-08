package ch.zhaw.wordle;

import java.util.ArrayList;
import java.util.List;

/**
 * Kernlogik des Wordle-Spiels.
 * Verwaltet den Spielzustand (geheimes Wort, Versuche, Ergebnis)
 * und berechnet die Farbcodierung für jeden Versuch.
 */
public class WordleGame {

    public enum LetterState {
        CORRECT,   // 🟢 Grün – richtige Position
        PRESENT,   // 🟡 Gelb – im Wort, aber anderswo
        ABSENT     // ⚫ Grau – nicht im Wort
    }

    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    private final String secretWord;
    private final List<String> guesses;

    /**
     * Startet ein neues Spiel mit einem zufälligen Wort.
     */
    public WordleGame() {
        this.secretWord = WordDictionary.randomWord();
        this.guesses = new ArrayList<>();
    }

    /**
     * Prüft einen Versuch und gibt die Farbcodierung zurück.
     * Fügt den Versuch automatisch zur Liste hinzu.
     *
     * @param guess der Versuch des Spielers (5 Buchstaben, Großbuchstaben)
     * @return Array aus 5 LetterState-Werten, einer pro Buchstabe
     */
    public LetterState[] checkGuess(String guess) {
        LetterState[] result = new LetterState[WORD_LENGTH];
        char[] secret = secretWord.toCharArray();
        char[] input = guess.toCharArray();

        // Zuerst: Grüne Buchstaben finden (richtige Position)
        boolean[] used = new boolean[WORD_LENGTH];
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (input[i] == secret[i]) {
                result[i] = LetterState.CORRECT;
                used[i] = true;
            }
        }

        // Dann: Gelbe Buchstaben finden (falsche Position)
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == LetterState.CORRECT) {
                continue;
            }
            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!used[j] && input[i] == secret[j]) {
                    result[i] = LetterState.PRESENT;
                    used[j] = true;
                    break;
                }
            }
        }

        // Rest: Graue Buchstaben (nicht im Wort)
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == null) {
                result[i] = LetterState.ABSENT;
            }
        }

        guesses.add(guess);
        return result;
    }

    /**
     * Prüft, ob der letzte Versuch das Wort korrekt erraten hat.
     * Wird nach {@code checkGuess()} aufgerufen, um die Gewinnmeldung zu bestimmen.
     *
     * @return true, wenn der letzte Versuch das geheime Wort war
     */
    public boolean isLastGuessCorrect() {
        if (guesses.isEmpty()) {
            return false;
        }
        return guesses.get(guesses.size() - 1).equals(secretWord);
    }

    /**
     * @return das geheime Wort (für Debugging/Verlust-Anzeige)
     */
    public String getSecretWord() {
        return secretWord;
    }

    /**
     * @return die Liste aller bisherigen Versuche
     */
    public List<String> getGuesses() {
        return new ArrayList<>(guesses);
    }

    /**
     * @return true, wenn das Spiel beendet ist (gewonnen oder 6 Versuche aufgebraucht)
     */
    public boolean isGameOver() {
        return isLastGuessCorrect() || guesses.size() >= MAX_ATTEMPTS;
    }

    /**
     * @return die aktuelle Anzahl der Versuche
     */
    public int getAttemptCount() {
        return guesses.size();
    }

    /**
     * @return die maximale Anzahl Versuche
     */
    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    /**
     * @return die Länge des Wortes
     */
    public static int getWordLength() {
        return WORD_LENGTH;
    }
}
