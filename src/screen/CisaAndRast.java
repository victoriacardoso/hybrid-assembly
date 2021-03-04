package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import database.DatabaseConnection;
import javax.swing.JRadioButton;

public class CisaAndRast {
	private JFrame frmParameters;

	private JTextField textField_GenomeSize;
	private JTextField textField_MinLength;
	private JTextField textField_R2Gap;
	private JTextField textField_loginRast;
	private JTextField textField_taxonId;
	private JDialog dialogEmpty;
	private JPasswordField passwordField;
	private static String[] yesNoOptions = { "Yes", "No" };

	public CisaAndRast() {
		initialize();
	}

	private void initialize() {
		frmParameters = new JFrame();
		frmParameters.setResizable(false);
		frmParameters.setTitle("Parameters");
		frmParameters.setBounds(100, 100, 654, 408);
		frmParameters.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmParameters.setLocationRelativeTo(null);
		frmParameters.getContentPane().setLayout(null);
		frmParameters.addWindowListener(new WindowAdapter() {
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
					frmParameters.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				}

			};
		});

		JMenuBar menuBar = new JMenuBar();
		frmParameters.setJMenuBar(menuBar);

		JMenu mnProject = new JMenu("Project");
		menuBar.add(mnProject);

		JMenuItem mntmCreate = new JMenuItem("Create");
		mntmCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Home home;
				try {
					home = new Home();
					home.getDialog().setVisible(true);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "CISA Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_1.setBounds(12, 28, 616, 80);
		frmParameters.getContentPane().add(panel_1);

		JLabel lblGenomeSize = new JLabel("Genome size");
		lblGenomeSize.setBounds(368, 25, 112, 15);
		panel_1.add(lblGenomeSize);

		textField_GenomeSize = new JTextField();
		textField_GenomeSize.setColumns(10);
		textField_GenomeSize.setBounds(485, 25, 70, 19);
		panel_1.add(textField_GenomeSize);

		JLabel lblMinimumLength = new JLabel("Minimum length");
		lblMinimumLength.setBounds(12, 25, 112, 15);
		panel_1.add(lblMinimumLength);

		textField_MinLength = new JTextField();
		textField_MinLength.setText("100");
		textField_MinLength.setColumns(10);
		textField_MinLength.setBounds(129, 25, 70, 19);
		panel_1.add(textField_MinLength);

		JLabel lblGenomeSize_1 = new JLabel("R2_Gap");
		lblGenomeSize_1.setBounds(12, 52, 112, 15);
		panel_1.add(lblGenomeSize_1);

		textField_R2Gap = new JTextField();
		textField_R2Gap.setText("0.95");
		textField_R2Gap.setColumns(10);
		textField_R2Gap.setBounds(129, 52, 70, 19);
		panel_1.add(textField_R2Gap);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "RAST Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 184, 616, 108);
		frmParameters.getContentPane().add(panel);

		JLabel lblLoginRast = new JLabel("Login");
		lblLoginRast.setBounds(12, 25, 127, 15);
		panel.add(lblLoginRast);

		JLabel lblPasswordRast = new JLabel("Password");
		lblPasswordRast.setBounds(12, 52, 127, 15);
		panel.add(lblPasswordRast);

		textField_loginRast = new JTextField();
		textField_loginRast.setColumns(10);
		textField_loginRast.setBounds(138, 23, 114, 19);
		panel.add(textField_loginRast);

		JLabel lblTaxonomicIdOf = new JLabel("Taxonomic ID of Organism");
		lblTaxonomicIdOf.setBounds(298, 23, 193, 15);
		panel.add(lblTaxonomicIdOf);

		textField_taxonId = new JTextField();
		textField_taxonId.setColumns(10);
		textField_taxonId.setBounds(490, 23, 72, 19);
		panel.add(textField_taxonId);

		dialogEmpty = new JDialog(frmParameters, "Error");
		dialogEmpty.setBounds(450, 300, 400, 150);
		dialogEmpty.setResizable(false);
		dialogEmpty.getContentPane().setLayout(null);

		JLabel emptyField = new JLabel("There are empty fields!");
		emptyField.setBounds(104, 24, 184, 30);
		dialogEmpty.getContentPane().add(emptyField);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialogEmpty.dispose();
			}
		});
		btnOk.setBounds(154, 66, 66, 19);
		dialogEmpty.getContentPane().add(btnOk);

		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {

					String password_Rast = new String(passwordField.getPassword());

					String cisaMinLength = getTextField_MinLength().getText();
					String cisaGenomeSize = getTextField_GenomeSize().getText();
					String cisaR2Gap = getTextField_R2Gap().getText();

					String rast_login = getTextField_loginRast().getText();
					String taxonId = getTextField_taxonId().getText();

					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					if (!cisaR2Gap.isBlank() && !cisaMinLength.isBlank() && !cisaGenomeSize.isBlank()
							&& !rast_login.isBlank() && !password_Rast.isBlank() && !taxonId.isBlank()) {
						PreparedStatement stat = null;
						stat = DatabaseConnection.connect.prepareStatement("UPDATE parameter SET cisaMinLength="
								+ cisaMinLength + "," + "cisaGenomeSize=" + cisaGenomeSize + ", cisaR2Gap=" + cisaR2Gap
								+ ", rast_user='" + rast_login + "'," + "rast_pass='" + password_Rast + "',"
								+ "taxonId=" + taxonId + " WHERE idproject=" + idproject);
						stat.executeUpdate();

						Input input = new Input();

						String cmmd;
						ResultSet resulSet;
						cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
						Statement statement = DatabaseConnection.connect.createStatement();
						resulSet = statement.executeQuery(cmmd);

						while (resulSet.next()) {
							String orientation = resulSet.getString("orientation");

							if (orientation == null) {
								input.getTxtNoFileSelected_single().setVisible(true);
								input.getButton_chooseFileSingleRead().setVisible(true);
							} else {
								input.getPanel_pairedReadFileInput().setVisible(true);
								input.getLblRead1().setVisible(true);
								input.getLblRead2().setVisible(true);
								input.getTxtNoFileSelected_paired1().setVisible(true);
								input.getTxtNoFileSelected_paired2().setVisible(true);
								input.getButton_chooseRead1().setVisible(true);
								input.getButton_chooseRead2().setVisible(true);
							}
						}

						input.getFrame().setVisible(true);
						getFrame().dispose();

					} else {
						dialogEmpty.setVisible(true);
					}
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}

			}
		});
		btnNext.setBounds(550, 304, 78, 31);
		frmParameters.getContentPane().add(btnNext);

		JButton button = new JButton("Back");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpadesAndMegahit spadesParametersScreen = new SpadesAndMegahit();
				spadesParametersScreen.getfrm_SpadesAndMegahit().setVisible(true);
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

					PreparedStatement statSpades = null;
					statSpades = DatabaseConnection.connect.prepareStatement("UPDATE parameter SET spades_options="
							+ null + "," + "orientation=" + null + ", spades_kmers=" + null + ", spades_memory='" + null
							+ "'," + "spades_threads='" + null + "'," + "mem_flag='" + null + "'," + "min_count='"
							+ null + "'," + "megahit_kmers='" + null + "' WHERE idproject=" + idproject);
					statSpades.executeUpdate();

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
				getFrame().dispose();
			}
		});
		button.setBounds(26, 303, 72, 32);
		frmParameters.getContentPane().add(button);

		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordField.setBounds(138, 50, 114, 19);
		passwordField.setEchoChar('*');
		panel.add(passwordField);

		JCheckBox chckbxShowPassword = new JCheckBox("Show password");
		chckbxShowPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxShowPassword.isSelected()) {
					passwordField.setEchoChar((char) 0);
				} else {
					passwordField.setEchoChar('*');
				}
			}
		});
		chckbxShowPassword.setFont(new Font("Dialog", Font.BOLD, 10));
		chckbxShowPassword.setBounds(267, 49, 129, 23);
		panel.add(chckbxShowPassword);

		JPanel panel_1_1 = new JPanel();
		panel_1_1.setBounds(12, 124, 616, 48);
		frmParameters.getContentPane().add(panel_1_1);
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Mauve", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(51, 51, 51)));

		JLabel lblDoYouWant = new JLabel("Do you want to order the assembly result?");
		lblDoYouWant.setBounds(12, 25, 316, 15);
		panel_1_1.add(lblDoYouWant);
		
		JRadioButton rdbtnYes = new JRadioButton("Yes");
		JRadioButton rdbtnNo = new JRadioButton("No");
		
		rdbtnNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnYes.setSelected(false);
				try {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					PreparedStatement stat = null;
					stat = DatabaseConnection.connect
							.prepareStatement("UPDATE parameter SET ordination=0" + " WHERE idproject=" + idproject);
					stat.executeUpdate();
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ":" + e.getMessage());
				}
			}
		});

		rdbtnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnNo.setSelected(false);
				try {
					int idproject = 0;
					Statement st = null;
					st = DatabaseConnection.connect.createStatement();
					idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

					PreparedStatement stat = null;
					stat = DatabaseConnection.connect
							.prepareStatement("UPDATE parameter SET ordination=1" + " WHERE idproject=" + idproject);
					stat.executeUpdate();
				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ":" + e.getMessage());
				}
			}
		});
		rdbtnYes.setBounds(340, 21, 56, 23);
		panel_1_1.add(rdbtnYes);

		rdbtnNo.setBounds(400, 21, 56, 23);
		panel_1_1.add(rdbtnNo);

	}

	public JFrame getFrame() {
		return frmParameters;
	}

	public JTextField getTextField_loginRast() {
		return textField_loginRast;
	}

	public JTextField getTextField_taxonId() {
		return textField_taxonId;
	}

	public JTextField getTextField_GenomeSize() {
		return textField_GenomeSize;
	}

	public JTextField getTextField_MinLength() {
		return textField_MinLength;
	}

	public JTextField getTextField_R2Gap() {
		return textField_R2Gap;
	}
}
