package com.networks.packets;


@SuppressWarnings("serial")
public class FileChunkDataPacket extends Packet {
	private long chunkId;
	private byte[] chunkData;
	
	public FileChunkDataPacket(long chunkId) {
		this.chunkId = chunkId;
	}

	public long getChunkId() {
		return chunkId;
	}

	public void setChunkId(long chunkId) {
		this.chunkId = chunkId;
	}

	public byte[] getChunkData() {
		return chunkData;
	}

	public void setChunkData(byte[] chunkData) {
		this.chunkData = chunkData;
	}

	@Override
	public String toString() {
		return "FileChunkDataPacket [type:" + getStingPacketType()+ " ,chunkId=" + chunkId +
			 "]";
	}
	

}
