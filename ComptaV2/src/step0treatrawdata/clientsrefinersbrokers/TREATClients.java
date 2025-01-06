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
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;

public class TREATClients extends TREATRoot {

	public TREATClients(TREATManager _sTREATManager) {
		super(_sTREATManager, 
				StaticDir.getIMPORT_CLIENTS_PURCHASE(), 
				StaticDir.getTREATED_CLIENTS_PURCHASE());
	}

	public final void run(LitUnFichierEnLignes _sReadFile) {
	}

	@Override public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate for new file
		 */
		String lNameFile = _sReadFile.getmNomFichier();
		List<String> lListLineToWrite = new ArrayList<String>();
		String lBKAccountStr = getBKAccountFromFileName(_sReadFile.getmNomFichier());
		Map<String, Integer> lMapHeaderToIdxColumn = ColumnIdx.getMapIdxColumns(_sReadFile);
		/*
		 * Header
		 */
		String lHeader = _sReadFile.getmHeadersAndComments().get(0).replaceAll(",BKPrice", "");
		lHeader += "," + BKAccount.class.getSimpleName()
				+ "," + "BKIncome"
				+ "," + "BKPrice";
		/*
		 * Fill
		 */
		for (int lIdx = 0; lIdx < _sReadFile.getmContenuFichierLignes().size(); lIdx++) {
			/*
			 * Load line
			 */
			List<String> lLineList = _sReadFile.getmContenuFichierListe().get(lIdx);
			BKTransactionQuick lBKTransactionQuick = null;
			try {
				lBKTransactionQuick = new BKTransactionQuick(lLineList, lMapHeaderToIdxColumn);
			} catch (Exception lException) {
				BasicPrintMsg.error("Error when reading the file\nFile= " + _sReadFile.getmNomCheminPlusFichier()
					+ "\nIdxLine= " + lIdx + "\nLine= " + lLineList);
			}
			BKAsset lBKAsset = lBKTransactionQuick.getpBKAsset();
			double lBKPrice;
			String lLineStr = _sReadFile.getmContenuFichierLignes().get(lIdx);
			if (lLineList.size() >= 5) {
				lBKPrice = BasicString.getDouble(lLineList.get(4));
				lLineStr = lLineStr.substring(0, BasicString.indexOf(lLineStr, ',', 4));
			} else {
				lBKPrice = Double.NaN;
			}
			/*
			 * Write line for client
			 */
			lLineStr += "," + lBKAccountStr;
			lLineStr += ",";
			if (lBKAsset.getpIsCurrency()) {
				if (lBKTransactionQuick.getpComment().toUpperCase().equals(StaticBKIncome.getCAPITAL().toUpperCase())) {
					lLineStr += StaticBKIncome.getCAPITAL();
				} else if (lBKTransactionQuick.getpComment().equals(StaticBKIncome.getCLIENT_CASH_IN())) {
					lLineStr += StaticBKIncome.getCLIENT_CASH_IN();
				} else {
					lLineStr += StaticBKIncome.getCLIENT_CASH_SPENDING();
				}
			} else if (lBKAsset.getpIsBar()) {
				lLineStr += StaticBKIncome.getCLIENT_BARS();
			} else if (lBKAsset.getpIsPaper()){
				lLineStr += BKI_HEDGING_SUB.Directional.toString();
			} else {
				lLineStr += ",Others";
				BasicPrintMsg.error("BKIncome impossible to determine\n lLineList= " + lLineList + "\n_sReadFile= " + _sReadFile.getmNomCheminPlusFichier());
			}
			lLineStr += "," + lBKPrice;
			lListLineToWrite.add(lLineStr);
			/*
			 * Create mirror transaction
			 */
			if (lBKAsset.getpIsCurrency() || lBKAsset.getpIsPaper()
					|| (lBKAsset.getpIsBar() && lBKTransactionQuick.getpQuantity() < 0)
					|| (lBKAsset.getpIsBar() && lBKAccountStr.equals(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress()))) {
				/*
				 * Load transaction
				 */
				String[] lArray = lLineStr.split(",", -1);
				lArray[3] = "" + (-BasicString.getDouble(lArray[3]));
				lArray[4] = BKAccountManager.getpBKAccountBunker().getpEmailAddress();
				/*
				 * BKIncome
				 */
				String lBKIncome = lArray[5];
				if (lBKAsset.getpIsBar()) {
					lBKIncome = StaticBKIncome.getBARS_BUNKER();
				} else if (lBKIncome.equals(BKI_HEDGING_SUB.Directional.toString())) {
					lBKIncome = StaticBKIncome.getHEDGING() + " " + lArray[1] + " " + BKI_HEDGING_SUB.Trades.toString();
				}
				lArray[5] = lBKIncome;
				/*
				 * Build line mirror and store it
				 */
				String lLineStrMirror = lArray[0];				
				for (int lKdx = 1; lKdx < lArray.length; lKdx++) {
					lLineStrMirror += "," + lArray[lKdx];
				}
				lListLineToWrite.add(lLineStrMirror);
			}
		}
		/*
		 * Write file treated
		 */
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
	}



	/**
	 * Extract the BKAccount from a file<br>
	 * Withdraw the '.csv' at the end<br>
	 * if there is '_' in the last 3 letters, then withdraw the '_' and the letters which follow
	 * @param _sFileName
	 * @return
	 */
	private String getBKAccountFromFileName(String _sFileName) {
		String lBKAccount = "";
		if (_sFileName.endsWith(".csv")) {
			lBKAccount = _sFileName.substring(0, _sFileName.length() - ".csv".length());
		}
		int lIdx = lBKAccount.lastIndexOf('_');
		if (lIdx > 0) {
			boolean lIsNumber = true;
			if (lIdx < lBKAccount.length() - 3) {
				char[] lNumberStr = lBKAccount.substring(lIdx + 1, lBKAccount.length()).toCharArray();
				for (char lChar : lNumberStr) {
					if (lChar < '0' || lChar > '9') {
						lIsNumber = false;
					}
				}
			}
			if (lIsNumber) {
				lBKAccount = lBKAccount.substring(0, lIdx);
			}
		}
		return lBKAccount;
	}








}











