package step5fiscalyearend.balancesheetv2;

import step1loadtransactions.transactions.BKTransactionManager;
import step5fiscalyearend.FYMain;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator;
import step5fiscalyearend.balancesheetv2.assets.BSAssetManager;
import step5fiscalyearend.balancesheetv2.writefileallassets.BSWriteFile;
import step5fiscalyearend.balancesheetv2.writefiledebug.BSAssetWriteFileDebugAllBSAssets;
import step5fiscalyearend.balancesheetv2.writefiledebug.BSAssetWriteFileDebugMirrorTransactions;
import step5fiscalyearend.balancesheetv2.writefiledebug.BSAssetWriteFileDebugSelectedBSAssets;

public class BSManager {

	public BSManager(FYMain _sFYMain) {
		pFYMain = _sFYMain;
		/*
		 * 
		 */
		pBSAssetGenerator = new BSAssetGenerator(this);
		pBSAssetWriteFileDebugAllBSAssets = new BSAssetWriteFileDebugAllBSAssets(this);
		pBSAssetManager = new BSAssetManager(this);
		pBSAssetWriteFileDebugMirrorTransactions = new BSAssetWriteFileDebugMirrorTransactions(this);
		pBSAssetWriteFileDebugSelectedBSAssets = new BSAssetWriteFileDebugSelectedBSAssets(this);
		pBSWriteFile = new BSWriteFile(this);
	}
	
	/*
	 * Data
	 */
	private FYMain pFYMain;
	private BSAssetGenerator pBSAssetGenerator;
	private BSAssetWriteFileDebugAllBSAssets pBSAssetWriteFileDebugAllBSAssets;
	private BSAssetWriteFileDebugMirrorTransactions pBSAssetWriteFileDebugMirrorTransactions;
	private BSAssetWriteFileDebugSelectedBSAssets pBSAssetWriteFileDebugSelectedBSAssets;
	private BSAssetManager pBSAssetManager;
	private BSWriteFile pBSWriteFile;

	/**
	 * The core of the program is in the class pBSAssetGenerator which sets the ASSETS, LIABILITIES, EQUITIES
	 */
	public final void run() {
		/*
		 * Group all the BKTransactions according to the Override methods
		 */
		pBSAssetGenerator.generateBKHolder();
		/*
		 * Create the BSAssets
		 */
		pBSAssetManager.run();
		/*
		 * Write a file debug + check for bugs
		 */
		pBSAssetWriteFileDebugAllBSAssets.run();
		pBSAssetWriteFileDebugMirrorTransactions.run();
		pBSAssetWriteFileDebugSelectedBSAssets.run();
		/*
		 * Write file balance sheet + check for A-L-E = 0
		 */
		pBSWriteFile.run();
	}
	
	/*
	 * Getters & Setters
	 */
	public final FYMain getpFYMain() {
		return pFYMain;
	}
	public BKTransactionManager getpBKTransactionManager() {
		return pFYMain.getpLNManager().getpBKTransactionManager();
	}
	public final BSAssetGenerator getpBSAssetGenerator() {
		return pBSAssetGenerator;
	}
	public final BSAssetManager getpBSAssetManager() {
		return pBSAssetManager;
	}
}
