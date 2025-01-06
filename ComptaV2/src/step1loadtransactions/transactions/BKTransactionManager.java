package step1loadtransactions.transactions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticColumns.ColumnName;
import staticdata.StaticDate;
import staticdata.StaticDebug;
import staticdata.StaticDir;
import step0treatrawdata.brokers.interactivebrokers.IBManager;
import step0treatrawdata.brokers.oanda.OAManager;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step0treatrawdata.postchecker.ColumnIdx;
import step1loadtransactions.accounts.BKAccount;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.brokers.interactivebrokers.BKIBManager;

public class BKTransactionManager {


	public BKTransactionManager() {
		pListBKTransaction = new ArrayList<BKTransaction>();
	}

	/*
	 * Data
	 */
	private List<BKTransaction> pListBKTransaction;

	/**
	 * fill the BKTransaction into the BKSubAccount from the files csv
	 */
	public void loadBKTransactionsIncludingLoans() {
		readFile(StaticDir.getTREATED_LOANS());
		/*
		 * Do the rest
		 */
		loadBKTransactionsExcludingLoans();
	}

	/**
	 * fill the BKTransaction into the BKSubAccount from the files csv
	 */
	public void loadBKTransactionsExcludingLoans() {
		BasicPrintMsg.displayTitle(null, BKTransactionManager.class.getSimpleName() 
				+ " load files into BKTransactions");
		/*
		 * UOB transactions + RHB + IFB (CondorBank)
		 */
		readFile(StaticDir.getUOB_ALL_TRANSACTIONS());
		readFile(StaticDir.getRHB_ALL_TRANSACTIONS());
		readFile(StaticDir.getIFB_ALL_TRANSACTIONS());
		readFile(StaticDir.getARGENTOR_ALL_TRANSACTIONS());
		/*
		 * Load deprecated brokers
		 */
		loadDeprecatedBrokers();
		/*
		 * Load accounts which can have metals
		 */
		loadBKTransactionsOnlyForMetals(true);
		/*
		 * Load CRYPTOS
		 */
		readFile(StaticDir.getTREATED_CRYPTOS());
		/*
		 * Load FY Adjustments (to pass forward payments to current FY)
		 */
		readFile(StaticDir.getTREATED_FY_ADJUSTMENTS());
	}

	/**
	 * 
	 */
	private void loadDeprecatedBrokers() {
		readFile(StaticDir.getTREATED_BROKERS_HF());
		readFile(StaticDir.getTREATED_BROKERS_IFC());
	}

	/**
	 * 
	 */
	public final void loadBKTransactionsOnlyForMetals(boolean _sIsIncludeDelivery) {
		/*
		 * Load REFINERS
		 */
		readFile(StaticDir.getTREATED_REFINERS_PURCHASE());
		/*
		 * Load directional
		 */
		readFile(StaticDir.getTREATED_DIRECTIONAL());
		/*
		 * Interactive brokers
		 */
		IBManager lIBManager = new IBManager();
		lIBManager.run();
		new BKIBManager(this, lIBManager).run();
		/*
		 * OANDA
		 */
		new OAManager().run();
		readFile(StaticDir.getTREATED_BROKERS_OANDA());
		readFile(StaticDir.getTREATED_BROKERS_OANDA_CRYPTO());
		/*
		 * Load clients
		 */
		loadBKTransactionsOnlyForClients(_sIsIncludeDelivery);
	}
	
	/**
	 * 
	 */
	public final void loadBKTransactionsOnlyForClients(boolean _sIsIncludeDelivery) {
		/*
		 * Load client accounts: currencies + bars
		 */
		readFile(StaticDir.getTREATED_CLIENTS_PURCHASE());
		/*
		 * Load deliveries
		 */
		if (_sIsIncludeDelivery) {
			readFile(StaticDir.getTREATED_DELIVERY());
		}
		/*
		 * Load FOREX
		 */
		readFile(StaticDir.getTREATED_FOREX());
	}
	
	/**
	 * 
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sQuantity
	 * @param _sBKAccount
	 * @param _sBKIncome
	 * @param _sBKPrice
	 * @param _sFileNameOrigin
	 * @return
	 */
	public final BKTransaction createBKTransaction(int _sDate, 
			String _sComment, BKAsset _sBKAsset, double _sQuantity, 
			BKAccount _sBKAccount, String _sBKIncome, double _sBKPrice, String _sFileNameOrigin) {
		return createBKTransaction(_sDate, _sComment, _sBKAsset, _sQuantity, _sBKAccount, _sBKIncome, _sBKPrice, _sFileNameOrigin, true);
	}

	/**
	 * Create a BKTransaction and add it to the List
	 * @param _sDate
	 * @param _sComment
	 * @param _sBKAsset
	 * @param _sQuantity
	 */
	public final BKTransaction createBKTransaction(int _sDate, 
			String _sComment, BKAsset _sBKAsset, double _sQuantity, 
			BKAccount _sBKAccount, String _sBKIncome, double _sBKPrice, String _sFileNameOrigin, boolean _sIsIgnoreDateLimits) {
		if (_sIsIgnoreDateLimits && StaticDate.getDATE_MAX() > 0 && _sDate > StaticDate.getDATE_MAX()) {
			return null;
		}
		/*
		 * Create BKTransaction
		 */
		BKTransaction lBKTransaction = new BKTransaction(_sDate, 
				_sBKAsset, _sComment, _sQuantity, _sBKAccount, _sBKIncome, _sBKPrice, _sFileNameOrigin);
		if (!_sIsIgnoreDateLimits && StaticDate.getDATE_MAX() > 0 && _sDate > StaticDate.getDATE_MAX()) {
			return lBKTransaction;
		}
		/*
		 * Debug, display
		 */
		StaticDebug.writeFileDebug(lBKTransaction);
		/*
		 * Add to list
		 */
		if (pListBKTransaction == null) {
			pListBKTransaction = new ArrayList<BKTransaction>();
		}
		pListBKTransaction.add(lBKTransaction);
		return lBKTransaction;
	}

	/**
	 * Create all BKTransaction for each line of the file
	 * @param _sReadFile
	 */
	public final List<BKTransaction> readFile(LitUnFichierEnLignes _sReadFile, boolean _sIsIgnoreDateLimits) {
		List<BKTransaction> lListBKTransaction = new ArrayList<>();
		Map<String, Integer> lMapColumnIdx = ColumnIdx.getAndCheckMapIdxColumns(_sReadFile);
		for (List<String> lLineStr : _sReadFile.getmContenuFichierListe()) {
			/*
			 * Load Line
			 */
			int lDate = BasicString.getInt(lLineStr.get(lMapColumnIdx.get(ColumnName.Date.toString())));
			String lComment = lLineStr.get(lMapColumnIdx.get(ColumnName.Comment.toString()));
			String lBKAssetStr = lLineStr.get(lMapColumnIdx.get(ColumnName.BKAsset.toString()));
			double lQuantity = BasicString.getDouble(lLineStr.get(lMapColumnIdx.get(ColumnName.Amount.toString())));
			String lBKAccountStr = lLineStr.get(lMapColumnIdx.get(ColumnName.BKAccount.toString()));
			String lBKIncome = lLineStr.get(lMapColumnIdx.get(ColumnName.BKIncome.toString()));
			double lBKPrice = BasicString.getDouble(lLineStr.get(lMapColumnIdx.get(ColumnName.BKPrice.toString())));
			/*
			 * File name origin
			 */
			String lOrigin;
			try {
				String[] lArray = _sReadFile.getmNomCheminPlusFichier().split("//", -1);
				String lRoot = lArray[lArray.length - 2];
				lRoot = lRoot.substring(3, lRoot.length() - 1);
				lOrigin = lRoot + "/" + lArray[lArray.length - 1];
			} catch(Exception lException) {
				lOrigin = _sReadFile.getmNomFichier();
			}
			/*
			 * Create BKTransaction
			 */
			BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lBKAssetStr);
			BKAccount lBKAccount = BKAccountManager.getpBKAccount(lBKAccountStr);
			BKTransaction lBKTransaction = createBKTransaction(lDate, 
					lComment, lBKAsset, lQuantity, lBKAccount, lBKIncome, lBKPrice, lOrigin, _sIsIgnoreDateLimits);
			if (lBKTransaction != null) {
				lListBKTransaction.add(lBKTransaction);
			}
		}
		return lListBKTransaction;
	}

	/**
	 * 
	 * @param _sDir
	 * @param _sIsIgnoreDateLimits
	 * @return
	 */
	public final List<BKTransaction> readFile(String _sDir) {
		return readFile(_sDir, true);
	}

	/**
	 * Read all the file of on DIR and create the BKTransactions
	 * @param _sDir
	 */
	public final List<BKTransaction> readFile(String _sDir, boolean _sIsIgnoreDateLimits) {
		System.out.println("Loading BKtransactions in the dir '" + _sDir + "'");
		List<Path> lListPath = BasicFichiersNioRaw.getListPath(Paths.get(_sDir));
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>();
		for (Path lPath : lListPath) {
			lListBKTransaction.addAll(readFile(new LitUnFichierEnLignes(lPath, true), _sIsIgnoreDateLimits));
		}
		return lListBKTransaction;
	}

	
	/**
	 * 
	 * @param _sDir
	 * @param _sIsIgnoreDateLimits
	 * @return
	 */
	public final List<BKTransaction> readLastFile(String _sDir, String _sSuffix) {
		BasicDir lBasicDir = new BasicDir(_sDir, _sSuffix);
		BasicFile lBasicFile = lBasicDir.getmBasicFile(BasicDateInt.getmToday());
		List<BKTransaction> lListBKTransaction = new ArrayList<BKTransaction>();
		if (lBasicFile != null && lBasicFile.getmLitUnFichierEnLignes().getmIsFichierLuCorrectement()) {
			lListBKTransaction.addAll(readFile(lBasicFile.getmLitUnFichierEnLignes(), true));
		}
		return lListBKTransaction;
	}

	
	
	/*
	 * Getters & Setters
	 */
	public final List<BKTransaction> getpListBKTransaction() {
		return pListBKTransaction;
	}






}
