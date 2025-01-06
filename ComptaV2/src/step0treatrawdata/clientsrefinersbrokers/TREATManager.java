package step0treatrawdata.clientsrefinersbrokers;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.StaticBKIncome.BKI_HEDGING_BROKERS;
import staticdata.StaticDir;
import step0treatrawdata.checkdeliveries.TRCheckDeliveries;
import step0treatrawdata.cryptos.TREATCryptos;
import step0treatrawdata.fyadjustments.TREATFYAdjustments;
import step0treatrawdata.ifb.IFBTreatedManager;
import step0treatrawdata.rhb.RHBTreatedWriter;
import step0treatrawdata.uob.versionnew.UOBTreatedManager;

public class TREATManager {

	public TREATManager(boolean _sIsAutoInstantiate) {
		pListTreatRoot = new ArrayList<TREATRoot>();
		/*
		 * Create TREATRoot
		 */
		if (_sIsAutoInstantiate) {
			instantiate();
		}
	}
	
	/*
	 * Data
	 */
	private List<TREATRoot> pListTreatRoot;
	
	/**
	 * Create TREATRoot
	 */
	private void instantiate() {
		new TREATBrokers(this, StaticDir.getIMPORT_BROKERS_HF(), StaticDir.getTREATED_BROKERS_HF(), BKI_HEDGING_BROKERS.HotForex);
		new TREATBrokers(this, StaticDir.getIMPORT_BROKERS_IFC(), StaticDir.getTREATED_BROKERS_IFC(), BKI_HEDGING_BROKERS.IFCM);
		new TREATClients(this);
		new TREATDelivery(this);
		new TREATRefiners(this);
		new TREATDirectional(this);
		new TREATForex(this);		
	}
	
	
	/**
	 * add to the list
	 * @param _sTreatRoot
	 */
	protected final void declareTreatRoot(TREATRoot _sTreatRoot) {
		pListTreatRoot.add(_sTreatRoot);
	}
	
	/**
	 * Run: treat all the files
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "all csv files");
		/*
		 * FY adjustments
		 */
		new TREATFYAdjustments().run();
		/*
		 * Write the UOBTransactions from the raw files UOB into on single treated file
		 */
		UOBTreatedManager lUOBTreatedManager = new UOBTreatedManager();
		lUOBTreatedManager.loadFromRawFiles();
		lUOBTreatedManager.writeTreated();
		/*
		 * RHB: read the imported files of RHB and write the treated file with all the transactions
		 */
		new RHBTreatedWriter().writeFileTreated();
		/*
		 * IFB: write file treated after reading all the bank statements
		 */
		new IFBTreatedManager().writeFileTreated();
		/*
		 * Treated. All the files are treated the same since it is standard
		 */
		for (TREATRoot lTREATRoot : pListTreatRoot) {
			lTREATRoot.treatDir();
		}
		new TREATCryptos().run();
		/*
		 * Check deliveries are not at the same date as a transaction
		 */
		new TRCheckDeliveries().run();
	}

	/*
	 * Getters & Setters
	 */
	public final List<TREATRoot> getpListTreatRoot() {
		return pListTreatRoot;
	}


	
	
}
