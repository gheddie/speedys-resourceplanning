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
import javax.swing.event.*;

import de.trispeedys.resourceplanning.components.*;
import net.coderazzi.filters.gui.TableFilterHeader;
import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.treetable.TreeTable;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataModel;
import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.gui.builder.TableModelBuilder;
import de.trispeedys.resourceplanning.util.HierarchicalEventItemType;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.webservice.EventDTO;
import de.trispeedys.resourceplanning.webservice.HelperDTO;
import de.trispeedys.resourceplanning.webservice.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.webservice.PositionDTO;
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

    private List<PositionDTO> availablePositions;

    private PositionDTO selectedAvailablePosition;

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
        // events
        tbEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbEvents.getSelectedRow();
                if (selectedRow >= 0)
                {
                    eventSelected(events.get(selectedRow));
                }
            }
        });
        // manual assignments
        tbManualAssignments.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbManualAssignments.getSelectedRow();
                if (selectedRow >= 0)
                {
                    manualAssignmentSelected(manualAssignments.get(selectedRow));
                }
            }
        });
        // available positions
        tbAvailablePositions.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                int selectedRow = tbAvailablePositions.getSelectedRow();
                if (selectedRow >= 0)
                {
                    availablePositionSelected(availablePositions.get(selectedRow));
                }
            }
        });
    }
    
    private void btnFinishProcessesPressed(ActionEvent e)
    {
        new ResourceInfoService().getResourceInfoPort().finishUp();
    }
    
    private void btnCancelAssignmentPressed(ActionEvent e)
    {
        TreeTableDataNode pathComponent = treeTablePositions.getPathComponent();
        System.out.println(pathComponent);
        if (pathComponent == null)
        {
            return;
        }
        if (pathComponent.getEventItemType().equals(HierarchicalEventItemType.POSITION))
        {
            if ((selectedEvent != null) && (pathComponent.getEntityId() != null))
            {
                System.out.println("cancel : [helper.id:"+pathComponent.getEntityId()+"|event:"+selectedEvent.getEventId()+"]");
                new ResourceInfoService().getResourceInfoPort().cancelAssignment(selectedEvent.getEventId(), pathComponent.getEntityId());
            }
        }
    }

    private void fillPressed(ActionEvent e)
    {
        // load
        events = new ResourceInfoService().getResourceInfoPort().queryEvents().getItem();
        helpers = new ResourceInfoService().getResourceInfoPort().queryHelpers().getItem();
        manualAssignments =
                new ResourceInfoService().getResourceInfoPort().queryManualAssignments().getItem();

        // fill
        tbEvents.setModel(TableModelBuilder.createGenericTableModel(events));
        tbHelpers.setModel(TableModelBuilder.createGenericTableModel(helpers));
        tbManualAssignments.setModel(TableModelBuilder.createGenericTableModel(manualAssignments));
    }

    private void eventSelected(EventDTO event)
    {
        selectedEvent = event;
        fillTree(selectedEvent.getEventId());
    }

    private void manualAssignmentSelected(ManualAssignmentDTO manualAssignment)
    {
        selectedManualAssignment = manualAssignment;
    }

    private void availablePositionSelected(PositionDTO positionDTO)
    {
        selectedAvailablePosition = positionDTO;
    }

    private void fillTree(Long eventId)
    {
        if (eventId != null)
        {
            treeTablePositions.setModel(new TreeTableDataModel(
                    ResourcePlanningClientRoutines.createDataStructure(eventId,
                            chkUnassignedOnly.isSelected())));
        }
        else
        {
            // show all events
            TreeTableDataNode root = new TreeTableDataNode("root", "root", null, null, null);
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

    private void btnBookManuallyPressed(ActionEvent e)
    {
        if (selectedManualAssignment == null)
        {
            System.out.println("moo");
            return;
        }
        if (selectedAvailablePosition == null)
        {
            System.out.println("maa");
            return;
        }        
        new ResourceInfoService().getResourceInfoPort().completeManualAssignment(
                selectedManualAssignment.getTaskId(), selectedAvailablePosition.getPositionId());
    }

    private void tdbMainStateChanged(ChangeEvent e)
    {
        System.out.println(((JTabbedPane) e.getSource()).getSelectedIndex());
    }

    private void btnReloadAvailablePositionsPressed(ActionEvent e)
    {
        if (selectedEvent == null)
        {
            System.out.println("ppp");
            return;
        }
        // TODO ignore canceled asignments !!
        availablePositions =
                new ResourceInfoService().getResourceInfoPort()
                        .queryAvailablePositions(selectedEvent.getEventId())
                        .getItem();
        tbAvailablePositions.setModel(TableModelBuilder.createGenericTableModel(availablePositions));
    }

    private void initComponents()
    {
        // JFormDesigner - Component initialization - DO NOT MODIFY //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        toolBar1 = new JToolBar();
        btnFinishProcesses = new JButton();
        btnCancelAssignment = new JButton();
        tdbMain = new JTabbedPane();
        pnlPositions = new JPanel();
        scPositions = new JScrollPane();
        treeTablePositions = new TreeTable();
        chkUnassignedOnly = new JCheckBox();
        scEvents = new JScrollPane();
        tbEvents = new ResourcePlanningTable();
        btnPlanEvent = new JButton();
        pnlHelper = new JPanel();
        scHelpers = new JScrollPane();
        tbHelpers = new ResourcePlanningTable();
        pnlManualAssignments = new JPanel();
        borderManualAssignments = new JPanel();
        scManualAssignments = new JScrollPane();
        tbManualAssignments = new ResourcePlanningTable();
        borderAvailablePositions = new JPanel();
        scAvailablePositions = new JScrollPane();
        tbAvailablePositions = new ResourcePlanningTable();
        btnReloadAvailablePositions = new JButton();
        btnBookManually = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 49, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0, 1.0E-4};

        //======== toolBar1 ========
        {

            //---- btnFinishProcesses ----
            btnFinishProcesses.setText("Abschliessen");
            btnFinishProcesses.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    btnFinishProcessesPressed(e);
                }
            });
            toolBar1.add(btnFinishProcesses);

            //---- btnCancelAssignment ----
            btnCancelAssignment.setText("Absagen");
            btnCancelAssignment.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    btnCancelAssignmentPressed(e);
                }
            });
            toolBar1.add(btnCancelAssignment);
        }
        contentPane.add(toolBar1, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //======== tdbMain ========
        {
            tdbMain.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    tdbMainStateChanged(e);
                }
            });

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
            tdbMain.addTab("Positionen", pnlPositions);

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
            tdbMain.addTab("Helfer", pnlHelper);

            //======== pnlManualAssignments ========
            {
                pnlManualAssignments.setLayout(new GridBagLayout());
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWidths = new int[] {0, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                ((GridBagLayout)pnlManualAssignments.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
                ((GridBagLayout)pnlManualAssignments.getLayout()).rowWeights = new double[] {1.0, 1.0, 0.0, 1.0E-4};

                //======== borderManualAssignments ========
                {
                    borderManualAssignments.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderManualAssignments.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)borderManualAssignments.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)borderManualAssignments.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)borderManualAssignments.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scManualAssignments ========
                    {
                        scManualAssignments.setViewportView(tbManualAssignments);
                    }
                    borderManualAssignments.add(scManualAssignments, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                pnlManualAssignments.add(borderManualAssignments, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //======== borderAvailablePositions ========
                {
                    borderAvailablePositions.setLayout(new GridBagLayout());
                    ((GridBagLayout)borderAvailablePositions.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)borderAvailablePositions.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //======== scAvailablePositions ========
                    {
                        scAvailablePositions.setViewportView(tbAvailablePositions);
                    }
                    borderAvailablePositions.add(scAvailablePositions, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                pnlManualAssignments.add(borderAvailablePositions, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 5), 0, 0));

                //---- btnReloadAvailablePositions ----
                btnReloadAvailablePositions.setText("Aktualisieren");
                btnReloadAvailablePositions.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btnReloadAvailablePositionsPressed(e);
                    }
                });
                pnlManualAssignments.add(btnReloadAvailablePositions, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 5, 0), 0, 0));

                //---- btnBookManually ----
                btnBookManually.setText("Buchen");
                btnBookManually.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btnBookManuallyPressed(e);
                    }
                });
                pnlManualAssignments.add(btnBookManually, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            tdbMain.addTab("Manuelle Zuweisungen", pnlManualAssignments);
        }
        contentPane.add(tdbMain, new GridBagConstraints(0, 1, 3, 2, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JToolBar toolBar1;
    private JButton btnFinishProcesses;
    private JButton btnCancelAssignment;
    private JTabbedPane tdbMain;
    private JPanel pnlPositions;
    private JScrollPane scPositions;
    private TreeTable treeTablePositions;
    private JCheckBox chkUnassignedOnly;
    private JScrollPane scEvents;
    private ResourcePlanningTable tbEvents;
    private JButton btnPlanEvent;
    private JPanel pnlHelper;
    private JScrollPane scHelpers;
    private ResourcePlanningTable tbHelpers;
    private JPanel pnlManualAssignments;
    private JPanel borderManualAssignments;
    private JScrollPane scManualAssignments;
    private ResourcePlanningTable tbManualAssignments;
    private JPanel borderAvailablePositions;
    private JScrollPane scAvailablePositions;
    private ResourcePlanningTable tbAvailablePositions;
    private JButton btnReloadAvailablePositions;
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
