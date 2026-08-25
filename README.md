# Parkhaus-Verwaltung

Swing-Anwendung zur Verwaltung eines Parkhauses mit MySQL-Datenbank.

## Funktionen

- Anzeige freier Parkplätze (berechnet aus Etagen-Kapazität minus belegter Plätze)
- Ein- und Ausfahrt von Fahrzeugen mit automatischer Parkplatz-Zuweisung
  (jeweils der kleinste freie Platz, die Etage ergibt sich aus der Kapazität)
- Positionsabfrage: auf welcher Etage und welchem Platz steht ein Fahrzeug
- Prüfung des Kennzeichen-Formats sowie Alarm, wenn ein Kennzeichen ein zweites
  Mal einfahren will
- Administrations-GUI zum Registrieren und Löschen von Fahrzeugen, mit
  Fahrzeug- und Parkplatz-Tabelle

## Technik

- **MVC-Architektur** (`model` / `view` / `controller`-Packages)
- **Geschichtete Model-Schicht**: pro Tabelle eine reine Datenklasse (`entity`),
  eine Klasse mit ihrem SQL (`dao`) und eine mit der Fachlogik (`service`). Der
  Controller kennt vom Model nur die Services, SQL steht ausschließlich in den
  DAOs. Dieselbe Aufteilung wie `@Entity` / Repository / `@Service` in Spring Boot
- **Konstruktor-Injektion**: jede Klasse bekommt ihre Abhängigkeiten beim
  Erzeugen. Weil Entitäten und Services getrennt sind, gibt es keine halb
  gefüllten Objekte mehr — ein nachgereichtes `inits()` braucht niemand
- **Observer-Muster** über `PropertyChangeSupport`: das Model meldet Änderungen
  an die View, ohne sie zu kennen. `PropertyChangeHandle` kapselt den Support,
  damit alle Modelle über denselben Kanal melden
- **JDBC/MySQL** über `MyConnection`: alle Werte gehen als Platzhalter in ein
  `PreparedStatement`, im Projekt steht kein zusammengesetztes SQL mehr. Jede
  Methode öffnet ihre eigene Verbindung per try-with-resources und schließt sie
  garantiert wieder; Schreibzugriffe laufen in einer Transaktion und rollen bei
  einem Fehler zurück. `executeUpdate()` schreibt einen Datensatz,
  `executeBatch()` sammelt viele und schickt sie in einer Übertragung.
  `queryList()` nimmt einen `RowMapper` (funktionales Interface) entgegen und
  liefert fertige Objektlisten — das `ResultSet` verlässt die Model-Schicht nicht
- **Eigene `AbstractTableModel`-Implementierungen** für die beiden Tabellen:
  `getValueAt()` ermittelt den passenden Getter per **Reflection** aus dem
  Spaltennamen, eine neue Spalte braucht deshalb nur einen Eintrag im
  `columnNames`-Array
- **Composition Root**: alle Objekte werden ausschließlich in `MainGarage`
  erzeugt und ineinander gesteckt; die Klassen suchen sich ihre Abhängigkeiten
  nicht selbst

## Projektstruktur

```
src/
├── MainGarage.java              Einstiegspunkt, Composition Root
├── controller/
│   └── Controller.java          setzt die Anwendungsfälle zusammen, bleibt DB-frei
├── model/
│   ├── entity/                  reine Datenklassen, eine je Tabelle
│   │   ├── Fahrzeug.java        Tabelle fahrzeug
│   │   ├── Garage.java          Tabelle garage
│   │   └── Parketage.java       Tabelle parketage
│   ├── dao/                     das SQL, eine Klasse je Tabelle
│   │   ├── FahrzeugDao.java     eintragen, löschen, alle lesen
│   │   ├── GarageDao.java       belegen, freigeben, Position, Platznummern
│   │   └── ParketageDao.java    Etagen lesen, freie Plätze zählen
│   ├── service/                 Fachlogik und Meldungen an die View
│   │   ├── FahrzeugService.java Registrieren, Löschen, Kennzeichenprüfung
│   │   ├── GarageService.java   Ein- und Ausfahrt, Positionsabfrage
│   │   └── ParketageService.java freie Plätze
│   ├── db/
│   │   ├── MyConnection.java    Verbindung, executeUpdate, executeBatch, queryList
│   │   └── RowMapper.java       funktionales Interface: ResultSet-Zeile → Objekt
│   └── event/
│       └── PropertyChangeHandle.java  gemeinsamer Ereignis-Kanal
└── view/
    ├── ViewParkhaus.java        Hauptfenster
    ├── AdministrationsGui.java  Adminbereich
    ├── ParkplatzTabelle.java    Tabelle der belegten Plätze
    ├── AutoTabelle.java         Tabelle der registrierten Fahrzeuge
    ├── GarageTableModel.java    TableModel dazu
    └── FahrzeugTableModel.java  TableModel dazu
```

Jedes Unterpaket beschreibt seinen Zweck in einer eigenen `package-info.java`.
Die Abhängigkeiten laufen nur in eine Richtung: `entity` kennt niemanden,
`dao` kennt `entity` und `db`, `service` kennt `dao` und `event`, und der
`controller` kennt vom Model nur `service`.

## Datenbank einrichten

Das Skript [db_parkhaus.sql](db_parkhaus.sql) in MySQL einspielen — es legt die
Datenbank `parkhaus` mit allen Tabellen an **und** befüllt die Stammdaten.
Zusätzlich liegt das Schema als MySQL-Workbench-Modell bei
([datenbankPark.mwb](datenbankPark.mwb)).

### Wichtig: Stammdaten der Parketagen

Das Programm legt die Parketagen nicht selbst an — aus ihnen berechnet sich
sowohl die Gesamtzahl der Parkplätze als auch die Zuordnung eines Platzes zu
seiner Etage. Ohne diese Datensätze meldet das Parkhaus 0 freie Plätze und
keine Einfahrt ist möglich. Das SQL-Skript bringt sie deshalb bereits mit:

| etageNr | anzahlPlaetze |
|---------|---------------|
| 0       | 20            |
| 1       | 120           |
| 2       | 120           |

Die Werte lassen sich ändern — die Anwendung liest sie zur Laufzeit aus der
Tabelle, im Code steht keine feste Platzzahl.

## Konfiguration

Die Zugangsdaten stehen nicht im Quellcode, sondern in einer
`config.properties` im Projektverzeichnis. Diese Datei ist per `.gitignore`
von der Versionierung ausgeschlossen. Als Vorlage dient
[config.properties.example](config.properties.example):

```bash
cp config.properties.example config.properties
```

Anschließend die eigenen Werte eintragen:

```properties
db.url=jdbc:mysql://localhost:3306/parkhaus
db.user=DEIN_BENUTZER
db.password=DEIN_PASSWORT
```

Welcher Port frei ist, hängt vom jeweiligen System ab — bei einer
Standard-MySQL-Installation ist es 3306, bei XAMPP häufig 3307.

## Programm starten

1. MySQL Connector/J als Bibliothek im Classpath eintragen
2. Datenbank einspielen und `config.properties` anlegen (siehe oben)
3. `src/MainGarage.java` ausführen

Das Arbeitsverzeichnis muss das Projektverzeichnis sein, da
`config.properties` relativ dazu geladen wird — in IntelliJ ist das die
Voreinstellung.

## Bedienung

**Hauptfenster** — Kennzeichen eingeben, Fahrzeugtyp wählen, dann:

- *Checkin*: registriert das Fahrzeug (falls noch nicht bekannt) und weist ihm
  den kleinsten freien Parkplatz zu
- *Checkout*: bucht das Fahrzeug aus und gibt den Platz frei
- *Show Position*: zeigt Etage und Platznummer

Das Kennzeichen muss dem Format `X-XX 1234` entsprechen (1–3 Buchstaben,
Bindestrich, 1–2 Buchstaben, Leerzeichen, 2–5 Ziffern ohne führende Null).

**Adminbereich** — Fahrzeuge registrieren und löschen sowie beide Tabellen
einsehen. Ein Fahrzeug, das gerade im Parkhaus steht, lässt sich nicht löschen;
es muss erst auschecken.

## Bekannte Grenzen

Bewusst offen gelassen und für eine Folgeversion vorgesehen:

- Fehler werden auf der Konsole ausgegeben statt in der Oberfläche angezeigt
- Die Fahrzeugtyp-Auswahl arbeitet mit Strings; geplant sind Objekte in der
  ComboBox
- Für die Kennzeichenprüfung fehlen noch Tests — als einzige Methode ohne
  Abhängigkeiten wäre sie der naheliegende Anfang

## Lizenz

Alle Rechte vorbehalten — siehe [LICENSE](LICENSE).
