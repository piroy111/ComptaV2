package step0treatrawdata.clientsrefinersbrokers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import step0treatrawdata.postchecker.ColumnIdx;

public abstract class TREATRoot {


	public TREATRoot(TREATManager _sTREATManager, 
			String _sDirSource, String _sDirTreat) {
		pTREATManager = _sTREATManager;
		pDirSource = _sDirSource;
		pDirTreat = _sDirTreat;
		/*
		 * Declare
		 */
		pTREATManager.declareTreatRoot(this);
	}
	
	
	/*
	 * Data
	 */
	protected TREATManager pTREATManager;
	protected String pDirSource;
	protected String pDirTreat;
	/*
	 * abstract
	 */
	public abstract void treatAndWriteFile(LitUnFichierEnLignes _sReadFile);
	
	/**
	 * Treat all the files of the directory
	 */
	public final void treatDir() {
		BasicPrintMsg.displayTitle(this, "Treat into " + pDirTreat);
		List<Path> lListPath = BasicFichiersNioRaw.getListPath(Paths.get(pDirSource));
		for (Path lPath : lListPath) {
			if (lPath.getFileName().toString().endsWith(".csv")) {
				/*
				 * Write file
				 */
				treatAndWriteFile(new LitUnFichierEnLignes(lPath,  true));
				/*
				 * Check headers are all here
				 */
				ColumnIdx.getAndCheckMapIdxColumns(new LitUnFichierEnLignes(pDirTreat,  
						lPath.getFileName().toString(), true));
			}
		}
		/*
		 * Check bijection
		 */
		List<Path> lListPathTreated = BasicFichiersNioRaw.getListPath(Paths.get(pDirTreat));
		List<Path> lListPathShouldNotBeHere = new ArrayList<>();
		for (Path lPathTreated : lListPathTreated) {
			boolean lIsFound = false;
			for (Path lPathOrigin : lListPath) {
				if (lPathOrigin.getFileName().toString().equals(lPathTreated.getFileName().toString())) {
					lIsFound = true;
					break;
				}
			}
			if (!lIsFound) {
				lListPathShouldNotBeHere.add(lPathTreated);
			}
		}
		if (lListPathShouldNotBeHere.size() > 0) {
			String lMsgError = "Some files remains in the dir treated from a previous version, and should not be here anymore. You must delete manually those files.";
			lMsgError += "\nDir= '" + pDirTreat + "'";
			lMsgError += "\nList of files treated which should no longer be here:";
			for (Path lPathError : lListPathShouldNotBeHere) {
				lMsgError += "\n'" + lPathError.getFileName().toString() + "'";
			}
			BasicPrintMsg.error(lMsgError);
		}
	}
	
	/**
	 * classic header
	 * @return
	 */
	protected final String getpClassicHeader() {
		return "#Date,Comment,BKAsset,Amount,BKAccount,BKIncome,BKPrice";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
