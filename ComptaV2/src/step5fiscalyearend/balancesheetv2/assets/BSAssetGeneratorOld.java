package step5fiscalyearend.balancesheetv2.assets;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome;
import staticdata.StaticNames;
import step0treatrawdata.objects.BKAsset;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.transactions.BKTransaction;
import step5fiscalyearend.balancesheetv2.BSManager;

public class BSAssetGeneratorOld extends BKHolderGenerator {

	public BSAssetGeneratorOld(BSManager _sBSManager) {
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

		///////////////////////////////////////////////////
		if (AMNumberTools.isEqual(_sBKTransaction.getpQuantity(), -117)
				&& AMNumberTools.isEqual(_sBKTransaction.getpBKPrice(), 1.20141)) {
			AMNumberTools.isNaNOrZero(0);
		}

		///////////////////////////////////////////////////


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
		 * Determine the type of the asset and its designation
		 */
		/*
		 * Case of the Capital -> it is an equity
		 * We ignore if the capital is to another account than Bunker (for instance pierre.roy@hotmail.com) because it should be in the Equities of the shareholders
		 */
		if (lBKIncome.equals(StaticBKIncome.getCAPITAL())) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = StaticBKIncome.getCAPITAL() + " in USD";
				lListBSTypeAndTitle.add(BS_TYPE.Equities + SEPARATOR + lTitle);
			}
		}
		/*
		 * Case of the cash contained in UOB --> it is an asset if Bunker holds it 
		 * and both an asset and a liability if the client holds it
		 */
		else if (lBKAsset.getpIsCurrency() && lFileNameOrigin.equals(StaticNames.getUOB_ALL_TRANSACTIONS())) {
			String lTitle = "UOB account in " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
			if (lBSOwner == BS_OWNER.Clients) {
				String lTitleClient = "Clients holdings in " + lBKAsset.getpName();
				lListBSTypeAndTitle.add(BS_TYPE.Liabilities + SEPARATOR + lTitleClient);
			}
		}
		/*
		 * Case of the physical bars --> it is an asset if Bunker holds it 
		 * and outside of the balance sheet if the client holds it (nothing to report) 
		 */
		else if (lBKAsset.getpIsBar()) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = "Bars held by Bunker in " + lBKAsset.getpMetalName();
				lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
			} 
		}
		/*
		 * Case of the loans Bunker --> it is a liability if Bunker holds it
		 * and it should not be reported if the client holds it
		 */
		else if (lBKAsset.getpIsBarLoan()) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = StaticBKIncome.getLOAN() + lBKAsset.getpMetalName();
				lListBSTypeAndTitle.add(BS_TYPE.Liabilities + SEPARATOR + lTitle);
			}
		}
		/*
		 * Case of a broker account used for hedging --> it is an asset for Bunker
		 */
		else if ((lBKIncome.startsWith(StaticBKIncome.getHEDGING())
				&& getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values()) != null)
				|| (lBKIncome.equals(StaticBKIncome.getCRYPTOS() + " " + StaticBKIncome.BKI_HEDGING_BROKERS.OANDA))) {
			String lBroker = getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values());
			String lTitle = "Hedging broker account " + lBroker;
			lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
		}
		/*
		 * Case of the CRYPTO currencies account --> it is an asset for Bunker
		 */
		else if (lBKIncome.startsWith(StaticBKIncome.getCRYPTOS())
				&& (getValueContained(lFileNameOrigin, StaticBKIncome.BKI_CRYPTO_BROKERS.values()) != null)
				&& (getValueContained(lFileNameOrigin, StaticBKIncome.BKI_HEDGING_BROKERS.values()) == null)) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lBrokerCrypto = getValueContained(lFileNameOrigin, StaticBKIncome.BKI_CRYPTO_BROKERS.values());
				String lTitle = "Cryptos " + lBrokerCrypto;
				lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
			}
		}
		/*
		 * Case of the account of CONDOR
		 */
		else if (lBKIncome.equals(StaticBKIncome.getCONDOR_IN())) {
			if (lBSOwner == BS_OWNER.Bunker) {
				String lTitle = "Condor account";
				lListBSTypeAndTitle.add(BS_TYPE.Assets + SEPARATOR + lTitle);
			} else {
				BasicPrintMsg.error("Case not handled. Need some more code here");
			}
		}
		/*
		 * Otherwise it should be a mirror transaction
		 */
		else {
			String lTitle = lFileNameOrigin + " -> " + lBKAsset.getpName();
			lListBSTypeAndTitle.add(BS_TYPE.Mirror + SEPARATOR + lTitle);
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
