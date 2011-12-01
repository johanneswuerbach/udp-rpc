package rpc.client;

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

import com.google.protobuf.InvalidProtocolBufferException;

public class RPCRemoteServiceProvider extends RPCServiceProvider {
	private static final int MAX_ATTEMPTS = 5;
	private static final int TIMEOUT = 2000;
	private final InetAddress _server;
	private final int _port;
	private final DatagramSocket _socket;

	public RPCRemoteServiceProvider(final InetAddress server, final int port)
			throws SocketException {
		_server = server;
		_port = port;
		_socket = new DatagramSocket();
		_socket.setSoTimeout(TIMEOUT);
	}

	/**
	 * Diese Methode soll alle benötigten Informationen zum Ausführen des
	 * Methodenaufrufs serialisieren, dann alles in eine RPCCall-Message packen
	 * und diese übertragen. Danach wartet sie auf eine Antwort des Servers,
	 * wertet diese aus und gibt dann entweder das Ergebnis zurück oder wirft
	 * eine Exception.
	 */
	@Override
	public <R> R callexplicit(String className, String methodName,
			Serializable[] params) throws RPCException {
		for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
			sendCall(className, methodName, params);
			try {
				return receiveResult();
			} catch (SocketTimeoutException e) {
				continue;
			}
		}
		throw new RPCException("Server not responding after " + MAX_ATTEMPTS
				+ " attempts.");
	}

	/**
	 * Receives the result from the server and extract the return value
	 * 
	 * @return the return value of the remote procedure call.
	 * @throws RPCException
	 *             if something goes wrong. Contains the exception thrown by the
	 *             remote method.
	 * @throws SocketTimeoutException
	 *             if the server is not responding.
	 */
	private <R> R receiveResult() throws RPCException, SocketTimeoutException {
		DatagramPacket packet = null;
		RPCResult result = null;
		byte[] buffer = new byte[1024];
		try {
			packet = new DatagramPacket(buffer, buffer.length);
			_socket.receive(packet); //wait for the result

			byte[] bytes = new byte[packet.getLength()];
			for (int i = 0; i < packet.getLength(); i++) {
				bytes[i] = buffer[i];
			}
			result = RPCResult.parseFrom(bytes);
			if (result.hasException()) {
				throw (RPCException) RPCSecrets.deserialize(result
						.getException());
			} else {
				return RPCSecrets.deserialize(result.getResult());
			}
		} catch (InvalidProtocolBufferException e) {
			throw new RPCException("can not parse result.");
		} catch (SocketTimeoutException e) {
			throw e; //Aufgabenteil e)
		} catch (IOException e1) {
			throw new RPCException("connection error.");
		} catch (ClassNotFoundException e) {
			throw new RPCException("class not found.");
		}
	}

	/**
	 * Sends a remote procedure call to the server.
	 * 
	 * @param className
	 *            the class name
	 * @param methodName
	 *            the procedure name
	 * @param params
	 *            the parameters
	 */
	private void sendCall(String className, String methodName,
			Serializable[] params) {
		RPCCall.Builder builder = RPCCall.newBuilder();
		builder.setClassname(className);
		builder.setMethodname(methodName);
		for (int i = 0; i < params.length; i++) {
			builder.addParameters(RPCSecrets.serialize(params[i]));
		}

		RPCCall message = builder.build();
		byte[] bytes = message.toByteArray();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
				_server, _port);
		try {
			_socket.send(packet);
		} catch (IOException wmca) {
			wmca.printStackTrace();
		}
	}
}
