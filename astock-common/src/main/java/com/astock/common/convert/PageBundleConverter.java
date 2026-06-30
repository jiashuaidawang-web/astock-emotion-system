package com.astock.common.convert;

import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.PageSnapshotBundle;

public interface PageBundleConverter<Q, V> {
    V convert(Q query, PageDataQualityVO quality, PageSnapshotBundle bundle);
}
