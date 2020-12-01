package com.zeuschops.socketmanager.tcp;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	private int port = 65535;
	private String host = "localhost";
	private boolean caching = false;
	private Socket client;
	private PrintWriter pw;
	private BufferedReader br;
	private FileWriter fw;
	/**
	 * Alias to primary constructor, will assume localhost. NOTE: THIS INSTANCE HOST CANNOT BE CHANGED LATER IN THE APPLICATION.
	 * @param port Required: maximum value of 65535 accepted (16-bit Integer). Will be changed to 65535 if higher, and 7000 if lower than 1.
	 */
	public SocketClient(int port) {
		this("localhost", port);
	}

	/**
	 * Creates new instance of a TCP client without caching.
	 * @param host Required: IPv4 assignment - no guarantees of mDNS or IPv6 addressing being successful upon connection.
	 * @param port Requried: maximum value of 65535 accepted (16-bit Integer). WIll be changed to 65535 if higher, and 7000 if lower than 1.
	 */
	public SocketClient(String host, int port) {
		this(host, port, false);
	}

	/**
	 * Creates a new instance of a TCP Client.
	 * @param host Required: IPv4 assignment - no guarantees of mDNS or IPv6 addressing being successful upon connection.
	 * @param port Required: maximum value of 65535 accepted (16-bit Integer). Will be changed to 65535 if higher, and 7000 if lower than 1.
	 * @param caching Not mandatory, used to cache data sent from server to local device. NOTE: Currently not encrypted.
	 */
	public SocketClient(String host, int port, boolean caching) {
		this.host = host;
		this.port = port < 1 ? 7000 : (port > 65535 ? 65535 : port);
		this.caching = caching;
		if(caching) {
			try {
				fw = new FileWriter(new File("cache.log"));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Opens the connection for the socket. IMPORTANT: THIS MUST BE CALLED BEFORE SENDING OR RECEIVING DATA.
	 * @return Returns boolean to identify success or failure
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public boolean connect() {
		try {
			client = new Socket(host,port);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sends data through the socket connection to some host.
	 * @param data information to send, as a String.
	 * @return Returns success while sending data.
	 */
	public boolean sendData(String data) {
		try {
			PrintWriter pw = new PrintWriter(client.getOutputStream());
			pw.println(data);
			pw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets the data from the host, will wait until data is sent if need-be.
	 * @return String of either the data from the socket or else an empty string.
	 */
	public String getData() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String toReturn = br.readLine();
			br.close();
			return toReturn;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Disconnects the client from the host.
	 * @return Returns whether or not the disconnect was successful.
	 */
	public boolean disconnect() {
		if(client.isConnected()) {
			try {
				if(pw != null)
					pw.close();
				if(br != null)
					br.close();
				if(client != null)
					client.close();
				//Reset to null regardless, we could avoid this as a system call but whatever
				br = null;
				pw = null;
				client = null;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Checks connected status of the Socket
	 * @return Returns true if socket is connected.
	 */
	public boolean isConnected() {
		return client.isConnected();
	}
}
