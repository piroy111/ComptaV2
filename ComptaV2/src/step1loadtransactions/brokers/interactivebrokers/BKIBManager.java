package step1loadtransactions.brokers.interactivebrokers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.brokers.interactivebrokers.IBManager;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;

public class BKIBManager {

	public static void main(String[] _sArgs) {
		IBManager lIBManager = new IBManager();
		lIBManager.run();
		new BKIBManager(new BKTransactionManager(), lIBManager).run();
	}
	
	
	public BKIBManager(BKTransactionManager _sBKTransactionManager, IBManager _sIBManager) {
		pBKTransactionManager = _sBKTransactionManager;
		pIBManager = _sIBManager;
		/*
		 * Initiate
		 */
		pBKIBCheckCash = new BKIBCheckCash(this);
		pBKIBCheckMarkToMarket = new BKIBCheckMarkToMarketAndNAV(this);
		pBKIBLoadIBFiles = new BKIBLoadIBFiles(this);
	}

	/*
	 * Data
	 */
	private BKTransactionManager pBKTransactionManager;
	private IBManager pIBManager;
	private Map<String, List<BKTransaction>> pMapCommentToListBKTransaction;
	private BKIBCheckCash pBKIBCheckCash;
	private BKIBCheckMarkToMarketAndNAV pBKIBCheckMarkToMarket;
	private BKIBLoadIBFiles pBKIBLoadIBFiles;

	/**
	 * Check all the BKTransactions give back the NAV of the Interactive Brokers report
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Check all the BKTransactions give back the NAV of the Interactive Brokers report");
		/*
		 * Load
		 */
		loadListBKTransaction();
		pBKIBLoadIBFiles.loadIBReports();
		/*
		 * Check first the simple cash flows. It would allow to detect obvious mistakes
		 */
		pBKIBCheckCash.run();
		/*
		 * Check the Mark to market and the NAV: this is a more complex check 
		 */		
		pBKIBCheckMarkToMarket.run();
	}

	/**
	 * 
	 */
	private void loadListBKTransaction() {
		/*
		 * Load
		 */
		List<BKTransaction> lListBKTransactionAll = new ArrayList<BKTransaction>();
		lListBKTransactionAll.addAll(pBKTransactionManager.readFile(StaticDir.getTREATED_BROKERS_IB(), false));
		/*
		 * Create Map
		 */
		pMapCommentToListBKTransaction = new HashMap<String, List<BKTransaction>>();
		for (BKTransaction lBKTransaction : lListBKTransactionAll) {
			String lComment = lBKTransaction.getpComment().split(";")[0];
			List<BKTransaction> lListBKTransaction = pMapCommentToListBKTransaction.get(lComment);
			if (lListBKTransaction == null) {
				lListBKTransaction = new ArrayList<>();
				pMapCommentToListBKTransaction.put(lComment, lListBKTransaction);
			}
			lListBKTransaction.add(lBKTransaction);
		}
		/*
		 * Write file in debug
		 */
		String lDir = StaticDir.getOUTPUT_DEBUG();
		String lNameFile = IBStatic.getNAME_FILE_DEBUG_BKTRANSACTION();
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKTransaction lBKTransaction : lListBKTransactionAll) {
			String lLine = lBKTransaction.getpDate()
					+ "," + lBKTransaction.getpBKAsset()
					+ "," + lBKTransaction.getpQuantity()
					+ "," + lBKTransaction.getpBKPrice()
					+ "," + lBKTransaction.getpComment()
					+ "," + lBKTransaction.getpBKIncome()
					+ "," + lBKTransaction.getpValueUSD()
					+ "," + lBKTransaction.getpFileNameOrigin()
					+ "," + lBKTransaction.getpBKAccount();
			lListLineToWrite.add(lLine);
		}
		String lHeader = "Date,Asset,Quantity,Price,Comment,BKIncome,ValueUSD,FileNameOrigin,BKAccount";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		BasicPrintMsg.display(this, "File debug written= " + lDir + lNameFile);
	}

	/*
	 * Getters & Setters
	 */
	public final Map<String, List<BKTransaction>> getpMapCommentToListBKTransaction() {
		return pMapCommentToListBKTransaction;
	}
	public final BKIBCheckCash getpBKIBCheckCash() {
		return pBKIBCheckCash;
	}
	public final IBManager getpIBManager() {
		return pIBManager;
	}
	public final BKIBLoadIBFiles getpBKIBLoadIBFiles() {
		return pBKIBLoadIBFiles;
	}












}
