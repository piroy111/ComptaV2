package step1loadtransactions.accounts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAssetManager;
import uob.staticdata.UOBStatic;

public class BKAccountManager {

	/*
	 * Data
	 */
	private static Map<String, BKAccount> pMapNameToBKAccount;
	private static BKAccount pBKAccountBunker;
	private static BKAccount pBKAccountPierreRoy;

	/**
	 * @return Classic get or create
	 * @param _sEmailAddress
	 */
	public static final BKAccount getpBKAccount(String _sEmailAddress) {
		initiate();
		BKAccount lBKAccount = pMapNameToBKAccount.get(_sEmailAddress);
		if (lBKAccount == null) {
			BasicPrintMsg.error("The BKAccount is not in the conf file"
					+ "; NameBKAccount= " + _sEmailAddress
					+ "\nConf file= " + UOBStatic.getDIR_CONF_DYNAMIC()
					+ UOBStatic.getNAME_FILE_ACCOUNT());
		}
		return lBKAccount;
	}

	/**
	 * @return Classic get or create
	 * @param _sEmailAddress
	 */
	private static final BKAccount getpOrCreateBKAccount(String _sEmailAddress) {
		BKAccount lBKAccount = pMapNameToBKAccount.get(_sEmailAddress);
		if (lBKAccount == null) {
			lBKAccount = new BKAccount(_sEmailAddress);
			pMapNameToBKAccount.put(_sEmailAddress, lBKAccount);
		}
		return lBKAccount;
	}

	/**
	 * Initiate only one time: create Map and identify special BKAccount like Bunker and PRoy
	 */
	private static void initiate() {
		if (pMapNameToBKAccount == null) {
			pMapNameToBKAccount = new HashMap<String, BKAccount>();
			/*
			 * Read file CONF
			 */
			String lDir = UOBStatic.getDIR_CONF_DYNAMIC();
			String lFileName = UOBStatic.getNAME_FILE_ACCOUNT();
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir,  lFileName, true);
			for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
				try {
					/*
					 * Read Line
					 */
					int lIdx = -1;
					String lOwner = lLineStr.get(++lIdx);
					String lCurrency = lLineStr.get(++lIdx);
					String lEmail = lLineStr.get(++lIdx);
					String lJointEmail = lLineStr.get(++lIdx);
					String lOrigin = lLineStr.get(++lIdx);
					/*
					 * Case of account for the stage
					 */
					if (lCurrency.contains("undefined")) {
						continue;
					}
					/*
					 * Create and fill BKAccount
					 */
					BKAccount lBKAccount = getpOrCreateBKAccount(lEmail);
					lBKAccount.setpNameOwner(lOwner);
					lBKAccount.setpEmailAddressJoint(lJointEmail);
					lBKAccount.setpBKAssetCurrency(BKAssetManager.getpAndCheckBKAsset(lCurrency));
					lBKAccount.setpCommercialOrigin(lOrigin);
				} catch (Exception lException) {
					String lMsg = "Error in line"
							+ "\nLine= '" + lLineStr + "'"
							+ "\nDir= '" + lDir + "'"
							+ "\nFile= '" + lFileName + "'";
					BasicPrintMsg.error(lMsg, lException);
				}
			}
			/*
			 * Specific accounts of Bunker and PIERRE Roy
			 */
			pBKAccountBunker = getpBKAccount(StaticNames.getACCOUNT_BUNKER());
			pBKAccountPierreRoy = getpBKAccount(StaticNames.getACCOUNT_PROY());
		}
	}

	/*
	 * Getters & Setters
	 */
	public static final Map<String, BKAccount> getpMapNameToBKAccount() {
		initiate();
		return pMapNameToBKAccount;
	}
	public static final BKAccount getpBKAccountBunker() {
		initiate();
		return pBKAccountBunker;
	}
	public static final BKAccount getpBKAccountPierreRoy() {
		initiate();
		return pBKAccountPierreRoy;
	}






























}
