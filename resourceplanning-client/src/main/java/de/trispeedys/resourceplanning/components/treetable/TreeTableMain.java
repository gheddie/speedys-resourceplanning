package de.trispeedys.resourceplanning.components.treetable;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TreeTableMain extends JFrame
{
    public TreeTableMain()
    {
        super("Tree Table Demo");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(0, 1));

        AbstractTreeTableModel treeTableModel = new TreeTableDataModel(createDataStructure());

        TreeTable myTreeTable = new TreeTable(treeTableModel);

        Container cPane = getContentPane();

        cPane.add(new JScrollPane(myTreeTable));

        setSize(1000, 800);
        setLocationRelativeTo(null);

    }

    private static TreeTableDataNode createDataStructure()
    {
        List<TreeTableDataNode> children1 = new ArrayList<TreeTableDataNode>();
        children1.add(new TreeTableDataNode("N12", "C12", new Date(), Integer.valueOf(50), null));
        children1.add(new TreeTableDataNode("N13", "C13", new Date(), Integer.valueOf(60), null));
        children1.add(new TreeTableDataNode("N14", "C14", new Date(), Integer.valueOf(70), null));
        children1.add(new TreeTableDataNode("N15", "C15", new Date(), Integer.valueOf(80), null));

        List<TreeTableDataNode> children2 = new ArrayList<TreeTableDataNode>();
        children2.add(new TreeTableDataNode("N12", "C12", new Date(), Integer.valueOf(10), null));
        children2.add(new TreeTableDataNode("N13", "C13", new Date(), Integer.valueOf(20), children1));
        children2.add(new TreeTableDataNode("N14", "C14", new Date(), Integer.valueOf(30), null));
        children2.add(new TreeTableDataNode("N15", "C15", new Date(), Integer.valueOf(40), null));

        List<TreeTableDataNode> rootNodes = new ArrayList<TreeTableDataNode>();
        rootNodes.add(new TreeTableDataNode("N1", "C1", new Date(), Integer.valueOf(10), children2));
        rootNodes.add(new TreeTableDataNode("N2", "C2", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N3", "C3", new Date(), Integer.valueOf(10), children2));
        rootNodes.add(new TreeTableDataNode("N4", "C4", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N5", "C5", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N6", "C6", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N7", "C7", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N8", "C8", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N9", "C9", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N10", "C10", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N11", "C11", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N12", "C7", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N13", "C8", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N14", "C9", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N15", "C10", new Date(), Integer.valueOf(10), children1));
        rootNodes.add(new TreeTableDataNode("N16", "C11", new Date(), Integer.valueOf(10), children1));
        TreeTableDataNode root = new TreeTableDataNode("R1", "R1", new Date(), Integer.valueOf(10), rootNodes);

        return root;
    }

    public static void main(final String[] args)
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
                new TreeTableMain().setVisible(true);
            }
        };
        SwingUtilities.invokeLater(gui);
    }
}