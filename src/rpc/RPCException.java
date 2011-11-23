package rpc;

/**
 * Diese Exception wird vom RPC-Framework genutzt die Fehlerbehandlung waerende eines RPCs zu gewaehrleisten.
 */
public class RPCException extends Exception {
	private static final long serialVersionUID = 2514725871823460618L;

	public RPCException() {
		super();
	}

	public RPCException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RPCException(String arg0) {
		super(arg0);
	}

	public RPCException(Throwable arg0) {
		super(arg0);
	}

}
