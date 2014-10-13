import java.sql.*;
import java.lang.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.HashMap;
import java.io.*;
import java.net.ServerSocket;  
import java.net.Socket;
import java.util.HashSet;
public class MainController {
	static int PORT = 8080;
	public static HashMap<Integer, String> type_map;
	static String remote_file_path = "http://101.227.252.154:81/file/";
	//用于保存当前时刻运算的IP和对应的计算任务
	public static HashMap<String,Integer> ipMatchTask = new HashMap<String,Integer>();
	public static void main(String[] args){ 
		//nodeState用于记录节点当前状态，0为空闲，1为运算，2为出错
		//String ipPool[] = initIpIndex();
		fill_map();
		//String ipPool[] = initIpIndexFromIPTable("F:/Java/ControlProgram/src/IPTABLE.txt");
		String ipPool[] = initIpIndexFromIPTable("IPTABLE.txt");
		Vector<String> idleNodeIP = new Vector<String>();
		initNodeState(idleNodeIP,ipPool);
		//taskPool用于记录当前待完成任务
		HashSet<Project> taskPool = new HashSet<Project>();
		
		SocketServer serverThread = new SocketServer();
		while(true){
			updateNodePool(idleNodeIP,serverThread);
			Connection conn = createMysqlConn();
			if(conn != null){
				updatePrjInfo(taskPool,conn);
				printSet(taskPool);
			}
			closeMysqlConn(conn);

			//now get the task pool
			scheduling(taskPool,idleNodeIP);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
	
	}
	private static void fill_map() {
		type_map = new HashMap<Integer,String>();
		type_map.put(1, "trainData");
		type_map.put(2, "trainLabels");
		type_map.put(3, "testData");
		type_map.put(4, "testLabels");
		type_map.put(5, "Finetune_data");
		type_map.put(6, "Finetune_labels");
		type_map.put(7, "Weight_addr");
	}
	private static String[] initIpIndexFromIPTable(String iptable) {
		// TODO Auto-generated method stub
		Vector<String> vs = new Vector<String>();
		File file = new File(iptable);
		BufferedReader bf = null;
		try{
			bf = new BufferedReader(new FileReader(file));
			String tempString = null;
			while((tempString = bf.readLine())!=null){
				vs.add(tempString);
			}
			bf.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}finally{
			if(bf != null){
				try{
					bf.close();
				}catch(IOException el){
					el.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
		
		String[] nodeState = new String[vs.size()];
		for(int i=0;i<vs.size();i++){
			nodeState[i] = vs.get(i);
		}
		return nodeState;
	}
	private static void printSet(HashSet<Project> taskPool){
		Iterator<Project> iter = taskPool.iterator();
		System.out.print("[ ");
		while(iter.hasNext()){
			Project task = iter.next();
			System.out.print(task.getPrjId() + " , ");
		}
		System.out.println(" ] ");
	}
	private static void updateNodePool(Vector<String> idleNodeIP, SocketServer serverThread) {
		
		for(int i = 0;i < serverThread.idleIP.size();i++){
			if(!idleNodeIP.contains(serverThread.idleIP.get(i))){
				idleNodeIP.add(serverThread.idleIP.get(i));
			}
		}
		serverThread.idleIP.clear();
		
		
	}

	private static void initNodeState(Vector<String> idleNodeIP, String[] ipPool) {
		// TODO Auto-generated method stub
		for(int i = 0;i < ipPool.length;i++){
			idleNodeIP.add(ipPool[i]);
		}
	}

	private static void scheduling(HashSet<Project> taskPool,
			Vector<String> idleNodeIP) {
		if(idleNodeIP.size() == 0) return; 
		Iterator<Project> task_iter = taskPool.iterator();
		int nodeIndex = 0;
		while(task_iter.hasNext()){
			
			Project task = task_iter.next();
			int taskId = task.getPrjId();
			
			if(nodeIndex < idleNodeIP.size()){
				String ipAddr = idleNodeIP.get(nodeIndex);
				sendTaskToNode(ipAddr,PORT,task.paramFileInfo);
				updateDatabase(taskId);
				ipMatchTask.put(ipAddr, taskId);
				idleNodeIP.remove(nodeIndex);
				nodeIndex ++;
			}else{
				break;
			}	
		}
		
	}

	private static void updateDatabase(Integer taskId) {
		// TODO Auto-generated method stub
		try {
			Connection conn = createMysqlConn();
			if(conn != null){
				String sql = "update project set resultFlag = 3 where prjId = " + taskId; 
				// TODO 需要更新resultAddr
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

	private static void sendTaskToNode(String ipAddr, int port, String task_info) {
		// TODO Auto-generated method stub
		Socket socket = null;  
        PrintWriter pw = null;  
        try {  
            //客户端socket指定服务器的地k址和端口号   
            socket = new Socket(ipAddr, port);  
            System.out.println("Socket=" + socket);  
            //同服务器原理一样   
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(  
                    socket.getOutputStream())));  
            pw.println(task_info);
            pw.flush();
            pw.println("END");  
            pw.flush();
        } catch (Exception e) {  
            e.printStackTrace();
            System.exit(-1);
        } finally {  
            try {  
                System.out.println("close......");  
                pw.close();  
                socket.close();  
                
            } catch (IOException e) {  
                // TODO Auto-generated catch block   
                e.printStackTrace();
                System.exit(-1);
            }  
        }  
	}

	private static void closeMysqlConn(Connection conn) {
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}

	private static void updatePrjInfo(HashSet<Project> taskPool, Connection conn) {
		try { 
			String sql = "select * from project where resultFlag = 2"; 
			Statement statement = conn.createStatement();
	        ResultSet rs = statement.executeQuery(sql);
	        
	        int prjid,userid,prjtype;
	        String prjName;
	        
	        taskPool.clear();
	        while(rs.next()) {
		          // 选择sname这列数据
		          prjid = rs.getInt("prjId");
		          prjtype = rs.getInt("type");
		          Project task = new Project(prjid,prjtype);
		          String paramName = getPrjInfo(task,prjid,conn);
		          
		          taskPool.add(task);
		          
	         }
	        rs.close();
		} catch(SQLException e) { 
            e.printStackTrace();
            System.exit(-1);
        } catch(Exception e) { 
        	e.printStackTrace();
        	System.exit(-1);
        }
	}
	private static String  getPrjInfo(Project prj, int taskId, Connection conn) {
		String paramFileName = new String();
		try{
			String sql = null;
			ResultSet rs = null;
			Statement statement = conn.createStatement();
			int layerid,cal_order,cal_typeid,param_name_id,param_type;
			String cal_type = null;
			String param_name = new String();
			String param_values = new String();
			
			/**
			 * Here, we need to query the database to acquire global model infomation.
			 */
			sql = "select * from model_info where prj_id = " + taskId;
			rs = statement.executeQuery(sql);
			while(rs.next()){
				param_name = rs.getString("param_name");
				param_type = rs.getInt("param_type");
				param_values = rs.getString("param_values");
				Param p = new Param(param_name,param_type,param_values);
				prj.addGlobalInfo(p);
			}
			
			
			
			
			sql = "select l.*,t.cal_type_name from cal_layer l,cal_type t where l.prjid = " + taskId + " and l.cal_typeid = t.cal_type_id";
			rs = statement.executeQuery(sql);
			while(rs.next()){
				layerid = rs.getInt("layerid");
				cal_order = rs.getInt("cal_order");
				cal_typeid = rs.getInt("cal_typeid");
				cal_type = rs.getString("cal_type_name");

				//System.out.println(layerid + "\t" + cal_order + "\t" + cal_typeid);
				LayerInfo layer = new LayerInfo(layerid,cal_order,cal_typeid,cal_type);
				//layer.printInfo();
				prj.addLayer(layer);

			}
			
			
			/**
			 * Now we want to query the database to acquire data files.
			 * But this version didn't include the fine-tune data files.
			 * This part just get the remote files' address, then send it to computing nodes
			 * computing nodes will download the files needed.
			 * 
			 */
			sql = "select c.data_type,d.data_file_addr,d.data_file_type from cal_data c,tbl_datas d where c.prjid = " + taskId + " and c.data_file_id = d.data_file_Id";
			rs = statement.executeQuery(sql);
			while(rs.next()){
				int data_type = rs.getInt("data_type");
				String filename = rs.getString("data_file_addr");
				String file_type = rs.getString("data_file_type");
				
				String remote_path = filename;// remote_file_path + filename;
				DataAddress file_addr = new DataAddress(type_map.get(data_type),filename,file_type);
				prj.get_data_addr().add(file_addr);
			}

			for(int j = 0;j < prj.getLayerNum();j++){
				sql = "select l.*,p.parms_name from layer_parms l,tbl_parms_name p where l.layerId = " +prj.getLayer(j).layerId + " and l.parms_name_Id = p.parms_name_id" ;
				rs = statement.executeQuery(sql);
				while(rs.next()){
					param_type = rs.getInt("parms_type");
					param_name = rs.getString("parms_name");
					param_values = rs.getString("parms_value");
					Param p = new Param(param_name,param_type,param_values);
					prj.getLayer(j).addParam(p);
					//p.printInfo();

				}
			}
			
			prj.fillParamFile();
			
			//System.out.print(prj.paramFileInfo);
			rs.close();

		} catch(SQLException e) { 
			e.printStackTrace(); 
			System.exit(-1);
		} catch(Exception e) { 
			e.printStackTrace();
			System.exit(-1);
		} 
		return paramFileName;
	}
		private static Connection createMysqlConn() {
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

}