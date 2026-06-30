package com.astock.common.api;

import java.util.List;
import lombok.Data;

/**
 * PageResult 数据载体。
 */
@Data
public class PageResult<T> {
    /** pageNo 字段。 */
    private Integer pageNo;
    /** pageSize 字段。 */
    private Integer pageSize;
    /** total 字段。 */
    private Long total;
    /** totalPage 字段。 */
    private Integer totalPage;
    /** records 字段。 */
    private List<T> records;

    public PageResult() {}

    public PageResult(Integer pageNo, Integer pageSize, Long total, List<T> records) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        this.totalPage = pageSize == null || pageSize == 0 ? 0 : (int) Math.ceil(total * 1.0 / pageSize);
    }
}
