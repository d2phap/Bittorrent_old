
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
 * Class FileInfo lưu thông tin cơ bản của 1 file khi dow và chia sẻ
 *
 * @author OPTIMUS
 */
public class ThongTinTapTin{

    public static long ktchunk = 512 * 1024;
    private String tenfile;
    private long ktfile;
    private String duongdan;
    private int sochunk;
    private transient Vector listeners;

    /**
     * @return the ktchunk
     */
    public long getKichThuocChunk() {
        return ktchunk;
    }

    /**
     * @param aKtchunk the ktchunk to set
     */
    public void setKichThuocChunk(long aKtchunk) {
        ktchunk = aKtchunk;
    }

    public ThongTinTapTin(String dd) {
        this(new File(dd));
    }

    public String getTenfile() {
        return tenfile;
    }

    /**
     * @param tenfile the tenfile to set
     */
    public void setTenfile(String tenfile) {
        this.tenfile = tenfile;
    }

    /**
     * @return the ktfile
     */
    public long getKichThuocFile() {
        return ktfile;
    }

    /**
     * @param ktfile the ktfile to set
     */
    public void setKichThuocFile(long ktfile) {
        this.ktfile = ktfile;
    }

    /**
     * @return the duongdan
     */
    public String getDuongdan() {
        return duongdan;
    }

    /**
     * @param duongdan the duongdan to set
     */
    public void setDuongdan(String duongdan) {
        this.duongdan = duongdan;
    }

    /**
     * @return the sochunk
     */
    public int getSochunk() {
        return sochunk;
    }

    /**
     * @param sochunk the sochunk to set
     */
    public void setSochunk(int sochunk) {
        this.sochunk = sochunk;
    }

    /**
     * * Phương thức khởi tạo mặc định
     */
    public ThongTinTapTin() {
        this.tenfile = null;
        this.ktfile = 0;
        this.duongdan = null;
        this.sochunk = 0;

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
        this.tenfile = ten;
        this.ktfile = kt;
        this.duongdan = dd;
        this.sochunk = chunk;
    }

    /**
     *
     * @param f
     */
    public ThongTinTapTin(File f) {
        this.tenfile = f.getName();
        this.ktfile = f.length();
        this.duongdan = f.getAbsolutePath();
        this.sochunk = demchunk();
    }

    /**
     * Đếm số lượng chunk của 1 file
     *
     * @return
     */
    public int demchunk() {
        int dem = (int) (ktfile / getKichThuocChunk());
        if (dem * getKichThuocChunk() < ktfile) {
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
            File f = new File(ThongTinChunk.ddmacdinh + tenfile + "/" + tenfile + "_" + chisochunk + ".chunk");
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
            File f = new File(ThongTinChunk.ddmacdinh + tenfile + "/" + tenfile + "_" + indexchunk + ".chunk");
            fis = new FileInputStream(f);
            buffer = new byte[(int) ktchunk];
            int readsize = fis.read(buffer);
            if (readsize < getKichThuocChunk()) {
                byte[] newbuffer = new byte[readsize];
                System.arraycopy(buffer, 0, newbuffer, 0, readsize);
                return newbuffer;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi\n\n" + ex.getMessage());
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
