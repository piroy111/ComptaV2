package step5fiscalyearend.balancesheetv2.assets;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator.BS_TYPE;

public class BSAsset implements Comparable<BSAsset> {

	protected BSAsset(BS_TYPE _sBSType, String _sTitle, BSAssetManager _sBSAssetManager) {
		pBSType = _sBSType;
		pTitle = _sTitle;
		pBSAssetManager = _sBSAssetManager;
		/*
		 * 
		 */
		pKeyStr = getKeyStr(pBSType, pTitle);
		pListBKHolder = new ArrayList<>();
		pRank = getpRank(pBSType);
	}
	
	/*
	 * Data
	 */
	private BSAssetManager pBSAssetManager;
	private String pKeyStr;
	private List<BKHolder> pListBKHolder;
	private BS_TYPE pBSType;
	private String pTitle;
	private int pRank;
	
	/**
	 * 
	 * @param _sBSType
	 * @param _sTitle
	 * @return
	 */
	public final static String getKeyStr(BS_TYPE _sBSType, String _sTitle) {
		return _sBSType + BSAssetGenerator.SEPARATOR + _sTitle;
	}
	
	/**
	 * 
	 * @param _sBKHolder
	 */
	protected final void declareNewBKHolder(BKHolder _sBKHolder) {
		if (!pListBKHolder.contains(_sBKHolder)) {
			pListBKHolder.add(_sBKHolder);
		}
	}
	
	@Override public int compareTo(BSAsset _sBSAsset) {
		int lCompare = Integer.compare(pRank, _sBSAsset.pRank);
		if (lCompare == 0) {
			lCompare = pTitle.compareTo(_sBSAsset.pTitle);
		}
		return lCompare;
	}
	
	/**
	 * Get the value USD of the asset at a given date.
	 * If the date is after today, we take the date of today
	 * @param _sDate
	 * @return
	 */
	public final Double getpAndComputeValueUSD(int _sDate) {
		/*
		 * Initiate
		 */
		Double lValueUSD = null;
		int lDate = Math.min(_sDate, BasicDateInt.getmToday());
		/*
		 * Add the value USD of all the BKHolder related to this BSAsset
		 */
		for (BKHolder lBKHolder : pListBKHolder) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
			if (lBKInventory != null) {
				if (lValueUSD == null) {
					lValueUSD = 0.;
				}
				lValueUSD += lBKInventory.getpValueUSD();
			}
		}
		/*
		 * 
		 */
		return lValueUSD;
	}
	
	/**
	 * 
	 * @param _sBSType
	 * @return
	 */
	private int getpRank(BS_TYPE _sBSType) {
		switch (_sBSType) {
		case Assets : return 0;
		case Liabilities : return 1;
		case Equities : return 2;
		case Mirror : return 3;
		}
		BasicPrintMsg.error("There is a missing BSType. You must add some code above.\nMissing type= " + _sBSType);
		return 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public final BSAssetManager getpBSAssetManager() {
		return pBSAssetManager;
	}
	public final String getpKeyStr() {
		return pKeyStr;
	}
	public final BS_TYPE getpBSType() {
		return pBSType;
	}
	public final String getpTitle() {
		return pTitle;
	}
	public final List<BKHolder> getpListBKHolder() {
		return pListBKHolder;
	}

	
	
	
}
