package com.astock.module.spider.ths;

import java.util.List;

public record ThsPlatePage(int sourceTotalCount, int totalPage, int pageNo, List<ThsStockRow> rows) {
}
