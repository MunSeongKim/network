package echo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	private static final int SERVER_PORT = 6000;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			// 1. 서버소켓 생성
			serverSocket = new ServerSocket();

			// 2. Binding => InetSocketAddress를 사용하여 바인딩
			String localhostAddr = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind(new InetSocketAddress(localhostAddr, SERVER_PORT));
			consoleLog(localhostAddr + ":" + SERVER_PORT + " binded");
			
			while( true ) {
				// 3. Wait for connection request(accept)
				Socket socket = serverSocket.accept(); // Blocking state
	
				if(socket != null) {
					new EchoServerReceiveThread(socket).start();
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
	
	private static void consoleLog(String log){
		System.out.println("[Server: " + Thread.currentThread().getId() + "] " + log);
	}
}
