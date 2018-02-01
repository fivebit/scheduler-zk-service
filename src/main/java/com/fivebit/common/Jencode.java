package com.fivebit.common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * 编码与解码，包括加密函数等。
 */

public final class Jencode {
	public static String skey = "topzedu.com!0.0!";
	public static String sEncode(String str) {
		String newSkey = new StringBuffer(Jencode.skey).reverse().toString();
		String base64Str = Jencode.getBase64(str);
		Jlog.info("xxxxx:"+base64Str);
		int count = base64Str.length();
		StringBuffer strbuf = new StringBuffer(base64Str);
		for (int i = 0; i < newSkey.length(); i++) {
			if(i < count){
				strbuf.replace(i*2, i*2+1, ""+base64Str.charAt(i)+newSkey.charAt(i));
			}
		}
		return strbuf.toString().replace("=", "O0O0O");
	}
	
	public static String sDecode(String str) {
		String newSkey = new StringBuffer(Jencode.skey).reverse().toString();
		String base64Str = str.replace("O0O0O", "=");
		int count = base64Str.length();
		StringBuffer strbuf = new StringBuffer(base64Str);
		for (int i = 0; i < newSkey.length(); i++) {
			if(i < count/2 && (i*2+2-i) < count){
				strbuf.delete(i*2+1-i, i*2+2-i);
				//if(i >= 1) break;
			}
		}
		return Jencode.getFromBase64(strbuf.toString()); 
	}
	
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}