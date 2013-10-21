
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
 * @author OPTIMUS
 */

public class peerinfo {
    private int id; 
    private InetAddress ipaddresss;
    private int port;
    private boolean status;

    public peerinfo() {
        try{
            lspeer = new ArrayList();
            loadpeer();
        }catch(Exception ex){
            
        }
        
    }
    public peerinfo(int _id,String _ip,int _port,boolean _status){
        this.id=_id;
        setIpaddress(_ip);
        this.port=_port;
        this.status=_status;
    }
    public peerinfo(peerinfo peer){
        this.id=peer.id;
        this.ipaddresss=peer.ipaddresss;
        this.port=peer.port;
        this.status=peer.status;
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
    public InetAddress getIpaddresss() {
        return ipaddresss;
    }

    /**
     * @param ipaddresss the ipaddresss to set
     */
    public void setIpaddresss(InetAddress ipaddresss) {
        this.ipaddresss = ipaddresss;
    }
    public void setIpaddress(String name){
        try {
            this.ipaddresss=InetAddress.getByName(name);
        } catch (UnknownHostException ex) {
            Logger.getLogger(peerinfo.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private ArrayList<peerinfo> lspeer;

    /**
     * @return the lspeer
     */
    public peerinfo getLspeer(int index) {
        return lspeer.get(index);
    }
    
    public int countlspeer(){
        return lspeer.size();
    }
            

    /**
     * @param lspeer the lspeer to set
     */
    public void addlsspeer(peerinfo peer) {
        lspeer.add(peer);
    }

    /**
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    //load peer
    public void loadpeer() throws IOException{
        FileInputStream fis = null;
        try{           
            fis = new FileInputStream("./Map/Nodes.map");
            Scanner input=new Scanner(fis);
            int n = input.nextInt();
            for(int i=0;i<n;i++){
                int _id=input.nextInt();
                String _ip=input.next();
                int _port=input.nextInt();
                if(_ip.compareTo(InetAddress.getLocalHost().getHostAddress())==0){
                    this.id=_id;
                    setIpaddress(_ip);
                    this.port=_port;
                    this.status=false;
                }else{
                    addlsspeer(new peerinfo(_id,_ip,_port,false) );
                }
            }
        }catch(IOException ex){
            
        }finally{
            fis.close();
        }
    }
    
}
