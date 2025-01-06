package step2loans.computeloancostsyearly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;

class LNCCFile {

	protected LNCCFile(int _sDate, LNCCManager _sLNCCManager) {
		pDate = _sDate;
		pLNCCManager = _sLNCCManager;
		/*
		 * 
		 */
		pListDate = new ArrayList<>();
		int lDateStart = BasicDateInt.getmFirstDayOfMonth(pDate);
		for (int lDate = lDateStart; lDate <= pDate; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			pListDate.add(lDate);
		}
		pListLineToWrite = new ArrayList<>();
		pNameFile = pDate + StaticNames.getTREATED_COSTS_LOANS_YEARLY();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private LNCCManager pLNCCManager;
	private List<Integer> pListDate;
	private List<String> pListLineToWrite;
	private String pNameFile;
	
	/**
	 * 
	 * @param _sDate
	 * @param _sCostLoan
	 */
	public final void addNewLineToWrite(int _sDate, double _sCostLoan, String _sNameMetal) {
		String lLinePRoy = _sDate 
				+ "," + "Loan cost yearly for " + _sNameMetal
				+ "," + "USD"
				+ "," + _sCostLoan
				+ "," + BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress()
				+ "," + StaticBKIncome.getLOAN_COST()
				+ "," + "NaN";
		String lLineBunker = _sDate 
				+ "," + "Loan cost yearly for " + _sNameMetal
				+ "," + "USD"
				+ "," + (-_sCostLoan)
				+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
				+ "," + StaticBKIncome.getLOAN_COST()
				+ "," + "NaN";
		pListLineToWrite.add(lLinePRoy);
		pListLineToWrite.add(lLineBunker);
	}
	
	/**
	 * 
	 */
	public final void writeFile() {
		Collections.sort(pListLineToWrite);
		String lDir = StaticDir.getTREATED_LOANS();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		BasicFichiers.writeFile(lDir, pNameFile, lHeader, pListLineToWrite);
		BasicPrintMsg.display(this, "File written= '" + lDir + pNameFile + "'");
	}
	
	/**
	 * 
	 */
	public final void loadBKTransactions() {
		String lDir = StaticDir.getTREATED_LOANS();
		LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir + pNameFile, true);
		pLNCCManager.getpBKTransactionManager().readFile(lReadFile, true);
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final LNCCManager getpLNCCManager() {
		return pLNCCManager;
	}
	public final List<Integer> getpListDate() {
		return pListDate;
	}
	
}
