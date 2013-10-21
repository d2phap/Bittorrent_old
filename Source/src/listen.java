
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author OPTIMUS
 */
public class listen extends Thread{
    public peerinfo peer;
    public int port = 100;
    private byte[] buffer;
    private DatagramSocket socket;
    private DatagramPacket sendPacket, rcvPacket;  
    private String yc;
    
    /*
     * dùng để nghe toàn bộ yêu cầu các máy vs
     */
    public void nghe(){
        try {
            socket = new DatagramSocket(Bittorent_Like.portlisten);
            buffer = new byte[1024];
            rcvPacket = new DatagramPacket(buffer,buffer.length);
            do{
                try {
                     socket.receive(rcvPacket);
                     yc=new String(rcvPacket.getData(),0,rcvPacket.getLength());
                     if(yc.compareTo("ONL")==0){
                         socket.setSoTimeout(5000); // quá 5s mà không ai trả lời hủy
                         for(int i=0;i<Bittorent_Like.peer.countlspeer();i++){
                             if(Bittorent_Like.peer.getLspeer(i).getIpaddresss().getHostAddress().compareTo(rcvPacket.getAddress().getHostAddress())==0){
                                 Bittorent_Like.peer.getLspeer(i).setStatus(true);
                                 break;
                             }
                         }
                         sendPacket=new DatagramPacket("END".getBytes(),"END".getBytes().length,rcvPacket.getAddress(),rcvPacket.getPort());
                         try {
                             socket.send(sendPacket);
                         } catch (IOException ex) {
                            Logger.getLogger(sendrequest.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     }else if(yc.compareTo("Down_File") == 0){ //thằng send qua peerinfo
                         sendchunk send = new sendchunk();
                         send.peer = new peerinfo(1, rcvPacket.getAddress().getHostAddress(), rcvPacket.getPort(), true);
                         send.start();
                     }
                     
                } catch (IOException ex) {
                    
                    Logger.getLogger(listen.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                
            }while(true);
        } catch (SocketException ex) {
            
            Logger.getLogger(listen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void run(){
        nghe();
    }
}
