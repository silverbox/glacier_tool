package com.mytool.glacier.biz;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.model.DeleteArchiveResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.glacier.transfer.UploadResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytool.glacier.vo.ArchiveInfoVo;
import com.mytool.glacier.vo.InventoryInfoVo;
import com.mytool.glacier.vo.JobInfoVo;
import com.sun.glass.ui.Application;

public class GlacierInventoryInfoDao {

	private final static String JOB_TYPE_INVENTORY = "inventory-retrieval";
	private final static String JOB_TYPE_ARCHIVE = "archive-retrieval";
	private final static String JOB_TYPE_ARCHIVE_CAMEL = "ArchiveRetrieval";

	private final static String SUFFIX_INVENTORY_FILE = "_inventory.json";

	private String inventoryFineName;
	private AmazonGlacierClient client;
	private DescribeVaultOutput vaultInfo;

	public GlacierInventoryInfoDao(AmazonGlacierClient client, DescribeVaultOutput vaultInfo) {
		super();
		this.client = client;
		this.vaultInfo = vaultInfo;
		String appName = getAppName();
		// StringBuilder filePath = new
		// StringBuilder(Application.GetApplication().getDataDirectory());
		StringBuilder filePath = new StringBuilder(System.getProperty("user.dir"));
		String fileSep = java.io.File.separator;
		filePath.append(fileSep).append(appName).append("_").append(getVaultName(vaultInfo))
				.append(SUFFIX_INVENTORY_FILE);
		inventoryFineName = filePath.toString();
	}

	public InventoryInfoVo readFromFile() {
		ObjectMapper mapper = new ObjectMapper();
		// System.out.println(inventoryFineName);
		InventoryInfoVo inventoryInfoVo = null;
		try {
			inventoryInfoVo = mapper.readValue(new File(inventoryFineName), InventoryInfoVo.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inventoryInfoVo;
	}

	public void saveToFile(InventoryInfoVo inventoryInfo) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(inventoryFineName), inventoryInfo);
	}

	private String getAppName() {
		String mainClassName = Application.GetApplication().getName();
		String[] wk = mainClassName.split("\\.");
		return wk[wk.length - 1];
	}

	private static final int VAULT_INFO_ARN_CODE_INDEX = 5;

	public static String getVaultName(DescribeVaultOutput vaultInfo) {
		String[] infoAry = vaultInfo.getVaultARN().split(":");
		return infoAry[VAULT_INFO_ARN_CODE_INDEX].replaceAll("/", "_");
	}

	public InitiateJobResult executeInventoryRetrivalJob() {
		InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultInfo.getVaultName())
				.withJobParameters(
						new JobParameters().withType(JOB_TYPE_INVENTORY).withDescription(vaultInfo.getVaultName()));

		return client.initiateJob(initJobRequest);
	}

	public ArchiveInfoVo uploadFile(File file, String description, ProgressListener uploadlistener)
			throws AmazonServiceException, AmazonClientException, FileNotFoundException {
		ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
		ArchiveTransferManager atm = atmb.withGlacierClient(client).build();
		String storeDescription = String.format("%1$s [%2$s]", description, (new Date()));
		UploadResult uploadResult = atm.upload(null, vaultInfo.getVaultName(), storeDescription, file, uploadlistener);
		// System.out.println("Archive ID: " + uploadResult.getArchiveId());// TODO

		ArchiveInfoVo targetArchive = new ArchiveInfoVo();
		targetArchive.setArchiveDescription(storeDescription);
		targetArchive.setArchiveId(uploadResult.getArchiveId());
		targetArchive.setSize(file.length());
		targetArchive.setCreationDate((new Date()).toString());

		return targetArchive;
	}

	public InitiateJobResult executeArchiveRetrivalJob(ArchiveInfoVo targetArchive, long start, long end) {
		JobParameters jobParams = new JobParameters();
		jobParams.withType(JOB_TYPE_ARCHIVE).withArchiveId(targetArchive.getArchiveId())
				.withDescription(targetArchive.getArchiveDescription());
		if (start >= 0) {
			jobParams.withRetrievalByteRange(getRangeStr(start, end));
		}
		InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultInfo.getVaultName())
				.withJobParameters(jobParams);

		// it will takes around 4 hours.
		return client.initiateJob(initJobRequest);
	}

	public DeleteArchiveResult executeArchiveRemoveJob(ArchiveInfoVo targetArchive) {
		DeleteArchiveRequest delRequest = new DeleteArchiveRequest().withVaultName(vaultInfo.getVaultName())
				.withArchiveId(targetArchive.getArchiveId());

		return client.deleteArchive(delRequest);
	}

	public InventoryInfoVo getInventoryInfo(JobInfoVo jobInfo) {
		GetJobOutputRequest getJobOutputRequest = new GetJobOutputRequest();
		getJobOutputRequest.setVaultName(vaultInfo.getVaultName());
		getJobOutputRequest.setJobId(jobInfo.getJobId());
		GetJobOutputResult getJobOutputResult = client.getJobOutput(getJobOutputRequest);

		System.out.println("start get joboutput");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			InputStream inputStream = getJobOutputResult.getBody();
			while (true) {
				int len = inputStream.read(buffer);
				if (len < 0) {
					break;
				}
				bout.write(buffer, 0, len);
			}
			byte[] outByte = bout.toByteArray();
			String jsonStr = new String(outByte, "UTF-8");
			// System.out.println("job sdk result string1: " + (jsonStr));
			// System.out.println("job sdk result string2: " + outByte.toString());

			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonStr, InventoryInfoVo.class);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bout.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean isArchiveJob(JobInfoVo jobInfo) {
		return jobInfo.getAction().equals(JOB_TYPE_ARCHIVE_CAMEL);
	}

	public static String getRangeStr(long start, long end) {
		// long endlong;
		// if (end < 0 && targetArchive != null) {
		// endlong = targetArchive.getSize() - 1;
		// } else {
		// endlong = end * HashRangeStructure.MEGA_UNIT - 1;
		// }
		return String.format("%1$d-%2$d", start, end);
	}
}
