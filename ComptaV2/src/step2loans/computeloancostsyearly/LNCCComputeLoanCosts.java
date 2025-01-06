package step2loans.computeloancostsyearly;

import java.util.ArrayList;
import java.util.List;

import step0treatrawdata.conf.CFLoanCostYearly;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;

public class LNCCComputeLoanCosts extends BKHolderGenerator {

	public LNCCComputeLoanCosts(LNCCManager _sLNCCManager) {
		super(_sLNCCManager.getpBKTransactionManager());
		pLNCCManager = _sLNCCManager;
	}

	/*
	 * Data
	 */
	private LNCCManager pLNCCManager;
	
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		BKAccount lBKAccount = _sBKTransaction.getpBKAccount();
		BKAsset lBKAsset = _sBKTransaction.getpBKAsset();
		return lBKAccount.equals(BKAccountManager.getpBKAccountPierreRoy()) && lBKAsset.getpIsBarLoan();
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return "All";
	}

	public final void run() {
		/*
		 * Initiate
		 */
		generateBKHolder();		
		BKHolder lBKHolder = getpListBKHolder().get(0);
		List<BKAsset> lListBKAssetBarLoan = new ArrayList<>();
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAsset()) {
			if (lBKAsset.getpIsBarLoan()) {
				lListBKAssetBarLoan.add(lBKAsset);
			}
		}
		/*
		 * Fill the missing files'content
		 */
		for (LNCCFile lLNCCFile : pLNCCManager.getpLNCCDeleteCurrentFile().getpMapDateToLNCCFileMissing().values()) {
			for (int lDate : lLNCCFile.getpListDate()) {
				BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
				if (lBKInventory != null) {
					for (BKAsset lBKAsset : lListBKAssetBarLoan) {
						double lHolding = lBKInventory.getpBKAssetQty(lBKAsset);
						double lCostPercent = CFLoanCostYearly.getpCostLoan(lBKAsset.getpName(), lDate);
						double lCostUSD = lBKAsset.getpPriceUSD(lDate) * lHolding * lCostPercent / 365;
						lLNCCFile.addNewLineToWrite(lDate, lCostUSD, lBKAsset.getpName());
					}
				}
			}
		}
	}
	
	
}
