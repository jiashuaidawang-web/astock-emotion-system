from pathlib import Path
import re, json, argparse, sys

def read(p): return Path(p).read_text(encoding="utf-8") if Path(p).exists() else ""

def main():
    ap=argparse.ArgumentParser()
    ap.add_argument("--project-root", default=".")
    ap.add_argument("--mysql-sql", default="sql/init_mysql.sql")
    ap.add_argument("--ck-sql", default="sql/init_ck.sql")
    ap.add_argument("--out-dir", default="docs")
    args=ap.parse_args()
    root=Path(args.project_root).resolve()
    out=root/args.out_dir if not Path(args.out_dir).is_absolute() else Path(args.out_dir)
    checks=[]
    def add(n,p,d): checks.append({"name":n,"passed":bool(p),"detail":d})
    aj=out/"AGENT_BUSINESS_ALIGNMENT_REPORT.json"
    add("必须先运行BusinessAlignmentAgent", aj.exists(), {"file":str(aj)})
    if aj.exists():
        r=json.loads(read(aj))
        add("BusinessAlignmentAgent必须通过", r.get("status")=="PASSED", {"business_status":r.get("status"),"failed_checks":[c["name"] for c in r.get("checks",[]) if not c.get("passed")]})
    mysql=read(root/args.mysql_sql if not Path(args.mysql_sql).is_absolute() else args.mysql_sql)
    ck=read(root/args.ck_sql if not Path(args.ck_sql).is_absolute() else args.ck_sql)
    mc=len(re.findall(r"create\s+table\s+if\s+not\s+exists", mysql, re.I))
    cc=len(re.findall(r"create\s+table\s+if\s+not\s+exists", ck, re.I))
    add("禁止回退到14张MySQL旧脚本", mc>=42, {"mysql_table_count":mc})
    add("禁止回退到30张ClickHouse旧脚本", cc>=39, {"clickhouse_table_count":cc})
    corr=read(root/"docs/FINAL_V2_ALIGNMENT_CORRECTION_REPORT.md")
    add("必须承认旧版14/30问题并说明修正", "上一版交付包确实不完整" in corr and "42 张" in corr and "39 张" in corr, {"correction_doc":"docs/FINAL_V2_ALIGNMENT_CORRECTION_REPORT.md"})
    report={"agent":"DisciplineSupervisorAgent","status":"PASSED" if all(c["passed"] for c in checks) else "FAILED","checks":checks}
    (out/"AGENT_DISCIPLINE_SUPERVISOR_REPORT.json").write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding="utf-8")
    md=["# DisciplineSupervisorAgent 监督报告","",f"状态：{report['status']}"]
    for c in checks:
        md += ["",f"- [{'x' if c['passed'] else ' '}] {c['name']}","```json",json.dumps(c["detail"],ensure_ascii=False,indent=2),"```"]
    (out/"AGENT_DISCIPLINE_SUPERVISOR_REPORT.md").write_text("\n".join(md)+"\n",encoding="utf-8")
    print("DisciplineSupervisorAgent:", report["status"])
    if report["status"]!="PASSED":
        print(json.dumps(report,ensure_ascii=False,indent=2))
        sys.exit(1)
if __name__=="__main__": main()
