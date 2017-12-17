package wgb.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectClone {

	public static <T> T deepCopy(T object, Class<T> aClass) throws ClassNotFoundException, IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bOut);
		oos.writeObject(object);
		oos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bOut.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);

		return aClass.cast(ois.readObject());
	}

}
