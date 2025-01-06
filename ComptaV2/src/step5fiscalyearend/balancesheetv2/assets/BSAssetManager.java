package step5fiscalyearend.balancesheetv2.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import step1loadtransactions.holder.BKHolder;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator.BS_TYPE;

public class BSAssetManager {

	/**
	 * Classic
	 */
	public BSAssetManager(BSManager _sBSManager) {
		pBSManager = _sBSManager;
		/*
		 * 
		 */
		pMapKeyToBSAsset = new HashMap<>();
		pListBSAsset = new ArrayList<>();
		pMapBSTypeToBSAsset = new HashMap<>();
	}
	
	/*
	 * Data
	 */
	private BSManager pBSManager;
	private Map<String, BSAsset> pMapKeyToBSAsset;
	private List<BSAsset> pListBSAsset;
	private Map<BS_TYPE, List<BSAsset>> pMapBSTypeToBSAsset;

	
	/**
	 * 
	 * @param _sKeyStr
	 * @return
	 */
	public final BSAsset getpOrCreateBSAsset(BS_TYPE _sBSType, String _sTitle) {
		String lKeyStr = BSAsset.getKeyStr(_sBSType, _sTitle);
		BSAsset lBSAsset = pMapKeyToBSAsset.get(lKeyStr);
		if (lBSAsset == null) {
			lBSAsset = new BSAsset(_sBSType, _sTitle, this);
			pMapKeyToBSAsset.put(lKeyStr, lBSAsset);
			pListBSAsset.add(lBSAsset);
			/*
			 * 
			 */
			Collections.sort(pListBSAsset);
			/*
			 * 
			 */
			List<BSAsset> lListBSAsset = pMapBSTypeToBSAsset.get(lBSAsset.getpBSType());
			if (lListBSAsset == null) {
				lListBSAsset = new ArrayList<>();
				pMapBSTypeToBSAsset.put(lBSAsset.getpBSType(), lListBSAsset);
			}
			lListBSAsset.add(lBSAsset);			
		}
		return lBSAsset;
	}

	/**
	 * Create the BSAssets
	 */
	public final void run() {
		/*
		 * Create the BSAssets
		 */
		for (BKHolder lBKHolder : pBSManager.getpBSAssetGenerator().getpListBKHolder()) {
			String lKeyStr = lBKHolder.getpKey();
			/*
			 * Keeps only the assets inside the balance sheet
			 *  --> if there is no SEPARATOR, it means the asset is outside (most likely the bars held by the clients)
			 */
			if (lKeyStr.contains(BSAssetGenerator.SEPARATOR)) {
				String[] lArrayKeyBSAsset = lKeyStr.split(BSAssetGenerator.SEPARATOR_LIST, -1);
				for (String lKeyBSAsset : lArrayKeyBSAsset) {
					/*
					 * Load
					 */
					String[] lArrayStr = lKeyBSAsset.split(BSAssetGenerator.SEPARATOR, -1);
					BS_TYPE lBSType = BS_TYPE.valueOf(lArrayStr[0]);
					String lTitle = lArrayStr[1];
					/*
					 * Create
					 */
					BSAsset lBSAsset = getpOrCreateBSAsset(lBSType, lTitle);
					/*
					 * Declare a new BKHolder into BSAsset
					 */
					lBSAsset.declareNewBKHolder(lBKHolder);
				}
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final Map<String, BSAsset> getpMapKeyToBSAsset() {
		return pMapKeyToBSAsset;
	}
	public final List<BSAsset> getpListBSAsset() {
		return pListBSAsset;
	}
	public final Map<BS_TYPE, List<BSAsset>> getpMapBSTypeToBSAsset() {
		return pMapBSTypeToBSAsset;
	}
	
	
}
