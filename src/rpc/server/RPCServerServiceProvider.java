package rpc.server;

import java.net.SocketException;
import rpc.RPCServiceProvider;

/**
 * Biete einen RPC-Service auf einen gegebenen Port an; so das statischen Methoden von
 * beliebigen Klassen ueber Netzwerk mit Hilfe des <tt>RPCRemoteServiceProvider</tt> aufgerufen werden koennen.
 */
public class RPCServerServiceProvider
		implements
			Runnable
{
	/**
	 * @param serviceProvider der RPC-Service, der genutz werden soll, um die Methode aufzurufen.
	 * @param port Port, auf dem der Server den RPC Service anbietet
	 */
	public RPCServerServiceProvider(RPCServiceProvider serviceProvider, int port) throws SocketException {

	}

	@Override
	public void run() {

	}

	/**
	 * Terminiert den Server.
	 */
	public void terminate() {

	}

}
