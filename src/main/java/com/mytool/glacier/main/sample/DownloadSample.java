package com.mytool.glacier.main.sample;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;

public class DownloadSample {

	public static String vaultName = "TestVault";

	public static String archiveId = "xx";
	public static String downloadFilePath = "D:\\TEMP\\20170527111725_1_jobtest.mp4";
	public static String jobId = "yy";

	public static String archiveIdRange = "zz";
	public static String jobIdRange = "tt";
	public static String downloadRangeFilePath = "D:\\TEMP\\20170708_part0_jobtest.part";

	public static AmazonGlacierClient glacierClient;

	public static void main(String[] args) throws IOException {
		glacierClient = (AmazonGlacierClient) AmazonGlacierClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1)
				.withCredentials(new ProfileCredentialsProvider()).build();

		// simpleDownload();
		// startRetrieveJob();
		// jobDefineDownload();
		// startRangeSpecifyRetrieveJob();
		jobDefineRangeSpecifyDownload();
	}

	private static void simpleDownload() {
		try {
			ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = atmb.withGlacierClient(glacierClient).build();

			atm.download(vaultName, archiveId, new File(downloadFilePath));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private static void startRetrieveJob() {
		InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultName)
				.withJobParameters(new JobParameters().withType("archive-retrieval").withArchiveId(archiveId));

		// it will takes around 4 hours.
		InitiateJobResult initJobResult = glacierClient.initiateJob(initJobRequest);

		String jobId = initJobResult.getJobId();
		System.out.println("job ID: " + jobId);
		System.out.println("job Info String: " + initJobResult.toString());
	}

	private static void startRangeSpecifyRetrieveJob() {
		InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultName)
				.withJobParameters(new JobParameters().withType("archive-retrieval").withArchiveId(archiveIdRange)
						.withRetrievalByteRange("0-10485759"));

		// it will takes around 4 hours.
		InitiateJobResult initJobResult = glacierClient.initiateJob(initJobRequest);

		String jobId = initJobResult.getJobId();
		System.out.println("job ID: " + jobId);
		System.out.println("job Info String: " + initJobResult.toString());
	}

	private static void jobDefineDownload() {
		try {
			ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = atmb.withGlacierClient(glacierClient).build();

			atm.downloadJobOutput(null, vaultName, jobId, new File(downloadFilePath));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private static void jobDefineRangeSpecifyDownload() {
		try {
			ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = atmb.withGlacierClient(glacierClient).build();

			atm.downloadJobOutput(null, vaultName, jobIdRange, new File(downloadRangeFilePath));
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
