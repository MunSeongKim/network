package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	private static final String SERVER_IP = "192.168.56.1";
	private static final int SERVER_PORT = 6000;

	public static void main(String[] args) {
		Socket socket = new Socket();
		Scanner sc = new Scanner(System.in);

		try {
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			while (true) {
				System.out.print(">> ");
				String msg = sc.nextLine();

				if ("exit".equals(msg)) {
					break;
				}

				pw.println(msg);

				String echoMsg = br.readLine();
				if (echoMsg == null) {
					System.out.println("[Client] Disconnected by server");
					break;
				}
				System.out.println("<< " + echoMsg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sc.close();
		}
	}
}
