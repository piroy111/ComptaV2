package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKPvLClient extends STStatementAbstract {

	public STWriteFileBKPvLClient(STBKAccounts _sSTBKAccounts) {
		super (_sSTBKAccounts);
	}

	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKACCOUNTS();
	}

	@Override public void createLines() {
		/*
		 * Big unique file
		 */
		TreeMap<Double, String> lMapLineToWrite2 = new TreeMap<Double, String>();
		List<String> lListLineToWrite2 = new ArrayList<String>();
		/*
		 * Loop on the BKHolder
		 */
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
			super.initiate();
			/*
			 * Set the Currency
			 */
			String[] lArrayStr = lBKHolder.getpKey().split("; ", -1);
			if (lArrayStr.length ==2) {
				String lAccount = lArrayStr[0];
				if (lAccount.equals(BKAccountManager.getpBKAccountBunker().getpEmailAddress())
						|| lAccount.equals(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
					continue;
				}
				String lCurrencyRef = lArrayStr[1];
				BKAsset lBKAssetCurrencyRef = BKAssetManager.getpBKCurrency(lCurrencyRef);
				if (lBKAssetCurrencyRef != null) {
					/*
					 * Look for the start date and for the BKAsset with a non-zero position
					 */
					int lDateStart = -1;
					List<BKAsset> lListBKBassetCurrency = new ArrayList<BKAsset>();
					List<BKAsset> lListBKBassetBars = new ArrayList<BKAsset>();
					for (BKInventory lBKInventory : lBKHolder.getpMapDateToBKInventory().values()) {
						if (lDateStart == -1) {
							if (lBKInventory.getpListBKTransactionToday().size() > 0) {
								lDateStart = lBKInventory.getpDate();
							}
						}
						for (BKTransaction lBKTransaction : lBKInventory.getpListBKTransactionToday()) {
							BKAsset lBKAsset = lBKTransaction.getpBKAsset();
							if (lBKAsset.getpIsCurrency()) {
								if (!lListBKBassetCurrency.contains(lBKAsset)) {
									lListBKBassetCurrency.add(lBKAsset);
								}
							} else if (lBKAsset.getpIsBar()) {
								if (!lListBKBassetBars.contains(lBKAsset)) {
									lListBKBassetBars.add(lBKAsset);
								}
							}
						}
					}
					/*
					 * Header
					 */
					addToHeader("Date,");
					for (BKAsset lBKAsset : lListBKBassetCurrency) {
						addToHeader(",Cash held in your Bunker account (" + lBKAsset + ")");
					}
					addToHeader("");
					for (BKAsset lBKAsset : lListBKBassetBars) {
						addToHeader("," + lBKAsset + " (Oz)");
					}
					for (BKAsset lBKAsset : lListBKBassetBars) {
						addToHeader("," + lBKAsset + " (" + lCurrencyRef + ")");
					}
					addToHeader("");
					addToHeader(",Cash wired into your Bunker account (" + lCurrencyRef + ")"
							+ ",Value of your folio (" + lCurrencyRef + ")"
							+ ",Performance (" + lCurrencyRef + ")"
							+ ",Performance (%)");
					/*
					 * Look for the list of dates end of month
					 */
					List<Integer> lListDate = new ArrayList<Integer>();
					int lDateLoop = BasicDateInt.getmEndOfMonth(lDateStart);
					while (lDateLoop <= BasicDateInt.getmToday()) {
						lListDate.add(lDateLoop);
						lDateLoop = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusDay(lDateLoop, 1));
					}
					/*
					 * File content
					 */
					for (int lDate : lListDate) {
						String lLineStr = "" + lDate;
						lLineStr += ",";
						BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
						/*
						 * Write cash holdings
						 */
						for (BKAsset lBKAsset : lListBKBassetCurrency) {
							lLineStr += "," + afficheIntegerWithComma(lBKInventory.getpBKAssetQty(lBKAsset), "");
						}
						lLineStr += ",";
						/*
						 * Write bar holdings
						 */
						for (BKAsset lBKAsset : lListBKBassetBars) {
							lLineStr += "," + afficheIntegerWithComma(lBKInventory.getpBKAssetQty(lBKAsset), "oz");
						}
						/*
						 * Write the value in currency ref of the bars
						 */
						for (BKAsset lBKAsset : lListBKBassetBars) {
							lLineStr += "," + afficheIntegerWithComma((lBKInventory.getpBKAssetQty(lBKAsset)
									* lBKAsset.getpPriceUSD(lDate)
									/ lBKAssetCurrencyRef.getpPriceUSD(lDate)), "");
						}
						lLineStr += ",";
						/*
						 * Write initial investment
						 */
						double lInvestment = lBKInventory.getpIncomingFundsUSD()
								/ lBKAssetCurrencyRef.getpPriceUSD(lDate);
						lLineStr += "," + afficheIntegerWithComma(lInvestment, "");
						/*
						 * Current value of folio
						 */
						double lCurrentValue = lBKInventory.getpValueUSD()
								/ lBKAssetCurrencyRef.getpPriceUSD(lDate);
						lLineStr += "," + afficheIntegerWithComma(lCurrentValue, "");
						/*
						 * Write Perf in currency ref
						 */
						double lPerfCurrency = lCurrentValue - lInvestment;
						lLineStr += "," + afficheIntegerWithComma(lPerfCurrency, "");
						/*
						 * Write Perf in %
						 */
						double lPerfPercent = lCurrentValue / lInvestment - 1.;
						lLineStr += "," + BasicPrintMsg.affichePourcentage(lPerfPercent, 1);
						/*
						 * Write line
						 */
						addToListLine(lLineStr);
					}
					/*
					 * Big file content
					 */
					String lLineStr2 = "";
					if (lListDate.size() > 0) {
						int lDate = lListDate.get(lListDate.size() - 1);
						lLineStr2 += lDate;
						BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
						lLineStr2 += "," + lAccount;
						lLineStr2 += "," + lBKInventory.getpValueUSD();
						lLineStr2 += "," + lBKInventory.getpIncomingFundsUSD();
						double lPvLUSD = lBKInventory.getpValueUSD() - lBKInventory.getpIncomingFundsUSD();
						lLineStr2 += "," + lPvLUSD;
						double lPvLPercent = lPvLUSD / lBKInventory.getpIncomingFundsUSD();
						lLineStr2 += "," + lPvLPercent;
						if (!lLineStr2.equals("")) {
							lMapLineToWrite2.put(-lBKInventory.getpValueUSD(), lLineStr2);
						}
					}
					/*
					 * Write the file
					 */
					setpNameFile(getpNameFile().replaceAll(".csv", "_" + lAccount + ".csv"));
					super.writeFile();
				}
			}
		}
		/*
		 * Write file
		 */
		super.initiate();
		addToHeader("Date,Account,value USD,Incoming funds USD,P/L USD,P/L %");
		lListLineToWrite2 = new ArrayList<String>(lMapLineToWrite2.values());
		for (String lLine : lListLineToWrite2) {
			addToListLine(lLine);
		}
		super.writeFile();
	}


	/**
	 * Display for excel
	 * @param _sDouble
	 * @param _sCurrencyRef
	 * @return
	 */
	private String afficheIntegerWithComma(double _sDouble, String _sCurrencyRef) {
		if (AMNumberTools.isZero(_sDouble)) {
			return "";
		} else if (_sCurrencyRef.equals("")) {
			return "\"" + BasicPrintMsg.afficheIntegerWithComma(_sDouble) + "\"";
		} else {
			return "\"" + BasicPrintMsg.afficheIntegerWithComma(_sDouble) + " " + _sCurrencyRef
					+ "\"";
		}
	}


	
	
	
	
	
	
	
	
	
	
	

}
