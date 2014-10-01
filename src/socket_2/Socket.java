package socket_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Socket {
	public static void main(String[] args){
		Launch mLaunch = new Launch();
		mLaunch.server_launch();
	}
}

class Launch {
	ServerSocket mServerSocket;
	ArrayList<String> mMessage = new ArrayList<String>();
	ArrayList<String> mClient = new ArrayList<String>();
	
	public Launch(){}
	public void server_launch(){
		try {
			mServerSocket = new ServerSocket(8080);
			System.out.println("Server has been enabled...");
			while(true){
				java.net.Socket mSocket = mServerSocket.accept();
				if(!mClient.contains(""+mSocket.getInetAddress().toString().replace("/", "")))mClient.add(mSocket.getInetAddress().toString().replace("/", ""));
				System.out.println(""+mSocket.getInetAddress()+" is online");
				Client_in client = new Client_in(mSocket);
				client.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class Client_in extends Thread{
		java.net.Socket mSocket = new java.net.Socket();
		
		public Client_in(java.net.Socket mSocket){
			this.mSocket=mSocket;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub	
			try {
				final BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				final PrintWriter mPrintWriter = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream()),true);
				/** receive message **/
				Thread RCV_Message = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(true){
							try {
								String rcv_message = mBufferedReader.readLine();
								String[] message_seperate = rcv_message.split(";");
								if(message_seperate[2].equals("byebye")&&message_seperate[0].equals("192.168.2.131")){
									mBufferedReader.close();
									mPrintWriter.close();
									mSocket.close();
									mClient.remove((String)mSocket.getInetAddress().toString().replace("/", ""));
									System.out.println(mSocket.getInetAddress().toString().replace("/", "")+" has disconnected");
									break;
								}
								if(mClient.contains((String)message_seperate[0])){
									System.out.println("succeed");
									mMessage.add(rcv_message);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}});
				
				/****/
				
				while(true){
					if((!RCV_Message.isAlive())&&mSocket.isConnected())RCV_Message.start();
					if(!mMessage.isEmpty()){
						String[] message_seperate = mMessage.get(0).split(";");
						if(message_seperate[0].equals(mSocket.getInetAddress().toString().replace("/", ""))==true){
							mPrintWriter.println(message_seperate[2]+" sent from "+message_seperate[1]);
							mMessage.remove(0);
						}
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
