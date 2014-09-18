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
	public Vector<String> idleIP;
	public String web_result_filepath = "C:/Users/Administrator/Desktop/apache-ftpserver-1.0.6/res/home/wyq/result/";
		
	private static Object lockObject = new Object();
	public SocketServer(){
		idleIP = new Vector<String>();
		//start();
	}
	private  void updateDatabase(int taskId) {
		// TODO Auto-generated method stub
		try {
			Connection conn = createMysqlConn();
			if(conn != null){
				String sql = "update project set resultFlag = 5,resultAddr = '"+ web_result_filepath + "prj_" + taskId + ".zip' where prjId = " + taskId; 
				// TODO 需要更新resultAddr
				Statement statement = conn.createStatement();
		        statement.executeUpdate(sql);
			}else{
				return;
				
			}
			closeMysqlConn(conn);
			
	        
		} catch(SQLException e) { 
            e.printStackTrace();
            
            System.exit(-1);
        } catch(Exception e) { 
        	e.printStackTrace();
        	return;
        }
		
	}
	
	private void closeMysqlConn(Connection conn) {
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	private Connection createMysqlConn() {
		// 驱动程序名
        String driver = "com.mysql.jdbc.Driver"; 
        // URL指向要访问的数据库名scutcs
        String url = "jdbc:mysql://101.227.252.154:3306/test";
        // MySQL配置时的用户名
        String user = "root"; 
        // MySQL配置时的密码
        String password = "root"; 
        try { 
            // 加载驱动程序
            Class.forName(driver); 
            // 连续数据库
            Connection conn = DriverManager.getConnection(url, user, password); 
            if(!conn.isClosed()) {
           	 System.out.println("Succeeded connecting to the Database!"); 
           	 return conn;
            }
        } catch(ClassNotFoundException e) { 
            System.out.println("Sorry,can`t find the Driver!"); 
            e.printStackTrace(); 
            return null;
        } catch(SQLException e) { 
            e.printStackTrace();
            return null;
        } catch(Exception e) { 
        	e.printStackTrace();
        	return null;
        } 
		return null;
	}
	public void run() { 
		ServerSocket s = null;  
	    Socket socket  = null;  
	    String ipaddr = new String();
	    try {  
	        
	        //等待新请求、否则一直阻塞   
	    	System.out.println("In socketserver");
	        while(true){  
	        	s = new ServerSocket(8081);  
	            socket = s.accept();
	            System.out.println("socket:"+socket);  
	            BufferedReader br = null;  
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
	            }
	            //sleep(10000);
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
