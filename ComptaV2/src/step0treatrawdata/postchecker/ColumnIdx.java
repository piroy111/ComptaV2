package step0treatrawdata.postchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticColumns;
import staticdata.StaticColumns.ColumnName;

public class ColumnIdx {

	/**
	 * Give the headers in the form of a map; Map<Header, Index of the column>
	 * @param _sReadFile
	 * @return
	 */
	public static Map<String, Integer> getMapIdxColumns(LitUnFichierEnLignes _sReadFile) {
		Map<String, Integer> lMapColumnToIdx = new HashMap<String, Integer>();
		try {
			for (int lIdx = 0; lIdx < _sReadFile.getmHeadersAndCommentList().get(0).size(); lIdx++) {
				String lHeader = _sReadFile.getmHeadersAndCommentList().get(0).get(lIdx);
				lMapColumnToIdx.put(lHeader, lIdx);
			}
		} catch (Exception lException) {
			BasicPrintMsg.error("Error when reading the file; Name= " + _sReadFile.getmNomCheminPlusFichier(), lException);
		}
		return lMapColumnToIdx;
	}

	/**
	 * check the header contains all the columns of 
	 * @param _sReadFile
	 * @return
	 */
	public static Map<String, Integer> getAndCheckMapIdxColumns(LitUnFichierEnLignes _sReadFile) {
		/*
		 * Read map
		 */
		List<String> lListHeaderMissing = getListHeaderMissings(_sReadFile);
		/*
		 * Exit on error
		 */
		if (lListHeaderMissing.size() > 0) {
			BasicPrintMsg.error("Some headers are missing"
					+ "\nFile= " + _sReadFile.getmNomCheminPlusFichier()
					+ "\nList headers missing= " + lListHeaderMissing.toString());
		}
		/*
		 * Else return
		 */
		return getMapIdxColumns(_sReadFile);
	}


	/**
	 * Gives the list of the missing headers compared to StaticNames.getLIST_COLUMNS_FILES_CSV()
	 * @param _sReadFile
	 * @return
	 */
	public static List<String> getListHeaderMissings(LitUnFichierEnLignes _sReadFile) {
		Map<String, Integer> lMapIdx = getMapIdxColumns(_sReadFile);
		List<String> lListHeaderMissing = new ArrayList<String>();
		for (ColumnName lColumnName : StaticColumns.ColumnName.values()) {
			String lHeader = lColumnName.toString();
			if (!lMapIdx.containsKey(lHeader)) {
				lListHeaderMissing.add(lHeader);
			}
		}
		return lListHeaderMissing;
	}

	/**
	 * 
	 * @param _sReadFile
	 * @return
	 */
	public static String getHeaderMissings(LitUnFichierEnLignes _sReadFile) {
		String lHeader = "";
		List<String> lListHeaderMissing = getListHeaderMissings(_sReadFile);
		for (String lHeaderLoop : lListHeaderMissing) {
			if (!lHeader.equals("")) {
				lHeader += ",";
			}
			lHeader += lHeaderLoop;
		}
		return lHeader;
	}




}
