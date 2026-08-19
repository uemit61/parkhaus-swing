/**
 * Der Datenzugriff: je eine Klasse pro Tabelle, die deren SQL hält und Objekte
 * aus {@link model.entity} zurückgibt.
 *
 * <p>Klassen dieses Pakets führen Abfragen aus und werten sie aus, treffen aber
 * keine fachlichen Entscheidungen und melden nichts an die View — dafür ist
 * {@link model.service} da. SQL steht ausschließlich in diesem Paket.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package model.dao;