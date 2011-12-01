package rpc.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import rpc.RPCException;
import rpc.RPCSecrets;
import rpc.RPCServiceProvider;
import rpc.protobuf.RPCProtocol.RPCCall;
import rpc.protobuf.RPCProtocol.RPCResult;

import com.google.protobuf.ByteString;

/**
 * Biete einen RPC-Service auf einen gegebenen Port an; so das statischen
 * Methoden von beliebigen Klassen ueber Netzwerk mit Hilfe des
 * <tt>RPCRemoteServiceProvider</tt> aufgerufen werden koennen.
 */
public class RPCServerServiceProvider implements Runnable {
	private DatagramSocket _socket;
	private boolean _running;
	private RPCServiceProvider _serviceProvider;
	private InetAddress _clientAddress;
	private int _clientPort;

	/**
	 * @param serviceProvider
	 *            der RPC-Service, der genutz werden soll, um die Methode
	 *            aufzurufen.
	 * @param port
	 *            Port, auf dem der Server den RPC Service anbietet
	 */
	public RPCServerServiceProvider(RPCServiceProvider serviceProvider, int port)
			throws SocketException {
		_socket = new DatagramSocket(port);
		_socket.setSoTimeout(500);
		_running = true;
		_serviceProvider = serviceProvider;
		System.out.println("Starting server using port \"" + port + "\".");
	}

	/**
	 * Waits for remote procedure calls, executes them an send the result back
	 * to the client.
	 */
	@Override
	public void run() {
		while (_running) {
			RPCCall remoteCall = null;
			try {
				remoteCall = receive();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			try {
				ByteString byteResult = execute(remoteCall);
				sendResult(byteResult, _clientAddress, _clientPort); //only if no exception was thrown remotely
			} catch (RPCException e) {
				throwExecption(e, _clientAddress, _clientPort);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		_socket.close();
	}

	/**
	 * Executes the remotely called procedure
	 * 
	 * @param remoteCall
	 *            the remote call
	 * @return the result as byteString
	 * @throws ClassNotFoundException
	 * @throws RPCException
	 *             if the executed method throws an exception.
	 */
	private ByteString execute(RPCCall remoteCall)
			throws ClassNotFoundException, RPCException {
		Serializable[] params = (Serializable[]) RPCSecrets
				.deserialize(remoteCall.getParametersList());
		Object result = _serviceProvider.callexplicit(
				remoteCall.getClassname(), remoteCall.getMethodname(), params);
		return RPCSecrets.serialize(result);
	}

	/**
	 * Receives a remote procedure call from a client.
	 * 
	 * @return the received call.
	 * @throws IOException
	 *             in case of socket timeout.
	 */
	private RPCCall receive() throws IOException {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		_socket.receive(packet);

		byte[] bytes = new byte[packet.getLength()];
		for (int i = 0; i < packet.getLength(); i++) {
			bytes[i] = buffer[i];
		}
		_clientAddress = packet.getAddress();
		_clientPort = packet.getPort();
		return RPCCall.parseFrom(bytes);
	}

	/**
	 * Send the result to the client.
	 * 
	 * @param result
	 *            the result
	 * @param host
	 *            the client address
	 * @param port
	 *            the port
	 */
	private void sendResult(ByteString result, InetAddress host, int port) {
		RPCResult.Builder builder = RPCResult.newBuilder();
		builder.setResult(result);
		RPCResult message = builder.build();
		byte[] bytes = message.toByteArray();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host,
				port);
		try {
			_socket.send(packet);
		} catch (IOException wmca) {
			wmca.printStackTrace();
		}
	}

	/**
	 * Send an exception to the client. Used if an exception was thrown during
	 * the execution of the remote called method.
	 * 
	 * @param e
	 *            the exeption
	 * @param host
	 *            the client address
	 * @param port
	 *            the port
	 */
	private void throwExecption(RPCException e, InetAddress host, int port) {
		RPCResult.Builder builder = RPCResult.newBuilder();
		builder.setException(RPCSecrets.serialize(e));
		RPCResult message = builder.build();
		byte[] bytes = message.toByteArray();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host,
				port);
		try {
			_socket.send(packet);
		} catch (IOException wmca) {
			wmca.printStackTrace();
		}
	}

	/**
	 * Terminiert den Server.
	 */
	public void terminate() {
		_running = false;
	}

}
