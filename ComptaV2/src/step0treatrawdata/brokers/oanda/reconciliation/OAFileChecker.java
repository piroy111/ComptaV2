package step0treatrawdata.brokers.oanda.reconciliation;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class OAFileChecker {

	protected OAFileChecker(OAReconcilationManager _sOAReconcilationManager) {
		pOAReconcilationManager = _sOAReconcilationManager;
	}
	
	/*
	 * Data
	 */
	private OAReconcilationManager pOAReconcilationManager;
	private LitUnFichierEnLignes pReadFile;
	
	/**
	 * 
	 */
	public final void initiate() {
		
	}
	
	/**
	 * Check the dates of the report and the positions are the same
	 */
	public final void run() {
		/*
		 * Load file from positions import
		 */
		String lDir = StaticDir.getIMPORT_BROKERS_OANDA_POSITIONS();
		String lSuffix = StaticNames.getBROKERS_OANDA_POSITIONS();
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		BasicFile lBasicFile = lBasicDir.getmBasicFile(BasicDateInt.getmToday());
		/*
		 * Load file from report import
		 */
		String lDirReport = StaticDir.getIMPORT_BROKERS_OANDA();
		String lSuffixReport = StaticNames.getBROKERS_OANDA();
		BasicDir lBasicDirReport = new BasicDir(lDirReport, lSuffixReport);
		BasicFile lBasicFileReport = lBasicDirReport.getmBasicFile(BasicDateInt.getmToday());
		/*
		 * Check dates are equals
		 */
		if (lBasicFile.getmDate() != lBasicFileReport.getmDate()) {
			BasicPrintMsg.error("You must put the files csv for the positions of OANDA and for the report of OANDA with the same date"
					+ "; Date report file= " + lBasicFileReport.getmNameFile()
					+ "; Date positions file= " + lBasicFile.getmNameFile());
		}
		/*
		 * Normal case --> we load the file
		 */
		pReadFile = lBasicFile.getmLitUnFichierEnLignes();
		if (!pReadFile.getmIsFichierLuCorrectement()) {
			BasicPrintMsg.error("File corrupted; File= " + lBasicFile.getmNameFile());
		}
	}

	/*
	 * Getters & Setters
	 */
	public final OAReconcilationManager getpOAReconcilationManager() {
		return pOAReconcilationManager;
	}
	public final LitUnFichierEnLignes getpReadFile() {
		return pReadFile;
	}
}
