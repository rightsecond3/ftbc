package exe;

import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import exe.util.Path;

public class FileServer extends JFrame implements Runnable {
	private static final long serialVersionUID = -4628453340118366498L;
	ServerSocket fsSocket = null;
	Socket fcSocket = null;
	FileServerThread fst = null;
	JTextArea jta_log = new JTextArea();
	JScrollPane jsp_log = new JScrollPane(jta_log);
	ServerMapping serverMapping = null;
	boolean isStop = false;
	
	public FileServer(ServerMapping serverMapping) {
		this.initDisplay();
		this.serverMapping = serverMapping;
		new Thread(this).start();
	}
	
	public void initDisplay() {
		jta_log.setEditable(false);
		jta_log.getCaret().setSelectionVisible(true);
		jta_log.setCaretColor(Color.white);
		jta_log.getCaret().setBlinkRate(300);
		jta_log.getCaret().setVisible(true);
		jta_log.setForeground(Color.white);
		jta_log.setBackground(Color.black);
		jta_log.append("Microsoft Windows [Version 10.0.17763.615]"+"\n");
		jta_log.append("(c) 2018 Microsoft Corporation. All rights reserved."+"\n");
        jta_log.append("\n");
        jta_log.append("C:\\Server>"+ "\n");
		this.add(jsp_log);
		this.setTitle("파일 서버 측 로그 출력창");
		this.setSize(500, 500);
		this.setLocation(200, 0);
		this.setVisible(true);
	}

	
	@Override
	public void run() {
		try {
			fsSocket = new ServerSocket(Path.FILE_SERVER_PORT);
			jta_log.setCaretPosition(jta_log.getDocument().getLength());
			while (!isStop) {
				jta_log.append("파일서버의 응답 대기중..\n");
				fcSocket = fsSocket.accept();
				fst = new FileServerThread(this);
				fst.start();
				jta_log.append("파일 스레드 추가 : "+fcSocket.toString() + "\n");
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
