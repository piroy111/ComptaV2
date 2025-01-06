package step0treatrawdata.clientsrefinersbrokers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticBKIncome;
import staticdata.StaticBKIncome.BKI_HEDGING_SUB;
import staticdata.StaticDir;
import step0treatrawdata.bktransactionquick.BKTransactionQuick;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.postchecker.ColumnIdx;
import step1loadtransactions.accounts.BKAccountManager;

public class TREATRefiners extends TREATRoot {

	public TREATRefiners(TREATManager _sTREATManager) {
		super(_sTREATManager, 
				StaticDir.getIMPORT_REFINERS_PURCHASE(), 
				StaticDir.getTREATED_REFINERS_PURCHASE());
	}

	public final void run(LitUnFichierEnLignes _sReadFile) {
	}

	@Override public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate for new file
		 */
		Map<String, Integer> lMapHeaderToIdxColumn = ColumnIdx.getMapIdxColumns(_sReadFile);
		/*
		 * Check if old version
		 */
		boolean lIsOldVersion = !lMapHeaderToIdxColumn.containsKey("BKPrice");
		if (lIsOldVersion) {
			oldVversion(_sReadFile);
		} else {
			newVersion(_sReadFile);
		}
	}

	/**
	 * Case there is a column BKPrice
	 * @param _sReadFile
	 */
	private void newVersion(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate for new file
		 */
		String lNameFile = _sReadFile.getmNomFichier();
		List<String> lListLineToWrite = new ArrayList<String>();
		Map<String, Integer> lMapHeaderToIdxColumn = ColumnIdx.getMapIdxColumns(_sReadFile);
		/*
		 * Header
		 */
		String lHeader = _sReadFile.getmHeadersAndComments().get(0);
		lHeader += ",BKAccount,BKIncome";
		/*
		 * Fill
		 */
		for (int lIdx = 0; lIdx < _sReadFile.getmContenuFichierLignes().size(); lIdx++) {
			/*
			 * Load line from file origin
			 */
			String lLineStr = _sReadFile.getmContenuFichierLignes().get(lIdx);
			List<String> lLineList = _sReadFile.getmContenuFichierListe().get(lIdx);
			BKTransactionQuick lBKTransactionQuick = new BKTransactionQuick(lLineList, lMapHeaderToIdxColumn);
			BKAsset lBKAsset = lBKTransactionQuick.getpBKAsset();
			/*
			 * Add the BKAccount
			 */
			lLineStr += "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress();
			/*
			 * Case the asset is not a paper --> we add the BKIncome and BKPrice to the line
			 */
			if (!lBKAsset.getpIsPaper()) {
				lLineStr += ",";
				if (lBKAsset.getpIsCurrency()) {
					lLineStr += StaticBKIncome.getWIRE_OUT_REFINERS();
				} else if (lBKAsset.getpIsBar()) {
					lLineStr += StaticBKIncome.getBARS_BUNKER_BOUGHT_FROM_REFINER();
				} else {
					lLineStr += "Refiner Others";
				}
				lListLineToWrite.add(lLineStr);
			}
			/*
			 * Case the BKAsset is paper (XAU, XAG, XPT) --> we create a mirror transaction
			 */
			else {
				/*
				 * Load transaction
				 */
				String[] lArray = lLineStr.split(",", -1);
				lArray[3] = "" + (-BasicString.getDouble(lArray[3]));
				/*
				 * BKIncome
				 */
				String lBKIncomeRefiner = StaticBKIncome.getHEDGING() + " " + lArray[1] + " " + BKI_HEDGING_SUB.Refiner.toString();
				String lBKIncomeHedging = StaticBKIncome.getHEDGING() + " " + lArray[1] + " " + BKI_HEDGING_SUB.Trades.toString();
				/*
				 * Build the Line and store it
				 */
				lLineStr += "," + lBKIncomeRefiner;
				lListLineToWrite.add(lLineStr);
				/*
				 * Build line mirror and store it
				 */
				String lLineStrMirror = lArray[0];				
				for (int lKdx = 1; lKdx < lArray.length; lKdx++) {
					lLineStrMirror += "," + lArray[lKdx];
				}
				lLineStrMirror += "," + lBKIncomeHedging;
				lListLineToWrite.add(lLineStrMirror);
			}
		}
		/*
		 * Write file treated
		 */
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
	}

	/**
	 * Old version --> case there is no column BKPrice
	 * @param _sReadFile
	 */
	private void oldVversion(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate for new file
		 */
		String lNameFile = _sReadFile.getmNomFichier();
		List<String> lListLineToWrite = new ArrayList<String>();
		Map<String, Integer> lMapHeaderToIdxColumn = ColumnIdx.getMapIdxColumns(_sReadFile);
		/*
		 * Header
		 */
		String lHeader = _sReadFile.getmHeadersAndComments().get(0);
		lHeader += ",BKIncome,BKPrice";
		/*
		 * Case old version --> we compute BKPrice: we take only the cash which is negative
		 */
		double lQuantityOz = 0.;
		double lCashUSD = 0.;
		for (int lIdx = 0; lIdx < _sReadFile.getmContenuFichierLignes().size(); lIdx++) {
			List<String> lLineList = _sReadFile.getmContenuFichierListe().get(lIdx);
			BKTransactionQuick lBKTransactionQuick = new BKTransactionQuick(lLineList, lMapHeaderToIdxColumn);
			BKAsset lBKAsset = lBKTransactionQuick.getpBKAsset();
			if (lBKAsset.getpIsCurrency()) {
				if (!lBKAsset.getpName().equals("USD")) {
					BasicPrintMsg.error("Refiner must be in USD");
				}
				lCashUSD += -Math.min(0, lBKTransactionQuick.getpQuantity());
			} else if (lBKAsset.getpIsBar()) {
				lQuantityOz += lBKTransactionQuick.getpQuantity();
			} else {
				BasicPrintMsg.error("A refiner can have only cash or bars");
			}
		}
		double lBKPriceComputed = lCashUSD / lQuantityOz;
		/*
		 * Fill
		 */
		for (int lIdx = 0; lIdx < _sReadFile.getmContenuFichierLignes().size(); lIdx++) {
			/*
			 * Load line from file origin
			 */
			String lLineStr = _sReadFile.getmContenuFichierLignes().get(lIdx);
			List<String> lLineList = _sReadFile.getmContenuFichierListe().get(lIdx);
			BKTransactionQuick lBKTransactionQuick = new BKTransactionQuick(lLineList, lMapHeaderToIdxColumn);
			BKAsset lBKAsset = lBKTransactionQuick.getpBKAsset();
			/*
			 * Case the asset is not a paper --> we add the BKIncome and BKPrice to the line
			 */
			lLineStr += ",";
			if (lBKAsset.getpIsCurrency()) {
				lLineStr += StaticBKIncome.getCLIENT_CASH_SPENDING();
			} else if (lBKAsset.getpIsBar()) {
				lLineStr += StaticBKIncome.getBARS_BUNKER_BOUGHT_FROM_REFINER();
			} else {
				lLineStr += "Refiner Others";
			}
			lLineStr += "," + lBKPriceComputed;
			lListLineToWrite.add(lLineStr);
		}
		/*
		 * Write file treated
		 */
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
	}









}
