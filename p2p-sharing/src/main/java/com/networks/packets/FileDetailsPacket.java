package com.networks.packets;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FileDetailsPacket extends Packet{
	private FileDetails fileDetails;
	private List<Long> acquiredPieces;
	
	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}
	
	public List<Long> getAcquiredPieces() {
		if(acquiredPieces == null){
			acquiredPieces = new ArrayList<Long>();
		}
		return acquiredPieces;
	}
	public void setAcquiredPieces(List<Long> acquiredPieces) {
		this.acquiredPieces = acquiredPieces;
	}

	@Override
	public String toString() {
		return "FileDetailsPacket [type=" + getStingPacketType()+ " ,fileDetails=" + fileDetails + 
				"]";
	}
}
