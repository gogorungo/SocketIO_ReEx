package ex04.socketio.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ServerFrame extends JFrame {
	Container container;
	JTextArea textA;
	JTextField textIn;
	JScrollPane jscroll;
	
	public ServerFrame() {
		setTitle("채팅 프로그램");
		setSize(300, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		container = getContentPane();
		textA = new JTextArea();
		jscroll = new JScrollPane(textA);
		textIn = new JTextField();
		container.setLayout(new BorderLayout());
		container.add(jscroll, BorderLayout.CENTER);
		container.add(textIn, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
}


public class ServerEx extends Thread {
	ServerFrame view = new ServerFrame();
	
	class User {
		Socket socket;
		BufferedReader br;
		PrintWriter pw;

		public User(Socket socket, BufferedReader br, PrintWriter pw) {
			this.socket = socket;
			this.br = br;
			this.pw = pw;
		}
	}

	HashMap<String, User> userMap = new HashMap<String, User>();
	ServerSocket listener = null;
	Socket socket = null;
	Scanner scan = new Scanner(System.in);

	public ServerEx() {
		try {
			// ServerSocket을 생성하고
			listener = new ServerSocket(9000); // 모니터 - URL의 제일 끝단 (End Pointer)
			view.textA.append("서버 >>> 서버 대기중 ...\n");
			// 클라이언트 접속 대기 - 접속이 되면 Socket을 반환한다. (쓰레드에서 대기 중)

			// 쓰레드 실행 순서가 중요하다.
			this.start();

			// 서버에서 임의 메세지 입력 기능
			while (true) {
				String line = scan.nextLine();
				view.textA.append("Server>>> "+line+ "\n");
				broadcast(String.format("Server>>> %s\n", line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		// 새로운 사용자가 들어오는지 체크하는 쓰레드
		while (true) {
			acceptSocket();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void acceptSocket() {
		try {
			socket = listener.accept();
			// 클라이언트와 입/출력 스트림을 연결한다.
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 메세지 받는 쓰레드 실행
			try {
				// 라인의 '\n'이다. '\n'이 없는 데이터는 readLine()로 읽을 수 없다.
				String userId = br.readLine();
				view.textA.append("서버 >>> " + userId + "님이 접속 하였습니다!\n");
				// userId가 있고 pw가 있다면 사용자를 map 추가한다.
				userMap.put(userId, new User(socket, br, pw));
				//view.textA.append("서버 >> " + userId + "님이 입장하셨습니다 \n");
				broadcast("서버 >> " + userId + "님이 입장하셨습니다 \n");

				ReceiveThread receive = new ReceiveThread(br, userId);
				receive.start();
			} catch (Exception e) {
				System.out.println("사용자 소켓 생성 예외 발생!");
				System.out.println(e.getMessage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcast(String message) {
		// userMap에 저장된 모든 사용자들에게 메세지를 전달한다.
		Iterator<String> keys = userMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			User user = userMap.get(key);
			Socket socket = user.socket;
			try {
				PrintWriter out = user.pw;
				out.println(new String(message.getBytes(), "utf-8"));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// Socket Server
		new ServerEx();
	}

	// ---- 내부 클래스 - 메세지를 받는 쓰레드 선언
	class ReceiveThread extends Thread {
		// 연결된 소켓과의 입력 스트립 객체
		BufferedReader in = null;
		String userId = "";

		public ReceiveThread(BufferedReader br, String userId) {
			this.in = br;
			this.userId = userId;
		}

		@Override
		public void run() {
			while (true && in != null) {
				try {
					String clientMessage = in.readLine();
					System.out.println(clientMessage);
					if (".quit".equalsIgnoreCase(clientMessage)) {
						view.textA.append(".quit가 입력되어서 끝낸다!\n");
						break;
					}
					else if(clientMessage.indexOf(".to") == 0) {
						sendmsg(clientMessage);
						
					}else {
						view.textA.append(userId + ">>> " + clientMessage+"\n");
						broadcast(userId + ">>> " + clientMessage+"\n");
					}
				
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		private void sendmsg(String msg) {
			PrintWriter pw = null;
			int start = msg.indexOf(" ") +1;
			int end = msg.indexOf(" ",start);
			if(end != -1) {
				String to = msg.substring(start, end);
				String realMsg = msg.substring(end+1);
				Object obj = userMap.get(to).pw;
				Object obj1 = userMap.get(userId).pw;
				if(obj != null) {
					pw = (PrintWriter) obj;
					view.textA.append(userId + "님의 귓속말 : "+realMsg+"\n");
					pw.println(userId + "님의 귓속말 : "+realMsg);
					pw.flush();
					pw = (PrintWriter) obj1;
					pw.println(userId + "님의 귓속말 : "+realMsg);
					pw.flush();
				}
				
			}else {
				view.textA.append("존재하지 않습니다\n");
				pw.println("존재하지 않습니다");
				pw.flush();
			}
			
		}
	} // end of ReceiveThread
}