package com.mytool.glacier.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown=true)
@Data
public class InventoryInfoVo {

	@JsonProperty("VaultARN")
	private String vaultARN;

	@JsonProperty("InventoryDate")
	private String inventoryDate;

	@JsonProperty("ArchiveList")
	private List<ArchiveInfoVo> archiveList;
	
//	public String getVaultARN(){
//		return VaultARN;
//	}
//	
//	public void setVaultARN(String VaultARN){
//		this.VaultARN = VaultARN;
//	}
//	
//	public String getInventoryDate(){
//		return InventoryDate;
//	}
//	
//	public void setInventoryDate(String InventoryDate){
//		this.InventoryDate = InventoryDate;
//	}
//	
//	public List<ArchiveInfoVo> getArchiveList(){
//		return ArchiveList;
//	}
//	
//	public void setArchiveList(List<ArchiveInfoVo> ArchiveList){
//		this.ArchiveList = ArchiveList;
//	}
}
