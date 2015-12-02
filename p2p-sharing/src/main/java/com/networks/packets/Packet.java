package com.networks.packets;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Packet implements Serializable{
	public static int PACKET_TYPE_REQUEST = 0;
	public static int PACKET_TYPE_RESPONSE = 1;
	
	public static int PACKET_STATUS_SUCCESSFUL = 0;
	public static int PACKET_STATUS_FAILED = 1;
	
	private int packetType, status;

	public int getPacketType() {
		return packetType;
	}

	public void setPacketType(int packetType) {
		this.packetType = packetType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStingPacketType(){
		return packetType == 0 ? "Request" : "Response";
	}
	
}
