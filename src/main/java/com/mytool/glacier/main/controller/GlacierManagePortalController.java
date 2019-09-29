package com.mytool.glacier.main.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DeleteArchiveResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.mytool.common.controller.ProgressDialogController;
import com.mytool.common.service.AwsConnInfoService;
import com.mytool.common.util.MessageUtils;
import com.mytool.common.vo.ProgressDialogParamVo;
import com.mytool.glacier.biz.GlacierInventoryInfoDao;
import com.mytool.glacier.biz.GlacierJobInfoDao;
import com.mytool.glacier.biz.GlacierVaultInfoDao;
import com.mytool.glacier.consts.GlacierMessage;
import com.mytool.glacier.dialog.controller.UploadFileInfoDlgController;
import com.mytool.glacier.structure.HashRangeStructure;
import com.mytool.glacier.vo.ArchiveInfoVo;
import com.mytool.glacier.vo.InventoryInfoVo;
import com.mytool.glacier.vo.JobInfoVo;
import com.mytool.glacier.vo.SizeLevelVo;
import com.mytool.glacier.vo.SizeRangeVo;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GlacierManagePortalController implements Initializable {

	private final String TITLE_FILE_UPLOAD = "File to upload";

	private final String TITLE_FILE_SAVE = "File to save";
	private final FileChooser fc = new FileChooser();

	AmazonGlacierClient amazonGlacierClient;
	GlacierInventoryInfoDao inventoryDao;
	GlacierJobInfoDao jobInfoDao;
	/*
	 * 
	 */
	@FXML
	TableView<DescribeVaultOutput> table_vault_list;

	@FXML
	TableColumn<DescribeVaultOutput, String> tcol_vault_name;

	@FXML
	TableColumn<DescribeVaultOutput, String> tcol_vault_arn;

	@FXML
	TableColumn<DescribeVaultOutput, Integer> tcol_vault_archives;

	@FXML
	TableColumn<DescribeVaultOutput, Long> tcol_vault_size;

	@FXML
	TableColumn<DescribeVaultOutput, String> tcol_vault_create_date;

	@FXML
	TableColumn<DescribeVaultOutput, String> tcol_vault_last_inventory_date;

	List<DescribeVaultOutput> vaultInfoList;
	DescribeVaultOutput selectedVaultInfo;
	JobInfoVo selectedJobInfo = null;
	/*
	 * 
	 */
	// @FXML
	// TextField txt_upload_description;

	@FXML
	TableView<ArchiveInfoVo> table_inventory_info;

	@FXML
	TableColumn<ArchiveInfoVo, String> tcol_archive_id;

	@FXML
	TableColumn<ArchiveInfoVo, String> tcol_archive_description;

	@FXML
	TableColumn<ArchiveInfoVo, String> tcol_archive_size;

	@FXML
	TableColumn<ArchiveInfoVo, String> tcol_archive_create_date;

	@FXML
	Button btn_execute_retrieve;

	@FXML
	Button btn_remove_archive;

	@FXML
	TableView<JobInfoVo> table_job_info;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_id;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_description;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_action;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_creation_date;

	@FXML
	TableColumn<JobInfoVo, Boolean> tcol_job_completed;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_completion_date;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_status;

	@FXML
	TableColumn<JobInfoVo, String> tcol_job_retrieve_range;

	// @FXML
	// TextField txt_retrieve_range_start;
	//
	// @FXML
	// TextField txt_retrieve_range_end;

	@FXML
	ComboBox<String> cmb_retrieve_range_lvl;

	@FXML
	ComboBox<String> cmb_retrieve_range_val;

	HashRangeStructure rangeStruct = null;

	@FXML
	Button btn_execute_download;

	@FXML
	Button btn_execute_refresh_inventory;

	UploadFileInfoDlgController fileInfDialog = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			AwsConnInfoService connInfoService = new AwsConnInfoService();
			amazonGlacierClient = connInfoService.getGlacierClient();
			setTableColumns();
			loadVaultInfo();
		} catch (com.amazonaws.services.glacier.model.AmazonGlacierException ae) {
			MessageUtils.showNormalMessage(GlacierMessage.getMessage(GlacierMessage.MsgId.MSGID_CONINFO_REQUIRED));
			ae.printStackTrace();
		} catch (IOException e) {
			MessageUtils.showExceptionMessage(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	protected void onVaultSelected(MouseEvent ev) throws Exception {
		ObservableList<TablePosition> posList = table_vault_list.getSelectionModel().getSelectedCells();
		if (posList != null && posList.size() > 0) {
			int col = posList.get(0).getColumn();
			int row = posList.get(0).getRow();
			if (col < 0 || row < 0) {
				return;
			}
			selectedVaultInfo = vaultInfoList.get(row);
			setDetailInfo();
		}
	}

	@FXML
	protected void onArchiveTableClick(MouseEvent ev) {
		onArchiveTableChanged();
	}

	@FXML
	protected void onJobListTableClicked(MouseEvent ev) {
		onSelectJobInfoChanged();
	}

	@FXML
	protected void doAwsConnInfoSetting(ActionEvent ev) throws Exception {
		AwsConnInfoService aAwsConnInfoService = new AwsConnInfoService();
		aAwsConnInfoService.openInfoSetDialog(((Node) ev.getSource()).getScene().getWindow());

		AwsConnInfoService connInfoService = new AwsConnInfoService();
		amazonGlacierClient = connInfoService.getGlacierClient();
		loadVaultInfo();

		progressTest();
	}

	@FXML
	protected void onExecuteInventoryInfoGetJob(ActionEvent ev) {
		inventoryDao.executeInventoryRetrivalJob();
		setJobInfo();
	}

	@FXML
	protected void onExecuteFileUpload(ActionEvent ev) throws IOException {
		// String desc = txt_upload_description.getText();
		// if (desc == null || desc.length() == 0) {
		// return;// TODO message
		// }
		// fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		// fc.setTitle(TITLE_FILE_UPLOAD);
		// fileInfDialog.getDlgStage().initOwner(((Node) ev.getSource()).getScene().getWindow());
		// fileInfDialog.getDlgStage().initModality(Modality.WINDOW_MODAL);
		// fileInfDialog.getDlgStage().show();
		fileInfDialog = createProgressDialog(ev);
		fileInfDialog.showModal();
		// File importFile = fc.showOpenDialog(((Node) ev.getSource()).getScene().getWindow());
		if (fileInfDialog.getSelectedFilePath() == null) {
			return;
		}
		File importFile = new File(fileInfDialog.getSelectedFilePath());
		try {
			long size = importFile.length();
			ProgressDialogParamVo param = new ProgressDialogParamVo();
			param.setTitle("Upload file");
			param.setStartValue(0);
			param.setEndValue(size);
			param.setProgressMessage(importFile.getAbsolutePath());
			param.setProgressValueFormat("%1$,3d / %2$,3d byte uploaded.");
			ProgressDialogController progressDialog = createProgressDialog(param);

			UploadThread thread = new UploadThread(importFile, fileInfDialog.getDescription(), progressDialog);
			thread.start();
			progressDialog.getDlgStage().showAndWait();
		} catch (AmazonClientException | FileNotFoundException e) {
			MessageUtils.showExceptionMessage(e);
			e.printStackTrace();
		} catch (IOException e) {
			MessageUtils.showExceptionMessage(e);
			e.printStackTrace();
		}
	}

	private class UploadThread extends Thread {
		ProgressDialogController progressDialog;
		File importFile;
		String desc;

		public UploadThread(File importFile, String desc, ProgressDialogController progressDialog) throws IOException {
			this.progressDialog = progressDialog;
			this.importFile = importFile;
			this.desc = desc;
		}

		public void run() {
			ProgressListener uploadlistener = new ProgressListener() {
				long currentSize = 0;

				@Override
				public void progressChanged(ProgressEvent progressEvent) {
					ProgressEventType type = progressEvent.getEventType();
					if (type.equals(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT)) {
						currentSize += progressEvent.getBytesTransferred();
						Platform.runLater(() -> progressDialog.setCurrentProgress(currentSize / 2));
						// System.out.println("Transferred bytes: " +
						// type.toString() + ":"
						// + progressEvent.getBytesTransferred() + ":" +
						// currentSize);
					} else {
						// System.out.println("Transferred bytes: " + type.toString() + ":"
						// + progressEvent.getBytesTransferred() + ":" + currentSize);
					}
				}
			};

			ArchiveInfoVo targetArchive;
			try {
				String descEncodedResult = URLEncoder.encode(desc, "UTF-8");
				targetArchive = inventoryDao.uploadFile(importFile, descEncodedResult, uploadlistener);
				InventoryInfoVo inventoryInfoVo = inventoryDao.readFromFile();
				inventoryInfoVo.getArchiveList().add(targetArchive);

				inventoryDao.saveToFile(inventoryInfoVo);

				Platform.runLater(() -> {
					progressDialog.getDlgStage().close();
					setInventoryInfo();
				});
			} catch (AmazonClientException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				MessageUtils.showExceptionMessage(e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MessageUtils.showExceptionMessage(e);
				e.printStackTrace();
			}
		}
	}

	private UploadFileInfoDlgController createProgressDialog(ActionEvent ev) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/com/mytool/glacier/dialog/javafx/UploadFileInfoDlg.fxml"));
		AnchorPane new_pane = (AnchorPane) fxmlLoader.load();
		Scene scene = new Scene(new_pane);

		Stage secondStage = new Stage();
		secondStage.setTitle("Fill upload file information");
		secondStage.setScene(scene);
		secondStage.initOwner(((Node) ev.getSource()).getScene().getWindow());
		secondStage.initModality(Modality.WINDOW_MODAL);

		UploadFileInfoDlgController fileInfoDialog = (UploadFileInfoDlgController) fxmlLoader.getController();
		fileInfoDialog.setDlgStage(secondStage);
		return fileInfoDialog;
	}

	private ProgressDialogController createProgressDialog(ProgressDialogParamVo param) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mytool/common/javafx/ProgressDlg.fxml"));
		AnchorPane new_pane = (AnchorPane) fxmlLoader.load();
		Scene scene = new Scene(new_pane);

		Stage secondStage = new Stage();
		secondStage.setTitle("Progress Information");
		secondStage.setScene(scene);

		ProgressDialogController progressDialog = (ProgressDialogController) fxmlLoader.getController();
		progressDialog.setParam(param);
		progressDialog.setDlgStage(secondStage);
		return progressDialog;

	}

	private void progressTest() throws IOException {
		ProgressDialogParamVo param = new ProgressDialogParamVo();
		param.setTitle("progress title");
		param.setProgressMessage("Upload file");
		param.setStartValue(0);
		param.setEndValue(100);
		param.setProgressMessage("path");
		param.setProgressValueFormat("%1$f / %2$f byte uploaded.");
		ProgressDialogController progressDialog = createProgressDialog(param);
		// secondStage.initModality(Modality.WINDOW_MODAL);
		// secondStage.initOwner(owner);
		ProgressTestTask testTask = new ProgressTestTask(progressDialog);
		Timer timer = new Timer(false);
		timer.schedule(testTask, 0, 10);
		testTask.run();

		progressDialog.getDlgStage().show();
	}

	class ProgressTestTask extends TimerTask {
		ProgressDialogController wk;
		double pg = 0;

		public ProgressTestTask(ProgressDialogController wk) {
			this.wk = wk;
		}

		public void run() {
			Platform.runLater(() -> wk.setCurrentProgress(pg));
			pg += 1;
			if (pg > 100) {
				this.cancel();
			}
		}
	}

	@FXML
	protected void onRangeLevelChange(ActionEvent ev) {
		SingleSelectionModel<String> lvlSelModel = cmb_retrieve_range_lvl.getSelectionModel();
		if (lvlSelModel.getSelectedIndex() < 0) {
			return;
		}
		int structIdx = rangeStruct.getLevelCount() - lvlSelModel.getSelectedIndex() - 1;
		SizeLevelVo sizeLevelVo = rangeStruct.getLevelVo(structIdx);
		List<SizeRangeVo> hashRangeList = sizeLevelVo.getHashRangeList();
		cmb_retrieve_range_val.getItems().clear();
		for (int idx = 0; idx < hashRangeList.size(); idx++) {
			SizeRangeVo wkVo = hashRangeList.get(idx);
			long startMB = (int) Math.ceil(wkVo.getStart() / HashRangeStructure.MEGA_UNIT);
			long endMB = (int) Math.ceil(wkVo.getEnd() / HashRangeStructure.MEGA_UNIT);
			String elem = String.format("%1$,3d - %2$,3d MB", startMB, endMB);
			cmb_retrieve_range_val.getItems().add(elem);
		}
		SingleSelectionModel<String> valSelModel = cmb_retrieve_range_val.getSelectionModel();
		valSelModel.select(0);
		cmb_retrieve_range_val.setSelectionModel(valSelModel);
	}

	@FXML
	protected void onExecuteRetrieveJob(ActionEvent ev) {
		ArchiveInfoVo targetArchive = getSelectedArchiveInfo();
		try {
			SingleSelectionModel<String> lvlSelModel = cmb_retrieve_range_lvl.getSelectionModel();
			int structIdx = rangeStruct.getLevelCount() - lvlSelModel.getSelectedIndex() - 1;
			SingleSelectionModel<String> valSelModel = cmb_retrieve_range_val.getSelectionModel();
			SizeLevelVo sizeLevelVo = rangeStruct.getLevelVo(structIdx);
			List<SizeRangeVo> hashRangeList = sizeLevelVo.getHashRangeList();
			SizeRangeVo wkVo = hashRangeList.get(valSelModel.getSelectedIndex());

			if (lvlSelModel.getSelectedIndex() == 0) {
				inventoryDao.executeArchiveRetrivalJob(targetArchive, -1, wkVo.getEnd());
			} else {
				inventoryDao.executeArchiveRetrivalJob(targetArchive, wkVo.getStart(), wkVo.getEnd());
			}
			setJobInfo();
		} catch (Throwable e) {
			MessageUtils.showExceptionMessage(e);
			throw e;
		}
	}

	@FXML
	protected void onExecuteRemoveArchiveJob(ActionEvent ev) {
		try {
			ArchiveInfoVo targetArchive = getSelectedArchiveInfo();
			DeleteArchiveResult result = inventoryDao.executeArchiveRemoveJob(targetArchive);
			InventoryInfoVo inventoryInfoVo = inventoryDao.readFromFile();
			inventoryInfoVo.getArchiveList().remove(targetArchive);

			inventoryDao.saveToFile(inventoryInfoVo);

			setInventoryInfo();
		} catch (AmazonClientException | FileNotFoundException e) {
			MessageUtils.showExceptionMessage(e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			MessageUtils.showExceptionMessage(e);
		}
	}

	@FXML
	protected void onExecuteRetrietiveDownload(ActionEvent ev) throws UnsupportedEncodingException {
		if (!GlacierInventoryInfoDao.isArchiveJob(selectedJobInfo)) {
			return;
		}
		String jobDesc = selectedJobInfo.getDecodedDescription();
		int apos = jobDesc.lastIndexOf("[");
		StringBuilder fileNameSb = new StringBuilder();
		if (apos >= 0) {
			fileNameSb.append(jobDesc.substring(0, apos).trim());
		} else {
			fileNameSb.append(jobDesc.trim());
		}
		String rangeStr = selectedJobInfo.getRetrievalByteRange();
		if (!String.format("0-%d", selectedJobInfo.getArchiveSizeInBytes()).equals(rangeStr)) {
			fileNameSb.append("_").append(rangeStr);
		}
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setInitialFileName(fileNameSb.toString());
		fc.setTitle(TITLE_FILE_SAVE);
		File importFile = fc.showSaveDialog(((Node) ev.getSource()).getScene().getWindow());
		if (importFile == null) {
			return;
		}

		try {
			ArchiveTransferManagerBuilder atmb = new ArchiveTransferManagerBuilder();
			ArchiveTransferManager atm = atmb.withGlacierClient(amazonGlacierClient).build();

			atm.downloadJobOutput(null, selectedVaultInfo.getVaultName(), selectedJobInfo.getJobId(), importFile);
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.showExceptionMessage(e);
			System.err.println(e);
		}
	}

	@FXML
	protected void onExecuteRefreshInventory() {
		if (GlacierInventoryInfoDao.isArchiveJob(selectedJobInfo)) {
			return;
		}
		try {
			inventoryDao.saveToFile(inventoryDao.getInventoryInfo(selectedJobInfo));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageUtils.showExceptionMessage(e);
		}
		setInventoryInfo();
	}

	@FXML
	protected void onExecuteReloadJobInfo(ActionEvent ev) {
		setJobInfo();
	}

	private void onArchiveTableChanged() {
		ObservableList<TablePosition> posList = table_inventory_info.getSelectionModel().getSelectedCells();
		btn_execute_retrieve.setDisable(posList.size() <= 0);
		btn_remove_archive.setDisable(posList.size() <= 0);
		if (posList.size() <= 0) {
			return;
		}
		ArchiveInfoVo targetArchive = getSelectedArchiveInfo();
		rangeStruct = new HashRangeStructure(targetArchive.getSize());
		cmb_retrieve_range_lvl.getItems().clear();
		for (int lvl = rangeStruct.getLevelCount() - 1; lvl >= 0; lvl = lvl - 1) {
			SizeLevelVo wkVo = rangeStruct.getLevelVo(lvl);
			String elem = String.format("Range %1$d : %2$,3d byte", wkVo.getRangeLevel(), wkVo.getRangeStep());
			cmb_retrieve_range_lvl.getItems().add(elem);
		}
		SingleSelectionModel<String> lvlSelModel = cmb_retrieve_range_lvl.getSelectionModel();
		lvlSelModel.select(0);
		cmb_retrieve_range_lvl.setSelectionModel(lvlSelModel);
		onRangeLevelChange(null);
	}

	private void onSelectJobInfoChanged() {
		boolean isArchiveFinish = false;
		boolean isInventoryFinish = false;

		ObservableList<TablePosition> posList = table_job_info.getSelectionModel().getSelectedCells();
		if (posList.size() > 0) {
			selectedJobInfo = table_job_info.getItems().get(posList.get(0).getRow());
			if (selectedJobInfo.getCompleted()) {
				if (GlacierInventoryInfoDao.isArchiveJob(selectedJobInfo)) {
					isArchiveFinish = true;
				} else {
					isInventoryFinish = true;
				}
			}
			// System.out.println("job hashA: " + selectedJobInfo.getSHA256TreeHash());
			// System.out.println("job hashB: " + selectedJobInfo.getArchiveSHA256TreeHash());
		}
		btn_execute_download.setDisable(!isArchiveFinish);
		btn_execute_refresh_inventory.setDisable(!isInventoryFinish);

	}

	private ArchiveInfoVo getSelectedArchiveInfo() {
		ObservableList<TablePosition> posList = table_inventory_info.getSelectionModel().getSelectedCells();
		if (posList.size() > 0) {
			return table_inventory_info.getItems().get(posList.get(0).getRow());
		}
		return null;
	}

	private void loadVaultInfo() throws IOException {

		GlacierVaultInfoDao glacierVaultInfoDao = new GlacierVaultInfoDao(amazonGlacierClient);
		vaultInfoList = glacierVaultInfoDao.getVaultInfoList();
		table_vault_list.getItems().clear();
		// System.out.println(vaultInfoList.size());
		// for(DescribeVaultOutput vaultInfo : vaultInfoList){
		// table_vault_list.getItems().add(vaultInfo);
		// }
		table_vault_list.getItems().addAll(vaultInfoList);
		if (vaultInfoList.size() == 1) {
			selectedVaultInfo = vaultInfoList.get(0);
			inventoryDao = new GlacierInventoryInfoDao(amazonGlacierClient, selectedVaultInfo);
			jobInfoDao = new GlacierJobInfoDao(amazonGlacierClient, selectedVaultInfo);

			setDetailInfo();
			// onInfoTypeSelected();
		}
	}

	private void setDetailInfo() throws IOException {
		setInventoryInfo();
		setJobInfo();
	}

	private void setTableColumns() {
		tcol_vault_name.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, String>("vaultName"));
		tcol_vault_arn.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, String>("vaultARN"));
		tcol_vault_archives
				.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, Integer>("numberOfArchives"));
		tcol_vault_size.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, Long>("sizeInBytes"));
		tcol_vault_create_date
				.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, String>("creationDate"));
		tcol_vault_last_inventory_date
				.setCellValueFactory(new PropertyValueFactory<DescribeVaultOutput, String>("lastInventoryDate"));
		tcol_vault_size.setStyle("-fx-alignment: CENTER-RIGHT");
		tcol_vault_archives.setStyle("-fx-alignment: CENTER-RIGHT");

		tcol_archive_id.setCellValueFactory(new PropertyValueFactory<ArchiveInfoVo, String>("archiveId"));
		tcol_archive_description
				.setCellValueFactory(new PropertyValueFactory<ArchiveInfoVo, String>("decodedDescription"));
		tcol_archive_size.setCellValueFactory(new PropertyValueFactory<ArchiveInfoVo, String>("thousandSepSize"));
		tcol_archive_create_date.setCellValueFactory(new PropertyValueFactory<ArchiveInfoVo, String>("creationDate"));
		tcol_archive_size.setStyle("-fx-alignment: CENTER-RIGHT");

		tcol_job_id.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("jobId"));
		tcol_job_description
				.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("decodedDescription"));
		tcol_job_action.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("action"));
		tcol_job_creation_date
				.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("creationDate"));
		tcol_job_completed.setCellValueFactory(new PropertyValueFactory<JobInfoVo, Boolean>("completed"));
		tcol_job_completion_date
				.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("completionDate"));
		tcol_job_status.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("statusCode"));
		tcol_job_retrieve_range
				.setCellValueFactory(new PropertyValueFactory<JobInfoVo, String>("retrievalByteRange"));
		// tcol_job_status.setCellValueFactory(new
		// PropertyValueFactory<GlacierJobDescription,
		// String>("statusMessage"));
	}

	private void setInventoryInfo() {
		InventoryInfoVo inventoryInfoVo = inventoryDao.readFromFile();
		table_inventory_info.getItems().clear();
		if (inventoryInfoVo != null) {
			table_inventory_info.getItems().addAll(inventoryInfoVo.getArchiveList());
			if (inventoryInfoVo.getArchiveList().size() > 0) {
				TableViewSelectionModel<ArchiveInfoVo> selModel = table_inventory_info.getSelectionModel();
				selModel.select(0);
				table_inventory_info.setSelectionModel(selModel);
			}
		}
		onArchiveTableChanged();
	}

	private void setJobInfo() {
		table_job_info.getItems().clear();
		table_job_info.getItems().addAll(jobInfoDao.getJobList());
	}
}
