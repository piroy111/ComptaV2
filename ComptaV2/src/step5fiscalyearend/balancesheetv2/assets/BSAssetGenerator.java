package step5fiscalyearend.balancesheetv2.assets;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.transactions.BKTransaction;
import step5fiscalyearend.balancesheetv2.BSManager;

public class BSAssetGenerator extends BKHolderGenerator {

	public BSAssetGenerator(BSManager _sBSManager) {
		super(_sBSManager.getpBKTransactionManager());
		pBSManager = _sBSManager;
	}

	/*
	 * Static
	 */
	protected final static String SEPARATOR = ";;;";
	protected final static String SEPARATOR_LIST = ";-;-;";
	public enum BS_TYPE {Assets, Liabilities, Equities, Mirror}
	public enum BS_OWNER {Bunker, Clients}
	/*
	 * Data
	 */
	private BSManager pBSManager;

	/**
	 * We keep all the transactions as we want the balance sheet on everything
	 */
	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	/**
	 * We group the transactions per BKAsset and per FileNameOrigin
	 */
	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		List<String> lListBSTypeAndTitle = new ArrayList<>();
		/*
		 * Load data from BKTransaction
		 */
		BKAccount lBKAccount = _sBKTransaction.getpBKAccount();
		BKAsset lBKAsset = _sBKTransaction.getpBKAsset();
		String lFileNameOrigin = _sBKTransaction.getpFileNameOrigin();
		String lBKIncome = _sBKTransaction.getpBKIncome();
		/*
		 * Determine owner or clients
		 */
		BS_OWNER lBSOwner;
		if (lBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
			lBSOwner = BS_OWNER.Bunker;
		} else {
			lBSOwner = BS_OWNER.Clients;
		}
		/*
		 * ASSETS
		 * The key are the physical accounts. An asset is held when it is in a physical account.
		 * A physical account is materialized by a NameFileOrigine. For instance UOB, InteractiveBrokers, etc.
		 * 
		 * It is always an asset for Bunker, even if the client holds it: UOB, Brokers, CRYPTOS, CONDOR
		 * It is outside of Bunker balance sheet if the client holds it: Bars
		 */
		/*
		 * Case of a FY adjustment -> we extract the FileNameOrigin
		 */
		if (lFileNameOrigin.startsWith(StaticNames.getTREATED_FY_ADJUSTMENTS_PREFIX())) {
			lFileNameOrigin = lFileNameOrigin.substring(StaticNames.getTREATED_FY_ADJUSTMENTS_PREFIX().length()
					+ "YYYY_to_YYYY_".length());
		}
		/*
		 * UOB
		 */
		if (lFileNameOrigin.equals(StaticNames.getUOB_ALL_TRANSACTIONS())) {
			String lTitle = "UOB account in " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * RHB
		 */
		else if (lFileNameOrigin.equals(StaticNames.getRHB_ALL_TRANSACTIONS())) {
			String lTitle = "RHB account in " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * IFB
		 */
		else if (lFileNameOrigin.equals(StaticNames.getIFB_ALL_TRANSACTIONS())) {
			String lTitle = "IFB account in " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * Brokers
		 */
		else if (getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values()) != null) {
			String lBroker = getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values());
			String lTitle = "Hedging broker account " + lBroker;
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * CRYPTOS
		 */
		else if (getValueContained(lFileNameOrigin, StaticBKIncome.BKI_CRYPTO_BROKERS.values()) != null
				&& getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values()) == null) {
			String lBrokerCrypto = getValueContained(lFileNameOrigin, StaticBKIncome.BKI_CRYPTO_BROKERS.values());
			String lTitle = "Cryptos " + lBrokerCrypto;
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * Physical bars --> it is an asset for Bunker only if it holds it. The physical bars of the client are outside of the balance sheet
		 */
		else if (lBKAsset.getpIsBar()) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = "Bars held by Bunker in " + lBKAsset.getpMetalName();
				lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
			} 
		}
		/*
		 * Otherwise it should be a mirror transaction
		 * It is very important to understand that if a transaction is not identified in a physical file above,
		 * then this transaction cannot create an asset for Bunker. It must be part of a mirror transaction
		 * If the value$ of the mirror transactions is not 0, then it means we have a new physical account,
		 * and we should create it above
		 */
		else {
			String lTitle = lFileNameOrigin + " -> " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Mirror + SEPARATOR + lTitle);
		}
		/*
		 * LIABILITIES
		 * Each time a client holds something (except a physical bar), it is a liability for Bunker<br>
		 * We split the liabilities for clarity purposes between the BKAssets: currency, paper metal, loan bars
		 */
		if (lBSOwner == BS_OWNER.Clients && !lBKAsset.getpIsBar()) {
			if (lBKAsset.getpIsCurrency()) {
				String lTitle = "Clients holdings in " + lBKAsset.getpName();
				lListBSTypeAndTitle.add(BS_TYPE.Liabilities + SEPARATOR + lTitle);
			} else if (lBKAsset.getpIsBarLoan()) {
				String lTitle = StaticBKIncome.getLOAN() + lBKAsset.getpMetalName();
				lListBSTypeAndTitle.add(BS_TYPE.Liabilities + SEPARATOR + lTitle);
			} else {
				String lTitle = "Clients holdings of " + lBKAsset.getpName();
				lListBSTypeAndTitle.add(BS_TYPE.Liabilities + SEPARATOR + lTitle);
			}
		}
		/*
		 * EQUITIES
		 * So far, only the capital.
		 */
		if (lBKIncome.equals(StaticBKIncome.getCAPITAL())) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = StaticBKIncome.getCAPITAL() + " in USD";
				lListBSTypeAndTitle.add(BS_TYPE.Equities + SEPARATOR + lTitle);
			}
		}
		/*
		 * Check if there is a BKTransaction which should be in Bunker balance sheet and cannot be allocated
		 */
		if (lListBSTypeAndTitle.size() == 0 && lBSOwner == BS_OWNER.Bunker) {
			BasicPrintMsg.error("There is a BKTransaction which I cannot allocate in the balance sheet"
					+ "\nIf there is a new type of account (banks, cryptos, broker, etc.)"
					+ ", you must create the case in the method 'BSAssetGenerator.getpUniqueKey(..)'"
					+ "\n"
					+ "\nBKTransaction= " + _sBKTransaction
					+ "\n"
					+ "\nBKAccount= " + lBKAccount
					+ "\nBKAsset= " + lBKAsset
					+ "\nFileNameOrigin= " + lFileNameOrigin
					+ "\nBKIncome= " + lBKIncome
					+ "\nBSOwner= " + lBSOwner
					+ "\n");
		}
		/*
		 * Return the key to group the BKTransactions
		 */
		if (lListBSTypeAndTitle.size() == 0) {
			return "outside of the balance sheet";
		} else {
			String lKeyStr = "";
			for (String lBSTypeAndTitle : lListBSTypeAndTitle) {
				if (!lKeyStr.equals("")) {
					lKeyStr += SEPARATOR_LIST;
				}
				lKeyStr += lBSTypeAndTitle;
			}
			return lKeyStr;
		}
	}

	/**
	 * 
	 * @param _sValuesToCheck
	 * @param _sNameFile
	 * @return
	 */
	private static String getValueContained(String _sNameFileOrigin, Object[] _sValuesToCheck) {
		for (Object lValueToCheck : _sValuesToCheck) {
			if (_sNameFileOrigin.contains(lValueToCheck.toString())) {
				return lValueToCheck.toString();
			}
		}
		return null;
	}

	/*
	 * Getters & Setters
	 */
	public static final String getSEPARATOR() {
		return SEPARATOR;
	}
	public static final String getSEPARATOR_LIST() {
		return SEPARATOR_LIST;
	}
	public final BSManager getpBSManager() {
		return pBSManager;
	}













}
