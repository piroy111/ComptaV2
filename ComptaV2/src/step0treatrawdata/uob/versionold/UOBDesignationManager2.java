package step0treatrawdata.uob.versionold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.ReadFileInLinesWithMap;
import staticdata.StaticDir;
import staticdata.StaticNames;

class UOBDesignationManager2 {

	protected UOBDesignationManager2(UOBManagerOld _sUOBManager) {
		mUOBManager = _sUOBManager;
		/*
		 * Load file
		 */
		loadFile();
	}

	/*
	 * Data
	 */
	private UOBManagerOld mUOBManager;
	private ReadFileInLinesWithMap mReadFileConf;
	private Map<String, UOBDesignationOld> mMapKeyToUOBDesignation;
	private static enum Column {YourReference, OurReference, Remarks, Description}	

	/**
	 * Get the name of the column in the conf file of designation
	 * @param _sColumn : enum
	 */
	private String getmColumnStr(Column _sColumn) {
		switch (_sColumn) {
		case Remarks : return "Remarks";
		case YourReference : return "Your Reference";
		case OurReference : return "Our Reference";
		case Description : return "Description";
		default : BasicPrintMsg.error("Error"); return null; 
		}
	}
	
	/**
	 * 
	 * @param _sUOBTransaction
	 * @return
	 */
	private String getmValueDesignation(UOBTransactionOld _sUOBTransaction, Column _sColumn) {
		switch (_sColumn) {
		case Remarks : return _sUOBTransaction.getmRemarks();
		case YourReference : return _sUOBTransaction.getmYourReference();
		case OurReference : return _sUOBTransaction.getmOurReference();
		case Description : return _sUOBTransaction.getmDescription();
		default : BasicPrintMsg.error("Error"); return null; 
		}
	}
	
	/**
	 * Load the conf file of designation
	 */
	private void loadFile() {
		mReadFileConf = new ReadFileInLinesWithMap(StaticDir.getCONF_COMPTA(), 
				StaticNames.getUOB_DESIGNATIONS(), true);
		mMapKeyToUOBDesignation = new HashMap<String, UOBDesignationOld>();
		for (List<String> lListLineStr : mReadFileConf.getmListLines()) {
			/*
			 * Load line
			 */
			int lIdx = -1;
			String lDesignation = lListLineStr.get(++lIdx);
			String lColumn = lListLineStr.get(++lIdx);
			String lCurrency = lListLineStr.get(++lIdx);
			String lAccount = lListLineStr.get(++lIdx);
			String lBKCategory = lListLineStr.get(++lIdx);
			/*
			 * Special case of '
			 */
			if (lDesignation.startsWith("'")) {
				lDesignation = lDesignation.substring(1);
			}
			lDesignation = lDesignation.trim();
			/*
			 * Key to identify UOBDesignation + Check for error
			 */
			String lKey = getKeyUOBDesignation(lDesignation, lColumn, lCurrency);
			if (mMapKeyToUOBDesignation.containsKey(lKey)) {
				BasicPrintMsg.error("The Designation is twice in the conf file"
						+ "\nConf file= " + mReadFileConf.getmPathPlusNameFile()
						+ "\nLineStr= " + lListLineStr.toString()
						+ "\nDesignation= " + lDesignation);
			}
			/*
			 * Get + fill the UOBDesignation
			 */
			UOBDesignationOld lUOBDesignation = getmOrCreateUOBDesignation(lDesignation, lColumn, lCurrency);
			lUOBDesignation.setmAccount(lAccount);
			lUOBDesignation.setmBKCategory(lBKCategory);
		}
	}
	
	/**
	 * @return link each UOBTransaction to a category and an account
	 */
	public final void run() {
		/*
		 * link each UOBTransaction to a category and an account
		 */
		List<UOBTransactionOld> lListUOBTransactionUnknown = new ArrayList<UOBTransactionOld>();
		for (UOBTransactionOld lUOBTransaction : mUOBManager.getmListUOBTransaction()) {
			/*
			 * Check the UOBDesignation which matches the UOBTransaction
			 */
			UOBDesignationOld lUOBDesignation = null;
			for (Column lColumn : Column.values()) {
				String lDesignation = getmValueDesignation(lUOBTransaction, lColumn);
				String lColumnStr = getmColumnStr(lColumn);
				String lKey = getKeyUOBDesignation(lDesignation, lColumnStr, "");
				lUOBDesignation = mMapKeyToUOBDesignation.get(lKey);
				if (lUOBDesignation == null) {
					String lKeyCurrency = getKeyUOBDesignation(lDesignation, lColumnStr, lUOBTransaction.getmUOBAccount().getmCurrency());
					lUOBDesignation = mMapKeyToUOBDesignation.get(lKeyCurrency);
				}
				if (lUOBDesignation != null) {
					break;
				}
			}
			/*
			 * Fill the info from the designation
			 */
			if (lUOBDesignation != null) {
				lUOBTransaction.setmAccount(lUOBDesignation.getmAccount());
				lUOBTransaction.setmCategory(lUOBDesignation.getmBKCategory());
				/*
				 * Set the comment
				 */
				String lComment = "Designation Compta= " + lUOBDesignation.getmDesignation()
				+ "| Description= " + lUOBTransaction.getmDescription()
				+ "| Your Reference= " + lUOBTransaction.getmYourReference()
				+ "| Our Reference= " + lUOBTransaction.getmOurReference()
				+ "| Remarks= " + lUOBTransaction.getmRemarks();
				lComment = lComment.replaceAll(",", "");
				lUOBTransaction.setmComment(lComment);
			} 
			/*
			 * Case of a new UOBTransaction which does not have a designation --> we need to create a new UOBDesignation
			 */
			else {
				lListUOBTransactionUnknown.add(lUOBTransaction);
			}
		}
		/*
		 * Communication for missing transaction
		 */
		if (lListUOBTransactionUnknown.size() > 0) {
			String lMsg = "Hi\n\nThere are some transactions in the UOB account that I cannot link to an account and a category."
					+ "\n\nYou must teach me the category in the conf file '"
					+ StaticDir.getCONF_COMPTA() + StaticNames.getUOB_DESIGNATIONS() + "'\n";
			for (UOBTransactionOld lUOBTransaction : lListUOBTransactionUnknown) {
				lMsg += "\n" + lUOBTransaction.toString2();
			}
			/*
			 * List of missing category
			 */
			lMsg += "\n\nList of missing designations:";
			List<String> lListDesignationMissing = new ArrayList<String>();
			for (UOBTransactionOld lUOBTransaction : lListUOBTransactionUnknown) {
				String lDesignation = lUOBTransaction.getmRemarks();
				if (lDesignation.equals("")) {
					lDesignation = lUOBTransaction.getmDescription();
				}
				if (!lListDesignationMissing.contains(lDesignation)) {
					lListDesignationMissing.add(lDesignation);
					lMsg += "\n" + lDesignation;
				}
			}
			/*
			 * Display message
			 */
			lMsg += "\n\nHave a good day";
			System.out.println(lMsg);
			BasicPrintMsg.error("Check designations above");
		}
	}
	
	/**
	 * Unique key for the map
	 * @param _sDesignation
	 * @param _sColumn
	 * @param _sCurrency
	 * @return
	 */
	protected static String getKeyUOBDesignation(String _sDesignation, String _sColumn, String _sCurrency) {
		return _sDesignation + ";;" + _sColumn + ";;" + _sCurrency;
	}
	
	/**
	 * 
	 * @return
	 */
	private UOBDesignationOld getmOrCreateUOBDesignation(String _sDesignation, String _sColumn, String _sCurrency) {
		String lKey = getKeyUOBDesignation(_sDesignation, _sColumn, _sCurrency);
		UOBDesignationOld lUOBDesignation = mMapKeyToUOBDesignation.get(lKey);
		if (lUOBDesignation == null) {
			lUOBDesignation = new UOBDesignationOld(_sDesignation, _sColumn, _sCurrency);
			mMapKeyToUOBDesignation.put(lKey, lUOBDesignation);
		}
		return lUOBDesignation;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
