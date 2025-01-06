package step0treatrawdata.uob.versionnew;

import basicmethods.BasicPrintMsg;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class UOBTreatedManager {

	public UOBTreatedManager() {
		pUOBTreatedWriter = new UOBTreatedWriter(this);
	}
	
	/*
	 * Data
	 */
	private UOBMainManager pUOBMainManager;
	private UOBTreatedWriter pUOBTreatedWriter;
	
	/**
	 * Load all UOBTransactions from raw files. 
	 * Communicate on errors if some UOBTransactions are unknown (i.e. without a BKIncome and an account associated to it)
	 */
	public final void loadFromRawFiles() {
		pUOBMainManager = new UOBMainManager(UOB_DISPLAY.Off);
		pUOBMainManager.run();
	}
	
	/**
	 * Use the UOBTransaction loaded by the method 'loadFromRawFiles' and write the files treated with them<br>
	 */
	public final void writeTreated() {
		BasicPrintMsg.displayTitle(this, "Write all the UOBTransactions into a file treated");
		pUOBTreatedWriter.run();
	}
	
	/**
	 * Load the UOBTransaction from the files treated directly<br>
	 * The objects UOBTransaction (which belongs to the stand-alone UOB) will not be created<br>
	 */
	public final void loadFromTreated() {
		
	}

	/*
	 * Getters & Setters
	 */
	public final UOBMainManager getpUOBMainManager() {
		return pUOBMainManager;
	}
	
	
}
