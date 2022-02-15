package com.example.appstaticutil.ip;

import java.net.*;
import java.util.Enumeration;

public class IPUtil {

    public static InetAddress getIp() throws SocketException, UnknownHostException {
        InetAddress result = null;
        int lowest = Integer.MAX_VALUE;
//        int count = 0;

        //返回此机器上的所有接口
        Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
        while (nics.hasMoreElements()) {
            NetworkInterface ifc = nics.nextElement();
//            System.out.println("getName获得网络设备名称=" + ifc.getName());
//            System.out.println("getDisplayName获得网络设备显示名称=" + ifc.getDisplayName());
//            System.out.println("getIndex获得网络接口的索引=" + ifc.getIndex());
//            System.out.println("isUp是否已经开启并运行=" + ifc.isUp());
//            System.out.println("isBoopback是否为回调接口=" + ifc.isLoopback());
//            System.out.println("getMTU获得最大传输单元=" + ifc.getMTU());
//            System.out.println("**********************" + count++);

            //判断网络接口是否已经开启并正常工作
            if (ifc.isUp()) {
//                System.out.println("Testing interface: " + ifc.getDisplayName());
                if (ifc.getIndex() < lowest || result == null) {
                    lowest = ifc.getIndex();
                } else if (result != null) {
                    continue;
                }
                Enumeration<InetAddress> addrs = ifc.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress address = addrs.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
//                        System.out.println("Found non-loopback interface: " + ifc.getDisplayName());
                        result = address;
                    }
                }
            }
        }

        if (result != null) {
            return result;
        }

        return InetAddress.getLocalHost();
    }


//    public static void main(String[] args) throws SocketException, UnknownHostException {
//        InetAddress ip = getIp();
//        System.out.println("iiip:" + ip.getHostAddress());
//    }
}
