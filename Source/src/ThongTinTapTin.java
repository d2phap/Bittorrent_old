
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template fi the editor.
 */
/**
 * Class FileInfo lưu thông tin cơ bản của 1 file khi down và chia sẻ
 *
 * @author OPTIMUS
 */
public class ThongTinTapTin{

    public static long kichThuocChunk = 512 * 1024;
    private String tenFile;
    private long kichThuocFile;
    private String duongDan;
    private int soChunk;
    private transient Vector listeners;

    /**
     * @return the kichThuocChunk
     */
    public long getKichThuocChunk() {
        return kichThuocChunk;
    }

    /**
     * @param aKtchunk the kichThuocChunk to set
     */
    public void setKichThuocChunk(long aKtchunk) {
        kichThuocChunk = aKtchunk;
    }

    public ThongTinTapTin(String dd) {
        this(new File(dd));
    }

    public String getTenfile() {
        return tenFile;
    }

    /**
     * @param tenfile the tenfile to set
     */
    public void setTenfile(String tenfile) {
        this.tenFile = tenfile;
    }

    /**
     * @return the ktfile
     */
    public long getKichThuocFile() {
        return kichThuocFile;
    }

    /**
     * @param ktfile the ktfile to set
     */
    public void setKichThuocFile(long ktfile) {
        this.kichThuocFile = ktfile;
    }

    /**
     * @return the duongdan
     */
    public String getDuongdan() {
        return duongDan;
    }

    /**
     * @param duongdan the duongdan to set
     */
    public void setDuongdan(String duongdan) {
        this.duongDan = duongdan;
    }

    /**
     * @return the sochunk
     */
    public int getSochunk() {
        return soChunk;
    }

    /**
     * @param sochunk the sochunk to set
     */
    public void setSochunk(int sochunk) {
        this.soChunk = sochunk;
    }

    /**
     * * Phương thức khởi tạo mặc định
     */
    public ThongTinTapTin() {
        this.tenFile = null;
        this.kichThuocFile = 0;
        this.duongDan = null;
        this.soChunk = 0;

    }

    /**
     * * Phương thức khởi tạo đầy đủ tham số
     *
     * @param ten
     * @param kt
     * @param dd
     * @param chunk
     */
    public ThongTinTapTin(String ten, long kt, String dd, int chunk) {
        this.tenFile = ten;
        this.kichThuocFile = kt;
        this.duongDan = dd;
        this.soChunk = chunk;
    }

    /**
     *
     * @param f
     */
    public ThongTinTapTin(File f) {
        this.tenFile = f.getName();
        this.kichThuocFile = f.length();
        this.duongDan = f.getAbsolutePath();
        this.soChunk = demChunk();
    }

    /**
     * Đếm số lượng chunk của 1 file
     *
     * @return
     */
    public int demChunk() {
        int dem = (int) (kichThuocFile / getKichThuocChunk());
        if (dem * getKichThuocChunk() < kichThuocFile) {
            dem++;
        }
        
        
        return dem;
    }

    /**
     * Kiểm tra xem thư mục tồn tại hay chưa
     *
     * @param dir
     */
    public void kiemTraVaTaoThuMuc(String dir) {
        File f = new File(dir);
        
        LogFile.Write("Bắt đầu kiểm tra thư mục " + f.getAbsolutePath());
        
        if (!f.exists()) {
            LogFile.Write("Thư mục " + f.getAbsolutePath() + " không tồn tại");
            f.mkdir();
            LogFile.Write("Tạo mới thư mục " + f.getAbsolutePath());
        }
    }

    /**
     * Ghi 1 chunk xuống
     *
     * @param chisochunk
     * @param data
     * @return
     */
    public boolean ghichunk(int chisochunk, byte[] data) {
        FileOutputStream out = null;
        try {
            File f = new File(ThongTinChunk.duongDanChunk + tenFile + "/" + tenFile + "_" + chisochunk + ".chunk");
            out = new FileOutputStream(f);
            out.write(data);
            out.flush();
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(ThongTinTapTin.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
    }

    /**
     * Đọc mảng byte từ file chunk
     *
     * @param indexchunk
     * @return
     */
    public byte[] readchunk(int indexchunk) {
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            File f = new File(ThongTinChunk.duongDanChunk + tenFile + "/" + tenFile + "_" + indexchunk + ".chunk");
            fis = new FileInputStream(f);
            buffer = new byte[(int) kichThuocChunk];
            int readsize = fis.read(buffer);
            if (readsize < getKichThuocChunk()) {
                byte[] newbuffer = new byte[readsize];
                System.arraycopy(buffer, 0, newbuffer, 0, readsize);
                return newbuffer;
            }
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(null, "Lỗi\n\n" + ex.getMessage());
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ThongTinTapTin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return buffer;
    }

    /**
     * Mã hóa hash code
     *
     * @param chunkName
     * @return
     */
    private byte[] createHashCode(String chunkName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(chunkName);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("SHA-1");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            fis.close();
            return complete.digest();
        } catch (Exception ex) {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
            }

            return null;
        }
    }

    /**
     * chuyển hasecode thành tên
     *
     * @param chunkName
     * @return
     */
    public String getHashCode(String chunkName) {
        byte[] b = createHashCode(chunkName);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
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
