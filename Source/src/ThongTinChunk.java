/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author OPTIMUS
 */
public class ThongTinChunk {
    public static String ddmacdinh = "Chunk/"; // mặc định đường dẫn Chunk
    public static String luufile = "ghepfile/"; // mặc định file được ghép lưu vào 
    
    /**
     * Lưu trạng thái khi dowload
     */
    public static enum TrangThaiChunk {
        EMPTY,
        DOWNLOADING,
        ERROR,
        COMPLETED
    }
    
    private TrangThaiChunk[] chunkState;
    public TrangThaiChunk getTrangThai(int index) {
        return chunkState[index];
    }
    
    /**
     * 
     * @param index
     * @param newState 
     */
    public void setTrangThai(int index, TrangThaiChunk newState)
    {
        chunkState[index] = newState;
    }
    
    /**
     * 
     * @param chunkCount 
     */
    public ThongTinChunk(int chunkCount)
    {
        chunkState = new TrangThaiChunk[chunkCount];
        for (int i = 0; i < chunkCount; i++)
            chunkState[i] = TrangThaiChunk.EMPTY;
    }
    
    public int getSoLuongChunk() {
        return chunkState.length;
    }
}
