package com.mytool.common.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class MessageUtils {

	public static void showNormalMessage(String message) {
		// TODO
		Alert alert = new Alert(AlertType.INFORMATION, "", ButtonType.CLOSE);
		alert.setTitle("Message");
		alert.getDialogPane().setHeaderText(message);
		alert.getDialogPane().setContentText(message);
		alert.showAndWait();
	}

	public static void showWarningMessage(String message) {
		// TODO
		Alert alert = new Alert(AlertType.WARNING, "", ButtonType.CLOSE);
		alert.setTitle("Warning");
		alert.getDialogPane().setHeaderText(message);
		alert.getDialogPane().setContentText(message);
		alert.showAndWait();
	}

	public static void showExceptionMessage(Throwable e) {
		// TODO
		Alert alert = new Alert(AlertType.ERROR, "", ButtonType.CLOSE);
		alert.setTitle("Error");
		alert.getDialogPane().setHeaderText(e.getLocalizedMessage());
		alert.getDialogPane().setContentText(getTraseMessage(e));
		alert.showAndWait();
	}

	public static String getTraseMessage(Throwable e) {
		StringBuilder sb = new StringBuilder();
		int cnt = 0;
		for (StackTraceElement st : e.getStackTrace()) {
			String msg = String.format("%1$s(%2$d)%3$s", st.getClassName(), st.getLineNumber(), "\n");
			sb.append(msg);
			cnt++;
			if (cnt > 30) {
				sb.append(" and more... ");
				break;
			}
		}
		return sb.toString();
	}

}
