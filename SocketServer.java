import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class SocketServer extends Thread {
	public Vector<String> idleIP;
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
				String sql = "update project set resultFlag = 5 where prjId = " + taskId; 
				// TODO ��Ҫ����resultAddr
				Statement statement = conn.createStatement();
		        statement.executeUpdate(sql);
			}else{
				closeMysqlConn(conn);
				System.exit(-1);
				
			}
			closeMysqlConn(conn);
			
	        
		} catch(SQLException e) { 
            e.printStackTrace();
            
            System.exit(-1);
        } catch(Exception e) { 
        	e.printStackTrace();
        	System.exit(-1);
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
		// ����������
        String driver = "com.mysql.jdbc.Driver"; 
        // URLָ��Ҫ���ʵ����ݿ���scutcs
        String url = "jdbc:mysql://101.227.252.154:3306/test";
        // MySQL����ʱ���û���
        String user = "root"; 
        // MySQL����ʱ������
        String password = "root"; 
        try { 
            // ������������
            Class.forName(driver); 
            // �������ݿ�
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
	    try {  
	        s = new ServerSocket(8081);  
	        //�ȴ������󡢷���һֱ����   
	        while(true){  
	            socket = s.accept();
	            System.out.println("socket:"+socket);  
	            ServeOneJabbr serveJabbr = new ServeOneJabbr(socket);
	            
	            synchronized(lockObject){
	            	String str = serveJabbr.getMessage();
	            	idleIP.add(str);
	            	int taskId = MainController.ipMatchTask.get(str);
	            	MainController.ipMatchTask.remove(str);
	            	updateDatabase(taskId);
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
