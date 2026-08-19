/**
 * Die Fachlogik: was beim Einfahren, Ausfahren, Registrieren und Löschen
 * geschehen soll, und was davon der View gemeldet wird.
 *
 * <p>Klassen dieses Pakets stellen die Regeln auf — welcher Platz der nächste
 * freie ist, wann ein Fahrzeug nicht gelöscht werden darf — und holen sich die
 * Daten über {@link model.dao}. Der Controller kennt von der Model-Schicht nur
 * dieses Paket.
 *
 * <p>Ein {@code import java.sql.} in diesem Paket ist ein Zeichen dafür, dass
 * etwas an der falschen Stelle gelandet ist.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package model.service;