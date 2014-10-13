import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class SocketServer extends Thread {
	public static Vector<String> idleIP;
		
	public static Object lockObject = new Object();
	public SocketServer(){
		idleIP = new Vector<String>();
		start();
	}

	public void run() { 
		ServerSocket s = null;  
	    Socket socket  = null;  
	    String ipaddr = new String();
	    try {  
	        
	        //等待新请求、否则一直阻塞   
	    	System.out.println("In socketserver");
	    	s = new ServerSocket(8081);  
	        while(true){  
	        	
	            new ServeOneJabbr(s); 
	           /* BufferedReader br = null;  
	            PrintWriter pw = null;
	            String str = new String();
	            try {  
	                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
	                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);  
	                str = br.readLine();  
	                System.out.println(str);
	                if(str.equals("END")){
	                    br.close();  
	                    pw.close();  
	                    socket.close();  
	                    break;  
	                }else{
	                	ipaddr = str;
	                }
	                System.out.println("Client Socket Message:"+str);  
	                
	                pw.flush();  
	                
	                
	            } catch (Exception e) {  
	            	try {  
	                    br.close();  
	                    pw.close();  
	                    socket.close();  
	                } catch (IOException e1) {  
	                    // TODO Auto-generated catch block   
	                    e1.printStackTrace();  
	                    System.exit(-1);
	                }  
	            }
	            
	            synchronized(lockObject){
	            	
	            	idleIP.add(ipaddr);
	            	int taskId = MainController.ipMatchTask.get(ipaddr);
	            	MainController.ipMatchTask.remove(ipaddr);
	            	updateDatabase(taskId);
	            }*/
	            
	            
	            
	        }  
	    } catch (Exception e) {  
	        try {  
	            s.close();  
	        } catch (IOException e1) {  
	            // TODO Auto-generated catch block   
	            e1.printStackTrace();  
	        }  
	    }
	}
}
