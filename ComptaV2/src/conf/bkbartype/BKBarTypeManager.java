package conf.bkbartype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import staticdata.StaticNames;

public class BKBarTypeManager {

	/*
	 * Data
	 */
	private static List<BKBarType> LIST_BKBARTYPE;
	private static Map<String, BKBarType> MAP_NAME_TO_BKBARTYPE;
	private static double OZ = 31.103;
	private static String[] METAL = new String[]{"GOLD", "SILVER", "PLATINUM"};


	private static void loadFileConf() {
		if (LIST_BKBARTYPE != null) {
			return;
		}
		MAP_NAME_TO_BKBARTYPE = new HashMap<String, BKBarType>();
		LIST_BKBARTYPE = new ArrayList<BKBarType>();
		/*
		 * Load file
		 */
		String lDir = StaticDir.getCONF_COMPTA();
		String lNameFile = StaticNames.getCONF_BKBARTYPE();
		LitUnFichierEnLignes lReadFile = new LitUnFichierEnLignes(lDir, lNameFile, true);
		/*
		 * Fill the list
		 */
		for (List<String> lLineStr : lReadFile.getmContenuFichierListe()) {
			/*
			 * Load line
			 */
			int lIdx = -1;
			String lName = lLineStr.get(++lIdx);
			String lOz = lLineStr.get(++lIdx);
			String lGrams = lLineStr.get(++lIdx);
			/*
			 * Load weight
			 */
			double lWeightOz = Double.NaN;
			double lWeightGrams = Double.NaN;
			String lNaturalUnit = "";
			if (!lOz.equals("")) {
				lNaturalUnit = "Oz";
				lWeightOz = BasicString.getDouble(lOz);
				lWeightGrams = lWeightOz * OZ;
			} else if (!lGrams.equals("")) {
				lNaturalUnit = "g";
				lWeightGrams = BasicString.getDouble(lGrams);
				lWeightOz = lWeightGrams / OZ;
			} else {
				BasicPrintMsg.error("Only one column can be empty"
						+ "\nLine in error = " + lLineStr.toString()
						+ "\nFile= " + lReadFile.getmNomCheminPlusFichier());
			}
			/*
			 * Create BKBarType
			 */
			for (String lMetal : METAL) {
				String lNameMetal = lMetal + " " + lName;
				BKBarType lBKBarType = MAP_NAME_TO_BKBARTYPE.get(lNameMetal);
				if (lBKBarType == null) {
					lBKBarType = new BKBarType(lNameMetal, lWeightOz, lWeightGrams, lNaturalUnit, lMetal);
					MAP_NAME_TO_BKBARTYPE.put(lNameMetal, lBKBarType);
					LIST_BKBARTYPE.add(lBKBarType);
				}
			}
		}
	}

	/**
	 * get the nearest BKBarType
	 * @param _sWeightOz
	 * @return
	 */
	public static final BKBarType getBKBarType(double _sWeightOz, String _sMetal) {
		loadFileConf();
		BKBarType lBKBarTypeNear = null;
		double lDistanceMin = Double.NaN;
		for (BKBarType lBKBarType : LIST_BKBARTYPE) {
			if (lBKBarType.getmMetal().equals(_sMetal)) {
				double lDistance = Math.abs(_sWeightOz - lBKBarType.getmWeightOz());
				if (Double.isNaN(lDistanceMin) || lDistance < lDistanceMin) {
					lDistanceMin = lDistance;
					lBKBarTypeNear = lBKBarType;
				}
			}
		}
		return lBKBarTypeNear;
	}

	/*
	 * Getters & Setters
	 */
	public static final List<BKBarType> getLIST_BKBARTYPE() {
		loadFileConf();
		return LIST_BKBARTYPE;
	}








}
