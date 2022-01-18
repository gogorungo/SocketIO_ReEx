package ex01.soketio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerEx {
	ServerSocket listener = null;

	
	public ServerEx() {
		Socket socket = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		Scanner scan = new Scanner(System.in);
		PrintWriter pw = null;
		try {
			// ServerSocket을 생성하고
			listener = new ServerSocket(9999); // 모니터 - URL의 제일 끝단 (End Pointer)
			System.out.println("서버 >>> 서버 대기중...");
			//클라이언트 접속 대기 - 접속이 되면 Socket을 반환한다. 클라이언트와 연결
			socket = listener.accept();
			System.out.println("서버 >>> 클라이언트와 접속이 되었습니다");
			// 클라이언트와 입/출력 스트림을 연결한다.
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream())); 
			//클라이언트의 userId를 읽어오기
			try {
				String userId = br.readLine();
				System.out.println("서버 >>>" +userId + "님이 접속하였습니다.");
			} catch (Exception e) {
				System.out.println("읽어 들일 데이터가 없다");
			}
//			bw.write(userId);
//			bw.flush(); // 에코
//			// 종료 대기
//			scan.next();
			
			//클라이언트로 다시 전송 (에코)
			while(true) {
				// 읽은 데이터를 클라이언트한테 바로 다시 보내준다
				String line = br.readLine();
				if(".quit".equalsIgnoreCase(line)) {
					System.out.println(".quit이 입력되어서 끝낸다.");
					break;
				}
				System.out.println(">>> " + line);
				//bw.write("Server>>> " + line + "\n"); // nextLine 이므로 \n 필수
				pw.printf("Server>>> %s\n",line);
				//bw.flush();
				pw.flush();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally { // 종료시 전부 닫아줌. 만든 것의 역순
			try {
				//if(bw != null) bw.close();
				if(pw != null) pw.close();
				if(br != null) br.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// Socket Server
		new ServerEx();

	}

}
