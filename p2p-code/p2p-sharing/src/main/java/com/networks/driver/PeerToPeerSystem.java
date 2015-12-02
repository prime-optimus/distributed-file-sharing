package com.networks.driver;

import java.io.IOException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;

import com.networks.client.FileClient;
import com.networks.server.FileServer;

public class PeerToPeerSystem {

	public static void main(String[] args) {
		try {
			String mode = args[0];
			if(StringUtils.equals(mode, "server")){
				FileServer fileServer = new FileServer();
				fileServer.execute();
			} else if (StringUtils.equals(mode, "client") && NumberUtils.isNumber(args[1]) ){
				int clientId = NumberUtils.toInt(args[1]);
				FileClient fileClient = new FileClient();
				fileClient.execute(clientId);
			}
		} catch (IOException e) {
			System.out.println("Sorry, Something went wrong");
			if(args.length > 2){
				e.printStackTrace();
			}
		}
		

	}

}
