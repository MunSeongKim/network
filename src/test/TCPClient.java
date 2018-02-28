package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
	private static final String SERVER_IP = "192.168.56.1";
	private static final int SERVER_PORT = 5000;

	public static void main(String[] args) {
		Socket socket = null;

		try {
			// 1. 소켓 생성
			socket = new Socket();

			// 2. 서버 연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			// 3. Get a I/O Stream
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			// 4. Read/Write
			String data = "hello";
			os.write(data.getBytes("UTF-8"));

			byte[] buffer = new byte[256];
			int readByteCount = is.read(buffer);
			if (readByteCount == -1) {
				System.out.println("[Client] Disconnected by server");
				return;
			}

			data = new String(buffer, 0, readByteCount, "UTF-8");
			System.out.println("[Client] received: " + data);

		} catch (ConnectException e) {
			System.out.println("[Clinet] Not Connected");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
