package ex01.soketio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientEx {
	Socket socket;
	BufferedReader br;
	BufferedWriter bw;
	Scanner scan = new Scanner(System.in);

	
	public ClientEx() {
		// 서버 소켓과 연결 - Socket 생성 즉시 서버와 연결됨.
		try {
			socket = new Socket(InetAddress.getLocalHost(), 9999);
			System.out.println("클라이언트 >>> 서버와 연결이 되었습니다.");
			// 서버와 입/출력 스트림을 연결한다.
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 서버에 userId 보내기
			bw.write("user01"+"\n");
			bw.flush(); // 버퍼를 비워준다
//			// 서버에서 보낸 메시지 받기
//			String userId = br.readLine();
//			System.out.println("userId => "+userId);
//			// 종료 대기
//			scan.next();
			
			while(true) {
				// 읽은 데이터를 클라이언트한테 바로 다시 보내준다
				String line = scan.nextLine();
				bw.write(line + "\n"); // nextLine 이므로 \n 필수
				bw.flush();
				if(".quit".equalsIgnoreCase(line)) {
					System.out.println(".quit이 입력되어서 끝낸다.");
					break;
				}
				String serverMessage = br.readLine();
				System.out.println(serverMessage);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally { // 종료시 전부 닫아줌. 만든 것의 역순
			try {
				if(bw != null) bw.close();
				if(br != null) br.close();
				if(socket != null) socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static void main(String[] args) {
		new ClientEx();

	}

}
