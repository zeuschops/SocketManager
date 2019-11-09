package com.gaijin.socketmanager.encryption;

public class XOR {
	
	private String xord = "";
	
	/**
	 * Constructor for xor-ing data before sending it.
	 * @param xord String to xor against, it's best to randomly generate this only once.
	 */
	public XOR(String xord) {
		this.xord = xord;
	}
	
	/**
	 * Method for actually xor-ing data, use this method a second time to reverse the xor.
	 * @param toXOR Data to XOR against.
	 * @return xor'd data returned as a String. (Returns an empty string if toXOR is empty.)
	 */
	public String xord(String toXOR) {
		String toReturn = "";
		for(int i = 0; i < toXOR.length(); i++) {
			toReturn += (char) (toXOR.charAt(i) ^ xord.charAt(i % xord.length()));
		}
		return toReturn;
	}
}
