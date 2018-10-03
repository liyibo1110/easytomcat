package ex16.pyrmont.shutdownhook;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class MySwingApp extends JFrame {

	JButton exitButton = new JButton();
	JTextArea jTextArea1 = new JTextArea();
	String dir = System.getProperty("user.dir");
	String filename = "temp.txt";
	
	public MySwingApp() {
		this.exitButton.setText("Exit");
		this.exitButton.setBounds(304, 248, 76, 37);
		this.exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				shutdown();	//删除临时文件
				System.exit(0);
			}
		});
		this.getContentPane().setLayout(null);
		this.jTextArea1.setText("Click the Exit button to quit");
		this.jTextArea1.setBounds(new Rectangle(9, 7, 371, 235));
		this.getContentPane().add(exitButton, null);
		this.getContentPane().add(jTextArea1, null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(0, 0, 400, 350);
		this.setVisible(true);
		this.initialize();
	}
	
	private void initialize() {
		
		//关闭钩子
		Runtime.getRuntime().addShutdownHook(new MyShutdownHook());
		
		//创建临时文件
		File file = new File(dir, filename);
		System.out.println("Creating temporary file.");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void shutdown() {
		File file = new File(dir, filename);
		if(file.exists()) {
			System.out.println("Deleting temporary file.");
			file.delete();
		}
	}
	
	private class MyShutdownHook extends Thread{
		@Override
		public void run() {
			shutdown();
		}
	}
	
	public static void main(String[] args) {
		new MySwingApp();
	}
}
