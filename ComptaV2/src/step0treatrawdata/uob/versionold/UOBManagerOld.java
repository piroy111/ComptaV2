package step0treatrawdata.uob.versionold;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class UOBManagerOld {

	/**
	 * Read the files uploaded from the UOB web site<br>
	 * then write all the transactions into one output file<br>
	 * warn if there are some transactions that we don't know the designation yet (new type of cost, new client, etc.)<br>
	 * @deprecated
	 */
	public UOBManagerOld(boolean _sIsCom) {
		mUOBLoadFiles = new UOBLoadFiles(this);
		mListUOBTransaction = new ArrayList<UOBTransactionOld>();
		mMapDateToListUOBTransaction = new HashMap<Integer, List<UOBTransactionOld>>();
		mMapNumberToUOBAccount = new HashMap<Long, UOBAccountOld>();
		mUOBDesignationManager = new UOBDesignationManager2(this);
		mMapNameFileToUOBFile = new HashMap<String, UOBFileOld>();
	}
	
	/*
	 * Data
	 */
	private Map<Long, UOBAccountOld> mMapNumberToUOBAccount;
	private UOBLoadFiles mUOBLoadFiles;
	private Map<Integer, List<UOBTransactionOld>> mMapDateToListUOBTransaction;
	private List<UOBTransactionOld> mListUOBTransaction;
	private UOBDesignationManager2 mUOBDesignationManager;
	private boolean pIsCom;
	private Map<String, UOBFileOld> mMapNameFileToUOBFile;
	
	/**
	 * Load all files and put then into the objects of UOB
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, this.getClass().getSimpleName());
		mUOBLoadFiles.run();
		runUOBFile();
		Collections.sort(mListUOBTransaction);
		mUOBDesignationManager.run();
		writeAccountInFile();
	}
	
	/**
	 * @return Classic get or create
	 * @param _sNumber : number of the account
	 * @param _sCurrency : currency of the account
	 */
	public final UOBAccountOld getmOrCreateUOBAccount(long _sNumber, String _sCurrency) {
		UOBAccountOld lUOBAccount = mMapNumberToUOBAccount.get(_sNumber);
		if (lUOBAccount == null) {
			lUOBAccount = new UOBAccountOld(this, _sCurrency, _sNumber);
			mMapNumberToUOBAccount.put(_sNumber, lUOBAccount);
		}
		if (!lUOBAccount.getmCurrency().equals(_sCurrency)) {
			BasicPrintMsg.error("The currency of the account is not correct");
		}
		return lUOBAccount;
	}
	
	/**
	 * declare UOBTransacton
	 * @param _sUOBTransaction
	 */
	public final void declareNewUOBTransaction(UOBTransactionOld _sUOBTransaction) {
		int lDate = _sUOBTransaction.getmDate();
		List<UOBTransactionOld> lListUOBTransaction = mMapDateToListUOBTransaction.get(lDate);
		if (lListUOBTransaction == null) {
			lListUOBTransaction = new ArrayList<UOBTransactionOld>();
			mMapDateToListUOBTransaction.put(lDate, lListUOBTransaction);
		}
		lListUOBTransaction.add(_sUOBTransaction);
		if (!mListUOBTransaction.contains(_sUOBTransaction)) {
			mListUOBTransaction.add(_sUOBTransaction);
		}
	}
	
	/**
	 * Write a file with the list of all the transactions
	 */
	public final void writeAccountInFile() {
		List<String> lListLineToWrite = new ArrayList<String>();
		for (UOBTransactionOld lUOBTransaction : mListUOBTransaction) {
			/*
			 * Load data
			 */
			int lDate = lUOBTransaction.getmValueDate();
			String lComment = lUOBTransaction.getmComment();
			double lAmount = lUOBTransaction.getmDeposit() - lUOBTransaction.getmWithdrawal();
			String lCurrency = lUOBTransaction.getmUOBAccount().getmCurrency();
			double lAmountUSD = lUOBTransaction.getmAmountUSD();
			String lAccount = lUOBTransaction.getmAccount();
			String lCategory = lUOBTransaction.getmCategory();
			/*
			 * Write in file
			 * 
			 */
			String lLine = lDate
					+ "," + lComment
					+ "," + lCurrency
					+ "," + lAmount
					+ "," + lAccount
					+ "," + lCategory
					+ "," + lAmountUSD
					+ "," + Double.NaN;
			lListLineToWrite.add(lLine);
		}
		String lDir = StaticDir.getUOB_ALL_TRANSACTIONS();
		String lNameFile = StaticNames.getUOB_ALL_TRANSACTIONS();
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,Amount USD,BKPrice";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}

	/**
	 * @return classic get or create
	 * @param _sReadFile
	 */
	public final UOBFileOld getmOrCreateUOBFile(LitUnFichierEnLignes _sReadFile) {
		UOBFileOld lUOBFile = mMapNameFileToUOBFile.get(_sReadFile.getmNomFichier());
		if (lUOBFile == null) {
			lUOBFile = new UOBFileOld(_sReadFile);
			mMapNameFileToUOBFile.put(_sReadFile.getmNomFichier(), lUOBFile);
		}
		return lUOBFile;
	}
	
	/**
	 * runUOBFile
	 */
	private void runUOBFile() {
		/*
		 * Initiate
		 */
		List<UOBFileOld> lListUOBFile = new ArrayList<>(mMapNameFileToUOBFile.values());
		for (UOBFileOld lUOBFile : lListUOBFile) {
			lUOBFile.computeDate();
		}
		/*
		 * Sort files according to their date
		 */
		Collections.sort(lListUOBFile);
		/*
		 * Input the previous file for each UOBFile
		 */
		for (int lIdx = 0; lIdx < lListUOBFile.size(); lIdx++) {
			if (lIdx > 0) {
				lListUOBFile.get(lIdx).declareUOBFilePrevious(lListUOBFile.get(lIdx - 1));
			}
		}
		/*
		 * Compute final ledger of each file
		 */
		for (UOBFileOld lUOBFile : lListUOBFile) {
			lUOBFile.computeAscendingOrDescending();
			lUOBFile.computeFinalLedger();
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final Map<Long, UOBAccountOld> getmMapNumberToUOBAccount() {
		return mMapNumberToUOBAccount;
	}
	public final UOBLoadFiles getmUOBLoadFiles() {
		return mUOBLoadFiles;
	}
	public final Map<Integer, List<UOBTransactionOld>> getmMapDateToListUOBTransaction() {
		return mMapDateToListUOBTransaction;
	}
	public final List<UOBTransactionOld> getmListUOBTransaction() {
		return mListUOBTransaction;
	}
	public final boolean getpIsCom() {
		return pIsCom;
	}
	public final Map<String, UOBFileOld> getmMapNameFileToUOBFile() {
		return mMapNameFileToUOBFile;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
