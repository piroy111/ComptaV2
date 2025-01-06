package step5fiscalyearend;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDate;
import staticdata.StaticDir;
import step2loans.createloanstransactions.LNManager;
import step5fiscalyearend.balancesheet.FYBalanceSheet;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.debug.FYDebugFileManager;
import step5fiscalyearend.incomestatement.FYIncomeStatementGenerator;
import step5fiscalyearend.incomestatement.compareincomeprevious.INComparePreviousFYIncome;
import step5fiscalyearend.incomestatement.loadfilesprevious.INLoadFilesPreviousFY;
import step5fiscalyearend.incomestatement.loadfilesprevious.INMoveSavedFYToDriverOutput;
import uob.UOBMainManager;

public class FYMain {

	public FYMain(LNManager _sLNManager, UOBMainManager _sUOBMainManager) {
		pLNManager = _sLNManager;
		pUOBMainManager = _sUOBMainManager;
		pFYIncomeStatementGenerator = new FYIncomeStatementGenerator(pLNManager);
		pFYBalanceSheet = new FYBalanceSheet(this);
		pFYDebugFileManager = new FYDebugFileManager();
		pBSManager = new BSManager(this);
		pINLoadFilesPreviousFY = new INLoadFilesPreviousFY(this);
		pINComparePreviousFYIncome = new INComparePreviousFYIncome(this);
	}
	
	/*
	 * Data
	 */
	private List<Integer> pListDateFYToDo;
	private FYIncomeStatementGenerator pFYIncomeStatementGenerator;
	private FYBalanceSheet pFYBalanceSheet;
	private LNManager pLNManager;
	private UOBMainManager pUOBMainManager;
	private FYDebugFileManager pFYDebugFileManager;
	private static int pDateFYLast;
	private BSManager pBSManager;
	private INLoadFilesPreviousFY pINLoadFilesPreviousFY;
	private INComparePreviousFYIncome pINComparePreviousFYIncome;
	
	/**
	 * Build and Write the output files for the missing Fiscal Years
	 */
	public final void run() {
		INMoveSavedFYToDriverOutput.run();
		/*
		 * Build list to do
		 */
		buildListDateFYToDo();
		if (pListDateFYToDo.size() > 0) {
			BasicPrintMsg.displayTitle(this, " Write files for fiscal year end");
			/*
			 * Compute the BKTransactions
			 */
			pFYIncomeStatementGenerator.generateBKHolder();
			pFYBalanceSheet.generateBKHolder();
			/*
			 * Run the income statements
			 */
			pFYIncomeStatementGenerator.run(pListDateFYToDo);
			/*
			 * Check the income of the previous FY are still the same (we should not touch the transaction once the FY is passed)
			 */
			pINLoadFilesPreviousFY.run();
			pINComparePreviousFYIncome.run();
			/*
			 * Balance sheet: compute, write files (BS + debug)
			 */
			pBSManager.run();
		}
		/*
		 * Write Debug Files
		 */
		pFYDebugFileManager.writeDebugFiles();
	}
	
	/**
	 * List the dates to do the FY from 20160331 until today<br>
	 * withdraw the dates for which there is already a file with this date in the folio of output<br>
	 */
	private void buildListDateFYToDo() {
		/*
		 * Build the list
		 */
		pListDateFYToDo = new ArrayList<Integer>();
		int lDateFY = StaticDate.getDATE_FY_BEGIN();
		int lDateEnd = BasicDateInt.getmPlusYear(BasicDateInt.getmToday(), 1);
		while (lDateFY < lDateEnd) {
			pListDateFYToDo.add(lDateFY);
			if (lDateFY < BasicDateInt.getmToday()) {
				pDateFYLast = lDateFY;
			}
			/*
			 * Move on
			 */
			lDateFY = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusYear(lDateFY, 1));
		}
	}

	/**
	 * If date is after today overwrite the file, if before today, write only if the file does not exist already
	 * @param _sDateNameFile
	 * @param _sPath
	 * @param _sName
	 * @param _sHeader
	 * @param _sListLineToWrite
	 */
	public static void writeFile_unless_already_exists(Object _sSender, String _sName, 
			String _sHeader,
			List<String> _sListLineToWrite) {
		if (!BasicFichiers.getmIsFileAlreadyExist(StaticDir.getOUTPUT_FY_COMPUTATION_VALIDATED()
				+ _sName)) {
			String lDir = StaticDir.getOUTPUT_FY_COMPUTATION_CURRENT();
			BasicFichiers.writeFile(lDir, _sName, _sHeader, _sListLineToWrite);
			BasicPrintMsg.display(_sSender, "File written '" + lDir + _sName + "'");
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final LNManager getpLNManager() {
		return pLNManager;
	}
	public final FYDebugFileManager getpFYDebugFileManager() {
		return pFYDebugFileManager;
	}
	public final FYIncomeStatementGenerator getpFYIncomeStatementGenerator() {
		return pFYIncomeStatementGenerator;
	}
	public final UOBMainManager getpUOBMainManager() {
		return pUOBMainManager;
	}
	public final List<Integer> getpListDateFYToDo() {
		return pListDateFYToDo;
	}
	public final FYBalanceSheet getpFYBalanceSheet() {
		return pFYBalanceSheet;
	}
	public static final int getpDateFYLast() {
		return pDateFYLast;
	}
	public final BSManager getpBSManager() {
		return pBSManager;
	}
	public final INLoadFilesPreviousFY getpINLoadFilesPreviousFY() {
		return pINLoadFilesPreviousFY;
	}
	public final INComparePreviousFYIncome getpINComparePreviousFYIncome() {
		return pINComparePreviousFYIncome;
	}
	
	
	
	
	
	
	
}
