#!/usr/bin/env bash
set -euo pipefail

JAVA_ROOTS=(astock-app/src/main/java astock-common/src/main/java astock-infrastructure/src/main/java astock-modules)

echo "[1/7] 检查是否存在 MyBatis 手写 SQL 注解"
if grep -R "@Select\|@Insert\|@Update\|@Delete" -n "${JAVA_ROOTS[@]}"; then
  echo "FAILED: 仍存在 MyBatis 注解 SQL，请改为 MyBatis-Plus BaseMapper/Wrapper。"
  exit 1
fi

echo "[2/7] 检查 Mapper 是否继承 BaseMapper"
BAD_MAPPER=$(grep -R -l "@Mapper" astock-app/src/main/java astock-infrastructure/src/main/java astock-modules/*/src/main/java \
  | grep -v MysqlDataSourceConfig.java \
  | while read -r f; do grep -q "extends BaseMapper" "$f" || echo "$f"; done || true)
if [ -n "$BAD_MAPPER" ]; then
  echo "FAILED: 以下 Mapper 未继承 MyBatis-Plus BaseMapper："
  echo "$BAD_MAPPER"
  exit 1
fi

echo "[3/7] 检查普通业务接口是否误加 Spring/MyBatis 注解"
BAD_INTERFACE=$(find astock-app/src/main/java astock-infrastructure/src/main/java astock-modules/*/src/main/java -name "*.java" \
  | while read -r f; do
      if grep -q "interface " "$f" && grep -q "@Mapper\|@Component\|@Service\|@Repository" "$f"; then
        case "$f" in
          *Mapper.java) ;;
          *) echo "$f" ;;
        esac
      fi
    done || true)
if [ -n "$BAD_INTERFACE" ]; then
  echo "FAILED: 以下普通接口疑似被错误注册为 Bean/Mapper："
  echo "$BAD_INTERFACE"
  exit 1
fi

echo "[4/7] 检查 MySQL 配置是否唯一"
COUNT=$(grep -R "public DataSourceProperties mysqlDataSourceProperties" -n astock-infrastructure/src/main/java | wc -l | tr -d ' ')
if [ "$COUNT" -ne 1 ]; then
  echo "FAILED: mysqlDataSourceProperties 出现次数异常，可能存在重复配置类。"
  grep -R "mysqlDataSourceProperties" -n astock-infrastructure/src/main/java || true
  exit 1
fi

echo "[5/7] 检查是否显式绑定 MyBatis-Plus 到 MySQL SqlSessionFactory"
grep -R "mysqlSqlSessionFactory\|mysqlSqlSessionTemplate\|annotationClass = Mapper.class" -n astock-infrastructure/src/main/java/com/astock/infrastructure/config/MysqlDataSourceConfig.java >/dev/null

echo "[6/7] 检查 Spring Boot 可执行 Jar repackage 配置"
grep -R "<goal>repackage</goal>\|<mainClass>com.astock.app.AstockApplication</mainClass>" -n astock-app/pom.xml >/dev/null

echo "[7/7] 检查交易建议禁用词"
FORBIDDEN=$(grep -R "买入\|卖出\|持有\|推荐\|目标价\|加仓\|清仓\|BUY\|SELL\|HOLD\|TARGET_PRICE\|RECOMMEND" -n "${JAVA_ROOTS[@]}" \
  | grep -v "TradingInstructionTextChecker.java" \
  | grep -v "AgentAuditRedLineScanner.java" \
  | grep -v "PageSql.java" \
  | grep -v "SampleSql.java" \
  | grep -v "SignalSql.java" \
  | grep -v "BUY_PATTERN" \
  | grep -v "buy_pattern" || true)
if [ -n "$FORBIDDEN" ]; then
  echo "FAILED: 出现交易建议禁用词。"
  echo "$FORBIDDEN"
  exit 1
fi

echo "PASSED: 后端重构质量守卫通过。"
