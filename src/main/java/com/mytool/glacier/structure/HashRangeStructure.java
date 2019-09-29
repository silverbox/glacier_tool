package com.mytool.glacier.structure;

import java.util.ArrayList;
import java.util.List;

import com.mytool.glacier.vo.SizeLevelVo;
import com.mytool.glacier.vo.SizeRangeVo;

public class HashRangeStructure {
	public final static long MEGA_UNIT = 1048576L;
	
	private long size;

	private List<SizeLevelVo> hashRangeList = new ArrayList<SizeLevelVo>();

	public HashRangeStructure(long size){
		this.size = size;
		init();
	}
	
	public int getLevelCount(){
		return hashRangeList.size();
	}
	
	public SizeLevelVo getLevelVo(int level){
		return hashRangeList.get(level);
	}
	
	private void init(){
		addFirstLevel();
		recurAddRangeList(0);
	}
	
	private static final int SEL_LEVEL_CNT = 6;
	private void addFirstLevel(){
		int megaSize = (int) Math.ceil(size / MEGA_UNIT);
		int maxLevel = (int) Math.ceil(Math.log(megaSize) / Math.log(2.0));
		long posStep = MEGA_UNIT;
		if(maxLevel > SEL_LEVEL_CNT){
			posStep = MEGA_UNIT << (maxLevel - SEL_LEVEL_CNT);
		}

		List<SizeRangeVo> firstRangeList = new ArrayList<SizeRangeVo>();
		for(long pos = 0;pos < size;pos += posStep){
			SizeRangeVo wkRange = new SizeRangeVo();
			wkRange.setStart(pos);
			if(pos + posStep < size){
				wkRange.setEnd(pos + posStep - 1);
			}else{
				wkRange.setEnd(size - 1);
			}
			firstRangeList.add(wkRange);
		}
		SizeLevelVo firstLevelList = new SizeLevelVo();
		firstLevelList.setHashRangeList(firstRangeList);
		firstLevelList.setRangeLevel(0);
		firstLevelList.setRangeStep(posStep);
		hashRangeList.add(firstLevelList);
	}
	
	private void recurAddRangeList(int level){
		List<SizeRangeVo> thisLevelRangeList = hashRangeList.get(level).getHashRangeList();
		if(thisLevelRangeList.size() <= 1){
			return;
		}
		List<SizeRangeVo> nextLevelRangeList = new ArrayList<SizeRangeVo>();
		int thisLvlSize = thisLevelRangeList.size();
		for(int thisLvlIdx = 0;thisLvlIdx < thisLvlSize;thisLvlIdx += 2){
			SizeRangeVo wkRange = new SizeRangeVo();
			wkRange.setStart(thisLevelRangeList.get(thisLvlIdx).getStart());
			if(thisLvlIdx + 1 < thisLvlSize){
				wkRange.setEnd(thisLevelRangeList.get(thisLvlIdx + 1).getEnd());
			}else{
				wkRange.setEnd(thisLevelRangeList.get(thisLvlIdx).getEnd());
			}
			nextLevelRangeList.add(wkRange);
			//System.out.println("wkRange=" + wkRange.getStart() + " - " + wkRange.getEnd());
		}
		//System.out.println("Level=" + level);
		
		SizeLevelVo nextLevelVo = new SizeLevelVo();
		nextLevelVo.setHashRangeList(nextLevelRangeList);
		nextLevelVo.setRangeLevel(level + 1);
		long step = nextLevelRangeList.get(0).getEnd() + 1;
		nextLevelVo.setRangeStep(step);

		hashRangeList.add(nextLevelVo);
		recurAddRangeList(nextLevelVo.getRangeLevel());
	}
}
