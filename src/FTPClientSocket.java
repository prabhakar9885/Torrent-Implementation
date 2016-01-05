import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
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
			String cmd = "";
			client = new Socket(serverName, port);
			System.out.println("Connected to " + client.getRemoteSocketAddress() + " on port " + client.getPort());
			System.out.println("Connected successfully.");

			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("Hello from " + client.getLocalSocketAddress());

			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(inFromServer);
			System.out.println("Server says " + in.readUTF());

			OUT_OF_LOOP: if (cmd.equals("exit"))
				client.close();
			else {
				while (true) {
					String str = scan.nextLine();
					System.out.println(str);
					out.writeUTF(str);

					List<String> words = Utils.Methods.parseIt(str, ":");
					cmd = words.get(0).trim();
					String fileName = words.size() >= 2 ? words.get(1).trim() : null; //"/home/prabhakar/mozilla-cpy.pdf";

					switch (cmd) {
						case "exit":
							break OUT_OF_LOOP;
						case "SEARCH":
							doCommand(cmd, fileName, out);
							break;
						case "PULL":
							doCommand(cmd, fileName, client);
							break;
					}
				}
			}
			System.out.println("Bye...");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void doCommand(String cmd, String fileName, Object obj) throws IOException {
		switch (cmd) {
		case "PULL":
			Socket client = (Socket) obj;
			getFile(fileName, client);
			break;
		case "SEARCH":
			DataInputStream dis = (DataInputStream) obj;
			System.out.println( dis.readUTF() );
			break;
		}
	}

	
	private void getFile(String fileName, Socket client) throws IOException {

		byte[] mybytearray = new byte[1024];
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = client.getInputStream();
			fos = new FileOutputStream(fileName+"cpy");
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
