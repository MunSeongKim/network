package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPServer {
	private static final int SERVER_PORT = 5000;
	private static final String SERVER_IP = "192.168.1.9";
	
	public static void main(String[] args) throws InterruptedException {
		ServerSocket serverSocket = null;
		try {
			// 1. 서버소켓 생성
			serverSocket = new ServerSocket();

			//Time-wait 상태에서 서버 재실행이 가능하게 끔 함
			serverSocket.setReuseAddress( true );
			
			// 2. Binding => InetSocketAddress를 사용하여 바인딩
			String localhostAddr = InetAddress.getLocalHost().getHostAddress();

			serverSocket.bind( new InetSocketAddress(SERVER_IP, SERVER_PORT) );
			System.out.println("[Server] " + SERVER_IP + ":" + SERVER_PORT + " binded");
			// 3. Wait for connection request(accept)
			System.out.println("[Server] Started! Waiting... :D");
			Socket socket = serverSocket.accept(); // Blocking state

			// 4. Connecting complete
			//SocketAddress < InetSocketAddress = InetAddress(IP Address) + Port
			InetSocketAddress remoteSockAddr = (InetSocketAddress)socket.getRemoteSocketAddress();
			int remoteHostPort = remoteSockAddr.getPort();
			String remoteHostAddr = remoteSockAddr.getAddress().getHostAddress();
			System.out.println("[Server] Connected from " + remoteHostAddr + ":" + remoteHostPort);
			
			InputStream is = null;
			OutputStream os = null;
			try {
				// 5. Get a I/O Stream
				is = socket.getInputStream();
				os = socket.getOutputStream();
				
				while( true ) {
					// 6. Read a data(read)
					byte[] buffer = new byte[256];
					int readByteCount = is.read(buffer); // Blocking state
					
					// 정상 종료
					if(readByteCount == -1) {
						System.out.println("[Server] Disconnected by client");
						break;
					}
					
					String data = new String(buffer, 0, readByteCount, "UTF-8");
					System.out.println("[Server] received: " + data);
					
					// 7. Write a data(write)
					Thread.sleep(100);
					os.write(data.getBytes("UTF-8"));
				}
			} catch (SocketException e) {
				// 상대편이 정상적으로 소켓을 닫지 않고 종료한 경우
				System.out.println("[Server] Sudden closed by client");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(serverSocket != null && serverSocket.isClosed() == false) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
