
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class SendRequest {

    public ThongTinPeer peer;
    public int port = 100;
    private byte[] buffer;
    private DatagramSocket socket;
    private DatagramPacket sendPacket, rcvPacket;

    public void gui() {
        try {
            socket = new DatagramSocket(Bittorent.portListen + port);
            socket.setSoTimeout(5000);
            
            for (int i = 0; i < peer.countListPeer(); i++) {
                String dc = peer.getListPeer(i).getIPAddresss().getHostAddress();
                sendPacket = new DatagramPacket("ONL".getBytes(), "ONL".getBytes().length, 
                        peer.getListPeer(i).getIPAddresss(), Bittorent.portListen);
                
                try {
                    socket.send(sendPacket);
                    buffer = new byte[1024];
                    rcvPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(rcvPacket);
                    String gt = new String(rcvPacket.getData(), 0, rcvPacket.getLength());
                    
                    if (gt != null) {
                        peer.getListPeer(i).setStatus(true);
                    }
                } catch (IOException ex) {

                    Logger.getLogger(SendRequest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            socket.close();
            
        } catch (SocketException ex) {
            socket.close();
            Logger.getLogger(SendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendPacket() {
        int timeOut = 500;
        try {
            int flag = 1;
            do {
                socket.setSoTimeout(timeOut);
                socket.send(sendPacket);
                int n = 1;
                while (n < 5) {
                    try {
                        rcvPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(rcvPacket);
                        timeOut -= 100;
                        flag = 6;
                        break;
                    } catch (SocketTimeoutException e) {
                        timeOut += 100;
                        socket.setSoTimeout(timeOut);
                        n++;
                        continue;
                    }
                }
                timeOut += 200;
                flag++;
            } while (flag < 5);
            if (flag == 5) {
                socket.close();
                //this.stop();
            }
        } catch (Exception ex) {
            socket.close();
            //this.stop();
        }
    }
}