package step3statements.statements.statements;

import java.util.TreeMap;

import basicmethods.BasicDateInt;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKHoldingClient extends STStatementAbstract {

	public STWriteFileBKHoldingClient(STBKAccounts _sSTBKAccounts) {
		super(_sSTBKAccounts);
	}

	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKACCOUNTS();
	}

	@Override public void createLines() {
		/*
		 * Loop on the BKHolder
		 */
		for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
			TreeMap<String, String> lMapLineToWrite = new TreeMap<String, String>();
			super.initiate();
			/*
			 * Set the Currency
			 */
			String[] lArrayStr = lBKHolder.getpKey().split("; ", -1);
			if (lArrayStr.length == 2) {
				String lAccount = lArrayStr[0];
				if (lAccount.equals(BKAccountManager.getpBKAccountBunker().getpEmailAddress())
						|| lAccount.equals(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
					continue;
				}
				String lCurrencyRef = lArrayStr[1];
				BKAsset lBKAssetCurrencyRef = BKAssetManager.getpBKCurrency(lCurrencyRef);
				if (lBKAssetCurrencyRef != null) {
					addToListLine("Account," + lAccount);
					addToListLine("");
					/*
					 * Write currency
					 */
					BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(BasicDateInt.getmToday());
					for (BKAsset lBKAsset : lBKInventory.getpMapBKAssetToQuantity().keySet()) {
						if (!lBKAsset.getpIsBar()) {
							String lLineStr = lBKAsset.getpName() 
									+ "," + lBKInventory.getpMapBKAssetToQuantity().get(lBKAsset);
							addToListLine(lLineStr);
						}
					}
					addToListLine("");
					/*
					 * Write bars
					 */
					addToListLine("Metal,Bar code,Weight(Oz)");
					int lIdx = 1000;
					for (BKBar lBKBar : lBKInventory.getpMapBKBarToHolding().keySet()) {
						if (lBKInventory.getpMapBKBarToHolding().get(lBKBar) == 1) {
							String lLineStr = lBKBar.getpBKAsset().getpMetalName()
									+ "," + lBKBar.getpRef()
									+ "," + lBKBar.getpWeightOz();
							lMapLineToWrite.put(lBKBar.getpBKAsset().getpMetalName()
									+ (++lIdx), lLineStr);
						}
					}
					for (String lLine : lMapLineToWrite.values()) {
						addToListLine(lLine);
					}
				}
				/*
				 * Write file
				 */
				setpNameFile(getpNameFile().replaceAll(".csv", "_" + lAccount + ".csv"));
				super.writeFile();
			}
		}
	}
	










}
