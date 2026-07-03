package com.astock.module.spider.dc;

import com.astock.module.spider.common.JsonpParser;
import com.astock.module.spider.common.SpiderHttpClient;
import com.astock.module.spider.dc.model.DcPageResult;
import com.astock.module.spider.dc.model.DcPoolResult;
import com.astock.module.spider.dc.model.DcSinglePageResult;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DcApiClient {

    private static final int PAGE_SIZE = 100;
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String PLATE_STOCK_URL = "https://push2.eastmoney.com/weblogin/api/qt/clist/get?np=1&fltt=1&invt=2&cb=jQuery37103197788499441794_1782544819942&fs=b%%3Abk%s%%2Bf%%3A!50&fields=f12%%2Cf13%%2Cf14%%2Cf1%%2Cf2%%2Cf4%%2Cf3%%2Cf152%%2Cf5%%2Cf6%%2Cf7%%2Cf15%%2Cf18%%2Cf16%%2Cf17%%2Cf10%%2Cf8%%2Cf9%%2Cf23&fid=f3&pn=%d&pz=100&po=1&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=3308306092050116%%7C0%%7C1%%7C0%%7Cweb&_=%d";

    private final SpiderHttpClient httpClient;
    private final JsonpParser jsonpParser;

    public DcPageResult fetchPaged(DcEndpoint endpoint) {
        return fetchPaged(page -> String.format(endpoint.getUrlTemplate(), page, System.currentTimeMillis()));
    }

    public DcSinglePageResult fetchPage(DcEndpoint endpoint, int page) {
        return fetchPage(page, String.format(endpoint.getUrlTemplate(), page, System.currentTimeMillis()));
    }

    public DcPageResult fetchPlateStocks(String plateCode) {
        String bkNumber = plateCode == null ? "" : plateCode.replace("BK", "");
        return fetchPaged(page -> String.format(PLATE_STOCK_URL, bkNumber, page, System.currentTimeMillis()));
    }

    public DcSinglePageResult fetchPlateStocksPage(String plateCode, int page) {
        String bkNumber = plateCode == null ? "" : plateCode.replace("BK", "");
        return fetchPage(page, String.format(PLATE_STOCK_URL, bkNumber, page, System.currentTimeMillis()));
    }

    public DcPoolResult fetchPool(DcEndpoint endpoint, LocalDate tradeDate) {
        String url = String.format(endpoint.getUrlTemplate(), tradeDate.format(BASIC_DATE), System.currentTimeMillis());
        try {
            JsonNode root = jsonpParser.parse(httpClient.get(url));
            JsonNode data = root.path("data");
            int total = data.path("tc").asInt(0);
            LocalDate queryDate = parseQueryDate(data.path("qdate").asText(""));
            List<JsonNode> rows = new ArrayList<>();
            data.path("pool").forEach(rows::add);
            return new DcPoolResult(total, queryDate == null ? tradeDate : queryDate, rows);
        } catch (IOException e) {
            throw new IllegalStateException("东方财富股票池接口解析失败: " + endpoint.name(), e);
        }
    }

    private DcPageResult fetchPaged(PageUrlBuilder urlBuilder) {
        int total = 0;
        List<JsonNode> allRows = new ArrayList<>();
        int totalPage = 1;
        for (int page = 1; page <= totalPage; page++) {
            try {
                JsonNode root = jsonpParser.parse(httpClient.get(urlBuilder.build(page)));
                JsonNode data = root.path("data");
                total = data.path("total").asInt(total);
                totalPage = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
                data.path("diff").forEach(allRows::add);
            } catch (IOException e) {
                throw new IllegalStateException("东方财富分页接口解析失败, page=" + page, e);
            }
        }
        return new DcPageResult(total, allRows);
    }

    private DcSinglePageResult fetchPage(int page, String url) {
        try {
            JsonNode root = jsonpParser.parse(httpClient.get(url));
            JsonNode data = root.path("data");
            int total = data.path("total").asInt(0);
            int totalPage = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            List<JsonNode> rows = new ArrayList<>();
            data.path("diff").forEach(rows::add);
            return new DcSinglePageResult(total, totalPage, page, rows);
        } catch (IOException e) {
            throw new IllegalStateException("东方财富分页接口解析失败, page=" + page, e);
        }
    }

    private LocalDate parseQueryDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value, BASIC_DATE);
    }

    private interface PageUrlBuilder {
        String build(int page);
    }
}
