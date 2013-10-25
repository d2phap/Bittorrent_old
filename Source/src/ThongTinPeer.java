
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * dùng lưu thông tin các máy trong file node
 *
 * @author OPTIMUS
 */
public class ThongTinPeer {

    private int id;
    private InetAddress ipAddresss;
    private int port;
    private boolean status;
    private ArrayList<ThongTinPeer> listPeer;

    public ThongTinPeer() {
        try {
            listPeer = new ArrayList();
            loadPeer();
        } catch (Exception ex) {
        }

    }

    public ThongTinPeer(int _id, String _ip, int _port, boolean _status) {
        this.id = _id;
        setIPAddress(_ip);
        this.port = _port;
        this.status = _status;
    }

    public ThongTinPeer(ThongTinPeer peer) {
        this.id = peer.id;
        this.ipAddresss = peer.ipAddresss;
        this.port = peer.port;
        this.status = peer.status;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the ipaddresss
     */
    public InetAddress getIPAddresss() {
        return ipAddresss;
    }

    /**
     * @param ipaddresss the ipaddresss to set
     */
    public void setIPAddresss(InetAddress ipaddresss) {
        this.ipAddresss = ipaddresss;
    }

    public void setIPAddress(String name) {
        try {
            this.ipAddresss = InetAddress.getByName(name);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ThongTinPeer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    

    /**
     * @return the lspeer
     */
    public ThongTinPeer getListPeer(int index) {
        return listPeer.get(index);
    }

    public int countListPeer() {
        return listPeer.size();
    }

    /**
     * @param lspeer the lspeer to set
     */
    public void addPeer(ThongTinPeer peer) {
        listPeer.add(peer);
    }

    /**
     * @return the status
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Đọc thông tin các peer từ file map
     * @throws IOException 
     */
    public void loadPeer() throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./Map/Nodes.map");
            Scanner input = new Scanner(fis);
            int n = input.nextInt();
            
            for (int i = 0; i < n; i++) {
                int _id = input.nextInt();
                String _ip = input.next();
                int _port = input.nextInt();
                
                if (_ip.compareTo(InetAddress.getLocalHost().getHostAddress()) == 0) {
                    this.id = _id;
                    setIPAddress(_ip);
                    this.port = _port;
                    this.status = false;
                } else {
                    addPeer(new ThongTinPeer(_id, _ip, _port, false));
                }
            }
        } catch (IOException ex) {
        } finally {
            fis.close();
        }
    }
}
