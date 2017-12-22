package wgb.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javafx.geometry.Point2D;

@SuppressWarnings("serial")
public class Point2DDeserializer extends StdDeserializer<List<Point2D>> {

	public Point2DDeserializer() {
		this(null);
	}

	public Point2DDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public List<Point2D> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		List<Point2D> points = new ArrayList<Point2D>();
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		Iterator<JsonNode> elements = node.elements();
		for (; elements.hasNext();) {
			JsonNode n = elements.next();
			points.add(new Point2D(n.get("x").asDouble(), n.get("y").asDouble()));
		}

		return points;
	}
}
