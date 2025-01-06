package step5fiscalyearend.incomestatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.StaticNames;
import step5fiscalyearend.FYMain;
import step5fiscalyearend.conf.FYSortIncomeCategories;

public class FYIncomeStatementDelta {

	
	protected FYIncomeStatementDelta(FYIncomeStatement _sFYIncomeStatementM1, FYIncomeStatement _sFYIncomeStatement) {
		pFYIncomeStatement = _sFYIncomeStatement;
		pFYIncomeStatementM1 = _sFYIncomeStatementM1;
		/*
		 * 
		 */
		pMapBKIncomeToValueUSD = new HashMap<>();
	}
	
	/*
	 * Data
	 */
	private FYIncomeStatement pFYIncomeStatement;
	private FYIncomeStatement pFYIncomeStatementM1;
	private double pIncome;
	private Map<String, Double> pMapBKIncomeToValueUSD;
	
	
	/**
	 * 
	 */
	protected final void writeFile() {
		/*
		 * Find List of Category
		 */
		Map<String, Map<String, FYData>> lMapCategoryToMapNameToFYData = pFYIncomeStatement.getpMapCategoryToMapNameToFYData();
		Map<String, Map<String, FYData>> lMapCategoryToMapNameToFYDataM1 = null;
		if (pFYIncomeStatementM1 != null) {
			lMapCategoryToMapNameToFYDataM1 = pFYIncomeStatementM1.getpMapCategoryToMapNameToFYData();
		}
		List<String> lListCategory = new ArrayList<String>(lMapCategoryToMapNameToFYData.keySet());
		lListCategory.remove("null");
		Collections.sort(lListCategory, new FYSortIncomeCategories());
		/*
		 * Title
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		String lDateFYStr = BasicDateInt.getmDay(pFYIncomeStatement.getpDateFY()) 
				+ "/" + BasicDateInt.getmMonth(pFYIncomeStatement.getpDateFY())
				+ "/" + BasicDateInt.getmYear(pFYIncomeStatement.getpDateFY());
		lListLineToWrite.add("INCOME STATEMENT FOR THE FISCAL YEAR ENDING " + lDateFYStr);
		lListLineToWrite.add("");
		/*
		 * Build the file output
		 */
		double lTotal =0;
		for (String lCategory : lListCategory) {
			lListLineToWrite.add(lCategory);
			double lSubTotal = 0.;
			List<FYData> lListFYData = new ArrayList<FYData>(lMapCategoryToMapNameToFYData.get(lCategory).values());
			Collections.sort(lListFYData);
			for (FYData lFYData : lListFYData) {
				double lValueFY = lFYData.getpValueUSD();
				if (lMapCategoryToMapNameToFYDataM1 != null
						&& lMapCategoryToMapNameToFYDataM1.containsKey(lCategory)
						&& lMapCategoryToMapNameToFYDataM1.get(lCategory).containsKey(lFYData.getpName())) {
					lValueFY += -lMapCategoryToMapNameToFYDataM1.get(lCategory).get(lFYData.getpName()).getpValueUSD();
				}
				/*
				 * Build line and store it in the list
				 */
				String lLine = ""
						+ "," + lFYData.getpName()
						+ "," + lValueFY;
				lSubTotal += lValueFY;
				lTotal += lValueFY;
				lListLineToWrite.add(lLine);
				/*
				 * Store in the global map of this object
				 */
				pMapBKIncomeToValueUSD.put(lFYData.getpName(), lValueFY);
			}
			String lLine = "Total " + lCategory + ",,," + lSubTotal;
			lListLineToWrite.add(lLine);
			lListLineToWrite.add("");
		}
		String lLine = "Total net income,,,," + lTotal;
		lListLineToWrite.add(lLine);
		/*
		 * Store result
		 */
		pIncome = lTotal;
		/*
		 * Write file
		 */
		String lNameFile = pFYIncomeStatement.getpDateFYFile() + StaticNames.getOUTPUT_FY_INCOME_STATEMENT_DELTA();
		FYMain.writeFile_unless_already_exists(this, lNameFile, null, lListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final double getpIncome() {
		return pIncome;
	}
	public final FYIncomeStatement getpFYIncomeStatement() {
		return pFYIncomeStatement;
	}
	public final Map<String, Double> getpMapBKIncomeToValueUSD() {
		return pMapBKIncomeToValueUSD;
	}

	public final FYIncomeStatement getpFYIncomeStatementM1() {
		return pFYIncomeStatementM1;
	}
	
	
	
	
	
	
}
