package rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import com.google.protobuf.ByteString;

/**
 * Eine Toolklasse mit allerlei nuetzlichen Methoden um RPCs zu ermoeglichen.
 */
public final class RPCSecrets {
	/**
	 * Serialisiert das gegebene Objekt. Diese Serialisierung enthaelt alle Daten um das Objekt wieder
	 * zu deserialisieren, inclusive Typ-Informationen.
	 * @param obj das zu serialisierende Objekt
	 * @return die Serialisierung
	 */
	public final static ByteString serialize(Object obj) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutputStream objectstream;
		try {
			objectstream = new ObjectOutputStream(stream);
			objectstream.writeObject(obj);
			return ByteString.copyFrom(stream.toByteArray());
		} catch (IOException e) {
			// this should not happen, so we don't expect an exception -> throw a runtime-exception instead
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Deserialisiert ein Objekt von Type <tt>R</tt>.
	 * 
	 * @param data die Bytes aus denen das Objekt von Type <tt>R</tt> rekonstruiert werden kann.
	 * @throws ClassCastException wenn das Objekt das in <tt>data</tt> angebeben wurde nicht von Typ <tt>R</tt> ist.
	 * @throws ClassNotFoundException wenn die Objekt-Daten die in <tt>data</tt> angeben wurden keiner Klasse
	 * zugeordnet werden koennen, die der VM zu verfuegung steht.
	 */
	@SuppressWarnings("unchecked")
	public final static <R> R deserialize(byte[] data) throws ClassNotFoundException {
		ObjectInputStream stream;
		try {
			stream = new ObjectInputStream(new ByteArrayInputStream(data));
			return (R)stream.readObject();
		} catch (IOException e) {
			// this should not happen, so we don't expect an exception -> throw a runtime-exception instead
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * Deserialisiert ein Objekt von Type <tt>R</tt>.
	 * 
	 * Wrapper der Methode {@link RPCSecrets#deserialize(byte[])}.
	 * 
	 * @param data die Bytes aus denen das Objekt von Type <tt>R</tt> rekonstruiert werden kann.
	 * @throws ClassCastException wenn das Objekt das in <tt>data</tt> angebeben wurde nicht von Typ <tt>R</tt> ist.
	 * @throws ClassNotFoundException wenn die Objekt-Daten die in <tt>data</tt> angeben wurden keiner Klasse
	 * zugeordnet werden koennen, die der VM zu verfuegung steht.
	 */
	public final static <R> R deserialize(ByteString data) throws ClassNotFoundException {
		return deserialize(data.toByteArray());
	}

	/**
	 * Deserialisiert eine Liste von Objekten.
	 * 
	 * @param datalist eine Liste der Bytes aus denen die Objekte rekonstruiert werden koennen.
	 * @throws ClassNotFoundException wenn einer der Object-Daten die in <tt>datalist</tt> angeben wurden keinee Klasse
	 * zugeordnet werden kann, die der VM zu verfuegung steht.
	 */
	public final static Object[] deserialize(List<ByteString> datalist) throws ClassNotFoundException {
		Object[] objects = new Serializable[datalist.size()];
		
		for(int i = 0; i < datalist.size(); i++) {
			objects[i] = deserialize(datalist.get(i));
		}
		
		return objects;
	}
	
	/**
	 * Versucht das boxing von allen primitiven Datentypen zu entfernen
	 * wenn moeglich. Falls dies nicht moeglich ist (es handelt sich nicht
	 * um eine Klasse, die einen primitiven Datentype boxt) wird die Klasse
	 * selbst zurueck gegeben.
	 * 
	 * Beispiele:
	 * <pre>Integer.class -> Type von int
	 *Boolean.class -> Typ von boolean
	 *...</pre>
	 * 
	 * @param clazz Orginalklasse, von der, wenn moeglich der primitive Datentyp
	 *            genutzt werden soll, wenn diese einen primitiven Datentyp boxt.
	 *            Beispiele sind hier <tt>Integer, Boolean, Float.. </tt>
	 * @return Klasse, die den primitiven Datentyp der boxenden Klassen entspricht (falls
	 * diese eine boxende Klasse eines primitiven Datentypen ist, sonst die original Klasse 
	 */
	public final static Class<?> warpToPrimitiveClass(Class<?> clazz) {
		try {
			/* alle boxenden Klassen haben nach Konvention das Feld TYPE, siehe Java-API */
			Object obj = clazz.getField("TYPE").get(null);
			if (!(obj instanceof Class))
				return clazz;
			Class<?> maybePrimitiveClass = (Class<?>) obj;
			/* teste ob die Typ-Klasse wirklich die Typklasse eines primitiven Datentyps ist */
			return maybePrimitiveClass.isPrimitive() ? maybePrimitiveClass : clazz;
		} catch (IllegalArgumentException e) {
			return clazz;
		} catch (IllegalAccessException e) {
			return clazz;
		} catch (SecurityException e) {
			return clazz;
		} catch (NoSuchFieldException e) {
			return clazz;
		}
	}
}
