package com.productivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;

import com.productivity.Custom.AddCustomCheckList;
import com.productivity.Custom.CustomCheckList;
import com.productivity.Panels.DailyChecklist;
import com.productivity.Panels.HomePanel;
import com.productivity.Panels.SettingsPanel;
import com.productivity.Panels.TimerPanel;
import com.productivity.Util.ComponentMover;
import com.productivity.Util.CustomTabbedUI;

import net.miginfocom.swing.MigLayout;

import com.formdev.flatlaf.FlatDarculaLaf;

public class Productivity extends JFrame {
	
	public static final int kWidth = 400; // 400
	public static final int kHeight = 300; // 300
	public static final int kTabHeight = 30;
	public static final Boolean kMigDebug = true;
	private static final Boolean kDebug = true;
	private static final String kDebugPath = "src\\main\\java\\com\\productivity\\";
	private static final String kJarPath = "classes\\com\\productivity\\";
	private static final String kExePath = "target\\classes\\com\\productivity\\";
	private static final String kCustomDebugPath = "src\\main\\java\\com\\productivity\\Custom\\Saves\\";
	private static final String kCustomJarPath = "classes\\com\\productivity\\Custom\\Saves\\";
	private static final String kCustomExePath = "target\\classes\\com\\productivity\\Custom\\Saves\\";
	
	private static final JTabbedPane mTabbedPane = new JTabbedPane();
	private static final JLayeredPane mLayeredPane = new JLayeredPane();
	private static CustomCheckList mCustomCheckList;
	
	private static String mCurrentPath;
	private static String mCurrentCustomPath;
	private static File mNameFile;
	private static File mStateFile;
	private static File mColorFile;
	private static CheckBoxes mCheckBoxes;

	private static JLabel[] mConfetti = new JLabel[2];
	private static JLabel mCurrentConfetti;
	private static final double kConfettiTime = 1;

	private static Productivity mInstance = null;
    public synchronized static Productivity getInstance() {
        if (mInstance == null) {
            mInstance = new Productivity();
        }
        return mInstance;
    }
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mCurrentPath = (kDebug)?kDebugPath: (args.length>0)?kExePath:kJarPath;
				mCurrentCustomPath = (kDebug)?kCustomDebugPath: (args.length>0)?kCustomExePath:kCustomJarPath;
				Productivity.getInstance().createAndShowGUI();
			}
		});
	}
	
	private void createAndShowGUI() {
		UIManager.put("TabbedPane.selected", Color.BLACK);
		mConfetti[0] = new JLabel(new ImageIcon(getClass().getResource("Confetti/high.gif")));
		mConfetti[0].setBounds(0, 0, kWidth, kHeight);
		mConfetti[1] = new JLabel(new ImageIcon(getClass().getResource("Confetti/low.gif")));
		mConfetti[1].setBounds(0, 0, kWidth, kHeight);
		mTabbedPane.setBounds(0, 0, kWidth, kHeight); // kWidth-15, kHeight-30
		mNameFile = new File(mCurrentPath+"Saves\\list.TXT");
		mStateFile = new File(mCurrentPath+"Saves\\listCheck.TXT");
		mColorFile = new File(mCurrentPath+"Saves\\listColor.TXT");
		start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				BlockSites.unBlockSites();
			}
		}, 
		"Shutdown-thread"
		));
	}
	
	private void start() {
		try {
			try {
				UIManager.setLookAndFeel(new FlatDarculaLaf());
				//setIconImage(Toolkit.getDefaultToolkit().getImage("C:/Users/aidan/OneDrive/Documents/Programms/Productivity/src/main/java/com/productivity/icon.png"));
			}
			catch (Exception ex) {
				System.out.print("Failed to initialize theme. Using fallback.");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			SwingUtilities.updateComponentTreeUI(mTabbedPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SettingsPanel.loadSettings();
		setConfetti(Integer.parseInt(SettingsPanel.getSetting("currentConfetti")));
		mCustomCheckList = CustomCheckList.getInstance();
		mCheckBoxes = new CheckBoxes(mNameFile, mStateFile, mColorFile, false, true);
		mTabbedPane.setUI(new CustomTabbedUI(new Color(64, 60, 68)));
		mTabbedPane.setFocusable(false);
		mTabbedPane.addTab("Checklist", mCheckBoxes);
		mTabbedPane.addTab("Daily", new DailyChecklist());
		mTabbedPane.addTab("Timers", new TimerPanel());
		if (AddCustomCheckList.getNumberOfChecklists() > 0) {
			mTabbedPane.addTab("Custom", mCustomCheckList);
		}
		mTabbedPane.addTab("Settings", new SettingsPanel());
		mTabbedPane.insertTab("Home", null, HomePanel.getInstance(), null, 0);

		JButton close = new JButton("X");
		JButton minimize = new JButton("-");

		close.addActionListener(e -> close());
		minimize.addActionListener(e -> setState(Frame.ICONIFIED));
		JPanel taskBar = new JPanel(new MigLayout((kMigDebug?"debug, ":"")+"gap 0px 0px, ins 0"));
		taskBar.add(close); taskBar.add(minimize);
		int index = mTabbedPane.getTabCount();
		mTabbedPane.insertTab("", null, null, "", index);
        mTabbedPane.setTabComponentAt(index, taskBar);

		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		String xLast = "xPos";
		String yLast = "yPos";
		int xPos = prefs.getInt(xLast, 0);
		int yPos = prefs.getInt(yLast, 0);

		super.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				prefs.putInt(xLast, Productivity.super.getX());
				prefs.putInt(yLast, Productivity.super.getY());
			}
		});
		
		ImageIcon img = new ImageIcon(getClass().getResource("icon.png"));
		mLayeredPane.add(mTabbedPane, 0);

		ComponentMover cm = new ComponentMover(this, mTabbedPane);
		cm.setChangeCursor(true);

		super.setTitle("Productivity");
		super.setIconImage(img.getImage());
		super.add(mLayeredPane);
		super.setAlwaysOnTop(Boolean.parseBoolean(SettingsPanel.getSetting("onTop")));
		super.setLocationByPlatform(true);
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.setSize(kWidth, kHeight);
		super.setLocation(xPos, yPos);
		super.setResizable(false);
		super.setUndecorated(true);
		super.setVisible(true);
	}

	private void close() {
		String ObjButtons[] = {"Yes","No"};
		int PromptResult = JOptionPane.showOptionDialog(Productivity.this, 
			"Are you sure you want to exit?", "Close", 
			JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
			ObjButtons,ObjButtons[1]);
		if (PromptResult == 0) {
			System.exit(0);          
		}
	}
	
	private void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void customCheckListVisibility(boolean value) {
		if (value && mTabbedPane.indexOfComponent(mCustomCheckList) == -1) {
			mTabbedPane.insertTab("Custom", null, mCustomCheckList, null, mTabbedPane.getTabCount()-2);
			repaintFrame();
		}
		else if (mTabbedPane.indexOfComponent(mCustomCheckList) != -1) {
			mTabbedPane.remove(mCustomCheckList);
			repaintFrame();
		}
	}
	
	public void runOnStartup(Boolean value) {
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
	
	public void setOnTop(boolean top) {
		super.setAlwaysOnTop(top);
	}
	
	public void repaintFrame() {
		super.repaint();
	}
	
	public String getCurrentPath() {
		return mCurrentPath;
	}
	
	public String getCurrentCustomPath() {
		return mCurrentCustomPath;
	}
	
	public JCheckBox[] getBoxes() {
		if (mCheckBoxes == null) return null;
		return mCheckBoxes.getBoxes();
	}
	
	public void setSelected(boolean state, int index) {
		mCheckBoxes.setSelected(state, index);
	}

	public void setConfetti(int index) {
		mCurrentConfetti = mConfetti[index];
	}

	static Timer time;
	static TimerTask task;
	static final int kScale = 10;
	public static void showConfetti() {
		if (!Boolean.parseBoolean(SettingsPanel.getSetting("wantConfetti"))) return;
		if (time != null) {
			time.cancel();
			time.purge();
		}
		if (task != null) task.cancel();
		FadeLabel label = new FadeLabel();
		label.setBounds(0, 0, kWidth, kHeight);
		label.add(mCurrentConfetti);
		time = new Timer();
        task = new TimerTask()
        {
            int i = 0;
            @Override
            public void run()
            {
				if (i == 0) {
					mLayeredPane.add(label, 0);
				}
				if (i >= kConfettiTime*kScale) {
					mLayeredPane.remove(label);
					Productivity.getInstance().repaintFrame();
					task.cancel();
					time.cancel();
					time.purge();
				}
				if (i <= kConfettiTime*kScale/2)
					label.setAlpha((float)(i/(kConfettiTime*kScale)));
				else
					label.setAlpha((float)(kConfettiTime-i/(kConfettiTime*kScale)));
				i++;
            }
        };
        time.schedule(task, 0, 1000/kScale);
	}

	public static class FadeLabel extends JLabel {

        private float alpha;
        private BufferedImage background;

        public FadeLabel() {
            setAlpha(1f);
        }

        public void setAlpha(float value) {
            if (alpha != value) {
                float old = alpha;
                alpha = value;
                firePropertyChange("alpha", old, alpha);
                repaint();
            }
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        public Dimension getPreferredSize() {
            return background == null ? super.getPreferredSize() : new Dimension(background.getWidth(), background.getHeight());
        }

        @Override
        public void paint(Graphics g) {
            // This is one of the few times I would directly override paint
            // This makes sure that the entire paint chain is now using
            // the alpha composite, including borders and child components
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
            super.paint(g2d);
            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // This is one of the few times that doing this before the super call
            // will work...
            if (background != null) {
                int x = (getWidth() - background.getWidth()) / 2;
                int y = (getHeight() - background.getHeight()) / 2;
                g.drawImage(background, x, y, this);
            }
            super.paintComponent(g);
        }
    }
}