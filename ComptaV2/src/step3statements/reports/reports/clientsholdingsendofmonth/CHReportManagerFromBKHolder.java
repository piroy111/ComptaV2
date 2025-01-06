package step3statements.reports.reports.clientsholdingsendofmonth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;

public class CHReportManagerFromBKHolder extends BKHolderGenerator {

	public CHReportManagerFromBKHolder(STBKHoldingsClientsEndOfMonth _sSTBKHoldingsClientsEndOfMonth) {
		super(_sSTBKHoldingsClientsEndOfMonth.getpBKTransactionManager());
		pSTBKHoldingsClientsEndOfMonth = _sSTBKHoldingsClientsEndOfMonth;
		/*
		 * 
		 */
		pTreeMapDateToCHReport = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private STBKHoldingsClientsEndOfMonth pSTBKHoldingsClientsEndOfMonth;
	private TreeMap<Integer, CHReport> pTreeMapDateToCHReport;

	/**
	 * Keep all except Bunker and PiRoy
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		BKAccount lBKAccount = _sBKTransaction.getpBKAccount();
		return !lBKAccount.equals(BKAccountManager.getpBKAccountBunker())
				&& !lBKAccount.equals(BKAccountManager.getpBKAccountPierreRoy());
	}

	/**
	 * 
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress();
	}

	/**
	 * 
	 */
	public final void createCHReport() {
		/*
		 * Initiate
		 */
		generateBKHolder();
		List<BKAsset> lListBKAsset = new ArrayList<>(BKAssetManager.getpMapNameToBKAsset().values());
		Collections.sort(lListBKAsset);
		/*
		 * Create list of dates to create the reports for
		 */
		List<Integer> lListDate = new ArrayList<>(pSTBKHoldingsClientsEndOfMonth.getpCHReportManagerFromImport().getpTreeMapDateToCHReport().keySet());
		int lDateCompta = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		if (!lListDate.contains(lDateCompta)) {
			lListDate.add(lDateCompta);
		}
		/*
		 * Create the reports
		 */
		for (int lDate : lListDate) {
			CHReport lCHReport = getpOrCreateCHReport(lDate);
			for (BKHolder lBKHolder : getpListBKHolder()) {
				BKAccount lBKAccount = BKAccountManager.getpBKAccount(lBKHolder.getpKey());
				for (BKAsset lBKAsset : lListBKAsset) {
					BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
					double lHolding = 0.;
					if (lBKInventory != null) {
						Double lHoldingObj = lBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
						if (lHoldingObj != null) {
							lHolding = lHoldingObj;
						}
					}
					lCHReport.putNewHolding(lBKAccount, lBKAsset, lHolding);
				}
			}
		}
	}
	
	/*
	 * Write reports
	 */
	public final void writeCHReport() {
		for (CHReport lCHReport : pTreeMapDateToCHReport.values()) {
			lCHReport.writeReport(StaticDir.getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH());
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final CHReport getpOrCreateCHReport(int _sDate) {
		CHReport lCHReport = pTreeMapDateToCHReport.get(_sDate);
		if (lCHReport == null) {
			lCHReport = new CHReport(_sDate);
			pTreeMapDateToCHReport.put(_sDate, lCHReport);
		}
		return lCHReport;
	}

	/*
	 * Getters & Setters
	 */
	public final TreeMap<Integer, CHReport> getpTreeMapDateToCHReport() {
		return pTreeMapDateToCHReport;
	}


	
}
