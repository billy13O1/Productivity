package com.productivity.Custom;

import java.awt.BorderLayout;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;
import com.productivity.checkBoxes;
import com.productivity.gui;

public class addCustomCheckList extends JPanel {
    static ArrayList<String> names = new ArrayList<String>();
    public static String customPath = "src\\main\\java\\com\\productivity\\Custom\\Saves\\";
    public static File customNames = new File(customPath + "customNames.TXT");
    static Box vertical = Box.createVerticalBox();
    
    public addCustomCheckList() {
        JTextField name = new JTextField();
        name.addActionListener(e -> {
            if (!names.contains(name.getText()) && !name.getText().equals("")) {
                addCheckList(name.getText());
                if (getNumberOfChecklists() == 1) {
                    gui.addCustomCheckList();
                    gui.repaintFrame();
                }
                name.setText("");
            }
        });
        JLabel nameLbl = new JLabel("Name Of Custom Checklist:");
        
        Box nameBox = Box.createVerticalBox();
        nameBox.add(nameLbl);
        nameBox.add(name);
        super.setLayout(new BorderLayout());
        super.add(BorderLayout.NORTH, nameBox);
        super.add(BorderLayout.CENTER, vertical);
    }
    
    public static int getNumberOfChecklists() {
        return readData(customNames).length;
    }
    
    public static void loadCheckLists() {
        String[] data = readData(customNames);
        names = new ArrayList<String>(Arrays.asList(data));
        for (int i = 0; i < names.size(); i++) {
            addCheckList(names.get(i));
        }
    }
    
    public static void addCheckList(String n) {
        File name = new File(customPath + n + "Name.TXT");
        File color = new File(customPath + n + "Color.TXT");
        File check = new File(customPath + n + "Check.TXT");
        if (!name.exists() && !color.exists() && !check.exists()) {        
            try {
                name.createNewFile();
                color.createNewFile();
                check.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!names.contains(n)) {
            names.add(n);
        }
        saveChecklists();
        JButton button = new JButton(n);
        button.addActionListener(e -> {
            vertical.remove(button);
            gui.repaintFrame();
            deleteChecklist(n);
        });
        vertical.add(button);
        gui.customCheckList.addCheckList(new checkBoxes(gui.height, gui.length, name, check, color, false), n);
    }
    
    public static void deleteChecklist(String n) {
        File name = new File(customPath + n + "Name.TXT");
        File color = new File(customPath + n + "Color.TXT");
        File check = new File(customPath + n + "Check.TXT");
        name.delete();
        color.delete();
        check.delete();
        names.remove(n);
        gui.customCheckList.removeChecklist(n);
        if (names.size() <= 0) {
            gui.removeCustomCheckList();
        }
    }
    
    public static void saveChecklists() {
        String[] data = new String[names.size()];
        data = names.toArray(data);
        writeData(data, customNames);
    }
    
    public static String[] readData(File file) {
        String[] result = new String[0];
        try {
            result = new String[(int)Files.lines(file.toPath()).count()];
            Scanner scanner = new Scanner(file);
            int index = 0;
            while (scanner.hasNextLine()) {
                result[index] = scanner.nextLine();
                index++;
            }
            scanner.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
}