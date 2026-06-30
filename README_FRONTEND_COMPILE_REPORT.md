# 前端编译清算报告

生成日期：2026-06-29

## Node / npm

```text
node: v22.16.0
npm: 10.9.2
```

## 静态检查

命令：

```bash
python tools/frontend_static_check.py
```

结果：

```text
Spreadsheet runtime warmup failed during python startup
Traceback (most recent call last):
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/patches/warm_spreadsheet_runtime_on_startup.py", line 26, in warm_spreadsheet_runtime_on_startup
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/spreadsheet_warmup.py", line 785, in warm_spreadsheet_runtime
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/spreadsheet_warmup.py", line 720, in _warm_feature_flows
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/spreadsheet_warmup.py", line 704, in _warm_collaboration_flows
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/generated/interface/models.py", line 30820, in hydrate_crdt_from_proto
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/rpc/remote.py", line 749, in __call__
  File "/tmp/tmp.yTcnQsZYiA/artifact_tool_v2-2.8.4/artifact_tool/rpc/client.py", line 150, in call
artifact_tool.rpc.client.RemoteError: hydrateCrdtFromProto requires an empty collaborative document.
FRONTEND_STATIC_CHECK_PASSED
Vue files: 46
TS files: 12
Business components: 18
```

## npm build

状态：

```text
SKIPPED_NO_NODE_MODULES
```

输出：

```text
node_modules 不存在；沙盒不能联网安装依赖，未执行 npm run build。正式环境需执行 npm install && npm run build。
```

## 结论

```text
1. 已完成源码结构、alias导入、15页面、business组件、JsonViewer主展示残留、props.data类型隐患静态清算。
2. 当前沙盒没有 node_modules，未伪造 npm build 通过。
3. 正式环境执行 npm install && npm run build 后，以 vue-tsc/vite 的结果作为最终构建结论。
```
