package com.astock.module.leader.application.service;

import com.astock.module.leader.api.vo.LeaderProfilePageVO;
import com.astock.module.leader.application.query.LeaderProfilePageQuery;

public interface LeaderProfileQueryService {
    LeaderProfilePageVO queryProfile(LeaderProfilePageQuery query);
}
