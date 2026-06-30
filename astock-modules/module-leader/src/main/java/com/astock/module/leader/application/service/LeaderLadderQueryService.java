package com.astock.module.leader.application.service;

import com.astock.module.leader.api.vo.LeaderLadderPageVO;
import com.astock.module.leader.application.query.LeaderLadderPageQuery;

public interface LeaderLadderQueryService {
    LeaderLadderPageVO queryLadderPage(LeaderLadderPageQuery query);
}
