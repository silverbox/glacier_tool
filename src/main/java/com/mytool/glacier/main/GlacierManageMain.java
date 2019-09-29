package com.mytool.glacier.main;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import com.mytool.glacier.vo.ArchiveInfoVo;
import com.mytool.glacier.vo.InventoryInfoVo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class GlacierManageMain extends Application {
	private static final String APP_TITLE = "Glacier Manage Tool";
	private static final String DEFAULT_VAULT_NAME = "TestVault";

	private String vaultName = DEFAULT_VAULT_NAME;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		prepareControl(stage);
		stage.show();

		// AwsConnInfoService connInfoService = new AwsConnInfoService();
		// AmazonGlacierClient amazonGlacierClient =
		// connInfoService.getGlacierClient();
		//
		// GlacierVaultInfoDao glacierVaultInfoDao = new
		// GlacierVaultInfoDao(amazonGlacierClient);
		// List<DescribeVaultOutput> vaultInfoList =
		// glacierVaultInfoDao.getVaultInfoList();
		// if(vaultInfoList.size() > 0){
		// DescribeVaultOutput targetVault = vaultInfoList.get(0);
		//
		// GlacierInventoryInfoDao dao = new
		// GlacierInventoryInfoDao(amazonGlacierClient, targetVault);
		// InventoryInfoVo inventoryInfoVo = dao.readFromFile();
		// debugInventoryInfo(inventoryInfoVo);
		// }

		// TODO test
		// getJobInfoList(amazonGlacierClient, DEFAULT_VAULT_NAME);
	}

	private void prepareControl(Stage stage) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("javafx/GlacierManagePortal.fxml"));
		SplitPane new_pane = (SplitPane) fxmlLoader.load();

		Scene scene = new Scene(new_pane);
		stage.setTitle(APP_TITLE);
		stage.setScene(scene);
	}
	//
	// // TODO test
	// private static void debugInventoryInfo(InventoryInfoVo inventoryInfoVo) {
	// System.out.println("debugInventoryInfo valutARN : " + inventoryInfoVo.getVaultARN());
	// System.out.println("debugInventoryInfo InventoryDate : " + inventoryInfoVo.getInventoryDate());
	// List<ArchiveInfoVo> infoList = inventoryInfoVo.getArchiveList();
	// for (ArchiveInfoVo info : infoList) {
	// System.out.println("job sdk result ArchiveId : " + info.getArchiveId());
	// System.out.println("job sdk result ArchiveDesc : " + info.getArchiveDescription());
	// System.out.println("job sdk result Size : " + info.getSize());
	// System.out.println("job sdk result SHA256Tree : " + info.getSha256TreeHash());
	// }
	// }
	//
	// // TODO test
	// private static void getJobInfoList(AmazonGlacierClient amazonGlacierClient, String vaultName) {
	// ListJobsRequest listJobsRequest = new ListJobsRequest();
	// listJobsRequest.setVaultName(vaultName);
	// ListJobsResult listJobsResult = amazonGlacierClient.listJobs(listJobsRequest);
	// List<GlacierJobDescription> jobDescList = listJobsResult.getJobList();
	// System.out.println("job sdk result jobCount : " + jobDescList.size());
	// for (GlacierJobDescription jobDesc : jobDescList) {
	// System.out.println("job sdk result toStr : " + jobDesc.toString());
	// System.out.println("job sdk result jobId : " + jobDesc.getJobId());
	// System.out.println("job sdk result jobDesc : " + jobDesc.getJobDescription());
	// System.out.println("job sdk result Action : " + jobDesc.getAction());
	// System.out.println("job sdk result CreateDate: " + jobDesc.getCreationDate());
	// System.out.println("job sdk result CompDate : " + jobDesc.getCompletionDate());
	// System.out.println("job sdk result Completed : " + jobDesc.getCompleted());
	// System.out.println("job sdk result StsCode : " + jobDesc.getStatusCode());
	// System.out.println("job sdk result StsMsg : " + jobDesc.getStatusMessage());
	// System.out.println("job sdk result SNSTopic : " + jobDesc.getSNSTopic());
	// }
	// }
}
