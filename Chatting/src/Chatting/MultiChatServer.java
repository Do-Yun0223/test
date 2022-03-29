package Chatting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.lang.reflect.Field;
import java.nio.charset.Charset;


//클라이언트로 부터 전송된 문자열을 받아서 다른 클라이언트에게 문자열을 보내주는 스레드
class EchoThread extends Thread{
	Socket socket;
	// 벡터 기본적으로 arraylist와 같은 기능을 하지만, 멀티 스레드의 경우에 하나의 스레드가 실행중일 때 다른 스레드는 실행하지 않는다.(하나의 스레드가 완료되야 실행됨.)
	Vector<Socket> vec; // 소켓 객체만 사용가능한 벡터
	public EchoThread(Socket socket, Vector<Socket> vec){
		this.socket = socket;
		this.vec = vec;
	}
	public void run(){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 소켓에서 입력스트림 받아옴
			BufferedWriter bw = new BufferedWriter(new FileWriter("ChattingLogs.txt",true));
			
			while(true){
				String str = br.readLine(); //클라이언트로 부터 문자열 받기
				
				if(str==null){ //상대가 접속을 끊으면 break;
					vec.remove(socket);//벡터에서 없애기
					break;
				}
				//연결된 소켓들을 통해서 다른 클라이언트에게 문자열 보내주기
				sendMsg(str);
				bw.append(str+"\n");
			}		
		}catch(IOException ie){
			System.out.println(ie.getMessage());
			
		/*}try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("MemberList1.txt",true));
			bw.write(str.getText()+"\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}finally{
			try{
				if(br!=null) br.close(); 
				socket.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}

	}
	
	//전송받은 문자열 다른 클라이언트들에게 보내주는 메서드
	public void sendMsg(String str){
		try{
			for(Socket socket:vec){ //enhanced for loop // 대입받을 변수 정의 : 배열
				// 배열의 크기를 조사할 필요x // 배열의 값 변경 불가 // 조건식에 대한 처리 필요x

				if(socket != this.socket){//for를 돌되 현재의 socket이 데이터를 보낸 클라이언트인 경우를 제외하고 나머지 socket들에게만 데이터를 보낸다.
					PrintWriter pw = 
						new PrintWriter(socket.getOutputStream(), true);
					BufferedWriter bw = new BufferedWriter(new FileWriter("ChattingLogs.txt",true));
					pw.println(str);
					bw.append(str+"\n");
					pw.flush(); // 남아있는것 없게 모두 보내기
					//단,여기서 얻어온 소켓들은 남의것들이기 때문에 여기서 닫으면 안된다.
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

public class MultiChatServer {
	public static void main(String[] args) {
		System.setProperty("file.encoding","UTF-8");
		try{
		Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null,null);
		}
		catch(Exception e){
		}
		ServerSocket server = null;
		Socket socket =null;
		//클라이언트와 연결된 소켓들을 배열처럼 저장할 벡터객체 생성
		Vector<Socket> vec = new Vector<Socket>();
		try{
			server= new ServerSocket(3000);
			while(true){
				System.out.println("접속대기중..");
				socket = server.accept();
				//클라이언트와 연결된 소켓을 벡터에 담기
				vec.add(socket);
				//스레드 구동
				new EchoThread(socket, vec).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
