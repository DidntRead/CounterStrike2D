package proj.cs2d.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MapLoader {
	public static Map load(InputStream in) {
		try {
			ObjectInputStream inp = new ObjectInputStream(in);
			Map map = (Map)inp.readObject();
			return map;
		} catch (ClassNotFoundException | IOException e) {
		}
		return null;
	}
}
