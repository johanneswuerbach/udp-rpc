package rpc;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Schnittstelle fuer das aufrufen von Methoden ueber die Angabe von Klassename,
 * Methodename und Parametern.
 */
public abstract class RPCServiceProvider {

	/**
	 * Aufruf einer statischen Methode.
	 * 
	 * @param classname
	 *            Name der Klasse, in der die aufzurufende statische Methode
	 *            sich befindet
	 * @param methodname
	 *            Name der aufzurufenden statischen Methode
	 * @param params
	 *            Liste aller Parameter mit denen die statische Methode
	 *            aufzurufen ist.
	 * @return Rueckgabewert des Methodeaufrufs
	 * @throws RPCException
	 *             wird geworfen, falls waernd des Aufrufes ein Versagen
	 *             auftritt ( die Methode kann nicht gefunden werden, die Klasse
	 *             kann nicht gefunden werden, die Methode wirft eine Exception)
	 */

	public final <R> R call(String classname, String methodname,
			Serializable... params) throws RPCException {
		return callexplicit(classname, methodname, params);
	}

	/**
	 * Aufruf einer statischen Methode; Ähnlich, wie call, allerdings muss nach
	 * dem letzten Parameter ein "null" übergeben werden. Dies wurde eingebaut,
	 * damit man ein einzelnes Array als Parameter übergeben kann. (Bug der
	 * ...-Syntax)
	 * 
	 * @param classname
	 *            Name der Klasse, in der die aufzurufende statische Methode
	 *            sich befindet
	 * @param methodname
	 *            Name der aufzurufenden statischen Methode
	 * @param params
	 *            Liste aller Parameter mit denen die statische Methode
	 *            aufzurufen ist.
	 * @return Rueckgabewert des Methodeaufrufs
	 * @throws RPCException
	 *             wird geworfen, falls waernd des Aufrufes ein Versagen
	 *             auftritt ( die Methode kann nicht gefunden werden, die Klasse
	 *             kann nicht gefunden werden, die Methode wirft eine Exception)
	 */

	public final <R> R callsave(String classname, String methodname,
			Serializable... params) throws RPCException {
		if (params == null) {
			Serializable[] array = new Serializable[0];
			return callexplicit(classname, methodname, array);
		} else {
			if (params[params.length - 1] != null)
				throw new IllegalArgumentException(
						"terminate the parameters with null");
			return callexplicit(classname, methodname,
					Arrays.copyOfRange(params, 0, params.length - 1));
		}
	}

	/**
	 * Aufruf einer statischen Methode.
	 * 
	 * @param classname
	 *            Name der Klasse, in der die aufzurufende statische Methode
	 *            sich befindet
	 * @param methodname
	 *            Name der aufzurufenden statischen Methode
	 * @param params
	 *            Array aller Parameter mit denen die statische Methode
	 *            aufzurufen ist.
	 * @return Rueckgabewert des Methodeaufrufs
	 * @throws RPCException
	 *             wird geworfen, falls waernd des Aufrufes ein Versagen
	 *             auftritt ( die Methode kann nicht gefunden werden, die Klasse
	 *             kann nicht gefunden werden, die Methode wirft eine Exception)
	 */
	public abstract <R> R callexplicit(String classname, String methodname,
			Serializable[] params) throws RPCException;
}
