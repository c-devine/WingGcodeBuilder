package wgb.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import wgb.domain.measure.Length;
import wgb.domain.measure.Unit;
import wgb.fx.MapEntry;

public class GcodeSettings {

	private String x1 = "X";
	private String y1 = "Y";
	private String x2 = "Z";
	private String y2 = "E";
	private Length f = new Length(1000, Unit.MM);
	private Length acc = new Length(100, Unit.MM);
	private Length towerWidth = new Length(1000, Unit.MM);
	private Length blockWidth = new Length(500, Unit.MM);
	private Length towerOffset = new Length(250, Unit.MM);
	private Length leadin = new Length(10.0, Unit.MM);
	private Length kerf = new Length(0.0, Unit.MM);

	@JsonIgnore
	public List<MapEntry<String, Object>> getEntries() {

		List<String> keys = Arrays.asList("X1", "Y1", "X2", "Y2", "F", "Acc", "Tower Width", "Block Width",
				"Tower Offset", "Leadin", "Kerf");
		List<Object> values = Arrays.asList(x1, y1, x2, y2, f, acc, towerWidth, blockWidth, towerOffset, leadin, kerf);

		return IntStream.range(0, keys.size()).mapToObj(i -> new MapEntry<String, Object>(keys.get(i), values.get(i)))
				.collect(Collectors.toList());

	}

	@JsonIgnore
	public void setEntries(List<MapEntry<String, Object>> entries) {
		BeanWrapperImpl bWrapper = new BeanWrapperImpl(this);
		entries.forEach(e -> {
			String key = e.getKey().replaceAll("\\s+", "");
			if (bWrapper.getPropertyType(key).equals(Length.class) && !(e.getValue() instanceof Length))
				bWrapper.setPropertyValue(key, new Length((String) e.getValue()));
			else
				bWrapper.setPropertyValue(key, e.getValue());
		});
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

	public Length getF() {
		return f;
	}

	public void setF(Length f) {
		this.f = f;
	}

	public Length getAcc() {
		return acc;
	}

	public void setAcc(Length acc) {
		this.acc = acc;
	}

	public Length getTowerWidth() {
		return towerWidth;
	}

	public void setTowerWidth(Length towerWidth) {
		this.towerWidth = towerWidth;
	}

	public Length getBlockWidth() {
		return blockWidth;
	}

	public void setBlockWidth(Length blockWidth) {
		this.blockWidth = blockWidth;
	}

	public Length getTowerOffset() {
		return towerOffset;
	}

	public void setTowerOffset(Length towerOffset) {
		this.towerOffset = towerOffset;
	}

	public Length getLeadin() {
		return leadin;
	}

	public void setLeadin(Length leadin) {
		this.leadin = leadin;
	}

	public Length getKerf() {
		return kerf;
	}

	public void setKerf(Length kerf) {
		this.kerf = kerf;
	}

}
