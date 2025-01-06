package step1loadtransactions.accounts;

import step0treatrawdata.objects.BKAsset;

public class BKAccount implements Comparable<BKAccount> {

	/**
	 * a BKAccount is defined by its email address
	 * @param _sEmailAddress : the unique email address which defines the account (one email address per account)
	 * @param _sBKAccountManager
	 */
	public BKAccount(String _sEmailAddress) {
		pEmailAddress = _sEmailAddress;
	}
	
	/*
	 * Data
	 */
	private String pEmailAddress;
	private String pNameOwner;
	private String pEmailAddressJoint;
	private BKAsset pBKAssetCurrency;
	private String pCommercialOrigin;
	
	/**
	 * a client account which is not pierre roy
	 */
	public final boolean getpIsClientAccount() {
		return !this.equals(BKAccountManager.getpBKAccountBunker())
				&& !this.equals(BKAccountManager.getpBKAccountPierreRoy());
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return "Email= " + pEmailAddress
				+ "; Owner= " + pNameOwner
				+ "; Currency= " + pBKAssetCurrency.getpName();
	}
	
	@Override public int compareTo(BKAccount _sBKAccount) {
		return pEmailAddress.compareTo(_sBKAccount.pEmailAddress);
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final String getpEmailAddress() {
		return pEmailAddress;
	}
	public final String getpNameOwner() {
		return pNameOwner;
	}
	public final String getpEmailAddressJoint() {
		return pEmailAddressJoint;
	}
	public final BKAsset getpBKAssetCurrency() {
		return pBKAssetCurrency;
	}
	protected final void setpNameOwner(String pNameOwner) {
		this.pNameOwner = pNameOwner;
	}
	protected final void setpEmailAddressJoint(String pEmailAddressJoint) {
		this.pEmailAddressJoint = pEmailAddressJoint;
	}
	protected final void setpBKAssetCurrency(BKAsset pBKAssetCurrency) {
		this.pBKAssetCurrency = pBKAssetCurrency;
	}
	public final String getpCommercialOrigin() {
		return pCommercialOrigin;
	}
	protected final void setpCommercialOrigin(String pCommercialOrigin) {
		this.pCommercialOrigin = pCommercialOrigin;
	}

	
	
	
	
	
	
	
	
}
