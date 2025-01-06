package step3statements.reports.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step1loadtransactions.transactions.BKTransactionManager;

public abstract class STAbstractArray extends BKHolderGenerator {

	public STAbstractArray(BKTransactionManager _sBKTransactionManager, String _sDir, String _sFileSuffix) {
		super(_sBKTransactionManager);
		pDir = _sDir;
		pFileSuffix = _sFileSuffix;
	}

	/*
	 * Abstract
	 */
	public abstract String getpHeaderRow(BKTransaction _sBKTransaction);
	public abstract String getpHeaderColumn(BKTransaction _sBKTransaction);
	public abstract Integer comparatorHeaderRow(String _sHeaderRow1, String _sHeaderRow2);
	public abstract Integer comparatorHeaderColumn(String _sHeaderColumn1, String _sHeaderColumn2);
	public abstract String valueToDisplay(String _sHeaderRow, String _sHeaderColumn, BKInventory _sBKInventory);
	/*
	 * Data
	 */
	private String pDir;
	private String pFileSuffix;

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return getpHeaderRow(_sBKTransaction) + ";@;@;" + getpHeaderColumn(_sBKTransaction);
	}
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * Generate BKHolder
		 */
		generateBKHolder();
		/*
		 * Create map for the array
		 */
		Map<String, List<String>> lMapHeaderRowToLine = new HashMap<String, List<String>>();
		List<String> lListHeaderColumn = new ArrayList<String>();
		/*
		 * Loop on the BKHolder to allocate the value USD of the BKHolder in the lMapHeaderRowToRow
		 */
		for (BKHolder lBKHolder : getpListBKHolder()) {
			/*
			 * Load values
			 */
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(BasicDateInt.getmToday());
			/*
			 * Load
			 */
			String[] lArrayStr = lBKHolder.getpKey().split(";@;@;", -1);
			String lHeaderRow = lArrayStr[0];
			String lHeaderColumn = lArrayStr[1];
			/*
			 * Get the line for the row of header row
			 */
			List<String> lLine = lMapHeaderRowToLine.get(lHeaderRow);
			if (lLine == null) {
				lLine = new ArrayList<String>();
				for (int lIdx = 0; lIdx < lListHeaderColumn.size(); lIdx++) {
					lLine.add("0");
				}
				lMapHeaderRowToLine.put(lHeaderRow, lLine);
			}
			/*
			 * Find out the column according to header column
			 */
			int lIdxColumn = lListHeaderColumn.indexOf(lHeaderColumn);
			/*
			 * Case if this is the first time we encounter the header column --> we add a cell to all lines
			 */
			if (lIdxColumn == -1) {
				lListHeaderColumn.add(lHeaderColumn);
				lIdxColumn = lListHeaderColumn.size() - 1;
				for (List<String> lLineDump : lMapHeaderRowToLine.values()) {
					lLineDump.add("");
				}
			}
			/*
			 * We set the value USD in the array
			 */
			lLine.set(lIdxColumn, valueToDisplay(lHeaderRow, lHeaderColumn, lBKInventory));
		}
		/*
		 * Sort headers
		 */
		List<String> lListHeaderRowSort = new ArrayList<>(lMapHeaderRowToLine.keySet());
		Collections.sort(lListHeaderRowSort, new sortRow("Row"));
		List<String> lListHeaderColumnSort = new ArrayList<>(lListHeaderColumn);
		Collections.sort(lListHeaderColumnSort, new sortRow("Column"));
		List<Integer> lListIndexOfHeaderColumn = new ArrayList<Integer>();
		for (String lHeaderColumnSort : lListHeaderColumnSort) {
			lListIndexOfHeaderColumn.add(lListHeaderColumn.indexOf(lHeaderColumnSort));
		}
		/*
		 * Build list line to write
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (String lHeaderRow : lListHeaderRowSort) {
			List<String> lLineStr = lMapHeaderRowToLine.get(lHeaderRow);
			String lLine = lHeaderRow;
			for (int lIdx : lListIndexOfHeaderColumn) {
				lLine += "," + lLineStr.get(lIdx);
			}
			lListLineToWrite.add(lLine);
		}
		/*
		 * Header
		 */
		String lHeader = "";
		for (String lHeaderColumn : lListHeaderColumnSort) {
			lHeader += "," + lHeaderColumn;
		}
		/*
		 * Write File
		 */
		BasicFichiers.getOrCreateDirectory(pDir);
		String lNameFile = BasicDateInt.getmToday() + pFileSuffix;
		BasicFichiers.writeFile(pDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	/**
	 * 
	 * @author pi
	 *
	 */
	private class sortRow implements Comparator<String> {
		private sortRow(String _sType) {
			if (_sType.equals("Row")) {
				pIsRow = true;
			} else if (!_sType.equals("Column")) {
				BasicPrintMsg.error("");
			}
		}
		
		boolean pIsRow;
		
		@Override public int compare(String _sHeader1, String _sHeader2) {
			Integer lCompare = null;
			if (pIsRow) {
				lCompare = comparatorHeaderRow(_sHeader1, _sHeader2);
			} else {
				lCompare = comparatorHeaderColumn(_sHeader1, _sHeader2);
			}
			if (lCompare == null) {
				return _sHeader1.compareTo(_sHeader2);
			} else {
				return lCompare;
			}
		}		
	}

	
	
}
