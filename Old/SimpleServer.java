

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class SimpleServer
{
	/////////////////////////////////////
	// Object Data Structure
	/////////////////////////////////////

	ServerSocket listening; // variables and references start out null if nothing is assigned to them
	int port = 8080; // default port to use (if we don't change it)
	
	
	/////////////////////////////////////
	// Object Methods
	/////////////////////////////////////

	// Constructor
	SimpleServer () {
		System.out.println( "SimpleServer: a new instance ("+this+") has been created!" );
	}
	
	// Get port number
	int getPortNumber () {
		return port;
	}
	
	// Set port number and initialize the port
	boolean openPort ( int p ) {
		// change the port number
		this.port = p;
		// call the other function
		return this.openPort();
	}
	
	// Initialize the port
	boolean openPort () {
		try {
			// create a new server socket object
			listening = new ServerSocket( port );
			listening.setSoTimeout( 1000 ); // will timeout once every second
			System.out.println( "SimpleServer: connected to port "+port+"." );
			return true;
		} catch (Exception e) {
			// if something goes wrong, we come here
			System.out.println( "SimpleServer: error creating a ServerSocket object with port "+port+"!" );
			System.out.println(e);
			e.printStackTrace();
			return false;
		}
	}
	
	void takeRequests () {
		while (true) {
			try {
				Socket s = listening.accept();  // Wait for a client to connect
				this.fulfillRequest( s ); // fullfill the request
				s.close(); // close the socket
			} catch (java.net.SocketTimeoutException timeout) {
				// special Exception thrown by the timeout
				this.takeBreak();
			} catch (Exception e) {
				// log any other Exception
				System.out.println(e);
				e.printStackTrace(System.out);
				// try to open the port or wait one second
				if ( !this.openPort() ) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (Exception e2) {
						// log any other Exception
						System.out.println(e2);
						e2.printStackTrace(System.out);
						break;
					}
				}
			}
		}
	}
	
	// Fullfill a request from a client
	void fulfillRequest ( Socket socket ) {
		// fullfill the request
		System.out.println( "SimpleServer: fulfilling a request...");
	}
	
	// Take a break from serving if there are no client connections for 1 second
	void takeBreak () {
		// do something else while on break
		System.out.println( "SimpleServer: taking a break...");
	}

	// This main method allows this class to be run as stand-alone (e.g. for testing)
	public static void main(String[] args) {
		
		SimpleServer server = new SimpleServer();
		server.openPort( args.length > 0 ? Integer.parseInt(args[0]) : 8080 );
		server.takeRequests();
		
	}
}
