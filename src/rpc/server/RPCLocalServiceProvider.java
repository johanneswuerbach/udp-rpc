package rpc.server;

import java.io.Serializable;
import java.lang.reflect.Method;

import rpc.RPCException;
import rpc.RPCSecrets;
import rpc.RPCServiceProvider;

/**
 * <p>Eine Implementierung des RPCServiceProviders, die eine statische Methode einer
 * Klasse <b>lokal</b> ausfuehrt und deren Ergebis zurueck liefert.</p>
 * 
 * <p>Zur Implementierung wird hier das Java-Reflections-Framework verwendet, im speziellen die
 * Methoden
 * 
 * <ul>
 *  <li>{@link Class#forName(String)}</li>
 *  <li>{@link Class#getMethod(String, Class...)}</li>
 *  <li>{@link Method#invoke(Object, Object...)}</li>
 * </ul></p>
 */
public class RPCLocalServiceProvider
		extends
			RPCServiceProvider
{
	public RPCLocalServiceProvider() {
		this(true);
	}
	
	/**
	 * Bei diesem Konstruktor kann gewählt werden, ob man möchte, dass beim
	 * Suchen nach den Methoden die Primitivklassen benutzt werden sollen, anstatt
	 * die der Wrapperklassen.
	 * 
	 * @param callPrimitivesIfBoxed
	 */
	public RPCLocalServiceProvider(boolean callPrimitivesIfBoxed) {
	}

	/**
	 * <p>Bei der Implemenierung dieser Methode ist darauf zu achten, 
	 * das {@link Class#getMethod(String, Class...)} eine Liste der Type-Parameter erwartet, nicht
	 * eine Liste der Parameter. Den Typen eines Parameters kann man ueber {@link Object#getClass()}
	 * herausfinden.</p>
	 * <p>Weiterhin ist der Hinweis auf dem Uebungsblatt ueber primitive Datentypen zu beachten. Siehe dazu auch
	 * {@link RPCSecrets#warpToPrimitiveClass(Class)}</p>
	 * 
	 * @param classname Name der Klasse, in der die aufzurufende statische Methode sich befindet
	 * @param methodname Name der aufzurufenden statischen Methode
	 * @param params Liste aller Parameter mit denen die statische Methode aufzurufen ist.
	 * @return Rueckgabewert des Methodeaufrufs
	 * @throws RPCException wird geworfen, falls waernd des Aufrufes ein Versagen auftritt (
	 * die Methode kann nicht gefunden werden, die Klasse kann nicht gefunden werden, die Methode wirft eine Exception)
	 */
	public <R> R callexplicit(String classname, String methodname, Serializable[] params) throws RPCException {
		return null;

	}

}
