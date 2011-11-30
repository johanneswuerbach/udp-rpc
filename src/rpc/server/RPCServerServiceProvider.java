package rpc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
		_running = true;
		_serviceProvider = serviceProvider;
		System.out.println("Starting server using port \"" + port + "\".");
	}

	@Override
	public void run() {
		while (_running) {
			RPCCall message = null;
			DatagramPacket packet = null;
			try {
				byte[] buffer = new byte[1024];
				packet = new DatagramPacket(buffer, buffer.length);
				_socket.receive(packet);
				byte[] bytes = new byte[packet.getLength()];
				for (int i = 0; i < packet.getLength(); i++) {
					bytes[i] = buffer[i];
				}
				message = RPCCall.parseFrom(bytes);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			try {
				//				for (int i=0;i<message.getParametersCount();i++){
				//					
				//				}
				ByteString result = RPCSecrets.serialize(_serviceProvider.call(
						message.getClassname(), message.getMethodname(),
						RPCSecrets.deserialize(message.getParametersList())));
				sendResult(result, packet.getAddress(), packet.getPort());
			} catch (RPCException e) {
				e.printStackTrace();
				throwExecption(e, packet.getAddress(), packet.getPort());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		_socket.close();
	}

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
