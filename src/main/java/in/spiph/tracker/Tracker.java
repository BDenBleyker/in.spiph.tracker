/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker;

import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.base.TestPacket;
import in.spiph.tracker.servercom.ServerCom;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Bennett.DenBleyker
 */
public class Tracker {

    public static String name = "Tracker4";

    public static void main(String[] args) throws Exception {
        ServerCom.main(args);
    }

    public static void handleServerPacket(ChannelHandlerContext ctx, APacket packet) {
        switch (packet.getType()) {
            case 0: // TestPacket
                switch (packet.getData().toString()) {
                    case "Request":
                        ctx.writeAndFlush(new TestPacket("Hello!"));
                        break;
                    default:
                        System.out.println("Test Succeeds");
                }
                break;
            case 1: // IpPacket
                String[] dataSplit = packet.getData().toString().split(";");
                if (dataSplit[1].equals("?")) {
                    ctx.writeAndFlush(idToIp(Long.valueOf(dataSplit[0])));
                } else {
                    putIp(Long.valueOf(dataSplit[0]), dataSplit[1]);
                }
                break;
            default: // ErrorPacket
                System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
        }
    }

    private static Map<Long, String> ipList = new HashMap();

    public static String idToIp(long id) {
        return ipList.getOrDefault(id, "X");
    }

    public static void putIp(long id, String ip) {
        ipList.put(id, ip);
        System.out.println("Learned ip " + ip + " for id " + id);
    }

}
