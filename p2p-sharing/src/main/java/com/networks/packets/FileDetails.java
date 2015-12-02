package com.networks.packets;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileDetails implements Serializable {
	private long fileSize;
	private long totalPieces;
	private String fileName;

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getTotalPieces() {
		return totalPieces;
	}

	public void setTotalPieces(long totalPieces) {
		this.totalPieces = totalPieces;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "FileDetails [fileSize=" + fileSize + ", totalPieces="
				+ totalPieces + ", fileName=" + fileName + "]";
	}
}
