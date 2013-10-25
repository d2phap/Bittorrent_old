
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author OPTIMUS
 */
public class ThreadSendChunk extends Thread {

    //public int port = 1;
    public ThongTinPeer peer;
    private byte[] buffer;
    private DatagramSocket socket;
    private DatagramPacket sendPacket, rcvPacket;

    public boolean send() { // dùng để send trở lại
        try {
            //socket = new DatagramSocket(Bittorent_Like.portlisten + port);
            socket = new DatagramSocket(); // random tự lấy port 
            buffer = new byte[1024];
            sendPacket = new DatagramPacket("OK".getBytes(), "OK".getBytes().length, peer.getIPAddresss(), peer.getPort());
            rcvPacket = new DatagramPacket(buffer, buffer.length);
            socket.send(sendPacket); //gui ok

            socket.receive(rcvPacket); // nhan tenfile_sochunk

            String rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength());

            String[] str = rc.split("_");
            String fileName = str[str.length - 1]; // tên
            int countChunk = Integer.parseInt(str[1]); // số lượng chunk

            ThongTinTapTin file = new ThongTinTapTin();
            file.setTenfile(fileName);

            byte[] data = file.readchunk(countChunk); // chứa 512kb đầy đủ
            int lengthData = data.length;

            byte[] b;
            int n = 0;
            while (lengthData != 0) {
                if (lengthData < 1024) {
                    b = new byte[data.length - n * 1024];
                    lengthData = 0; // để thoát khỏi while
                } else {
                    b = new byte[1024];
                    lengthData = lengthData - 1024; // mỗi lần trừ đi đúng 512kb
                }
                for (int i = 0; i < b.length; i++) {
                    b[i] = data[i + n * 1024];
                }

                n++;

                sendPacket = new DatagramPacket(b, b.length, rcvPacket.getAddress(), rcvPacket.getPort());
                socket.send(sendPacket); // gui 1024  byte

                socket.receive(rcvPacket); // nhận ok
            }
            sendPacket = new DatagramPacket("END".getBytes(), "END".getBytes().length, rcvPacket.getAddress(), rcvPacket.getPort());
            socket.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
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

    public void run() {
        send();
    }
}
