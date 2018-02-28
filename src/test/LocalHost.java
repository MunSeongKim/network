package test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public class LocalHost {

	public static void main(String[] args) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			//InetAddress inetAddress = InetAddress.getByName("eth0");
			String hostname = inetAddress.getHostName();
			String hostAddress = inetAddress.getHostAddress();
			byte[] addresses = inetAddress.getAddress();
			System.out.println(hostname);
			System.out.println(hostAddress);
			
			
			for(int i = 0; i < addresses.length; i++){
				System.out.print(addresses[i] & 0x000000ff);
				if(i < 3) {
					System.out.print(".");
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
