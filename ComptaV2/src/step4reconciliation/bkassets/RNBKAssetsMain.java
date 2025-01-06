package step4reconciliation.bkassets;

import java.util.Collections;

import step0treatrawdata.clientsrefinersbrokers.TREATManager;
import step1loadtransactions.holder.BKHolderGenerator;
import step1loadtransactions.transactions.BKTransaction;
import step2loans.createloanstransactions.LNManager;
import step3statements.statements.main.STSortBKHolder;
import uob.UOBMainManager;
import uob.staticdata.UOBStatic.UOB_DISPLAY;

public class RNBKAssetsMain extends BKHolderGenerator {

	public static void main(String[] _sArgs) {
		/*
		 * Initiate
		 */
		new UOBMainManager(UOB_DISPLAY.Off).run();
		new TREATManager(true).run();
		LNManager lLNManager = new LNManager();
		lLNManager.run(true);
		/*
		 * All transaction
		 */
		new RNBKAssetsMain(lLNManager).writeFile();
	}

	public RNBKAssetsMain(LNManager _sLNManager) {
		super(_sLNManager.getpBKTransactionManager());
		pLNManager = _sLNManager;
	}

	/*
	 * Data
	 */
	protected LNManager pLNManager;

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

	@Override public String getpUniqueKey(BKTransaction _sBKTransaction) {
		return _sBKTransaction.getpBKAccount().getpEmailAddress();
	}
	
	/**
	 * Write the file
	 * @param _sDir
	 * @param _sFileName
	 */
	public final void writeFile() {
		/*
		 * Generate
		 */
		generateBKHolder();
		Collections.sort(pListBKHolder, new STSortBKHolder());
		
	}


}
