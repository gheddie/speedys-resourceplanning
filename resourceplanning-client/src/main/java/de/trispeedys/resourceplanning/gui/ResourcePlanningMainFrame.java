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
import java.beans.*;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.coderazzi.filters.gui.TableFilterHeader;
import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

/**
 * @author Stefan Schulz
 */
public class ResourcePlanningMainFrame extends JFrame
{
    private static List<EventDTO> events;

    private EventDTO selectedEvent;

    private List<HelperDTO> helpers;
    
    private HelperDTO selectedHelper;

    public ResourcePlanningMainFrame()
    {
        initComponents();
        setSize(800, 600);
        putListeners();
        new TableFilterHeader(tbEvents);
        new TableFilterHeader(tbHelpers);
    }

    private void putListeners()
    {
        tbEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbEvents.getSelectedRow();
                if (selectedRow >= 0)
                {
                    EventDTO eventDTO = events.get(selectedRow);
                    eventSelected(eventDTO);
                }
            }
        });        
    }

    private void fillPressed(ActionEvent e)
    {
        tbEvents.setModel(createEventTableModel());
        tbHelpers.setModel(createHelperTableModel());
    }

    public TableModel createHelperTableModel()
    {
        helpers = new ResourceInfoService().getResourceInfoPort().queryHelpers().getItem();
        Object[] headers = new Object[]
        {
            "Nachname", "Vorname"
        };
        Object[][] data = new Object[helpers.size()][2];
        int index = 0;
        for (HelperDTO helper : helpers)
        {
            data[index][0] = helper.getLastName();
            data[index][1] = helper.getFirstName();
            index++;
        }
        return new DefaultTableModel(data, headers);
    }

    public static TableModel createEventTableModel()
    {
        events = new ResourceInfoService().getResourceInfoPort().queryEvents().getItem();
        Object[] headers = new Object[]
        {
            "Beschreibung"
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

    private void eventSelected(EventDTO event)
    {
        selectedEvent = event;
        fillTree(selectedEvent.getEventId());
    }

    private void fillTree(Long eventId)
    {
        if (eventId != null)
        {
            treeTablePositions.setModel(new TreeTableDataModel(
                    ResourcePlanningClientRoutines.createDataStructure(eventId, chkUnassignedOnly.isSelected())));
        }
        else
        {
            // show all events
            TreeTableDataNode root = new TreeTableDataNode("root", "root", null);
            for (EventDTO dto : events)
            {
                root.addChild(ResourcePlanningClientRoutines.createDataStructure(dto.getEventId(), false));
            }
            treeTablePositions.setModel(new TreeTableDataModel(root));
        }
        // TODO expand tree
        // treeTablePositions.expandAll();
    }

    private void tbEventsPropertyChange(PropertyChangeEvent e)
    {
        // TODO add your code here
    }

    private void btnPlanPressed(ActionEvent e)
    {
        if (selectedEvent != null)
        {
            new ResourceInfoService().getResourceInfoPort().startProcessesForActiveHelpersByEventId(
                    selectedEvent.getEventId());
        }
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        tabbedPane1 = new JTabbedPane();
        pnlPositions = new JPanel();
        scPositions = new JScrollPane();
        treeTablePositions = new TreeTable();
        chkUnassignedOnly = new JCheckBox();
        scEvents = new JScrollPane();
        tbEvents = new JTable();
        btnPlanEvent = new JButton();
        brnFill = new JButton();
        pnlHelper = new JPanel();
        scHelpers = new JScrollPane();
        tbHelpers = new JTable();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0, 0.0, 1.0E-4};

        //======== tabbedPane1 ========
        {

            //======== pnlPositions ========
            {

                // JFormDesigner evaluation mark
                pnlPositions.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), pnlPositions.getBorder())); pnlPositions.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                pnlPositions.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlPositions.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)pnlPositions.getLayout()).rowHeights = new int[] {203, 0, 136, 0, 0};
                ((GridBagLayout)pnlPositions.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout)pnlPositions.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};

                //======== scPositions ========
                {
                    scPositions.setViewportView(treeTablePositions);
                }
                pnlPositions.add(scPositions, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- chkUnassignedOnly ----
                chkUnassignedOnly.setText("Nur unzugewiesene");
                pnlPositions.add(chkUnassignedOnly, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //======== scEvents ========
                {

                    //---- tbEvents ----
                    tbEvents.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            tbEventsPropertyChange(e);
                            tbEventsPropertyChange(e);
                        }
                    });
                    scEvents.setViewportView(tbEvents);
                }
                pnlPositions.add(scEvents, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- btnPlanEvent ----
                btnPlanEvent.setText("Planen");
                btnPlanEvent.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btnPlanPressed(e);
                    }
                });
                pnlPositions.add(btnPlanEvent, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- brnFill ----
                brnFill.setText("Fill");
                brnFill.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        fillPressed(e);
                    }
                });
                pnlPositions.add(brnFill, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tabbedPane1.addTab("Positionen", pnlPositions);

            //======== pnlHelper ========
            {
                pnlHelper.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlHelper.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlHelper.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                //======== scHelpers ========
                {
                    scHelpers.setViewportView(tbHelpers);
                }
                pnlHelper.add(scHelpers, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));
            }
            tabbedPane1.addTab("Helfer", pnlHelper);
        }
        contentPane.add(tabbedPane1, new GridBagConstraints(0, 0, 1, 3, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JTabbedPane tabbedPane1;
    private JPanel pnlPositions;
    private JScrollPane scPositions;
    private TreeTable treeTablePositions;
    private JCheckBox chkUnassignedOnly;
    private JScrollPane scEvents;
    private JTable tbEvents;
    private JButton btnPlanEvent;
    private JButton brnFill;
    private JPanel pnlHelper;
    private JScrollPane scHelpers;
    private JTable tbHelpers;
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
