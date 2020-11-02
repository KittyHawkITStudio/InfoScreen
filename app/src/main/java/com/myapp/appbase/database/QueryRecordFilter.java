package com.myapp.appbase.database;

/** 查询的筛选条件 */
public class QueryRecordFilter {

	String startDate;
	String endDate;

	public QueryRecordFilter() {
	}

	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
