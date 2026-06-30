package com.astock.common.util;

import com.astock.common.exception.TradingInstructionDetectedException;
import java.util.List;

public final class TradingInstructionTextChecker {
    private static final List<String> FORBIDDEN_WORDS = List.of(
            "买入", "卖出", "持有", "推荐", "强烈推荐", "必买", "目标价", "加仓", "清仓"
    );

    private TradingInstructionTextChecker() {}

    public static void assertNoTradingInstruction(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                throw new TradingInstructionDetectedException("文本包含交易指令表达：" + word);
            }
        }
    }
}
