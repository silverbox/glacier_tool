package com.mytool.common.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.amazonaws.regions.Regions;
import com.mytool.common.type.AwsConnInfoType;
import com.mytool.common.vo.AwsConnInfoVo;
import com.sun.glass.ui.Application;

public class AwsConnInfoDao {

	private final static String PROPFILE_COMMENT = "AWS Connection information";
	private final static String SUFFIX_INIFILE = "_conninfo.properties";

	private final static String PROPKEY_CONNINFO_REGION = "region";
	private final static String PROPKEY_CONNINFO_TYPE = "infotype";
	private final static String PROPKEY_CONNINFO_FILEPATH = "filepath";
	private final static String PROPKEY_CONNINFO_PROPNAME = "propname";
	private final static String PROPKEY_CONNINFO_ACCESS_KEYID = "accesskeyid";
	private final static String PROPKEY_CONNINFO_ACCESS_SECRETKEY = "secretaccesskey";

	private Properties prop = null;
	private String iniFineName;

	public AwsConnInfoDao() {
		super();
		String appName = getAppName();
		// StringBuilder filePath = new StringBuilder(Application.GetApplication().getDataDirectory());
		StringBuilder filePath = new StringBuilder(System.getProperty("user.dir"));
		String fileSep = java.io.File.separator;
		filePath.append(fileSep).append(appName).append(SUFFIX_INIFILE);
		iniFineName = filePath.toString();
		System.out.println("readInifile:" + iniFineName);
	}

	public AwsConnInfoVo getAwsConnInfo() throws IOException {
		if (prop == null) {
			readPropertiesFromFile();
		}
		// Stub
		AwsConnInfoVo awsConnInfoVo = new AwsConnInfoVo();
		Regions region = Regions
				.fromName(prop.getProperty(PROPKEY_CONNINFO_REGION, Regions.DEFAULT_REGION.getName())); // Regions.AP_NORTHEAST_1.getName()
		awsConnInfoVo.setRegions(region);
		awsConnInfoVo.setAwsConnInfoType(
				AwsConnInfoType.getByStr(prop.getProperty(PROPKEY_CONNINFO_TYPE, AwsConnInfoType.DEFAULT.getStr())));
		awsConnInfoVo.setConfigFilePath(prop.getProperty(PROPKEY_CONNINFO_FILEPATH, iniFineName));
		awsConnInfoVo.setConfigPropertyName(prop.getProperty(PROPKEY_CONNINFO_PROPNAME));
		awsConnInfoVo.setAccessKeyId(prop.getProperty(PROPKEY_CONNINFO_ACCESS_KEYID));
		awsConnInfoVo.setSecretAccessKey(prop.getProperty(PROPKEY_CONNINFO_ACCESS_SECRETKEY));
		return awsConnInfoVo;
	}

	public void saveAwsConnInfo(AwsConnInfoVo awsConnInfoVo) throws IOException {
		prop.setProperty(PROPKEY_CONNINFO_REGION, awsConnInfoVo.getRegions().getName());
		prop.setProperty(PROPKEY_CONNINFO_TYPE, awsConnInfoVo.getAwsConnInfoType().getStr());
		prop.setProperty(PROPKEY_CONNINFO_FILEPATH, awsConnInfoVo.getConfigFilePath());
		prop.setProperty(PROPKEY_CONNINFO_PROPNAME, awsConnInfoVo.getConfigPropertyName());
		prop.setProperty(PROPKEY_CONNINFO_ACCESS_KEYID, awsConnInfoVo.getAccessKeyId());
		prop.setProperty(PROPKEY_CONNINFO_ACCESS_SECRETKEY, awsConnInfoVo.getSecretAccessKey());
		OutputStream os = new FileOutputStream(iniFineName);
		try {
			prop.store(os, PROPFILE_COMMENT);
		} finally {
			os.close();
		}

	}

	private void readPropertiesFromFile() throws IOException {
		prop = new Properties();

		File chkWk = new File(iniFineName);
		if (chkWk.exists()) {
			InputStream is = new FileInputStream(iniFineName);
			try {
				prop.load(is);
			} finally {
				is.close();
			}
		} else {
			AwsConnInfoVo awsConnInfoVo = new AwsConnInfoVo();
			saveAwsConnInfo(awsConnInfoVo);
		}
	}

	private String getAppName() {
		String mainClassName = Application.GetApplication().getName();
		String[] wk = mainClassName.split("\\.");
		return wk[wk.length - 1];
	}
}
