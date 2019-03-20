/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.spiph.tracker;

import in.spiph.info.packets.handling.PacketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author Bennett.DenBleyker
 */
public class PacketIDTracker implements Runnable {

    private static final int PORT = Integer.parseInt(System.getProperty("spiphi.port", "4198")) + 1;
    static final boolean SSL = System.getProperty("ssl") != null;

    @Override
    public void run() {
        try {
            Logger.getLogger(Tracker.class.getName()).log(Level.INFO, "Starting on port {0}", PORT);
            
            //Setup SSL
            final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }
            
            //Thread management
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new PacketServerInitializer(sslCtx, new PacketIDTrackerHandler(Tracker.name))) //Initializes
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true); //Prevents shutdown after the first finished packet
                ChannelFuture f = b.bind(PORT).sync() //Hold the thread until the Future finishes binding
                        .channel().closeFuture().sync(); //Hold the thread until the Future closes
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
            
        //
        } catch (SSLException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, "SSL failed to build\n" + ex.getLocalizedMessage(), ex);
        } catch (CertificateException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, "Invalid certificate\n" + ex.getLocalizedMessage(), ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, "ChannelFuture failed to bind or to close\n" + ex.getLocalizedMessage(), ex);
        }
    }

}
