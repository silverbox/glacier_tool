package com.mytool.glacier.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ArchiveInfoVo {

	@JsonProperty("ArchiveId")
	private String archiveId;

	@JsonProperty("ArchiveDescription")
	private String archiveDescription;

	@JsonProperty("CreationDate")
	private String creationDate;

	@JsonProperty("Size")
	private Long size;

	@JsonProperty("SHA256TreeHash")
	private String sha256TreeHash;

	public String getDecodedDescription() throws UnsupportedEncodingException {
		return URLDecoder.decode(archiveDescription, "UTF-8");
	}

	public void setDecodedDescription(String dummy) {
	}

	public String getThousandSepSize() {
		return String.format("%1$,3d", size);
	}

	public void setThousandSepSize(String dummy) {
	}
}
