package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int SERVER_PORT = 7000;
	private static final String SERVER_IP = "192.168.1.9";

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<PrintWriter> clientBroadcastList = new ArrayList<PrintWriter>();

		try {
			serverSocket = new ServerSocket();

			String localhostAddr = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			consoleLog(SERVER_IP + ":" + SERVER_PORT + " binded");

			while (true) {
				Socket socket = serverSocket.accept();

				if (socket != null) {
					new ChatServerProcessThread(socket, clientBroadcastList).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void consoleLog(String log) {
		System.out.println("[Server#" + Thread.currentThread().getId() + "] " + log);
	}
}
