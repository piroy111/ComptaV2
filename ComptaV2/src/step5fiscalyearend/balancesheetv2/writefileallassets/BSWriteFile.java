package step5fiscalyearend.balancesheetv2.writefileallassets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticNames;
import step5fiscalyearend.FYMain;
import step5fiscalyearend.balancesheetv2.BSManager;
import step5fiscalyearend.balancesheetv2.assets.BSAsset;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator;
import step5fiscalyearend.balancesheetv2.assets.BSAssetGenerator.BS_TYPE;
import step5fiscalyearend.incomestatement.FYIncomeStatementDelta;

public class BSWriteFile {

	public BSWriteFile(BSManager _sBSManager) {
		pBSManager = _sBSManager;
	}

	/*
	 * Data
	 */
	private BSManager pBSManager;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Write file balance sheet");
		/*
		 * Special case of the equity: we need to store the FY income 
		 */
		TreeMap<Integer, Double> lTreeMapDateFYToIncomeFY = new TreeMap<>();
		for (FYIncomeStatementDelta lFYIncomeStatementDelta : pBSManager.getpFYMain().getpFYIncomeStatementGenerator().getpListFYToIncomeStatementDelta()) {
			int lDateFYIncomeDelta = lFYIncomeStatementDelta.getpFYIncomeStatement().getpDateFY();
			double lIncomeDelta = lFYIncomeStatementDelta.getpIncome();
			lTreeMapDateFYToIncomeFY.put(lDateFYIncomeDelta, lIncomeDelta);
		}
		/*
		 * Loop on all the date FY
		 */
		for (int lDateFY : pBSManager.getpFYMain().getpListDateFYToDo()) {
			List<String> lListLineToWrite = new ArrayList<String>();
			/*
			 * Title
			 */
			String lDateFYStr = BasicDateInt.getmDay(lDateFY) 
					+ "/" + BasicDateInt.getmMonth(lDateFY)
					+ "/" + BasicDateInt.getmYear(lDateFY);
			lListLineToWrite.add("BALANCE SHEET FOR FISCAL YEAR ENDING " + lDateFYStr);
			lListLineToWrite.add("");
			/*
			 * Loop over the types
			 */
			Map<BS_TYPE, Double> lMapBSTypeToTotal = new HashMap<>();
			for (BS_TYPE lBSType : BSAssetGenerator.BS_TYPE.values()) {
				if (lBSType != BS_TYPE.Mirror) {
					double lTotalUSD = 0;
					lMapBSTypeToTotal.put(lBSType, lTotalUSD);
					/*
					 * Write sub-title
					 */
					String lBSTypeStr = lBSType.toString().toUpperCase();
					lListLineToWrite.add(lBSTypeStr);
					/*
					 * Write a line for each asset and write the value $ 
					 */
					List<BSAsset> lListBSAsset = pBSManager.getpBSAssetManager().getpMapBSTypeToBSAsset().get(lBSType);
					Collections.sort(lListBSAsset);
					for (BSAsset lBSAsset : lListBSAsset) {
						Double lValueUSD = lBSAsset.getpAndComputeValueUSD(lDateFY);
						if (lValueUSD != null && !AMNumberTools.isZero(lValueUSD)) {
							String lLine = "," + lBSAsset.getpTitle() + "," + lValueUSD;
							lListLineToWrite.add(lLine);
							/*
							 * Store the total for future display and check
							 */
							lTotalUSD += lValueUSD;
							lMapBSTypeToTotal.put(lBSType, lTotalUSD);
						}
					}
					/*
					 * Special case of the equity --> we must write the incomes
					 */
					if (lBSType == BS_TYPE.Equities) {
						for (int lDateFYIncome : lTreeMapDateFYToIncomeFY.keySet()) {
							if (lDateFYIncome > lDateFY) {
								break;
							}
							double lValueUSD = lTreeMapDateFYToIncomeFY.get(lDateFYIncome);
							String lLine = "," + "Retained Earnings FY ending " + lDateFYIncome
									+ "," + lValueUSD;
							lListLineToWrite.add(lLine);
							/*
							 * Store the total for future display and check
							 */
							lTotalUSD += lValueUSD;
							lMapBSTypeToTotal.put(lBSType, lTotalUSD);
						}
					}
					/*
					 * Write the total
					 */
					lListLineToWrite.add("Total " + lBSTypeStr + ",,," + lTotalUSD);
				}
			}
			/*
			 * Write the grand total
			 */
			double lTotalAsset = lMapBSTypeToTotal.get(BS_TYPE.Assets);
			double lTotalLiabilities = lMapBSTypeToTotal.get(BS_TYPE.Liabilities);
			double lTotalEquity = lMapBSTypeToTotal.get(BS_TYPE.Equities);
			double lGrandTotal = Math.round((lTotalAsset - lTotalLiabilities - lTotalEquity) * 1000.) / 1000.;
			lListLineToWrite.add("Grand total (ASSETS-LIABILITIES-EQUITY) must = 0,,," 
					+ lGrandTotal);
			/*
			 * Write file
			 */
			String lNameFile = lDateFY + StaticNames.getOUTPUT_FY_BALANCE_SHEET();
			FYMain.writeFile_unless_already_exists(this, lNameFile, null, lListLineToWrite);
			/*
			 * Check error grand total is not zero
			 */
			if (!AMNumberTools.isEqual(lGrandTotal, 0)) {
				BasicPrintMsg.error("The grand total of the balance sheet is not zero; GrandTotal= " + lGrandTotal
						+ "\nCheck the file output= '" + lNameFile + "'");
			} else {
				BasicPrintMsg.display(this, "Success: grand total of BS == 0 for date FY= " + lDateFY);
			}

		}
	}













}
