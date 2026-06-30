package com.astock.infrastructure.dataquality;

import com.astock.common.data.DataQualityCheckVO;
import com.astock.common.data.PageDataQualityVO;
import com.astock.common.data.RequiredSnapshot;
import com.astock.infrastructure.dataquality.entity.DataQualityCheckLogEntity;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 数据质量查询服务实现。
 *
 * <p>运行时快照存在性检查仍走 ClickHouse，已落库的检查日志查询走 MyBatis-Plus，避免手写 SQL 与
 * data_quality_check_log 表结构不一致。</p>
 */
@Service
public class DataQualityQueryServiceImpl implements DataQualityQueryService {

    /** ClickHouse 快照存在性仓储。 */
    private final SnapshotExistenceRepository snapshotExistenceRepository;

    /** 数据质量检查日志 MyBatis-Plus Mapper。 */
    private final DataQualityCheckLogMapper dataQualityCheckLogMapper;

    /**
     * 创建数据质量查询服务。
     *
     * @param snapshotExistenceRepository 快照存在性仓储
     * @param dataQualityCheckLogMapper 数据质量检查日志 Mapper
     */
    public DataQualityQueryServiceImpl(SnapshotExistenceRepository snapshotExistenceRepository,
                                       DataQualityCheckLogMapper dataQualityCheckLogMapper) {
        this.snapshotExistenceRepository = snapshotExistenceRepository;
        this.dataQualityCheckLogMapper = dataQualityCheckLogMapper;
    }

    /**
     * 检查页面依赖快照在指定交易日是否完整。
     *
     * @param pageCode 页面编码
     * @param tradeDate 交易日
     * @param marketScope 市场范围
     * @param requiredSnapshots 页面依赖快照清单
     * @return 页面数据质量视图
     */
    @Override
    public PageDataQualityVO checkPage(String pageCode, LocalDate tradeDate, String marketScope, List<RequiredSnapshot> requiredSnapshots) {
        PageDataQualityVO page = new PageDataQualityVO();
        page.setPageCode(pageCode);
        page.setTradeDate(tradeDate);

        List<DataQualityCheckVO> checks = new ArrayList<>();
        boolean complete = true;

        for (RequiredSnapshot snapshot : requiredSnapshots) {
            long count = snapshotExistenceRepository.countByTradeDate(snapshot.getTableName(), tradeDate, marketScope);
            DataQualityCheckVO check = new DataQualityCheckVO();
            check.setTradeDate(tradeDate);
            check.setDataDomain(snapshot.getDataDomain());
            check.setDataDomainName(snapshot.getDataDomain());
            check.setCheckStatus(count > 0 ? "PASSED" : "MISSING");
            check.setCritical(snapshot.isCritical());
            check.setExpectedCount(1);
            check.setActualCount((int) Math.min(count, Integer.MAX_VALUE));
            check.setMissingCount(count > 0 ? 0 : 1);
            check.setCompletenessRatio(count > 0 ? BigDecimal.ONE : BigDecimal.ZERO);
            check.setImpactPage(snapshot.isCritical() && count == 0);
            check.setCheckText(count > 0 ? "快照已生成：" + snapshot.getTableName() : "快照缺失：" + snapshot.getTableName());
            checks.add(check);
            if (snapshot.isCritical() && count == 0) {
                complete = false;
            }
        }

        page.setChecks(checks);
        page.setDataComplete(complete);
        page.setDataStatusText(complete ? "页面关键快照完整" : "页面关键快照缺失，禁止返回完整可信结论");
        return page;
    }

    /**
     * 查询已经持久化的数据质量检查记录。
     *
     * @param tradeDate 交易日
     * @return 数据质量检查视图列表
     */
    @Override
    public List<DataQualityCheckVO> queryPersistedChecks(LocalDate tradeDate) {
        return dataQualityCheckLogMapper.selectList(Wrappers.<DataQualityCheckLogEntity>lambdaQuery()
                        .eq(DataQualityCheckLogEntity::getTradeDate, tradeDate)
                        .eq(DataQualityCheckLogEntity::getIsDeleted, 0)
                        .orderByDesc(DataQualityCheckLogEntity::getCritical)
                        .orderByAsc(DataQualityCheckLogEntity::getSnapshotTable))
                .stream()
                .map(this::toView)
                .toList();
    }

    /**
     * 将数据质量日志实体转换为页面视图对象。
     *
     * @param entity 数据质量日志实体
     * @return 页面视图对象
     */
    private DataQualityCheckVO toView(DataQualityCheckLogEntity entity) {
        boolean dataComplete = Integer.valueOf(1).equals(entity.getDataComplete());
        boolean critical = Integer.valueOf(1).equals(entity.getCritical());
        String dataDomain = entity.getSnapshotCode() == null || entity.getSnapshotCode().isBlank()
                ? entity.getSnapshotTable()
                : entity.getSnapshotCode();

        DataQualityCheckVO view = new DataQualityCheckVO();
        view.setTradeDate(entity.getTradeDate());
        view.setDataDomain(dataDomain);
        view.setDataDomainName(dataDomain);
        view.setCheckStatus(entity.getCheckStatus());
        view.setCritical(critical);
        view.setExpectedCount(1);
        view.setActualCount(dataComplete ? 1 : 0);
        view.setMissingCount(dataComplete ? 0 : 1);
        view.setCompletenessRatio(entity.getCompletenessRatio());
        view.setImpactPage(critical && !dataComplete);
        view.setCheckText(entity.getMissingReason() == null || entity.getMissingReason().isBlank()
                ? "快照检查：" + entity.getSnapshotTable()
                : entity.getMissingReason());
        return view;
    }
}
