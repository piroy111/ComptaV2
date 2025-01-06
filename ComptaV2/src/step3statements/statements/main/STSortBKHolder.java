package step3statements.statements.main;

import java.util.Comparator;

import step1loadtransactions.holder.BKHolder;

public class STSortBKHolder implements Comparator<BKHolder> {

	@Override
	public int compare(BKHolder _sBKHolder1, BKHolder _sBKHolder2) {
		return _sBKHolder1.getpKey().compareTo(_sBKHolder2.getpKey());
	}

}
