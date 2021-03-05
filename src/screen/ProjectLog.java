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

import database.DatabaseConnection;
import log.Log;
import log.Status;
import log.Tools;
import mauve.Ordering;
import rast.Rast;

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
						String output = resulSet.getString("output");

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
						String output = resulSet.getString("output");
						File directory = new File(output);
						directory.mkdir();
					}
					runTools(idproject);

				}

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());

			}
		}
	};

	public static Runnable t2 = new Runnable() {

		@Override
		public void run() {
			try {
				if (id != null) {
					String order = null;
					Statement statement;
					String cmmd;
					ResultSet resulSet;
					cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					resulSet = statement.executeQuery(cmmd);

					while (resulSet.next()) {
						order = resulSet.getString("ordination");
					}
					if (order.equals("1")) {
						startLogProject(Integer.parseInt(id), 0, 1, 2, 3, 4);
					} else {
						startLogProject(Integer.parseInt(id), 0, 1, 2, 0, 3);
					}
				} else {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					String order = null;
					Statement statement;
					String cmmd;
					ResultSet resulSet;
					cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
					statement = DatabaseConnection.connect.createStatement();
					resulSet = statement.executeQuery(cmmd);

					while (resulSet.next()) {
						order = resulSet.getString("ordination");

						if (order.equals("1")) {
							startLogProject(idproject, 0, 1, 2, 3, 4);
						} else {
							startLogProject(idproject, 0, 1, 2, 0, 3);
						}
					}
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
				String singleRead = null;
				String order = null;
				Tools tools = new Tools();

				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);

				Statement stm = null;
				String cmmdo;
				ResultSet rst;
				cmmdo = "SELECT * FROM organism WHERE idproject=" + id + ";";
				stm = DatabaseConnection.connect.createStatement();
				rst = stm.executeQuery(cmmdo);

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (rst.next()) {
					singleRead = rst.getString("single");
				}
				while (resulSet.next()) {
					order = resulSet.getString("ordination");
				}

				if (order.equals("1")) {
					if ((singleRead != null)) {
						customPanel.addProgress(20);
						tools.runSingleSPAdes(Integer.parseInt(id), customPanel, 20);

						Thread.sleep(10000);

					} else {
						customPanel.addProgress(20);
						tools.runPareadSPAdes(Integer.parseInt(id), customPanel, 20);

						Thread.sleep(10000);
					}
					tools.runCisa(Integer.parseInt(id), customPanel, 20);
					Thread.sleep(10000);
					tools.runMauve(Integer.parseInt(id), customPanel, 20);
					Thread.sleep(10000);
					tools.runRast(Integer.parseInt(id), customPanel, 20);
				} else {
					if ((singleRead != null)) {
						customPanel.addProgress(25);

						tools.runSingleSPAdes(Integer.parseInt(id), customPanel, 25);

						Thread.sleep(10000);

					} else {
						customPanel.addProgress(25);
						tools.runPareadSPAdes(Integer.parseInt(id), customPanel, 25);

						Thread.sleep(10000);
					}
					tools.runCisa(Integer.parseInt(id), customPanel, 25);
					Thread.sleep(10000);
					tools.runRast(Integer.parseInt(id), customPanel, 25);
				}
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

				String order = null;
				Tools tools = new Tools();

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					order = resulSet.getString("ordination");
				}

				if (order.equals("1")) {
					customPanel.addProgress(40);
					tools.runCisa(Integer.parseInt(id), customPanel, 20);

					Thread.sleep(5000);

					tools.runMauve(Integer.parseInt(id), customPanel, 20);

					Thread.sleep(10000);

					tools.runRast(Integer.parseInt(id), customPanel, 20);
				} else {
					customPanel.addProgress(50);
					tools.runCisa(Integer.parseInt(id), customPanel, 25);

					Thread.sleep(10000);

					tools.runRast(Integer.parseInt(id), customPanel, 25);
				}
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
				ordering.OrderContigs(Integer.parseInt(id));
				customPanel.addProgress(20);

				Thread.sleep(10000);

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
				String order = null;

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					order = resulSet.getString("ordination");

					if (order.equals("1")) {
						if (status.checkStatus(Integer.parseInt(id)).equals("Running SPAdes")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete Megahit")) {
							startLogProject(Integer.parseInt(id), 0, 0, 1, 2, 3);
						}
						if (status.checkStatus(Integer.parseInt(id)).equals("Running CISA")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete SPAdes")) {
							startLogProject(Integer.parseInt(id), 0, 0, 0, 1, 2);
						}
						if (status.checkStatus(Integer.parseInt(id)).equals("Running Mauve")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete CISA")) {
							startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 1);
						}
						if (status.checkStatus(Integer.parseInt(id)).equals("Running RAST")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete Mauve")) {
							startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 0);
						}
					} else {
						if (status.checkStatus(Integer.parseInt(id)).equals("Running SPAdes")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete Megahit")) {
							startLogProject(Integer.parseInt(id), 0, 0, 1, 0, 2);
						}
						if (status.checkStatus(Integer.parseInt(id)).equals("Running CISA")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete SPAdes")) {
							startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 1);
						}
						if (status.checkStatus(Integer.parseInt(id)).equals("Running RAST")
								|| status.checkStatus(Integer.parseInt(id)).equals("Complete CISA")) {
							startLogProject(Integer.parseInt(id), 0, 0, 0, 0, 0);
						}
					}
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

				String order;

				Rast rast = new Rast();
				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM parameter WHERE idproject=" + id + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					String job_id = resulSet.getString("job_id");
					order = resulSet.getString("ordination");

					if (order.equals("1")) {
						if (job_id != null) {
							customPanel.addProgress(80);
							rast.statusRast(Integer.parseInt(id));
							customPanel.addProgress(20);
						} else {
							customPanel.addProgress(80);
							rast.submitRast(Integer.parseInt(id));
							customPanel.addProgress(20);

						}
					} else {
						if (job_id != null) {
							customPanel.addProgress(75);
							rast.statusRast(Integer.parseInt(id));
							customPanel.addProgress(25);
						} else {
							customPanel.addProgress(75);
							rast.submitRast(Integer.parseInt(id));
							customPanel.addProgress(25);

						}
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
			Thread.sleep(20000);

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
			String singleRead = null;
			String order = null;
			Tools tools = new Tools();

			CustomPanel customPanel = new CustomPanel();
			panel.add(customPanel);
			customPanel.setBounds(236, 229, 125, 96);

			Statement stm = null;
			String cmmdo;
			ResultSet rst;
			cmmdo = "SELECT * FROM organism WHERE idproject=" + idproject + ";";
			stm = DatabaseConnection.connect.createStatement();
			rst = stm.executeQuery(cmmdo);

			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			singleRead = rst.getString("single");

			order = resulSet.getString("ordination");

			if (order.equals("1")) {
				if ((singleRead != null)) {
					tools.runSingleMegahit(idproject, customPanel, 20);

					Thread.sleep(10000);

					tools.runSingleSPAdes(idproject, customPanel, 20);

					Thread.sleep(10000);

				} else {
					tools.runPareadMegahit(idproject, customPanel, 20);

					Thread.sleep(10000);

					tools.runPareadSPAdes(idproject, customPanel, 20);

					Thread.sleep(10000);
				}

				tools.runCisa(idproject, customPanel, 20);
				Thread.sleep(5000);

				tools.runMauve(idproject, customPanel, 20);
				Thread.sleep(10000);

				tools.runRast(idproject, customPanel, 20);
			} else {
				if ((singleRead != null)) {
					tools.runSingleMegahit(idproject, customPanel, 25);

					Thread.sleep(20000);

					tools.runSingleSPAdes(idproject, customPanel, 25);

					Thread.sleep(10000);

				} else {
					tools.runPareadMegahit(idproject, customPanel, 25);

					Thread.sleep(10000);

					tools.runPareadSPAdes(idproject, customPanel, 25);

					Thread.sleep(10000);
				}
				tools.runCisa(idproject, customPanel, 25);

				Thread.sleep(10000);

				tools.runRast(idproject, customPanel, 25);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
