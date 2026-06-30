from pathlib import Path
import re, json, sys, argparse
from collections import defaultdict

def read(p): return Path(p).read_text(encoding="utf-8") if Path(p).exists() else ""

def ddl_tables(sql):
    return {m.group(1).lower() for m in re.finditer(r"create\s+table\s+if\s+not\s+exists\s+(?:`?\w+`?\.)?`?([a-zA-Z_]\w*)`?", sql, re.I)}

def norm(p):
    p = re.sub(r"\$\{[^}]+\}", "{param}", p)
    p = re.sub(r"\{[^}]+\}", "{param}", p)
    return p

def frontend_apis(root):
    txt = read(root/"astock-frontend/src/api/pageApi.ts") + "\n" + read(root/"astock-frontend/src/api/engineApi.ts")
    paths = set(re.findall(r"url:\s*`([^`]+)`", txt)) | set(re.findall(r"url:\s*'([^']+)'", txt))
    return {norm(x) for x in paths}

def backend_methods(root):
    paths = set()
    for f in root.rglob("*Controller.java"):
        txt = read(f)
        bm = re.search(r"@RequestMapping\(\s*(?:value\s*=\s*)?\"([^\"]+)\"", txt)
        base = bm.group(1) if bm else ""
        for ann in ["GetMapping","PostMapping","PutMapping","DeleteMapping","PatchMapping"]:
            for m in re.finditer(r"@"+ann+r"\(\s*(?:value\s*=\s*)?\"([^\"]+)\"", txt):
                paths.add(norm((base.rstrip("/")+"/"+m.group(1).lstrip("/")).replace("//","/")))
    return paths

def java_refs(root):
    refs=defaultdict(set)
    pats=[r"\bfrom\s+([a-zA-Z_]\w*)", r"\bjoin\s+([a-zA-Z_]\w*)", r"\binsert\s+into\s+([a-zA-Z_]\w*)", r"\bupdate\s+([a-zA-Z_]\w*)", r"\bdelete\s+from\s+([a-zA-Z_]\w*)"]
    ignore={"select","where","set","values","dual","system","date","string","long","int","map","list","new","old","following","rows","row","t","a","b","c","d","x","y"}
    for f in root.rglob("*.java"):
        txt=read(f)
        if not any(k in txt.lower() for k in ["select ","insert ","update ","delete "," from "," join "]): continue
        for pat in pats:
            for m in re.finditer(pat, txt, re.I):
                t=m.group(1).lower()
                if t not in ignore: refs[t].add(str(f.relative_to(root)))
    return refs

def mapper_refs(root):
    refs=set()
    for f in root.rglob("*Mapper.java"):
        txt=read(f)
        for pat in [r"\bfrom\s+([a-zA-Z_]\w*)", r"\binsert\s+into\s+([a-zA-Z_]\w*)", r"\bupdate\s+([a-zA-Z_]\w*)", r"\bdelete\s+from\s+([a-zA-Z_]\w*)"]:
            refs |= {m.group(1).lower() for m in re.finditer(pat, txt, re.I)}
    return refs

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--project-root", default=".")
    ap.add_argument("--mysql-sql", default="sql/init_mysql.sql")
    ap.add_argument("--ck-sql", default="sql/init_ck.sql")
    ap.add_argument("--out-dir", default="docs")
    args=ap.parse_args()
    root=Path(args.project_root).resolve()
    out=root/args.out_dir if not Path(args.out_dir).is_absolute() else Path(args.out_dir)
    out.mkdir(parents=True, exist_ok=True)
    mysql=ddl_tables(read(root/args.mysql_sql if not Path(args.mysql_sql).is_absolute() else args.mysql_sql))
    ck=ddl_tables(read(root/args.ck_sql if not Path(args.ck_sql).is_absolute() else args.ck_sql))
    all_tables=mysql|ck
    fapis=frontend_apis(root); bmaps=backend_methods(root)
    refs=java_refs(root); mrefs=mapper_refs(root)
    missing=[t for t in refs if t not in all_tables]
    router=read(root/"astock-frontend/src/router/index.ts")
    route_imports=re.findall(r"component:\s*\(\)\s*=>\s*import\('([^']+)'\)", router)
    pages=list((root/"astock-frontend/src/pages").glob("*Page.vue"))
    checks=[]
    def add(n,p,d): checks.append({"name":n,"passed":bool(p),"detail":d})
    add("MySQL初始化表数量>=42", len(mysql)>=42, {"mysql_table_count":len(mysql)})
    add("ClickHouse初始化表数量>=39", len(ck)>=39, {"clickhouse_table_count":len(ck)})
    add("Java宽口径SQL引用表均在DDL", not missing, {"wide_java_sql_ref_count":len(refs), "mapper_direct_ref_count":len(mrefs), "missing_tables":missing})
    add("前端API均有后端方法级映射", not [p for p in fapis if p not in bmaps], {"frontend_api_count":len(fapis), "backend_method_mapping_count":len(bmaps), "missing_backend_paths":[p for p in fapis if p not in bmaps]})
    add("路由页面文件存在", len(route_imports)>=15, {"route_page_count":len(route_imports)})
    add("页面文件数量为15", len(pages)==15, {"page_file_count":len(pages)})
    add("页面主展示不依赖JsonViewer", not [str(p.relative_to(root)) for p in pages if "JsonViewer" in read(p)], {"json_pages":[str(p.relative_to(root)) for p in pages if "JsonViewer" in read(p)]})
    report={"agent":"BusinessAlignmentAgent","status":"PASSED" if all(c["passed"] for c in checks) else "FAILED","summary":{"mysql_table_count":len(mysql),"clickhouse_table_count":len(ck),"wide_java_sql_ref_count":len(refs),"mapper_direct_ref_count":len(mrefs),"frontend_api_count":len(fapis),"backend_method_mapping_count":len(bmaps),"route_page_count":len(route_imports),"page_file_count":len(pages)},"checks":checks}
    (out/"AGENT_BUSINESS_ALIGNMENT_REPORT.json").write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding="utf-8")
    md=["# BusinessAlignmentAgent 对齐检查报告","",f"状态：{report['status']}","","```json",json.dumps(report["summary"],ensure_ascii=False,indent=2),"```"]
    for c in checks:
        md += ["",f"- [{'x' if c['passed'] else ' '}] {c['name']}","```json",json.dumps(c["detail"],ensure_ascii=False,indent=2),"```"]
    (out/"AGENT_BUSINESS_ALIGNMENT_REPORT.md").write_text("\n".join(md)+"\n",encoding="utf-8")
    print("BusinessAlignmentAgent:", report["status"])
    print(json.dumps(report["summary"],ensure_ascii=False,indent=2))
    if report["status"]!="PASSED": sys.exit(1)
if __name__=="__main__": main()
