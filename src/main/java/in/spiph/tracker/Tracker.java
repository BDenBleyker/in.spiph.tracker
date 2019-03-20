/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Tracker {

    public static String name = "TRack4";
    private static final Map<String, String> IPLIST = new HashMap();
    private static final String ID_PATH = "idToIp.csv";

    public static void main(String[] args) throws Exception {
        Logger.getLogger(Tracker.class.getName()).setLevel(Level.WARNING);
        
        getIps();
        //putIp("bdbleyker@gmail.com", "127.0.0.1");
        
        new Thread(new PacketIDTracker()).start();
    }

    public static String idToIp(String id) {
        return IPLIST.getOrDefault(id, "X");
    }

    public static void putIp(String id, String ip) {
        IPLIST.put(id, ip);
        Logger.getLogger(Tracker.class.getName()).log(Level.INFO, "Learned ip {0} for id {1}", new Object[]{ip, id});
    }
    
    public static void getIps() throws IOException {
        File file = new File(ID_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] id = line.split(",");
                putIp(id[0], id[1]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
