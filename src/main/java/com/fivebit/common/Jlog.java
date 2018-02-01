package com.fivebit.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Jlog {
	
	public static Logger logger;
	public static String filename = "";
	public static int linenum = 0;
	public static String classname = "";
	public static String requestid = "";
	
	
	public static Logger _get(){
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		int i = 3;	//this is the caller function
		classname = stack[i].getClassName();
		//methodname = stack[i].getMethodName();
		//filename = stack[i].getFileName();
		linenum = stack[i].getLineNumber();
		//logger = LoggerFactory.getLogger(classname);
		logger = LoggerFactory.getLogger(Jlog.classname);
		if(requestid == ""){
			requestid=UUID.randomUUID().toString().replace("-", "").substring(0,15);
		}
		return logger;
	}
	public static void _print(int type,Object msg){
		Logger logger = _get();
		boolean is_string = msg instanceof String;
		if( is_string == false){
			msg = msg.toString();
		}
		msg = linenum+" "+requestid+" "+ msg.toString();
		//msg = ""+linenum+" "+ msg.toString();
		switch(type){
		case 0:
			logger.debug((String) msg);
			break;
		case 1:
			logger.info(msg.toString());
			break;
		case 2:
			logger.error(msg.toString());
			break;
		case 3:
			logger.warn(msg.toString());
			break;
		}
	}
	
	public static void debug(Object msg){
		_print(0,msg);
	}
	public static void info(Object msg){
		_print(1,msg);
	}
	public static void error(Object msg){
		_print(2,msg);
	}
	public static void fatal(Object msg){
		_print(3,msg);
	}

}
