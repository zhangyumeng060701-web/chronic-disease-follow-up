# AI 辅助开发指南（零基础适用）

> 本文档教零基础组员如何用 AI（Codex）写代码，不需要任何编程经验。

---

## 一、核心公式

```
角色 + 上下文 + 具体需求 + 输出格式 = 高质量的 AI 回答
```

简单说：告诉 AI **你是谁、项目是什么、你要做什么、输出什么格式**。

---

## 二、前端 Prompt 模板

### 模板
```
我负责慢病随访系统的前端开发，技术栈是 Vue 3 + Element Plus。
项目仓库在 frontend-target/src/ 目录下。
[粘贴你要修改或参考的现有文件内容]

请帮我 [具体功能描述]：
- [具体要求1]
- [具体要求2]

参考已有的 [某文件] 的写法保持风格一致。
请给出完整文件内容，包含 <template>、<script setup>、<style scoped>。
```

### 实际示例1：新增页面
```
我负责慢病随访系统的前端开发，技术栈是 Vue 3 + Element Plus。
项目仓库在 frontend-target/src/views/ 目录下。

请帮我创建 FollowUpList.vue 随访记录管理页面：
- 顶部搜索栏：按患者姓名（输入框）、随访日期范围（两个日期选择器）
- 中间操作栏：新增随访按钮
- 表格列：患者姓名、随访日期、随访方式、血压、血糖、用药依从性
- 分页组件
- 新增/编辑弹窗（参考患者管理页面的弹窗结构）
- 接口调用走 api/followUp.js（先占位，接口我自己补）

参考 frontend-target/src/views/patient/PatientList.vue 的写法。
请给出完整文件内容，含 <template>、<script setup>、<style scoped>。
```

### 实际示例2：修复样式
```
我的 Vue 页面有一个表格列太窄，想加宽到 150px。
当前代码：[粘贴 el-table-column 代码]
请帮我修改。
```

---

## 三、后端 Prompt 模板

### 模板
```
我负责慢病随访系统的后端开发，技术栈是 Spring Boot 2.7 + MyBatis-Plus。
项目仓库在 backend/src/main/java/com/example/followup/ 目录下。
数据库建表脚本：[粘贴相关表的 CREATE TABLE 语句]

请帮我 [具体功能描述]：
- [具体要求1]
- [具体要求2]

参考 [某 Controller/Service] 的写法保持风格一致。
请给出完整文件内容，含 package 声明、import、类定义。
```

### 实际示例：新增随访接口
```
我负责慢病随访系统的后端开发，技术栈是 Spring Boot 2.7 + MyBatis-Plus。

数据库表：
CREATE TABLE t_follow_up (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    follow_up_date DATE NOT NULL,
    follow_up_type VARCHAR(20) NOT NULL COMMENT '门诊/电话/上门',
    systolic_bp INT,
    diastolic_bp INT,
    fasting_glucose DECIMAL(4,1),
    ...
);

请帮我写 FollowUpController，参考 PatientController.java 的写法：
- GET /api/follow-ups 分页查询，参数：patientId、startDate、endDate、page、size
- POST /api/follow-ups 新增
- PUT /api/follow-ups/{id} 编辑
- DELETE /api/follow-ups/{id} 删除
- 使用统一返回格式 Result<T> 和 PageResponse<T>

参考 backend/src/main/java/com/example/followup/controller/PatientController.java。
请给出完整文件内容。
```

---

## 四、数据库 Prompt 模板

### 模板
```
数据库是 MySQL 8.0，当前已有表：
[粘贴 schema.sql]

请帮我写 SQL：
- [具体需求]
```

---

## 五、常见错误及解决

### 错误1：AI 编造不存在的字段
```
症状：AI 返回的代码里用了 disease_name，但数据库字段是 disease_type
解决：把 schema.sql 贴给 AI，明确说"字段名以我给的建表脚本为准"
```

### 错误2：AI 用了错误的依赖版本
```
症状：代码里 import 了某个类但编译报错
解决：把 pom.xml 或 package.json 贴给 AI，说"依赖版本以我给的为准"
```

### 错误3：AI 只给片段
```
症状：返回的代码缺少 import、package 声明
解决：在 Prompt 末尾追加"请给出完整文件内容，包含所有 import"
```

### 错误4：风格不一致
```
症状：新代码和仓库里已有代码的写法完全不同
解决：把现有代码文件内容也贴给 AI，说"参考这个文件的风格"
```

---

## 六、开发流程（零基础适用）

```
1. 打开自己负责的 STANDARDS_X_xxx.md，确认本周任务
2. 在 GitHub 上创建 feature 分支
3. 把要修改的现有文件内容、数据库建表脚本准备好
4. 打开 Codex，用上面的模板写 Prompt
5. 把 AI 返回的代码复制到 IDE 里
6. 本地跑起来测试
7. 测试通过 → git commit → git push → 提 PR
```

**核心原则：把上下文给够、把要求说清楚、不满意就追问。**