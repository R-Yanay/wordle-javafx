# Wordle – JavaFX Desktop App

Eine Wordle-Spiel-Implementierung in Java mit JavaFX als Benutzeroberfläche. Das Spiel verwendet alle 2.315 offiziellen Wordle-Antwortwörter und folgt den bekannten Regeln: Errate das 5-Buchstaben-Wort in maximal 6 Versuchen.

## Features

- Vollständige Wordle-Spiellogik mit Farbcodierung (Grün, Gelb, Grau)
- 2.315 offizielle Wordle-Wörter im Wörterbuch
- JavaFX-GUI mit 6×5 Grid und virtuellem QWERTY-Keyboard
- Tastatursteuerung – physische Tastatur und virtuelles Keyboard
- Wortvalidierung – nur gültige Wörter aus der Wortliste werden akzeptiert
- Doppelte Versuche verhindern
- Neustart-Funktion mit neuem zufälligem Wort
- Dunkles Wordle-Design mit originalgetreuen Farben

## Technischer Stack

| Technologie | Version |
|---|---|
| Java | 25 |
| JavaFX | 21.0.6 |
| Build-System | Maven |
| IDE | IntelliJ IDEA |

## Projektstruktur

```
src/main/java/ch/zhaw/wordle/
├── WordleApplication.java    # Einstiegspunkt – JavaFX-Fenster erstellen
├── WordleController.java     # UI-Steuerung – Grid, Keyboard, Tastatureingaben
├── WordleGame.java           # Spiellogik – Farbcodierung, Gewinn/Verlust
└── WordDictionary.java       # Wörterbuch – 2.315 offizielle Wordle-Wörter
```

### Klassenverantwortlichkeiten (MVC-Muster)

| Rolle | Klasse | Verantwortung |
|---|---|---|
| Model | `WordleGame` | Spielzustand, Farbcodierung, Gewinnprüfung |
| Model | `WordDictionary` | Wortliste, zufälliges Wort, Wortvalidierung |
| Controller | `WordleController` | UI-Aufbau, Tastatureingaben, Farbanzeige |
| Application | `WordleApplication` | JavaFX-Start, Fenster, Tastaturfokus |

## Starten der App

### Voraussetzungen

- Java 25 (oder höher) installiert
- Maven installiert
- JavaFX-SDK (für den direkten Start in IntelliJ IDEA)
- IntelliJ IDEA (empfohlen) oder eine andere Java-IDE

### Über Maven (Empfohlen)

```bash
# Projekt bauen und starten
mvn clean javafx:run
```

### Über IntelliJ IDEA

1. Projekt in IntelliJ IDEA öffnen
2. `WordleApplication.java` öffnen
3. Auf Run klicken oder `Shift+F10` drücken

> Hinweis: Für den direkten Start in IntelliJ muss das JavaFX-SDK installiert sein und die VM-Options korrekt konfiguriert sein:
> ```
> --module-path "pfad/zu/javafx-sdk/lib" --add-modules javafx.controls,javafx.graphics
> ```

## OOP-Prinzipien

Dieses Projekt demonstriert folgende OOP-Prinzipien:

- Single Responsibility Principle – Jede Klasse hat genau eine klare Verantwortung
- Encapsulation – Spielzustand ist in `WordleGame` gekapselt
- Separation of Concerns – Saubere Trennung von Logik (`WordleGame`) und UI (`WordleController`)
- MVC-Muster – Model-View-Controller-Architektur für klare Verantwortlichkeiten

## Was ich gelernt habe

Bei diesem Projekt habe ich die MVC-Architektur praktisch angewendet und verstanden, warum die Trennung von Spiellogik und Benutzeroberfläche wichtig ist. Der Farbcodierungsalgorithmus (grün → gelb → grau) war die komplexeste Stelle – hier musste ich genau auf die Reihenfolge der Buchstabenprüfung achten, damit doppelte Buchstaben korrekt behandelt werden. Außerdem habe ich gelernt, wie JavaFX mit Maven und dem JavaFX-SDK zusammenarbeitet, sowie wie man ein Projekt sauber mit Git und GitHub teilt.

## Lizenz

Dieses Projekt wurde als Übungprojekt erstellt und dient Demonstrationszwecken.
