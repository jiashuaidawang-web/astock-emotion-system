# 第十四步：前端联调可编译清算

生成日期：2026-06-29

## 本次完成内容

```text
1. 修复 Vue/TS 潜在类型错误
2. 补齐 src/env.d.ts
3. 补齐 .env.example
4. 修复 business 组件 props.data 类型隐患
5. 给页面业务组件增加 key，保证接口数据返回后重新渲染
6. 检查 15 页面主展示不再依赖 JsonViewer
7. 统一 alias 导入静态检查
8. 生成前端运行手册
9. 生成前后端联调清单
10. 生成前端编译清算报告
```

## 关键修复

修复前：

```text
const d = props.data as AnyRecord || {}
```

修复后：

```text
const d = (props.data && typeof props.data === 'object' ? props.data as AnyRecord : {})
```

同时页面增加：

```text
:key="data ? JSON.stringify(data).length : 0"
```

保证 API 从 null 变成真实 PageVO 后，业务组件重新挂载并重新计算专属卡片、排行和表格。

## 新增文件

```text
astock-frontend/src/env.d.ts
astock-frontend/.env.example
tools/frontend_static_check.py
README_FRONTEND_RUN.md
docs/FRONTEND_BACKEND_INTEGRATION_CHECKLIST.md
README_FRONTEND_COMPILE_REPORT.md
README_STEP14_FRONTEND_COMPILE_CLOSURE.md
```

## npm build 说明

当前沙盒无 node_modules，不能联网安装依赖，所以不伪造 npm build 通过。

正式环境执行：

```bash
cd astock-frontend
npm install
npm run build
```

## 下一步建议

进入第十五步：

```text
全栈最终交付包清算：
合并后端、前端、SQL、README、运行脚本，
生成最终可交付目录结构、启动脚本、docker-compose、本地开发环境说明。
```
