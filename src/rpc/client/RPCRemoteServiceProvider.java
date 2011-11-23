package rpc.client;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import rpc.RPCException;
import rpc.RPCServiceProvider;

public class RPCRemoteServiceProvider
		extends
			RPCServiceProvider
{

	public RPCRemoteServiceProvider(final InetAddress inetAddress, int port)
			throws SocketException {
	}
	
	/**
	 * Diese Methode soll alle benötigten Informationen zum Ausführen des
	 * Methodenaufrufs serialisieren, dann alles in eine RPCCall-Message packen
	 * und diese übertragen. Danach wartet sie auf eine Antwort des Servers,
	 * wertet diese aus und gibt dann entweder das Ergebnis zurück oder wirft
	 * eine Exception.
	 */
	@Override
	public <R> R callexplicit(String classname, String methodname, Serializable[] params) throws RPCException {
		return null;

	}

}
