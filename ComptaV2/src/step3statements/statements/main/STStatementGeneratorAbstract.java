package step3statements.statements.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicPrintMsg;
import step1loadtransactions.holder.BKHolderGenerator;
import step2loans.createloanstransactions.LNManager;

public abstract class STStatementGeneratorAbstract extends BKHolderGenerator {

	public STStatementGeneratorAbstract(LNManager _sLNManager) {
		super(_sLNManager.getpBKTransactionManager());
		pLNManager = _sLNManager;
		/*
		 * 
		 */
		pListSTStatementAbstract = new ArrayList<>();
	}

	/*
	 * Abstract
	 */
	public abstract void instantiateStatements();
	/*
	 * Data
	 */
	protected LNManager pLNManager;
	private List<STStatementAbstract> pListSTStatementAbstract;

	/**
	 * 
	 * @param _sSTStatementAbstract
	 */
	public final void declareNewSTStatementAbstract(STStatementAbstract _sSTStatementAbstract) {
		if (!pListSTStatementAbstract.contains(_sSTStatementAbstract)) {
			pListSTStatementAbstract.add(_sSTStatementAbstract);
		}
	}

	/**
	 * Write the file
	 * @param _sDir
	 * @param _sFileName
	 */
	public void run() {
		BasicPrintMsg.displayTitle(this, "Statements using " + this.getClass().getSimpleName());
		instantiateStatements();
		BasicPrintMsg.display(this, "Number of statements created= " + pListSTStatementAbstract.size());
		generateBKHolder();
		Collections.sort(pListBKHolder, new STSortBKHolder());
		for (STStatementAbstract lSTStatementAbstract : pListSTStatementAbstract) {
			BasicPrintMsg.display(this, "Compute and write file for statement " + lSTStatementAbstract.getClass().getSimpleName());
			lSTStatementAbstract.initiate();
			lSTStatementAbstract.createLines();
			if (!lSTStatementAbstract.getpIsFileWrittenAlready()) {
				lSTStatementAbstract.writeFile();
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final LNManager getpLNManager() {
		return pLNManager;
	}
	
	
}
