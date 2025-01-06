package step2loans.computeloancostscurrencies;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticNames;
import step0treatrawdata.conf.CFLoanCostCurrency;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;

public class LNCYComputeLoanCosts extends BKHolderGenerator {

	public LNCYComputeLoanCosts(LNCYManager _sLNCYManager) {
		super(_sLNCYManager.getpBKTransactionManager());
		pLNCYManager = _sLNCYManager;
	}

	/*
	 * Static
	 */
	private static enum key {CAPITAL, UOB, PROY, PROY_UOB}
	/*
	 * Data
	 */
	private LNCYManager pLNCYManager;

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return getpUniqueKey(_sBKTransaction) != null;
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		BKAccount lBKAccount = _sBKTransaction.getpBKAccount();
		BKAsset lBKAsset = _sBKTransaction.getpBKAsset();
		if (!lBKAsset.getpIsCurrency()) {
			return null;
		}
		/*
		 * We keep the capital held by Bunker
		 */
		if (_sBKTransaction.getpBKAccount().equals(BKAccountManager.getpBKAccountBunker())
				&& _sBKTransaction.getpBKIncome().equals(StaticBKIncome.getCAPITAL())) {
			return key.CAPITAL.toString();
		}
		/*
		 * We keep all the transactions of UOB
		 */
		if (_sBKTransaction.getpFileNameOrigin().equals(StaticNames.getUOB_ALL_TRANSACTIONS())) {
			if (lBKAccount.equals(BKAccountManager.getpBKAccountPierreRoy())) {
				return key.PROY_UOB.toString();
			}
			return key.UOB.toString();
		}
		/*
		 * We keep the transactions of PROY for the currencies
		 */
		if (lBKAccount.equals(BKAccountManager.getpBKAccountPierreRoy())) {
			return key.PROY.toString();
		}
		/*
		 * We ignore all other BKTransaction
		 */
		return null;
	}
	
	
	public final void run() {
		/*
		 * Initiate
		 */
		generateBKHolder();
		/*
		 * Fill the missing files'content
		 */
		for (LNCYFile lLNCYFile : pLNCYManager.getpLNCYDeleteCurrentFile().getpMapDateToLNCYFileMissing().values()) {
			for (int lDate : lLNCYFile.getpListDate()) {
				for (BKAsset lBKAsset : BKAssetManager.getpListCurrency()) {
					/*
					 * Compute the amount of the loan
					 */
					double lHoldingProyUOB = getpHolding(key.PROY_UOB, lDate, lBKAsset);
					double lHoldingPRoy = getpHolding(key.PROY, lDate, lBKAsset) + lHoldingProyUOB;
					double lHoldingCapital = getpHolding(key.CAPITAL, lDate, lBKAsset);
					double lHoldingUOB = getpHolding(key.UOB, lDate, lBKAsset) + lHoldingProyUOB;
					double lLoan = Math.max(lHoldingPRoy + lHoldingCapital - lHoldingUOB, 0);
					double lLoanUSD = lLoan * lBKAsset.getpPriceUSD(lDate);
					/*
					 * Compute the cost of the loan
					 */
					double lCostLoanPercent = CFLoanCostCurrency.getpCostLoan(lDate);
					double lCostLoanUSD = lLoanUSD * lCostLoanPercent / 365;
					/*
					 * Write line in file
					 */
					lLNCYFile.addNewLineToWrite(lDate, lCostLoanUSD, lBKAsset.getpName());
					lLNCYFile.addNewLineToWriteReport(lDate, lBKAsset.getpName(), lHoldingPRoy, lHoldingCapital, lHoldingUOB, lLoan, lLoanUSD, lCostLoanPercent, lCostLoanUSD);
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
	private double getpHolding(key _sKey, int _sDate, BKAsset _sBKAsset) {
		BKHolder lBKHolder = getpMapKeyToBKHolder().get(_sKey.toString());
		if (lBKHolder == null) {
			BasicPrintMsg.errorCodeLogic();
		}
		BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(_sDate);
		if (lBKInventory == null) {
			return 0.;
		}
		return lBKInventory.getpBKAssetQty(_sBKAsset);
	}
}
