package com.productivity;

import java.io.*;
import javax.swing.*;
import com.productivity.Custom.addCustomCheckList;
import com.productivity.Custom.customCheckList;

public class gui extends JFrame {
	
	public static int length = 400;
	public static int height = 300;
	public static Boolean debug = true;
	public static String debugPath = "src\\main\\java\\com\\productivity\\";
	public static String jarPath = "classes\\com\\productivity\\";
	public static String exePath = "target\\classes\\com\\productivity\\";
	public static String customDebugPath = "src\\main\\java\\com\\productivity\\Custom\\Saves\\";
	public static String customJarPath = "classes\\com\\productivity\\Custom\\Saves\\";
	public static String customExePath = "target\\classes\\com\\productivity\\Custom\\Saves\\";
	public static String currentPath;
	public static String currentCustomPath;
	
	private static boolean onTop = false;
	private static JFrame frame = new JFrame("Produtivity");
	private static JTabbedPane tabbedPane;
	public static customCheckList customCheckList = new customCheckList();
	private static CheckBoxes checkBoxPanel;
	
	private static File nameFile;
	private static File stateFile;
	private static File colorFile;
	//private static File nameFile = new File((!debug)?"classes\\list.TXT":debugPath+"list.TXT");
	//private static File stateFile = new File((!debug)?"classes\\listCheck.TXT":debugPath+"listCheck.TXT");
	//private static File colorFile = new File((!debug)?"classes\\listColor.TXT":debugPath+"listColor.TXT");
	
	static String[] lookAndFeel = {"Motif", "Metal"};
	static String[] lookAndFeelValues = {"com.sun.java.swing.plaf.motif.MotifLookAndFeel", "javax.swing.plaf.metal.MetalLookAndFeel"};
	
	public static void main(String[] args) throws IOException {
		currentPath = (debug)?debugPath: (args.length>0)?exePath:jarPath;
		currentCustomPath = (debug)?customDebugPath: (args.length>0)?customExePath:customJarPath;
		loadFiles();
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				BlockSites.unBlockSites();
			}
		}, "Shutdown-thread"));
	}
	
	private static void loadFiles() {
		nameFile = new File(currentPath+"Saves\\list.TXT");
		stateFile = new File(currentPath+"Saves\\listCheck.TXT");
		colorFile = new File(currentPath+"Saves\\listColor.TXT");
	}
	
	private static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			//SwingUtilities.updateComponentTreeUI(frame);
			//frame.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
		load();
		checkBoxPanel = new CheckBoxes(height, length, nameFile, stateFile, colorFile, false);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Home", new HomePanel());
		tabbedPane.addTab("Checklist", checkBoxPanel);
		tabbedPane.addTab("Daily", new DailyChecklist());
		tabbedPane.addTab("Timers", new TimerPanel());
		tabbedPane.addTab("Notes", new NotesPanel());
		tabbedPane.addTab("Settings", new SettingsPanel());
		if (addCustomCheckList.getNumberOfChecklists() > 0) {
			tabbedPane.addTab("Custom", customCheckList);
		}
		ImageIcon img = new ImageIcon("src\\main\\java\\com\\productivity\\icon.png");
		frame.setIconImage(img.getImage());
		frame.add(tabbedPane);
		frame.setAlwaysOnTop(onTop);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(length, height);
		frame.setVisible(true);
	}
	
	public static JCheckBox[] getCheckBoxes() {
		return checkBoxPanel.getBoxes();
	}

	private static void load() {
		SettingsPanel.loadSettings();
		addCustomCheckList.loadCheckLists();
		onTop = Boolean.parseBoolean(SettingsPanel.getSetting("onTop"));
	}
	
	public static void customCheckListVisibility(boolean value) {
		if (value && tabbedPane.indexOfComponent(customCheckList) == -1) {
			tabbedPane.addTab("Custom", customCheckList);
			repaintFrame();
		}
		else if (tabbedPane.indexOfComponent(customCheckList) != -1) {
			tabbedPane.remove(customCheckList);
			repaintFrame();
		}
	}
	
	public static void setOnTop(boolean top) {
		frame.setAlwaysOnTop(top);
	}
	
	public static void repaintFrame() {
		frame.repaint();
	}
	
	public static void runOnStartup(Boolean value) {
		String path = System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\productivity.bat";
		
		if (value) {
			File startupFile = new File(path);
			try {
				if (!startupFile.exists()) {
					startupFile.createNewFile();
				}
				String currentDir = System.getProperty("user.dir");
				String data = "start " + currentDir + "\\Productivity.exe";
				writeData(data, startupFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			File startupFile = new File(path);
			try {
				if (startupFile.exists()) {
					startupFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setLookAndFeel(int index) {
		try {
			UIManager.setLookAndFeel(lookAndFeelValues[index]);
			SwingUtilities.updateComponentTreeUI(frame);
			frame.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}