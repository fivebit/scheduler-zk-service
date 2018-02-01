package com.fivebit.errorhandling;


import com.fivebit.filters.AppConstants;


/**
 * Class to map application related exceptions
 * 
 * @author fivebit
 *
 */
public class AppException extends Exception {

	private static final long serialVersionUID = -8999932578270387947L;
	
	/** 
	 * contains redundantly the HTTP status of the response sent back to the client in case of error, so that
	 * the developer does not have to look into the response headers. If null a default 
	 */
	Integer status = AppConstants.GENERIC_APP_ERROR_CODE;
	
	/** application specific error code */
	String code;

	Object data;

	/**
	 * 
	 * @param status
	 * @param code
     * @param  data
	 */
	public AppException(Integer status, String code, Object data){
		super((String) data);
		this.status = status;
		this.code = code;
	}

	public AppException(String code,Object data) {
		super((String) data);
		this.code = code;
		this.data = data;
	}
	public AppException(String code) {
		super((String) "");
		this.code = code;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "AppException{" +
				"status='" + status + '\'' +
				", code='" + code + '\'' +
				", data=" + data +
				'}';
	}

}
