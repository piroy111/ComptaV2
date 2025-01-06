package step4reconciliation.sources;

import java.util.ArrayList;
import java.util.List;

import staticdata.StaticDir;

public class RNBrokers {

	
	public static void main(String[] _sArgs) {
		new RNBrokers().run();
	}
	
	
	public final void run() {
		/*
		 * Load the assets directories
		 */
		List<String> lListDirTreated = new ArrayList<String>();
		lListDirTreated.add(StaticDir.getTREATED_BROKERS_IB());
		lListDirTreated.add(StaticDir.getTREATED_BROKERS_HF());
		lListDirTreated.add(StaticDir.getTREATED_BROKERS_IFC());
		lListDirTreated.add(StaticDir.getTREATED_BROKERS_OANDA());
		lListDirTreated.add(StaticDir.getTREATED_BROKERS_OANDA_CRYPTO());
		/*
		 * Write the assets files
		 */
		for (String lDirTreated : lListDirTreated) {
			RNWriteFileAssets.writeFileAsset(lDirTreated, StaticDir.getOUTPUT_BROKERS_ASSETS());
		}
		RNWriteFileAssets.writeFileAsset(lListDirTreated, StaticDir.getOUTPUT_BROKERS_ASSETS());
		/*
		 * CRYPTOS
		 */
		RNWriteFileAssets.writeFileAsset(StaticDir.getTREATED_CRYPTOS(), StaticDir.getOUTPUT_CRYPTOS_ASSETS());
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
}
