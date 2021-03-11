package com.sipsd.flow.common.page;

public enum ORDERBY {
	DESC, ASC;

	public ORDERBY reverse() {
		return (this == ASC) ? DESC : ASC;
	}
}
