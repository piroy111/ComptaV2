package step2loans.computeloancostscurrencies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.accounts.BKAccountManager;

public class LNCYFile {

	protected LNCYFile(int _sDate, LNCYManager _sLNCYManager) {
		pDate = _sDate;
		pLNCYManager = _sLNCYManager;
		/*
		 * 
		 */
		pListDate = new ArrayList<>();
		int lDateStart = BasicDateInt.getmFirstDayOfMonth(pDate);
		for (int lDate = lDateStart; lDate <= pDate; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			pListDate.add(lDate);
		}
		pListLineToWrite = new ArrayList<>();
		pListLineToWriteReport = new ArrayList<>();
		pNameFile = pDate + StaticNames.getTREATED_COSTS_LOANS_CURRENCY();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private LNCYManager pLNCYManager;
	private List<Integer> pListDate;
	private List<String> pListLineToWrite;
	private String pNameFile;
	private List<String> pListLineToWriteReport;
	
	/**
	 * 
	 * @param _sDate
	 * @param _sCostLoan
	 */
	public final void addNewLineToWrite(int _sDate, double _sCostLoan, String _sNameCurrency) {
		if (AMNumberTools.isNaNOrZero(_sCostLoan)) {
			return;
		}
		String lLinePRoy = _sDate 
				+ "," + "Loan cost for currency lent to Bunker " + _sNameCurrency
				+ "," + "USD"
				+ "," + _sCostLoan
				+ "," + BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress()
				+ "," + StaticBKIncome.getLOAN_COST_CURRENCY()
				+ "," + "NaN";
		String lLineBunker = _sDate 
				+ "," + "Loan cost for currency lent to Bunker " + _sNameCurrency
				+ "," + "USD"
				+ "," + (-_sCostLoan)
				+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
				+ "," + StaticBKIncome.getLOAN_COST_CURRENCY()
				+ "," + "NaN";
		pListLineToWrite.add(lLinePRoy);
		pListLineToWrite.add(lLineBunker);
	}
	
	/**
	 * 
	 * @param _sDate
	 * @param _sHoldingProy
	 * @param _sHoldingBunker
	 * @param _sLoan
	 * @param _sLoanUSD
	 * @param _sCostLoanPercent
	 * @param _sCostLoanUSD
	 */
	public final void addNewLineToWriteReport(int _sDate, String _sNameBKAsset, 
			double _sHoldingProy, double _sHoldingCapital,
			double _sHoldingBunker, double _sLoan, double _sLoanUSD, 
			double _sCostLoanPercent, double _sCostLoanUSD) {
		String lLine = _sDate
				+ "," + _sNameBKAsset
				+ "," + _sHoldingProy
				+ "," + _sHoldingCapital
				+ "," + _sHoldingBunker
				+ "," + _sLoan
				+ "," + _sLoanUSD
				+ "," + (-_sCostLoanPercent)
				+ "," + (-_sCostLoanUSD);
		pListLineToWriteReport.add(lLine);
	}
	
	/**
	 * 
	 */
	public final void writeFile() {
		/*
		 * Write file transactions
		 */
		Collections.sort(pListLineToWrite);
		String lDir = StaticDir.getTREATED_LOANS();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		BasicFichiers.writeFile(lDir, pNameFile, lHeader, pListLineToWrite);
		BasicPrintMsg.display(this, "File written= '" + lDir + pNameFile + "'");
		/*
		 * Write file report
		 */
		String lDirReport = StaticDir.getOUTPUT_BKLOANS_CURRENCIES();
		String lHeaderReport = "Date,Currency,Holding PRoy in Bunker,Capital,UOB account ledger,Loan in currency,Loan in US$,Cost load in %,Cost of loan in US$";
		String lFileNameWithDate = pDate + StaticNames.getOUTPUT_BKLOANS_CURRENCIES();
		String lFileNameCurrent = StaticNames.getOUTPUT_BKLOANS_CURRENCIES().substring(1);
		BasicFichiers.writeFile(lDirReport, lFileNameWithDate, lHeaderReport, pListLineToWriteReport);
		BasicFichiers.writeFile(lDirReport, lFileNameCurrent, lHeaderReport, pListLineToWriteReport);
	}
	
	/**
	 * 
	 */
	public final void loadBKTransactions() {
		String lDir = StaticDir.getTREATED_LOANS();
		LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir + pNameFile, true);
		pLNCYManager.getpBKTransactionManager().readFile(lReadFile, true);
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final LNCYManager getpLNCYManager() {
		return pLNCYManager;
	}
	public final List<Integer> getpListDate() {
		return pListDate;
	}
}
