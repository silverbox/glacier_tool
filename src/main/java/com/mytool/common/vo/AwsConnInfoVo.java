package com.mytool.common.vo;

import com.amazonaws.regions.Regions;
import com.mytool.common.type.AwsConnInfoType;

import lombok.Data;

@Data
public class AwsConnInfoVo {

	private Regions regions = Regions.DEFAULT_REGION;

	private AwsConnInfoType awsConnInfoType = AwsConnInfoType.DEFAULT;

	private String configFilePath = "";

	private String configPropertyName = "";

	private String accessKeyId = "";

	private String secretAccessKey = "";
}
