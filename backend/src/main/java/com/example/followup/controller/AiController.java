@PostMapping("/decompose")
public String decompose(@RequestBody Map<String, String> request) throws Exception {
    String requirement = request.get("requirement");
    
    // 调用本地 Python 脚本
    ProcessBuilder pb = new ProcessBuilder("python", "ai-agent/scripts/decompose.py", requirement);
    Process process = pb.start();

    // 读取脚本输出
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
    StringBuilder output = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        output.append(line);
    }
    
    return output.toString(); // 直接返回 Python 脚本输出的 JSON 字符串
}