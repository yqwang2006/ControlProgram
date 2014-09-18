import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;  
import java.net.Socket;  
public class ServeOneJabbr extends Thread{  
  
    private Socket socket = null;  
    private BufferedReader br = null;  
    private PrintWriter pw = null;  
    public String info = new String();  
    public ServeOneJabbr(Socket s){  
        socket = s;  
        System.out.println("In one jabbr");
        try {  
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);  
            start();  
        } catch (Exception e) {  
              
            e.printStackTrace(); 
            System.exit(-1);
        }  
    }  
      
    @Override  
    public void run() {  
        while(true){  
            String str;  
            try {  
                str = br.readLine();  
                System.out.println(str);
                if(str.equals("END")){
                    br.close();  
                    pw.close();  
                    socket.close();  
                    break;  
                }else{
                	info = str;
                }
                System.out.println("Client Socket Message:"+str);  
                pw.println("Message Received");  
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
        }  
    }  
    public String getMessage(){
    	return info;
    }
      
      
}  