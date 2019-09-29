package com.mytool.glacier.main.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

public class GetInfoSample {

	public static String vaultName = "TestVault";
	public static final String jobId = "yyy";

	public static AmazonGlacierClient client;

	public static void main(String[] args) throws IOException {
		client = (AmazonGlacierClient) AmazonGlacierClientBuilder.standard()
				.withRegion(Regions.AP_NORTHEAST_1)
				.withCredentials(new ProfileCredentialsProvider())
				.build();

		// getVaultInfo();

		// startGetInventoryInfoJob();
		// getInventoryInfo(jobId);

		getJobInfoList();
	}

	private static void getVaultInfo() {
		try {
			ListVaultsRequest listVaultsRequest = new ListVaultsRequest();
			ListVaultsResult listVaultsResult = client.listVaults(listVaultsRequest);

			List<DescribeVaultOutput> vaultList = listVaultsResult.getVaultList();
			System.out.println("\nDescribing all vaults (vault list):");
			for (DescribeVaultOutput vault : vaultList) {
				System.out.println(
						"\nCreationDate: " + vault.getCreationDate() +
								"\nLastInventoryDate: " + vault.getLastInventoryDate() +
								"\nNumberOfArchives: " + vault.getNumberOfArchives() +
								"\nSizeInBytes: " + vault.getSizeInBytes() +
								"\nVaultARN: " + vault.getVaultARN() +
								"\nVaultName: " + vault.getVaultName());
			}
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	private static void startGetInventoryInfoJob() {
		InitiateJobRequest initJobRequest = new InitiateJobRequest()
				.withVaultName(vaultName)
				.withJobParameters(
						new JobParameters().withType("inventory-retrieval"));

		// it will takes around 4 hours.
		InitiateJobResult initJobResult = client.initiateJob(initJobRequest);

		String jobId = initJobResult.getJobId();
		System.out.println("job ID: " + jobId);
		System.out.println("job Info String: " + initJobResult.toString());
	}

	private static void getInventoryInfo(String jobId) {
		try {

			GetJobOutputRequest getJobOutputRequest = new GetJobOutputRequest();
			getJobOutputRequest.setVaultName(vaultName);
			getJobOutputRequest.setJobId(jobId);
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
				System.out.println("job sdk result string1: " + (new String(outByte, "UTF-8")));
				System.out.println("job sdk result string2: " + outByte.toString());
			} finally {
				bout.close();
			}
			System.out.println("job sdk result http: " + getJobOutputResult.getSdkHttpMetadata());
			System.out.println("job sdk result response: " + getJobOutputResult.getSdkResponseMetadata());
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private static void getJobInfoList() {
		ListJobsRequest listJobsRequest = new ListJobsRequest();
		listJobsRequest.setVaultName(vaultName);
		ListJobsResult listJobsResult = client.listJobs(listJobsRequest);
		List<GlacierJobDescription> jobDescList = listJobsResult.getJobList();
		for (GlacierJobDescription jobDesc : jobDescList) {
			System.out.println("job sdk result toStr     : " + jobDesc.toString());
			System.out.println("job sdk result jobId     : " + jobDesc.getJobId());
			System.out.println("job sdk result jobDesc   : " + jobDesc.getJobDescription());
			System.out.println("job sdk result Action    : " + jobDesc.getAction());
			System.out.println("job sdk result CreateDate: " + jobDesc.getCreationDate());
			System.out.println("job sdk result CompDate  : " + jobDesc.getCompletionDate());
			System.out.println("job sdk result Completed : " + jobDesc.getCompleted());
			System.out.println("job sdk result StsCode   : " + jobDesc.getStatusCode());
			System.out.println("job sdk result StsMsg    : " + jobDesc.getStatusMessage());
			System.out.println("job sdk result SNSTopic  : " + jobDesc.getSNSTopic());
		}
	}
	// GetVaultAccessPolicyRequest policyRequest = new GetVaultAccessPolicyRequest();
	// policyRequest.setVaultName(vaultName);
	// GetVaultAccessPolicyResult policyResult = amazonGlacierClient.getVaultAccessPolicy(policyRequest);
	// if (policyResult.getPolicy() != null){
	// return true;
	// }
}
