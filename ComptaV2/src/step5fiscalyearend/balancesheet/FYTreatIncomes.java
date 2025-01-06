package step5fiscalyearend.balancesheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import step5fiscalyearend.incomestatement.FYIncomeStatementDelta;

public class FYTreatIncomes {

	protected FYTreatIncomes(FYBalanceSheet _sFYBalanceSheet) {
		pFYBalanceSheet = _sFYBalanceSheet;
	}

	/*
	 * Data
	 */
	private FYBalanceSheet pFYBalanceSheet;
	private Map<Integer, Double> pMapDateFYToFYIncome;
	private List<Integer> pListDateFY;
	private double pTotalIncome;
	
	/**
	 * Extract the Income of the current fiscal year and the previous fiscal years from the FYIncomeStatement
	 * Check that the total income of the current year matches the sum of the delta incomes
	 */
	protected final void run() {
		/*
		 * Initiate
		 */
		pMapDateFYToFYIncome = new HashMap<>();
		List<FYIncomeStatementDelta> lListFYIncomeStatementDelta = pFYBalanceSheet.getpFYMain()
				.getpFYIncomeStatementGenerator().getpListFYToIncomeStatementDelta();
		pTotalIncome = 0.;
		/*
		 * Compute the incomes every year
		 */
		for (FYIncomeStatementDelta lFYIncomeStatementDelta : lListFYIncomeStatementDelta) {
			int lDate = lFYIncomeStatementDelta.getpFYIncomeStatement().getpDateFYFile();
			double lIncome = lFYIncomeStatementDelta.getpIncome();
			if (lDate <= pFYBalanceSheet.getpDateNameFile()) {
				pMapDateFYToFYIncome.put(lDate, lIncome);
				pTotalIncome += lIncome;
			}
		}
		/*
		 * Check consistency
		 */
		if (!AMNumberTools.isEqual(pTotalIncome, pFYBalanceSheet.getpFYTreatBunker().getpNetIncomeUSD())) {
			BasicPrintMsg.error("The income computed in balance sheet is different than the sum of P/L of income statements"
					+ "\nTotal income from 'FYIncomeStatementDelta'= " + pTotalIncome
					+ "\nIncome from 'FYTreatBunker'= " + pFYBalanceSheet.getpFYTreatBunker().getpNetIncomeUSD());
		}
		/*
		 * List date FY
		 */
		pListDateFY = new ArrayList<Integer>(pMapDateFYToFYIncome.keySet());
		Collections.sort(pListDateFY);
	}

	/*
	 * Getters & Setters
	 */
	public final Map<Integer, Double> getpMapDateFYToFYIncome() {
		return pMapDateFYToFYIncome;
	}
	public final List<Integer> getpListDateFY() {
		return pListDateFY;
	}
	public final double getpTotalIncome() {
		return pTotalIncome;
	}
	
}
