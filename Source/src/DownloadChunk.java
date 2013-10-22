
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author OPTIMUS dùng để yêu cầu startDownload 1 chunk
 */
public class DownloadChunk extends Thread {

    public ThongTinTapTin file;
    public peerinfo peer;
    public int thuTuChunk; // thứ tự chunk đang tải về
    private byte[] buffer;
    private int port = 1;
    private ThongTinChunk chunkInfo;
    private DatagramSocket socket;
    private DatagramPacket sendPacket, rcvPacket;
    private volatile boolean isRunning = true;
    private transient Vector listeners;

    public boolean startDownload() {
        try {
            String tenChunk = file.getTenfile() + "_" + thuTuChunk;
            System.out.println("dang down chunk: " + tenChunk);


            LogFile.Write("Kiểm tra có tạo CustomEventListener chưa?");
            // if we have no listeners, do nothing...
            if (listeners != null && !listeners.isEmpty()) {
                // make a copy of the listener list in case
                //   anyone adds/removes listeners
                Vector targets;
                synchronized (this) {
                    targets = (Vector) listeners.clone();
                }
                // create the event object to send
                CustomEventObject event = new CustomEventObject();
                event._object1 = tenChunk; //Lưu tên tập tin
                LogFile.Write("Đã tạo CustomEventListener. Tạo CustomEventObject");


                //Phát sinh sự kiện START
                Enumeration e = targets.elements();
                while (e.hasMoreElements()) {
                    CustomEventListener l = (CustomEventListener) e.nextElement();
                    l.onStart(event);
                }


                for (int i = 0; i < peer.countlspeer(); i++) {

                    //Tạm dừng nếu nhận lệnh isRunning = false
                    while (!isRunning) {
                        //Lặp cho đến khi nhận được lệnh isRunning = true
                    }

                    try {
                        buffer = new byte[1024];

                        socket = new DatagramSocket();
                        socket.setSoTimeout(5000);

                        sendPacket = new DatagramPacket("Down_File".getBytes(), "Down_File".getBytes().length,
                                peer.getLspeer(i).getIpaddresss(), Bittorent_Like.portlisten);

                        socket.send(sendPacket); //gui Yêu Cầu down_file 
                        rcvPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(rcvPacket); // Nhận ok

                        String rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength());

                        if (rc != null) {
                            sendPacket = new DatagramPacket(tenChunk.getBytes(), tenChunk.getBytes().length, rcvPacket.getAddress(), rcvPacket.getPort());

                            socket.send(sendPacket); //gui tenfile_sochunk
                            socket.receive(rcvPacket); // nhận 1024 byte

                            sendPacket = new DatagramPacket("OK".getBytes(), "OK".getBytes().length, rcvPacket.getAddress(), rcvPacket.getPort());
                            rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength()); // kiểm tra xem có file đó ko

                            byte[] data = new byte[(int) file.ktchunk];// lưu chunk có kích thước 512 kb
                            int n = 0; // số lần startDownload
                            int size = 0;


                            while (rc.compareTo("END") != 0) {

                                if (rc.compareTo("END") != 0) {
                                    int numbyte = rcvPacket.getData().length;
                                    System.arraycopy(rcvPacket.getData(), 0, data, n * 1024, numbyte);
                                    size += numbyte;
                                    n++;
                                    socket.send(sendPacket);
                                    socket.receive(rcvPacket);
                                    System.out.println("chunk: " + thuTuChunk + " so lan nhan: " + n);
                                }

                                rc = new String(rcvPacket.getData(), 0, rcvPacket.getLength()); // kiểm tra xem có file đó ko
                            }

                            byte[] newData = new byte[size];
                            System.arraycopy(data, 0, newData, 0, size);
                            boolean ghi = file.ghichunk(thuTuChunk, newData);

                            //Phát sinh sự kiện OCCUR
                            e = targets.elements();
                            while (e.hasMoreElements()) {
                                CustomEventListener l = (CustomEventListener) e.nextElement();
                                l.onOccur(event);
                            }
                            

                            if (ghi == true) {
                                //Phát sinh sự kiện OCCUR
                                e = targets.elements();
                                while (e.hasMoreElements()) {
                                    CustomEventListener l = (CustomEventListener) e.nextElement();
                                    l.onFinish(event);
                                }
                                
                                //JOptionPane.showMessageDialog(null, "Tải tập tin thành công " + tenChunk);
                            } else {
                                //JOptionPane.showMessageDialog(null, " loi khi ghi chunk " + thuTuChunk);
                            }

                            socket.close();

                        }

                    } catch (Exception ex) {
                        continue;
                    }

                }
            }

        } catch (Exception e) {
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
        startDownload();
    }

    /**
     * Đăng ký sự kiện
     */
    synchronized public void addCustomListener(CustomEventListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    /**
     * Xoá sự kiện
     */
    synchronized public void removeCustomListener(CustomEventListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.removeElement(l);
    }

    /**
     * Tạm dừng tien trình
     *
     * @throws InterruptedException
     */
    public void pauseThread() throws InterruptedException {
        isRunning = false;
    }

    /**
     * Phục hồi tiến trình
     */
    public void resumeThread() {
        isRunning = true;
    }
}
