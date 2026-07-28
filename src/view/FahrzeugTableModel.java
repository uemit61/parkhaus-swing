package view;

import model.Fahrzeug;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.lang.reflect.Method;

public class FahrzeugTableModel extends AbstractTableModel
{
    private List<Fahrzeug> fahrzeugList;
    private final String[] columnNames ={"nummernschild", "typ"};

    public FahrzeugTableModel(List<Fahrzeug> fahrzeugList)
    {
        this.fahrzeugList = fahrzeugList;
    }

    public void updateTable(List<Fahrzeug> neueListe)
    {
        this.fahrzeugList = neueListe;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return fahrzeugList.size();
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Object retVal =null;
        String propName = columnNames[columnIndex];
        try
        {
            String methodeName ="get"+propName.substring(0,1).toUpperCase()+propName.substring(1);
            Fahrzeug fahrzeug = fahrzeugList.get(rowIndex);
            Method getValue =fahrzeug.getClass().getMethod(methodeName);
            retVal = getValue.invoke(fahrzeug);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return retVal;
    }
    @Override
    public String getColumnName(int colum)
    {
        return columnNames[colum];
    }

}
