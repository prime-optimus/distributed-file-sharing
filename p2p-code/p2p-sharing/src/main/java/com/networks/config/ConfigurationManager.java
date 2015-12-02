package com.networks.config;

import com.networks.packets.FileDetails;

public class ConfigurationManager {
	
	private Configuration configuration;
	private FileDetails fileDetails;
	
	public Configuration getConfiguration() {
		return configuration;
	}
	
	public FileDetails getFileDetails() {
		return fileDetails;
	}
	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	private static ConfigurationManager configurationManager;
	public static ConfigurationManager getConfigurationManager() {
		if (configurationManager == null){
			configurationManager = new ConfigurationManager();
			configurationManager.configuration = new Configuration();
		}
		return configurationManager;
	}
	
}
