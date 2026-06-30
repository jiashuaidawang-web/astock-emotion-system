package com.astock.module.sector.application.service;

import com.astock.module.sector.api.vo.SectorStrengthPageVO;
import com.astock.module.sector.application.query.SectorStrengthPageQuery;

public interface SectorStrengthQueryService {
    SectorStrengthPageVO queryStrengthPage(SectorStrengthPageQuery query);
}
