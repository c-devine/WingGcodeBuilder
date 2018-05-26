package wgb.domain.measure;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class WeightSerializer extends StdSerializer<Weight> {

	public WeightSerializer() {
		this(null);
	}

	public WeightSerializer(Class<Weight> t) {
		super(t);
	}

	@Override
	public void serialize(Weight value, JsonGenerator gen, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		gen.writeNumber(value.getWeight(Unit.GM));
	}

}
