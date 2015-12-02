package com.networks.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.networks.config.Client;
import com.networks.handlers.FileRequestGenerator;
import com.networks.handlers.FileRequestHandler;

public class ServerUtils {

	public static Runnable startFileServer(Client serverToStart, int totalClientToServe) {
		final int totalClients = totalClientToServe;
		
		Runnable server = () -> {
			ServerSocket serverSocket = null;
			try {
				System.out.println("Starting server with id " + serverToStart.getId());
				serverSocket = new ServerSocket(serverToStart.getPort());

				for(int i=0; i<totalClients; i++) {
					Socket clientSocket = serverSocket.accept();
					startFileRequestHandler(clientSocket);
				} 
				System.out.println("Closing server with id " + serverToStart.getId());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(serverSocket);
			}
		};
		return server;
	}
	
	public static Runnable startFileRequestGenerator(Client serverToConnect, Client currentClient){
		Runnable client = () -> {
			Socket socket = null;
			
			while (socket == null){
				try {
					System.out.println("Trying to connect to neighbor(id: " + serverToConnect.getId() +")");
					socket = new Socket(serverToConnect.getIpAddress(), serverToConnect.getPort());
					FileRequestGenerator fileRequestGenerator = new FileRequestGenerator(socket, serverToConnect, currentClient);
					
					fileRequestGenerator.startRequestingChunks();
					fileRequestGenerator.requestTermination();
					System.out.println("Closing connection to neighbor(id: " + serverToConnect.getId() +")");
				} catch (IOException e) {
					sleepForSomeTime();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(socket);
				}
			}
		};
		return client;
	}
	
	private static void startFileRequestHandler(Socket clientSocket) {
		Runnable runnable = () -> {
			try {
				FileRequestHandler fileRequestHandler = new FileRequestHandler(clientSocket);

				boolean terminate = false;
				while(!terminate){
					terminate= fileRequestHandler.processRequests();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		new Thread(runnable).start();
	}

	public static void sleepForSomeTime() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void awaitTerminationForCurrentTasks(ExecutorService executorService) {
		try {
			executorService.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
