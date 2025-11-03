package com.yeahmobi.embabel.hometown;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.domain.library.Person;
import com.embabel.agent.domain.library.PersonImpl;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

/**
 * 根据人的籍贯查找当地最好吃的小吃和最出名的景点
 */
@Agent(
        name = "HometownExplorer",
        description = "根据一个人的籍贯（家乡/出生地）查找当地最好吃的小吃和最出名的景点",
        beanName = "hometownExplorer")
public class HometownExplorer {

    private final int resultCount;

    public HometownExplorer(@Value("${hometown.explorer.result.count:5}") int resultCount) {
        this.resultCount = resultCount;
    }

    /** 从用户输入中提取姓名 */
    @Action
    public Person extractPerson(UserInput userInput, Ai ai) {
        return ai
                .withDefaultLlm()
                .createObjectIfPossible(
                        """
                        从以下输入中提取出人的姓名：
                        %s
                        """.formatted(userInput.getContent()),
                        PersonImpl.class
                );
    }

    @Action(cost = 100.0) // Make it costly so it won't be used in a plan unless there's no other path
    public LocalPlace makeHometown(Person person) {
        return WaitFor.formSubmission("让我们来了解一些籍贯（家乡/出生地）的细节 " + person.getName(),
                LocalPlace.class);
    }

    @Action
    public LocalPerson assembleLocalPerson(Person person, LocalPlace localPlace) {
        return new LocalPerson(
                person.getName(),
                localPlace.homeTown()
        );
    }

    @Action
    public LocalPerson extractLocalPerson(UserInput userInput, Ai ai) {
        return ai
                .withDefaultLlm()
                .createObjectIfPossible("""
                从以下输入中提取出人的姓名和籍贯（家乡/出生地）：
                %s""".formatted(userInput.getContent()),
                        LocalPerson.class
                );
    }



    /** 查找当地特色小吃 */
    @Action(toolGroups = {CoreToolGroups.WEB})
    public LocalFoods findLocalFoods(LocalPerson person, Ai ai) {
        var prompt = """
                请帮我查找%s当地最有名、最好吃的前%d种特色小吃。
                对每种小吃写一句简短的描述，包含风味或历史特色。
                返回结构化结果，不要广告或餐厅推广信息。
                """.formatted(person.hometown(), resultCount);

        return ai
                .withDefaultLlm()
                .createObject(prompt, LocalFoods.class);
    }

    /** 查找当地热门景点 */
    @Action(toolGroups = {CoreToolGroups.WEB})
    public LocalAttractions findLocalAttractions(LocalPerson person, Ai ai) {
        var prompt = """
                请查找%s最受欢迎或最具代表性的前%d个旅游景点，
                每个景点写一句有趣、轻松的简介。
                可以包含自然风光、历史遗迹或地标建筑。
                """.formatted(person.hometown(), resultCount);

        return ai
                .withDefaultLlm()
                .createObject(prompt, LocalAttractions.class);
    }

    /** 生成幽默的旅游推荐文案 */
    @AchievesGoal(
            description = "生成一篇基于籍贯、美食和景点的幽默旅游推荐文案",
            export = @Export(
                    remote = true,
                    name = "hometownWriteup",
                    startingInputTypes = {LocalPerson.class, UserInput.class})
    )
    @Action
    public Writeup writeupAll(LocalPerson person, LocalFoods foods, LocalAttractions attractions, Ai ai) {
        String foodList = foods.items().stream()
                .map(item -> "- " + item.name() + "：" + item.description())
                .collect(Collectors.joining("\n"));

        String attractionList = attractions.items().stream()
                .map(item -> "- " + item.name() + "：" + item.description())
                .collect(Collectors.joining("\n"));

        var prompt = """
                请为来自%s的%s写一篇幽默、有趣的旅游推荐文章。
                
                内容包括：
                1. 当地最有名的小吃：
                %s
                
                2. 最值得去的景点：
                %s
                
                要求：
                - 语气轻松幽默
                - 语言自然、带点俏皮
                - 用Markdown格式排版（可加链接）
                - 最后用一句意想不到的“旅游冷知识”或“本地人小贴士”结尾
                """.formatted(person.hometown(), person.name(), foodList, attractionList);

        return ai
                .withLlm(LlmOptions
                        .withDefaultLlm()
                        .withTemperature(0.85))
                .createObject(prompt, Writeup.class);
    }

    /** 生成幽默的旅游推荐文案 */
    @AchievesGoal(
            description = "生成一篇基于籍贯、美食旅游推荐文案",
            export = @Export(
                    remote = true,
                    name = "hometownWriteup",
                    startingInputTypes = {LocalPerson.class, UserInput.class})
    )
    @Action
    public Writeup writeupFood(LocalPerson person, LocalFoods foods, Ai ai) {
        String foodList = foods.items().stream()
                .map(item -> "- " + item.name() + "：" + item.description())
                .collect(Collectors.joining("\n"));

        var prompt = """
                请为来自%s的%s写一篇幽默、有趣的旅游推荐文章。
                
                内容包括：
                1. 当地最有名的小吃：
                %s
                
                要求：
                - 语气轻松幽默
                - 语言自然、带点俏皮
                - 用Markdown格式排版（可加链接）
                - 最后用一句意想不到的“旅游冷知识”或“本地人小贴士”结尾
                """.formatted(person.hometown(), person.name(), foodList);

        return ai
                .withLlm(LlmOptions
                        .withDefaultLlm()
                        .withTemperature(0.85))
                .createObject(prompt, Writeup.class);
    }
}
