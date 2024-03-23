package com.fujitsupraktika.markus.proovitoo;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LocalInfo {

    /**
     * Get local ip address
     * @return local IPv4
     */
    public static String getIp() {
        // https://www.baeldung.com/java-get-ip-address
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            return datagramSocket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
