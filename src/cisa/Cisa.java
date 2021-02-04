package cisa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import database.DatabaseConnection;

public class Cisa {
	private String idproject;
	private String assemblyOutput;
	private Integer genomeSize;

	public String getIdproject() {
		return idproject;
	}

	public String getAssemblyOutput() {
		return assemblyOutput;
	}

	public int getGenomeSize() {
		return genomeSize;
	}

	public void mergeFileRun(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;
			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);

			while (resulSet.next()) {

				String assemblyOutput = resulSet.getString("output");
				
				File cisaFolder = new File(assemblyOutput + "/CISA");
				if(!cisaFolder.exists()) {
					cisaFolder.mkdir();
				}
				
				String cpAssemblySpades = "cp " + assemblyOutput + "/spades-assembly/scaffolds.fasta" + " "
						+ assemblyOutput + "/CISA/";
				CommandLine cpAssembly1 = CommandLine.parse(cpAssemblySpades);
				DefaultExecutor cpExecutor = new DefaultExecutor();
				cpExecutor.execute(cpAssembly1);

				String cpAssemblyMegahit = "cp " + assemblyOutput + "/megahit-assembly/final.contigs.fa" + " "
						+ assemblyOutput + "/CISA/";
				CommandLine cpAssembly2 = CommandLine.parse(cpAssemblyMegahit);
				DefaultExecutor cpExecutor2 = new DefaultExecutor();
				cpExecutor2.execute(cpAssembly2);

				BufferedWriter bw = new BufferedWriter(new FileWriter(assemblyOutput + "/CISA/Merge.config"));
				bw.write("count=2");
				bw.newLine();
				bw.write("data=" + assemblyOutput + "/CISA/scaffolds.fasta" + ",title=Contig1");
				bw.newLine();
				bw.write("data=" + assemblyOutput + "/CISA/final.contigs.fa" + ",title=Contig2");
				bw.newLine();
				bw.write("min_length=100");
				bw.newLine();
				bw.write("Master_file=" + assemblyOutput + "/CISA/Merged.ctg.fa");

				bw.flush();
				bw.close();

				String mergeCommand = "python2 " + "lib/CISA1.3/Merge.py " + assemblyOutput + "/CISA/Merge.config";
				CommandLine runMerge = CommandLine.parse(mergeCommand);
				DefaultExecutor singleExecutor = new DefaultExecutor();
				singleExecutor.execute(runMerge);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void cisaFileRun(int idproject) {
		try {
			Statement statement;
			String cmmd;
			ResultSet resulSet;

			cmmd = "SELECT * FROM parameter WHERE idproject=" + idproject + ";";
			statement = DatabaseConnection.connect.createStatement();
			resulSet = statement.executeQuery(cmmd);
			while (resulSet.next()) {
				String assemblyOutput = resulSet.getString("output");
				String genomeSize = resulSet.getString("cisaGenomeSize");

				BufferedWriter bw = new BufferedWriter(new FileWriter(assemblyOutput + "/CISA/CISA.config"));
				bw.write("genome=" + genomeSize);
				bw.newLine();
				bw.write("infile=" + assemblyOutput + "/CISA/Merged.ctg.fa");
				bw.newLine();
				bw.write("outfile=" + assemblyOutput + "/CISA/cisa.ctg.fa");
				bw.newLine();
				bw.write("nucmer=/usr/bin/nucmer");
				bw.newLine();
				bw.write("R2_Gap=0.95");
				bw.newLine();
				bw.write("CISA=lib/CISA1.3");
				bw.newLine();
				bw.write("makeblastdb=/usr/bin/makeblastdb");
				bw.newLine();
				bw.write("blastn=/usr/bin/blastn");

				bw.flush();
				bw.close();

				String cisaCommand = "python2 " + "lib/CISA1.3/CISA.py " + assemblyOutput + "/CISA/CISA.config";
				CommandLine runCisa = CommandLine.parse(cisaCommand);
				DefaultExecutor rfExecutor = new DefaultExecutor();
				rfExecutor.execute(runCisa);

				PreparedStatement preparedStmt = null;
				String result_assembly = assemblyOutput + "/CISA/cisa.ctg.fa";
				preparedStmt = DatabaseConnection.connect.prepareStatement("UPDATE organism SET result_cisa= '"
						+ result_assembly + "' WHERE idproject=" + idproject + ";");
				preparedStmt.executeUpdate();

				checkCisaFile(idproject);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void checkCisaFile(int idproject) {
		try {
			Statement stm = null;
			String cmmdo;
			ResultSet rst;
			cmmdo = "SELECT * FROM organism WHERE idproject =" + idproject + ";";
			stm = DatabaseConnection.connect.createStatement();
			rst = stm.executeQuery(cmmdo);

			while (rst.next()) {
				String result_assembly = rst.getString("result_cisa");

				try {
					File a = new File(result_assembly);
					Thread.sleep(10000);
					if (a.exists()) {
						if (a.length() > 0) {
							System.out.println("CISA OK");
						} else {
							mergeFileRun(idproject);
							cisaFileRun(idproject);
						}

					} else {
						mergeFileRun(idproject);
						cisaFileRun(idproject);					}

				} catch (Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
