package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PropertyChangeHandle
{
    private PropertyChangeSupport support;

    public PropertyChangeHandle(PropertyChangeSupport support)
    {
        this.support = support;
    }

    public void addPropertyChangeListener(String probName, PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(probName, pcl);
    }

    public void propertyChange(String propName, Object newValue)
    {
        support.firePropertyChange(propName, null, newValue);
    }

}
