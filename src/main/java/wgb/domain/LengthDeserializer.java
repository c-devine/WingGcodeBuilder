package wgb.domain;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class LengthDeserializer extends StdDeserializer<Length> {

	public LengthDeserializer() {
		this(null);
	}

	public LengthDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Length deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return new Length(jp.getDoubleValue(), Unit.MM);
	}
}
