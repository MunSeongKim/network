package util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NSLookup {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		while( true ) {
			System.out.print("> ");
			String destHost = sc.nextLine();
			if( "exit".equals(destHost) ) {
				sc.close();
				break ;
			}
			
			try {
				InetAddress[] inetAddr = InetAddress.getAllByName(destHost);
				for(InetAddress ia : inetAddr){
					System.out.println(destHost + " : " + ia.getHostAddress());
				}
			} catch (UnknownHostException e) { 
				e.printStackTrace();
			} 
		}
		
		
	}

}
