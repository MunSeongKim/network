package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class ChatServerProcessThread extends Thread {
	private Socket socket = null;
	private List<PrintWriter> clientBroadcastList = null;

	public ChatServerProcessThread(Socket s, List<PrintWriter> clientList) {
		this.socket = s;
		this.clientBroadcastList = clientList;
	}

	@Override
	public void run() {
		String clientNickName = null;
		InetSocketAddress remoteSockAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
		int remoteHostPort = remoteSockAddr.getPort();
		String remoteHostAddr = remoteSockAddr.getAddress().getHostAddress();
		String remoteHostName = remoteSockAddr.getHostName();
		consoleLog("Connected from " + remoteHostName + "(" + remoteHostAddr + ":" + remoteHostPort + ")");

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
					true);

			while (true) {
				String msg = br.readLine();
				if (msg == null) {
					consoleLog(clientNickName + "의 연결이 종료되었습니다.");
					break;
				}

				String[] token = msg.split(" ", 2);
				if ("SYN".equals(token[0])) {
					clientNickName = token[1];
					pw.println("SYNACK [SERVER] 채팅방에 접속하였습니다. " + clientNickName + "님 환영합니다.");
					if (clientBroadcastList.size() > 0) {
						synchronized (clientBroadcastList) {
							Iterator<PrintWriter> it = clientBroadcastList.iterator();
							while (it.hasNext()) {
								PrintWriter otherClientPW = it.next();
								otherClientPW.println("MSG [SERVER] " + clientNickName + "(이)가 접속하셨습니다.");
							}
						}
					}
					clientBroadcastList.add(pw);
					continue;
				}

				if ("FIN".equals(token[0])) {
					pw.println("FINACK [SERVER] 채팅방을 나갑니다.");
					clientBroadcastList.remove(pw);
					if (clientBroadcastList.size() > 0) {
						synchronized (clientBroadcastList) {
							Iterator<PrintWriter> it = clientBroadcastList.iterator();
							while (it.hasNext()) {
								PrintWriter otherClientPW = it.next();
								otherClientPW.println("MSG [SERVER] " + clientNickName + "(이)가 떠났습니다.");
							}
						}
					}
					break;
				}

				if ("MSG".equals(token[0])) {
					synchronized (clientBroadcastList) {
						Iterator<PrintWriter> it = clientBroadcastList.iterator();
						while (it.hasNext()) {
							PrintWriter otherClientPW = it.next();
							otherClientPW.println("MSG [" + clientNickName + "] " + token[1]);
						}
					}
				}

				if ("SET".equals(token[0])) {
					synchronized (clientBroadcastList) {
						Iterator<PrintWriter> it = clientBroadcastList.iterator();
						while (it.hasNext()) {
							PrintWriter otherClientPW = it.next();
							otherClientPW
									.println("MSG [SERVER] " + clientNickName + "의 닉네임이 " + token[1] + "(으)로 변경되었습니다.");
						}
					}
					clientNickName = token[1];
				}

				if ("GET".equals(token[0])) {
					synchronized (clientBroadcastList) {
						pw.println("MSG [SERVER] 현재 인원은 " + clientBroadcastList.size() + "명 입니다.");
					}
				}
			}

		} catch (SocketException e) {
			consoleLog(remoteHostName + "(" + remoteHostAddr + ":" + remoteHostPort + ")(이)가 종료되었습니다.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					consoleLog(remoteHostName + "(" + remoteHostAddr + ":" + remoteHostPort + ")(이)가 종료되었습니다.");
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void consoleLog(String log) {
		System.out.println("[Server#" + getId() + "] " + log);
	}

}
