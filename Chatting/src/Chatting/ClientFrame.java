package Chatting;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


class Id extends JFrame implements ActionListener{
	static JTextField tf;
	static TextField pw;
	JLabel Chat_lbl;
	JButton btn = new JButton("login");	
	JButton sign_up;
	
	WriteThread wt;	
	ClientFrame cf;
	
	public Id(){}
	public Id(WriteThread wt, ClientFrame cf) {
		super("TALK");	// title	
		this.wt = wt;	
		this.cf = cf;
		
		setLayout(null);
		
		JLabel chat = new JLabel("TALK");
		chat.setBounds(140,120,300,40);
		chat.setFont(chat.getFont().deriveFont(30.0f)); // 폰트크기 설정
		chat.setForeground(new Color(82,55,56));
		add(chat);
		
		
		tf = new JTextField("ID");
		tf.setForeground(Color.gray);
		tf.setBounds(85,210,200,40);
		add(tf);
		
		pw = new TextField("password");
		pw.setForeground(Color.gray);
		pw.setBounds(85,250,200,40);
		add(pw);
		// textfield에 나오는 문자들을 '*'로 변경
		
		btn.setBounds(85,320,200,40);
		btn.setBackground(new Color(82,55,56));
		btn.setForeground(Color.WHITE);
		add(btn);
		
		sign_up = new JButton("Sign Up");
		sign_up.setBounds(160, 355, 200, 40);
		sign_up.setContentAreaFilled(false);
		sign_up.setBorderPainted(false);
		sign_up.setForeground(Color.GRAY);
		add(sign_up);
		sign_up.addActionListener(this); // sign_up버튼이 눌려질때
		
		btn.addActionListener(this); // btn이 버튼이 눌려질때
	
		setSize(380,600);
		setVisible(true);
		
		tf.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				tf.setText("");
				tf.setForeground(Color.BLACK);
				
			}
		});
		pw.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				pw.setText("");
				pw.setForeground(Color.BLACK);
				pw.setEchoChar('*');
				
			}
		});;
	}

	
	public void actionPerformed(ActionEvent e) {
		 if(e.getSource()==btn) { //btn이 버튼이 눌려질때 
			 try {
				String s;
				String [] array;
				boolean isok = false; // 로그인이 되었는지 확인
				BufferedReader bw = new BufferedReader(new FileReader("MemberList1.txt"));
				while((s=bw.readLine())!=null) {
					array = s.split("/");
					if(array[0].equals(tf.getText()) && array[1].equals(pw.getText())){
						JOptionPane.showMessageDialog(null, "login success");
						wt.sendMsg();
						cf.isFirst = false;
						cf.setVisible(true);
						this.dispose();
						isok = true; // 로그인 성공 시 true
						break;
					}
				}
				if(!isok) { // 로그인 실패시 login failed 출력
					JOptionPane.showMessageDialog(null, "Login Failed");
				}
				bw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	 } 
		
		 if(e.getSource()==sign_up) { // sign_up 버튼 눌려지면 signFrame이 보이도록 설정
			 SignFrame sign = new SignFrame();
			 sign.setVisible(true);
		 }
		
	}
	static public String getId(){
		return tf.getText(); // tf에 있는 문자를 반환
	}
}

public class ClientFrame extends JFrame implements ActionListener{
	JTextArea txtA = new JTextArea();
	JTextField txtF = new JTextField(20);
	JButton btnTransfer = new JButton("전송");
	JButton btnExit = new JButton("닫기");
	boolean isFirst=true; // clientFrame이 열렸는지 확인
	boolean isout = false; // 누군가 나갔는지 확인
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;
	ClientFrame cf;
	
	public ClientFrame(Socket socket) {
		super("chatting"); //title
		this.socket = socket;
		wt = new WriteThread(this);
		new Id(wt, this);
		
		JScrollPane scl = new JScrollPane(txtA);
		txtA.setEditable(false);
		add("Center", scl);
		txtA.setBackground(new Color(176,208,214));
		
		setBackground(Color.gray);
		
		p1.add(txtF);
		p1.add(btnTransfer);
		p1.add(btnExit);
		add("South", p1);
		
		//메세지를 전송하는 클래스 생성.
		
		btnTransfer.addActionListener(this);
		btnExit.addActionListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(380,600);
		setVisible(false);	
		
		txtF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				String id = Id.getId();
				if(txtF.getText().equals("")){ // 공백 전송 반환 x
					return;
				}		
				if(key == KeyEvent.VK_ENTER) { // 엔터키 눌렀을때 전송
					String str = txtF.getText();
					txtA.append("["+id+"] "+ str + "\r\n");
					wt.sendMsg();
					txtF.setText("");
				}
			}
		});
	}
	
	public void actionPerformed(ActionEvent e){
		String id = Id.getId();
		if(e.getSource()==btnTransfer){//전송버튼 눌렀을 경우
			//메세지 입력없이 전송버튼만 눌렀을 경우
			if(txtF.getText().equals("")){
				return;
			}			
			txtA.append("["+id+"] "+ txtF.getText()+"\n");
			wt.sendMsg();
			txtF.setText("");
		}else{ // 닫기 버튼을 누를 경우
			this.dispose();
			isout = true;
			wt.sendMsg(); // 나갔습니다 출력
		}
		
	}
}
class SignFrame extends JFrame implements ActionListener{
	private String id,password;
	private JPanel signup_pnl;
	private TextField Username, Userid, pwd, pwd1;
	JButton save_btn, U_id;

	   public SignFrame() {

	      signup_pnl = new JPanel();
	      signup_pnl.setLayout(null);
	      
	      JLabel Signup = new JLabel("회원가입");
	      Signup.setBounds(105,20,300,40);
	      Signup.setForeground(Color.BLACK);
	      Signup.setFont(Signup.getFont().deriveFont(40.0f)); // 글씨 크기
	      add(Signup);
	      
	      JLabel NameLabel = new JLabel("이름 : ");
	      NameLabel.setBounds(85,95,150,30);
	      NameLabel.setForeground(Color.BLACK);
	      Username = new TextField();
	      Username.setBounds(85,120,150,30);
	      add(Username);
	      add(NameLabel);
	      
	      JLabel IdLabel = new JLabel("아이디 : ");
	      IdLabel.setBounds(85,155,50,20);
	      IdLabel.setForeground(Color.BLACK);
	      Userid = new TextField();
	      Userid.setBounds(85,175,150,30);
	      U_id = new JButton("중복확인");
	      U_id.setBounds(250,175,90,30);
	      U_id.addActionListener(this);
	      add(IdLabel);
	      add(Userid);
	      add(U_id);
	        
	      JLabel pwLabel = new JLabel("비밀번호 : ");
	      pwLabel.setBounds(85,210,80,20);
	      pwLabel.setForeground(Color.BLACK);
	      pwd = new TextField();
	      pwd.setBounds(85,235,150,30);
	      pwd.setEchoChar('*');
	      add(pwLabel);
	      add(pwd);
	      
	      JLabel pwLabel1 = new JLabel("비밀번호 확인 : ");
	      pwLabel1.setBounds(85,270,100,20);
	      pwLabel1.setForeground(Color.BLACK);
	      pwd1 = new TextField();
	      pwd1.setBounds(85,295,150,30);
	      pwd1.setEchoChar('*');
	      add(pwLabel1);
	      add(pwd1);
	      
	      save_btn = new JButton("회원가입");
	      save_btn.setBounds(85,360,200,40);
	      save_btn.setBackground(Color.DARK_GRAY);
	      save_btn.setForeground(Color.WHITE);
	      add(save_btn);
	      
	      add(signup_pnl);
	      setSize(380,600);
	      setTitle("sign up");
	      setVisible(false);
	      
	     
	      save_btn.addActionListener(new ActionListener() {
	         
	         @Override
	         public void actionPerformed(ActionEvent e) {
	            try {
	               String s;
	               boolean isok = false; // 중복 확인
	               boolean isnull = false;	// 빈칸 확인
	               BufferedWriter bw = new BufferedWriter(new FileWriter("MemberList1.txt",true));
	               BufferedReader br = new BufferedReader(new FileReader("MemberList1.txt"));
	               while((s = br.readLine())!=null) {
	                  String [] array =s.split("/");
	                  if(Userid.getText().equals(array[0])) {
	                     JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.");
	                     isok = true;
	                     break;
	                  }
	               }
	               br.close();
	               if(Username.getText().equals("")) {
	            	   JOptionPane.showMessageDialog(null, "이름을 입력하세요");
	            	   isnull = true;
	               }
	               if(Userid.getText().equals("")) {
            		   JOptionPane.showMessageDialog(null, "ID를 입력하세요");
            		   isnull = true;
            	   }
	               if(pwd.getText().equals("") || pwd1.getText().equals("")) {
        			   JOptionPane.showMessageDialog(null, "비밀번호를 입력하세요");
        			   isnull = true;
        		   }
	               if(!(pwd1.getText().equals(pwd.getText()))) {
    				   JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");
    				   isnull = true;
    			   }
	               if((isok==false) && (isnull==false)) {
	               bw.write(Userid.getText()+"/");
	               bw.write(pwd.getText()+"/");
	               bw.write(Username.getText()+"\r\n");
	               bw.close();
	               JOptionPane.showMessageDialog(null, "success");
	               dispose();
	               }
	            } catch (Exception e2) {
	               JOptionPane.showMessageDialog(null, "failed");
	            }
	            
	         }
	      });  
	   }
	   public void actionPerformed(ActionEvent e) {
	    	  if(e.getSource()==U_id) {
	    			  try {
	   	               String s;
	   	               BufferedReader br = new BufferedReader(new FileReader("MemberList1.txt"));
	   	               while((s = br.readLine())!=null) {
	   	                  String [] array =s.split("/");
	   	                  if(Userid.getText().equals(array[0])) {
	   	                     JOptionPane.showMessageDialog(null, "이미 존재하는 아이디입니다.");
	   	                     break;
	   	                  }
	   	               }
		                br.close();
		             }
		             catch (Exception e1) {
		                e1.printStackTrace();
		             }

	    		  }
	    	  }
	}