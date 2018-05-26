package wgb.domain.measure;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class WeightDeserializer extends StdDeserializer<Weight> {

	public WeightDeserializer() {
		this(null);
	}

	public WeightDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Weight deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return new Weight(jp.getDoubleValue(), Unit.GM);
	}
}
