
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author OPTIMUS dùng để yêu cầu dow 1 chunk
 */
public class DownloadChunk extends Thread {

    public ThongTinTapTin file;
    public peerinfo peer;
    public int chunkdow; // chunk dow số thứ nhiu, đang dow về
    private byte[] buffer;
    private int port = 1;
    private ThongTinChunk chunkInfo;
    private DatagramSocket socket;
    private DatagramPacket sendPacket, rcvPacket;

    public boolean dow() {
        try {
            String chunk = file.getTenfile() + "_" + chunkdow;

            System.out.println("dang down chunk: " + chunk);

            for (int i = 0; i < peer.countlspeer(); i++) {
                try {
                    buffer = new byte[1024];

                    socket = new DatagramSocket();
                    socket.setSoTimeout(5000);

                    sendPacket = new DatagramPacket("Down_File".getBytes(), "Down_File".getBytes().length, peer.getLspeer(i).getIpaddresss(), Bittorent_Like.portlisten);

                    socket.send(sendPacket); //gui Yêu Cầu down_file 

                    rcvPacket = new DatagramPacket(buffer, buffer.length);

                    socket.receive(rcvPacket); // Nhận ok

                    String rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength());

                    if (rc != null) {
                        sendPacket = new DatagramPacket(chunk.getBytes(), chunk.getBytes().length, rcvPacket.getAddress(), rcvPacket.getPort());

                        socket.send(sendPacket); //gui tenfile_sochunk


                        socket.receive(rcvPacket); // nhận 1024 byte


                        sendPacket = new DatagramPacket("OK".getBytes(), "OK".getBytes().length, rcvPacket.getAddress(), rcvPacket.getPort());
                        rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength()); // kiểm tra xem có file đó ko

                        byte[] data = new byte[(int) file.ktchunk];// lưu chunk có kích thước 512 kb
                        int n = 0; // số lần dow
                        int size = 0;
                        while (rc.compareTo("END") != 0) {
                            if (rc.compareTo("END") != 0) {
                                int numbyte = rcvPacket.getData().length;
                                System.arraycopy(rcvPacket.getData(), 0, data, n * 1024, numbyte);
                                size += numbyte;
                                n++;
                                socket.send(sendPacket);
                                socket.receive(rcvPacket);
                                System.out.println("chunk: " + chunkdow + " so lan nhan: " + n);
                            }
                            rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength()); // kiểm tra xem có file đó ko
                        }
                        byte[] newData = new byte[size];
                        System.arraycopy(data, 0, newData, 0, size);
                        boolean ghi = file.ghichunk(chunkdow, newData);
                        if (ghi == true) {
                            JOptionPane.showMessageDialog(null, "Tải tập tin thành công " + chunk);
                        } else {
                            //JOptionPane.showMessageDialog(null, " loi khi ghi chunk " + chunkdow);
                        }

                        socket.close();
                        this.stop();

                    }

                } catch (Exception ex) {

                    continue;
                }

            }

        } catch (Exception e) {
        }
        this.stop();
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
        dow();
    }
}
