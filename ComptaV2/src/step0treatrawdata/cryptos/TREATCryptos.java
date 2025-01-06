package step0treatrawdata.cryptos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticBKIncome;
import staticdata.StaticBKIncome.BKI_CRYPTO_BROKERS;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;

public class TREATCryptos {

	public static void main(String[] _sArgs) {
		new TREATCryptos().run();
	}
	
	public TREATCryptos() {

	}


	public final void run() {
		/*
		 * Load files import
		 */
		String lDirImport = StaticDir.getIMPORT_CRYPTOS();
		List<String> lListFiles = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirImport);
		for (String lNameFile : lListFiles) {
			String lNameCrypto = lNameFile.replaceAll(".csv", "");
			List<String> lListLineToWrite = new ArrayList<String>();
			/*
			 * Check
			 */
			BKI_CRYPTO_BROKERS lBKIBroker = null;
			for (BKI_CRYPTO_BROKERS lBKIBrokerLoop : BKI_CRYPTO_BROKERS.values()) {
				if (lBKIBrokerLoop.toString().equals(lNameCrypto)) {
					lBKIBroker = lBKIBrokerLoop;
					break;
				}
			}
			if (lBKIBroker == null) {
				BasicPrintMsg.error("The name of the broker does not exist. The name of the broker is taken from the name of the csv file"
						+ "\nname of file= " + lNameFile
						+ "\nname of broker= " + lNameCrypto
						+ "\nList of brokers allowed= " + BKI_CRYPTO_BROKERS.values().toString());
			}
			/*
			 * Load file import
			 */
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDirImport, lNameFile, true);
			/*
			 * Find out the list of the BKAssets
			 */
			List<BKAsset> lListBKAsset = new ArrayList<BKAsset>();
			Map<BKAsset, Double> lMapBKAssetToAmount = new HashMap<BKAsset, Double>();
			for (int lIdxHeader = 1; lIdxHeader < lReadFile.getmHeadersAndCommentList().get(0).size(); lIdxHeader++) {
				String lName = lReadFile.getmHeadersAndCommentList().get(0).get(lIdxHeader);
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lName);
				lListBKAsset.add(lBKAsset);
				lMapBKAssetToAmount.put(lBKAsset, 0.);
			}
			/*
			 * 
			 */
			for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
				/*
				 * Load data
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLineStr.get(++lIdx));
				/*
				 * Load amounts of BKAsset
				 */
				for (int lIdxBKAsset = 0; lIdxBKAsset < lListBKAsset.size(); lIdxBKAsset++) {
					double lNewAmount = BasicString.getDouble(lLineStr.get(lIdx + 1 + lIdxBKAsset));
					BKAsset lBKAsset = lListBKAsset.get(lIdxBKAsset);
					double lOldAmount = lMapBKAssetToAmount.get(lBKAsset);
					/*
					 * Create line
					 */
					double lQtyTransaction = lNewAmount - lOldAmount;
					lMapBKAssetToAmount.put(lBKAsset, lNewAmount);
					/*
					 * 
					 */
					if (!AMNumberTools.isNaNOrZero(lQtyTransaction)) {
						String lLine = lDate
								+ "," + lNameCrypto
								+ "," + lBKAsset.getpName()
								+ "," + lQtyTransaction
								+ "," + BKAccountManager.getpBKAccountBunker().getpEmailAddress()
								+ "," + "NaN"
								+ "," + StaticBKIncome.getCRYPTOS() + " " + lBKIBroker.toString();
						lListLineToWrite.add(lLine);
					}
				}
			}
			/*
			 * Write file
			 */
			Collections.sort(lListLineToWrite);
			String lHeader = "#Date,Comment,BKAsset,Amount,BKAccount,BKPrice,BKIncome";
			String lDirTreated = StaticDir.getTREATED_CRYPTOS();
			BasicFichiers.writeFile(lDirTreated, lNameFile, lHeader, lListLineToWrite);
		}
	}

















}
