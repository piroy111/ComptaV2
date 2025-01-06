package step2loans.computeloancostscurrencies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicTime;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class LNCYDeleteCurrentFile {

	protected LNCYDeleteCurrentFile(LNCYManager _sLNCYManager) {
		pLNCYManager = _sLNCYManager;
	}
	
	/*
	 * Static
	 */
	private static int FIRST_DATE = 20200501;
	/*
	 * Data
	 */
	private int pDateCurrent;
	private LNCYManager pLNCYManager;
	private List<Integer> pListMissingDate;
	private Map<Integer, LNCYFile> pMapDateToLNCYFileMissing;
	
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * Delete current file
		 */
		pDateCurrent = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		String lDir = StaticDir.getTREATED_LOANS();
		BasicDir lBasicDir = new BasicDir(lDir, StaticNames.getTREATED_COSTS_LOANS_CURRENCY());
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			if (lBasicFile.getmDate() >= pDateCurrent) {
				BasicFichiers.deleteFile(lDir + lBasicFile.getmNameFile());
			}
		}
		/*
		 * Compute the list of the missing files
		 */
		pListMissingDate = new ArrayList<>();
		pMapDateToLNCYFileMissing = new HashMap<>();
		int lDateStart = BasicDateInt.getmEndOfMonth(FIRST_DATE);
		for (int lDate = lDateStart; lDate <= pDateCurrent; lDate = BasicDateInt.getmPlusMonth(lDate, 1)) {
			if (!lBasicDir.getmMapDateToBasicFile().containsKey(lDate) || lDate >= pDateCurrent) {
				getpOrCreateLNCYFile(lDate);
			}
		}
		Collections.sort(pListMissingDate);
	}

	/**
	 * 
	 */
	public final void writeFiles() {
		for (LNCYFile lLNCYFile : pMapDateToLNCYFileMissing.values()) {
			lLNCYFile.writeFile();
		}
		BasicTime.sleep(25);
		for (LNCYFile lLNCYFile : pMapDateToLNCYFileMissing.values()) {
			lLNCYFile.loadBKTransactions();
		}
	}
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final LNCYFile getpOrCreateLNCYFile(int _sDate) {
		LNCYFile lLNCYFile = pMapDateToLNCYFileMissing.get(_sDate);
		if (lLNCYFile == null) {
			lLNCYFile = new LNCYFile(_sDate, pLNCYManager);
			pMapDateToLNCYFileMissing.put(_sDate, lLNCYFile);
			pListMissingDate.add(_sDate);
		}
		return lLNCYFile;
	}
	
	/*
	 * Data
	 */
	public final int getpDateCurrent() {
		return pDateCurrent;
	}
	public final LNCYManager getpLNCYManager() {
		return pLNCYManager;
	}
	public final List<Integer> getpListMissingDate() {
		return pListMissingDate;
	}
	public final Map<Integer, LNCYFile> getpMapDateToLNCYFileMissing() {
		return pMapDateToLNCYFileMissing;
	}
}
