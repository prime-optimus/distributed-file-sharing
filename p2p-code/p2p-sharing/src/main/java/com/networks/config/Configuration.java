package com.networks.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.networks.utils.CommonUtils;

public class Configuration {
	private int totalClients;
	private List<Client> clients;
	private Client server;
	private String directoryPath = "f:\\p2p\\";
	
	public Configuration() {
		intializeConfiguration();
		this.totalClients = clients.size();
	}

	private void intializeConfiguration() {
		try {
			Scanner in = new Scanner(new FileInputStream("config.txt"));
			
			this.directoryPath = in.nextLine();
			
			this.server = new Client(in.next(), in.nextInt(), -1, -1);
			this.server.setMainServer(true);
			
			this.clients = new ArrayList<>();
			while(in.hasNext()){
				Client client = new Client(in.next(), in.nextInt(), in.nextInt(), in.nextInt());
				this.clients.add(client);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public int getTotalClients() {
		return totalClients;
	}

	public List<Client> getClients() {
		return clients;
	}

	public Client getServer() {
		return server;
	}
	
	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public Client getClientWithId(int id){
		return CommonUtils.findBeanObjectFromCollection(clients, "id", id);
	}
	
}
