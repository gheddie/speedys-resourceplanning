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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.coderazzi.filters.gui.TableFilterHeader;
import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.gui.builder.TableModelBuilder;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;
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
    
    private ManualAssignmentDTO selectedManualAssignment;

    private List<ManualAssignmentDTO> manualAssignments;

    public ResourcePlanningMainFrame()
    {
        initComponents();
        setSize(800, 600);
        putListeners();
        new TableFilterHeader(tbEvents);
        new TableFilterHeader(tbHelpers);
        new TableFilterHeader(tbManualAssignments);
        fillPressed(null);
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
        // load
        events = new ResourceInfoService().getResourceInfoPort().queryEvents().getItem();
        helpers = new ResourceInfoService().getResourceInfoPort().queryHelpers().getItem();
        manualAssignments = new ResourceInfoService().getResourceInfoPort().queryManualAssignments().getItem();
        
        tbEvents.setModel(TableModelBuilder.createGenericTableModel(events));
        tbHelpers.setModel(TableModelBuilder.createGenericTableModel(helpers));
        tbManualAssignments.setModel(TableModelBuilder.createGenericTableModel(manualAssignments));
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
        pnlHelper = new JPanel();
        scHelpers = new JScrollPane();
        tbHelpers = new JTable();
        pnlManualAssignments = new JPanel();
        scManualAssignments = new JScrollPane();
        tbManualAssignments = new JTable();
        label1 = new JLabel();
        textField1 = new JTextField();
        btnBookManually = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 49, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

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
                ((GridBagLayout)pnlPositions.getLayout()).rowHeights = new int[] {203, 0, 131, 0};
                ((GridBagLayout)pnlPositions.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout)pnlPositions.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};

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
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- btnPlanEvent ----
                btnPlanEvent.setText("Planen");
                btnPlanEvent.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btnPlanPressed(e);
                    }
                });
                pnlPositions.add(btnPlanEvent, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tabbedPane1.addTab("Positionen", pnlPositions);

            //======== pnlHelper ========
            {
                pnlHelper.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlHelper.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)pnlHelper.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)pnlHelper.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== scHelpers ========
                {
                    scHelpers.setViewportView(tbHelpers);
                }
                pnlHelper.add(scHelpers, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tabbedPane1.addTab("Helfer", pnlHelper);

            //======== pnlManualAssignments ========
            {
                pnlManualAssignments.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWidths = new int[] {0, 55, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                //======== scManualAssignments ========
                {
                    scManualAssignments.setViewportView(tbManualAssignments);
                }
                pnlManualAssignments.add(scManualAssignments, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- label1 ----
                label1.setText("Position:");
                pnlManualAssignments.add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));
                pnlManualAssignments.add(textField1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- btnBookManually ----
                btnBookManually.setText("Buchen");
                pnlManualAssignments.add(btnBookManually, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tabbedPane1.addTab("Manuelle Zuweisungen", pnlManualAssignments);
        }
        contentPane.add(tabbedPane1, new GridBagConstraints(0, 0, 3, 2, 0.0, 0.0,
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
    private JPanel pnlHelper;
    private JScrollPane scHelpers;
    private JTable tbHelpers;
    private JPanel pnlManualAssignments;
    private JScrollPane scManualAssignments;
    private JTable tbManualAssignments;
    private JLabel label1;
    private JTextField textField1;
    private JButton btnBookManually;
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
