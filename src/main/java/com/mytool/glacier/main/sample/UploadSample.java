package com.mytool.glacier.main.sample;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.glacier.transfer.UploadResult;

public class UploadSample {

	public static String vaultName = "TestVault";
	public static String archiveToUpload = "D:\\test\\20170527111725_1.mp4";

	public static AmazonGlacierClient client;

	public static void main(String[] args) throws IOException {
		client = (AmazonGlacierClient) AmazonGlacierClientBuilder.standard()
				.withRegion(Regions.AP_NORTHEAST_1)
				.withCredentials(new ProfileCredentialsProvider())
				.build();
		try {
			ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = atmb.withGlacierClient(client).build();
			UploadResult result = atm.upload(vaultName, "my archive " + (new Date()), new File(archiveToUpload));
			System.out.println("Archive ID: " + result.getArchiveId());
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
