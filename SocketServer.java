import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class SocketServer extends Thread {
	public Vector<String> idleIP;
	private static Object lockObject = new Object();
	public SocketServer(){
		idleIP = new Vector<String>();
		//start();
	}
	
	public void run() { 
		ServerSocket s = null;  
	    Socket socket  = null;  
	    try {  
	        s = new ServerSocket(8081);  
	        //等待新请求、否则一直阻塞   
	        while(true){  
	            socket = s.accept();
	            System.out.println("socket:"+socket);  
	            ServeOneJabbr serveJabbr = new ServeOneJabbr(socket);
	            
	            synchronized(lockObject){
	            	String str = serveJabbr.getMessage();
	            	idleIP.add(str);
	            	MainController.ipMatchTask.remove(str);
	            }
	            sleep(10000);
	        }  
	    } catch (Exception e) {  
	        try {  
	            socket.close();  
	        } catch (IOException e1) {  
	            // TODO Auto-generated catch block   
	            e1.printStackTrace();  
	        }  
	    }finally{  
	        try {  
	            s.close();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block   
	            e.printStackTrace();  
	        }  
	    }  
	}
}
