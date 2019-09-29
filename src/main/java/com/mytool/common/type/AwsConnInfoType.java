package com.mytool.common.type;

public enum AwsConnInfoType {

	DEFAULT("default"), PROFILE("profile"), DIRECT("direct");

	private final String str;

	AwsConnInfoType(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

	public static AwsConnInfoType getByStr(String str) {
		for (AwsConnInfoType connInfoType : AwsConnInfoType.values()) {
			if (connInfoType.getStr().equals(str)) {
				return connInfoType;
			}
		}
		return null;
	}
}
