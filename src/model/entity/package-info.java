/**
 * Die Entitäten der Datenbank als reine Datenklassen: je eine Klasse pro
 * Tabelle, mit Feldern, Gettern und Settern und sonst nichts.
 *
 * <p>Klassen dieses Pakets kennen weder die Datenbank noch die View. Genau das
 * macht sie verlässlich: {@link model.dao} baut sie aus einer Ergebniszeile,
 * und es gibt kein Feld, das dabei uninitialisiert bleiben und später eine
 * NullPointerException auslösen könnte.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package model.entity;