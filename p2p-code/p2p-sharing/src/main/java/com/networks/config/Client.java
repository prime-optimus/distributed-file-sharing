package com.networks.config;

public class Client {
	private String ipAddress;
	private int port, id, neighborId;
	private boolean mainServer;

	public Client(String ipAddress, int port, int id, int neighborId) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.id = id;
		this.neighborId = neighborId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNeighborId() {
		return neighborId;
	}

	public void setNeighborId(int neighborId) {
		this.neighborId = neighborId;
	}

	public boolean isMainServer() {
		return mainServer;
	}

	public void setMainServer(boolean mainServer) {
		this.mainServer = mainServer;
	}

}
