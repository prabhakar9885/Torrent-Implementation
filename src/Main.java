import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		
		int port = 7090;// Integer.parseInt(args[0]);
		
		try {
			
			// Start Server Thread
			Thread serverThread = new FTPServerSocket(port);
			serverThread.start();
			
			// Start Client Thread
			Thread clientThread = new FTPClientSocket( "127.0.0.1" ,port);
			clientThread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
