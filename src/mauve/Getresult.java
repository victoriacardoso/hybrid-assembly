package mauve;

import java.io.BufferedReader;
import java.io.FileReader;

public class Getresult {
	public String GetResult(String path) {
		String x="";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			while (br.ready()) {
				String line=br.readLine();
				if (line.startsWith("C:")) {
					x=line;
					line=br.readLine();
					if (line.startsWith("shown")) {
						String [] p =x.split(":");
						x=p[p.length-1];
						break;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		
		return x;
		
	}

}
