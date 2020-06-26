package com.m3kyr.plist;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import java.awt.event.*;
import com.dd.plist.*;
import java.io.*;

public class PList_Editor {
    private JFrame frame;
    private JPanel mainPanel;
    private JTree plistTree;
    private DefaultTreeCellRenderer cellRenderer;
    private JTextField textField = new JTextField();
    private TreeCellEditor editor = new DefaultCellEditor(textField);
    private NSDictionary rootDict;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        PList_Editor main_menu = new PList_Editor();
        main_menu.go();
    }

    private void go() {
        editor.addCellEditorListener(new cellEditListener());
        JMenuItem openMenuItem = setupOpenMenuItem();
        JMenuItem saveMenuItem = setupSaveMenuItem();
        JMenuItem exitMenuItem = setupExitMenuItem();
        JMenu fileMenu = setupFileMenu(openMenuItem, saveMenuItem, exitMenuItem);
        JMenuBar menuBar = setupMenuBar(fileMenu);
        setupMainPanel();
        setupFrame(menuBar);
    }

    private void setupFrame(JMenuBar menuBar) {
        frame = new JFrame("PList Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setJMenuBar(menuBar);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private void setupMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setSize(500, 500);
    }

    private JMenuBar setupMenuBar(JMenu fileMenu) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        return menuBar;
    }

    private JMenu setupFileMenu(JMenuItem openFile, JMenuItem saveFile, JMenuItem exit) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exit);
        return fileMenu;
    }

    private JMenuItem setupOpenMenuItem() {
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new openFileListener());
        return openMenuItem;
    }

    private class openFileListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            if (fileOpen.getSelectedFile() != null) {
                setupPlist(fileOpen.getSelectedFile());
            }
        }
    }

    private void setupPlist(File f) {
        try {
            rootDict = (NSDictionary) PropertyListParser.parse(f);
            for (String key : rootDict.allKeys()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
                root.add(node);
                if (rootDict.objectForKey(key) instanceof NSDictionary) {
                    addPlistNode(node,rootDict.objectForKey(key));
                }
            }
            setupPlistJTree();
            setupCellRenderer();
            frame.getContentPane().add(new JScrollPane(plistTree));
            frame.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addPlistNode(DefaultMutableTreeNode node, NSObject object) {
        if (object instanceof NSDictionary) {
            for (String key : ((NSDictionary) object).allKeys()) {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(key);
                node.add(n);
                addPlistNode(n, ((NSDictionary) object).objectForKey(key));
            }
        }
        else if (object instanceof NSArray) {
            for (NSObject o : ((NSArray) object).getArray()) {
                addPlistNode(node, o);
            }
        }
        else {
            node.add(new DefaultMutableTreeNode(object.toString()));
        }
    }

    private void setupPlistJTree() {
        plistTree = new JTree(root);
        plistTree.setEditable(true);
        plistTree.setCellEditor(editor);
    }

    private void setupCellRenderer() {
        cellRenderer = (DefaultTreeCellRenderer) plistTree.getCellRenderer();
        cellRenderer.setLeafIcon(null);
        cellRenderer.setClosedIcon(null);
        cellRenderer.setOpenIcon(null);
    }

    private JMenuItem setupSaveMenuItem() {
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new saveFileListener());
        return saveMenuItem;
    }

    private class saveFileListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileSave = new JFileChooser();
            fileSave.setAcceptAllFileFilterUsed(false);
            fileSave.setFileFilter(new FileNameExtensionFilter("Property List", "plist"));
            fileSave.showSaveDialog(frame);
            if (fileSave.getSelectedFile() != null) {
                savePlist(fileSave.getSelectedFile());
            }
        }
    }

    private void savePlist (File f) {
        try {
            PropertyListParser.saveAsXML(rootDict,f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JMenuItem setupExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new exitListener());
        return exitMenuItem;
    }

    private class exitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }

    private class cellEditListener implements CellEditorListener {
        public void editingCanceled(ChangeEvent e) {
            Object value = editor.getCellEditorValue();
        }

        public void editingStopped(ChangeEvent e) {
            Object value = editor.getCellEditorValue();
        }
    }

}
