package ex04.socketio.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

// 귓속말 기능 구현
// .to user01 hello user01
// 접속 종료 - 서버와 연결 끊어진다
// .quit

public class ClientEx {
	ServerFrame view = new ServerFrame();
	Socket socket;
	BufferedReader br;
	BufferedWriter bw;
	Scanner scan = new Scanner(System.in);
	static String[] fieldArgs;
	String line;
	String userId;

	public ClientEx() {
		try {
			// 서버 소켓과 연결 - Socket생성 즉시 서버와 연결 됨.
			socket = new Socket(InetAddress.getLocalHost(), 9000);
			System.out.println("클라이언트 >>> 서버와 연결되었다.");
			// 서버와 입/출력 스트림을 연결한다.
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 서버에 userId 보내기
			userId = "user" + (int)(Math.random()*1000);
			if(fieldArgs != null && fieldArgs.length != 0) {
				userId = fieldArgs[0];
			}
			bw.write(userId + "\n");
			bw.flush(); // 버퍼를 비워준다.
		
			// ReceiveThread 실행
			ReceiveThread receive = new ReceiveThread(br);
			receive.start();
			startEvent();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startEvent() {
		view.textIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					inputMessage(view.textIn.getText());
					view.textIn.setText("");
					view.textIn.requestFocus();
				}
			}
		});
	}
	
	private void inputMessage(String text) {
		try {
			bw.write(text+"\n");
			bw.flush();
			if (".quit".equalsIgnoreCase(text)) {
				view.textA.append(".quit가 입력되어서 종료!\n");
				System.exit(0);	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		fieldArgs = args;
		new ClientEx();
	}

	// 메세지를 받는 쓰레드 선언
	class ReceiveThread extends Thread {
		// 연결된 소켓과의 입력 스트립 객체
		BufferedReader br = null;

		public ReceiveThread(BufferedReader br) {
			this.br = br;
		}

		@Override
		public void run() {
			while (true) {
				try {
					String serverMessage = br.readLine();
					view.textA.append(serverMessage+"\n");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				} catch (SocketException e) {
					view.textA.append("클라이언트>> 서버와 연결이 끊어졌습니다.");
					try {
						if(socket != null) socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0); // 강제 종료
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			} // end of while
		}
	} // end of ReceiveThread
}