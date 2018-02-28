package chat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatWindow {

	// for UI
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	// for Networking
	private static final String SERVER_IP = "192.168.1.9";
	private static final int SERVER_PORT = 7000;
	private String nickName;
	private Socket socket = null;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private ChatClientReceviceThread receiveThread = null;

	public ChatWindow(String name) {
		frame = new Frame("[" + name + "] Chat-room");
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.nickName = name;
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				execCommand();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					execCommand();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.append("[INFO] " + nickName + "님 반갑습니다! :D\n");
		textArea.append("[INFO] !HELP를 입력하시면 사용 가능한 명령어 목록을 볼 수 있습니다.\n");
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setLocation(1000, 200);
		frame.setVisible(true);
		frame.pack();

	}

	private void execCommand() {
		String input = textField.getText();
		if ("".equals(input)) {
			return;
		}

		textField.setText("");
		textField.requestFocus();
		if (input.charAt(0) == '!') {
			String[] cmd = input.split(" ", 2);

			switch (cmd[0]) {
			case "!HELP":
				printCommand();
				break;
			case "!JOIN":
				connectServer();
				break;
			case "!SET":
				setNickName(cmd[1]);
				break;
			case "!STATUS":
				showStatus();
				break;
			case "!COUNT":
				showMemberCount();
				break;
			case "!QUIT":
				disconnectServer();
				break;
			case "!EXIT":
				System.exit(0);
				break;
			default:
				textArea.append("[INFO] 알 수 없는 명령어입니다.\n");
				break;
			}
			return ;
		}
		sendMessage(input);
	}
	
	private void showStatus(){
		if(socket == null){
			textArea.append("[INFO] 채팅방에 연결되어 있지 않습니다.\n");
			return ;
		}
		
		if(socket.isClosed()){
			textArea.append("[INFO] 채팅방이 닫혔습니다.\n");
			return ;
		}
		textArea.append("[INFO] 채팅방에 접속되어 있습니다.\n");
	}
	
	private void showMemberCount(){
		if(socket == null){
			textArea.append("[INFO] 채팅방에 연결되어 있지 않습니다.\n");
			return ;
		}
		pw.println("GET ");
	}

	private void setNickName(String name) {
		if(socket == null){
			textArea.append("[INFO] 채팅방에 연결되어 있지 않습니다.\n");
			return ;
		}
		this.nickName = name;
		pw.println("SET " + name);
		frame.setTitle("[" + nickName + "] Chat-room" );
	}

	private void printCommand() {
		textArea.append("[INFO]\t=== 명령어 목록 ===\n");
		textArea.append("\t!JOIN -> 채팅방 접속\n");
		textArea.append("\t!STATUS -> 접속 상태 확인\n");
		textArea.append("\t!QUIT -> 채팅탕 나가기\n");
		textArea.append("\t!SET [NICKNAME] -> 닉네임 재설정\n");
		textArea.append("\t!COUNT -> 채팅 인원 보기\n");
		textArea.append("\t!EXIT -> 프로그램 종료\n");
	}

	private void connectServer() {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

			pw.println("SYN " + nickName);
			
			String rcvMsg = br.readLine();
			String[] token = rcvMsg.split(" ", 2);
			
			if("SYNACK".equals(token[0])){
				textArea.append(token[1] + "\n");
			}
			
			receiveThread = new ChatClientReceviceThread();
			receiveThread.start();
			
		} catch (IOException e) {
			textArea.append("[INFO] 서버가 열려 있지 않습니다.\n");
		}
	}
	
	private void disconnectServer() {
		if(socket == null){
			textArea.append("[INFO] 채팅방에 접속하지 않았습니다.\n");
			return ;
		}
		if(socket.isClosed()){
			textArea.append("[INFO] 채팅방이 닫혔습니다. 다시 접속해주세요.\n");
			return ;
		}
		if(socket != null && !socket.isClosed()){
			try {
				pw.println("FIN ");
				while(receiveThread.isAlive()){
				}
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMessage(String input) {
		if(socket == null){
			textArea.append("[INFO] 메시지를 전송할 수 없습니다.\n");
			return ;
		}
		pw.println("MSG " + input);
//		textArea.append(">> " + input);
//		textArea.append("\n");

		textField.setText("");
		textField.requestFocus();
	}

	private class ChatClientReceviceThread extends Thread {
				
		@Override
		public void run() {
			try {
				while( true ){
					String rcvMsg = br.readLine();
					String[] token = rcvMsg.split(" ", 2);
					
					if("FINACK".equals(token[0])){
						textArea.append(token[1] + "\n");
						break;
					}
					if("MSG".equals(token[0])){
						textArea.append(token[1] + "\n");
					}
				}
			} catch (IOException e) {
				textArea.append("[INFO] 서버가 종료되었습니다.\n");
			} finally {
				try {
					if(socket != null && !socket.isClosed()){
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
