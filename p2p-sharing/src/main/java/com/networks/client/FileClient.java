package com.networks.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.networks.config.Client;
import com.networks.config.Configuration;
import com.networks.config.ConfigurationManager;
import com.networks.filehandler.FileManager;
import com.networks.utils.ServerUtils;

public class FileClient {

	public void execute(int clientId) {
		ConfigurationManager configurationManager = ConfigurationManager.getConfigurationManager();
		Configuration configuration = configurationManager.getConfiguration();
		configuration.setDirectoryPath(configuration.getDirectoryPath() + "\\client" + clientId + "\\");
		
		Client currentClient = configuration.getClientWithId(clientId);
		Client uploadNeighborClient = configuration.getClientWithId(currentClient.getNeighborId());
		Client server = configuration.getServer();
		
		Runnable startClientTask = ServerUtils.startFileRequestGenerator(server, currentClient);
		Runnable startServerTask = ServerUtils.startFileServer(currentClient, 1);
		Runnable startNeighborClientTask = ServerUtils.startFileRequestGenerator(uploadNeighborClient, currentClient);
		
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		executorService.execute(startClientTask);
		executorService.shutdown();
		ServerUtils.awaitTerminationForCurrentTasks(executorService);
		System.out.println("Main Server connection ends");
		
		executorService = Executors.newFixedThreadPool(2);
		executorService.execute(startServerTask);
		executorService.execute(startNeighborClientTask);
		executorService.shutdown();
		ServerUtils.awaitTerminationForCurrentTasks(executorService);
		
		mergeFiles(configurationManager);
	}

	private void mergeFiles(ConfigurationManager configurationManager) {
		try {
			System.out.println("Merging Files...");
			FileManager.getFileManager().mergeFiles(configurationManager.getFileDetails().getFileName());
			System.out.println("Completed Merging Files...");
		} catch (IOException e) {
			System.out.println("Unable to merge files.");
		}
	}

}
