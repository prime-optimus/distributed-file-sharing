package com.networks.packets;

@SuppressWarnings("serial")
public class TerminationPacket extends Packet {
	private boolean terminate;

	public boolean isTerminate() {
		return terminate;
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	@Override
	public String toString() {
		return "TerminationPacket";
	}
	
}
