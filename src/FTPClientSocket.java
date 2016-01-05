import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import Utils.Constants;

public class FTPClientSocket extends Thread {

	private Socket client=null;
	private String serverName;
	private String sharedFolder;
	private int port;

	public FTPClientSocket(String serverName, int port, String sharedFolder) {
		this.serverName = serverName;
		this.port = port;
		this.sharedFolder = sharedFolder;
	}

	public void run() {
		Scanner scan = new Scanner(System.in);
		
		try {
			updateFilesList();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (true) {

			DataOutputStream out = null;
			DataInputStream in = null;
			
			try {
				// Read the command
				String str;
				do {
					str = scan.nextLine();
				} while( str.trim().length()==0 );
				System.out.println("Client: " + str);
				
				client = new Socket(serverName, port);
				System.out.println(
						"Client: Connected to " + client.getRemoteSocketAddress() + " on port " + client.getPort());
				System.out.println("Client: Connected successfully.");

				// Create Output stream
				out = createOutputStream();
				out.writeUTF(str);
				
				// Create Input stream
				in = createInputStream();


				List<String> words = Utils.Methods.parseIt(str, ":");
				String cmd = words.get(0).trim();
				String fileName = words.size() >= 2 ? words.get(1).trim() : null; // "/home/prabhakar/mozilla-cpy.pdf";

				switch (cmd) {
					case "exit":
						System.exit(0);
					case "SEARCH":
						doCommand(cmd, fileName, in);
						break;
					case "PULL":
						doCommand(cmd, fileName, client);
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					client.close();
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateFilesList() throws FileNotFoundException, IOException {
		
		ArrayList<String> lst = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(new File(Constants.INDEX_FILE)))) {
			for (String line; (line = br.readLine()) != null;) {
				List<String> wordsList = Utils.Methods.parseIt(line, ":");
				if ( ! wordsList.get(2).startsWith(this.serverName))
					lst.add(line);
			}
		}
		
		@SuppressWarnings("unused")
		List<String> newFiles = getDirContentsRecursively(sharedFolder);
		
		FileWriter fw = new FileWriter( new File(Constants.INDEX_FILE) );
		fw.write("");
		for( String s:newFiles) {
			fw.append(this.serverName+ ":");
			fw.append(this.port+ ":");
			fw.append(s);
			fw.append('\n');
		}
		fw.close();
	}

	private ArrayList<String> getDirContentsRecursively(String path) {
		File root = new File( path );
        File[] list = root.listFiles();
        ArrayList<String> res= new ArrayList<String>();

        if (list == null) return null;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
            	ArrayList<String> al = getDirContentsRecursively( f.getAbsolutePath() );
            	for(String s:al)
            		res.add(s);
            }
            else {
                res.add(f.getAbsolutePath());
            }
        }
		return res;
	}

	private DataInputStream createInputStream() throws IOException {
		InputStream inFromServer = client.getInputStream();
		DataInputStream in = new DataInputStream(inFromServer);
		return in;
	}

	private DataOutputStream createOutputStream() throws IOException {
		OutputStream outToServer = client.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToServer);
		return out;
	}

	private void doCommand(String cmd, String fileName, Object obj) throws IOException {
		switch (cmd) {
		case "PULL":
			Socket client = (Socket) obj;
			getFile(fileName, client);
			break;
		case "SEARCH":
			DataInputStream dis = (DataInputStream) obj;
			System.out.println("Client: " + dis.readUTF());
			break;
		}
	}

	private void getFile(String fileName, Socket client) throws IOException {

		byte[] mybytearray = new byte[1024];
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			is = client.getInputStream();
			fos = new FileOutputStream(fileName + "cpy");
			System.out.println("Client: Receiving...");

			int count;
			while ((count = is.read(mybytearray)) != -1)
				fos.write(mybytearray, 0, count);

			fos.flush();
			System.out.println("Client: Done.");
		} catch (Exception ex) {
			System.out.println("Client: Exception" + ex.getMessage());
		} finally {
			if (is != null)
				is.close();
			if (fos != null)
				fos.close();
		}
	}
}
