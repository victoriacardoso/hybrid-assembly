package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import cisa.Cisa;
import database.DatabaseConnection;
import log.Log;
import log.Status;
import mauve.Ordering;
import megahit.PairedRead;
import rast.Rast;
import spades.SingleRead;

public class ProjectLog {
	private static JPanel panel;
	private static JTable table;
	private static JScrollPane scrollPane;
	private static String status;
	private static DefaultTableModel dft;
	private static JFrame frame;
	private static String id;
	private static JDialog dialog;
	private static JLabel emptyField;
	private static String[] yesNoOptions = { "Yes", "No" };

	public String getId() {
		return id;
	}

	public void setId(String id) {
		ProjectLog.id = id;
	}

	public JFrame getFrame() {
		return frame;
	}

	public static String getStatus() {
		return status;
	}

	public static void setStatus(String status) {
		ProjectLog.status = status;
	}

	public void openScreen() {
		frame = new JFrame();
		frame.setBounds(100, 100, 654, 408);
		frame.setVisible(true);
		frame.setTitle("Project Log");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (JOptionPane.showOptionDialog(null, "Do you want to exit the program?", "Attention",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, yesNoOptions,
						null) == JOptionPane.YES_OPTION) {
					System.exit(0);
				} else {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				}

			};
		});

		panel = new JPanel();
		panel.setBounds(130, 167, 160, 158);
		panel.setLayout(null);
		frame.add(panel);

		JPanel panel1 = new JPanel();
		panel1.setBounds(65, 72, 461, 151);
		panel1.setLayout(null);
		panel.add(panel1);

		JLabel lblMegahitParameters = new JLabel("Project Log");
		lblMegahitParameters.setBounds(12, 12, 230, 24);
		lblMegahitParameters.setFont(new Font("Dialog", Font.BOLD, 16));
		panel.add(lblMegahitParameters);

		table = new JTable();
		table.setCellSelectionEnabled(true);
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setShowGrid(false);
		table.setBackground(Color.WHITE);
		table.setEnabled(false);

		dft = new DefaultTableModel();
		dft = (DefaultTableModel) table.getModel();
		dft.addColumn("Status");
		dft.addColumn("Started Date");
		dft.addColumn("Finished Date");

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(new Rectangle(0, 12, 452, 127));
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setEnabled(false);
		panel1.add(scrollPane);
		scrollPane.setViewportView(table);

		dialog = new JDialog(frame, "Complete Process");
		dialog.setBounds(450, 300, 537, 171);
		dialog.setResizable(false);
		dialog.getContentPane().setLayout(null);

		emptyField = new JLabel("");
		emptyField.setHorizontalAlignment(SwingConstants.CENTER);
		emptyField.setBounds(0, 36, 527, 25);
		dialog.getContentPane().add(emptyField);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();

				try {
					Home home = new Home();
					home.getFrm_Home().setVisible(true);
					frame.dispose();

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}

			}
		});
		btnOk.setBounds(224, 100, 81, 19);
		dialog.getContentPane().add(btnOk);

		JLabel lblYouCanCheck = new JLabel("All done. You can check the results in:");
		lblYouCanCheck.setBounds(124, 26, 296, 15);
		dialog.getContentPane().add(lblYouCanCheck);

		JLabel lblThankYouFor = new JLabel("Thank you for using GenTreat!");
		lblThankYouFor.setBounds(153, 73, 247, 15);
		dialog.getContentPane().add(lblThankYouFor);

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Input input = new Input();
				input.getFrame().setVisible(true);
				getFrame().dispose();
			}
		});
		btnBack.setBounds(22, 304, 78, 31);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnProject = new JMenu("Project");
		menuBar.add(mnProject);

		JMenuItem mntmCreate = new JMenuItem("Create");
		mntmCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Home home = new Home();
					home.getDialog().setVisible(true);
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
			}
		});
		mnProject.add(mntmCreate);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Home home = new Home();
					home.getDialog2().setVisible(true);
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
			}
		});
		mnHelp.add(mntmAbout);

	}

	public static Runnable t1 = new Runnable() {

		@Override
		public void run() {
			try {

				if (id != null) {
					Statement statement;
					String cmmd;
					ResultSet resulSet;
					cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					resulSet = statement.executeQuery(cmmd);

					while (resulSet.next()) {
						String output = resulSet.getString("output").trim().replace(" ", "");

						File directory = new File(output);
						directory.mkdir();
						runTools(Integer.parseInt(id));
					}
				} else {
					int idproject = 0;

					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					Statement statement;
					String cmmd;
					ResultSet resulSet;
					cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
					statement = DatabaseConnection.connect.createStatement();
					resulSet = statement.executeQuery(cmmd);

					while (resulSet.next()) {
						String output = resulSet.getString("output").trim().replace(" ", "");
						File directory = new File(output);
						directory.mkdir();
					}
					runTools(idproject);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static Runnable t2 = new Runnable() {

		@Override
		public void run() {
			try {
				if (id != null) {
					startLogProject(Integer.parseInt(id), 0, 1, 2, 3, 4);
				} else {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					startLogProject(idproject, 0, 1, 2, 3, 4);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	public static Runnable t3 = new Runnable() {

		@Override
		public void run() {
			try {
				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);
								
				Statement stm = null;
				String cmmdo;
				ResultSet rst;
				cmmdo = "SELECT * FROM organism WHERE idproject =" + id + ";";
				stm = DatabaseConnection.connect.createStatement();
				rst = stm.executeQuery(cmmdo);
				while (rst.next()) {
					String singleRead = rst.getString("single");

					if (singleRead != null) {
						customPanel.addProgress(20);
						SingleRead single = new SingleRead();
						single.runSpades(Integer.parseInt(id));
						customPanel.addProgress(20);

						Thread.sleep(10000);

					} else {
						customPanel.addProgress(20);
						spades.PairedRead pairedRead = new spades.PairedRead();
						pairedRead.runSpades(Integer.parseInt(id));
						customPanel.addProgress(20);

						Thread.sleep(10000);
					}
				}
				
				Cisa cisa = new Cisa();
				cisa.mergeFileRun(Integer.parseInt(id));
				cisa.cisaFileRun(Integer.parseInt(id));
				customPanel.addProgress(20);

				Thread.sleep(20000);

				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				customPanel.addProgress(20);


				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));
				customPanel.addProgress(20);

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t4 = new Runnable() {

		@Override
		public void run() {
			try {
				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);
				
				customPanel.addProgress(40);
				
				Cisa cisa = new Cisa();
				cisa.mergeFileRun(Integer.parseInt(id));
				cisa.cisaFileRun(Integer.parseInt(id));
				customPanel.addProgress(20);

				Thread.sleep(20000);

				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				customPanel.addProgress(20);

				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));
				customPanel.addProgress(20);

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t5 = new Runnable() {

		@Override
		public void run() {
			try {
				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);
				
				customPanel.addProgress(60);
				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				customPanel.addProgress(20);

				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));
				customPanel.addProgress(20);

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t7 = new Runnable() {

		@Override
		public void run() {
			Status status = new Status();
			
			try {
				if (status.checkStatus(Integer.parseInt(id)).equals("Running SPAdes") || status.checkStatus(Integer.parseInt(id)).equals("Complete Megahit")) {
					startLogProject(Integer.parseInt(id), 0, 0, 1, 2, 3);
				}
				if (status.checkStatus(Integer.parseInt(id)).equals("Running CISA") || status.checkStatus(Integer.parseInt(id)).equals("Complete SPAdes")) {
					startLogProject(Integer.parseInt(id), 0, 0, 0, 1, 2);
				}
				if (status.checkStatus(Integer.parseInt(id)).equals("Running Mauve") || status.checkStatus(Integer.parseInt(id)).equals("Complete CISA")) {
					startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 1);
				}
				if (status.checkStatus(Integer.parseInt(id)).equals("Running RAST") || status.checkStatus(Integer.parseInt(id)).equals("Complete Mauve")) {
					startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 0);
				}
			} catch (NumberFormatException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
	public static Runnable t8 = new Runnable() {

		@Override
		public void run() {
			try {
				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);

				Rast rast = new Rast();
				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					String job_id = resulSet.getString("job_id");

					if (job_id != null) {
						customPanel.addProgress(80);
						rast.statusRast(Integer.parseInt(id));
						customPanel.addProgress(20);
					} else {
						customPanel.addProgress(80);
						rast.submitRast(Integer.parseInt(id));
						customPanel.addProgress(20);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static void startLogProject(int idproject, int rowMegahit, int rowSpades, int rowCisa, int rowMauve,
			int rowRast) {
		try {
			Status status = new Status();
			String checkStatus = status.checkStatus(idproject);
			
			if (checkStatus.equals("Running Megahit")) {
				Log log = new Log();
				log.runMegahitLog(dft);

				while (status.checkStatus(idproject).equals("Running Megahit")) {
					Thread.sleep(30000);
				}
				Date date0 = new Date();
				DateFormat dateFormat1 = DateFormat.getInstance();
				String finishedDate = dateFormat1.format(date0);
				dft.setValueAt("Complete Megahit", rowMegahit, 0);
				dft.setValueAt(finishedDate, rowMegahit, 2);
				Thread.sleep(30000);
			}

			if (status.checkStatus(idproject).equals("Running SPAdes")) {
				Log log = new Log();
				log.runSpadesLog(dft);

				while (status.checkStatus(idproject).equals("Running SPAdes")) {
					Thread.sleep(30000);
				}
				Date date00 = new Date();
				DateFormat dateFormat00 = DateFormat.getInstance();
				String finishedDateSpades = dateFormat00.format(date00);
				dft.setValueAt("Complete Spades", rowSpades, 0);
				dft.setValueAt(finishedDateSpades, rowSpades, 2);
				Thread.sleep(30000);
			}
			if (status.checkStatus(idproject).equals("Running CISA")) {
				Log log = new Log();
				log.runCisaLog(dft);

				while (status.checkStatus(idproject).equals("Running CISA")) {
					Thread.sleep(30000);

				}

				Date date000 = new Date();
				DateFormat df = DateFormat.getInstance();
				String finishedDateCisa = df.format(date000);
				dft.setValueAt("Complete CISA", rowCisa, 0);
				dft.setValueAt(finishedDateCisa, rowCisa, 2);
				Thread.sleep(30000);
			}

			if (status.checkStatus(idproject).equals("Running Mauve")) {
				Log log = new Log();
				log.runMauveLog(dft);

				while (status.checkStatus(idproject).equals("Running Mauve")) {
					Thread.sleep(30000);
				}

				Date date0000 = new Date();
				DateFormat df0 = DateFormat.getInstance();
				String finishedDateRagoo = df0.format(date0000);
				dft.setValueAt("Complete Mauve", rowMauve, 0);
				dft.setValueAt(finishedDateRagoo, rowMauve, 2);
				Thread.sleep(30000);
			}

			if (status.checkStatus(idproject).equals("Running RAST")) {
				Log log = new Log();
				log.runRastLog(dft);

				while (status.checkStatus(idproject).equals("Running RAST")) {
					Thread.sleep(30000);
				}
				Date date00000 = new Date();
				DateFormat df00 = DateFormat.getInstance();
				String finishedDateRast = df00.format(date00000);
				dft.setValueAt("Complete RAST", rowRast, 0);
				dft.setValueAt(finishedDateRast, rowRast, 2);
			}

			Statement statement = null;
			String command;
			ResultSet resultSet;
			command = "SELECT * FROM parameter WHERE idproject =" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resultSet = statement.executeQuery(command);
			String output = null;

			while (resultSet.next()) {
				output = resultSet.getString("output");
			}
			emptyField.setText(output);
			dialog.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runTools(int idproject) {
		try {

			Statement stm = null;
			String cmmdo;
			ResultSet rst;
			cmmdo = "SELECT * FROM organism WHERE idproject=" + idproject + ";";
			stm = DatabaseConnection.connect.createStatement();
			rst = stm.executeQuery(cmmdo);
			
			CustomPanel customPanel = new CustomPanel();
			panel.add(customPanel);
			customPanel.setBounds(236, 229, 125, 96);

			while (rst.next()) {
				String singleRead = rst.getString("single");

				if ((singleRead != null)) {
					megahit.SingleRead megahitSingle = new megahit.SingleRead();
					megahitSingle.runMegahit(idproject);
					customPanel.addProgress(20);
					
					Thread.sleep(20000);

					SingleRead single = new SingleRead();
					single.runSpades(idproject);
					customPanel.addProgress(20);

					Thread.sleep(10000);

				} else {
					PairedRead paired = new PairedRead();
					paired.runMegahit(idproject);
					customPanel.addProgress(20);

					Thread.sleep(20000);

					spades.PairedRead pairedspades = new spades.PairedRead();
					pairedspades.runSpades(idproject);
					customPanel.addProgress(20);

					Thread.sleep(10000);
				}
			}
			Cisa cisa = new Cisa();
			cisa.mergeFileRun(idproject);
			cisa.cisaFileRun(idproject);
			customPanel.addProgress(20);

			Thread.sleep(20000);

			Ordering ordering = new Ordering();
			ordering.OrderContigs(id);
			customPanel.addProgress(20);

			Thread.sleep(20000);

			Rast rast = new Rast();
			rast.submitRast(idproject);
			customPanel.addProgress(20);

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
