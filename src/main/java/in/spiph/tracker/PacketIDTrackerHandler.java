/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker;

import in.spiph.info.packets.base.APacket;
import in.spiph.info.packets.base.TestPacket;
import in.spiph.info.packets.handling.PacketHandler;
import in.spiph.info.packets.tracker.IpPacket;
import static in.spiph.tracker.Tracker.idToIp;
import static in.spiph.tracker.Tracker.putIp;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelPipeline;

/**
 *
 * @author Bennett.DenBleyker
 */
@Sharable
public class PacketIDTrackerHandler extends PacketHandler {

    public PacketIDTrackerHandler(String from) {
        super(from);
    }

    @Override
    public boolean handleException(Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            System.out.println("\n\tServer disconnected");
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void handlePacket(ChannelPipeline pipeline, APacket packet) {
        switch (packet.getType()) {
            case 0: // TestPacket
                switch (packet.getData().toString()) {
                    case "Request":
                        pipeline.fireUserEventTriggered(new TestPacket("Hello!"));
                        break;
                    default:
                        System.out.println("Test Succeeds");
                }
                break;
            case 1: // IpPacket
                String[] dataSplit = packet.getData().toString().split(";");
                if (dataSplit[1].equals("?")) {
                    pipeline.fireUserEventTriggered(new IpPacket(dataSplit[0], idToIp(dataSplit[0])));
                } else {
                    putIp(dataSplit[0], dataSplit[1]);
                }
                break;
            default: // ErrorPacket
                System.out.println("Invalid packet id (" + packet.getType() + "): " + packet.toString());
        }
    }

    @Override
    public boolean getTestMode() {
        return false;
    }
}
