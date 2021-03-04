package log;

import java.io.IOException;
import java.sql.SQLException;

import cisa.Cisa;
import mauve.Ordering;
import megahit.PairedRead;
import rast.Rast;
import screen.CustomPanel;
import spades.SingleRead;

public class Tools {
	public void runSingleMegahit(int idproject, CustomPanel customPanel, int progress) {
		megahit.SingleRead megahitSingle = new megahit.SingleRead();
		megahitSingle.runMegahit(idproject);
		customPanel.addProgress(progress);
	}
	public void runPareadMegahit(int idproject, CustomPanel customPanel, int progress) {
		PairedRead paired = new PairedRead();
		paired.runMegahit(idproject);
		customPanel.addProgress(progress);
	}
	public void runSingleSPAdes(int idproject, CustomPanel customPanel, int progress) {
		SingleRead single = new SingleRead();
		single.runSpades(idproject);
		customPanel.addProgress(progress);
	}
	public void runPareadSPAdes(int idproject, CustomPanel customPanel, int progress) {
		spades.PairedRead pairedspades = new spades.PairedRead();
		pairedspades.runSpades(idproject);
		customPanel.addProgress(progress);
	}
	public void runCisa(int idproject, CustomPanel customPanel, int progress) {
		Cisa cisa = new Cisa();
		cisa.mergeFileRun(idproject);
		cisa.cisaFileRun(idproject);
		customPanel.addProgress(progress);
	}
	public void runMauve(int idproject, CustomPanel customPanel, int progress) throws SQLException, IOException {
		Ordering ordering = new Ordering();
		ordering.OrderContigs(idproject);
		customPanel.addProgress(progress);
	}
	public void runRast(int idproject, CustomPanel customPanel, int progress) throws SQLException, IOException {
		Rast rast = new Rast();
		rast.submitRast(idproject);
		customPanel.addProgress(progress);
	}
}
