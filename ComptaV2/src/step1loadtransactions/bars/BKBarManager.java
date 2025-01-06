package step1loadtransactions.bars;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import conf.bkbartype.BKBarTypeManager;
import step1loadtransactions.transactions.BKTransaction;

public class BKBarManager {

	/*
	 * Data
	 */
	private static Map<String, BKBar> pMapRefToBKBar = new HashMap<String, BKBar>();

	/**
	 * Create all the BKBars
	 */
	public static final void loadBKBar(List<BKTransaction> _sListBKTransaction) {
		for (BKTransaction lBKTransaction : _sListBKTransaction) {
			loadIfBKBar(lBKTransaction);
		}
	}

	/**
	 * Create the BKBar if the asset is a bar
	 * @param _sBKTransaction
	 */
	public static final void loadIfBKBar(BKTransaction _sBKTransaction) {
		if (_sBKTransaction.getpBKAsset().getpIsBar()) {
			String lRef = _sBKTransaction.getpComment();
			if (!pMapRefToBKBar.containsKey(lRef)) {
				BKBar lBKBar = getpOrCreateBKBar(lRef);
				lBKBar.setpBKAsset(_sBKTransaction.getpBKAsset());
				lBKBar.setpWeightOz(Math.abs(_sBKTransaction.getpQuantity()));
				lBKBar.setpBKBarType(BKBarTypeManager.getBKBarType(lBKBar.getpWeightOz(), lBKBar.getpBKAsset().getpMetalName()));
			} else {
				BKBar lBKBar = getpOrCreateBKBar(lRef);
				lBKBar.setpDateAcquisition(_sBKTransaction.getpDate());
			}
		}
	}

	/**
	 * classic get or create
	 * @param _sRef
	 * @return
	 */
	public static final BKBar getpOrCreateBKBar(String _sRef) {
		BKBar lBKBar = pMapRefToBKBar.get(_sRef);
		if (lBKBar == null) {
			lBKBar = new BKBar(_sRef);
			pMapRefToBKBar.put(_sRef,  lBKBar);
		}
		return lBKBar;
	}

	/**
	 * get the BKBar and check if it really exists
	 * @param _sBKTransaction
	 * @return
	 */
	public static BKBar getpAndCheckBKBar(BKTransaction _sBKTransaction) {
		if (!_sBKTransaction.getpBKAsset().getpIsBar()) {
			BasicPrintMsg.error("This is not a bar; BKTransaction= " + _sBKTransaction); 
		}
		BKBar lBKBar = pMapRefToBKBar.get(_sBKTransaction.getpComment());
		if (lBKBar == null) {
			BasicPrintMsg.error("The bar should have been loaded via the run method previously");
		}
		return lBKBar;
	}

	/*
	 * Getters & Setters
	 */
	public static final Map<String, BKBar> getpMapRefToBKBar() {
		return pMapRefToBKBar;
	}


}

