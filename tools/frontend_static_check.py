from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
FRONT = ROOT / "astock-frontend"
SRC = FRONT / "src"
errors = []

def fail(msg: str):
    errors.append(msg)

required_files = [
    "package.json",
    "vite.config.ts",
    "tsconfig.json",
    "src/main.ts",
    "src/App.vue",
    "src/env.d.ts",
    "src/api/http.ts",
    "src/api/pageApi.ts",
    "src/api/engineApi.ts",
    "src/composables/usePageQuery.ts",
    "src/composables/useEngineBatch.ts",
]
for item in required_files:
    if not (FRONT / item).exists():
        fail(f"缺少关键文件: {item}")

required_pages = [
    "MarketDashboardPage.vue",
    "HistoricalSimilarityPage.vue",
    "EmotionCycleStateMachinePage.vue",
    "HistoricalCycleSamplePage.vue",
    "MainlineRadarPage.vue",
    "HistoricalCycleSamplePage.vue",
    "SectorStrengthPage.vue",
    "LeaderLadderPage.vue",
    "LeaderProfilePage.vue",
    "PatternConditionPage.vue",
    "RiskControlPage.vue",
    "BacktestLabPage.vue",
    "BacktestReportDetailPage.vue",
    "DailyReviewWorkbenchPage.vue",
    "RuleVersionManagePage.vue",
    "AgentAuditDashboardPage.vue",
]
for page in required_pages:
    if not (SRC / "pages" / page).exists():
        fail(f"缺少页面文件: {page}")

business_dir = SRC / "components" / "business"
if business_dir.exists():
    business_components = list(business_dir.glob("*.vue"))
    if len(business_components) < 18:
        fail(f"business组件数量不足，当前 {len(business_components)}")
else:
    fail("缺少 business 组件目录")

import_pattern = re.compile(r"from\s+['\"]@/([^'\"]+)['\"]")
for file in list(SRC.rglob("*.ts")) + list(SRC.rglob("*.vue")):
    text = file.read_text(encoding="utf-8")
    for match in import_pattern.finditer(text):
        target = SRC / match.group(1)
        candidates = [target, target.with_suffix(".ts"), target.with_suffix(".vue"), target / "index.ts"]
        if not any(p.exists() for p in candidates):
            fail(f"{file.relative_to(FRONT)} 导入不存在: @/{match.group(1)}")

for file in list((SRC / "pages").glob("*.vue")):
    text = file.read_text(encoding="utf-8")
    if "JsonViewer" in text:
        fail(f"{file.relative_to(FRONT)} 仍依赖 JsonViewer 作为页面主展示")

bad_patterns = [
    ("props.data as AnyRecord || {}", "props.data as AnyRecord || {} 类型隐患"),
    ("import {component}", "疑似模板变量未替换"),
]
for file in list(SRC.rglob("*.vue")) + list(SRC.rglob("*.ts")):
    text = file.read_text(encoding="utf-8")
    for pattern, message in bad_patterns:
        if pattern in text:
            fail(f"{file.relative_to(FRONT)} 命中: {message}")

engine_api = SRC / "api" / "engineApi.ts"
if engine_api.exists():
    text = engine_api.read_text(encoding="utf-8")
    if "/api/engines/batch/daily/run" not in text and "/engines/batch/daily/run" not in text:
        fail("engineApi未接入一键跑批接口")

if errors:
    print("FRONTEND_STATIC_CHECK_FAILED")
    for item in errors:
        print("-", item)
    sys.exit(1)

print("FRONTEND_STATIC_CHECK_PASSED")
print(f"Vue files: {len(list(SRC.rglob('*.vue')))}")
print(f"TS files: {len(list(SRC.rglob('*.ts')))}")
print(f"Business components: {len(list(business_dir.glob('*.vue')))}")
