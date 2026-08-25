# Parkhaus-Verwaltung

Swing-Anwendung zur Verwaltung eines Parkhauses mit MySQL-Datenbank.

## Funktionen

- Anzeige freier Parkplätze (berechnet aus Etagen-Kapazität minus belegter Plätze)
- Ein- und Ausfahrt von Fahrzeugen mit Parkplatz-Zuweisung
- Administrations-GUI mit Fahrzeug- und Parkplatz-Tabellen

## Technik

- **MVC-Architektur** (`model` / `view` / `controller`-Packages)
- **Observer-Muster** über `PropertyChangeSupport`: das Model meldet Änderungen
  an die View, ohne sie zu kennen
- **JDBC/MySQL**: Verbindung über `MyConnection`; das Datenbankschema liegt
  als fertiges SQL-Skript bei ([db_parkhaus.sql](db_parkhaus.sql)), zusätzlich als
  MySQL-Workbench-Modell ([datenbankPark.mwb](datenbankPark.mwb))

## Datenbank einrichten

1. Das Skript [db_parkhaus.sql](db_parkhaus.sql) in MySQL einspielen — es legt die
   Datenbank `parkhaus` mit allen Tabellen an **und** befüllt die Stammdaten.
2. Zugangsdaten und Port in `src/model/Parkhaus.java` an die eigene Umgebung
   anpassen (welcher Port frei ist, hängt vom jeweiligen System ab).

### Wichtig: Stammdaten der Parketagen

Das Programm legt die Parketagen nicht selbst an — aus ihnen berechnet sich
die Gesamtzahl der Parkplätze. Ohne diese Datensätze meldet das Parkhaus
0 freie Plätze und keine Einfahrt ist möglich. Das SQL-Skript bringt sie
deshalb bereits mit:

| etageNr | anzahlPlaetze |
|---------|---------------|
| 0       | 20            |
| 1       | 120           |
| 2       | 120           |

## Programm starten

- MySQL Connector/J im Classpath
- Einstiegspunkt: `src/MainGarage.java`
