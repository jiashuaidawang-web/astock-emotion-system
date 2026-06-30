package com.astock.infrastructure.lineage;

import com.astock.common.lineage.PageFieldLineageVO;
import java.util.List;

public interface PageFieldLineageQueryService {
    List<PageFieldLineageVO> queryByPageCode(String pageCode);
    List<PageFieldLineageVO> queryByVoClass(String pageCode, String voClassName);
}
