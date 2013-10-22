
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Scanner;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Duong Dieu Phap
 */
public class DownloadTorrent extends Thread implements EventListener {

    private transient Vector listeners;
    private volatile boolean isRunning = true;
    private int numberFinished = 0;
    public File torrentFile = null;
    public peerinfo peer = null;

    public void run() {
        StartDownload();
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
     * Tạm dừng tiesn trình
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

    private void StartDownload() {

        if (torrentFile == null || peer == null) {
            return;
        }

        FileInputStream fin = null;
        int sochunk = 0;
        try {
            fin = new FileInputStream(torrentFile);
            Scanner input = new Scanner(fin);
            sochunk = input.nextInt();
        } catch (Exception ex) {
            ex.printStackTrace();
            //JOptionPane.showMessageDialog(new JPanel(), "Lỗi\n\n" + ex.getMessage());
            return;
        }

        String ten = torrentFile.getName();
        ten = ten.replaceAll(".torrent", "");

        final ThongTinTapTin fi = new ThongTinTapTin();
        fi.setTenfile(ten);
        fi.setSochunk(sochunk);

        LogFile.Write("Kiểm tra có tạo CustomEventListener chưa?");
        // if we have no listeners, do nothing...
        if (listeners != null && !listeners.isEmpty()) {
            // make a copy of the listener list in case
            //   anyone adds/removes listeners
            final Vector targets;
            synchronized (this) {
                targets = (Vector) listeners.clone();
            }
            // create the event object to send
            final CustomEventObject event = new CustomEventObject(0, sochunk);
            event._object1 = ten; //Lưu tên tập tin
            LogFile.Write("Đã tạo CustomEventListener. Tạo CustomEventObject");
            

            //Phát sinh sự kiện START
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                CustomEventListener l = (CustomEventListener) e.nextElement();
                l.onStart(event);
            }
            
            
            DownloadChunk down;
            for (int i = 0; i < sochunk; i++) {
                
                //Tạm dừng nếu nhận lệnh isRunning = false
                while (!isRunning) {
                    //Lặp cho đến khi nhận được lệnh isRunning = true
                }
                
                File f = new File(ThongTinChunk.ddmacdinh + ten + "/" + ten + "_" + (i + 1 + ".chunk"));
                if (!f.exists()) {
                    
                    // lúc này yêu cầu gửi
                    down = new DownloadChunk();
                    down.file = fi;
                    down.peer = peer;
                    down.thuTuChunk = i + 1;
                    down.addCustomListener(new CustomEventListener() {

                        @Override
                        public void onStart(CustomEventObject e) {
                        }

                        @Override
                        public void onOccur(CustomEventObject e) {
                        }

                        @Override
                        public void onFinish(CustomEventObject e) {
                            numberFinished++;
                            
                            //Phát sinh sự kiện OCCUR
                            event._value = numberFinished;
                            event._object1 = fi.getTenfile();
                            Enumeration ev = targets.elements();
                            while (ev.hasMoreElements()) {
                                CustomEventListener l = (CustomEventListener) ev.nextElement();
                                l.onOccur(event);
                            }
                        }
                    });
                    
                    down.start();
                }
            }
            
            
            //Phát sinh sự kiện FINISH
            e = targets.elements();
            while (e.hasMoreElements()) {
                CustomEventListener l = (CustomEventListener) e.nextElement();
                l.onFinish(event);
            }
            
        }//end if

    }
}
