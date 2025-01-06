package step3statements.statements.main;

import java.util.Comparator;

import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;

public class STSortBKHolderIfBKAccount implements Comparator<BKHolder> {

	public STSortBKHolderIfBKAccount(int _sDateRef) {
		pDateRef = _sDateRef;
	}
	
	private int pDateRef;
	
	@Override
	public int compare(BKHolder _sBKHolder0, BKHolder _sBKHolder1) {
		if (_sBKHolder0.getpKey().equals(_sBKHolder1.getpKey())) {
			return 0;
		}
		if (_sBKHolder0.getpKey().contains(BKAccountManager.getpBKAccountBunker().getpEmailAddress())) {
			return -1;
		} else if (_sBKHolder1.getpKey().contains(BKAccountManager.getpBKAccountBunker().getpEmailAddress())) {
			return 1;
		} else if (_sBKHolder0.getpKey().contains(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
			return -1;
		} else if (_sBKHolder1.getpKey().contains(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
			return 1;
		} else {
			BKInventory lBKInventory0 = _sBKHolder0.getpMapDateToBKInventory().get(pDateRef);
			BKInventory lBKInventory1 = _sBKHolder1.getpMapDateToBKInventory().get(pDateRef);
			double lValueUSD0 = lBKInventory0.getpValueUSD();
			double lValueUSD1 = lBKInventory1.getpValueUSD();
			return -Double.compare(lValueUSD0, lValueUSD1);
		}
	}
	
}
