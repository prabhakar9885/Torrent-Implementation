import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import Utils.Constants;

public class FTPServerSocket extends Thread {
	private ServerSocket serverSocket;

	public FTPServerSocket(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(100000);
	}

	public void run() {
		while (true) {
			try {
				String cmd = "";
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket client = serverSocket.accept();
				System.out.println("Connected to " + client.getRemoteSocketAddress());
				
				DataInputStream in = new DataInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());

				String str = in.readUTF();
				System.out.println(str);

				List<String> words = Utils.Methods.parseIt(str, ":");
				cmd = words.get(0).trim();
				String fileName = words.size() >= 2 ? words.get(1).trim() : null;

				switch (cmd) {
				case "exit":
					break;
				case "SEARCH":
					doCommand(cmd, fileName, out);
					break;
				case "PULL":
					doCommand(cmd, fileName, client);
					break;
				}
				out.flush();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doCommand(String cmd, String fileName, Object obj) throws IOException {
		switch (cmd) {
		case "PULL":
			Socket client = (Socket) obj;
			SendFile(fileName, client);
			break;
		case "SEARCH":
			DataOutputStream dos = (DataOutputStream) obj;
			dos.writeUTF(searchFile(fileName));
			break;
		}
	}

	private String searchFile(String fileName) throws FileNotFoundException, IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(new File(Constants.INDEX_FILE)))) {
			for (String line; (line = br.readLine()) != null;) {
				List<String> wordsList = Utils.Methods.parseIt(line, ":");
				if (wordsList.get(2).contains(fileName))
					return line;
			}
		}
		return "File not found";
	}

	private void SendFile(String fileName, Socket client) throws IOException {
		File mFile = new File(fileName);
		byte[] mybytearray = new byte[(int) mFile.length()];
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;

		try {
			fis = new FileInputStream(mFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
			os = client.getOutputStream();
			System.out.println("Sending " + mFile.getName() + "(" + mybytearray.length + " bytes)...");
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
			System.out.println("Sent");
		} catch (Exception ex) {
			System.out.println("Exception" + ex.getMessage());
		} finally {
			if (os != null)
				os.close();
			if (bis != null)
				bis.close();
			if (fis != null)
				fis.close();
		}
	}
}
