package step5fiscalyearend.incomestatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step5fiscalyearend.conf.FYConfManager;
import step5fiscalyearend.conf.FYSortIncomeCategories;

public class FYIncomeStatement implements Comparable<FYIncomeStatement> {

	public FYIncomeStatement(FYIncomeStatementGenerator _sFYIncomeStatementGenerator, int _sDateFY) {
		pFYIncomeStatementGenerator = _sFYIncomeStatementGenerator;
		pDateFY = _sDateFY;
	}

	/*
	 * Data
	 */
	private Map<String, Map<String, FYData>> pMapCategoryToMapNameToFYData;
	private int pDateFY;
	private FYIncomeStatementGenerator pFYIncomeStatementGenerator;
	private int pDateFYFile;
	
	/**
	 * Write the file of Income Statement
	 * @param _sDateFY
	 */
	public final void writeFile() {
		pDateFYFile = pDateFY;
		String lNameFile = pDateFYFile + StaticNames.getOUTPUT_FY_INCOME_STATEMENT();
		if (pDateFY > BasicDateInt.getmToday()) {
			pDateFY = BasicDateInt.getmToday();
		}
		/*
		 * Initiate
		 */
		pMapCategoryToMapNameToFYData = new HashMap<>();
		/*
		 * Fill the Map of FYData (pMapCategoryToListFYData)
		 */
		for (BKHolder lBKHolder : pFYIncomeStatementGenerator.getpListBKHolder()) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(pDateFY);
			if (lBKInventory != null) {
				/*
				 * Load data
				 */
				String lBKIncome = lBKHolder.getpKey();
				String lCategory = FYConfManager.getpCategory(lBKIncome);
				double lValueUSD = lBKInventory.getpValueUSD();
				/*
				 * Create FYData
				 */
				FYData lFYData = new FYData(lBKIncome, lValueUSD, lCategory);
				Map<String, FYData> lMapNameToFYData = pMapCategoryToMapNameToFYData.get(lCategory);
				if (lMapNameToFYData == null) {
					lMapNameToFYData = new HashMap<>();
					pMapCategoryToMapNameToFYData.put(lCategory, lMapNameToFYData);
				}
				lMapNameToFYData.put(lFYData.getpName(), lFYData);
			}
		}
		/*
		 * Check for missing categories
		 */
		FYConfManager.checkMissing();
		/*
		 * Build the file output
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		/*
		 * Title
		 */
		String lDateFYStr = BasicDateInt.getmDay(pDateFYFile) 
				+ "/" + BasicDateInt.getmMonth(pDateFYFile)
				+ "/" + BasicDateInt.getmYear(pDateFYFile);
		lListLineToWrite.add("INCOME STATEMENT SINCE THE CREATION OF THE COMPANY UNTIL FISCAL YEAR ENDING " + lDateFYStr);
		lListLineToWrite.add("");
		/*
		 * Content
		 */
		List<String> lListCategory = new ArrayList<String>(pMapCategoryToMapNameToFYData.keySet());
		lListCategory.remove("null");
		Collections.sort(lListCategory, new FYSortIncomeCategories());
		double lTotal =0;
		for (String lCategory : lListCategory) {
			lListLineToWrite.add(lCategory);
			double lSubTotal = 0.;
			Map<String, FYData> lMapNameToFYData = pMapCategoryToMapNameToFYData.get(lCategory);
			List<FYData> lListFYData = new ArrayList<>(lMapNameToFYData.values());
			Collections.sort(lListFYData);
			for (FYData lFYData : lListFYData) {
				String lLine = ""
						+ "," + lFYData.getpName()
						+ "," + lFYData.getpValueUSD();
				lSubTotal += lFYData.getpValueUSD();
				lTotal += lFYData.getpValueUSD();
				lListLineToWrite.add(lLine);
			}
			String lLine = "Total " + lCategory + ",,," + lSubTotal;
			lListLineToWrite.add(lLine);
			lListLineToWrite.add("");
		}
		String lLine = "Total net income,,,," + lTotal;
		lListLineToWrite.add(lLine);
		/*
		 * Write file
		 */
		String lDir = StaticDir.getOUTPUT_FY_DEBUG();
		BasicFichiers.writeFile(lDir, lNameFile, null, lListLineToWrite);
		/*
		 * Com
		 */
		System.out.println("File written: " + lDir + lNameFile);
	}

	@Override public int compareTo(FYIncomeStatement _sFYIncomeStatement) {
		return Integer.compare(pDateFY, _sFYIncomeStatement.getpDateFY());
	}
	
	/*
	 * Getters & Setters
	 */
	public final Map<String, Map<String, FYData>> getpMapCategoryToMapNameToFYData() {
		return pMapCategoryToMapNameToFYData;
	}
	public final int getpDateFY() {
		return pDateFY;
	}

	public final int getpDateFYFile() {
		return pDateFYFile;
	}

	


	
}
