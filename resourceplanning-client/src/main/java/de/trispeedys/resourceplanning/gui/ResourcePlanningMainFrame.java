/*
 * Created by JFormDesigner on Sun Oct 25 22:35:29 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

/**
 * @author Stefan Schulz
 */
public class ResourcePlanningMainFrame extends JFrame
{
    private static List<EventDTO> events;
    
    public ResourcePlanningMainFrame()
    {
        initComponents();
    }

    private void fillPressed(ActionEvent e)
    {        
        tbEvents.setModel(createEventTableModel());
    }
    
    public static TableModel createEventTableModel()
    {
        events = new ResourceInfoService().getResourceInfoPort().queryEvents().getItem();
        Object[] headers = new Object[]
        {
            "desc"
        };
        Object[][] data = new Object[events.size()][2];
        int index = 0;
        for (EventDTO event : events)
        {            
            data[index][0] = event.getDescription();
            index++;
        }
        return new DefaultTableModel(data, headers);
    }
    
    private void eventSelected(Long eventId)
    {
        System.out.println("eventSelected : " + eventId);
        
        fillTree(eventId);
        // fillTree(null);
    }

    private void fillTree(Long eventId)
    {
        if (eventId != null)
        {
            treeTablePositions.setModel(new TreeTableDataModel(
                    ResourcePlanningClientRoutines.createDataStructure(eventId)));       
        }
        else
        {
            // show all events
            TreeTableDataNode root = new TreeTableDataNode("root", "root", null);
            for (EventDTO dto : events)
            {
                root.addChild(ResourcePlanningClientRoutines.createDataStructure(dto.getEventId()));
            }
            treeTablePositions.setModel(new TreeTableDataModel(root));
        }
        // TODO expand tree
        // treeTablePositions.expandAll();
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        scPositions = new JScrollPane();
        treeTablePositions = new TreeTable();
        scEvents = new JScrollPane();
        tbEvents = new JTable();
        button1 = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {173, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0, 0.0, 1.0E-4};

        //======== scPositions ========
        {
            scPositions.setViewportView(treeTablePositions);
        }
        contentPane.add(scPositions, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //======== scEvents ========
        {

            //---- tbEvents ----
            tbEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {
                    int selectedRow = tbEvents.getSelectedRow();
                    if (selectedRow >= 0)
                    {
                        eventSelected(events.get(selectedRow).getEventId());   
                    }                    
                }
            });
            scEvents.setViewportView(tbEvents);
        }
        contentPane.add(scEvents, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- button1 ----
        button1.setText("Fill");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillPressed(e);
            }
        });
        contentPane.add(button1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JScrollPane scPositions;
    private TreeTable treeTablePositions;
    private JScrollPane scEvents;
    private JTable tbEvents;
    private JButton button1;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    // ---

    public static void main(String[] args)
    {
        Runnable gui = new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                new ResourcePlanningMainFrame().setVisible(true);
            }
        };
        SwingUtilities.invokeLater(gui);        
    }
}
