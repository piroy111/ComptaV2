package step0treatrawdata.brokers.oanda.reconciliation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;

public class OAFileReader {

	protected OAFileReader(OAReconcilationManager _sOAReconcilationManager) {
		pOAReconcilationManager = _sOAReconcilationManager;
	}
	
	/*
	 * Data
	 */
	private OAReconcilationManager pOAReconcilationManager;
	private Map<String, Double> pMapSymbolToPosition;
	
	/**
	 * 
	 */
	public final void initiate() {
		pMapSymbolToPosition = new HashMap<>();
	}
	
	/**
	 * 
	 */
	public final void run() {
		LitUnFichierEnLignes lReadFile = pOAReconcilationManager.getpOAFileChecker().getpReadFile();
		for (List<String> lLine : lReadFile.getmContenuFichierListe()) {
			/*
			 * Load
			 */
			int lIdx = -1;
			String lSymbol = lLine.get(++lIdx);
			double lPosition = BasicString.getDouble(lLine.get(++lIdx));
			/*
			 * Fill Map
			 */
			pMapSymbolToPosition.put(lSymbol, lPosition);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final OAReconcilationManager getpOAReconcilationManager() {
		return pOAReconcilationManager;
	}
	public final Map<String, Double> getpMapSymbolToPosition() {
		return pMapSymbolToPosition;
	}
	
}
