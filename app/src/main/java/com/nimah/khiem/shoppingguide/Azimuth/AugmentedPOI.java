package com.nimah.khiem.shoppingguide.Azimuth;

public class AugmentedPOI {
	private int id;
	private double teoreticalAzimuth;
	private double minRange;
	private double maxRange;

	public AugmentedPOI(int id, double teoricalAzimuth, double minRange, double maxRange) {
		this.id = id;
		this.teoreticalAzimuth = teoricalAzimuth;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTeoreticalAzimuth() {
		return teoreticalAzimuth;
	}

	public void setTeoreticalAzimuth(double teoricalAzimuth) {
		this.teoreticalAzimuth = teoricalAzimuth;
	}

	public double getMinRange() {
		return minRange;
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}

	public double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public boolean isInside(double mAzimuth)
	{
		return (minRange<=mAzimuth && mAzimuth<=maxRange);
	}
}
