package socket;

import ui.ChatFrame;
import java.io.*;
import java.net.*;

public class Download implements Runnable{
    
    public ServerSocket server;//server de hong nguoi gui
    public Socket socket;
    public int port;
    public String saveTo = "";//duong dan toi file save...
    public InputStream In;// doc tu mang
    public FileOutputStream Out;//ghi ra file
    public ChatFrame ui;//giao dien
    
    public Download(String saveTo, ChatFrame ui){//cai ham khoi tao chu ko phai ham down load
        try {
            server = new ServerSocket(0);//serversocket(0) thi no thay cong nao con trong thi lay
            port = server.getLocalPort();//gio moi thay co nghia :v
            this.saveTo = saveTo;
            this.ui = ui;
        } 
        catch (IOException ex) {
            System.out.println("Exception [Download : Download(...)]");
        }
    }

    @Override
    public void run() {
        try {
            socket = server.accept();
            System.out.println("Download : "+socket.getRemoteSocketAddress());
            
            In = socket.getInputStream();
            Out = new FileOutputStream(saveTo);
            
            byte[] buffer = new byte[1024];
            int count;
            
            while((count = In.read(buffer)) >= 0){//doc toi khi cai buffer nhan duoc con full
                Out.write(buffer, 0, count);//ghi ra file
            }
            
            Out.flush();
            
            ui.jTextArea1.append("[Application > Me] : Download complete\n");//thong bao download thanh cong ra giao dien
            
            if(Out != null){ Out.close(); }
            if(In != null){ In.close(); }
            if(socket != null){ socket.close(); }
        } 
        catch (Exception ex) {
            System.out.println("Exception [Download : run(...)]");
        }
    }
}