/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker.servercom;

import in.spiph.info.packets.base.APacket;
import in.spiph.tracker.Tracker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PacketTrackerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;
    private List<APacket> readablePackets = new ArrayList();
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        APacket packet = (APacket) msg;
        System.out.println(packet.getFrom() + ": " + packet);
        Tracker.handleServerPacket(ctx, packet);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("Channel Active");
        this.context = ctx;
        for (APacket packet : packetsAwaitingSending) {
            ctx.write(packet);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            System.out.println("\n\tServer disconnected");
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }
    
    List<APacket> packetsAwaitingSending = new ArrayList();
    public void sendPacket(APacket packet) {
        if (context != null) {
            context.writeAndFlush(packet);
        } else {
            packetsAwaitingSending.add(packet);
        }
    }

    public List<APacket> getPackets() {
        List<APacket> packets = this.readablePackets;
        this.readablePackets = new ArrayList();
        return packets;
    }
}
