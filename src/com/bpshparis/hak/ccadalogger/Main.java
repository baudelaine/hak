package com.bpshparis.hak.ccadalogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	private final static int PACKETSIZE = 10000;

	public static void main(String args[]){

		DatagramSocket socket = null ;

	    try{
			//Convert the arguments first, to ensure that they are valid
			InetAddress host = InetAddress.getByName("193.251.53.223") ;
			int port = Integer.parseInt("5100") ;

			// Construct the socket
			socket = new DatagramSocket() ;

			// Construct the datagram packet
			// /opt/wks/eclipse/hak/hak/res/request.json

			Path path = Paths.get("/opt/wks/hak/WebContent/res/request.json");
			byte[] data = Files.readAllBytes(path);

			DatagramPacket packet = new DatagramPacket(data, data.length, host, port) ;

			// Send it
			socket.send(packet) ;

			// Set a receive timeout, 2000 milliseconds
			socket.setSoTimeout(2000) ;

			// Prepare the packet for receive
			packet.setData(new byte[PACKETSIZE]) ;

			// Wait for a response from the server
			socket.receive(packet) ;

			// Print the response
			System.out.println(new String(packet.getData())) ;

	    }
		catch( Exception e ){
			System.out.println( e ) ;
		}
		finally{
			if( socket != null )
				socket.close() ;
			}
		}

}
