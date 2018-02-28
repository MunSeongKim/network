package echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoServerReceiveThread extends Thread {
	private Socket socket = null;

	public EchoServerReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// 4. Connecting complete
		// SocketAddress < InetSocketAddress = InetAddress(IP Address) + Port
		InetSocketAddress remoteSockAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
		int remoteHostPort = remoteSockAddr.getPort();
		String remoteHostAddr = remoteSockAddr.getAddress().getHostAddress();
		consoleLog("Connected from " + remoteHostAddr + ":" + remoteHostPort);

		InputStream is = null;
		OutputStream os = null;
		try {
			// 5. Get a I/O Stream
			is = socket.getInputStream();
			os = socket.getOutputStream();

			while (true) {
				// 6. Read a data(read)
				byte[] buffer = new byte[256];
				int readByteCount = is.read(buffer); // Blocking state

				// 정상 종료
				if (readByteCount == -1) {
					consoleLog("Disconnected by client");
					break;
				}

				String data = new String(buffer, 0, readByteCount, "UTF-8");
				consoleLog("received: " + data);

				// 7. Write a data(write)

				os.write(data.getBytes("UTF-8"));
			}
		} catch (SocketException e) {
			// 상대편이 정상적으로 소켓을 닫지 않고 종료한 경우
			consoleLog("Sudden closed by client");
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

	private void consoleLog(String log) {
		System.out.println("[Server: " + getId() + "] " + log);
	}
}
