package com.networks.handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.LongStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.networks.config.Client;
import com.networks.config.ConfigurationManager;
import com.networks.filehandler.FileManager;
import com.networks.packets.FileChunkDataPacket;
import com.networks.packets.FileDetails;
import com.networks.packets.FileDetailsPacket;
import com.networks.packets.Packet;
import com.networks.packets.TerminationPacket;
import com.networks.utils.ServerUtils;

public class FileRequestGenerator {
	private Socket socket;
	private Client otherServer;
	private Client currentClient;
	
	private ObjectInputStream  objectInputStream;
	private ObjectOutputStream objectOutputStream;
	
	private List<Long> missingPieces;
	
	public FileRequestGenerator(Socket socket, Client client, Client currentClient) throws IOException {
		this.socket = socket;
		this.otherServer = client;
		this.currentClient = currentClient;
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
	}
	
	public void startRequestingChunks() throws ClassNotFoundException, IOException{
		do {
			List<Long> toRequestChunks = requestFileDetails();
			System.out.println("Requesting Chunks: " +  getString(toRequestChunks));
			requestChunks(toRequestChunks);
			
			ServerUtils.sleepForSomeTime();
		} while(CollectionUtils.isNotEmpty(missingPieces));
	}

	private String getString(List<Long> list) {
		return StringUtils.join(list, ",");
	}

	public List<Long> requestFileDetails() throws IOException, ClassNotFoundException {
		List<Long> chunkIds = Collections.emptyList();
		
		FileDetailsPacket fileDetailsPacket = new FileDetailsPacket();
		fileDetailsPacket.setPacketType(Packet.PACKET_TYPE_REQUEST);
		writeObjectToSocket(fileDetailsPacket);
		
		Object responseObject = readObjectFromSocket();
		if(responseObject instanceof FileDetailsPacket){
			FileDetailsPacket detailsPacket = (FileDetailsPacket) responseObject;
			ConfigurationManager.getConfigurationManager().setFileDetails(detailsPacket.getFileDetails());
			intializeMissingPiecesListIfNeeded(detailsPacket);
			chunkIds = getRemainingChunkIds(detailsPacket);
		}
		return chunkIds;
	}
	
	private void intializeMissingPiecesListIfNeeded(FileDetailsPacket detailsPacket) {
		if (missingPieces == null){
			missingPieces = new ArrayList<>();
			
			int totalClients = ConfigurationManager.getConfigurationManager().getConfiguration().getTotalClients();
			long totalPieces = detailsPacket.getFileDetails().getTotalPieces();
			LongStream.range(0, totalPieces)
					.filter(curerntIndex -> this.otherServer.isMainServer() 
							? curerntIndex % totalClients == this.currentClient.getId()
							: curerntIndex % totalClients != this.currentClient.getId())
					.forEach(curerntIndex -> missingPieces.add(curerntIndex));				
		}
	}

	@SuppressWarnings("unchecked")
	private List<Long> getRemainingChunkIds(FileDetailsPacket detailsPacket) {
		List<Long> ramainingChunkIds = new ArrayList<Long>();

		if(this.otherServer.isMainServer()){
			int totalClients = ConfigurationManager.getConfigurationManager().getConfiguration().getTotalClients();
			long totalPieces = detailsPacket.getFileDetails().getTotalPieces();
			LongStream.range(0, totalPieces)
					.filter(curerntIndex -> curerntIndex % totalClients == this.currentClient.getId())
					.forEach(item -> ramainingChunkIds.add(item));
		} else {
			List<Long> acquiredPieces = detailsPacket.getAcquiredPieces();
			ramainingChunkIds.addAll(CollectionUtils.intersection(acquiredPieces, missingPieces));
		}
		return ramainingChunkIds;
	}

	public void requestChunks(List<Long> toRequestChunks) throws IOException, ClassNotFoundException{
		FileDetails fileDetails = ConfigurationManager.getConfigurationManager().getFileDetails();

		Iterator<Long> iterator = toRequestChunks.iterator();
		while(iterator.hasNext()){
			long chunkId = iterator.next();
			boolean response = generateChunkRequest(fileDetails, chunkId);
			if (response){
				missingPieces.remove(chunkId);
			}
		}
	}
	
	public void requestTermination() throws IOException {
		TerminationPacket terminationPacket = new TerminationPacket();
		terminationPacket.setTerminate(true);
		terminationPacket.setPacketType(Packet.PACKET_TYPE_REQUEST);
		writeObjectToSocket(terminationPacket);
	}

	private boolean generateChunkRequest(FileDetails fileDetails, long chunkId) throws IOException, ClassNotFoundException {
		boolean isSuccessful = false;
		
		FileChunkDataPacket chunkDataPacket = new FileChunkDataPacket(chunkId);
		chunkDataPacket.setPacketType(Packet.PACKET_TYPE_REQUEST);
		
		writeObjectToSocket(chunkDataPacket);
		Object responseObject = readObjectFromSocket();
		if(responseObject instanceof FileChunkDataPacket){
			FileChunkDataPacket dataPacket = (FileChunkDataPacket) responseObject;
			isSuccessful= handleResponseData(dataPacket, fileDetails, chunkId);
		}
		return isSuccessful;
	}

	private boolean handleResponseData(FileChunkDataPacket dataPacket, FileDetails fileDetails, 
			long chunkId) throws IOException {
		boolean isSuccessful = dataPacket.getStatus() == Packet.PACKET_STATUS_SUCCESSFUL;
		if(isSuccessful){
			FileManager.getFileManager().writeChunkData(fileDetails.getFileName(), chunkId, dataPacket.getChunkData());
		}
		return isSuccessful;
	}

	private Object readObjectFromSocket() throws IOException, ClassNotFoundException {
		Object responseObject = getInputStream().readObject();
		System.out.println("Read: " + responseObject);
		return responseObject;
	}

	private ObjectInputStream getInputStream() throws IOException {
		if(objectInputStream == null){
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		}
		return objectInputStream;
	}

	private void writeObjectToSocket(Packet packet) throws IOException {
		objectOutputStream.writeObject(packet);
		objectOutputStream.flush();
		System.out.println("Written: "+ packet);
	}

}
