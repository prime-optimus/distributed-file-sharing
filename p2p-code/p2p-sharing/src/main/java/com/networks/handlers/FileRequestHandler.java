package com.networks.handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.networks.config.ConfigurationManager;
import com.networks.filehandler.FileManager;
import com.networks.packets.FileChunkDataPacket;
import com.networks.packets.FileDetails;
import com.networks.packets.FileDetailsPacket;
import com.networks.packets.Packet;
import com.networks.packets.TerminationPacket;

public class FileRequestHandler {
	private ObjectInputStream  objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public FileRequestHandler(Socket socket) throws IOException {
		this.objectInputStream  = new ObjectInputStream(socket.getInputStream());
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
	}
	
	public boolean processRequests() throws IOException, ClassNotFoundException {
		boolean terminate = false;
		
		Object messagePacket = readObjectFromSocket();
		if (messagePacket instanceof FileDetailsPacket){
			processFileDetailsPacket((FileDetailsPacket)messagePacket);
		} else if (messagePacket instanceof FileChunkDataPacket){
			processFileChunkRequestPacket((FileChunkDataPacket)messagePacket);
		} else if (messagePacket instanceof TerminationPacket){
			terminate = processTerminationMessage((TerminationPacket)messagePacket);
		}
		return terminate;
	}

	private void processFileChunkRequestPacket(FileChunkDataPacket messagePacket) throws IOException {
		ConfigurationManager configurationManager = ConfigurationManager.getConfigurationManager();
		FileDetails fileDetails = configurationManager.getFileDetails();
		
		FileManager fileManager = FileManager.getFileManager();
		byte[] chunkData = fileManager.getChunkData(fileDetails.getFileName(), messagePacket.getChunkId());
		
		FileChunkDataPacket chunkDataPacket = new FileChunkDataPacket(messagePacket.getChunkId());
		chunkDataPacket.setPacketType(Packet.PACKET_TYPE_RESPONSE);
		chunkDataPacket.setStatus(chunkData != null ? Packet.PACKET_STATUS_SUCCESSFUL : Packet.PACKET_STATUS_FAILED);
		chunkDataPacket.setChunkData(chunkData);
		chunkDataPacket.setChunkId(messagePacket.getChunkId());
		
		writeObjectToSocket(chunkDataPacket);
	}

	private void processFileDetailsPacket(FileDetailsPacket messagePacket) throws IOException {
		ConfigurationManager configurationManager = ConfigurationManager.getConfigurationManager();
		FileDetails fileDetails = configurationManager.getFileDetails();
		
		FileDetailsPacket fileDetailsPacket = new FileDetailsPacket();
		fileDetailsPacket.setPacketType(FileDetailsPacket.PACKET_TYPE_RESPONSE);
		fileDetailsPacket.setFileDetails(fileDetails);
		fileDetailsPacket.setAcquiredPieces(getAcquiredChunkList(fileDetails));
		
		writeObjectToSocket(fileDetailsPacket);
	}

	private List<Long> getAcquiredChunkList(FileDetails fileDetails) {
		return FileManager.getFileManager().getAcquiredChunkList(fileDetails.getFileName());
	}

	private boolean processTerminationMessage(TerminationPacket terminationPacket) {
		return terminationPacket.isTerminate();
	}
	
	private Object readObjectFromSocket() throws IOException, ClassNotFoundException {
		Object responseObject = this.objectInputStream.readObject();
		System.out.println("Read:" + responseObject);
		return responseObject;
	}
	
	private void writeObjectToSocket(Packet packet) throws IOException {
		objectOutputStream.writeObject(packet);
		objectOutputStream.flush();
		System.out.println("Written:" + packet);
	}
	
}
