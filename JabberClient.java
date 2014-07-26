import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;  
import java.net.Socket;  
  
public class JabberClient {  
  
        public static void main(String[] args) {  
        Socket socket = null;  
        BufferedReader br = null;  
        PrintWriter pw = null;  
        try {  
            //�ͻ���socketָ���������ĵ�kַ�Ͷ˿ں�   
            socket = new Socket("172.16.248.231", 8080);  
            System.out.println("Socket=" + socket);  
            //ͬ������ԭ��һ��   
            br = new BufferedReader(new InputStreamReader(  
                    socket.getInputStream()));  
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(  
                    socket.getOutputStream())));  
            for (int i = 0; i < 10; i++) {  
                pw.println("howdy " + i);  
                pw.flush();  
                String str = br.readLine();  
                System.out.println(str);  
            }  
            pw.println("END");  
            pw.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
            System.exit(-1);
        } finally {  
            try {  
                System.out.println("close......");  
                br.close();  
                pw.close();  
                socket.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block   
                e.printStackTrace();  
                System.exit(-1);
            }  
        }  
    }  
  
}  