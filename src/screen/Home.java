package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import database.DatabaseConnection;

public class Home {

	private JFrame frm_Home;
	private JDialog dialog;
	private JDialog dialogProjectEmpty;
	private JDialog dialog2;
	public static String selectedProject;
	private JPanel panel;
	private static File checkExist;
	private static String[] yesNoOptions = { "Yes", "No" };
	private DefaultTableModel dft;
	public static JTable table;
	private JScrollPane scrollPane;

	DatabaseConnection connect = new DatabaseConnection();

	public JDialog getDialog() {
		return dialog;
	}

	public JFrame getFrm_Home() {
		return frm_Home;
	}
	

	public JDialog getDialog2() {
		return dialog2;
	}

	public Home() throws SQLException {
		initialize();
		existingProjects();
		DatabaseConnection.connect.close();
		connect.connectDatabase();


	}

	public void initialize() {
		frm_Home = new JFrame();
		frm_Home.setResizable(false);
		frm_Home.setTitle("GenTreat - a tool to perform automated hybrid assembly");
		frm_Home.setBounds(100, 100, 654, 430);
		frm_Home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm_Home.getContentPane().setLayout(null);
		frm_Home.setLocationRelativeTo(null);
		frm_Home.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (JOptionPane.showOptionDialog(null, "Do you want to exit the program?", "Attention",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, yesNoOptions,
						null) == JOptionPane.OK_OPTION) {
					System.exit(0);
				} else {
					frm_Home.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				}

			};
		});

		JLabel lblGentreat = new JLabel(new ImageIcon("pictures/GenTreat.png"));
		lblGentreat.setFont(new Font("Dialog", Font.BOLD, 18));
		lblGentreat.setForeground(Color.BLACK);
		lblGentreat.setBounds(154, 13, 329, 109);
		frm_Home.getContentPane().add(lblGentreat);

		dialogProjectEmpty = new JDialog(frm_Home, "Error");
		dialogProjectEmpty.setBounds(450, 300, 400, 150);
		dialogProjectEmpty.setResizable(false);
		dialogProjectEmpty.getContentPane().setLayout(null);

		JLabel fieldEmpty = new JLabel("Please, insert the project name");
		fieldEmpty.setBounds(99, 30, 236, 30);
		dialogProjectEmpty.getContentPane().add(fieldEmpty);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialogProjectEmpty.dispose();
			}
		});
		btnOk.setBounds(176, 66, 66, 19);
		dialogProjectEmpty.getContentPane().add(btnOk);

		dialog = new JDialog(frm_Home, "Create Project");
		dialog.setBounds(450, 300, 400, 150);
		dialog.setResizable(false);
		dialog.getContentPane().setLayout(null);

		JLabel lblProjectName = new JLabel("Project Name");
		lblProjectName.setBounds(30, 32, 141, 20);
		dialog.getContentPane().add(lblProjectName);

		JTextField textField_projectName = new JTextField();
		textField_projectName.setBounds(132, 33, 242, 19);
		textField_projectName.setColumns(10);
		dialog.getContentPane().add(textField_projectName);

		panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Existing Projects",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(62, 134, 498, 162);
		frm_Home.getContentPane().add(panel);
		panel.setLayout(null);
		
		dialog2 = new JDialog(frm_Home, "Complete Process");
		dialog2.setTitle("About");
		dialog2.setBounds(420, 300, 530, 185);
		dialog2.setResizable(false);
		dialog2.getContentPane().setLayout(null);

		JLabel lblYouCanCheck = new JLabel("BIOD Research Group");
		lblYouCanCheck.setBounds(12, 56, 249, 17);
		dialog2.getContentPane().add(lblYouCanCheck);
		
		JLabel logoBIOD = new JLabel(new ImageIcon("pictures/BIOD-en.png"));
		logoBIOD.setLocation(320, 3);
		logoBIOD.setHorizontalAlignment(SwingConstants.RIGHT);
		logoBIOD.setVerticalTextPosition(SwingConstants.BOTTOM);
		logoBIOD.setSize(77, 139);
		dialog2.getContentPane().add(logoBIOD);

		JLabel logoUFPA = new JLabel(new ImageIcon("pictures/UFPA.png"));
		logoUFPA.setLocation(407, 0);
		logoUFPA.setHorizontalAlignment(SwingConstants.RIGHT);
		logoUFPA.setVerticalTextPosition(SwingConstants.BOTTOM);
		logoUFPA.setSize(112, 142);
		dialog2.getContentPane().add(logoUFPA);
		
		JLabel lblFederalUniversityOf = new JLabel("Federal University of Pará (UFPA/CAMTUC)");
		lblFederalUniversityOf.setBounds(12, 75, 311, 15);
		dialog2.getContentPane().add(lblFederalUniversityOf);
		
		JLabel lblContactAllanverascegmailcom = new JLabel("E-mail: allanverasce@gmail.com");
		lblContactAllanverascegmailcom.setBounds(12, 102, 249, 15);
		dialog2.getContentPane().add(lblContactAllanverascegmailcom);
		
		JLabel lblGenTreat = new JLabel("GenTreat");
		lblGenTreat.setFont(new Font("Dialog", Font.BOLD, 20));
		lblGenTreat.setBounds(12, 20, 164, 24);
		dialog2.getContentPane().add(lblGenTreat);
	
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String projectName = textField_projectName.getText();
				if (projectName.isBlank()) {
					dialogProjectEmpty.setVisible(true);

				} else {

					dialog.dispose();
					getFrm_Home().dispose();
					SpadesAndMegahit spadesScreen = new SpadesAndMegahit();
					spadesScreen.getfrm_SpadesAndMegahit().setVisible(true);

					connect.insertProject(projectName);
					connect.insertParameters(null, null, null, null, null, null, null, null, null, null, null, null,
							null, null, null);
				}

			}
		});
		btnNext.setBounds(296, 67, 78, 20);
		dialog.getContentPane().add(btnNext);

		JMenuBar menuBar = new JMenuBar();
		frm_Home.setJMenuBar(menuBar);

		JMenu mnProject = new JMenu("Project");
		menuBar.add(mnProject);

		JMenuItem mntmCreate = new JMenuItem("Create");
		mntmCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.setVisible(true);
			}
		});
		mnProject.add(mntmCreate);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog2.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);

	}

	public void existingProjects() {
		JDialog dialog = new JDialog(frm_Home, "Attention");
		dialog.setBounds(450, 300, 430, 150);
		dialog.getContentPane().setLayout(null);

		JLabel lblDoYouWant = new JLabel("Do you want to overwrite the files or create a new folder?");
		lblDoYouWant.setBounds(12, 44, 423, 25);
		dialog.getContentPane().add(lblDoYouWant);

		JButton btnOverwrite = new JButton("Overwrite");
		btnOverwrite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Statement st = null;
					String cmd;
					ResultSet resultSet = null;
					cmd = "SELECT * FROM parameter WHERE idproject=" + selectedProject + ";";
					st = DatabaseConnection.connect.createStatement();
					resultSet = st.executeQuery(cmd);

					Statement statement;
					String cmmd;
					ResultSet resulSet = null;
					statement = DatabaseConnection.connect.createStatement();
					cmmd = "SELECT * FROM project WHERE idproject=" + selectedProject + ";";
					resulSet = statement.executeQuery(cmmd);

					String output = resultSet.getString("output");

					if (resulSet.getString("status").equals("Complete process")) {
						File folderToDelete = new File(output);
						FileUtils.deleteDirectory(folderToDelete);
						ProjectLog log = new ProjectLog();
						log.openScreen();
						log.setId(selectedProject);
						new Thread(ProjectLog.t1).start();
						new Thread(ProjectLog.t2).start();
						frm_Home.dispose();
					}

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}

			}
		});
		btnOverwrite.setBounds(22, 81, 117, 25);
		dialog.getContentPane().add(btnOverwrite);

		JDialog dialog2 = new JDialog(frm_Home, "Folder Name");
		dialog2.setBounds(450, 300, 400, 170);
		dialog2.getContentPane().setLayout(null);

		JLabel lblInsertTheFolder = new JLabel("Insert the folder name");
		lblInsertTheFolder.setBounds(114, 25, 171, 15);
		dialog2.getContentPane().add(lblInsertTheFolder);

		JTextField textField = new JTextField();
		textField.setBounds(114, 52, 166, 19);
		dialog2.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblFolderAlreadyExists = new JLabel("Folder already exists!");
		lblFolderAlreadyExists.setForeground(Color.RED);
		lblFolderAlreadyExists.setFont(new Font("Dialog", Font.BOLD, 10));
		lblFolderAlreadyExists.setBounds(12, 98, 128, 15);
		dialog2.getContentPane().add(lblFolderAlreadyExists);
		lblFolderAlreadyExists.setVisible(false);

		JButton btnNewButton2 = new JButton("OK");
		btnNewButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PreparedStatement preparedStmt = null;
				try {
					String folderName = textField.getText();

					Statement st;
					String cmd;
					ResultSet rs = null;
					st = DatabaseConnection.connect.createStatement();
					cmd = "SELECT * FROM parameter WHERE idproject=" + selectedProject + ";";
					rs = st.executeQuery(cmd);

					String output = rs.getString("output");
					File outputFolder = new File(output);
					checkExist = new File(outputFolder.getParent() + "/" + folderName);
					if (checkExist.exists()) {
						lblFolderAlreadyExists.setVisible(true);
					}

					else {

						lblFolderAlreadyExists.setVisible(false);

						preparedStmt = DatabaseConnection.connect
								.prepareStatement("UPDATE parameter SET output= '" + checkExist.getParent() + "/"
										+ folderName + "' WHERE idproject=" + selectedProject + ";");
						preparedStmt.executeUpdate();

						Statement statement;
						String cmmd;
						ResultSet resulSet = null;
						statement = DatabaseConnection.connect.createStatement();
						cmmd = "SELECT * FROM project WHERE idproject=" + selectedProject + ";";
						resulSet = statement.executeQuery(cmmd);

						if (resulSet.getString("status").equals("Complete process")) {
							ProjectLog projectLog = new ProjectLog();
							projectLog.openScreen();
							projectLog.setId(selectedProject);
							new Thread(ProjectLog.t1).start();
							new Thread(ProjectLog.t2).start();
							frm_Home.dispose();
						}
					}

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}

			}

		});
		btnNewButton2.setBounds(158, 76, 71, 25);
		dialog2.getContentPane().add(btnNewButton2);

		JButton btnNewButton = new JButton("New Folder");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog2.setVisible(true);

			}
		});
		btnNewButton.setBounds(275, 81, 117, 25);
		dialog.getContentPane().add(btnNewButton);

		JLabel lblThereIsAlready = new JLabel("There is already a folder with the name of this project!");
		lblThereIsAlready.setBounds(22, 27, 421, 15);
		dialog.getContentPane().add(lblThereIsAlready);

		JButton btnRunAgain = new JButton("Run Again");
		btnRunAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ProjectLog run = new ProjectLog();
				try {
					Statement statement;
					String cmmd;
					ResultSet resulSet = null;
					statement = DatabaseConnection.connect.createStatement();
					cmmd = "SELECT * FROM project WHERE idproject=" + selectedProject + ";";
					resulSet = statement.executeQuery(cmmd);

					Statement st = null;
					String cmd;
					ResultSet resultSet = null;
					cmd = "SELECT * FROM parameter WHERE idproject=" + selectedProject + ";";
					st = DatabaseConnection.connect.createStatement();
					resultSet = st.executeQuery(cmd);

					File folderOutput = new File(resultSet.getString("output"));
					if (resulSet.getString("status").equals("Complete process")) {

						if (folderOutput.exists()) {
							dialog.setVisible(true);

						} else {
							run.setId(selectedProject);
							run.openScreen();
							new Thread(ProjectLog.t1).start();
							new Thread(ProjectLog.t2).start();
							frm_Home.dispose();
						}

					} else {

						if (folderOutput.exists()) {
							String output = resultSet.getString("output");
							if (resulSet.getString("status").equals("Running Megahit")) {
								File folderToDelete = new File(output);
								FileUtils.deleteDirectory(folderToDelete);
								ProjectLog log = new ProjectLog();
								log.setId(selectedProject);
								log.openScreen();
								runAgain(resulSet.getString("status"));
								frm_Home.dispose();
							}
							if (resulSet.getString("status").equals("Running SPAdes")
									|| resulSet.getString("status").equals("Complete Megahit")) {
								File folderToDelete = new File(output + "/spades-assembly");
								FileUtils.deleteDirectory(folderToDelete);
								ProjectLog log = new ProjectLog();
								log.setId(selectedProject);
								log.openScreen();
								runAgain(resulSet.getString("status"));
								frm_Home.dispose();
							}

							if (resulSet.getString("status").equals("Running CISA")
									|| resulSet.getString("status").equals("Complete SPAdes")) {
								File folderToDelete = new File(output + "/CISA");
								FileUtils.deleteDirectory(folderToDelete);
								ProjectLog log = new ProjectLog();
								log.setId(selectedProject);
								log.openScreen();
								runAgain(resulSet.getString("status"));
								frm_Home.dispose();
							}
							if (resulSet.getString("status").equals("Running Ragoo")
									|| resulSet.getString("status").equals("Complete CISA")) {
								File folderToDelete = new File(output + "/ragoo-output");
								FileUtils.deleteDirectory(folderToDelete);
								File folderToDelete2 = new File(output + "/GenTreat");
								FileUtils.deleteDirectory(folderToDelete2);
								ProjectLog log = new ProjectLog();
								log.setId(selectedProject);
								log.openScreen();
								runAgain(resulSet.getString("status"));
								frm_Home.dispose();
							}
							if (resulSet.getString("status").equals("Running RAST")
									|| resulSet.getString("status").equals("Complete Ragoo")) {
								File folderToDelete = new File(output + "/RAST");
								FileUtils.deleteDirectory(folderToDelete);
								ProjectLog log = new ProjectLog();
								log.setId(selectedProject);
								log.openScreen();
								runAgain(resulSet.getString("status"));
								frm_Home.dispose();
							}

						} else {
							run.setId(selectedProject);
							run.openScreen();
							runAgain(resulSet.getString("status"));
							frm_Home.dispose();
						}
					}

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
			}
		});
		btnRunAgain.setBounds(245, 125, 117, 25);
		btnRunAgain.setVisible(false);
		panel.add(btnRunAgain);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Statement stmt_delfield;
				
				try {
					
					stmt_delfield = DatabaseConnection.connect.createStatement();
					String cmdd = "DELETE FROM project WHERE idproject=" + selectedProject + ";";
					stmt_delfield.executeUpdate(cmdd);

					Statement st = DatabaseConnection.connect.createStatement();
					String cmd = "DELETE FROM parameter WHERE idproject=" + selectedProject + ";";
					st.executeUpdate(cmd);

					Statement stmt = DatabaseConnection.connect.createStatement();
					String cd = "DELETE FROM organism WHERE idproject=" + selectedProject + ";";
					stmt.executeUpdate(cd);
					
					
					existingProjects();
					btnDelete.setEnabled(false);
					btnRunAgain.setEnabled(false);
				
				} catch (Exception e0) {
					e0.printStackTrace();
				}

			}
		});
		btnDelete.setBounds(369, 125, 117, 25);
		btnDelete.setVisible(false);
		panel.add(btnDelete);

		table = new JTable();
		table.setDefaultEditor(Object.class, null);
		table.setEnabled(false);


		dft = new DefaultTableModel();
		dft = (DefaultTableModel) table.getModel();
		dft.addColumn("id");
		dft.addColumn("Name");
		dft.addColumn("Status");
		dft.addColumn("Creation Date");

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String valor = (String) table.getValueAt(table.rowAtPoint(e.getPoint()), 0);
				selectedProject = valor;
				
				btnDelete.setVisible(true);
				btnDelete.setEnabled(true);

				try {
					
						String status = (String) table.getValueAt(table.rowAtPoint(e.getPoint()), 2);
						if (status.equals("Complete process")) {
							btnRunAgain.setVisible(true);
							btnRunAgain.setEnabled(true);
							btnRunAgain.setText("Run Again");

						} else {
							btnRunAgain.setVisible(true);
							btnRunAgain.setEnabled(true);
							btnRunAgain.setText("Continue");

						}

					
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(221);
		table.getColumnModel().getColumn(2).setPreferredWidth(163);
		table.getColumnModel().getColumn(3).setPreferredWidth(163);

		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(new Rectangle(12, 23, 474, 90));
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setEnabled(false);
		panel.add(scrollPane);
		scrollPane.setViewportView(table);

		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet = null;
			statement = DatabaseConnection.connect.createStatement();
			cmmd = "SELECT * FROM project;";
			resulSet = statement.executeQuery(cmmd);

			while (resulSet.next()) {
				String[] list = { resulSet.getString("idproject"), resulSet.getString("name"),
						resulSet.getString("status"), resulSet.getString("createdate") };
				dft.addRow(list);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}

	public static void runAgain(String status) {
		if (status.equals("Running Megahit")) {
			new Thread(ProjectLog.t1).start();
			new Thread(ProjectLog.t2).start();
		}
		if (status.equals("Complete Megahit") || status.equals("Running SPAdes")) {
			new Thread(ProjectLog.t7).start();
			new Thread(ProjectLog.t3).start();
		}
		if (status.equals("Complete SPAdes") || status.equals("Running CISA")) {
			new Thread(ProjectLog.t7).start();
			new Thread(ProjectLog.t4).start();
		}
		if (status.equals("Complete CISA") || status.equals("Running Ragoo")) {
			new Thread(ProjectLog.t7).start();
			new Thread(ProjectLog.t5).start();
		}
		if (status.equals("Complete Ragoo") || status.equals("Running RAST")) {
			new Thread(ProjectLog.t7).start();
			new Thread(ProjectLog.t8).start();
		}
	}

	public static void main(String[] args) throws SQLException {
		DatabaseConnection connect = new DatabaseConnection();
		connect.start();
		connect.checkFieldsDataBase();
		Home home = new Home();
		home.existingProjects();
		home.getFrm_Home().setVisible(true);

	}
}
