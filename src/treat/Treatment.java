package treat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.exec.ExecuteException;

public class Treatment {
	public void treatRead(String read1, String read2, String output) throws ExecuteException, IOException, InterruptedException {
		String command = "lib/bbmap/repair.sh in1=" + read1 + " in2=" + read2 + " out1=" + output + "1_treated.fastq"
				+ " out2=" + output + "2_treated.fastq repair";
		System.out.println("Treating files");
		Process p = Runtime.getRuntime().exec(command);

		BufferedReader br;
		String linha;

		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		PrintWriter pw= new PrintWriter(new FileWriter(output + "/log.txt"));

		while ((linha= br.readLine()) != null) {
			pw.println(linha);
		}
		pw.close();
		p.waitFor();
		
	}
}
