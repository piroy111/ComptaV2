package step5fiscalyearend.incomestatement.compareincomeprevious;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step5fiscalyearend.FYMain;
import step5fiscalyearend.incomestatement.FYIncomeStatementDelta;
import step5fiscalyearend.incomestatement.FYIncomeStatementGenerator;
import step5fiscalyearend.incomestatement.loadfilesprevious.INFileFY;

public class INComparePreviousFYIncome {

	public INComparePreviousFYIncome(FYMain _sFYMain) {
		pFYMain = _sFYMain;
	}
	
	/*
	 * Data
	 */
	private FYMain pFYMain;
	
	/**
	 * 
	 */
	public final void run() {
		FYIncomeStatementGenerator lFYIncomeStatementGenerator = pFYMain.getpFYIncomeStatementGenerator();
		for (FYIncomeStatementDelta lFYIncomeStatementDelta : lFYIncomeStatementGenerator.getpListFYToIncomeStatementDelta()) {
			/*
			 * Look for the existing file income for the FY date
			 */
			int lDateFY = lFYIncomeStatementDelta.getpFYIncomeStatement().getpDateFY();
			INFileFY lINFileFY = pFYMain.getpINLoadFilesPreviousFY().getpMapDateToINFileFY().get(lDateFY);
			if (lINFileFY != null) {
				/*
				 * Determine the union of the BKIncome present in the FY and FY-1
				 */
				Set<String> lSetBKIncome = new HashSet<>();
				lSetBKIncome.addAll(lFYIncomeStatementDelta.getpMapBKIncomeToValueUSD().keySet());
				lSetBKIncome.addAll(lINFileFY.getpMapBKIncomeStrToValueUSD().keySet());
				List<String> lListBKIncome = new ArrayList<>(lSetBKIncome);
				Collections.sort(lListBKIncome);
				/*
				 * Build the file content
				 */
				List<String> lListLineToWrite = new ArrayList<>();
				for (String lBKIncome : lListBKIncome) {
					String lLine = lBKIncome
							+ "," + lINFileFY.getpMapBKIncomeStrToValueUSD().get(lBKIncome)
							+ "," + lFYIncomeStatementDelta.getpMapBKIncomeToValueUSD().get(lBKIncome);
					lListLineToWrite.add(lLine);
				}
				/*
				 * Write the file debug in any case (even if there is no bug)
				 */
				String lHeader = "BKIncome,Value$ in previous file,Value$ of the java program";
				String lDir = StaticDir.getOUTPUT_FY_DEBUG_IN_COMPARISON();
				String lNameFile = BasicDateInt.getmToday() 
						+ StaticNames.getOUTPUT_FY_DEBUG_IN_COMPARISON()
						+ lDateFY + ".csv";
				BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
				/*
				 * Check if there is a bug
				 */
				double lIncomeTotalFilePrevious = 0.;
				double lIncomeTotalJava = 0.;
				for (String lBKIncome : lListBKIncome) {
					Double lValueUSDFile = lINFileFY.getpMapBKIncomeStrToValueUSD().get(lBKIncome);
					Double lValueUSD = lFYIncomeStatementDelta.getpMapBKIncomeToValueUSD().get(lBKIncome);
					if (lValueUSDFile != null) {
						lIncomeTotalFilePrevious += lValueUSDFile;
					}
					if (lValueUSD != null) {
						lIncomeTotalJava += lValueUSD;
					}
				}
				/*
				 * Kill if the P/L is too different
				 */
				if (Math.abs(lIncomeTotalFilePrevious - lIncomeTotalJava) > 10) {
					String lMsg = "The income computed is different than the income stored and filed to the authorities in the FY report of FY= " + lDateFY
							+ "\nValue in file= " + BasicPrintMsg.afficheIntegerWithComma(lIncomeTotalFilePrevious) + " $"
							+ "\nValue computed in java= " + BasicPrintMsg.afficheIntegerWithComma(lIncomeTotalJava) + " $"
							+ "\nYou can use the file debug just written to understand further: '" + lDir + lNameFile + "'";
					BasicPrintMsg.error(lMsg);
				}
			}
		}
	}
	
}
