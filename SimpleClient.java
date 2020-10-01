
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class SimpleClient
{
	/////////////////////////////////////
	// Object Data Structure
	/////////////////////////////////////

 	private String server_address = "";
	private int server_port = 8080; // default server_port to connect to
	private String client_address = "";
	private int client_port; // 0 by default


	/////////////////////////////////////
	// Object Methods
	/////////////////////////////////////

	// get instance variables
	String 	getServerAddress() 	{return server_address;}
	int 		getServerPort() 		{return server_port;}
	String 	getClientAddress() 	{return client_address;}
	int			getClientPort()			{return client_port;}

	// Set the server_address
	public SimpleClient setServerAddress ( String a ) {
		server_address = a;
		return this;
	}

	// Set the server_port
	public SimpleClient setServerPort ( int p ) {
		server_port = p;
		return this;
	}

	// Connect to the server
	public boolean connect () {
		try {
			// connect to the server
			Socket socket = new Socket( server_address, server_port );

			// get the address and port this computer used to connect to the server
			client_address = socket.getLocalAddress().toString();
			client_port = socket.getLocalPort();

			// create Scanner and PrintWriter objects to make it easier to talk through the connection
			Scanner receiver = new Scanner(socket.getInputStream());
			PrintWriter transmitter = new PrintWriter(socket.getOutputStream(), true); // autoflush set to 'true'

			// talk with the server
			talk(receiver, transmitter);

			// close the connection
			socket.close();

			// the connection worked!
			return true;

		} catch (Exception e) {
			// if something goes wrong, we come here
			System.out.println( "Unable to connect to "+server_address+":"+server_port+"!" );
			e.printStackTrace();

			// the connection didn't work.
			return false;
		}
	}


	// this is the method that defines the protocol of how data is exchanged
	public void talk (Scanner receiver, PrintWriter transmitter) {
		// print a message
		System.out.println( "Connected!\n"+getClientAddress()+":"+getClientPort()+" <-> "+getServerAddress()+":"+getServerPort() );

		// chat loop
		Scanner stdin = new Scanner( System.in );
		String fromServer;
		String toServer;
		while (true) {
			// get a line from the command line
			System.out.print( " > " );
			toServer = stdin.nextLine();
			// break loop if the line "exit" is entered
			if (toServer.toLowerCase().equals("exit")) break;
			// transmit the line to the server
			transmitter.println( toServer );
			// break the loop if there was an error sending the message
			if (transmitter.checkError()) {
				System.out.println( "Error sending message!" );
				break;
			}
			// wait for a line from the server
			if (receiver.hasNextLine()) {
				fromServer = receiver.nextLine();
				System.out.println( "   "+fromServer );
			}
		}
		// if we break the loop, tell the user that we're disconnecting
		System.out.print( "Disconnecting from server "+server_address+":"+server_port+".\n\n" );
	}



	// This main method allows this class to be run stand-alone (e.g. for testing)
	public static void main(String[] args) {
		// splash banner
    System.out.println( "\n*********************************" );
    System.out.println( "SimpleClient" );
    System.out.println( "*********************************\n" );
		// new instance of SimpleClient
		SimpleClient client = new SimpleClient();
		// create command line Scanner object
		Scanner stdin = new Scanner( System.in );
		// get the server address from the command line
		System.out.print("Server address: ");
		client.setServerAddress( stdin.nextLine() );
		// get the server port from the command line
		System.out.print("Server port: ");
		// try to understand the port number
		try {
			client.setServerPort( Integer.parseInt(stdin.nextLine()) );
		} catch (Exception e) {
			System.out.println("Didn't understand that port number!\nConnecting to port "+client.getServerPort()+" instead.");
		}
		// connect to the server
		client.connect();
	}


}
