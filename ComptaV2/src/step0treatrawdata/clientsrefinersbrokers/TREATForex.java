package step0treatrawdata.clientsrefinersbrokers;

import java.util.ArrayList;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicFichiers;
import basicmethods.BasicString;
import basicmethods.LitUnFichierEnLignes;
import staticdata.StaticDir;
import step0treatrawdata.objects.BKAsset;
import step0treatrawdata.objects.BKAssetManager;

public class TREATForex extends TREATRoot {

	public TREATForex(TREATManager _sTREATManager) {
		super(_sTREATManager, 
				StaticDir.getIMPORT_FOREX(), 
				StaticDir.getTREATED_FOREX());
	}

	/*
	 * Data
	 */
	private String pNameIncome;
	
	@Override public void treatAndWriteFile(LitUnFichierEnLignes _sReadFile) {
		pNameIncome = _sReadFile.getmNomFichier().replaceAll(".csv", "").replaceAll("_", " ");
		/*
		 * Header
		 */
		String lHeader = getpClassicHeader();
		/*
		 * File Content
		 */
		List<String> lListLineToWrite = new ArrayList<String>();
		for (List<String> lLineRead : _sReadFile.getmContenuFichierListe()) {
			/*
			 * Load Line
			 */
			int lIdx = -1;
			String lDate = lLineRead.get(++lIdx); 
			String lAccountBuy = lLineRead.get(++lIdx);
			String lAccountSell = lLineRead.get(++lIdx);
			String lCurrencyPair = lLineRead.get(++lIdx);
			double lQuantity = AMNumberTools.getDouble(lLineRead.get(++lIdx));
			double lPrice = AMNumberTools.getDouble(lLineRead.get(++lIdx));
			/*
			 * 
			 */
			String lCurrencyLeft = lCurrencyPair.substring(0, 3);
			String lCurrencyRight = lCurrencyPair.substring(3);
			/*
			 * Case the 2nd currency is USD
			 */
			if (lCurrencyRight.equals("USD")) {
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyLeft, lQuantity, lPrice));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyLeft, -lQuantity, lPrice));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyRight, -lQuantity * lPrice, 1.));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyRight, lQuantity * lPrice, 1.));
			}
			/*
			 * Case the 1st currency is USD
			 */
			else if (lCurrencyLeft.equals("USD")) {
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyLeft, lQuantity, 1.));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyLeft, -lQuantity, 1.));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyRight, -lQuantity * lPrice, 1 / lPrice));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyRight, lQuantity * lPrice, 1 / lPrice));
			}
			/*
			 * Case none of the currency is USD
			 */
			else {
				BKAsset lBKAsset1 = BKAssetManager.getpAndCheckBKAsset(lCurrencyLeft);
				double lPrice1 = lBKAsset1.getpPriceUSD(BasicString.getInt(lDate));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyLeft, lQuantity, lPrice1));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyLeft, -lQuantity, lPrice1));
				double lPrice2 = lPrice / lPrice1;
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountBuy, lCurrencyRight, -lQuantity * lPrice, 1 / lPrice2));
				lListLineToWrite.add(buildLineToWrite(lDate, lAccountSell, lCurrencyRight, lQuantity * lPrice, 1 / lPrice2));				
			}
		}
		/*
		 * Write file treated
		 */
		String lNameFile = _sReadFile.getmNomFichier();
		BasicFichiers.writeFile(pDirTreat, lNameFile, lHeader, lListLineToWrite);
	}

	/**
	 * Line to write in file treated
	 * @param _sDate
	 * @param _sAccountBuy
	 * @param _sCurrency
	 * @param _sAmount
	 * @param _sPrice
	 * @return
	 */
	private String buildLineToWrite(String _sDate, 
			String _sAccountBuy, 
			String _sCurrency, 
			double _sAmount,
			double _sPrice) {
		String lLine = _sDate
				+ "," + "FOREX"
				+ "," + _sCurrency
				+ "," + _sAmount
				+ "," + _sAccountBuy
				+ "," + pNameIncome
				+ "," + _sPrice;
		return lLine;
	}
	
}
