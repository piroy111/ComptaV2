package step3statements.statements.statements;

import java.util.HashMap;
import java.util.Map;

import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;
import step1loadtransactions.accounts.BKAccountManager;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileAmountMetalsStored extends STStatementAbstract {

	public STWriteFileAmountMetalsStored(STBKAccounts _sSTBKAccounts) {
		super(_sSTBKAccounts);
	}

	/*
	 * Data
	 */
	private enum owner {Bunker, Clients, Total}
	private enum unit {Oz, USD}

	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_STORAGE_COST_MALCA_AMIT();
	}
	
	/*
	 * Write file
	 */
	@Override public void createLines() {
		/*
		 * Initiate
		 */
		owner[] lOwnerBunker = new owner[]{owner.Bunker, owner.Total};
		owner[] lOwnerClients = new owner[]{owner.Clients, owner.Total};
		/*
		 * Header
		 */
		String lHeader0 = "#Date";
		String lHeader1 = "#";
		for (unit lUnit : unit.values()) {
			for (owner lOwner : owner.values()) {
				for (BKAsset lBKAsset : BKAssetManager.getpListBKBar()) {
					lHeader0 += "," + lOwner;
					lHeader1 += "," + lBKAsset.getpMetalName() + " " + lUnit;
				}
			}
		}
		addToListLine(lHeader0);
		addToListLine(lHeader1);
		/*
		 * Build file content
		 */
		for (int lDate : pSTStatementGeneratorAbstract.getpListDate()) {
			/*
			 * Initiate
			 */
			Map<owner, Map<BKAsset, Double>> lMapOwnerToMapBarToAmountOz = new HashMap<>();
			for (owner lOwner : owner.values()) {
				Map<BKAsset, Double> lMapBarToAmountOz = new HashMap<>();
				for (BKAsset lBKAsset : BKAssetManager.getpListBKBar()) {
					lMapBarToAmountOz.put(lBKAsset, 0.);
				}
				lMapOwnerToMapBarToAmountOz.put(lOwner, lMapBarToAmountOz);
			}
			/*
			 * Loop over the holders and the bars to add the amounts
			 */
			for (BKHolder lBKHolder : pSTStatementGeneratorAbstract.getpListBKHolder()) {
				BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDate);
				if (lBKInventory != null) {
					for (BKBar lBKBar : lBKInventory.getpMapBKBarToHolding().keySet()) {
						BKAsset lBKAsset = lBKBar.getpBKAsset();
						int lHolding = lBKInventory.getpMapBKBarToHolding().get(lBKBar);
						if (lHolding == 1) {
							/*
							 * Identify the owner. Determine whether it is Bunker, or Clients
							 */
							String lOwnerStr = lBKHolder.getpKey();
							owner[] lArrayOwner;
							if (lOwnerStr.contains(BKAccountManager.getpBKAccountBunker().getpEmailAddress())
									|| lOwnerStr.contains(BKAccountManager.getpBKAccountPierreRoy().getpEmailAddress())) {
								lArrayOwner = lOwnerBunker;
							} else {
								lArrayOwner = lOwnerClients;
							}
							/*
							 * Put back the amount in the maps
							 */
							for (owner lOwner : lArrayOwner) {
								Double lAmountOz = lMapOwnerToMapBarToAmountOz.get(lOwner).get(lBKAsset);
								lAmountOz += lBKBar.getpWeightOz();
								lMapOwnerToMapBarToAmountOz.get(lOwner).put(lBKAsset, lAmountOz);
							}
						}
					}
				}
			}
			/*
			 * Build the lines to write in the file
			 */
			String lLine = "" + lDate;
			for (unit lUnit : unit.values()) {
				for (owner lOwner : owner.values()) {
					for (BKAsset lBKAsset : BKAssetManager.getpListBKBar()) {
						Double lAmount = lMapOwnerToMapBarToAmountOz.get(lOwner).get(lBKAsset);
						if (lUnit == unit.USD) {
							lAmount = lAmount * lBKAsset.getpMapDateToPrice().get(lDate);
						}
						lLine += "," + lAmount;
					}
				}
			}
			addToListLine(lLine);
		}
	}


}
