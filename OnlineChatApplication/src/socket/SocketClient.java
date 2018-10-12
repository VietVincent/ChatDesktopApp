package socket;

import ui.ChatFrame;
import ui.LoginFrame;
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ui.HistoryFrame;

public class SocketClient implements Runnable{//class chay client
    
    public int port;
    public String serverAddr;
    public Socket socket;// thong tin soc ket
    public Thread clientThread;//====================>
    public String username, password;//================>
    public ChatFrame ui_Chat;
    public LoginFrame ui_Login;
    public HistoryFrame historyFrame;
    
    public ObjectInputStream In;
    public ObjectOutputStream Out;//cac luong in out
    public History hist;// lich su chat chit
    
    public SocketClient(LoginFrame frame) throws IOException{
        ui_Login = frame; 
        this.serverAddr = ui_Login.serverAddr; this.port = ui_Login.port;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
    }

    @Override
    public void run() {//lap vo han de hong tin nhan
        boolean keepRunning = true;
        while(keepRunning){
            try {
            	ui_Login.jButton1.setEnabled(false); 
                ui_Login.jTextField1.setEditable(false); 
                ui_Login.jTextField2.setEditable(false);
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                if(msg.type.equals("message")){//tin nhan chat
                    if(msg.recipient.equals(username)){//gui_Chat toi clinet nay thi ghi ra Me
                        ui_Chat.jTextArea1.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    else{//con ko thi ghi ten nguoi nhan ... ma gui_Chat cho nguoi khac cung doc duoc???
                        ui_Chat.jTextArea1.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                                            
                    if(!msg.content.equals(".bye") && !msg.sender.equals(username)){//neu khong phai la tin dang xuat
                        String msgTime = (new Date()).toString();						// thi ghi ra history
                        
                        try{
                            hist.addMessage(msg, msgTime);
                            DefaultTableModel table = (DefaultTableModel) ui_Chat.historyFrame.jTable1.getModel();
                            table.addRow(new Object[]{msg.sender, msg.content, "Me", msgTime});
                        }
                        catch(Exception ex){}  
                    }
                }
                else if(msg.type.equals("login")){//ket qua login tu server
                    if(msg.content.equals("TRUE")){//chap nhan
                        ui_Login.jButton2.setEnabled(false); 
                        ui_Login.jButton3.setEnabled(false);
                        username = ui_Login.jTextField3.getText();//==============>
                        password = msg.content;//===============>
                        ui_Chat = new ChatFrame();//=====================>
                        ui_Chat.setVisible(true);//===================>
                        ui_Login.setVisible(false);//===================>
                        ui_Chat.client = this;
                        
                        
                        //ui_Login.jButton4.setEnabled(true); ui_Chat.jButton5.setEnabled(true);
                        final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "You have already logged into the application!", "Login Successful!", JOptionPane.INFORMATION_MESSAGE);
                        ui_Chat.jButton4.setEnabled(true);//====
                        ui_Chat.jButton5.setEnabled(true);//======
                        ui_Chat.jButton6.setEnabled(true);//========
                        ui_Chat.jButton8.setEnabled(true);//==========>
                        ui_Login.jTextField3.setEnabled(false); 
                        hist = ui_Chat.hist;
                        //ui_Chat.jPasswordField1.setEnabled(false);//thay doi hien thi giao dien
                    }	
                    else{
                    	final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "Username or password is incorrect or account has already logined!", "Login Failed!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else if(msg.type.equals("test")){
                    ui_Login.jButton1.setEnabled(false);
                    ui_Login.jButton2.setEnabled(true); 
                    ui_Login.jButton3.setEnabled(true);
                    ui_Login.jTextField3.setEnabled(true);
                    ui_Login.jTextField3.setEditable(true);
                    ui_Login.jPasswordField1.setEnabled(true);
                    ui_Login.jPasswordField1.setEditable(true);
                    //ui_Chat.jTextField3.setEnabled(true); ui_Chat.jPasswordField1.setEnabled(true);
                    //ui_Chat.jTextField1.setEditable(false); ui_Chat.jTextField2.setEditable(false);
                    //ui_Chat.jButton7.setEnabled(true);
                }
                else if(msg.type.equals("newuser")){//neu co nguoi moi dang nhap
                    if(!msg.content.equals(username)){//neu khong phai minh thi moi add vo
                        boolean exists = false;//kiem tra xem da co hay chua
                        for(int i = 0; i < ui_Chat.model.getSize(); i++){
                            if(ui_Chat.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        if(!exists){ ui_Chat.model.addElement(msg.content); }
                    }
                }
                else if(msg.type.equals("signup")){//ket qua dang ki tu server
                    if(msg.content.equals("TRUE")){//thanh cong
                    	ui_Login.jButton2.setEnabled(false); 
                        ui_Login.jButton3.setEnabled(false);
                        username = ui_Login.jTextField3.getText();//==============>
                        password = msg.content;//===============>
                        ui_Chat = new ChatFrame();//=====================>
                        ui_Chat.setVisible(true);//===================>
                        ui_Login.setVisible(false);//===================>
                        ui_Chat.client = this;
                        
                        
                        //ui_Login.jButton4.setEnabled(true); ui_Chat.jButton5.setEnabled(true);
                        final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "You have already logged into the application!", "Signup Successful!", JOptionPane.INFORMATION_MESSAGE);
                        ui_Chat.jButton4.setEnabled(true);//====
                        ui_Chat.jButton5.setEnabled(true);//======
                        ui_Chat.jButton6.setEnabled(true);//========
                        ui_Chat.jButton8.setEnabled(true);//==========>
                        ui_Login.jButton2.setEnabled(false); ui_Login.jButton3.setEnabled(false);
                        ui_Chat.jButton4.setEnabled(true); ui_Chat.jButton5.setEnabled(true);
                    }
                    else{
                    	final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "Username already exist!", "Signup Failed!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(username)){
                        ui_Chat.jTextArea1.append("["+ msg.sender +"] has already logged out!\n");
                        ui_Login.jButton1.setEnabled(true); 
                        //ui_Chat.jButton4.setEnabled(false); 
                        ui_Login.jTextField1.setEditable(true); 
                        ui_Login.jTextField2.setEditable(true);
                        
                        for(int i = 1; i < ui_Chat.model.size(); i++){
                            ui_Chat.model.removeElementAt(i);
                        }
                        
                        clientThread.stop();
                    }
                    else{
                        ui_Chat.model.removeElement(msg.content);
                        ui_Chat.jTextArea1.append("["+ msg.content +"] has already logged out!\n");
                    }
                }
                else if(msg.type.equals("upload_req")){// yeu cau gui_Chat file
                    //hoi thoai cho lua chon xuat hien
                    if(JOptionPane.showConfirmDialog(ui_Chat, ("Accept '"+msg.content+"' from "+msg.sender+" ?")) == 0){
                        
                        JFileChooser jf = new JFileChooser();//neu accept thi mo hop thoai cho noi de luu file
                        jf.setSelectedFile(new File(msg.content));
                        int returnVal = jf.showSaveDialog(ui_Chat);
                       
                        String saveTo = jf.getSelectedFile().getPath();
                        if(saveTo != null && returnVal == JFileChooser.APPROVE_OPTION){
                            Download dwn = new Download(saveTo, ui_Chat);
                            Thread t = new Thread(dwn);//chay thread download
                            t.start();
                            //send(new Message("upload_res", (""+InetAddress.getLocalHost().getHostAddress()), (""+dwn.port), msg.sender));
                            send(new Message("upload_res", username, (""+dwn.port), msg.sender));
                        }
                        else{
                            send(new Message("upload_res", username, "NO", msg.sender));
                        }
                    }
                    else{
                        send(new Message("upload_res", username, "NO", msg.sender));
                    }
                }
                else if(msg.type.equals("upload_res")){//giu y/c gui_Chat file
                    if(!msg.content.equals("NO")){//duoc chap thuan
                        int port  = Integer.parseInt(msg.content);
                        String addr = msg.sender;//lay dia chi nguoi giu
                    	final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "["+msg.sender+"] accepted file request!", "Transfer Successful!", JOptionPane.INFORMATION_MESSAGE);
                        ui_Chat.jButton5.setEnabled(false); ui_Chat.jButton6.setEnabled(false);
                        Upload upl = new Upload(addr, port, ui_Chat.file, ui_Chat);
                        Thread t = new Thread(upl);
                        t.start();//chay thread upload
                    }
                    else{
                    	final JPanel panel = new JPanel();
                    	JOptionPane.showMessageDialog(panel, "["+msg.sender+"] rejected file request!", "Transfer Failed!", JOptionPane.INFORMATION_MESSAGE);
                        //ui_Chat.jTextArea1.append("["+msg.sender+"] rejected file request\n");
                    }
                }
                else{
                	final JPanel panel = new JPanel();
                	JOptionPane.showMessageDialog(panel, "Unknown message type!", "Warning!", JOptionPane.WARNING_MESSAGE);
                }
            }
            catch(Exception ex) {
                keepRunning = false;
                final JPanel panel = new JPanel();
            	JOptionPane.showMessageDialog(panel, "Connection Failure!", "Warning!", JOptionPane.WARNING_MESSAGE);
                ui_Login.jButton1.setEnabled(true); 
                ui_Login.jTextField1.setEditable(true); 
                ui_Login.jTextField2.setEditable(true);
                //ui_Chat.jButton4.setEnabled(false); ui_Chat.jButton5.setEnabled(false); ui_Chat.jButton5.setEnabled(false);
                
                for(int i = 1; i < ui_Chat.model.size(); i++){
                    ui_Chat.model.removeElementAt(i);
                }
                
                clientThread.stop();
                JOptionPane.showMessageDialog(panel, "Exception SocketClient run()!", "Warning!", JOptionPane.WARNING_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    public void send(Message msg){//ham viet tin nhan
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
            
            if(msg.type.equals("message") && !msg.content.equals(".bye")){
                String msgTime = (new Date()).toString();
                try{//viet tin nao thi luu lai
                    hist.addMessage(msg, msgTime);               
                    DefaultTableModel table = (DefaultTableModel) ui_Chat.historyFrame.jTable1.getModel();
                    table.addRow(new Object[]{"Me", msg.content, msg.recipient, msgTime});
                }
                catch(Exception ex){}
            }
        } 
        catch (IOException ex) {
        	final JPanel panel = new JPanel();
        	JOptionPane.showMessageDialog(panel, "Exception SocketClient send()!", "Warning!", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
