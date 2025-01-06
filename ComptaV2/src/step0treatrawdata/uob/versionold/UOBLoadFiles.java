package step0treatrawdata.uob.versionold;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.BasicTime;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class UOBLoadFiles {

	protected UOBLoadFiles(UOBManagerOld _sUOBManager) {
		mUOBManager = _sUOBManager;
	}

	/*
	 * Data
	 */
	private UOBManagerOld mUOBManager;
	private List<Path> mListPathToRead;
	private LitUnFichierEnLignes mReadFile;

	/**
	 * Load files of UOB import into UOBTransaction & UOBAccount
	 */
	public final void run() {
		detectFiles();
		loadFiles();
	}

	/**
	 * Detects the files with the correct extension
	 */
	private void detectFiles() {
		String lDir = StaticDir.getIMPORT_UOB_STATEMENTS();
		List<Path> lListPaths = BasicFichiersNioRaw.getListPath(Paths.get(lDir));
		mListPathToRead = new ArrayList<Path>();
		for (Path lPath : lListPaths) {
			if (lPath.getFileName().toString().contains(StaticNames.getUOB_ACCOUNT_STATEMENT())) {
				mListPathToRead.add(lPath);
			}
		}
	}

	/**
	 * @return Load the files into the class UOBTransaction & UOBAccount
	 */
	private void loadFiles() {
		for (Path lPath : mListPathToRead) {
			mReadFile = new LitUnFichierEnLignes(lPath, true);
			UOBFileOld lUOBFile = mUOBManager.getmOrCreateUOBFile(mReadFile);
			int lNbUOBTransaction = 0;
			/*
			 * IDX of columns
			 */
			UOBAccountOld lUOBAccount = null;
			int lIdxAccountNumber = -1;
			int lIdxAccountCurrency = -1;
			int lIdxValueDate = -1;
			int lIdxDate = -1;
			int lIdxTime = -1;
			int lIdxDescription = -1;
			int lIdxYourReference = -1;
			int lIdxOurReference = -1;
			int lidxChequeNumber = -1;
			int lIdxRemarks = -1;
			int lIdxDeposit = -1;
			int lIdxWithdrawal = -1;
			int lIdxLedgerBalance = -1;
			/*
			 * Read file
			 */
			for (List<String> lLineStr : mReadFile.getmContenuFichierListe()) {
				if (lLineStr.size() < 1) {
					continue;
				}
				String lType = lLineStr.get(0).replaceAll(" ", "");
				/*
				 * Exit on T
				 */
				if (lType.startsWith("T")) {
					break;
				}
				/*
				 * Load header account
				 */
				if (lType.equals("H1")) {
					List<String> lListHeader = removeWhiteSpaces(lLineStr);
					lIdxAccountNumber = getIndex(lListHeader, "Account Number");
					lIdxAccountCurrency = getIndex(lListHeader, "Account Currency");
				}
				/*
				 * Load account
				 */
				else if (lType.equals("H2")) {
					if (lIdxAccountNumber == -1) {
						errorInFile("The header H1 is missing in the file");
					}
					long lNumber = BasicString.getLong(lLineStr.get(lIdxAccountNumber).trim());
					String lCurrency = lLineStr.get(lIdxAccountCurrency).trim();
					lUOBAccount = mUOBManager.getmOrCreateUOBAccount(lNumber, lCurrency);
				}
				/*
				 * Load header transaction
				 */
				else if (lType.equals("D1")) {
					List<String> lListHeader = removeWhiteSpaces(lLineStr);
					lIdxValueDate = getIndex(lListHeader, " Value Date ");
					lIdxDate = getIndex(lListHeader, " Date ");
					lIdxTime = getIndex(lListHeader, " Time ");
					lIdxDescription = getIndex(lListHeader, " Description ");
					lIdxYourReference = getIndex(lListHeader, " Your Reference ");
					lIdxOurReference = getIndex(lListHeader, " Our Reference ");
					lidxChequeNumber = getIndex(lListHeader, " Cheque Number ");
					lIdxRemarks = getIndex(lListHeader, " Remarks ");
					lIdxDeposit = getIndex(lListHeader, " Deposit ");
					lIdxWithdrawal = getIndex(lListHeader, " Withdrawal ");
					lIdxLedgerBalance = getIndex(lListHeader, " Ledger Balance");
				}
				/*
				 * Load transaction
				 */
				else if (lType.equals("D2")) {
					/*
					 * Errors
					 */
					if (lIdxValueDate == -1) {
						errorInFile("The header D1 is missing in the file");
					}
					if (lUOBAccount == null) {
						errorInFile("The account is not defined in the file whereas we have some transactions");
					}
					/*
					 * Load
					 */
					int lValueDate = BasicDateInt.getmDateFromString(lLineStr.get(lIdxValueDate).trim(), false);
					int lDate = BasicDateInt.getmDateFromString(lLineStr.get(lIdxDate).trim(), false);
					long lTime = BasicTime.getHeureJavaFromStringHHMMSSsss(removeAMPM(lLineStr.get(lIdxTime).trim()));
					String lDescription = lLineStr.get(lIdxDescription).trim().replaceAll(",", ";");
					String lYourReference = lLineStr.get(lIdxYourReference).trim();
					String lOurReference = lLineStr.get(lIdxOurReference).trim();
					String lChequeNumber = lLineStr.get(lidxChequeNumber).trim();
					String lRemarks = lLineStr.get(lIdxRemarks).trim().replaceAll(",", ";");
					double lDeposit = BasicString.getDouble(lLineStr.get(lIdxDeposit).replaceAll(",", ""));
					double lWithdrawal = BasicString.getDouble(lLineStr.get(lIdxWithdrawal).replaceAll(",", ""));
					double lLedgerBalance = BasicString.getDouble(lLineStr.get(lIdxLedgerBalance).replaceAll(",", ""));
					/*
					 * Create UOBTransaction
					 */
					UOBTransactionOld lUOBTransaction = new UOBTransactionOld(lUOBAccount, 
							lValueDate, lDate, lTime, lDescription, lYourReference, lOurReference, 
							lChequeNumber, lRemarks, lDeposit, lWithdrawal, lLedgerBalance);
					mUOBManager.declareNewUOBTransaction(lUOBTransaction);
					lNbUOBTransaction++;
					/*
					 * Add to UOBFile
					 */
					lUOBFile.addNewUOBTransaction(lUOBTransaction);
					/*
					 * Error
					 */
					if (lValueDate == 0) {
						errorInFile("The transaction does not have any value date; lValueDate= 0");
					}
				}
			}
			/*
			 * Communication
			 */
			if (mUOBManager.getpIsCom()) {
				System.out.println("File '" + mReadFile.getmNomFichier() + "' read --> " 
						+ (lNbUOBTransaction == 0 ? "empty" : lNbUOBTransaction + " transactions"));
			}
		}
	}

	/**
	 * @return Remove white spaces of all elements of the list of String
	 * @param _sListString
	 */
	private List<String> removeWhiteSpaces(List<String> _sListString) {
		List<String> lListString = new ArrayList<String>();
		for (String lWord : _sListString) {
			lWord = lWord.replaceAll(" ", "");
			lWord = lWord.replaceAll("\\t", "");
			lListString.add(lWord);
		}
		return lListString;
	}

	/**
	 * @return the index of the header in the list. Send an error message if it is not found
	 * @param _sListString
	 * @param _sWordToLookFor
	 * @param _sFileName
	 */
	private int getIndex(List<String> _sListString, String _sWordToLookFor) {
		_sWordToLookFor = _sWordToLookFor.replaceAll(" ", "");
		_sWordToLookFor = _sWordToLookFor.replaceAll("\t", "");
		if (!_sListString.contains(_sWordToLookFor)) {
			errorInFile("The list of headers does not contain the header we are looking for"
					+ "\nListHeaders= " + _sListString.toString() + "; Header= " + _sWordToLookFor);
		}
		return _sListString.indexOf(_sWordToLookFor);
	}

	/**
	 * Stop program and send an error message
	 * @param _sErroMSg
	 */
	private void errorInFile(String _sErroMSg) {
		BasicPrintMsg.error(_sErroMSg
				+ "\nFile= " + mReadFile.getmNomCheminPlusFichier());
	}


	/**
	 * Remove the AM or PM at the ned of the text of a time
	 * @param _sTimeStr
	 * @return
	 */
	private String removeAMPM(String _sTimeStr) {
		String lTimeStr = "";
		for (int lIdx = 0; lIdx < _sTimeStr.length(); lIdx++) {
			char lChar = _sTimeStr.charAt(lIdx);
			if (lChar == ':' || (lChar >= '0' && lChar <= '9')) {
				lTimeStr += lChar;
			}
		}
		return lTimeStr;
	}

	/*
	 * Getters & Setters
	 */
	public final List<Path> getmListPathToRead() {
		return mListPathToRead;
	}








































}


