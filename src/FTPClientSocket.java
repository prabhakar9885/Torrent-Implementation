import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class FTPClientSocket extends Thread {

	private Socket client;
	private String serverName;
	private int port;

	public FTPClientSocket(String serverName, int port) {
		this.serverName = serverName;
		this.port = port;
	}

	public void run() {
		Scanner scan = new Scanner(System.in);
		try {
			System.out.println("Connecting to " + client.getRemoteSocketAddress() + " on port " + client.getPort());
			client = new Socket(serverName, port);
			System.out.println("Connected successfully.");

			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("Hello from " + client.getLocalSocketAddress());

			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println("Server says " + in.readUTF());

			while (true) {
				String str = scan.nextLine();
				out.writeUTF(str);
				if (str.trim().equals("exit"))
					break;
				doCommand(str, client);
			}
			System.out.println("Bye...");

			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doCommand(String str, Socket client) throws IOException {
		switch (str) {
		case "PULL":
			getFile(str, client);
			break;
		case "SEARH":
			break;
		}
	}

	private void getFile(String str, Socket client) throws IOException {

		byte[] mybytearray = new byte[1024];
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = client.getInputStream();
			fos = new FileOutputStream("/home/prabhakar/mozilla-cpy.pdf");
			System.out.println("Receiving...");

			int count;
			while ((count = is.read(mybytearray)) != -1)
				fos.write(mybytearray, 0, count);

			System.out.println("Done.");
		} catch (Exception ex) {
			System.out.println("Exception" + ex.getMessage());
		} finally {
			if (is != null)
				is.close();
			if (fos != null)
				fos.close();
		}
	}
}
