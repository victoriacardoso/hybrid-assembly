package screen;

import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import database.DatabaseConnection;

public class Input {

	private JFrame frmInput;
	private JTextField txtNoFileSelected_single;
	private JTextField txtNoFileSelected_paired1;
	private JTextField txtNoFileSelected_paired2;
	private Button button_chooseFileSingleRead;
	private Button button_chooseRead1;
	private Button button_chooseRead2;
	private JPanel panel_SingleReadFileInput;
	private JPanel panel_pairedReadFileInput;
	private Button button_ReferenceFile;
	private String fileReadSingle;
	private String fileRead1;
	private String fileRead2;
	private String referenceFile;
	private String selectedItemLibrary;
	private JLabel lblRead1;
	private JLabel lblRead2;
	private JDialog emptyDialog;
	private JFileChooser fileChooser;
	private static String[] yesNoOptions = { "Yes", "No" };

	DatabaseConnection connect = new DatabaseConnection();
	SpadesAndMegahit spadesParameter = new SpadesAndMegahit();

	private JTextField txtNoFileSelected_reference;

	public Input() {
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmInput = new JFrame();
		frmInput.setTitle("Input");
		frmInput.getContentPane().setName("");
		frmInput.setBounds(100, 100, 654, 408);
		frmInput.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmInput.getContentPane().setLayout(null);
		frmInput.setLocationRelativeTo(null);
		frmInput.setResizable(false);
		frmInput.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {

				if (JOptionPane.showOptionDialog(null, "Do you want to exit the program?", "Attention",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, yesNoOptions,
						null) == JOptionPane.OK_OPTION) {
					try {
						int idproject = 0;
						Statement st = null;
						st = DatabaseConnection.connect.createStatement();
						idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

						Statement stmt_delfield = DatabaseConnection.connect.createStatement();
						String cmdd = "DELETE FROM parameter WHERE idproject=" + idproject + ";";
						stmt_delfield.executeUpdate(cmdd);

						System.exit(0);
					} catch (Exception e) {
						System.err.println(e.getClass().getName() + ": " + e.getMessage());
					}
				} else {
					frmInput.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				}

			};
		});

		JLabel lblInputRawData = new JLabel(" Input Raw Data and Reference File");
		lblInputRawData.setBounds(12, 22, 269, 15);
		frmInput.getContentPane().add(lblInputRawData);

		panel_SingleReadFileInput = new JPanel();
		panel_SingleReadFileInput.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Input",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_SingleReadFileInput.setBounds(37, 49, 578, 112);
		frmInput.getContentPane().add(panel_SingleReadFileInput);
		panel_SingleReadFileInput.setLayout(null);

		txtNoFileSelected_single = new JTextField();
		txtNoFileSelected_single.setVisible(false);
		txtNoFileSelected_single.setText("no file selected");
		txtNoFileSelected_single.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtNoFileSelected_single.setEditable(false);
		txtNoFileSelected_single.setBounds(117, 35, 435, 23);
		panel_SingleReadFileInput.add(txtNoFileSelected_single);
		txtNoFileSelected_single.setColumns(10);

		button_chooseFileSingleRead = new Button("Choose file");
		button_chooseFileSingleRead.setVisible(false);
		button_chooseFileSingleRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTQ FILES", "fastq", "fq"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showOpenDialog(button_chooseFileSingleRead);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setFileReadSingle(fileChooser.getSelectedFile().getAbsolutePath());
					txtNoFileSelected_single.setText(getFileReadSingle());

				}
			}
		});
		button_chooseFileSingleRead.setBounds(27, 35, 86, 23);
		panel_SingleReadFileInput.add(button_chooseFileSingleRead);

		JButton button = new JButton("Back");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CisaAndRast cisaAndRast = new CisaAndRast();
				cisaAndRast.getFrame().setVisible(true);
				try {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					PreparedStatement stat = null;
					stat = DatabaseConnection.connect.prepareStatement("UPDATE parameter SET cisaMinLength=" + null
							+ "," + "cisaGenomeSize=" + null + ", cisaR2Gap=" + null + ", rast_user='" + null + "',"
							+ "rast_pass='" + null + "'," + "taxonId=" + null + " WHERE idproject=" + idproject);
					stat.executeUpdate();
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
				getFrame().dispose();
			}
		});
		button.setBounds(37, 309, 72, 32);
		getFrame().getContentPane().add(button);

		panel_pairedReadFileInput = new JPanel();
		panel_pairedReadFileInput.setBounds(0, 0, 578, 112);
		panel_SingleReadFileInput.add(panel_pairedReadFileInput);
		panel_pairedReadFileInput.setLayout(null);
		panel_pairedReadFileInput.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Input",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));

		txtNoFileSelected_paired1 = new JTextField();
		txtNoFileSelected_paired1.setVisible(false);
		txtNoFileSelected_paired1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		txtNoFileSelected_paired1.setText("no file selected");
		txtNoFileSelected_paired1.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtNoFileSelected_paired1.setEditable(false);
		txtNoFileSelected_paired1.setColumns(10);
		txtNoFileSelected_paired1.setBounds(172, 37, 340, 23);
		panel_pairedReadFileInput.add(txtNoFileSelected_paired1);

		button_chooseRead1 = new Button("Choose file");
		button_chooseRead1.setVisible(false);
		button_chooseRead1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTQ FILES", "fastq", "fq"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showOpenDialog(button_chooseRead1);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setFileRead1(fileChooser.getSelectedFile().getAbsolutePath());
					txtNoFileSelected_paired1.setText(getFileRead1());

				}

			}
		});
		button_chooseRead1.setBounds(73, 37, 93, 23);
		panel_pairedReadFileInput.add(button_chooseRead1);

		button_chooseRead2 = new Button("Choose file");
		button_chooseRead2.setVisible(false);
		button_chooseRead2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FASTQ FILES", "fastq", "fq"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showOpenDialog(button_chooseRead2);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setFileRead2(fileChooser.getSelectedFile().getAbsolutePath());
					txtNoFileSelected_paired2.setText(getFileRead2());
				}
			}
		});
		button_chooseRead2.setBounds(73, 72, 93, 23);
		panel_pairedReadFileInput.add(button_chooseRead2);

		txtNoFileSelected_paired2 = new JTextField();
		txtNoFileSelected_paired2.setVisible(false);
		txtNoFileSelected_paired2.setText("no file selected");
		txtNoFileSelected_paired2.setEditable(false);
		txtNoFileSelected_paired2.setColumns(10);
		txtNoFileSelected_paired2.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtNoFileSelected_paired2.setBounds(172, 72, 340, 23);
		panel_pairedReadFileInput.add(txtNoFileSelected_paired2);

		lblRead1 = new JLabel("Read 1");
		lblRead1.setVisible(false);
		lblRead1.setBounds(12, 37, 121, 15);
		panel_pairedReadFileInput.add(lblRead1);

		lblRead2 = new JLabel("Read 2");
		lblRead2.setVisible(false);
		lblRead2.setBounds(12, 72, 58, 23);
		panel_pairedReadFileInput.add(lblRead2);

		emptyDialog = new JDialog(frmInput, "Error");
		emptyDialog.setBounds(450, 300, 400, 150);
		emptyDialog.getContentPane().setLayout(null);

		JLabel emptyField = new JLabel("Please, choose the files!");
		emptyField.setBounds(98, 23, 184, 30);
		emptyDialog.getContentPane().add(emptyField);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				emptyDialog.dispose();
			}
		});
		btnOk.setBounds(154, 66, 66, 19);
		emptyDialog.getContentPane().add(btnOk);

		JButton btnEnter = new JButton("Create and Run");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ProjectLog run = new ProjectLog();
				run.setId(null);
				try {
					if ((getFileReadSingle() == null && getTxtNoFileSelected().getText().equals("no file selected"))
							|| (getFileRead1() == null && getFileRead2() == null
									&& getTxtNoFileSelected().getText().equals("no file selected"))) {

						emptyDialog.setVisible(true);

					} else {

						if (getFileReadSingle() == null) {
							connect.insertOrganism(null, getFileRead1(), getFileRead2(), getReferenceFile(), null);
						}

						else {
							connect.insertOrganism(getFileReadSingle(), null, null, getReferenceFile(), null);

						}

						getFrame().dispose();

						run.openScreen();
						new Thread(ProjectLog.t1).start();
						new Thread(ProjectLog.t2).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnEnter.setBounds(465, 309, 150, 32);
		frmInput.getContentPane().add(btnEnter);

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CisaAndRast megahit = new CisaAndRast();
				megahit.getFrame().setVisible(true);
				getFrame().dispose();
			}
		});
		btnBack.setBounds(22, 304, 78, 31);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Reference File", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(37, 204, 578, 82);
		frmInput.getContentPane().add(panel);
		panel.setLayout(null);

		txtNoFileSelected_reference = new JTextField();
		txtNoFileSelected_reference.setEditable(false);
		txtNoFileSelected_reference.setText("no file selected");
		txtNoFileSelected_reference.setName("no file selected.");
		txtNoFileSelected_reference.setBorder(new EmptyBorder(0, 0, 0, 0));
		txtNoFileSelected_reference.setBounds(112, 34, 328, 23);
		panel.add(txtNoFileSelected_reference);
		txtNoFileSelected_reference.setColumns(10);

		button_ReferenceFile = new Button("Choose File");
		button_ReferenceFile.setBounds(10, 34, 86, 23);
		panel.add(button_ReferenceFile);
		button_ReferenceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("GBK FILES", "gbk", "gb"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = fileChooser.showOpenDialog(button_ReferenceFile);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					setReferenceFile(fileChooser.getSelectedFile().getAbsolutePath());

					try {
						int idproject = 0;
						Statement st = null;
						st = DatabaseConnection.connect.createStatement();
						idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

						Statement stmt = null;
						String command;
						ResultSet resultName;
						command = "SELECT * FROM project WHERE idproject=" + idproject + ";";
						stmt = DatabaseConnection.connect.createStatement();
						resultName = stmt.executeQuery(command);

						while (resultName.next()) {
							String output = fileChooser.getSelectedFile().getParent() + "/"
									+ resultName.getString("name").replaceAll("\s", "-");

							PreparedStatement preparedStmt = null;
							preparedStmt = DatabaseConnection.connect.prepareStatement(
									"UPDATE parameter SET output= '" + output + "' WHERE idproject=" + idproject + ";");
							preparedStmt.executeUpdate();
						}
					} catch (Exception e) {
						System.err.println(e.getClass().getName() + ": " + e.getMessage());
					}
					txtNoFileSelected_reference.setText(getReferenceFile());

				}

			}
		});

		JMenuBar menuBar = new JMenuBar();
		frmInput.setJMenuBar(menuBar);

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

	public JFrame getFrame() {
		return frmInput;
	}

	public String getFileReadSingle() {
		return fileReadSingle;
	}

	public String getFileRead1() {
		return fileRead1;
	}

	public String getFileRead2() {
		return fileRead2;
	}

	public void setFileReadSingle(String fileReadSingle) {
		this.fileReadSingle = fileReadSingle;
	}

	public void setFileRead1(String fileRead1) {
		this.fileRead1 = fileRead1;
	}

	public void setFileRead2(String fileRead2) {
		this.fileRead2 = fileRead2;
	}

	public String getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(String referenceFile) {
		this.referenceFile = referenceFile;
	}

	public String getSelectedItemLibrary() {
		return selectedItemLibrary;
	}

	public JTextField getTxtNoFileSelected_1() {
		return txtNoFileSelected_single;
	}

	public JTextField getTxtNoFileSelected_2() {
		return txtNoFileSelected_paired1;
	}

	public JTextField getTxtNoFileSelected_3() {
		return txtNoFileSelected_paired2;
	}

	public JTextField getTxtNoFileSelected() {
		return txtNoFileSelected_reference;
	}

	public Button getButton_chooseFileSingleRead() {
		return button_chooseFileSingleRead;
	}

	public Button getButton_chooseRead1() {
		return button_chooseRead1;
	}

	public Button getButton_chooseRead2() {
		return button_chooseRead2;
	}

	public Button getButton_ReferenceFile() {
		return button_ReferenceFile;
	}

	public JLabel getLblRead1() {
		return lblRead1;
	}

	public JLabel getLblRead2() {
		return lblRead2;
	}

	public JTextField getTxtNoFileSelected_paired1() {
		return txtNoFileSelected_paired1;
	}

	public JTextField getTxtNoFileSelected_paired2() {
		return txtNoFileSelected_paired2;
	}

	public JTextField getTxtNoFileSelected_reference() {
		return txtNoFileSelected_reference;
	}

	public JTextField getTxtNoFileSelected_single() {
		return txtNoFileSelected_single;
	}

	public JPanel getPanel_SingleReadFileInput() {
		return panel_SingleReadFileInput;
	}

	public JPanel getPanel_pairedReadFileInput() {
		return panel_pairedReadFileInput;
	}

}
