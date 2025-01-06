package step5fiscalyearend.balancesheetv2.writefiledebug;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.balancesheetv2.assets.BSAsset;

public class BSAssetWriteFileDebugAllBSAssets {

	/**
	 * Write all the BSAssets in a debug file
	 * It is the same thing as the balance sheet, except in this case we publish also the asset at 0
	 */
	public BSAssetWriteFileDebugAllBSAssets(BSManager _sBSManager) {
		pBSManager = _sBSManager;
	}
	
	/*
	 * Data
	 */
	private BSManager pBSManager;
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * File header
		 */
		String lHeader = "KeyStr,BSType,Title";
		for (int lDateFY : pBSManager.getpFYMain().getpListDateFYToDo()) {
			lHeader += ",Value$ at " + lDateFY;
		}
		/*
		 * File content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BSAsset lBSAsset : pBSManager.getpBSAssetManager().getpListBSAsset()) {
			String lLineStr = lBSAsset.getpKeyStr()
					+ "," + lBSAsset.getpBSType()
					+ "," + lBSAsset.getpTitle();
			for (int lDateFY : pBSManager.getpFYMain().getpListDateFYToDo()) {
				lLineStr += "," + lBSAsset.getpAndComputeValueUSD(lDateFY);
			}
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write file
		 */
		String lDir = StaticDir.getOUTPUT_FY_DEBUG_BS();
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + StaticNames.getOUTPUT_FY_DEBUG_BS();
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
}
