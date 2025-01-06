package step4reconciliation.reconciliators;

import java.nio.file.Path;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicString;
import staticdata.StaticDate;
import staticdata.StaticNames;
import step4reconciliation.file.RNColumnInFile;
import step4reconciliation.file.RNWriteFile;
import step4reconciliation.main.RNMain;
import step4reconciliation.main.RNReconciliatorRoot;
import uob.step1objects.transactions.UOBTransaction;

public class RNUOBAccounts extends RNReconciliatorRoot{

	/**
	 * @deprecated
	 * @param _sRNMain
	 */
	public RNUOBAccounts(RNMain _sRNMain) {
		super(_sRNMain);
	}

	@Override public int getmDateMaximum() {
		List<Path> lListPathToRead = mRNMain.getmUOBMainManager().getmUOBAccountLoader().getmUOBFileManager().getmListPathToRead();
		int lDateMax = -1;
		for (Path lPath : lListPathToRead) {
			String lNameFile = lPath.getFileName().toString();
			int lYear = BasicString.getInt(lNameFile.substring(0, 4));
			int lMonth = BasicString.getInt(lNameFile.substring(5, 7));
			int lDate = BasicDateInt.getmDateInt(lYear, lMonth, 1);
			lDateMax = Math.max(lDateMax, lDate);
		}
		lDateMax = BasicDateInt.getmEndOfMonth(lDateMax);
		return lDateMax;
	}

	@Override public void run() {
		/*
		 * Build columns of the file
		 */
		RNWriteFile lRNWriteFile = new RNWriteFile(BasicDateInt.getmToday() + StaticNames.getOUTPUT_RECONCILIATION_UOB());
		UOBTransaction lUOBTransactionPrevious = null;
		for (UOBTransaction lUOBTransaction : mRNMain.getmUOBMainManager().getmUOBTransactionManager().getmListUOBTransaction()) {
			/*
			 * Add value of UOBTransaction
			 */
			String lKey = lUOBTransaction.getmUOBAccount().getmCurrency()
					+ "; Act= " + lUOBTransaction.getmUOBAccount().getmNumber();
			RNColumnInFile lRNColumnInFile = lRNWriteFile.getmOrCreateRNColumnInFile(lKey);
			/*
			 * Check the file is ascending or descending
			 */
			boolean lIsSkip = false;
			if (lUOBTransactionPrevious != null && lUOBTransaction.getmValueDate() == lUOBTransactionPrevious.getmValueDate()
					&& lUOBTransaction.getmUOBAccount().equals(lUOBTransactionPrevious.getmUOBAccount())
					&& lUOBTransaction.getmValueDate() >= StaticDate.getDATE_FILE_UOB_SORT_DESCENDING()) {
				lIsSkip = true;
			}
			/*
			 * Add new value
			 */
			if (!lIsSkip) {
				lRNColumnInFile.addNewValue(lUOBTransaction.getmValueDate(), 
						lUOBTransaction.getmDate(), 
						lUOBTransaction.getmTime(), 
						lUOBTransaction.getmLedgerBalance());
			}
			/*
			 * Save
			 */
			lUOBTransactionPrevious = lUOBTransaction;
		}		
		/*
		 * Write File 
		 */
		lRNWriteFile.writeFile();
	}
















}
