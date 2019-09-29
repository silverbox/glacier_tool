package com.mytool.glacier.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SizeLevelVo {
	
	private int rangeLevel;
	
	private long rangeStep;

	private List<SizeRangeVo> hashRangeList = new ArrayList<SizeRangeVo>();

}
