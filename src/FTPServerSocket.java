import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class FTPServerSocket extends Thread {
	private ServerSocket serverSocket;

	public FTPServerSocket(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}

	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF( "Connetion terminated from " + server.getLocalSocketAddress() );
				while (true) {
					String str = in.readUTF();
					System.out.println(str);
					if (str.trim().equals("exit"))
						break;
					DoCommand(str, server);
				}
				server.close();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private void DoCommand(String str, Socket server) throws IOException {
		switch(str) {
			case "PULL":
				SendFile( str, server );
				break;
			case "PUSH":
				break;
		}
	}

	private void SendFile(String str, Socket server) throws IOException {
		File mFile = new File("/home/prabhakar/mozilla.pdf");
		byte[] mybytearray = new byte[(int) mFile.length()];
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;

		try {
			fis = new FileInputStream(mFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
			os = server.getOutputStream();
			System.out.println("Sending " + mFile.getName() + "(" + mybytearray.length + " bytes)...");
			os.write(mybytearray, 0, mybytearray.length);
//			os.flush();
			System.out.println("Sent");
		} catch (Exception ex) {
			System.out.println("Exception" + ex.getMessage());
		} finally {
//			if( os!=null )	os.close();
//			if( bis!=null )	bis.close();
//			if( fis!=null )	fis.close();
		}
	}
}
