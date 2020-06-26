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

    JFrame frame;
    JPanel mainPanel;
    JTree plistTree;
    DefaultTreeCellRenderer cellRenderer;
    JTextField textField = new JTextField();
    TreeCellEditor editor = new DefaultCellEditor(textField);
    NSDictionary rootDict;
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        PList_Editor main_menu = new PList_Editor();
        main_menu.go();
    }

    private void go() {
        frame = new JFrame("PList Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        mainPanel.setSize(500, 500);

        editor.addCellEditorListener(new cellEditListener());

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openFile = new JMenuItem("Open");
        openFile.addActionListener(new openFileListener());

        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.addActionListener(new saveFileListener());
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new exitListener());

        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exit);

        menuBar.add(fileMenu);

        frame.getContentPane().add(mainPanel);

        frame.setJMenuBar(menuBar);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    private class openFileListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            setupPlist(fileOpen.getSelectedFile());
        }
    }

    private class saveFileListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileSave = new JFileChooser();
            fileSave.setAcceptAllFileFilterUsed(false);
            fileSave.setFileFilter(new FileNameExtensionFilter("Property List", "plist"));
            fileSave.showSaveDialog(frame);
            if(fileSave.getSelectedFile() != null) {
                savePlist(fileSave.getSelectedFile());
            }
        }
    }

    private class exitListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }

    void setupPlist(File f) {
        try {
            rootDict = (NSDictionary) PropertyListParser.parse(f);
            for (String key : rootDict.allKeys()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
                root.add(node);
                if (rootDict.objectForKey(key) instanceof NSDictionary) {
                    addPlistNode(node,rootDict.objectForKey(key));
                }
            }
            plistTree = new JTree(root);
            plistTree.setEditable(true);
            plistTree.setCellEditor(editor);
            cellRenderer = (DefaultTreeCellRenderer) plistTree.getCellRenderer();
            cellRenderer.setLeafIcon(null);
            cellRenderer.setClosedIcon(null);
            cellRenderer.setOpenIcon(null);
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

    private void savePlist (File f) {
        try {
            PropertyListParser.saveAsXML(rootDict,f);
        }
        catch (IOException e) {
            e.printStackTrace();
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
