package com.unicom.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageHelper;
import com.unicom.common.IConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分页工具类
 *
 * @author zhouhengsheng
 */
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 总记录数
	 */
	private int totalCount;
	/**
	 * 每页记录数
	 */
	private int pageSize;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 当前页数
	 */
	private int currPage;
	/**
	 * 列表数据
	 */
	private List<?> list;

	/**
	 * 分页
	 *
	 * @param list       列表数据
	 * @param totalCount 总记录数
	 * @param pageSize   每页记录数
	 * @param currPage   当前页数
	 */
	public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
	}

	/**
	 * 分页
	 */
	public PageUtils(IPage<?> page) {
		this.list = page.getRecords();
		this.totalCount = (int) page.getTotal();
		this.pageSize = (int) page.getSize();
		this.currPage = (int) page.getCurrent();
		this.totalPage = (int) page.getPages();
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}


	public static void page(Map<String, Object> parm, int count) {
		String pageindex = parm.get("pageNum") + "";
		String pageSize = parm.get("pageRow") + "";
		int pg = getPageIndex(pageindex);
		int sz = getPageSize(pageSize);
		int all = count / sz + 1;
		if (pg >= all) {
			pg = all;
		}
		int startNumber = (pg - 1) * sz;
		parm.put("offSet", startNumber);
		parm.put("pageRow", sz);
	}

	public static void page(Map<String, Object> parm) {
		String pageindex = parm.get("pageSize") + "";
		String pageSize = parm.get("pageRow") + "";
		Integer pg = getPageIndex(pageindex);
		Integer sz = getPageSize(pageSize);
		parm.put("pageNum", pg);
		parm.put("pageSize", sz);

		PageHelper.startPage(parm);
	}

	public static int getPageIndex(String pageIndex) {
		if (StringUtils.isNotEmpty(pageIndex) && NumberUtils.isCreatable(pageIndex)) {
			return Integer.parseInt(pageIndex);

		}

		return IConstants.DEFAULT_PAGEINDEX;
	}


	public static int getPageSize(String pageSize) {
		if (StringUtils.isNotEmpty(pageSize) && NumberUtils.isCreatable(pageSize)) {
			return Integer.parseInt(pageSize);

		}

		return IConstants.DEFAULT_PAGESIZE;
	}

}
