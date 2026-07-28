package view;

import model.Fahrzeug;
import model.Garage;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GarageTableModel extends AbstractTableModel
{
    private List<Garage> garageList;
    private final String[] columnNames ={"platzNr", "parketage_etageNr","fahrzeug_nummernschild"};

    public GarageTableModel(List<Garage> garageList)
    {
        this.garageList = garageList;
    }

    public void updateTable(List<Garage> neueListe)
    {
        this.garageList = neueListe;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return garageList.size();
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
            Garage garage = garageList.get(rowIndex);
            Method getValue =garage.getClass().getMethod(methodeName);
            retVal = getValue.invoke(garage);
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
