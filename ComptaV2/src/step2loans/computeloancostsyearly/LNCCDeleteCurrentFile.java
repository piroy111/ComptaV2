package step2loans.computeloancostsyearly;

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

class LNCCDeleteCurrentFile {

	protected LNCCDeleteCurrentFile(LNCCManager _sLNCCManager) {
		pLNCCManager = _sLNCCManager;
	}
	
	/*
	 * Static
	 */
	private static int FIRST_DATE = 20200501;
	/*
	 * Data
	 */
	private int pDateCurrent;
	private LNCCManager pLNCCManager;
	private List<Integer> pListMissingDate;
	private Map<Integer, LNCCFile> pMapDateToLNCCFileMissing;
	
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * Delete current file
		 */
		pDateCurrent = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		String lDir = StaticDir.getTREATED_LOANS();
		BasicDir lBasicDir = new BasicDir(lDir, StaticNames.getTREATED_COSTS_LOANS_YEARLY());
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			if (lBasicFile.getmDate() >= pDateCurrent) {
				BasicFichiers.deleteFile(lDir + lBasicFile.getmNameFile());
			}
		}
		/*
		 * Compute the list of the missing files
		 */
		pListMissingDate = new ArrayList<>();
		pMapDateToLNCCFileMissing = new HashMap<>();
		int lDateStart = BasicDateInt.getmEndOfMonth(FIRST_DATE);
		for (int lDate = lDateStart; lDate <= pDateCurrent; lDate = BasicDateInt.getmPlusMonth(lDate, 1)) {
			if (!lBasicDir.getmMapDateToBasicFile().containsKey(lDate) || lDate >= pDateCurrent) {
				getpOrCreateLNCCFile(lDate);
			}
		}
		Collections.sort(pListMissingDate);
	}

	/**
	 * 
	 */
	public final void writeFiles() {
		for (LNCCFile lLNCCFile : pMapDateToLNCCFileMissing.values()) {
			lLNCCFile.writeFile();
		}
		BasicTime.sleep(25);
		for (LNCCFile lLNCCFile : pMapDateToLNCCFileMissing.values()) {
			lLNCCFile.loadBKTransactions();
		}
	}
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final LNCCFile getpOrCreateLNCCFile(int _sDate) {
		LNCCFile lLNCCFile = pMapDateToLNCCFileMissing.get(_sDate);
		if (lLNCCFile == null) {
			lLNCCFile = new LNCCFile(_sDate, pLNCCManager);
			pMapDateToLNCCFileMissing.put(_sDate, lLNCCFile);
			pListMissingDate.add(_sDate);
		}
		return lLNCCFile;
	}
	
	/*
	 * Data
	 */
	public final int getpDateCurrent() {
		return pDateCurrent;
	}
	public final LNCCManager getpLNCCManager() {
		return pLNCCManager;
	}
	public final List<Integer> getpListMissingDate() {
		return pListMissingDate;
	}
	public final Map<Integer, LNCCFile> getpMapDateToLNCCFileMissing() {
		return pMapDateToLNCCFileMissing;
	}
	
}
