import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		int port = 7080;// Integer.parseInt(args[0]);
		try {
			Thread t = new FTPServerSocket(port);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
