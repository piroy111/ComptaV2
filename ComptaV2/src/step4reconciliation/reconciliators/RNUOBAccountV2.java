package step4reconciliation.reconciliators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDate;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step4reconciliation.main.RNMain;
import uob.step2loaduobfiles.files.UOBFile;

public class RNUOBAccountV2 {

	public RNUOBAccountV2(RNMain _sRNMain) {
		pRNMain = _sRNMain;
	}
	
	/*
	 * Data
	 */
	private RNMain pRNMain;
	
	/**
	 * Write a file with the final ledgers of the latest UOBFile
	 */
	public final void run() {
		/*
		 * Find out the latest files
		 */
		int lDateMax = -1;
		List<UOBFile> lListUOBFile = new ArrayList<UOBFile>();
		for (UOBFile lUOBFile : pRNMain.getmUOBMainManager().getmUOBAccountLoader()
				.getmUOBFileManager().getmMapNameFileToUOBFile().values()) {
			/*
			 * Ignore if later than global DATE_MAX
			 */
			if (lUOBFile.getmDate() > StaticDate.getDATE_MAX()) {
				continue;
			}
			/*
			 * Reset date max
			 */
			if (lUOBFile.getmDate() > lDateMax) {
				lListUOBFile.clear();
				lDateMax = lUOBFile.getmDate();
			}
			/*
			 * Store file
			 */
			if (lUOBFile.getmDate() == lDateMax) {
				lListUOBFile.add(lUOBFile);
			}
		}
		/*
		 * 
		 */
		TreeMap<String, Double> lMapCurrencyToLedger = new TreeMap<String, Double>();
		Map<String, String> lMapCurrencyToFiles = new HashMap<String, String>();
		for (UOBFile lUOBFile : lListUOBFile) {
			String lCurrency = lUOBFile.getmCurrency();
			Double lLedger;
			String lFiles;
			if (lMapCurrencyToLedger.containsKey(lCurrency)) {
				lLedger = lMapCurrencyToLedger.get(lCurrency);
				lFiles = lMapCurrencyToFiles.get(lCurrency) + "; ";
			}else {
				lLedger = 0.;
				lFiles = "";
			}
			lLedger += lUOBFile.getmFinalLedger();
			lFiles += lUOBFile.getmNameFile();
			lMapCurrencyToLedger.put(lCurrency, lLedger);
			lMapCurrencyToFiles.put(lCurrency, lFiles);
		}
		/*
		 * Prepare lines to write
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (String lCurrency : lMapCurrencyToLedger.keySet()) {
			String lLine = lCurrency 
					+ "," + lMapCurrencyToFiles.get(lCurrency) 
					+ "," + lMapCurrencyToLedger.get(lCurrency);
			lListLineToWrite.add(lLine);
		}
		/*
		 * Write File
		 */
		String lHeader = "Currency,File(s),Final ledger in UOB files";
		String lDir = StaticDir.getOUTPUT_RECONCILIATION();;
		String lNameFile = BasicDateInt.getmToday() + StaticNames.getOUTPUT_LEDGERS_UOB();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	
	
	
	
}
