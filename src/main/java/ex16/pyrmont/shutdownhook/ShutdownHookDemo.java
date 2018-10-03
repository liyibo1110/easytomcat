package ex16.pyrmont.shutdownhook;

import java.io.IOException;

public class ShutdownHookDemo {

	
	public void start() {
		System.out.println("Demo");
		ShutdownHook shutdownHook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownHook);
	}
	
	public static void main(String[] args) {
		ShutdownHookDemo demo = new ShutdownHookDemo();
		demo.start();
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ShutdownHook extends Thread{
		@Override
		public void run() {
			System.out.println("Shutting down");
		}
	}
}
