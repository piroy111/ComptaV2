package step3statements.reports.reports.clientsholdingsendofmonth;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFileInLinesWithMap;
import staticdata.StaticDir;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;

class CHReportManagerFromImport {

	protected CHReportManagerFromImport(STBKHoldingsClientsEndOfMonth _sSTBKHoldingsClientsEndOfMonth) {
		pSTBKHoldingsClientsEndOfMonth = _sSTBKHoldingsClientsEndOfMonth;
		/*
		 * 
		 */
		pTreeMapDateToCHReport = new TreeMap<>();
	}
	
	/*
	 * Static
	 */
	private static boolean IS_DEBUG = true;
	/*
	 * Data
	 */
	private STBKHoldingsClientsEndOfMonth pSTBKHoldingsClientsEndOfMonth;
	private TreeMap<Integer, CHReport> pTreeMapDateToCHReport;
	
	
	/**
	 * 
	 */
	public final void loadExistingCHReports() {
		String lDir = StaticDir.getIMPORT_HOLDING_CLIENTS_END_OF_MONTH();
		String lSuffix = StaticNames.getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH();
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			/*
			 * Get or create CHReport
			 */
			int lDate = lBasicFile.getmDate();
			CHReport lCHReport = getpOrCreateCHReport(lDate);
			/*
			 * Get the list of BKAccount
			 */
			ReadFileInLinesWithMap lReadFileInLinesWithMap = new ReadFileInLinesWithMap(lDir + lBasicFile.getmNameFile(), true);
			String lNameColumnBKAccount = BKAccount.class.getSimpleName();
			if (!lReadFileInLinesWithMap.getmMapHeaderListItem().containsKey(lNameColumnBKAccount)) {
				BasicPrintMsg.error("The file header does not contain '" + lNameColumnBKAccount + "'"
						+ "\nDir= '" + lDir + "'"
						+ "\nFile= '" + lBasicFile.getmNameFile() + "'");
			}
			List<BKAccount> lListBKAccount = new ArrayList<>();
			List<String> lListBKAccountStr = lReadFileInLinesWithMap.getmMapHeaderListItem().get(lNameColumnBKAccount);
			for (String lBKAccountStr : lListBKAccountStr) {
				if (!BKAccountManager.getpMapNameToBKAccount().containsKey(lBKAccountStr)) {
					BasicPrintMsg.error("The file contains a BKAccount which is not in BKAccountManager"
							+ "\nBKAccount in error = '" + lBKAccountStr + "'"
							+ "\nDir= '" + lDir + "'"
							+ "\nFile= '" + lBasicFile.getmNameFile() + "'");
				}
				lListBKAccount.add(BKAccountManager.getpBKAccount(lBKAccountStr));
			}
			/*
			 * Get the list of BKAsset
			 */
			List<BKAsset> lListBKAsset = new ArrayList<>();
			List<String> lListBKAssetStr = lReadFileInLinesWithMap.getmHeadersAndCommentList().get(0);
			for (int lIdx = 1; lIdx < lListBKAssetStr.size(); lIdx++) {
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lListBKAssetStr.get(lIdx));
				lListBKAsset.add(lBKAsset);
			}
			/*
			 * Fill CHReport with file content
			 */
			for (BKAccount lBKAccount : lListBKAccount) {
				for (BKAsset lBKAsset : lListBKAsset) {
					String lHoldingStr = lReadFileInLinesWithMap.getmItem(lNameColumnBKAccount, lBKAsset.getpName(), lBKAccount.getpEmailAddress());
					double lHolding = BasicString.getDouble(lHoldingStr);
					lCHReport.putNewHolding(lBKAccount, lBKAsset, lHolding);
				}
			}
		}
		/*
		 * Debug -> write files in debug
		 */
		if (IS_DEBUG) {
			for (CHReport lCHReport : pTreeMapDateToCHReport.values()) {
				lCHReport.writeReport(StaticDir.getOUTPUT_DEBUG());
			}
		}
	}
	
	/**
	 * 
	 */
	public final void checkCHReport() {
		for (CHReport lCHReportFromImport : pTreeMapDateToCHReport.values()) {
			CHReport lCHReportFromBKHolder = pSTBKHoldingsClientsEndOfMonth.getpCHReportManagerFromBKHolder().getpTreeMapDateToCHReport().get(lCHReportFromImport.getpDate());
			List<String> lListDifference = lCHReportFromImport.compare(lCHReportFromBKHolder);
			if (lListDifference.size() > 0) {
				String lHeader = "BKAccount,BKAsset,Value previously stored in 'COMPTA/01../15..',Value computed now by the program";
				String lDir = StaticDir.getOUTPUT_DEBUG();
				String lName = lCHReportFromImport.getpDate() + "_List_discrepencies_balances.csv";
				BasicFichiers.writeFile(lDir, lName, lHeader, lListDifference);
				BasicPrintMsg.error("There are discrepencies between the report of balances from the website and what I recompute real time"
						+ "\nFile import website= '" + StaticDir.getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH() + lCHReportFromImport.getpDate() 
						+ StaticNames.getOUTPUT_HOLDING_CLIENTS_END_OF_MONTH()
						+ "\nFile with the discrepencies= '" + lDir + lName + "'");
			}
		}
		BasicPrintMsg.display(this,  "All good");
	}

	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final CHReport getpOrCreateCHReport(int _sDate) {
		CHReport lCHReport = pTreeMapDateToCHReport.get(_sDate);
		if (lCHReport == null) {
			lCHReport = new CHReport(_sDate);
			pTreeMapDateToCHReport.put(_sDate, lCHReport);
		}
		return lCHReport;
	}

	/*
	 * Getters & Setters
	 */
	public static final boolean isIS_DEBUG() {
		return IS_DEBUG;
	}
	public final STBKHoldingsClientsEndOfMonth getpSTBKHoldingsClientsEndOfMonth() {
		return pSTBKHoldingsClientsEndOfMonth;
	}
	public final TreeMap<Integer, CHReport> getpTreeMapDateToCHReport() {
		return pTreeMapDateToCHReport;
	}
	
	
	
	
	
	
	
	
	
	
}
