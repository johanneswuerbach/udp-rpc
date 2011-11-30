package rpc.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import rpc.RPCException;
import rpc.RPCSecrets;
import rpc.RPCServiceProvider;
import rpc.protobuf.RPCProtocol.RPCCall;
import rpc.protobuf.RPCProtocol.RPCResult;

import com.google.protobuf.InvalidProtocolBufferException;

public class RPCRemoteServiceProvider extends RPCServiceProvider {
	private final InetAddress _server;
	private final int _port;
	private final DatagramSocket _socket;

	public RPCRemoteServiceProvider(final InetAddress server, final int port)
			throws SocketException {
		_server = server;
		_port = port;
		_socket = new DatagramSocket();
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
		sendCall(className, methodName, params);

		DatagramPacket packet = null;
		RPCResult message = null;
		byte[] buffer = new byte[1024];
		try {
			_socket.receive(packet);

			byte[] bytes = new byte[packet.getLength()];
			for (int i = 0; i < packet.getLength(); i++) {
				bytes[i] = buffer[i];
			}
			message = RPCResult.parseFrom(bytes);
			if (message.hasException()) {
				throw (RPCException) RPCSecrets.deserialize(message
						.getException());
			} else {
				return RPCSecrets.deserialize(message.getResult());
			}
		} catch (InvalidProtocolBufferException e) {
			throw new RPCException("can not parse result.");
		} catch (IOException e1) {
			throw new RPCException("connection error.");
		} catch (ClassNotFoundException e) {
			throw new RPCException("class not found.");
		}

	}

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
