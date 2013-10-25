/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author OPTIMUS
 */
public class ThongTinChunk {
    public static String duongDanChunk = "Chunk/"; // mặc định đường dẫn Chunk
    public static String duongDanLuuChunk = "ghepfile/"; // mặc định file được ghép lưu vào 
    private TrangThaiChunk[] trangThaiChunk;
    
    /**
     * Lưu trạng thái khi dowload
     */
    public static enum TrangThaiChunk {
        MAC_DINH,
        DANG_TAI,
        LOI,
        TAI_HOAN_TAT
    }
    
    
    public TrangThaiChunk getTrangThai(int index) {
        return trangThaiChunk[index];
    }
    
    /**
     * 
     * @param index
     * @param newState 
     */
    public void setTrangThai(int index, TrangThaiChunk newState)
    {
        trangThaiChunk[index] = newState;
    }
    
    /**
     * 
     * @param chunkCount 
     */
    public ThongTinChunk(int chunkCount)
    {
        trangThaiChunk = new TrangThaiChunk[chunkCount];
        for (int i = 0; i < chunkCount; i++)
            trangThaiChunk[i] = TrangThaiChunk.MAC_DINH;
    }
    
    public int getSoLuongChunk() {
        return trangThaiChunk.length;
    }
}
