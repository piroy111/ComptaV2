package step1loadtransactions.brokers.interactivebrokers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataNav;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReport;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.transactions.BKTransaction;

public class BKIBCheckMarkToMarketAndNAV {

	protected BKIBCheckMarkToMarketAndNAV(BKIBManager _sBKIBManager) {
		pBKIBManager = _sBKIBManager;
	}

	/*
	 * Data
	 */
	private BKIBManager pBKIBManager;
	private double pBKMtM;
	private double pNavMtM;
	private double pNav;
	private double pBKTransactionsInterests;
	private double pBKTransactionsCommissions;
	private double pBKTransactionsDeposits;
	private Set<String> pSetKeysCalled;
	private boolean pIsWriteFileDebug;

	/**
	 * 
	 */
	protected final void run() {
		BasicPrintMsg.display(this, null);
		/*
		 * Compute and check for the last date
		 */
		pIsWriteFileDebug = true;
		initiate();
		computeMtMFromNav();
		computeBKTransactionsIntDepositComm();
		computeMtMFromBKTransactions();
		check();
		BasicPrintMsg.display(this, "Reconciliation of the NAV successful !!! The sum of the BKTransactions gives the NAV of the last IB report");
	}

	/**
	 * 
	 */
	private void initiate() {
		pSetKeysCalled = new HashSet<>();
	}

	/**
	 * MtM = NAV - interests - deposits - commissions
	 */
	private void computeMtMFromNav() {
		/*
		 * NAV
		 */
		IBReport lIBReport = pBKIBManager.getpBKIBLoadIBFiles().getpIBReportLast();
		int lDate = lIBReport.getpDateStop();
		IBDataNav lIBDataNav = lIBReport.getpIBDataManager().getpMapDateToIBDataNav().get(lDate);
		pNav = lIBDataNav.getpNav();
		/*
		 * Deduce MtM from NAV
		 */
		BKIBCheckCash lBKIBCheckCash = pBKIBManager.getpBKIBCheckCash();
		pNavMtM = pNav - lBKIBCheckCash.getpInterests() - lBKIBCheckCash.getpCommissions() - lBKIBCheckCash.getpDeposits();
	}

	/**
	 * computeMtMFromBKTransactions
	 */
	private void computeMtMFromBKTransactions() {
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>();
		/*
		 * Compute the MtM from FOREX real
		 */
		double lBKMtMForexReal = 0.;
		lListBKTransaction = getpListBKTransaction(IBStatic.getCOMMENT_FOREX());
		for (BKTransaction  lBKTransaction : lListBKTransaction) {
			BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKTransaction.getpBKAsset());
			lBKMtMForexReal += lBKTransaction.getpQuantity() * lBKIBClosePrice.getpValueWithInversion();
		}
		/*
		 * Compute the MtM from swaps
		 */
		double lBKMtMSwaps = 0.;
		lListBKTransaction.clear();
		lListBKTransaction.addAll(getpListBKTransaction(IBStatic.getCOMMENT_FOREX_SWAPS()));
		lListBKTransaction.addAll(getpListBKTransaction(IBStatic.getCOMMENT_METAL_SWAPS()));
		for (BKTransaction  lBKTransaction : lListBKTransaction) {
			BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKTransaction.getpBKAsset());
			BKIBClosePrice lBKIBClosePriceCurrency = new BKIBClosePrice(pBKIBManager, BKAssetManager.getpAndCheckBKAsset(lBKIBClosePrice.getpBKAssetCurrency()));
			double lQuantity = lBKTransaction.getpQuantity();
			double lPriceMtM = lBKIBClosePrice.getpValue();
			double lPriceExec = lBKTransaction.getpBKPrice();
			if (lBKIBClosePrice.getpIsInverted()) {
				lQuantity = -lQuantity * lPriceExec;
				lPriceExec = 1 / lPriceExec;				
			}
			lBKMtMSwaps += lQuantity * (lPriceMtM - lPriceExec) * lBKIBClosePriceCurrency.getpValueWithInversion();
		}
		/*
		 * Compute MtM from futures (oil)
		 */
		double lBKMtMOil = 0.;
		lListBKTransaction = getpListBKTransaction(IBStatic.getCOMMENT_OIL());
		for (BKTransaction  lBKTransaction : lListBKTransaction) {
			BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKTransaction.getpBKAsset());
			lBKMtMOil += lBKTransaction.getpQuantity() 
					* (lBKIBClosePrice.getpValueWithInversion() - lBKTransaction.getpBKPrice());
		}
		/*
		 * final MtM
		 */
		pBKMtM = lBKMtMForexReal + lBKMtMSwaps + lBKMtMOil;		
	}

	/**
	 * 
	 * @param _sComment
	 * @return
	 */
	private List<BKTransaction> getpListBKTransaction(String _sComment) {
		pSetKeysCalled.add(_sComment);
		return pBKIBManager.getpMapCommentToListBKTransaction().get(_sComment);
	}


	/**
	 * 
	 */
	private void computeBKTransactionsIntDepositComm() {
		pBKTransactionsInterests = getAndComputeSumBKTransaction(IBStatic.getCOMMENT_INTERESTS());
		pBKTransactionsCommissions = getAndComputeSumBKTransaction(IBStatic.getCOMMENT_COMMISSIONS());
		pBKTransactionsDeposits = getAndComputeSumBKTransaction(IBStatic.getCOMMENT_DEPOSITS());
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	private double getAndComputeSumBKTransaction(String _sComment) {
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Sum all the BKTransactions of the item
		 */
		double lSum = 0.;
		List<BKTransaction> lListBKTransaction = getpListBKTransaction(_sComment);
		for (BKTransaction  lBKTransaction : lListBKTransaction) {
			BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKTransaction.getpBKAsset());
			double lValueUSD = lBKTransaction.getpQuantity() * lBKIBClosePrice.getpValueWithInversion();
			lSum += lValueUSD;
			lListLineToWrite.add(lBKTransaction.getpDate() 
					+ "," + lBKTransaction.getpComment() 
					+ "," + lBKTransaction.getpQuantity() 
					+ "," + lBKIBClosePrice.getpValueWithInversion()
					+ "," + lValueUSD);
		}
		/*
		 * Write file debug
		 */
		if (pIsWriteFileDebug) {
			String lDir = StaticDir.getOUTPUT_DEBUG();
			String lNameFile = BasicDateInt.getmToday() + "_" + "Debug_" + _sComment + ".csv";
			String lHeader = "Date,Comment,Quantity,Forex,ValueUSD";
			BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
		}
		return lSum;
	}

	/**
	 * 
	 */
	private void check() {
		/*
		 * Check there is not a new instrument not taken into account
		 */
		for (String lComment : pBKIBManager.getpMapCommentToListBKTransaction().keySet()) {
			if (!pSetKeysCalled.contains(lComment)) {
				BasicPrintMsg.error("New product not taken into account in the method 'computeMtMFromBKTransactions()' above, line 80");
			}
		}
		/*
		 * Check consistency of interests, deposits, commissions --> The easy part 
		 */
		String lCheckStr = "'Report MtM == BKTransactions MtM' (where 'MtM' means Mark to Market)";
		BKIBCheckCash lBKIBCheckCash = pBKIBManager.getpBKIBCheckCash();
		if (getpIsequal(lBKIBCheckCash.getpInterests(), pBKTransactionsInterests)
				&& getpIsequal(lBKIBCheckCash.getpDeposits(), pBKTransactionsDeposits)
				&& getpIsequal(lBKIBCheckCash.getpCommissions(), pBKTransactionsCommissions)) {
			BasicPrintMsg.display(this, "interests, deposits, commissions --> all good");
		} else {
			String lMsgError = "Failed " + lCheckStr
					+ "\n   Last report= '" + pBKIBManager.getpBKIBLoadIBFiles().getpIBReportLast().getpIBFile().getpReadFile().getmNomFichier() + "'"
					+ "\n   Report MtM= NAV - interests - deposits - commissions"
					+ "\n      Nav= " + pNav + " $"
					+ "\n      interests= " + lBKIBCheckCash.getpInterests() + " $"
					+ "\n      deposits= " + lBKIBCheckCash.getpDeposits() + " $"
					+ "\n      commissions= " + lBKIBCheckCash.getpCommissions() + " $"
					+ "\n"
					+ "\n   BKTransactions MtM= BKTransactions MtM ForexReal + BKTransactions MtM Swaps + BKTransactions MtM Futures Oil"
					+ "\n      interests= " + pBKTransactionsInterests + " $"
					+ "\n      deposits= " + pBKTransactionsDeposits + " $"
					+ "\n      commissions= " + pBKTransactionsCommissions + " $"
					+ "\n"
					+ getErrorMsg("interests", lBKIBCheckCash.getpInterests(), pBKTransactionsInterests)
					+ getErrorMsg("deposits", lBKIBCheckCash.getpDeposits(), pBKTransactionsDeposits)
					+ getErrorMsg("commissions", lBKIBCheckCash.getpCommissions(), pBKTransactionsCommissions)
					;
			BasicPrintMsg.error(lMsgError);
		}
		/*
		 * Check consistency of data
		 */
		if (getpIsequal(pBKMtM, pNavMtM)) {
			BasicPrintMsg.display(this, "Done " + lCheckStr + "; Value MtM= " + pBKMtM + " $");
		} else {
			String lMsgError = "Failed " + lCheckStr
					+ "\n   Report MtM= " + pNavMtM + " $"
					+ "\n   BKTransactions MtM= " + pBKMtM + " $"
					+ "\n   Error= " + (pNavMtM - pBKMtM) + " $"
					+ "\n"
					+ "\n   Last report= '" + pBKIBManager.getpBKIBLoadIBFiles().getpIBReportLast().getpIBFile().getpReadFile().getmNomFichier() + "'"
					+ "\n   Report MtM= NAV - interests - deposits - commissions"
					+ "\n      Nav= " + pNav + " $"
					+ "\n      interests= " + lBKIBCheckCash.getpInterests() + " $"
					+ "\n      deposits= " + lBKIBCheckCash.getpDeposits() + " $"
					+ "\n      commissions= " + lBKIBCheckCash.getpCommissions() + " $"
					+ "\n"
					+ "\n   If interests, deposits and commissions match, it means the error comes from the valorisation MtM of the instruments in BKTransactions"
					;
			BasicPrintMsg.error(lMsgError);
		}
	}

	/**
	 * 
	 * @param _sX1
	 * @param _sX2
	 * @return
	 */
	private boolean getpIsequal(double _sX1, double _sX2) {
		return AMNumberTools.isSmallerOrEqual(Math.abs(_sX1 - _sX2), IBStatic.getERROR_ACCEPTED());
	}

	/**
	 * 
	 * @param _sName
	 * @param _sFromReport
	 * @param _sFromBKTransactions
	 * @return
	 */
	private String getErrorMsg(String _sName, double _sFromReport, double _sFromBKTransactions) {
		double lError = Math.abs(_sFromReport - _sFromBKTransactions);
		if (AMNumberTools.isSmallerOrEqual(lError, IBStatic.getERROR_ACCEPTED())) {
			return "\n   " + _sName + " match -> Ok";
		} else {
			return "\n   !!! " + _sName + " error= " + lError;
		}
	}













}
