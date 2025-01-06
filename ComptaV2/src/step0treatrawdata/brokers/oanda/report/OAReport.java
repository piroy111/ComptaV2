package step0treatrawdata.brokers.oanda.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticAsset;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class OAReport {

	public OAReport(LitUnFichierEnLignes _sReadFile, OAReportManager _sOAReportManager) {
		pReadFile = _sReadFile;
		pOAReportManager = _sOAReportManager;
		/*
		 * 
		 */
		pDate = BasicString.getInt(BasicString.getDate(_sReadFile.getmNomFichier()));
		loadFile();
	}

	/*
	 * Data
	 */
	private LitUnFichierEnLignes pReadFile;
	private OAReportManager pOAReportManager;
	private List<OATransaction> pListOATransaction;
	private int pDate;

	/**
	 * Load file and create the OATransaction
	 */
	private void loadFile() {
		/*
		 * Initiate
		 */
		pListOATransaction = new ArrayList<OATransaction>();
		boolean lIsNewVersion = pReadFile.getmContenuFichierListe().get(0).contains("Direction");
		/*
		 * Read file
		 */
		for (int lIdxLine = 1; lIdxLine < pReadFile.getmContenuFichierListe().size(); lIdxLine++) {
			List<String> lLineStr = pReadFile.getmContenuFichierListe().get(lIdxLine);
			if (lIsNewVersion) {
				readLineNewVersion(lLineStr);
			} else {
				readLineOldVersion(lLineStr);
			}
		}
		/*
		 * Sort
		 */
		Collections.sort(pListOATransaction);
	}

	/**
	 * New version of OANDA files
	 * @param _sLineStr
	 */
	private void readLineNewVersion(List<String> _sLineStr) {
		/*
		 * Load line
		 */
		int lIdx = -1;
		String lID = _sLineStr.get(++lIdx);
		String lDateStr = _sLineStr.get(++lIdx);
		++lIdx;
		String lTransaction = _sLineStr.get(++lIdx);
		String lDetails = _sLineStr.get(++lIdx);
		String lSymbol = _sLineStr.get(++lIdx);
		double lPrice = BasicString.getDouble(_sLineStr.get(++lIdx));
		double lQuantity = BasicString.getDouble(_sLineStr.get(++lIdx));
		String lDirection = _sLineStr.get(++lIdx);
		++lIdx;
		++lIdx;
		++lIdx;
		++lIdx;
		double lInterests = BasicString.getDouble(_sLineStr.get(++lIdx));
		double lCommissions = BasicString.getDouble(_sLineStr.get(++lIdx));
		++lIdx;
		double lAmount = BasicString.getDouble(_sLineStr.get(++lIdx));
		/*
		 * Check ID
		 */
		if (lID.equals("") || !pOAReportManager.checkAndStoreNewID(lID, true)) {
			return;
		}
		/*
		 * Is a trade or a cash flow?
		 */
		boolean lIsTradeOrCash = !AMNumberTools.isZero(lPrice);
		/*
		 * Case of a Trade: Buy or Sell for a Trade ? + commissions
		 */
		BKAsset lBKAsset = null;
		String lComment = null;
		if (lIsTradeOrCash) {
			/*
			 * Skip if fake trade
			 */
			if (!lTransaction.equals("ORDER_FILL")) {
				return;
			}
			if (lDetails.contains("MIGRATION")) {
				return;
			}
			/*
			 * Find the way: buy or sell
			 */
			lComment = "Trade";
			if (lDirection.equals("Sell")) {
				lQuantity = -lQuantity;
			} else if (!lDirection.equals("Buy")) {
				BasicPrintMsg.error("Error"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmNomCheminPlusFichier());
			}
			/*
			 * Commissions
			 */
			lCommissions = lInterests + lCommissions;
		}
		/*
		 * Case of a cash flow
		 */
		else {
			if (AMNumberTools.isNaNOrZero(lInterests) && AMNumberTools.isNaNOrZero(lCommissions)) {
				return;
			}
			if (lTransaction.equals("TRANSFER_FUNDS")) {
				lComment = "Deposit";
				lQuantity = lAmount;
			} else if (lTransaction.equals("DAILY_FINANCING")) {
				lComment = "Interests";
				lQuantity = lInterests;
			} else {
				BasicPrintMsg.error("Error"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmNomCheminPlusFichier());
			}			
			lBKAsset = BKAssetManager.getpAndCheckBKAsset("USD");
		}
		/*
		 * lBKAsset
		 */
		double lQuantityOANDA = lQuantity;
		if (lBKAsset == null) {
			lBKAsset = getBKAsset(lSymbol);
			if (getIsInvertedPrice(lSymbol)) {
				lQuantity = -lQuantity * lPrice;
				lPrice = 1/ lPrice;
			}
		}
		/*
		 * Date
		 */
		String[] lArray = lDateStr.split("/", -1);
		int lDay = BasicString.getInt(lArray[1]);
		int lMonth = BasicString.getInt(lArray[0]);
		int lYear = BasicString.getInt(lArray[2].substring(0, 4));
		int lDate = BasicDateInt.getmDateInt(lYear, lMonth, lDay);
		/*
		 * Create OATransaction
		 */
		OATransaction lOATransaction = new OATransaction(lID, lComment, lBKAsset, lQuantity, lDate, lPrice, lIsTradeOrCash, lSymbol, lQuantityOANDA);
		/*
		 * Case the transaction is a migration --> we keep it apart
		 */
		if (lDetails.contains(OAStatic.getDETAILS_MIGRATION())) {
			pOAReportManager.getpOAMigrationManager().declareNewOATransactionMigration(lOATransaction);
		}
		/*
		 * Case of a normal transaction --> we keep it in the report
		 */
		pListOATransaction.add(lOATransaction);
		if (!AMNumberTools.isNaNOrZero(lCommissions)) {
			OATransaction lOATransactionComm = new OATransaction(lID, "Commissions", BKAssetManager.getpAndCheckBKAsset("USD"), 
					lCommissions, lDate, Double.NaN, lIsTradeOrCash, lSymbol, lQuantityOANDA);
			pListOATransaction.add(lOATransactionComm);
		}
	}

	/**
	 * 
	 * @param _sLineStr
	 */
	private void readLineOldVersion(List<String> _sLineStr) {
		/*
		 * Load line
		 */
		int lIdx = -1;
		String lID = _sLineStr.get(++lIdx);
		++lIdx;
		String lType = _sLineStr.get(++lIdx);
		String lSymbol = _sLineStr.get(++lIdx);
		double lQuantity = BasicString.getDouble(_sLineStr.get(++lIdx));
		String lDateStr = _sLineStr.get(++lIdx);
		double lPrice = BasicString.getDouble(_sLineStr.get(++lIdx));
		++lIdx;
		double lInterests = BasicString.getDouble(_sLineStr.get(++lIdx));
		++lIdx;
		++lIdx;
		++lIdx;
		double lAmount = BasicString.getDouble(_sLineStr.get(++lIdx));
		/*
		 * Continue if it is a migration
		 */
		if (lType.contains(" (System Migration)")) {
			return;
		}
		/*
		 * Check ID
		 */
		if (!pOAReportManager.checkAndStoreNewID(lID, false)) {
			return;
		}
		/*
		 * Is a trade or a cash flow?
		 */
		boolean lIsTradeOrCash = !AMNumberTools.isZero(lPrice);
		/*
		 * Case of a Trade: Buy or Sell for a Trade ? + commissions
		 */
		BKAsset lBKAsset = null;
		String lComment = null;
		double lCommissions = Double.NaN;
		if (lIsTradeOrCash) {
			/*
			 * Skip if fake trade
			 */
			if (AMNumberTools.isNaNOrZero(lAmount)) {
				return;
			}
			/*
			 * Special case of the issue I have every month
			 */
			if (lID.equals("11090345542")) {
				lType = "Sell";
			}
			/*
			 * Find the way: buy or sell
			 */
			lComment = "Trade";
			if (lType.contains("Sell")) {
				lQuantity = -lQuantity;
			} else if (!lType.contains("Buy")) {
				BasicPrintMsg.error("We cannot know the way(buy/sell); of the trade."
						+ " You must put it manually by putting 'Buy' or 'Sell' in the column 'Type' of the csv file"
						+ "\nLineStr= " + _sLineStr.toString()
						+ "\nFile= " + pReadFile.getmNomCheminPlusFichier());
			}
			/*
			 * Commissions
			 */
			lCommissions = lInterests;
		}
		/*
		 * Case of a cash flow
		 */
		else {
			if (lType.equals("Interest")) {
				lComment = "Interests";
				lQuantity = lInterests;
			} else  {
				lComment = "Deposit";
				lQuantity = lAmount;
				if (lType.toUpperCase().contains("WITHDRAWAL")) {
					lQuantity = -lQuantity;
				}

			}			
			lBKAsset = BKAssetManager.getpAndCheckBKAsset("USD");
		}
		/*
		 * lBKAsset
		 */
		double lQuantityOANDA = lQuantity;
		if (lBKAsset == null) {
			lBKAsset = getBKAsset(lSymbol);
			if (getIsInvertedPrice(lSymbol)) {
				lQuantity = -lQuantity * lPrice;
				lPrice = 1/ lPrice;
			}
		}
		/*
		 * Date
		 */
		String[] lArray = lDateStr.split("-", -1);
		int lDay = BasicString.getInt(lArray[2]);
		int lMonth = BasicString.getInt(lArray[1]);
		int lYear = BasicString.getInt(lArray[0]);
		int lDate = BasicDateInt.getmDateInt(lYear, lMonth, lDay);
		/*
		 * Create OATransaction
		 */
		OATransaction lOATransaction = new OATransaction(lID, lComment, lBKAsset, lQuantity, lDate, lPrice, lIsTradeOrCash, lSymbol, lQuantityOANDA);
		pListOATransaction.add(lOATransaction);
		if (!AMNumberTools.isNaNOrZero(lCommissions)) {
			OATransaction lOATransactionComm = new OATransaction(lID, "Commissions", BKAssetManager.getpAndCheckBKAsset("USD"), 
					lCommissions, lDate, Double.NaN, lIsTradeOrCash, lSymbol, lQuantityOANDA);
			pListOATransaction.add(lOATransactionComm);
		}
	}

	/**
	 * Convert the asset OANDA into a BKAsset
	 * @param _sAssetOANDA
	 * @return
	 */
	public static BKAsset getBKAsset(String lSymbol) {
		/*
		 * Initiate
		 */
		String[] lArrayStr = lSymbol.split("/", -1);
		String lSymbol0 = lArrayStr[0];
		String lSymbol1 = lArrayStr[1];
		String lNameAsset;
		/*
		 * Build Symbol
		 */
		if (lSymbol0.startsWith("X") || lSymbol.length() == 3) {
			lNameAsset = lSymbol0;
		} else if (lSymbol0.equals(OAStatic.getOIL())) {
			lNameAsset = StaticAsset.getOIL();
		} else if (lSymbol1.equals("USD")) {
			lNameAsset = "X" + lSymbol0;
		} else {
			lNameAsset = "X" + lSymbol1;
		}
		/*
		 * Return BKAsset
		 */
		return BKAssetManager.getpAndCheckBKAsset(lNameAsset);
	}
	
	/**
	 * If the asset is inverted we should do the following:<br>
	 * Quantity -> -Quantity * Price
	 * Price -> 1 / Price; 
	 * @param lSymbol
	 * @return
	 */
	public static boolean getIsInvertedPrice(String lSymbol) {
		return lSymbol.startsWith("USD");
	}
	
	/*
	 * Getters & Setters
	 */
	public final List<OATransaction> getpListOATransaction() {
		return pListOATransaction;
	}
	public final int getpDate() {
		return pDate;
	}
















}
