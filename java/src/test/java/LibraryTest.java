import org.junit.Test;

import com.zeuschops.socketmanager.SocketManager;

import static org.junit.Assert.*;

import java.io.IOException;

public class LibraryTest {
    @Test public void testSomeLibraryMethod() {
        String received = "";
        String testIP = "localhost";
        
        try {
    		// TCP work:
    		Thread serverThread = new Thread(new Runnable() {
    			public void run() {
    				//Start server
    				SocketManager ss = new SocketManager(true, false, 20090, testIP);
    				ss.startServer();
    				
    				//Give time for the client to connect
    				try {
    					Thread.sleep(3000);
    				} catch(Exception e) {
    					e.printStackTrace();
    				}
    				
    				//After the client should've connected, send data through the serversocket
    				try {
    					ss.sendTCPData("hi");
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		});
    		serverThread.start();
    		SocketManager sma = new SocketManager(false, false, 20090, testIP);
    		assertTrue(sma.startClient()); //Validate that the connection was successful
    		received = sma.getData();
    		assertTrue(received.equals("hi")); //Check if data transaction was successful
    		
    		//UDP Work:
    		serverThread = new Thread(new Runnable() {
    			//Server Thread start
    			public void run() {
    				SocketManager smb = new SocketManager(true, true, 20100);
    				assertTrue(smb.startServer()); //This will result in the same action either way, so always true
    				try {
    					Thread.sleep(3000);
    				} catch(Exception e) {
    					e.printStackTrace();
    				}
    				smb.sendUDPData(testIP, "hi", 20101);
    			}
    		});
    		serverThread.start();
    		//Main thread manager
    		SocketManager smc = new SocketManager(false, true, 20101);
    		smc.startClient(); //Should return true if the connection was successful anyways
    		received = smc.getData();
    		assertTrue(received.equals("hi")); //Check if data transaction was successful
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
}