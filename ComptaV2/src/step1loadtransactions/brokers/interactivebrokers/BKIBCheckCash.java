package step1loadtransactions.brokers.interactivebrokers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataCash;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataNav;
import step0treatrawdata.brokers.interactivebrokers.data.IBDataTrade;
import step0treatrawdata.brokers.interactivebrokers.ibstatic.IBStatic;
import step0treatrawdata.brokers.interactivebrokers.reports.IBReport;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.transactions.BKTransaction;

public class BKIBCheckCash {

	protected BKIBCheckCash(BKIBManager _sBKIBManager) {
		pBKIBManager = _sBKIBManager;
	}

	/*
	 * Data
	 */
	private BKIBManager pBKIBManager;
	private double pInterests;
	private double pCommissions;
	private double pDeposits;
	private double pBKInterests;
	private double pBKCommissions;
	private double pBKDeposits;
	private List<String> pListLineToWriteFromBKTransactions;
	private List<String> pListLineToWriteFromIBReport;

	/**
	 * Compute the cash component from the BKTransactions and from the IBReports
	 */
	protected final void run() {
		BasicPrintMsg.display(this, null);
		/*
		 * Debug
		 */
		initiateDebug();
		/*
		 * Compute data
		 */
		computeDataFromBKTransactions();
		computeDataFromIBReports();
		/*
		 * Debug
		 */
		writeFilesDebug();
		/*
		 * Check equality
		 */
		check(IBStatic.getCOMMENT_INTERESTS(), pBKInterests, pInterests);
		check(IBStatic.getCOMMENT_COMMISSIONS(), pBKCommissions, pCommissions);
		check(IBStatic.getCOMMENT_DEPOSITS(), pBKDeposits, pDeposits);
	}

	/**
	 * Check the data of the BKTransactions is the same as the ones of the report
	 */
	private void check(String _sName, double _sFromBK, double _sFromReport) {
		if (AMNumberTools.isSmallerStrict(Math.abs(_sFromBK - _sFromReport), 0.001)) {
			BasicPrintMsg.display(this, "Done report==BKTransactions for '" + _sName + "'; Value= " + _sFromBK + " $");
		} else {
			BasicPrintMsg.error("Failed report==BKTransactions for '" + _sName + "'"
					+ "\n _sFromBK= " + _sFromBK + " $"
					+ "\n _sFromReport= " + _sFromReport + " $"
					+ "\n Difference= " + (_sFromBK - _sFromReport) + " $");
		}
	}

	/**
	 * From BKTransactions
	 */
	private void computeDataFromBKTransactions() {
		pBKInterests = sumBKTransaction(pBKIBManager.getpMapCommentToListBKTransaction().get(IBStatic.getCOMMENT_INTERESTS()));
		pBKCommissions = sumBKTransaction(pBKIBManager.getpMapCommentToListBKTransaction().get(IBStatic.getCOMMENT_COMMISSIONS()));
		pBKDeposits = sumBKTransaction(pBKIBManager.getpMapCommentToListBKTransaction().get(IBStatic.getCOMMENT_DEPOSITS()));
	}

	/**
	 * @return
	 * @param _sListBKTransaction
	 */
	private double sumBKTransaction(List<BKTransaction> _sListBKTransaction) {
		double lSum = 0.;
		for (BKTransaction lBKTransaction : _sListBKTransaction) {
			/*
			 * Get price used in NAV of report
			 */
			BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKTransaction.getpBKAsset());
			double lQuantity = lBKTransaction.getpQuantity();
			double lPrice;
			if (lBKIBClosePrice.getpIsInverted()) {
				lPrice = 1 / lBKIBClosePrice.getpValue();
			} else {
				lPrice = lBKIBClosePrice.getpValue();
			}
			/*
			 * Add to sum
			 */
			double lAdd = lQuantity * lPrice;
			if (AMNumberTools.isNaNOrZero(lAdd)) {
				BasicPrintMsg.error("Error");
			}
			lSum += lAdd;
			/*
			 * Write in file debug
			 */
			addNewLineToWriteFromBKTransactions(lBKTransaction.getpDate(), lBKTransaction.getpComment(), lQuantity, lPrice, lAdd);
		}
		return lSum;
	}

	/**
	 * compute from report
	 */
	private void computeDataFromIBReports() {
		/*
		 * Initiate
		 */
		pCommissions = 0.;
		pDeposits = 0.;
		/*
		 * The interests taken from the NAV should only be from the last report
		 */
		IBReport IBReportLast = pBKIBManager.getpBKIBLoadIBFiles().getpIBReportLast();
		int lDateLast = IBReportLast.getpDateStop();
		IBDataNav lIBDataNavLast = IBReportLast.getpIBDataManager().getpMapDateToIBDataNav().get(lDateLast);
		pInterests = lIBDataNavLast.getpInterests();
		/*
		 * Loop over all the continuous reports
		 */
		for (IBReport lIBReport : pBKIBManager.getpBKIBLoadIBFiles().getpListIBReportContinuous()) {
			/*
			 * Data from CASH
			 */
			Map<String, IBDataCash> lMapSymbolToIBDataCash = lIBReport.getpIBDataManager().getpMapSymbolToIBDataCash();
			for (String lSymbol : lMapSymbolToIBDataCash.keySet()) {
				/*
				 * Load close price
				 */
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lSymbol);
				BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKAsset);
				double lValue = lBKIBClosePrice.getpValueWithInversion();
				/*
				 * Add up
				 */
				IBDataCash lIBDataCash = lMapSymbolToIBDataCash.get(lSymbol);
				pInterests += lValue * lIBDataCash.getpBrokerInterests();
				pDeposits += lValue * lIBDataCash.getpDeposits();
				pCommissions += lValue * lIBDataCash.getpOtherFees();
				/*
				 * Write other file debug
				 */
				addNewLineToWriteFromIBReport(lIBDataCash.getpDate(), "pInterests", lIBReport.toString(), lIBDataCash.getpBrokerInterests(), lValue);
				addNewLineToWriteFromIBReport(lIBDataCash.getpDate(), "pDeposits", lIBReport.toString(), lIBDataCash.getpDeposits(), lValue);
				addNewLineToWriteFromIBReport(lIBDataCash.getpDate(), "pCommissions", lIBReport.toString(), lIBDataCash.getpOtherFees(), lValue);
			}
			/*
			 * Data from trades
			 */
			for (IBDataTrade lIBDataTrade : lIBReport.getpIBDataManager().getpMapIDToIBDataTrade().values()) {
				/*
				 * Load close price for commissions
				 */
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lIBDataTrade.getpCommissionsCurrency());
				BKIBClosePrice lBKIBClosePrice = new BKIBClosePrice(pBKIBManager, lBKAsset);
				double lCurrencyPrice = lBKIBClosePrice.getpValueWithInversion();
				/*
				 * Add up commissions
				 */
				pCommissions += lIBDataTrade.getpCommissions() * lCurrencyPrice;
				/*
				 * Write other file debug
				 */
				addNewLineToWriteFromIBReport(lIBDataTrade.getpDate(), "pCommissions from trade", lIBReport.toString(), lIBDataTrade.getpCommissions(), lCurrencyPrice);
			}
		}
	}

	/**
	 * 
	 */
	private void initiateDebug() {
		pListLineToWriteFromBKTransactions = new ArrayList<>();
		pListLineToWriteFromIBReport = new ArrayList<>();
	}
	
	/**
	 * 
	 */
	private void addNewLineToWriteFromBKTransactions(int _sDate, String _sItem, double _sQuantity, double _sPrice, double _sQuantityAdded) {
		pListLineToWriteFromBKTransactions.add(_sDate + "," + _sItem + "," + _sQuantity + "," + _sPrice + "," + _sQuantityAdded);
	}
	
	/**
	 * 
	 */
	private void addNewLineToWriteFromIBReport(int _sDate, String _sItem, String _sFileReport, double _sQuantity, double _sForex) {
		pListLineToWriteFromIBReport.add(_sDate + "," + _sItem + "," + _sFileReport + "," + _sQuantity + "," + _sForex + "," + (_sQuantity * _sForex));
	}
	
	/**
	 * 
	 */
	private void writeFilesDebug() {
		String lDir = StaticDir.getOUTPUT_DEBUG();
		String lNameFileFromBKTransactions = BasicDateInt.getmToday() + "_" + this.getClass().getSimpleName() + "_FromBKTransactions.csv";
		String lHeader = "Date,Item,Quantity,Price,ValueAdded";
		BasicFichiers.writeFile(lDir, lNameFileFromBKTransactions, lHeader, pListLineToWriteFromBKTransactions);
		String lNameFileFromIBReport = BasicDateInt.getmToday() + "_" + this.getClass().getSimpleName() + "_FromIBReport.csv";
		lHeader = "Date,Item,From IBFile,Quantity,Forex,ValueAdded";
		BasicFichiers.writeFile(lDir, lNameFileFromIBReport, lHeader, pListLineToWriteFromIBReport);
	}
	
	/*
	 * Getters & Setters
	 */
	public final double getpInterests() {
		return pInterests;
	}
	public final double getpCommissions() {
		return pCommissions;
	}
	public final double getpDeposits() {
		return pDeposits;
	}







}
