package com.zeuschops.socketmanager.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer implements Runnable {

	private int port = 65535;
	private Thread serverThread;
	private static ArrayList<Thread> clientThreads = new ArrayList<Thread>();
	private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
	private boolean active = true;
	private ServerSocket ss;

	/**
	 * Creates a TCP/IP Server for networking.
	 * @param port Port to run the server on.
	 */
	public SocketServer(int port) {
		this.port = port;
	}

	/**
	 * Start the ServerSocket in either single client or multi-client mode.
	 */
	public void start() {
		this.serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					ss = new ServerSocket(port);
					while(active) {
						clientSockets.add(ss.accept());
						Thread clientThread = new Thread(new SocketServer(port));
						clientThreads.add(clientThread);
						clientThread.start();
					}
					ss.close();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		serverThread.start();
	}
	
	/**
	 * Threaded client task, class-specific.
	 */
	public void run() {
		Socket client = clientSockets.get(clientSockets.size()-1);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String toSend = "";
			while(client.isConnected()) {
				toSend = br.readLine();
				for(Socket s : clientSockets) {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println(toSend);
					pw.close();
				}
			}
		} catch(IOException ioe) {
			clientSockets.remove(clientSockets.indexOf(client));
		}
	}

	/**
	 * Sends data through the server to the connected client(s).
	 * @param data byte data to send.
	 * @throws IOException If there's an error sending data, an error may be thrown.
	 */
	public void sendData(String data) throws IOException {
		if(clientSockets.size() > 0) {
			for(Socket s : clientSockets) {
				if(s.getPort() == this.port) {
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					pw.println(data);
				}
			}
		}
	}

	/**
	 * Gets the data from the InputStream of the 
	 * @return String data from the client with information
	 */
	public String getData() {
		if(clientSockets.size() > 0) {
			SocketClient sc = new SocketClient(this.port);
			String toReturn = "";
			if(sc.connect()) {
				toReturn = sc.getData();
				sc.disconnect();
			}
			return toReturn;
			
			//Looking into not creating a new Socket to listen to what's going through the Server - memory reduction.
			/*for(Socket s : clientSockets) {
				if(s.getPort() == this.port) {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
						return br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}*/
		}
		return null;
	}

	/**
	 * Stops listening for new clients, and closes down any active connections.
	 * @return True if stop() was successful, false if there was an exception.
	 */
	public boolean stop() {
		active = false;
		try {
			serverThread.wait(3000);
			int length = clientSockets.size();
			for(int i = 0; i < length; i++) {
				clientSockets.get(0).close();
				clientSockets.remove(0);
			}
			return true;
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
