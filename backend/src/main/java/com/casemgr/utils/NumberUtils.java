package com.casemgr.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NumberUtils {
	public static String generateFormNumber(String prefix) {
    	String formNumber="";

		String dateFormat = "yyMM";
				
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String midfix = sdf.format(new Date());

		 Random random = new Random();
	     int number = random.nextInt(900000) + 1; // 確保是 6 碼數字
	     String ramdonSix = String.format("%06d", number);
		
		formNumber = prefix + midfix + ramdonSix;
		return formNumber;
    }
}
