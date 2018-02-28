package http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
	private static final String DOCUMENT_ROOT = "./webapp";

	private Socket socket;

	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			consoleLog("connected from " + inetSocketAddress.getAddress().getHostAddress() + ":"
					+ inetSocketAddress.getPort());

			// get IOStream
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			OutputStream os = socket.getOutputStream();

			String request = null;
			while (true) {
				String line = br.readLine();
				if (line == null || "".equals(line)) {
					break;
				}

				if (request == null) {
					request = line;
					break;
				}
			}

			consoleLog(request);

			String[] tokens = request.split(" ");

			if ("GET".equals(tokens[0])) {
				responseStaticResource(os, tokens[1], tokens[2]);
			} else { // If request is POST
				responseStatic400Error(os, tokens[1], tokens[2]);
			}

			// // 예제 응답입니다.
			// // 서버 시작과 테스트를 마친 후, 주석 처리 합니다.
			// // ---------- Header -----------
			// os.write( "HTTP/1.1 200 OK\r\n".getBytes( "UTF-8" ) );
			// os.write( "Content-Type:text/html; charset=utf-8\r\n".getBytes( "UTF-8" ) );
			// os.write( "\r\n".getBytes() );
			// // ---------- Header end -----------
			// // ---------- Body -----------
			// os.write( "<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된
			// 것입니다.</h1>".getBytes( "UTF-8" ) );
			// // ---------- Body end ----------

		} catch (Exception ex) {
			consoleLog("error:" + ex);
		} finally {
			// clean-up
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException ex) {
				consoleLog("error:" + ex);
			}
		}
	}

	private void consoleLog(String message) {
		System.out.println("[RequestHandler#" + getId() + "] " + message);
	}

	private void responseStaticResource(OutputStream os, String url, String protocol) throws IOException {
		if ("/".equals(url)) {
			url = "/index.html";
		}

		File file = new File(DOCUMENT_ROOT + url);
		if (!file.exists()) {
			response404Error(os, url, protocol);
			return;
		}

		// Using NIO package
		byte[] body = Files.readAllBytes(file.toPath());
		String mimeType = Files.probeContentType(file.toPath());
		
		// header sending
		os.write((protocol + " 200 OK\r\n").getBytes("UTF-8"));
		os.write(("Content-Type:" + mimeType + "; charset=utf-8\r\n").getBytes("UTF-8"));
		os.write("\r\n".getBytes());
		// body sending
		os.write(body);
	}
	
	private void response404Error(OutputStream os, String url, String protocol) throws IOException{
		File file = new File(DOCUMENT_ROOT + "/error/404.html");
		byte[] body = null;
		if (file.exists()) {
			body = Files.readAllBytes(file.toPath());
		}

		// header sending
		os.write((protocol + " 404 Not Found\r\n").getBytes("UTF-8"));
		if(body != null){
			String mimeType = Files.probeContentType(file.toPath());
			
			os.write(("Content-Type:" + mimeType + "; charset=utf-8\r\n").getBytes("UTF-8"));
			os.write("\r\n".getBytes());
			os.write(body);	
		}
	}
	
	private void responseStatic400Error(OutputStream os, String url, String protocol) throws IOException {
		File file = new File(DOCUMENT_ROOT + "/error/400.html");
		byte[] body = null;
		if (file.exists()) {
			body = Files.readAllBytes(file.toPath());
		}
		
		os.write((protocol + " 400 Bad Request\r\n").getBytes("UTF-8"));
		if(body != null){
			String mimeType = Files.probeContentType(file.toPath());
			
			os.write(("Content-Type:" + mimeType + "; charset=utf-8\r\n").getBytes("UTF-8"));
			os.write("\r\n".getBytes());
			os.write(body);	
		}
		
	}
}