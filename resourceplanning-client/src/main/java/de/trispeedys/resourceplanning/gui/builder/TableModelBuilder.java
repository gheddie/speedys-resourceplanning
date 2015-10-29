package de.trispeedys.resourceplanning.gui.builder;

import java.lang.reflect.Field;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class TableModelBuilder
{
    public static TableModel createGenericTableModel(List<?> objects)
    {
        try
        {
            if ((objects == null) || (objects.size() == 0))
            {
                return null;
            }

            Class<? extends Object> clazz = objects.get(0).getClass();
            int fieldCount = clazz.getDeclaredFields().length;

            Object[] headers = new Object[fieldCount];
            for (int headerIndex = 0; headerIndex < fieldCount; headerIndex++)
            {
                headers[headerIndex] = clazz.getDeclaredFields()[headerIndex].getName();
            }
            Object[][] data = new Object[objects.size()][fieldCount];
            int rowIndex = 0;
            for (Object obj : objects)
            {
                Field field = null;
                for (int colIndex = 0; colIndex < fieldCount; colIndex++)
                {
                    field = clazz.getDeclaredFields()[colIndex];
                    field.setAccessible(true);
                    data[rowIndex][colIndex] = field.get(obj);
                }      
                rowIndex++;
            }
            return new DefaultTableModel(data, headers);   
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}