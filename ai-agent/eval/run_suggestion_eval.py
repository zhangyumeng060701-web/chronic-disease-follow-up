#!/usr/bin/env python3
import json
import logging
import pathlib
import sys

logging.basicConfig(level=logging.INFO, format="%(message)s")


ROOT = pathlib.Path(__file__).resolve().parents[2]
CASES_PATH = ROOT / "docs" / "ai-eval" / "suggestion-eval-cases.json"


def suggest(patient_input):
    records = patient_input.get("recentFollowUps") or []
    evidence = []
    severe = False
    moderate = False
    for record in records:
        systolic = record.get("systolicBp")
        diastolic = record.get("diastolicBp")
        fasting = record.get("fastingGlucose")
        postprandial = record.get("postprandialGlucose")
        adherence = record.get("medicationAdherence")
        if systolic is not None:
            if systolic >= 180:
                severe = True
                evidence.append("收缩压≥180")
            elif systolic >= 140:
                moderate = True
                evidence.append("收缩压≥140")
        if diastolic is not None:
            if diastolic >= 110:
                severe = True
                evidence.append("舒张压≥110")
            elif diastolic >= 90:
                moderate = True
                evidence.append("舒张压≥90")
        if fasting is not None:
            if fasting >= 11.1:
                severe = True
                evidence.append("空腹血糖≥11.1")
            elif fasting >= 7.0:
                moderate = True
                evidence.append("空腹血糖≥7.0")
        if postprandial is not None:
            if postprandial >= 16.7:
                severe = True
                evidence.append("餐后血糖≥16.7")
            elif postprandial >= 11.1:
                moderate = True
                evidence.append("餐后血糖≥11.1")
        if adherence in ("间断", "不服药"):
            moderate = True
            evidence.append("用药依从性异常")
    if severe:
        risk = "HIGH"
    elif moderate:
        risk = "MEDIUM"
    else:
        risk = patient_input.get("riskLevel") or "STABLE"
        evidence.append("无近期随访数据" if not records else "近期随访指标处于目标范围")
    if not records:
        evidence.append("无近期随访数据")
    confidence = round(min(0.95, 0.80 + min(len(records), 3) * 0.05), 2) if records else 0.60
    advice = {
        "HIGH": "建议3天内复诊，复核用药方案并评估靶器官风险。",
        "MEDIUM": "建议1-2周内随访，加强指标监测与生活方式管理。",
    }.get(risk, "建议按原计划随访，维持当前生活方式与用药方案。")
    return {
        "status": "PENDING",
        "risk_level": risk,
        "confidence": confidence,
        "evidence": "；".join(evidence),
        "advice": advice,
    }


def main():
    cases = json.loads(CASES_PATH.read_text(encoding="utf-8"))
    failures = []
    for case in cases:
        output = suggest(case["input"])
        expected = case["expected"]
        problems = []
        if output["risk_level"] != expected["riskLevel"]:
            problems.append(f"risk expected {expected['riskLevel']} got {output['risk_level']}")
        if expected.get("evidenceContains") and expected["evidenceContains"] not in output["evidence"]:
            problems.append(f"evidence missing {expected['evidenceContains']}")
        if not (0 < output["confidence"] <= 1):
            problems.append("confidence out of range")
        if not output["advice"].strip():
            problems.append("advice empty")
        if output["status"] != "PENDING":
            problems.append("status not PENDING")
        if problems:
            failures.append({"id": case["id"], "problems": problems})

    logging.info("AI建议评测：%s 条，通过 %s 条，失败 %s 条",
                 len(cases), len(cases) - len(failures), len(failures))
    for failure in failures:
        logging.info("case %s: %s", failure["id"], failure["problems"])
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
