package com.astock.module.mainline.application.service;

import com.astock.module.mainline.api.vo.MainlineRadarPageVO;
import com.astock.module.mainline.application.query.MainlineRadarPageQuery;

public interface MainlineRadarQueryService {
    MainlineRadarPageVO queryRadar(MainlineRadarPageQuery query);
}
