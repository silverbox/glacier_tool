package com.mytool.common.vo;

import lombok.Data;

@Data
public class ProgressDialogParamVo {

	private String title;

	private double startValue;

	private double endValue;

	private String progressValueFormat;

	private String progressMessage;
}
