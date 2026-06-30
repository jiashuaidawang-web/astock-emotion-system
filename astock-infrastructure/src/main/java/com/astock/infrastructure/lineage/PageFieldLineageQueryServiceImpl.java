package com.astock.infrastructure.lineage;

import com.astock.common.lineage.PageFieldLineageVO;
import com.astock.infrastructure.lineage.entity.PageContractFieldLineageEntity;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 页面字段血缘查询服务实现。
 *
 * <p>使用 MyBatis-Plus LambdaQueryWrapper 表达查询条件，避免手写 SQL 列名和实体字段脱节。</p>
 */
@Service
public class PageFieldLineageQueryServiceImpl implements PageFieldLineageQueryService {

    /** 页面字段血缘 Mapper。 */
    private final PageFieldLineageMapper mapper;

    /**
     * 创建页面字段血缘查询服务。
     *
     * @param mapper 页面字段血缘 Mapper
     */
    public PageFieldLineageQueryServiceImpl(PageFieldLineageMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 按页面编码查询字段血缘。
     *
     * @param pageCode 页面编码
     * @return 字段血缘视图列表
     */
    @Override
    public List<PageFieldLineageVO> queryByPageCode(String pageCode) {
        return mapper.selectList(Wrappers.<PageContractFieldLineageEntity>lambdaQuery()
                        .eq(PageContractFieldLineageEntity::getPageCode, pageCode)
                        .eq(PageContractFieldLineageEntity::getIsDeleted, 0)
                        .orderByAsc(PageContractFieldLineageEntity::getVoClassName)
                        .orderByAsc(PageContractFieldLineageEntity::getId))
                .stream()
                .map(this::toView)
                .toList();
    }

    /**
     * 按页面编码和 VO 类名查询字段血缘。
     *
     * @param pageCode 页面编码
     * @param voClassName VO 类名
     * @return 字段血缘视图列表
     */
    @Override
    public List<PageFieldLineageVO> queryByVoClass(String pageCode, String voClassName) {
        return mapper.selectList(Wrappers.<PageContractFieldLineageEntity>lambdaQuery()
                        .eq(PageContractFieldLineageEntity::getPageCode, pageCode)
                        .eq(PageContractFieldLineageEntity::getVoClassName, voClassName)
                        .eq(PageContractFieldLineageEntity::getIsDeleted, 0)
                        .orderByAsc(PageContractFieldLineageEntity::getId))
                .stream()
                .map(this::toView)
                .toList();
    }

    /**
     * 将血缘实体转换为接口视图。
     *
     * @param entity 血缘实体
     * @return 字段血缘视图
     */
    private PageFieldLineageVO toView(PageContractFieldLineageEntity entity) {
        PageFieldLineageVO view = new PageFieldLineageVO();
        view.setPageCode(entity.getPageCode());
        view.setPageName(entity.getPageCode());
        view.setVoClassName(entity.getVoClassName());
        view.setFieldName(entity.getFieldName());
        view.setFieldComment(entity.getRemark());
        view.setSourceType(entity.getSourceType());
        view.setSourceTable(entity.getSourceTable());
        view.setSourceColumn(entity.getSourceColumn());
        view.setCalculationFormula(entity.getCalculationFormula());
        view.setRequired(Integer.valueOf(1).equals(entity.getRequired()));
        view.setAuditPassed(Integer.valueOf(1).equals(entity.getAuditPassed()));
        return view;
    }
}
