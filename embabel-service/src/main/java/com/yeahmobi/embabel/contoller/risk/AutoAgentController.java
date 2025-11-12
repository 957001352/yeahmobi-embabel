package com.yeahmobi.embabel.contoller.risk;

import com.embabel.agent.api.common.autonomy.AgentProcessExecution;
import com.embabel.agent.api.common.autonomy.Autonomy;
import com.embabel.agent.api.common.autonomy.ProcessExecutionException;
import com.embabel.agent.core.ProcessOptions;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/agent")
public class AutoAgentController {


    @Autowired
    private Autonomy autonomy; // Embabel-Agent 核心类

    /**
     * 查询某人的家乡美食和景点
     * Example: GET /agent/hometown?name=杨星
     */
    @GetMapping("/auto")
    public ResponseEntity<String> exploreHometown(@RequestParam("userInput") String userInput) {
        try {
            // 构建用户意图
//            String userInput = String.format("列举出%s家乡的小吃和景点", name);

            // 可以自定义 ProcessOptions，也可以使用默认值
            ProcessOptions options = ProcessOptions.DEFAULT;

            // 调用 Autonomy 执行 Agent
            AgentProcessExecution execution = autonomy.chooseAndRunAgent(userInput, options);
            // 获取 Agent 执行结果

            String result = new Gson().toJson(execution.getOutput()); // 框架返回文本结果

            return ResponseEntity.ok(result);
        } catch (ProcessExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Agent 执行失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("未知错误: " + e.getMessage());
        }
    }
}
