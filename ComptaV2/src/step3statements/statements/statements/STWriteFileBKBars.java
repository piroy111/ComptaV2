package step3statements.statements.statements;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import conf.bkbartype.BKBarType;
import staticdata.StaticDate;
import staticdata.StaticDir;
import step1loadtransactions.bars.BKBar;
import step1loadtransactions.holder.BKHolder;
import step1loadtransactions.inventory.BKInventory;
import step1loadtransactions.transactions.BKTransaction;
import step3statements.statements.abstractstatements.STBKAccounts;
import step3statements.statements.main.STStatementAbstract;

public class STWriteFileBKBars extends STStatementAbstract {

	public STWriteFileBKBars(STBKAccounts _sSTBKAccounts) {
		super(_sSTBKAccounts);
	}

	@Override public String getpDirName() {
		return StaticDir.getOUTPUT_BKBARS();
	}

	@Override public void createLines() {
		List<BKHolder> lListBKHolder = new ArrayList<BKHolder>(pSTStatementGeneratorAbstract.getpListBKHolder());
		/*
		 * Find the last date
		 */
		int lDateMax = StaticDate.getDATE_MAX();
		if (lDateMax == -1) {
			lDateMax = BasicDateInt.getmYesterdayBD();
		}
		/*
		 * Header
		 */
		addToHeader("Reference number,Metal,Natural unit,Theoritical weight gram,Theoritical weight Oz,Real weight Oz,Owner,Holding");
		/*
		 * Build file content
		 */
		for (BKHolder lBKHolder : lListBKHolder) {
			BKInventory lBKInventory = lBKHolder.getpMapDateToBKInventory().get(lDateMax);
			for (BKBar lBKBar : lBKInventory.getpMapBKBarToHolding().keySet()) {
				/*
				 * Load
				 */
				BKBarType lBKBarType = lBKBar.getpBKBarType();
				int lHolding = lBKInventory.getpMapBKBarToHolding().get(lBKBar);
				if (lHolding > 1) {
					String lErrorMsg = "Error. This bar is held more than once. lBKBar= " + lBKBar
							+ "; Holding= " + lHolding
							+ "\nIDTrackFrom= " + lBKInventory.getpIDTrackFrom();
					for (BKTransaction lBKTransactionError : lBKHolder.getpListBKTransaction()) {
						if (lBKTransactionError.getpBKAsset().getpIsBar() && lBKTransactionError.getpComment().equals(lBKBar.getpRef())) {
							lErrorMsg += "\nBKTransaction= " + lBKTransactionError.toString();
						}
					}
					BasicPrintMsg.error(lErrorMsg);
				} else if (lHolding == 0) {
					continue;
				}
				/*
				 * Build Line
				 */
				String lLine = lBKBar.getpRef()
						+ "," + lBKBarType.getmMetal()
						+ "," + lBKBarType.getmNaturalUnit()
						+ "," + lBKBarType.getmWeightGram()
						+ "," + lBKBarType.getmWeightOz()
						+ "," + lBKBar.getpWeightOz()
						+ "," + lBKHolder.getpKey()
						+ "," + lHolding;
				/*
				 * Store Line
				 */
				addToListLine(lLine);
			}
		}
	}





}
