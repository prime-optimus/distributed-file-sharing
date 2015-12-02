package com.networks.server;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.networks.config.Client;
import com.networks.config.ConfigurationManager;
import com.networks.filehandler.FileManager;
import com.networks.packets.FileDetails;
import com.networks.utils.ServerUtils;

public class FileServer {

	public void execute() throws IOException {
		boolean splitSuccessful = splitRequestedFile(readUserInput());
		
		if(splitSuccessful){
			System.out.println("Split Successful.");
			Runnable startFileServer = ServerUtils.startFileServer(getCurrentFileServer(), getTotalClients());
			ExecutorService executorService = Executors.newFixedThreadPool(1);
			executorService.execute(startFileServer);
			executorService.shutdown();
		}
	}

	private Client getCurrentFileServer() {
		return ConfigurationManager.getConfigurationManager().getConfiguration().getServer();
	}
	
	private int getTotalClients() {
		return ConfigurationManager.getConfigurationManager().getConfiguration().getTotalClients();
	}

	private boolean splitRequestedFile(FileDetails fileDetails) {
		boolean splitSuccessful = true;
		
		try {
			FileManager.getFileManager().splitFile(fileDetails.getFileName());
		} catch (IOException e1) {
			e1.printStackTrace();
			splitSuccessful = false; 
		}
		return splitSuccessful;
	}

	private FileDetails readUserInput() {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Enter File Name:");
		String fileName = in.nextLine();
		in.close();
		
		FileDetails fileDetails = FileManager.getFileManager().getFileDetails(fileName);
		System.out.println(fileDetails);
		ConfigurationManager.getConfigurationManager().setFileDetails(fileDetails);
		return fileDetails;
	}

}
