package com.mytool.common.util;

import com.sun.glass.ui.Application;

public class CommonUtils {

	public static String getAppName() {
		String mainClassName = Application.GetApplication().getName();
		String[] wk = mainClassName.split("\\.");
		return wk[wk.length - 1];
	}
	
	public static boolean isEmptyStr(String target){
		return target == null || target.length() == 0;
		
	}
}
