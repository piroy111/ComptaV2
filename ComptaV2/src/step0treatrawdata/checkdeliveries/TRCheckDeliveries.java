package step0treatrawdata.checkdeliveries;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;

public class TRCheckDeliveries {

	public static void main(String[] _sArgs) {
		new TRCheckDeliveries().run();
	}
		
	
	/**
	 * Check deliveries are not happening at the same time as an order. <br>
	 * The program does not work well because of the loans if there is a delivery at the same time as a transaction on a bar<br>
	 * We must put a different date for the delivery if this happens<br>
	 */
	public TRCheckDeliveries() {
		pMapBKAccountListDateTransactions = new HashMap<>();
	}
	
	/*
	 * Data
	 */
	private Map<String, List<Integer>> pMapBKAccountListDateTransactions;
	
	/**
	 * Check deliveries are not happening at the same time as an order. <br>
	 * The program does not work well because of the loans if there is a delivery at the same time as a transaction on a bar<br>
	 * We must put a different date for the delivery if this happens<br>
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Check deliveries are not the same date as a transaction");
		BasicPrintMsg.display(this,"Load files clients");
		loadMapDateTransactions();
		BasicPrintMsg.display(this,"Check deliveries");
		checkVersusDeliveries();
		BasicPrintMsg.display(this,"All good");
	}
	
	/**
	 * 
	 */
	private void loadMapDateTransactions() {
		String lDirTransactionsClients = StaticDir.getTREATED_CLIENTS_PURCHASE();
		List<Path> lListPathFiles = BasicFichiersNio.getListFilesAndSubFiles(Paths.get(lDirTransactionsClients));
		for (Path lPath : lListPathFiles) {
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lPath, true);
			for (List<String> lLine : lReadFile.getmContenuFichierListe()) {
				if (lLine.size() > 4) {
					/*
					 * Load line
					 */
					int lDate = BasicString.getInt(lLine.get(0));
					String lBKAccount = lLine.get(4);
					/*
					 * Store date in map
					 */
					List<Integer> lListDate = pMapBKAccountListDateTransactions.get(lBKAccount);
					if (lListDate == null) {
						lListDate = new ArrayList<>();
						pMapBKAccountListDateTransactions.put(lBKAccount, lListDate);
					}
					if (!lListDate.contains(lDate)) {
						lListDate.add(lDate);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void checkVersusDeliveries() {
		String lDirDeliveries = StaticDir.getTREATED_DELIVERY();
		List<Path> lListPathFiles = BasicFichiersNio.getListFilesAndSubFiles(Paths.get(lDirDeliveries));
		for (Path lPath : lListPathFiles) {
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lPath, true);
			for (List<String> lLine : lReadFile.getmContenuFichierListe()) {
				if (lLine.size() > 4) {
					/*
					 * Load line
					 */
					int lDate = BasicString.getInt(lLine.get(0));
					String lBKAccount = lLine.get(4);
					/*
					 * Check
					 */
					List<Integer> lListDate = pMapBKAccountListDateTransactions.get(lBKAccount);
					if (lListDate != null && lListDate.contains(lDate)) {
						BasicPrintMsg.error("The file delivery is at a date on which tehre is a transaction for the same BKAccount"
								+ ". This is not supported by the program. You must put a later date of delivery"
								+ "\nBKAccount= '" + lBKAccount + "'"
								+ "\nFile delivery= '" + lReadFile.getmNomCheminPlusFichier() + "'"
								+ "\nDate in error= " + lDate);
					}
				}
			}
		}
	}
	
}
