package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.PreparedStatement;
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
				e.printStackTrace();
			}
		}
	};

	public static Runnable t2 = new Runnable() {

		@Override
		public void run() {
			try {
				if (id != null) {
					startLogProject(Integer.parseInt(id));
				} else {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					startLogProject(idproject);
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
				Statement stm = null;
				String cmmdo;
				ResultSet rst;
				cmmdo = "SELECT * FROM organism WHERE idproject =" + id + ";";
				stm = DatabaseConnection.connect.createStatement();
				rst = stm.executeQuery(cmmdo);
				while (rst.next()) {
					String singleRead = rst.getString("single");
					PreparedStatement sta = null;
					if (singleRead != null) {
						SingleRead single = new SingleRead();

						sta = DatabaseConnection.connect.prepareStatement(
								"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + id + ";");
						sta.executeUpdate();

						single.runSpades(Integer.parseInt(id));

						PreparedStatement statmt = null;
						statmt = DatabaseConnection.connect.prepareStatement(
								"UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject=" + id + ";");
						statmt.executeUpdate();
						Thread.sleep(10000);

					} else {
						spades.PairedRead pairedRead = new spades.PairedRead();

						sta = DatabaseConnection.connect.prepareStatement(
								"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + id + ";");
						sta.executeUpdate();

						pairedRead.runSpades(Integer.parseInt(id));

						PreparedStatement statmt = null;
						statmt = DatabaseConnection.connect.prepareStatement(
								"UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject=" + id + ";");
						statmt.executeUpdate();
						Thread.sleep(10000);
					}
				}
				PreparedStatement statmnt = null;
				statmnt = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running CISA' WHERE project.idproject=" + id + ";");
				statmnt.executeUpdate();

				Cisa cisa = new Cisa();
				cisa.mergeFileRun(Integer.parseInt(id));
				cisa.cisaFileRun(Integer.parseInt(id));
				PreparedStatement stmt = null;
				stmt = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Complete CISA' WHERE project.idproject=" + id + ";");
				stmt.executeUpdate();
				Thread.sleep(20000);

				PreparedStatement stmtOrder = null;
				stmtOrder = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running Mauve' WHERE project.idproject=" + id + ";");
				stmtOrder.executeUpdate();
				
				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				PreparedStatement stmtOrderFinish = null;
				stmtOrderFinish = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Complete Mauve' WHERE project.idproject=" + id + ";");
				stmtOrderFinish.executeUpdate();
				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t4 = new Runnable() {

		@Override
		public void run() {
			try {
				PreparedStatement statmnt = null;
				statmnt = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running CISA' WHERE project.idproject=" + id + ";");
				statmnt.executeUpdate();

				Cisa cisa = new Cisa();
				cisa.mergeFileRun(Integer.parseInt(id));
				cisa.cisaFileRun(Integer.parseInt(id));
				PreparedStatement stmt = null;
				stmt = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Complete CISA' WHERE project.idproject=" + id + ";");
				stmt.executeUpdate();
				Thread.sleep(20000);

				PreparedStatement stmtOrder = null;
				stmtOrder = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running Mauve' WHERE project.idproject=" + id + ";");
				stmtOrder.executeUpdate();
				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				PreparedStatement stmtOrderFinish = null;
				stmtOrderFinish = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Complete Mauve' WHERE project.idproject=" + id + ";");
				stmtOrderFinish.executeUpdate();
				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t5 = new Runnable() {

		@Override
		public void run() {
			try {
				PreparedStatement stmtOrder = null;
				stmtOrder = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Running Mauve' WHERE project.idproject=" + id + ";");
				stmtOrder.executeUpdate();
				Ordering ordering = new Ordering();
				ordering.OrderContigs(id);
				PreparedStatement stmtOrderFinish = null;
				stmtOrderFinish = DatabaseConnection.connect.prepareStatement(
						"UPDATE project SET status =  'Complete Mauve' WHERE project.idproject=" + id + ";");
				stmtOrderFinish.executeUpdate();
				Thread.sleep(20000);

				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	};
	public static Runnable t6 = new Runnable() {

		@Override
		public void run() {
			try {
				Rast rast = new Rast();
				rast.submitRast(Integer.parseInt(id));
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
			}

		}
	};
	public static Runnable t7 = new Runnable() {

		@Override
		public void run() {
			try {
				CustomPanel customPanel = new CustomPanel();
				panel.add(customPanel);
				customPanel.setBounds(236, 229, 125, 96);

				Date date = new Date();
				DateFormat dateFormat = DateFormat.getInstance();
				String startDate = dateFormat.format(date);

				Statement st;
				String cmd;
				ResultSet resultSet;
				cmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
				st = DatabaseConnection.connect.createStatement();
				resultSet = st.executeQuery(cmd);
				while (resultSet.next()) {
					String initialStatus = resultSet.getString("status");
					setStatus(initialStatus);
				}

				if (getStatus().equals("Running SPAdes")) {
					dft.addRow(new Object[][] { { null, null, null } });
					customPanel.addProgress(20);

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {

							String status = resulSet.getString("status");
							setStatus(status);

							dft.setValueAt("Running SPAdes", 0, 0);
							dft.setValueAt(startDate, 0, 1);
							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running SPAdes"));

					Date date0 = new Date();
					DateFormat dateFormat1 = DateFormat.getInstance();
					String finishedDateSpades = dateFormat1.format(date0);
					dft.setValueAt("Complete SPAdes", 0, 0);
					customPanel.addProgress(20);

					dft.setValueAt(finishedDateSpades, 0, 2);
					Thread.sleep(10000);

					dft.addRow(new Object[][] { { null, null, null } });
					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);

							dft.setValueAt("Running CISA", 1, 0);
							dft.setValueAt(finishedDateSpades, 1, 1);
							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running CISA"));
					customPanel.addProgress(20);
					Date date000 = new Date();
					DateFormat df = DateFormat.getInstance();
					String finishedDateCisa = df.format(date000);
					dft.setValueAt("Complete CISA", 1, 0);
					dft.setValueAt(finishedDateCisa, 1, 2);

					dft.addRow(new Object[][] { { null, null, null } });
					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running Mauve", 2, 0);
							dft.setValueAt(finishedDateCisa, 2, 1);

							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running Mauve"));
					customPanel.addProgress(20);
					Date date0000 = new Date();
					DateFormat df0 = DateFormat.getInstance();
					String finishedDateRagoo = df0.format(date0000);

					dft.setValueAt("Complete Mauve", 2, 0);
					dft.setValueAt(finishedDateRagoo, 2, 2);
					dft.addRow(new Object[][] { { null, null, null } });

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);

							dft.setValueAt("Running RAST", 3, 0);
							dft.setValueAt(finishedDateRagoo, 3, 1);
							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running RAST"));
					customPanel.addProgress(20);

					Date date00000 = new Date();
					DateFormat df00 = DateFormat.getInstance();
					String finishedDateRast = df00.format(date00000);
					dft.setValueAt("Complete RAST", 3, 0);
					dft.setValueAt(finishedDateRast, 3, 2);

					Statement statement = null;
					String command;
					ResultSet rs;
					command = "SELECT * FROM parameter WHERE idproject =" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					rs = statement.executeQuery(command);
					while (rs.next()) {

						String output = rs.getString("output");

						emptyField.setText(output);
						dialog.setVisible(true);
					}
				}
				if (status.equals("Running CISA")) {
					dft.addRow(new Object[][] { { null, null, null } });
					customPanel.addProgress(40);

					Date date0 = new Date();
					DateFormat dateFormat1 = DateFormat.getInstance();
					String finishedDateSpades = dateFormat1.format(date0);
					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running CISA", 0, 0);
							dft.setValueAt(finishedDateSpades, 0, 1);

							Thread.sleep(30000);
						}

					} while (getStatus().equals("Running CISA"));
					customPanel.addProgress(20);

					Date date000 = new Date();
					DateFormat df = DateFormat.getInstance();
					String finishedDateCisa = df.format(date000);
					dft.setValueAt("Complete CISA", 0, 0);
					dft.setValueAt(finishedDateCisa, 0, 2);

					dft.addRow(new Object[][] { { null, null, null } });

					do {
						dft.setValueAt("Running Mauve", 1, 0);
						dft.setValueAt(finishedDateCisa, 1, 1);
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);
							Thread.sleep(30000);
						}

					} while (getStatus().equals("Running Mauve"));
					customPanel.addProgress(20);
					Date date0000 = new Date();
					DateFormat df0 = DateFormat.getInstance();
					String finishedDateRagoo = df0.format(date0000);

					dft.setValueAt("Complete Mauve", 1, 0);
					dft.setValueAt(finishedDateRagoo, 1, 2);
					dft.addRow(new Object[][] { { null, null, null } });

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {

							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running RAST", 2, 0);
							dft.setValueAt(finishedDateRagoo, 2, 1);
							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running RAST"));
					customPanel.addProgress(20);

					Date date000000 = new Date();
					DateFormat df000 = DateFormat.getInstance();
					String finishedDateRast = df000.format(date000000);
					dft.setValueAt("Complete RAST", 2, 0);
					dft.setValueAt(finishedDateRast, 2, 2);

					Statement statement = null;
					String command;
					ResultSet rs;
					command = "SELECT * FROM parameter WHERE idproject =" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					rs = statement.executeQuery(command);

					while (rs.next()) {
						String output = rs.getString("output");

						emptyField.setText(output);
						dialog.setVisible(true);
					}
				}
				if (getStatus().equals("Running Mauve")) {
					dft.addRow(new Object[][] { { null, null, null } });
					Date date000 = new Date();
					DateFormat df = DateFormat.getInstance();
					String finishedDateCisa = df.format(date000);
					customPanel.addProgress(60);

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running Mauve", 0, 0);
							dft.setValueAt(finishedDateCisa, 0, 1);

							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running Mauve"));
					customPanel.addProgress(20);
					Date date0000 = new Date();
					DateFormat df0 = DateFormat.getInstance();
					String finishedDateRagoo = df0.format(date0000);

					dft.setValueAt("Complete Mauve", 0, 0);
					dft.setValueAt(finishedDateRagoo, 0, 2);
					dft.addRow(new Object[][] { { null, null, null } });

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {
							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running RAST", 1, 0);
							dft.setValueAt(finishedDateRagoo, 1, 1);
							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running RAST"));
					customPanel.addProgress(20);

					Date date000000 = new Date();
					DateFormat df000 = DateFormat.getInstance();
					String finishedDateRast = df000.format(date000000);
					dft.setValueAt("Complete RAST", 1, 0);
					dft.setValueAt(finishedDateRast, 1, 2);

					Statement statement = null;
					String command;
					ResultSet rs;
					command = "SELECT * FROM parameter WHERE idproject =" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					rs = statement.executeQuery(command);
					while (rs.next()) {
						String output = rs.getString("output");

						emptyField.setText(output);
						dialog.setVisible(true);
					}
				}
				if (getStatus().equals("Running RAST")) {
					dft.addRow(new Object[][] { { null, null, null } });
					Date date0000 = new Date();
					DateFormat df0 = DateFormat.getInstance();
					String finishedDateRagoo = df0.format(date0000);
					customPanel.addProgress(80);

					do {
						Statement statement;
						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM project WHERE project.idproject=" + id + ";";
						statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);
						while (resulSet.next()) {

							String status = resulSet.getString("status");
							setStatus(status);
							dft.setValueAt("Running RAST", 0, 0);
							dft.setValueAt(finishedDateRagoo, 0, 1);

							Thread.sleep(30000);
						}
					} while (getStatus().equals("Running RAST"));
					customPanel.addProgress(20);

					Date date000000 = new Date();
					DateFormat df000 = DateFormat.getInstance();
					String finishedDateRast = df000.format(date000000);
					dft.setValueAt("Complete RAST", 0, 0);
					dft.setValueAt(finishedDateRast, 0, 2);
					Statement statement = null;
					String command;
					ResultSet rs;
					command = "SELECT * FROM parameter WHERE idproject =" + id + ";";
					statement = DatabaseConnection.connect.createStatement();
					rs = statement.executeQuery(command);
					while (rs.next()) {

						String output = rs.getString("output");

						emptyField.setText(output);
						dialog.setVisible(true);
					}
				}

			} catch (SQLException | NumberFormatException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	public static Runnable t8 = new Runnable() {

		@Override
		public void run() {
			try {
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
						rast.statusRast(Integer.parseInt(id));
					}
					else {
						rast.submitRast(Integer.parseInt(id));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static void startLogProject(int idproject) {
		try {
			CustomPanel customPanel = new CustomPanel();
			panel.add(customPanel);
			customPanel.setBounds(236, 229, 125, 96);

			Date date = new Date();
			DateFormat dateFormat = DateFormat.getInstance();
			String startDate = dateFormat.format(date);
			do {
				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM project WHERE idproject=" + idproject + ";";
				statement = DatabaseConnection.connect.createStatement();

				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					String status = resulSet.getString("status");
					setStatus(status);
		
					dft.setNumRows(1);
					dft.setValueAt("Running Megahit", 0, 0);
					dft.setValueAt(startDate, 0, 1);

					Thread.sleep(30000);
				}

			} while (getStatus().equals("Running Megahit"));
			customPanel.addProgress(20);

			Date date0 = new Date();
			DateFormat dateFormat1 = DateFormat.getInstance();
			String finishedDate = dateFormat1.format(date0);
			dft.setValueAt("Complete Megahit", 0, 0);

			dft.setValueAt(finishedDate, 0, 2);
			Thread.sleep(10000);
			dft.addRow(new Object[][] { { null, null, null } });

			do {

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM project WHERE idproject=" + idproject + ";";
				statement = DatabaseConnection.connect.createStatement();

				resulSet = statement.executeQuery(cmmd);
				while (resulSet.next()) {

					String status = resulSet.getString("status");
					setStatus(status);
					dft.setValueAt("Running SPades", 1, 0);
					dft.setValueAt(finishedDate, 1, 1);
					Thread.sleep(30000);
				}
			} while (getStatus().equals("Running SPAdes"));
			customPanel.addProgress(20);

			Date date00 = new Date();
			DateFormat dateFormat00 = DateFormat.getInstance();
			String finishedDateSpades = dateFormat00.format(date00);
			dft.setValueAt("Complete Spades", 1, 0);
			dft.setValueAt(finishedDateSpades, 1, 2);

			dft.addRow(new Object[][] { { null, null, null } });

			do {

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM project WHERE project.idproject=" + idproject + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);
				while (resulSet.next()) {
					String status = resulSet.getString("status");
					setStatus(status);
					dft.setValueAt("Running CISA", 2, 0);
					dft.setValueAt(finishedDateSpades, 2, 1);
					Thread.sleep(30000);
				}

			} while (getStatus().equals("Running CISA"));
			customPanel.addProgress(20);

			Date date000 = new Date();
			DateFormat df = DateFormat.getInstance();
			String finishedDateCisa = df.format(date000);
			dft.setValueAt("Complete CISA", 2, 0);
			dft.setValueAt(finishedDateCisa, 2, 2);

			dft.addRow(new Object[][] { { null, null, null } });

			do {

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM project WHERE project.idproject=" + idproject + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					String status = resulSet.getString("status");
					setStatus(status);

					dft.setValueAt("Running Mauve", 3, 0);
					dft.setValueAt(finishedDateCisa, 3, 1);

					Thread.sleep(30000);
				}
			} while (getStatus().equals("Running Mauve"));
			customPanel.addProgress(20);

			Date date0000 = new Date();
			DateFormat df0 = DateFormat.getInstance();
			String finishedDateRagoo = df0.format(date0000);

			dft.setValueAt("Complete Mauve", 3, 0);
			dft.setValueAt(finishedDateRagoo, 3, 2);

			dft.addRow(new Object[][] { { null, null, null } });

			do {

				Statement statement;
				String cmmd;
				ResultSet resulSet;
				cmmd = "SELECT * FROM project WHERE idproject=" + idproject + ";";
				statement = DatabaseConnection.connect.createStatement();
				resulSet = statement.executeQuery(cmmd);

				while (resulSet.next()) {
					String status = resulSet.getString("status");
					setStatus(status);

					dft.setValueAt("Running RAST", 4, 0);
					dft.setValueAt(finishedDateRagoo, 4, 1);
					Thread.sleep(30000);
				}
			} while (getStatus().equals("Running RAST"));

			customPanel.addProgress(20);

			Date date00000 = new Date();
			DateFormat df00 = DateFormat.getInstance();
			String finishedDateRast = df00.format(date00000);
			dft.setValueAt("Complete RAST", 4, 0);
			dft.setValueAt(finishedDateRast, 4, 2);

			Statement statement = null;
			String command;
			ResultSet resultSet;
			command = "SELECT * FROM parameter WHERE idproject =" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resultSet = statement.executeQuery(command);

			while (resultSet.next()) {

				String output = resultSet.getString("output");

				emptyField.setText(output);
				dialog.setVisible(true);

			}

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

			while (rst.next()) {
				String singleRead = rst.getString("single");

				if ((singleRead != null)) {
					PreparedStatement statement = null;
					statement = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Running Megahit' WHERE project.idproject="
									+ idproject + ";");
					statement.executeUpdate();

					megahit.SingleRead megahitSingle = new megahit.SingleRead();
					megahitSingle.runMegahit(idproject);

					PreparedStatement stat = null;
					stat = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Complete Megahit' WHERE project.idproject="
									+ idproject + ";");
					stat.executeUpdate();
					Thread.sleep(20000);

					SingleRead single = new SingleRead();
					PreparedStatement sta = null;
					sta = DatabaseConnection.connect.prepareStatement(
							"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + idproject + ";");
					sta.executeUpdate();

					single.runSpades(idproject);

					PreparedStatement statmt = null;
					statmt = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject="
									+ idproject + ";");
					statmt.executeUpdate();
					Thread.sleep(10000);

				} else {
					PreparedStatement statement = null;
					statement = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Running Megahit' WHERE project.idproject="
									+ idproject + ";");
					statement.executeUpdate();
					PairedRead paired = new PairedRead();
					paired.runMegahit(idproject);
					PreparedStatement stat = null;
					stat = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Complete Megahit' WHERE project.idproject="
									+ idproject + ";");
					stat.executeUpdate();
					Thread.sleep(20000);

					spades.PairedRead pairedspades = new spades.PairedRead();
					PreparedStatement sta = null;
					sta = DatabaseConnection.connect.prepareStatement(
							"UPDATE project SET status =  'Running SPAdes' WHERE project.idproject=" + idproject + ";");
					sta.executeUpdate();

					pairedspades.runSpades(idproject);

					PreparedStatement statmt = null;
					statmt = DatabaseConnection.connect
							.prepareStatement("UPDATE project SET status =  'Complete SPAdes' WHERE project.idproject="
									+ idproject + ";");
					statmt.executeUpdate();

					Thread.sleep(10000);
				}
			}
			PreparedStatement statmnt = null;
			statmnt = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Running CISA' WHERE project.idproject=" + idproject + ";");
			statmnt.executeUpdate();

			Cisa cisa = new Cisa();
			cisa.mergeFileRun(idproject);
			cisa.cisaFileRun(idproject);
			PreparedStatement stmt = null;
			stmt = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Complete CISA' WHERE project.idproject=" + idproject + ";");
			stmt.executeUpdate();
			Thread.sleep(20000);

			PreparedStatement stmtOrder = null;
			stmtOrder = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Running Mauve' WHERE project.idproject=" + idproject + ";");
			stmtOrder.executeUpdate();
			Ordering ordering = new Ordering();
			ordering.OrderContigs(id);
			PreparedStatement stmtOrderFinish = null;
			stmtOrderFinish = DatabaseConnection.connect.prepareStatement(
					"UPDATE project SET status =  'Complete Mauve' WHERE project.idproject=" + idproject + ";");
			stmtOrderFinish.executeUpdate();
			Thread.sleep(20000);

			Rast rast = new Rast();
			rast.submitRast(idproject);

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
