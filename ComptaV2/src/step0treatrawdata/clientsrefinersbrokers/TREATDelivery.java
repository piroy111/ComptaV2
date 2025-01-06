package step0treatrawdata.clientsrefinersbrokers;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticBKIncome;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class TREATDelivery extends TREATRoot {

	public TREATDelivery(TREATManager _sTREATManager) {
		super(_sTREATManager, 
				StaticDir.getIMPORT_DELIVERY(), 
				StaticDir.getTREATED_DELIVERY());
	}

	@Override
	public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Initiate
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		String lNameFile = _sReadFile.getmNomFichier();
		if (!lNameFile.startsWith(StaticNames.getPREFIX_DELIVERIES())) {
			BasicPrintMsg.error("Files of deliveries must begin with '" + StaticNames.getPREFIX_DELIVERIES() + "'");
		}
		String lBKAccount = lNameFile.substring(StaticNames.getPREFIX_DELIVERIES().length());
		int lIdxString = lBKAccount.length() - 1;
		while (lBKAccount.charAt(lIdxString) != '_') {
			lIdxString--;
		}
		lBKAccount = lBKAccount.substring(0, lIdxString);
		/*
		 * Read lines
		 */
		for (List<String> lListLine : _sReadFile.getmContenuFichierListe()) {
			/*
			 * Load Line
			 */
			int lIdx = -1;
			String lDate = lListLine.get(++lIdx);
			String lComment = lListLine.get(++lIdx);
			String lBKAsset = lListLine.get(++lIdx);
			String lAmount = lListLine.get(++lIdx);;
			/*
			 * Write line for file treated
			 */
			String lLineTreated = lDate
					+ "," + lComment
					+ "," + lBKAsset
					+ "," + lAmount
					+ "," + lBKAccount
					+ "," + StaticBKIncome.getCLIENT_BARS()
					+ "," + "NaN";
			lListLineToWrite.add(lLineTreated);
		}
		/*
		 * Write file
		 */
		String lHeader = "Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
		
	}

}
