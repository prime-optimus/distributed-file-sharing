package com.networks.filehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.networks.config.ConfigurationManager;
import com.networks.packets.FileDetails;

public class FileManager {
	private static final int CHUNK_SIZE = 100000;
	
	public void splitFile(String fileName) throws IOException{
		File file = getFileFromName(fileName);
		File directory = createDirectoryForTempFiles(file);
		
		FileInputStream fileInputStream = new FileInputStream(file);
		byte chunk[] = new byte[CHUNK_SIZE];
		
		int chunkNumber = 0, bytesRead = 0;
		while((bytesRead = fileInputStream.read(chunk))  > 0){
			String chunkFilePath = directory.getAbsolutePath() + "\\" + (chunkNumber++) + ".bin";
			
			FileOutputStream  fileOutputStream= new FileOutputStream(chunkFilePath);
			
			fileOutputStream.write(Arrays.copyOfRange(chunk, 0, bytesRead));
			
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		fileInputStream.close();
	}

	public void mergeFiles(String fileName) throws IOException{
		File file = new File(getDirectoryPath() + FilenameUtils.removeExtension(fileName));
		File[] allFiles = file.listFiles();
		
		FileOutputStream fileOutputStream = new FileOutputStream(getDirectoryPath() + fileName);
		for (int i=0; i<allFiles.length; i++){
			FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath() + "\\"+i+".bin");
			IOUtils.copy(fileInputStream, fileOutputStream);
			fileInputStream.close();
		}
		fileOutputStream.flush();
		fileOutputStream.close();
	}
	
	public byte[] getChunkData(String fileName, long chunkId) throws IOException{
		byte[] chunkData = null;
		
		File file = new File(getDirectoryPath() + FilenameUtils.removeExtension(fileName) + "\\"+ chunkId + ".bin");
		if (file.exists()){
			chunkData = FileUtils.readFileToByteArray(file);
		}
		return chunkData;
	}
	
	public void writeChunkData(String fileName, long chunkId, byte[] data) throws IOException{
		String chunkDirectoryPath = getDirectoryPath() + FilenameUtils.removeExtension(fileName);
		File chunkPath = new File(chunkDirectoryPath);
		FileUtils.forceMkdir(chunkPath);
		
		String chunkFilePath =  chunkDirectoryPath + "\\"+ chunkId + ".bin";
		FileOutputStream  fileOutputStream= new FileOutputStream(chunkFilePath);
		
		IOUtils.write(data, fileOutputStream);
		IOUtils.closeQuietly(fileOutputStream);
	}
	
	public List<Long> getAcquiredChunkList(String fileName){
		List<Long> acquiredChunks = new ArrayList<>();
		
		String chunkDirectoryPath = getDirectoryPath() + FilenameUtils.removeExtension(fileName);
		File directory = new File(chunkDirectoryPath);
		
		File[] allFiles = directory.listFiles();
		for(File file : allFiles){
			String currentChunkFileName = file.getName();
			acquiredChunks.add(Long.parseLong(StringUtils.replace(currentChunkFileName, ".bin", "")));
		}
		return acquiredChunks;
	}
	
	private File createDirectoryForTempFiles(File file) throws IOException {
		String directoryName = file.getParentFile().getAbsolutePath() + "\\" 
							+ FilenameUtils.removeExtension(file.getName());
		File directory = new File(directoryName);
		FileUtils.deleteQuietly(directory);
		FileUtils.forceMkdir(directory);
		return directory;
		
	}

	public static void main(String[] args) throws IOException {
		String fileName = "input.pdf";
		FileManager fileManager = new FileManager();
		//fileManager.splitFile(fileName);
		System.out.println(fileManager.getAcquiredChunkList(fileName).size());
		System.out.println("Done");
	}
	
	private static FileManager fileManager;
	public static FileManager getFileManager(){
		if (fileManager == null){
			fileManager = new FileManager();
		}
		return fileManager;
	}

	public FileDetails getFileDetails(String fileName) {
		File file = getFileFromName(fileName);
		
		FileDetails fileDetails = new FileDetails();
		fileDetails.setFileName(fileName);
		fileDetails.setFileSize(file.length());
		fileDetails.setTotalPieces(getTotalPieces(file)); 
		return fileDetails;
	}

	private long getTotalPieces(File file) {
		long length = file.length();
		return (length % CHUNK_SIZE != 0) ? ((length / CHUNK_SIZE)+ 1) : (length/CHUNK_SIZE);
	}

	public File getFileFromName(String fileName) {
		File file = new File(getDirectoryPath() + fileName);
		return file;
	}

	private static String getDirectoryPath() {
		return ConfigurationManager.getConfigurationManager().getConfiguration().getDirectoryPath();
	}

}
