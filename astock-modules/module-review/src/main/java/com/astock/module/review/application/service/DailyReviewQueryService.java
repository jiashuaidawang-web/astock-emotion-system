package com.astock.module.review.application.service;

import com.astock.module.review.api.vo.DailyReviewWorkbenchVO;
import com.astock.module.review.application.query.DailyReviewWorkbenchQuery;

public interface DailyReviewQueryService {
    DailyReviewWorkbenchVO queryWorkbench(DailyReviewWorkbenchQuery query);
}
