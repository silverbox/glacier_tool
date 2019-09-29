package com.mytool.glacier.dialog.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.mytool.common.util.CommonUtils;
import com.mytool.common.vo.ProgressDialogParamVo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadFileInfoDlgController implements Initializable {
	private final String TITLE_FILE_UPLOAD = "Select file to upload";

	private final FileChooser fc = new FileChooser();

	@FXML
	TextField txt_upload_localfile;

	@FXML
	TextField txt_upload_description;

	@FXML
	Button btn_save;

	private ProgressDialogParamVo param;
	private Stage secondStage;
	private boolean isOkClose = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fc.setTitle(TITLE_FILE_UPLOAD);
	}

	@FXML
	protected void onOk(ActionEvent ev) {
		isOkClose = true;
		this.secondStage.close();
	}

	@FXML
	protected void onSelectLocalFile(ActionEvent ev) {
		String oldPath = txt_upload_localfile.getText();
		File oldFile = new File(oldPath);
		String oldName = null;
		String iniDir = System.getProperty("user.dir");
		if (!CommonUtils.isEmptyStr(oldPath)) {
			if (oldFile.exists()) {
				if (oldFile.isDirectory()) {
					iniDir = oldFile.getAbsolutePath();
				} else {
					iniDir = oldFile.getParent();
					oldName = oldFile.getName();
				}
			}
		}
		fc.setInitialDirectory(new File(iniDir));

		File importFile = fc.showOpenDialog(((Node) ev.getSource()).getScene().getWindow());
		if (importFile == null) {
			return;
		}
		txt_upload_localfile.setText(importFile.getAbsolutePath());
		String newFileName = importFile.getName();
		if (CommonUtils.isEmptyStr(txt_upload_description.getText())
				|| txt_upload_description.getText().equals(oldName)) {
			txt_upload_description.setText(newFileName);
		}
	}

	public void showModal() {
		isOkClose = false;
		secondStage.showAndWait();
	}

	public String getSelectedFilePath() {
		if (isOkClose) {
			return txt_upload_localfile.getText();
		} else {
			return null;
		}
	}

	public String getDescription() {
		return txt_upload_description.getText();
	}

	public void setDlgStage(Stage secondStage) {
		this.secondStage = secondStage;
	}

	public Stage getDlgStage() {
		return secondStage;
	}
}
