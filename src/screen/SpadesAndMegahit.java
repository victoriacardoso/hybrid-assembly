package screen;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import database.DatabaseConnection;

public class SpadesAndMegahit {

	private JFrame frm_SpadesAndMegahit;
	private JComboBox<String> comboBox_library;
	private JComboBox<String> comboBox_Orientation;
	private JComboBox<String> comboBox_options;
	private JLabel lblOrientation;
	private JLabel lblMemory;
	private JSpinner spinner_memory;
	private JLabel lblThreads;
	private JSpinner spinner_threads;
	private JLabel lblKmer;
	private JTextField txtKmers;
	private String selectedItemLibrary;
	private String selectedItemOrientation;
	private String selectedOption;
	private String output;
	private JTextField txt_kmerSizeMegahit;
	private JSpinner spinner_memFlag;
	private JSpinner spinner_minCount;
	private JLabel lblKmersMegahit;
	private JLabel memoryFlag;
	private JLabel minCt;
	private JDialog dialogEmpty;
	private static String[] yesNoOptions = { "Yes", "No" };

	/**
	 * Launch the application.
	 * 
	 * 
	 * /** Create the application.
	 * 
	 * @throws SQLException
	 */
	public SpadesAndMegahit() {

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	DatabaseConnection connect = new DatabaseConnection();

	public void initialize() {

		frm_SpadesAndMegahit = new JFrame();
		frm_SpadesAndMegahit.setTitle("Parameters");
		frm_SpadesAndMegahit.setBounds(100, 100, 654, 408);
		frm_SpadesAndMegahit.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frm_SpadesAndMegahit.setLocationRelativeTo(null);
		frm_SpadesAndMegahit.setResizable(false);

		frm_SpadesAndMegahit.addWindowListener(new WindowAdapter() {
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
					frm_SpadesAndMegahit.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

				}

			};
		});
		frm_SpadesAndMegahit.getContentPane().setLayout(null);

		JPanel panel_inputParameters = new JPanel();
		panel_inputParameters.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "SPAdes Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel_inputParameters.setBounds(12, 12, 616, 177);
		frm_SpadesAndMegahit.getContentPane().add(panel_inputParameters);
		panel_inputParameters.setLayout(null);

		JLabel lblNewLabel = new JLabel("Library");
		lblNewLabel.setBounds(12, 29, 70, 15);
		panel_inputParameters.add(lblNewLabel);

		comboBox_library = new JComboBox<String>();
		comboBox_library.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox_library.getSelectedItem().toString().equals("Single-read")) {
					lblOrientation.setVisible(false);
					comboBox_Orientation.setVisible(false);
				} else {
					lblOrientation.setVisible(true);
					comboBox_Orientation.setVisible(true);
				}
			}
		});
		comboBox_library.setModel(new DefaultComboBoxModel<String>(new String[] { "Single-read", "Paired-end" }));
		comboBox_library.setBounds(76, 22, 129, 24);
		panel_inputParameters.add(comboBox_library);

		lblOrientation = new JLabel("Orientation");
		lblOrientation.setVisible(false);
		lblOrientation.setBounds(327, 27, 104, 15);
		panel_inputParameters.add(lblOrientation);

		comboBox_Orientation = new JComboBox<String>();
		comboBox_Orientation.setVisible(false);
		comboBox_Orientation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		comboBox_Orientation.setModel(new DefaultComboBoxModel<String>(new String[] { "fr", "rf" }));
		comboBox_Orientation.setBounds(425, 22, 55, 24);
		panel_inputParameters.add(comboBox_Orientation);

		comboBox_options = new JComboBox<String>();
		comboBox_options.setModel(new DefaultComboBoxModel<String>(new String[] { "Assembly only", "Careful" }));
		comboBox_options.setBounds(76, 56, 129, 24);
		panel_inputParameters.add(comboBox_options);

		JLabel label = new JLabel("Options");
		label.setBounds(12, 61, 70, 15);
		panel_inputParameters.add(label);

		lblMemory = new JLabel("Memory");
		lblMemory.setEnabled(false);
		lblMemory.setBounds(12, 124, 70, 15);
		panel_inputParameters.add(lblMemory);

		lblThreads = new JLabel("Threads");
		lblThreads.setEnabled(false);
		lblThreads.setBounds(12, 149, 70, 15);
		panel_inputParameters.add(lblThreads);

		spinner_threads = new JSpinner();
		spinner_threads.setEnabled(false);
		spinner_threads.setModel(new SpinnerNumberModel(16, null, null, 1));
		spinner_threads.setBounds(76, 149, 51, 20);
		panel_inputParameters.add(spinner_threads);

		spinner_memory = new JSpinner();
		spinner_memory.setEnabled(false);
		spinner_memory.setModel(new SpinnerNumberModel(250, 1, null, 1));
		spinner_memory.setBounds(76, 124, 51, 20);
		panel_inputParameters.add(spinner_memory);

		JCheckBox chckbxKmers = new JCheckBox("Set K-mers size");
		chckbxKmers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxKmers.isSelected()) {
					lblKmer.setEnabled(true);
					txtKmers.setEnabled(true);
				} else {
					lblKmer.setEnabled(false);
					txtKmers.setEnabled(false);
				}
			}
		});
		chckbxKmers.setBounds(327, 93, 263, 23);
		panel_inputParameters.add(chckbxKmers);

		lblKmer = new JLabel("K-mer");
		lblKmer.setEnabled(false);
		lblKmer.setBounds(327, 122, 70, 15);
		panel_inputParameters.add(lblKmer);

		txtKmers = new JTextField();
		txtKmers.setText("auto");
		txtKmers.setEnabled(false);
		txtKmers.setColumns(10);
		txtKmers.setBounds(387, 120, 174, 19);
		panel_inputParameters.add(txtKmers);

		JCheckBox chckbxmemoryThreadandMemory = new JCheckBox("Set memory and threads settings");
		chckbxmemoryThreadandMemory.setBounds(12, 93, 263, 23);
		panel_inputParameters.add(chckbxmemoryThreadandMemory);
		chckbxmemoryThreadandMemory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxmemoryThreadandMemory.isSelected()) {
					lblMemory.setEnabled(true);
					spinner_memory.setEnabled(true);
					lblThreads.setEnabled(true);
					spinner_threads.setEnabled(true);
				} else {
					lblMemory.setEnabled(false);
					spinner_memory.setEnabled(false);
					lblThreads.setEnabled(false);
					spinner_threads.setEnabled(false);
				}

			}
		});

		dialogEmpty = new JDialog(frm_SpadesAndMegahit, "Error");
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
					String option = comboBox_options.getSelectedItem().toString();
					String selectedOption;
					switch (option) {
					case "Assembly only":
						selectedOption = "--only-assembler";
						setSelectedOption(selectedOption);
						break;
					default:
						selectedOption = "--careful";
						setSelectedOption(selectedOption);
						break;
					}
					String spadesKmer = getTxtKmers().getText();
					String spadesMemory = getSpinner_memory().getValue().toString();
					String spadesThreads = getSpinner_threads().getValue().toString();

					String mem_flag = getSpinner_memFlag().getValue().toString();
					String min_count = getSpinner_minCount().getValue().toString();
					String megahit_kmers = getTxt_kmerSizeMegahit().getText();
					PreparedStatement stt = null;

					if (!spadesKmer.isBlank() && !spadesMemory.isBlank() && !spadesThreads.isBlank()
							&& !getSelectedOption().isBlank()
							&& !getComboBox_Orientation().getSelectedItem().toString().isBlank() && !mem_flag.isBlank()
							&& !min_count.isBlank() && !megahit_kmers.isBlank()) {
						PreparedStatement stmt = null;

						int idproject = 0;
						Statement st = null;
						st = DatabaseConnection.connect.createStatement();
						idproject = st.executeQuery("select last_insert_rowid()").getInt(1);

						stt = DatabaseConnection.connect.prepareStatement(
								"UPDATE parameter SET mem_flag='" + mem_flag + "'," + "min_count='" + min_count + "',"
										+ "megahit_kmers='" + megahit_kmers + "' WHERE idproject=" + idproject);
						stt.executeUpdate();

						if (getComboBox_library().getSelectedItem().toString().equals("Single-read")) {

							stmt = DatabaseConnection.connect.prepareStatement("UPDATE parameter SET spades_options='"
									+ getSelectedOption() + "'," + "spades_kmers='" + spadesKmer + "',"
									+ "spades_memory='" + spadesMemory + "'," + "spades_threads='" + spadesThreads
									+ "' WHERE idproject=" + idproject);
							stmt.executeUpdate();

						} else {

							stmt = DatabaseConnection.connect.prepareStatement("UPDATE parameter SET spades_options='"
									+ getSelectedOption() + "'," + "spades_kmers='" + spadesKmer + "',"
									+ "spades_memory='" + spadesMemory + "'," + "orientation='"
									+ getComboBox_Orientation().getSelectedItem().toString() + "'," + "spades_threads='"
									+ spadesThreads + "' WHERE idproject=" + idproject);
							stmt.executeUpdate();

						}

						if (getComboBox_library().getSelectedItem().toString().equals("Single-read")) {
							getLblOrientation().setVisible(false);
							getComboBox_Orientation().setVisible(false);

						} else {
							getLblOrientation().setVisible(true);
							getComboBox_Orientation().setVisible(true);

						}
						CisaAndRast megahit = new CisaAndRast();
						megahit.getFrame().setVisible(true);
						getfrm_SpadesAndMegahit().dispose();
					} else {
						dialogEmpty.setVisible(true);
					}

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
			}

		});
		btnNext.setBounds(550, 304, 78, 31);
		frm_SpadesAndMegahit.getContentPane().add(btnNext);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Megahit Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(12, 192, 616, 100);
		frm_SpadesAndMegahit.getContentPane().add(panel);

		JCheckBox chckbxSetSettings = new JCheckBox("Set settings");
		chckbxSetSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxSetSettings.isSelected()) {
					memoryFlag.setEnabled(true);
					spinner_memFlag.setEnabled(true);
					minCt.setEnabled(true);
					spinner_minCount.setEnabled(true);
				} else {
					memoryFlag.setEnabled(false);
					spinner_memFlag.setEnabled(false);
					minCt.setEnabled(false);
					spinner_minCount.setEnabled(false);
				}
			}
		});
		chckbxSetSettings.setBounds(8, 21, 263, 23);
		panel.add(chckbxSetSettings);

		memoryFlag = new JLabel("Memory Flag");
		memoryFlag.setEnabled(false);
		memoryFlag.setBounds(12, 52, 100, 15);
		panel.add(memoryFlag);

		spinner_memFlag = new JSpinner();
		spinner_memFlag.setModel(new SpinnerNumberModel(1, 0, 2, 1));
		spinner_memFlag.setEnabled(false);
		spinner_memFlag.setBounds(165, 47, 51, 20);
		panel.add(spinner_memFlag);

		minCt = new JLabel("Minimum multiplicity ");
		minCt.setEnabled(false);
		minCt.setBounds(12, 77, 147, 15);
		panel.add(minCt);

		spinner_minCount = new JSpinner();
		spinner_minCount.setModel(new SpinnerNumberModel(2, 2, null, 1));
		spinner_minCount.setEnabled(false);
		spinner_minCount.setBounds(165, 72, 51, 20);
		panel.add(spinner_minCount);

		JCheckBox checkBoxKmersMegahit = new JCheckBox("Set K-mers size");
		checkBoxKmersMegahit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (checkBoxKmersMegahit.isSelected()) {
					lblKmersMegahit.setEnabled(true);
					txt_kmerSizeMegahit.setEnabled(true);
				} else {
					lblKmersMegahit.setEnabled(false);
					txt_kmerSizeMegahit.setEnabled(false);
				}
			}
		});
		checkBoxKmersMegahit.setBounds(345, 21, 263, 23);
		panel.add(checkBoxKmersMegahit);

		lblKmersMegahit = new JLabel("K-mers");
		lblKmersMegahit.setEnabled(false);
		lblKmersMegahit.setBounds(349, 52, 70, 15);
		panel.add(lblKmersMegahit);

		txt_kmerSizeMegahit = new JTextField();
		txt_kmerSizeMegahit.setText("21,29,39,59,79,99,119,141");
		txt_kmerSizeMegahit.setColumns(10);
		txt_kmerSizeMegahit.setEnabled(false);
		txt_kmerSizeMegahit.setBounds(409, 50, 195, 19);
		panel.add(txt_kmerSizeMegahit);

		JMenuBar menuBar = new JMenuBar();
		frm_SpadesAndMegahit.setJMenuBar(menuBar);

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
	}

	public JSpinner getSpinner_memory() {
		return spinner_memory;
	}

	public JFrame getfrm_SpadesAndMegahit() {
		return frm_SpadesAndMegahit;
	}

	public JComboBox<String> getComboBox_library() {
		return comboBox_library;
	}

	public JComboBox<String> getComboBox_options() {
		return comboBox_options;
	}

	public String getSelectedItemLibrary() {
		return selectedItemLibrary;
	}

	public String getSelectedItemOrientation() {
		return selectedItemOrientation;
	}

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public JSpinner getSpinner_threads() {
		return spinner_threads;
	}

	public JTextField getTxtKmers() {
		return txtKmers;
	}

	public JComboBox<String> getComboBox_Orientation() {
		return comboBox_Orientation;
	}

	public JLabel getLblOrientation() {
		return lblOrientation;
	}

	public JTextField getTxt_kmerSizeMegahit() {
		return txt_kmerSizeMegahit;
	}

	public JSpinner getSpinner_memFlag() {
		return spinner_memFlag;
	}

	public JSpinner getSpinner_minCount() {
		return spinner_minCount;
	}

	public JLabel getLblMemory() {
		return lblMemory;
	}

	public JLabel getLblThreads() {
		return lblThreads;
	}

	public JLabel getLblKmer() {
		return lblKmer;
	}

	public JDialog getDialogEmpty() {
		return dialogEmpty;
	}

}