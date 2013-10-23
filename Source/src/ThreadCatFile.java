

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Cắt file thành torrent
 *
 * @author OPTIMUS
 */
public class ThreadCatFile extends Thread implements EventListener {

    public ThongTinTapTin fi;
    private transient Vector listeners;

    public void Catfile() {
        
        LogFile.Write("#BEGIN Catfile(): " + fi.getTenfile());
        fi.kiemTraVaTaoThuMuc("./" + ThongTinChunk.ddmacdinh + fi.getTenfile() + "/");
        fi.kiemTraVaTaoThuMuc("./torrent/");

        FileInputStream fis = null;
        BufferedWriter bw = null;

        try {
            LogFile.Write("Đếm số lượng chunk sẽ cắt");
            fis = new FileInputStream(fi.getDuongdan());
            bw = new BufferedWriter(new FileWriter("torrent/" + fi.getTenfile() + ".torrent"));
            bw.write(fi.getSochunk() + "\r\n");
            bw.flush();
            LogFile.Write("Ghi số lượng chunk sẽ cắt: " + fi.getSochunk());
            
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
                CustomEventObject event = new CustomEventObject(0, fi.getSochunk());
                LogFile.Write("Đã tạo CustomEventListener. Tạo CustomEventObject");
                LogFile.Write("Bắt đầu cắt tập tin");
                
                for (int i = 0; i < fi.getSochunk(); i++) {
                    byte[] buffer = new byte[(int) fi.getKichThuocChunk()];
                    int readsize = fis.read(buffer);
                    byte[] newbuffer = new byte[readsize];

                    System.arraycopy(buffer, 0, newbuffer, 0, readsize);
                    fi.ghichunk(i + 1, newbuffer);
                    String hash = fi.getHashCode(ThongTinChunk.ddmacdinh + fi.getTenfile() + "/" + fi.getTenfile() + "_" + (i + 1) + ".chunk");

                    bw.write((i + 1) + " " + hash + "\r\n");
                    bw.flush();
                    LogFile.Write("Cắt chunk thứ i = " + i);
                    
                    event._value = i + 1;
                    
                    //Phát sinh sự kiện đang cắt tập tin
                    Enumeration e = targets.elements();
                    while (e.hasMoreElements()) {
                        CustomEventListener l = (CustomEventListener) e.nextElement();
                        l.onOccur(event);
                    }
                }

                //Phát sinh sự kiện cắt tập tin hoàn tất
                Enumeration e = targets.elements();
                while (e.hasMoreElements()) {
                    CustomEventListener l = (CustomEventListener) e.nextElement();
                    l.onFinish(event);
                }
                LogFile.Write("Cắt thành công " + fi.getSochunk() + "/" + fi.getSochunk() + " chunks");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(new JPanel(), "Lỗi:\n\n" + ex.getMessage());
            LogFile.Write("!ERROR: " + ex.getMessage());
        } finally {
            try {
                bw.flush();
                bw.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ThreadCatFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LogFile.Write("#END Catfile()");
    }

    public void run() {
        Catfile();
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

    
}
