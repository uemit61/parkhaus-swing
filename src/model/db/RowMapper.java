package model.db;



import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Wandelt eine einzelne Zeile eines {@link java.sql.ResultSet} in ein Objekt um.
 * Als funktionales Interface lässt sich eine Umwandlung direkt als Lambda an
 * {@link model.db.MyConnection#queryList(String, RowMapper, Object...)} übergeben.
 *
 * @param <T> Typ des erzeugten Objekts
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@FunctionalInterface
public interface RowMapper<T>
{
    /**
     * Liest die Werte der aktuellen Zeile und baut daraus ein Objekt.
     *
     * @param rs das ResultSet, das bereits auf der zu lesenden Zeile steht
     * @return das gefüllte Objekt
     * @throws SQLException wenn eine Spalte nicht gelesen werden kann; behandelt
     *         wird die Ausnahme in {@link MyConnection#queryList(String, RowMapper,Object...)}
     */
    T mapRow(ResultSet rs) throws SQLException;
}
