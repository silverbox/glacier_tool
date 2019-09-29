package com.mytool.common.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.mytool.common.vo.ProgressDialogParamVo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ProgressDialogController implements Initializable {

	@FXML
	Label txt_progress_title;

	@FXML
	Label txt_progress_value;

	@FXML
	Label txt_progress_message;

	@FXML
	ProgressBar prgbar_progress_value;

	@FXML
	Button btn_save;

	private ProgressDialogParamVo param;
	private Stage secondStage;
	private boolean isIntVal = true;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void setDlgStage(Stage secondStage) {
		this.secondStage = secondStage;
	}

	public Stage getDlgStage() {
		return secondStage;
	}

	public void isIntProgressValType(boolean isIntVal) {
		this.isIntVal = isIntVal;
	}

	public void setParam(ProgressDialogParamVo param) {
		this.param = param;
		txt_progress_title.setText(param.getTitle());

		setCurrentProgress(param.getStartValue(), param.getProgressMessage());
	}

	public void setCurrentProgress(double currentProgress, String progressMessage) {
		setCurrentProgress(currentProgress);
		txt_progress_message.setText(progressMessage);
		param.setProgressMessage(progressMessage);
	}

	public void setCurrentProgress(double currentProgress) {
		prgbar_progress_value.setProgress(currentProgress / param.getEndValue());
		if (isIntVal) {
			txt_progress_value.setText(
					String.format(param.getProgressValueFormat(), (long) currentProgress, (long) param.getEndValue()));
		} else {
			prgbar_progress_value.setProgress(currentProgress / param.getEndValue());
			txt_progress_value
					.setText(String.format(param.getProgressValueFormat(), currentProgress, param.getEndValue()));
		}
	}

}
