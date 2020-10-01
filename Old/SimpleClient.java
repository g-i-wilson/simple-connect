
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class SimpleClient
{
	/////////////////////////////////////
	// Object Data Structure
	/////////////////////////////////////

	Scanner stdin = new Scanner( System.in );
	
	Socket connection; // null
	Scanner receiver; // null
	PrintWriter transmitter; // null
	String server_address; // null
	int server_port = 8080; // default server_port to use (if we don't change it)
	String local_address;
	int local_port;
	
	
	/////////////////////////////////////
	// Object Methods
	/////////////////////////////////////

	// Constructor
	SimpleClient () {
		System.out.println( "SimpleClient: a new instance ("+this+") has been created!" );
	}
	
	// Set the server_address and server_port of the server
	boolean openConnection ( String a, int p ) {
		// change server_address and server_port
		this.server_address = a;
		this.server_port = p;
		// call the other function
		return this.openConnection();
	}
	
	// Connect to the server
	boolean openConnection () {
		try {
			// create a new server socket object
			this.connection = new Socket( this.server_address, this.server_port );
			this.local_address = connection.getLocalAddress().toString();
			this.local_port = connection.getLocalPort();
			System.out.println( "Socket connected!\n"+local_address+":"+local_port+" <-> "+server_address+":"+server_port );
			this.receiver = new Scanner(connection.getInputStream());
			this.transmitter = new PrintWriter(connection.getOutputStream(), true); // autoflush set to 'true'
			return true;
		} catch (Exception e) {
			// if something goes wrong, we come here
			System.out.println( "SimpleClient: error -- unable to connect to "+server_address+":"+server_port+"!" );
			System.out.println(e);
			e.printStackTrace();
			return false;
		}
	}
	
	// Wait for user data to be entered, send the data to the server, and also return it as a String.
	String transmitMessage () {
		System.out.print( "Message to server: " );
		String message = stdin.nextLine();
		transmitter.println( message );
		System.out.print( "\nSending... " );
		if (transmitter.checkError()) {
			//transmitter.clearError();
			System.out.println( "Error sending message!" );
		} else {
			System.out.println( "\b\b\b\b\b\b\b\b\b\b\bSent." );
		}
		return message;
	}
	
	// Wait for data to be received from the server, then return the data as a String
	String receiveMessage () {
		System.out.print( "Waiting..." );
		String message = receiver.nextLine();
		System.out.print( "\b\b\b\b\b\b\b\b\b\b\bMessage from server: "+message );
		return message;
	}
	
	void chat () {
		// splash banner
		System.out.println("\n*** SimpleClient Chat ***");
		
		while (true) {
			// get the server address from the user
			System.out.print("Server address: ");
			this.server_address = stdin.nextLine();
			// get the server port from the user
			System.out.print("Server port: ");
			try {
				this.server_port = Integer.parseInt(stdin.nextLine());
			} catch (Exception e) {
				System.out.println("Unable to convert that port number to an INT!\nUsing default port "+this.server_port+" instead.");
			}
			
			// try to open a connection with the server
			if (this.openConnection()) {
				String message = "";
				while (!message.toLowerCase().equals("exit")) {
					message = this.transmitMessage();
					message = this.receiveMessage();
				}
			} else {
				System.out.println("Unable to connect to server "+this.server_address+":"+this.server_port+"!\n");
			}
		}
	}
	
	// This main method allows this class to be run as stand-alone (e.g. for testing)
	public static void main(String[] args) {
		
		SimpleClient client = new SimpleClient();
		client.chat();
		
	}
}
