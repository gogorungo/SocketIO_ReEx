package ex03.socketio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerEx extends Thread {
	ServerSocket listener = null;
	Socket socket = null;
	BufferedReader br = null;
	BufferedWriter bw = null;
	PrintWriter pw = null;
	Scanner scan = new Scanner(System.in);

	public ServerEx() {
		try {
			// ServerSocket을 생성하고
			listener = new ServerSocket(9000); // 모니터 - URL의 제일 끝단 (End Pointer)
			System.out.println("서버 >>> 서버 대기중 ...");
			// 클라이언트 접속 대기 - 접속이 되면 Socket을 반환한다. (쓰레드에서 대기 중)

			// 쓰레드 실행 순서가 중요하다.
			this.start();

			// 메세지 받는 쓰레드 실행
			ReceiveThread receive = new ReceiveThread(br);
			receive.start();

			// 서버에서 임의 메세지 입력 기능
			while (true) {
				String line = scan.nextLine();
				pw.printf("Server>>> %s\n", line);
				pw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (br != null)
					br.close();
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
			System.out.println("Server thread ....");
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
			System.out.println("서버 >>> 클라이언트와 접속이 되었습니다~");
			// 클라이언트와 입/출력 스트림을 연결한다.
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 클라이언트의 userId를 읽어오기
			try {
				// 라인의 '\n'이다. '\n'이 없는 데이터는 readLine()로 읽을 수 없다.
				String userId = br.readLine();
				System.out.println("서버 >>> " + userId + "님이 접속 하였습니다!");

				// userId가 있고 pw가 있다면 사용자를 map 추가한다.

			} catch (Exception e) {
				System.out.println("읽어 들일 데이터가 없다!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Socket Server
		new ServerEx();
	}

	// ---- 내부 클래스 - 메세지를 받는 쓰레드 선언
	class ReceiveThread extends Thread {
		// 연결된 소켓과의 입력 스트립 객체
		BufferedReader br = null;

		public ReceiveThread(BufferedReader br) {
			this.br = br;
		}

		@Override
		public void run() {
			while (true && br != null) {
				System.out.println("ReceiveThread ...");
				try {
					String clientMessage = br.readLine();
					if (".quit".equalsIgnoreCase(clientMessage)) {
						System.out.println(".quit가 입력되어서 끝낸다!");
						break;
					}
					System.out.println(">>> " + clientMessage);
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
	} // end of ReceiveThread
}