package com.zeuschops.socketmanager.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DatagramManager {
	
	private int port = 0;
	private DatagramSocket server;
	private DatagramPacket packet;
	
	public enum ReceiverType {
		IPv4, IPv6, localhost, unknown
	}
	
	/**
	 * Creates a new DatagramManager on a port
	 * @param port Integer value between 1000 and 65535 for listening on.
	 */
	public DatagramManager(int port) {
		this.port = port > 65535 ? 65535 : (port < 1000 ? 1000 : port);
	}
	
	/**
	 * Starts the DatagramManager on the constructor's specified port. (Both the client and server are the same, so "start" will work for the name.)
	 * @return Returns true if everything went smoothly during setup.
	 */
	public boolean start() {
		try {
			server = new DatagramSocket(port);
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Sends data across the DatagramManager.
	 * @param destination Destination should be an IPv4 or IPv6 (experimental) address as a String.
	 * @param data Data to be sent across the Datagram.
	 * @param destPort Destination port of the destination host that is listening.
	 * @return
	 */
	public boolean sendData(String destination, String data, int destPort) {
		byte[] sendBuf = (data + "\r\n").getBytes();
		InetAddress receiverAddress = null;
		ReceiverType type = ReceiverType.unknown;
		
		if(destination.contains(":"))
			type = ReceiverType.IPv6;
		else if(destination.contains("."))
			type = ReceiverType.IPv4;
		else if(destination.equalsIgnoreCase("localhost"))
			type = ReceiverType.localhost;

		try {
			if(type == ReceiverType.IPv4 || type == ReceiverType.IPv6) {
				receiverAddress = InetAddress.getByName(destination);
			} else if(type == ReceiverType.localhost) {
				receiverAddress = InetAddress.getLocalHost();
			}
		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
		}
		if(receiverAddress != null) {
			packet = new DatagramPacket(sendBuf, sendBuf.length, receiverAddress, destPort);
			try {
				server.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the data from the DatagramPacket while it's listening on the port specified in the constructor for the class.
	 * @return Returns a String for the data returned over the Datagram.
	 */
	public String getData() {
		if(server == null)
			start();
		byte[] buffer = new byte[10];
		packet = new DatagramPacket(buffer, buffer.length);
		try {
			server.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer = packet.getData();
		String toReturn = new String(buffer);
		toReturn = toReturn.substring(0, toReturn.indexOf("\r\n"));
		return toReturn;
	}
	
	/**
	 * Stops the DatagramManager from listening on the port that was specified in the constructor.
	 * @return Returns true if stopped, false if not.
	 */
	public boolean stop() {
		boolean serv = server == null;
		if(server != null) {
			server.close();
		}
		return serv;
	}
}
