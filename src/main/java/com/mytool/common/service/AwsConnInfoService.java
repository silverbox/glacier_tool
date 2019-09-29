package com.mytool.common.service;

import java.io.IOException;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.mytool.common.biz.AwsConnInfoDao;
import com.mytool.common.controller.AwsConnInfoController;
import com.mytool.common.vo.AwsConnInfoVo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AwsConnInfoService {
	private static final String SYSPROP_KEY_AWSKEYID = "aws.accessKeyId";
	private static final String SYSPROP_KEY_AWSSECRETKEY = "aws.secretKey";

	private AwsConnInfoDao awsConnInfoDao = new AwsConnInfoDao();

	public AmazonGlacierClient getGlacierClient() throws IOException {
		ProfileCredentialsProvider profileCredentialsProvider = null;
		AwsConnInfoVo awsConnInfoVo = awsConnInfoDao.getAwsConnInfo();
		switch (awsConnInfoVo.getAwsConnInfoType()) {
		case DIRECT:
			java.lang.System.setProperty(SYSPROP_KEY_AWSKEYID, awsConnInfoVo.getAccessKeyId());
			java.lang.System.setProperty(SYSPROP_KEY_AWSSECRETKEY, awsConnInfoVo.getSecretAccessKey());
			// this choice need instance creation by following
		case DEFAULT:
			profileCredentialsProvider = new ProfileCredentialsProvider();
			break;
		case PROFILE:
			profileCredentialsProvider = new ProfileCredentialsProvider(awsConnInfoVo.getConfigFilePath(),
					awsConnInfoVo.getConfigPropertyName());
			break;
		}

		AmazonGlacierClient clientPro = (AmazonGlacierClient) AmazonGlacierClientBuilder.standard()
				.withRegion(awsConnInfoVo.getRegions()).withCredentials(profileCredentialsProvider).build();
		return clientPro;
	}

	public void openInfoSetDialog(Window owner) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mytool/common/javafx/AwsConnInfoDlg.fxml"));
		ScrollPane new_pane = (ScrollPane) fxmlLoader.load();
		AwsConnInfoController wk = (AwsConnInfoController) fxmlLoader.getController();
		Scene scene = new Scene(new_pane);
		Stage secondStage = new Stage();
		secondStage.setTitle("Second Stage");
		secondStage.setScene(scene);
		wk.setStage(secondStage);
		// 以下の2行を加えると、モーダルopenになる
		secondStage.initModality(Modality.WINDOW_MODAL);
		secondStage.initOwner(owner);
		secondStage.show();
	}
}
