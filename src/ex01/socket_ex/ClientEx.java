package ex01.socket_ex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
		try {
			socket = new Socket(InetAddress.getLocalHost(),9999);
			System.out.println("클라이언트 >>> 서버와 연결이 되었습니다");
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("user01"+"\n");
			bw.flush();
			
			while(true) {
				String line = scan.nextLine();
				bw.write(line + "\n");
				bw.flush();
				
				String serverMessage = br.readLine();
				System.out.println(serverMessage);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new ClientEx();

	}

}
