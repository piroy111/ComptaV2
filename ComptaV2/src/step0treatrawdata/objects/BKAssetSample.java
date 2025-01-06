package step0treatrawdata.objects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BKAssetSample {

	public static void main(String[] _sArgs) {
		displaySampleMetal();
	}

	protected static void displayAll() {
		for (BKAsset lBKObject : BKAssetManager.getpMapNameToBKAsset().values()) {
			System.out.println(lBKObject.getpName()
					+ "; Map= " + lBKObject.getpMapDateToPrice().toString());
		}
	}
	
	protected static void displaySampleForex() {
		System.out.println(BKAssetManager.getpForexReference(20160331, "SGD"));
	}
	
	protected static void displaySampleMetal() {
		BKAsset lBKAsset = BKAssetManager.getpMapNameToBKAsset().get("XAU");
		System.out.println(lBKAsset.getpName()
				+ "; Map= " + lBKAsset.getpMapDateToPrice().toString());
		List<Integer> lListDates = new ArrayList<>(lBKAsset.getpMapDateToPrice().keySet());
		Collections.sort(lListDates);
		for (int lDate : lListDates) {
			System.out.println("Date= " + lDate + "; Value= " + lBKAsset.getpMapDateToPrice().get(lDate));
		}		
	}
}

