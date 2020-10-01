

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;


public class SimpleServer
{
	/////////////////////////////////////
	// Object Data Structure
	/////////////////////////////////////

	ServerSocket listening; // variables and references start out null if nothing is assigned to them

	String server_address = "";
	int server_port = 8080; // default port to use (if we don't change it)
	String client_address = "";
	int client_port; // 0 by default

	Scanner stdin = new Scanner(System.in); // object to receive input from the console

	/////////////////////////////////////
	// Object Methods
	/////////////////////////////////////

	// Get variables
	String 	getServerAddress()	{ return server_address; }
	int 		getServerPort () 		{ return server_port; }
	String 	getClientAddress() 	{ return client_address; }
	int			getClientPort()			{ return client_port; }

	// Initialize the port
	boolean openServerPort ( int p ) {
		server_port = p;
		try {
			// create a new server socket object
			listening = new ServerSocket( server_port );
			System.out.println( "\n*********************************" );
			System.out.println( "SimpleServer (port "+server_port+")" );
			System.out.println( "*********************************\n" );
			return true;
		} catch (Exception e) {
			// if something goes wrong, we come here
			System.out.println( "Unable to create a ServerSocket object at port "+server_port+"!" );
			System.out.println(e);
			e.printStackTrace();
			return false;
		}
	}

	// accept connections from clients
	void acceptConnections () {
		while (true) {
			try {
				// wait for a client to connect (blocks)
				Socket socket = listening.accept();

				// a client has connected!
				server_address = socket.getLocalAddress().toString();
				client_address = socket.getRemoteSocketAddress().toString();
				client_port = socket.getPort();

				// get references to the input & output stream objects connected to the socket
				InputStream bytesInput = socket.getInputStream();
				OutputStream bytesOutput = socket.getOutputStream();

				// construct String reading & writing objects to work with the socket input & output streams
				Scanner scannerObject = new Scanner(bytesInput);
				PrintWriter printWriterObject	= new PrintWriter(bytesOutput, true); // autoFlush true

				// talk with the client
				talk( scannerObject, printWriterObject );

				// close the socket
				socket.close();
				System.out.println( "Connection closed.\n" );

			} catch (Exception e) {
				// if something goes wrong we come here...
				System.out.println( "Something went wrong!" );
				e.printStackTrace(System.out);
			}
		}
	}

	// this is the method that defines the protocol of how data is exchanged
	void talk ( Scanner in, PrintWriter out ) {
		System.out.println("Connection from a client!\n"+getServerAddress()+":"+getServerPort()+" <-> "+getClientAddress() );
		while (in.hasNextLine()) {
			String fromClient = in.nextLine();
			System.out.println("   "+fromClient);
			// get a line back to the client
			System.out.print(" > ");
			String toClient = stdin.nextLine();
			if (toClient.equals("exit")) break;
			out.println(toClient);
		}
	}

	// This main method allows this class to be run stand-alone (e.g. for testing)
	public static void main(String[] args) {

		SimpleServer server = new SimpleServer();
		server.openServerPort( args.length > 0 ? Integer.parseInt(args[0]) : 8080 );
		server.acceptConnections();

	}
}
