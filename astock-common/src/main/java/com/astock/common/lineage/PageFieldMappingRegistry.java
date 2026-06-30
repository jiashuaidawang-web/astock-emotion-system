package com.astock.common.lineage;

import java.util.List;

public interface PageFieldMappingRegistry {
    String pageCode();
    String voClassName();
    List<FieldMapping> mappings();
}
