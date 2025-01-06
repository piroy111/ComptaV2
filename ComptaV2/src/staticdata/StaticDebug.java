package staticdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicString;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.bars.BKBarManager;
import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNCreateLoan;

public class StaticDebug {
	
	/*
	 * Will display each time there is a BKTransaction with the bar; Put null if no debug
	 */
	private static String DEBUG_BAR = "D06813";
	/*
	 * BKInventory coupled with the DEBUG_BAR
	 */
	private static String BKINVENTORY_ID_FROM = "pierre.roy@hotmail.com";
	private static String BKINVENTORY_CLASS_FROM =  LNCreateLoan.class.getSimpleName();
	private static boolean IS_ACTIVATE_BKINVENTORY_DEBUG = false;
	/*
	 * File debug Bars
	 */
	private static String DIR_DEBUG = "C:/zz_no_back_up/";
	private static String NAME_DEBUG = BasicDateInt.getmToday() + "_DebugFileBKBar_" + DEBUG_BAR + ".csv";
	private static Map<Integer, Integer> pMapDateToHolding;
	private static Map<Integer, List<String>> pMapDateToListLineToWrite;
	
	/**
	 * 
	 * @param _sBKTransaction
	 */
	public static final void writeFileDebug(BKTransaction _sBKTransaction) {
		if (DEBUG_BAR != null && !DEBUG_BAR.equals("")
				&& _sBKTransaction.getpBKAsset().getpIsBar() && _sBKTransaction.getpComment().equals(StaticDebug.getDEBUG_BAR())) {
			/*
			 * Display in the console
			 */
			System.err.println();
			System.err.println("DEBUG >>> new BKTransaction: Bar= " + _sBKTransaction.getpComment()
					+ "; _sDate= " + _sBKTransaction.getpDate()
					+ "; _sQuantity= " + _sBKTransaction.getpQuantity()
					+ "; _sBKAccount= " + _sBKTransaction.getpBKAccount().getpEmailAddress());
			System.err.println();
			/*
			 * Initiate
			 */
			if (pMapDateToListLineToWrite == null) {
				pMapDateToListLineToWrite = new HashMap<>();
				pMapDateToHolding = new HashMap<>();
			}
			int lDate = _sBKTransaction.getpDate();
			if (!pMapDateToListLineToWrite.containsKey(lDate)) {
				pMapDateToListLineToWrite.put(lDate, new ArrayList<>());
				pMapDateToHolding.put(lDate, 0);
			}
			/*
			 * Count the number of bars for one date
			 */
			BKBar lBKBar = BKBarManager.getpAndCheckBKBar(_sBKTransaction);
			int lQuantity = (int) Math.round(_sBKTransaction.getpQuantity() / lBKBar.getpWeightOz());
			Integer lQuantityTotal = pMapDateToHolding.get(lDate);
			lQuantityTotal += lQuantity;
			pMapDateToHolding.put(lDate, lQuantityTotal);
			/*
			 * Create the new line and add it to the map
			 */
			int lNbCreatedAlready = 0;
			List<Integer> lListDate = new ArrayList<>(pMapDateToHolding.keySet());
			Collections.sort(lListDate);
			for (int lDateLoop : lListDate) {
				lNbCreatedAlready += pMapDateToListLineToWrite.get(lDateLoop).size();
			}
			String lLineStrNew = _sBKTransaction.getpDate()
					+ "," + _sBKTransaction.getpBKAsset().getpName()
					+ "," + _sBKTransaction.getpComment()
					+ "," + _sBKTransaction.getpQuantity()
					+ "," + _sBKTransaction.getpBKAccount().getpEmailAddress()
					+ "," + lNbCreatedAlready;
			pMapDateToListLineToWrite.get(lDate).add(lLineStrNew);
			Collections.sort(pMapDateToListLineToWrite.get(lDate));
			/*
			 * Add to the list of transactions to write in the CSV file
			 */
			List<String> lListLineToWrite = new ArrayList<>();
			for (int lDateLoop : lListDate) {
				for (String lLine : pMapDateToListLineToWrite.get(lDateLoop)) {
					int lDateLine = BasicString.getInt(lLine.substring(0, 8));
					lLine += "," + pMapDateToHolding.get(lDateLine);
					lListLineToWrite.add(lLine);
				}				
			}
			/*
			 * Write file
			 */
			String lHeader = "Date,BKAsset,Reference BKBar,Quantity,BKAccount,Order of creation of the BKTransaction, Holding total for the date";
			BasicFichiers.writeFile(DIR_DEBUG, NAME_DEBUG, lHeader, lListLineToWrite);
		}
	}
	
	
	
	/*
	 * Getters & Setters
	 */
	public static final String getDEBUG_BAR() {
		return DEBUG_BAR;
	}
	public static final boolean getIS_ACTIVATE_BKINVENTORY_DEBUG() {
		return IS_ACTIVATE_BKINVENTORY_DEBUG;
	}
	public static final void setIS_ACTIVATE_BKINVENTORY_DEBUG(boolean iS_ACTIVATE_BKINVENTORY_DEBUG) {
		IS_ACTIVATE_BKINVENTORY_DEBUG = iS_ACTIVATE_BKINVENTORY_DEBUG;
	}
	public static final String getBKINVENTORY_ID_FROM() {
		return BKINVENTORY_ID_FROM;
	}
	public static final String getBKINVENTORY_CLASS_FROM() {
		return BKINVENTORY_CLASS_FROM;
	}
	public static final String getDIR_DEBUG() {
		return DIR_DEBUG;
	}
	public static final String getNAME_DEBUG() {
		return NAME_DEBUG;
	}
	
}
