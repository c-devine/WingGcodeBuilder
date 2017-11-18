package wgb.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapperImpl;

import wgb.fx.MapEntry;

public class GcodeSettings {

	private String x1 = "X";
	private String y1 = "Y";
	private String x2 = "Z";
	private String y2 = "E";
	private double f = 1000;
	private double acc = 100;
	private double leadin = 10.0;
	private double kerf = 0.0;

	private List<MapEntry<String, Object>> entries = new ArrayList<MapEntry<String, Object>>();
	{
		entries.add(new MapEntry<String, Object>("X1", x1));
		entries.add(new MapEntry<String, Object>("Y1", y1));
		entries.add(new MapEntry<String, Object>("X2", x2));
		entries.add(new MapEntry<String, Object>("Y2", y2));
		entries.add(new MapEntry<String, Object>("F", f));
		entries.add(new MapEntry<String, Object>("Acc", acc));
		entries.add(new MapEntry<String, Object>("Leadin", leadin));
		entries.add(new MapEntry<String, Object>("Kerf", kerf));
	}

	public String getX1() {
		return x1;
	}

	public void setX1(String x1) {
		this.x1 = x1;
	}

	public String getY1() {
		return y1;
	}

	public void setY1(String y1) {
		this.y1 = y1;
	}

	public String getX2() {
		return x2;
	}

	public void setX2(String x2) {
		this.x2 = x2;
	}

	public String getY2() {
		return y2;
	}

	public void setY2(String y2) {
		this.y2 = y2;
	}

	public double getAcc() {
		return acc;
	}

	public void setAcc(double acc) {
		this.acc = acc;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getLeadin() {
		return leadin;
	}

	public void setLeadin(double leadin) {
		this.leadin = leadin;
	}

	public double getKerf() {
		return kerf;
	}

	public void setKerf(double kerf) {
		this.kerf = kerf;
	}

	public List<MapEntry<String, Object>> getEntries() {
		return entries;
	}

	public void setEntries(List<MapEntry<String, Object>> entries) {
		this.entries = entries;
		BeanWrapperImpl bWrapper = new BeanWrapperImpl(this);
		entries.forEach(e -> bWrapper.setPropertyValue(e.getKey(), e.getValue()));
	}

}
