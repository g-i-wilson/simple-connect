

import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
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
	Scanner terminalStringInput = new Scanner(System.in); // object to receive input from the console

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
			System.out.println( "*********************************" );
			System.out.println( "Server listening at port "+port );
			System.out.println( "*********************************" );
			return true;
		} catch (Exception e) {
			// if something goes wrong, we come here
			System.out.println( "Unable to create a ServerSocket object at port "+port+"!" );
			System.out.println(e);
			e.printStackTrace();
			return false;
		}
	}

	// begin taking requests
	void takeRequests () {
		while (true) {
			try {
				Socket socket = listening.accept();  // Wait for a client to connect
				String IP = socket.getInetAddress().toString();
				int port = socket.getPort();
				System.out.println( "Fulfilling a request from "+IP+":"+port);
				// get references to the input & output stream objects connected to the socket
				InputStream bytesInput = socket.getInputStream();
				OutputStream bytesOutput = socket.getOutputStream();
				// construct String reading & writing objects to work with the socket input & output streams
				Scanner scannerObject = new Scanner(bytesInput);
				PrintWriter printWriterObject	= new PrintWriter(bytesOutput, true); // autoFlush true
				this.fulfillRequest( scannerObject, printWriterObject );
				socket.close(); // close the socket
			} catch (java.net.SocketTimeoutException timeout) {
				// special Exception thrown by the timeout
				this.takeBreak();
			} catch (IOException e) {
				// catch any IOException
				System.out.println("There was an IOException, meaning I'm unable to get input or output streams from the socket!");
				System.out.println(e);
				e.printStackTrace(System.out);
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

	// Receive and send messages
	void fulfillRequest ( Scanner in, PrintWriter out ) {
			out.println("Hi, you're connected!");
			while (in.hasNextLine()) {
				String clientString = in.nextLine();
				System.out.println("Message from client: \""+clientString+"\"");
				// transmit a line back to the client
				out.println("You sent me \""+clientString+"\"!");
			}
	}

	// Take a break from serving if there are no client connections
	void takeBreak () {
		// do something else while on break
		System.out.println( "Server taking a break...");
	}

	// This main method allows this class to be run as stand-alone (e.g. for testing)
	public static void main(String[] args) {

		SimpleServer server = new SimpleServer();
		server.openPort( args.length > 0 ? Integer.parseInt(args[0]) : 8080 );
		server.takeRequests();

	}
}
