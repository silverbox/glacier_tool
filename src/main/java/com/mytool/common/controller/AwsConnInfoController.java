package com.mytool.common.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.amazonaws.regions.Regions;
import com.mytool.common.biz.AwsConnInfoDao;
import com.mytool.common.type.AwsConnInfoType;
import com.mytool.common.vo.AwsConnInfoVo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AwsConnInfoController implements Initializable {

	@FXML
	TextField txt_aws_conn_config_region;

	@FXML
	RadioButton rb_aws_conn_default;

	@FXML
	RadioButton rb_aws_conn_filespecify;

	@FXML
	RadioButton rb_aws_conn_direct;

	@FXML
	TextField txt_aws_conn_config_file_path;

	@FXML
	TextField txt_aws_conn_config_property_name;

	@FXML
	TextField txt_aws_conn_access_key_id;

	@FXML
	TextField txt_aws_conn_secret_access_key;

	@FXML
	Button btn_save;

	private AwsConnInfoDao awsConnInfoDao = new AwsConnInfoDao();

	private Stage secondStage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			setAwsConnInfo(awsConnInfoDao.getAwsConnInfo());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAwsConnInfo(AwsConnInfoVo awsConnInfo) {
		txt_aws_conn_config_region.setText(awsConnInfo.getRegions().getName());

		switch (awsConnInfo.getAwsConnInfoType()) {
		case PROFILE:
			rb_aws_conn_filespecify.setSelected(true);
			break;
		case DIRECT:
			rb_aws_conn_direct.setSelected(true);
			break;
		default:
			rb_aws_conn_default.setSelected(true);
		}
		txt_aws_conn_config_file_path.setText(awsConnInfo.getConfigFilePath());
		txt_aws_conn_config_property_name.setText(awsConnInfo.getConfigPropertyName());
		txt_aws_conn_access_key_id.setText(awsConnInfo.getAccessKeyId());
		txt_aws_conn_secret_access_key.setText(awsConnInfo.getSecretAccessKey());
	}

	public void setStage(Stage stage) {
		secondStage = stage;
	}

	@FXML
	protected void onSaveBtnClick(ActionEvent ev) throws Exception {
		awsConnInfoDao.saveAwsConnInfo(getAwsConnInfoVoFromWindow());
		if (secondStage != null) {
			secondStage.close();
		}

	}

	private AwsConnInfoVo getAwsConnInfoVoFromWindow() {
		AwsConnInfoVo awsConnInfo = new AwsConnInfoVo();
		awsConnInfo.setRegions(Regions.fromName(txt_aws_conn_config_region.getText()));
		if (rb_aws_conn_filespecify.isSelected()) {
			awsConnInfo.setAwsConnInfoType(AwsConnInfoType.PROFILE);
		} else if (rb_aws_conn_direct.isSelected()) {
			awsConnInfo.setAwsConnInfoType(AwsConnInfoType.DIRECT);
		} else {
			awsConnInfo.setAwsConnInfoType(AwsConnInfoType.DEFAULT);
		}
		awsConnInfo.setConfigFilePath(getTextFromTextField(txt_aws_conn_config_file_path));
		awsConnInfo.setConfigPropertyName(getTextFromTextField(txt_aws_conn_config_property_name));
		awsConnInfo.setAccessKeyId(getTextFromTextField(txt_aws_conn_access_key_id));
		awsConnInfo.setSecretAccessKey(getTextFromTextField(txt_aws_conn_secret_access_key));

		return awsConnInfo;
	}

	private String getTextFromTextField(TextField textField) {
		if (textField.getText() == null) {
			return "";
		}
		return textField.getText();
	}
}
