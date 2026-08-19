/**
 * Der technische Unterbau des Datenbankzugriffs: Verbindungsaufbau,
 * Platzhalter, Transaktionen und die Umwandlung einer Ergebniszeile in ein
 * Objekt.
 *
 * <p>Das Paket weiß nichts von Fahrzeugen oder Parkplätzen, es führt beliebiges
 * SQL aus. Deshalb lässt es sich als Ganzes in andere Projekte übernehmen.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package model.db;