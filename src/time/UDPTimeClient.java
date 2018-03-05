package time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPTimeClient {
	private static final String SERVER_IP = "192.168.56.1";
	private static final int SERVER_PORT = 5000;
	private static final int BUFFER_SIZE = 1024;

	public static void main(String[] args) {
		DatagramSocket socket = null;
		Scanner sc = null;
		
		try {
			// 0. 키보드 연결
			sc = new Scanner(System.in);
			
			// 1. 소켓 생성
			socket = new DatagramSocket();

			while (true) {
				// 2. 사용자 입력
				System.out.print(">> ");
				String message = sc.nextLine();
								
				if("quit".equals(message)){
					break;
				}
				
				// 3. 전송 패킷 생성
				byte[] sendData = message.getBytes("UTF-8");
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						new InetSocketAddress(SERVER_IP, SERVER_PORT));
				
				// 4. 전송
				socket.send(sendPacket);

				// 5. 메세지 수신
				DatagramPacket receivePacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);

				socket.receive(receivePacket); // Blocking
				// 6. 수신
				// new String(byte[], offset, length, encoding)
				message = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
				System.out.println("<< " + message);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
	}
}
