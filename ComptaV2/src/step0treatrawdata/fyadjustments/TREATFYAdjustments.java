package step0treatrawdata.fyadjustments;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDate;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class TREATFYAdjustments {

	
	
	public TREATFYAdjustments() {
	}

	/*
	 * Data
	 */
	
	public final void run() {
		/*
		 * Initiate
		 */
		String lDirImport = StaticDir.getIMPORT_FY_ADJUSTMENTS();
		String lDirTreated = StaticDir.getTREATED_FY_ADJUSTMENTS();
		List<String> lListLineToWriteGlobal = new ArrayList<>();
		String lHeader = null;
		List<Path> lListPathImport = BasicFichiersNio.getListFilesAndSubFiles(Paths.get(lDirImport));
		/*
		 * Loop on all files import
		 */
		for (Path lPathImport : lListPathImport) {
			/*
			 * Check the name of the file
			 */
			String lNameFile = lPathImport.getFileName().toString();
			if (!lNameFile.startsWith(StaticNames.getTREATED_FY_ADJUSTMENTS_PREFIX())) {
				BasicPrintMsg.error("The file of adjustment treated must begin with the prefix: '"
						+ StaticNames.getTREATED_FY_ADJUSTMENTS_PREFIX() + "'"
						+ " and finish with the name of the file origin (the physical account it affects)");
			}
			/*
			 * Filter the transactions with the date, so that we rule out the one after the date limit
			 */
			LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lPathImport, true);
			List<String> lLisLineToWrite = new ArrayList<>();
			for (int lIdx = 0; lIdx < lReadFile.getmContenuFichierLignes().size(); lIdx++) {
				String lLineStr = lReadFile.getmContenuFichierLignes().get(lIdx);
				List<String> lListLine = lReadFile.getmContenuFichierListe().get(lIdx);
				int lDate = BasicString.getInt(lListLine.get(0));
				if (StaticDate.getDATE_MAX() == -1 || lDate <= StaticDate.getDATE_MAX()) {
					lLisLineToWrite.add(lLineStr);
					lListLineToWriteGlobal.add(lLineStr);
				}
			}		
			/*
			 * Write file
			 */
			lHeader = lReadFile.getmHeadersAndComments().get(0);
			BasicFichiers.writeFile(lDirTreated, lNameFile, lHeader, lLisLineToWrite);
		}
		/*
		 * Write file global
		 */
		if (lListLineToWriteGlobal.size() > 0) {
			String lDirOutput = StaticDir.getOUTPUT_FY_ADJUSTMENTS();
			String lNameFileGlobal = StaticNames.getOUTPUT_FY_ADJUSTMENTS();
			BasicFichiers.writeFile(lDirOutput, lNameFileGlobal, lHeader, lListLineToWriteGlobal);
		}
	}
	
	
	
	
}
