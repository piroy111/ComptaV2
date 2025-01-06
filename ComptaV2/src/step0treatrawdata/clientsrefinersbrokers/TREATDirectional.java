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

public class TREATDirectional extends TREATRoot {

	public TREATDirectional(TREATManager _sTREATManager) {
		super(_sTREATManager, 
				StaticDir.getIMPORT_DIRECTIONAL(), 
				StaticDir.getTREATED_DIRECTIONAL());
	}

	public final void run(LitUnFichierEnLignes _sReadFile) {
	}

	@Override public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
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
			 * Case the asset is not a paper --> Error
			 */
			if (!lBKAsset.getpIsPaper() && !lBKAsset.getpIsOil()) {
				BasicPrintMsg.error("Directional should be only on paper");
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
				String lBKIncomeDirectional = StaticBKIncome.getHEDGING() + " " + lArray[1] + " " + BKI_HEDGING_SUB.Directional.toString();
				String lBKIncomeHedging = StaticBKIncome.getHEDGING() + " " + lArray[1] + " " + BKI_HEDGING_SUB.Trades.toString();
				/*
				 * Build the Line and store it
				 */
				lLineStr += "," + lBKIncomeDirectional;
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

}
