import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;

public class FolderExplorer extends JFrame {

    private JTree tree;
    private DefaultTreeModel treeModel;
    private JTable fileInfoTable;
    private DefaultTableModel tableModel;

    private final String[] colHeads = {"File Name", "Size (in Bytes)", "Read Only", "Hidden"};

    public FolderExplorer() {
        setTitle("Folder Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Create the root node
        DefaultMutableTreeNode root = createTreeNode(new File(System.getProperty("user.home")));
        treeModel = new DefaultTreeModel(root);

        // Create the tree
        tree = new JTree(treeModel);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                displayFileInfo(e.getPath());
            }
        });

        // Create the scroll pane for the tree
        JScrollPane treeScrollPane = new JScrollPane(tree);

        // Create the table for file info
        tableModel = new DefaultTableModel(new String[][]{}, colHeads);
        fileInfoTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(fileInfoTable);

        // Add components to the frame
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(250);
        add(splitPane);

        setVisible(true);
    }

    private DefaultMutableTreeNode createTreeNode(File file) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    node.add(createTreeNode(child));
                }
            }
        }
        return node;
    }

    private void displayFileInfo(TreePath path) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        File file = (File) node.getUserObject();

        if (file.exists()) {
            tableModel.setRowCount(0);  // Clear previous data
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        addFileInfo(f);
                    }
                }
            } else {
                addFileInfo(file);
            }
        } else {
            tableModel.setRowCount(0);  // Clear previous data
            tableModel.addRow(new String[]{"File not found.", "", "", ""});
        }
    }

    private void addFileInfo(File file) {
        String fileName = file.getName();
        String fileSize = String.valueOf(file.length());
        String readOnly = String.valueOf(!file.canWrite());
        String hidden = String.valueOf(file.isHidden());
        tableModel.addRow(new String[]{fileName, fileSize, readOnly, hidden});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FolderExplorer());
    }
}