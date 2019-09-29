package com.mytool.glacier.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.amazonaws.services.glacier.model.InventoryRetrievalJobDescription;

import lombok.Data;

@Data
public class JobInfoVo {
	
	private String jobId;

	private String jobDescription;

	private String action;

	private String creationDate;

	private Boolean completed;

	private String completionDate;
	private String statusCode;
	
	private String retrievalByteRange;

	private String archiveId;
	private String archiveSHA256TreeHash;
	private Long archiveSizeInBytes;
	private InventoryRetrievalJobDescription inventoryRetrievalParameters;
	private Long inventorySizeInBytes;
	private String SHA256TreeHash;
	private String SNSTopic;
	private String statusMessage;
	private String tier;
	private String vaultARN;

	public String getDecodedDescription() throws UnsupportedEncodingException{
		return URLDecoder.decode(jobDescription, "UTF-8");
	}
	
	public void setDecodedDescription(String dummy) {
	}
}
