package Chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
// 키보드로 전송문자열 입력받아 서버로 전송하는 스레드
class WriteThread{
	Socket socket;
	ClientFrame cf;
	String str;
	String id;
	public WriteThread(ClientFrame cf) {
		this.cf = cf;
		this.socket= cf.socket;
	}
	public void sendMsg() {
		//키보드로부터 읽어오기 위한 스트림객체 생성
		BufferedReader br=
		new BufferedReader(new InputStreamReader(System.in));
		PrintWriter pw = null;
		try{
			//서버로 문자열 전송하기 위한 스트림객체 생성
			pw=new PrintWriter(socket.getOutputStream(),true);
			//첫번째 데이터는 id 이다. 상대방에게 id와 함께 내 IP를 전송한다.
			if(cf.isFirst==true){
				InetAddress iaddr=socket.getLocalAddress();	//소켓의 로컬 주소 받아옴
				String ip = iaddr.getHostAddress();	//ip주소 받아오기
				getId();
				System.out.println("ip:"+ip+"id:"+id);
				str = "["+id+"] 님 로그인 ("+ip+")"; 
			}
			else if(cf.isout==true){ // 누군가의 채팅창이 닫혔을 경우
				str = "["+id+"]님이 나가셨습니다."; // 누가 나갔다가고 출력
			}
			else {
				str = "["+id+"] "+cf.txtF.getText();
			}
			
			//입력받은 문자열 서버로 보내기
			pw.println(str);
		
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br!=null) br.close();
				//if(pw!=null) pw.close();
				//if(socket!=null) socket.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}	
	public void getId(){ 	
		id = Id.getId(); // id받아옴
	}
}
//서버가 보내온 문자열을 전송받는 스레드
class ReadThread extends Thread{
	Socket socket;
	ClientFrame cf;
	public ReadThread(Socket socket, ClientFrame cf) {
		this.cf = cf;
		this.socket=socket;
	}
	public void run() {
		BufferedReader br=null;
		try{
			//서버로부터 전송된 문자열 읽어오기 위한 스트림객체 생성
			br=new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while(true){
				//소켓으로부터 문자열 읽어옴
				String str=br.readLine();
				if(str==null){
					System.out.println("접속이 끊겼음");
					break;
				}
				//전송받은 문자열 화면에 출력
				//System.out.println("[server] " + str);
				cf.txtA.append(str+"\n");
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				br.close();
				socket.close();
			}catch(IOException ie){}
		}
	}
}
public class MultiChatClient {
	public static void main(String[] args) {
		System.setProperty("file.encoding","UTF-8");
		try{
		Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null,null);
		}
		catch(Exception e){
		}
		Socket socket=null;
		ClientFrame cf;
		try{
			socket=new Socket("220.95.118.174",3000);
			
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			System.out.println("연결성공!");
			cf = new ClientFrame(socket);
			new ReadThread(socket, cf).start();
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
