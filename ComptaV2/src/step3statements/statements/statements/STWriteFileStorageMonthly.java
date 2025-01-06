package step3statements.statements.statements;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step3statements.statements.abstractstatements.STIncome;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileStorageMonthly extends STStatementAbstract {

	public STWriteFileStorageMonthly(STIncome _sSTIncome) {
		super(_sSTIncome);
		pSTIncome = _sSTIncome;
	}

	/*
	 * Data
	 */
	private STIncome pSTIncome;
	
	
	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_STORAGE();
	}
	
	/**
	 * 
	 */
	@Override public void createLines() {		
		/*
		 * We keep the transactions only for the previous month
		 */
		int lDateStop = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		int lDateStart = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -2));
		/*
		 * Initiate
		 */
		addToHeader("Date to book the cost of storage,BKAccount,Currency of the BKAccount,Cost of storage in currency of the BKAccount");
		/*
		 * Build file content
		 */
		for (BKHolder lBKHolder : pSTIncome.getpListBKHolder()) {
			/*
			 * Keep only the accounts of the clients the storage paid by the client
			 */
			BKAccount lBKAccount = pSTIncome.getpBKAccount(lBKHolder);
			if (lBKAccount.equals(BKAccountManager.getpBKAccountPierreRoy()) || lBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
				continue;
			}
			String lBKIncome = pSTIncome.getpBKIncome(lBKHolder);
			if (!lBKIncome.equals("Operations_Storage_incoming_from_clients")) {
				continue;
			}
			/*
			 * 
			 */
			BKInventory lBKInventoryStop = lBKHolder.getpMapDateToBKInventory().get(lDateStop);
			BKInventory lBKInventoryStart = lBKHolder.getpMapDateToBKInventory().get(lDateStart);
			for (BKAsset lBKAsset : BKAssetManager.getpListCurrency()) {
				double lHoldingStorageStop = getpHolding(lBKInventoryStop, lBKAsset);
				double lHoldingStorageStart = getpHolding(lBKInventoryStart, lBKAsset);
				double lStoragePaid = lHoldingStorageStop - lHoldingStorageStart;
				if (!AMNumberTools.isNaNOrZero(lStoragePaid)) {
					String lLine = lDateStop
							+ "," + lBKAccount.getpEmailAddress()
							+ "," + lBKAccount.getpBKAssetCurrency().getpName()
							+ "," + lStoragePaid;
					addToListLine(lLine);
				}
			}
		}
	}

	/**
	 * 
	 * @param _sBKInventory
	 * @param _sBKAsset
	 * @return
	 */
	private double getpHolding(BKInventory _sBKInventory, BKAsset _sBKAsset) {
		if (_sBKInventory == null) {
			return 0.;
		}
		Double lHolding = _sBKInventory.getpMapBKAssetToQuantity().get(_sBKAsset);
		if (AMNumberTools.isNaNOrNullOrZero(lHolding)) {
			return 0.;
		}
		return lHolding;
	}
	
	

	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
