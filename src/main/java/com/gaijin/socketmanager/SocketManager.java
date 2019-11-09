package com.gaijin.socketmanager;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.gaijin.socketmanager.encryption.AES;
import com.gaijin.socketmanager.encryption.XOR;
import com.gaijin.socketmanager.tcp.SocketClient;
import com.gaijin.socketmanager.tcp.SocketServer;
import com.gaijin.socketmanager.udp.DatagramManager;

public class SocketManager {
	
	private boolean isHost = false;
	private boolean isUDP = false;
	private int port = 65535;
	private String host = "localhost";
	private SocketClient client;
	private SocketServer server;
	private DatagramManager dm;
	private AES aes;
	private XOR xor;
	
	public enum EncryptionType {
		FULL, AES, XOR, NONE;
	}
	
	/**
	 * My default port is 20090 (it's my favorite, don't ask). If hosting a port, this is a port that's completely unregistered, it's also going to attempt connections to localhost please use a different overload if you want to connect to a specific host. (This one is best used for testing.)
	 * @param isHost If true, this will start a Server (whether it's a UDP or TCP socket, it doesn't totally matter).
	 */
	public SocketManager(boolean isHost, boolean isUDP) {
		this(isHost, isUDP, 20090, "localhost");
	}
	
	/**
	 * 
	 * @param isHost If true, this will start a Server (whether it's a UDP or TCP socket, it doesn't totally matter).
	 * @param host
	 */
	public SocketManager(boolean isHost, boolean isUDP, String host) {
		this(isHost, isUDP, 20090, host);
	}
	
	/**
	 * My default port is 20090 (it's my favorite, don't ask). If hosting a port, this is a port that's completely unregistered, it's also going to attempt connections to localhost please use a different overload if you want to connect to a specific host. (This one is best used for testing.)
	 * @param isHost If true, this will start a Server (whether it's a UDP or TCP socket, it doesn't totally matter).
	 * @param port
	 */
	public SocketManager(boolean isHost, boolean isUDP, int port) {
		this(isHost, isUDP, port, "localhost");
	}
	
	/**
	 * Main SocketManager overriding constructor. Specify the host you want to connect to (the default is localhost), port to connect on the host's end, and whether or not this system is also a host.
	 * @param isHost Specifies whether or not to allow hosting a server connection.
	 * @param port What port to host on or establish a connection on.
	 * @param host What is the IPv4 address of the host (mDNS and IPv6 addresses are not officially supported at this time).
	 */
	public SocketManager(boolean isHost, boolean isUDP, int port, String host) {
		this.isHost = isHost;
		this.port = port;
		this.host = host;
		this.isUDP = isUDP;
	}
	
	/**
	 * Starts the Client connection.
	 * @return True if the connection was successful, false if the connection was unsuccessful.
	 */
	public boolean startClient() {
		if(isUDP) {
			dm = new DatagramManager(port);
			dm.start();
			return true; //UDP doesn't need much
		}
		if(client == null) {
			client = new SocketClient(host,port);
			return client.connect();
		}
		return false;
	}
	
	/**
	 * Check whether or not the client socket is connected.
	 * @return True if the socket is connected to some host.
	 */
	public boolean isClientConnected() {
		if(isUDP) {
			if(dm != null)
				return true;
		}
		return client.isConnected();
	}
	
	/**
	 * Gets the socket client's host that was originally passed in to the Constructor.
	 * @return String value of host for the socket.
	 */
	public String getClientHost() {
		return host;
	}
	
	/**
	 * Sets the encryption key to use during runtime - if the type is specified to be None, the method is immediately returned.
	 * @param type Type of encryption to use (FULL, AES, XOR).
	 * @param key The encryption key to use (i.e. "A1B2C3D4E5F6")
	 */
	public void setEncryptionKey(EncryptionType type, String key) {
		if(type == EncryptionType.NONE) {
			return;
		} else if(type == EncryptionType.AES) {
			this.aes = new AES();
			this.aes.setKey(key);
		} else if(type == EncryptionType.XOR) {
			this.xor = new XOR(key);
		} else if(type == EncryptionType.FULL) {
			this.aes = new AES();
			this.aes.setKey(key);
			this.xor = new XOR(key);
		}
	}
	
	/**
	 * Sets the socket client's host address that was originally passed in to the constructor.
	 * @param host String depiction of the host address (IPv4 known working, mDNS and IPv6 untested).
	 * @param reconnect Will reconnect if the client is already connected, if this is set to true.
	 */
	public void setClientHost(String host, boolean reconnect) {
		this.host = host;
		if(reconnect && client.isConnected()) {
			client.disconnect();
			client = new SocketClient(host,port);
			client.connect();
		}
	}
	
	/**
	 * Send data over TCP/IP to a connected server/client.
	 * @param data String value of information to send over TCP/IP. (Base64 encoding done for you for TCP/IP, DO NOT USE BASE64 ON UDP.)
	 * @return Success/Failure of the data being sent.
	 * @throws IOException Only thrown if this is the server-side. There is a chance that the client isn't connected, thus IOEexception.
	 * @throws BadPaddingException Related to AES encryption key failures.
	 * @throws IllegalBlockSizeException Related to AES encryption key failures.
	 * @throws NoSuchPaddingException Related to AES encryption key failures.
	 * @throws NoSuchAlgorithmException Related to AES encryption key failures.
	 * @throws InvalidKeyException Related to AES encryption key failures.
	 */
	public boolean sendTCPData(String data) throws IOException {
		String toSend = data;
		try {
			if(this.aes != null && this.xor != null) {
				toSend = this.xor.xord(toSend);
				toSend = Base64.getEncoder().encodeToString(this.aes.encrypt(toSend));
			} else if(this.aes != null && this.xor == null) {
				toSend = Base64.getEncoder().encodeToString(this.aes.encrypt(toSend));
			} else if(this.aes == null && this.xor != null) {
				toSend = Base64.getEncoder().encodeToString(this.xor.xord(toSend).getBytes());
			} else {
				toSend = Base64.getEncoder().encodeToString(toSend.getBytes());
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
		
		if(client != null) {
			return client.sendData(toSend);
		} else {
			server.sendData(toSend);
			return true;
		}
	}
	
	/**
	 * Send UDP data over the specified host and port.
	 * @param data String information to send to receiver. (Base64 encoding done for you for TCP/IP, DO NOT USE BASE64 ON UDP.)
	 * @return Success/Failure of data being sent.
	 */
	public boolean sendUDPData(String data) {
		return sendUDPData(data, this.port);
	}
	
	/**
	 * Send UDP data over the specified host and port.
	 * @param data String information to send to receiver. (Base64 encoding done for you for TCP/IP, DO NOT USE BASE64 ON UDP.)
	 * @param destPort The port to forward information to on the receiving end (in case this varies from the hosted port).
	 * @return Success/Failure of data being sent.
	 */
	public boolean sendUDPData(String data, int destPort) {
		return sendUDPData(this.host, data, destPort);
	}
	
	/**
	 * Send UDP data over the specified host and port.
	 * @param host The IP Address of the host to push data to (in case it varies from the normal push, saves memory space even though it isn't really recommended).
	 * @param data String information to send to receiver. (Base64 encoding done for you for TCP/IP, DO NOT USE BASE64 ON UDP.)
	 * @param destPort The port to forward information to on the receiving end (in case this varies from the hosted port).
	 * @return Success/Failure of data being sent.
	 */
	public boolean sendUDPData(String host, String data, int destPort) {
		if(dm != null) {
			return dm.sendData(host, data, destPort);
		}
		return false;
	}
	
	/**
	 * Get data from active connection.
	 * @return String data from active connection. (Base64 encoding done for you for TCP/IP, DO NOT USE BASE64 ON UDP.)
	 */
	public String getData() {
		String toReturn = "";
		if(isUDP) {
			toReturn = dm.getData();
		} else {
			toReturn = isHost ? server.getData() : client.getData();
			if(toReturn != null)
				toReturn = new String(Base64.getDecoder().decode(toReturn));
		}
		return toReturn;
	}
	
	/**
	 * Stops the client's active connection.
	 * @return Returns true if the disconnect was successful, false if something went wrong.
	 */
	public boolean stopClient() {
		if(client != null) {
			return client.disconnect();
		}
		return false;
	}
	
	/**
	 * Starts the server.
	 * @return Returns false if isHost is false or if the server's creation was unsuccessful.
	 */
	public boolean startServer() {
		if(isHost && !isUDP) {
			this.server = new SocketServer(this.port);
			this.server.start();
		} else if(isHost) {
			this.dm = new DatagramManager(port);
			this.dm.start();
		}
		return isHost;
	}
	
	/**
	 * Stops the server and closes active connections.
	 * @return Returns true if the shutdown was successful, false if something went wrong or the server was never started to begin with.
	 */
	public boolean stopServer() {
		if(isHost && server != null) {
			return server.stop();
		}
		return isHost;
	}
	
	/**
	 * Starts the server and client, if isHost is used; else this will only start the client if isHost is false.
	 * @return returns true if the startup was successful (overall), returns false if startup was unsuccessful in either case.
	 */
	public boolean start() {
		if(isHost)
			return startServer() && startClient();
		return startClient();
	}
	
	/**
	 * Stops the server and client, if isHost is true; otherwise this will only stop the client if isHost is false.
	 * @return Returns success status of halting server/client.
	 */
	public boolean stop() {
		boolean server = !isHost;
		boolean client = false;
		if(isHost)
			server = stopServer();
		if(!isHost)
			client = stopClient();
		return server && client;
	}
}
