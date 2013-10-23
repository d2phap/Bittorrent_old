
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Duong Dieu Phap
 */
public class ThreadNoiFile extends Thread implements EventListener{
    public ThongTinTapTin fInfo;
    private transient Vector listeners;

    public void NoiFile() {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        byte[] buffer;
        String tenfile = fInfo.getTenfile().replace(".torrent", "");
        
        LogFile.Write("#BEGIN NoiFile(): " + tenfile);
        
        try {
            fInfo.kiemTraVaTaoThuMuc(ThongTinChunk.luufile + tenfile + "/");
            fo = new FileOutputStream(ThongTinChunk.luufile + tenfile + "/" + tenfile);
            buffer = new byte[(int) fInfo.getKichThuocChunk()];
            LogFile.Write("Đọc số chunk: " + fInfo.getSochunk());
            
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
                CustomEventObject event = new CustomEventObject(0, fInfo.getSochunk());
                event._object1 = fInfo.getTenfile(); //Lưu tên tập tin
                LogFile.Write("Đã tạo CustomEventListener. Tạo CustomEventObject");
                
                LogFile.Write("Bắt đầu nối tập tin");
                for (int i = 0; i < fInfo.getSochunk(); i++) {
                    fi = new FileInputStream(ThongTinChunk.ddmacdinh + tenfile + "/" + tenfile + "_" + (i + 1) + ".chunk");
                    int readsize = fi.read(buffer);
                    fi.close();
                    fo.write(buffer, 0, readsize);

                    event._value = i + 1;
                    LogFile.Write("Nối chunk thứ i = " + i);

                    //Phát sinh sự kiện đang ghép tập tin
                    Enumeration e = targets.elements();
                    while (e.hasMoreElements()) {
                        CustomEventListener l = (CustomEventListener) e.nextElement();
                        l.onOccur(event);
                    }
                }

                fo.flush();
                
                //Phát sinh sự kiện cắt tập tin hoàn tất
                Enumeration e = targets.elements();
                while (e.hasMoreElements()) {
                    CustomEventListener l = (CustomEventListener) e.nextElement();
                    l.onFinish(event);
                }
                
                LogFile.Write("Nối thành công " + ThongTinChunk.luufile + tenfile + "/" + tenfile);

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi\n\n" + ex.getMessage());
            LogFile.Write("!ERROR: " + ex.getMessage());
        } finally {
            try {
                fo.close();
            } catch (IOException ex) {
                Logger.getLogger(ThongTinTapTin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LogFile.Write("#END Noifile()");
    }

    public void run() {
        NoiFile();
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
