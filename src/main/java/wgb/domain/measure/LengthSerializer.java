package wgb.domain.measure;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class LengthSerializer extends StdSerializer<Length> {

	public LengthSerializer() {
		this(null);
	}

	public LengthSerializer(Class<Length> t) {
		super(t);
	}

	@Override
	public void serialize(Length value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		gen.writeNumber(value.getLength(Unit.MM));
	}

}
