/*
 * Created by JFormDesigner on Sun Oct 25 22:35:29 CET 2015
 */

package de.trispeedys.resourceplanning.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import de.trispeedys.resourceplanning.ResourcePlanningClientRoutines;
import de.trispeedys.resourceplanning.components.treetable.*;

/**
 * @author Stefan Schulz
 */
public class ResourcePlanningMainFrame extends JFrame {
    public ResourcePlanningMainFrame() {
        initComponents();
    }

    private void fillPressed(ActionEvent e) {
        treeTable1.setModel(new TreeTableDataModel(ResourcePlanningClientRoutines.createDataStructure()));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Stefan Schulz
        scrollPane1 = new JScrollPane();
        treeTable1 = new TreeTable();
        button1 = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {173, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(treeTable1);
        }
        contentPane.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- button1 ----
        button1.setText("Fill");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillPressed(e);
            }
        });
        contentPane.add(button1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Stefan Schulz
    private JScrollPane scrollPane1;
    private TreeTable treeTable1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    
    // ---
    
    public static void main(String[] args)
    {
        new ResourcePlanningMainFrame().setVisible(true);
    }
}
