package com.sipsd.flow.common;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date createTime;

	private String creator;

	private Date updateTime;

	private String updator;

	private Integer delFlag = Integer.valueOf(0);

}
