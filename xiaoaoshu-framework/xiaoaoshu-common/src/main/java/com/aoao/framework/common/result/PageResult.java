package com.aoao.framework.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author aoao
 * @create 2025-08-18-22:07
 */
public class PageResult<T> implements Serializable  {
    /**
     * 总记录数
     */
    private long total = 0L;

    /**
     * 每页显示的记录数，默认每页显示 10 条
     */
    private long size = 10L;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 总页数
     */
    private long pages;

    private List<T> list;




}
