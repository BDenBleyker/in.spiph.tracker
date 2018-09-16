/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Tracker {

    public static String name = "Tracker4";
    private static final Map<String, String> IPLIST = new HashMap();

    public static void main(String[] args) throws Exception {
        putIp("bdbleyker@gmail.com", "127.0.0.1");
        
        new Thread(new PacketIDTracker()).start();
    }

    public static String idToIp(String id) {
        return IPLIST.getOrDefault(id, "X");
    }

    public static void putIp(String id, String ip) {
        IPLIST.put(id, ip);
        System.out.println("Learned ip " + ip + " for id " + id);
    }

}
