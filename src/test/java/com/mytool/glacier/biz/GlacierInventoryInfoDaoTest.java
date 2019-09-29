package com.mytool.glacier.biz;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

public class GlacierInventoryInfoDaoTest {

	@Test
	public void rangeFormatTest() throws IOException {
		long start = 0;
		long end = 5242879999L;
		assertEquals(GlacierInventoryInfoDao.getRangeStr(start, end), "0-5242879999");
	}

	@Test
	public void testReplace(){
		String orgStr = "abc&&dddd";
		assertEquals(orgStr.replaceAll("&&", ""), "abcdddd");
	}
}
