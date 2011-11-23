import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import rpc.RPCException;
import rpc.RPCServiceProvider;
import rpc.client.RPCRemoteServiceProvider;
import rpc.server.RPCLocalServiceProvider;
import rpc.server.RPCServerServiceProvider;

public class Main {
	protected static final int port = 8000;
	
	public static void main(String[] args) throws RPCException, SocketException, UnknownHostException {
		RPCServiceProvider local = new RPCLocalServiceProvider();
		
		/* lokaler Reflection-Test.
		 * Hier wird versucht einige statische Methode lokal per Reflections aufzurufen. */
		{
			System.out.println(local.call("testpackage.Testclass", "myMethod", 1, 3, 4));
			System.out.println(local.call("testpackage.Testclass", "myMethod"));
			System.out.println(local.call("testpackage.Testclass", "myMethod", 1));
			int[] a = {12,4,1,3}; 
			System.out.println(local.callsave("testpackage.Testclass", "integerArrayTest", a, null));
		}
		
		/* RPC */		
		{
			/* RPC-Server initialisieren, dieser stellt von nun an einen RPC-Service auf dem Port <tt>port</tt>
			 * zu Verfuegung (siehe auch weiter unten). Dieser kann auch in einem anderen Programm gestartet werden */
			RPCServerServiceProvider server = new RPCServerServiceProvider(local, port);
			new Thread(server).start();			
			
			/* RPC-Remote-Service, das Frontend fuer den Nutzer des RPC-Services. Die Methodenaufrufe werden
			 * an den RPC-Server an der angegebenen IP:Port weitergeleitet, und von diesem bearbeitet.
			 * Diese Weiterleitung wird daueber realisiert, das alle Daten, die noetig sind die Methode aufzurufen, 
			 * (Klassenname, Methodenname, Parameter) ueber das Netzwerk an Server uebertragen werden. Der Server
			 * wird dann mit Hilfe des RPCLocalServiceProvider die Methode aufrufen.
			 * 
			 * Die Ergebnisse des Funktionsaufrufes, werden an den RPC-Remote-Service zurueckgeleitet
			 * und dort entsprechend behandelt.
			 * 		- Rueckgabe des Ergebnisses vom Funktions-Aufruf
			 * 		- oder Weiterleitung einer eventuell aufgetretenden Exception */
			RPCRemoteServiceProvider remote = new RPCRemoteServiceProvider(InetAddress.getByName("localhost"), port);
			
			System.out.println(remote.call("testpackage.Testclass", "myMethod", 1, 3, 4));
			System.out.println(remote.call("testpackage.Testclass", "myMethod"));
			System.out.println(remote.call("testpackage.Testclass", "myMethod", 1));
			int[] b = {12,4,1,3}; 
			System.out.println(remote.callsave("testpackage.Testclass", "integerArrayTest", b, null));
			
			server.terminate();
		}
	}
}
