package com.sipsd.flow.common.page;


import java.io.Serializable;
import java.util.Map;

public class Query implements Serializable {
  private static final long serialVersionUID = 817880730448759944L;
  
  private int pageIndex;
  
  private int pageSize = 20;
  
  private int pageNum;
  
  private Map<String, ORDERBY> sqlOrderBy;
  
  private String sortField;
  
  private String sortOrder;
  
  public String getSortField() {
    return this.sortField;
  }
  
  public void setSortField(String sortField) {
    this.sortField = sortField;
  }
  
  public String getSortOrder() {
    return this.sortOrder;
  }
  
  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }
  
  public Query() {}
  
  public Query(int pageSize) {
    this.pageSize = pageSize;
  }
  
  public int getPageSize() {
    return this.pageSize;
  }
  
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
  
  public int getPageIndex() {
    if (this.pageIndex < 0) {
      this.pageIndex = 1;
    } else {
      this.pageIndex++;
    } 
    return this.pageIndex;
  }
  
  public int getInitPageIndex() {
    if (this.pageIndex < 0)
      this.pageIndex = 1; 
    return this.pageIndex;
  }
  
  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
  }
  
  public Map<String, ORDERBY> getSqlOrderBy() {
    return this.sqlOrderBy;
  }
  
  public void setSqlOrderBy(Map<String, ORDERBY> sqlOrderBy) {
    this.sqlOrderBy = sqlOrderBy;
  }
  
  public int getPageNum() {
    if (this.pageNum < 0)
      this.pageNum = 1; 
    return this.pageNum;
  }
  
  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }
}
