package com.yeahmobi.embabel.risk;

import com.embabel.agent.rag.*;
import com.embabel.agent.rag.ingestion.ContentChunker;
import com.embabel.agent.rag.ingestion.HierarchicalContentReader;
import com.embabel.agent.rag.lucene.LuceneRagFacetProvider;
import com.embabel.agent.rag.support.RagFacetResults;
import com.embabel.common.ai.model.EmbeddingService;
import com.embabel.common.ai.model.ModelProvider;
import com.embabel.common.ai.model.ModelSelectionCriteria;
import com.yeahmobi.embabel.EmbabelWebApplication;
import jakarta.annotation.Resource;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmbabelWebApplication.class)
public class EmbedingTest {

    private final HierarchicalContentReader reader = new HierarchicalContentReader();

    private LuceneRagFacetProvider luceneRag;
    private EmbeddingModel embeddingModel;

    @Resource
    private ModelProvider modelProvider;

    @BeforeEach
    public void setUp() {
        // 假设你有默认的 EmbeddingModel，可以直接注入
        EmbeddingService embeddingService = modelProvider.getEmbeddingService(ModelSelectionCriteria.getPlatformDefault());
        embeddingModel = embeddingService.getModel();
        luceneRag = new LuceneRagFacetProvider(
                "testLuceneRagVector",
                embeddingModel,  // 提供向量模型
                0.7,             // 向量权重
                new ContentChunker.DefaultConfig(1000, 100),
                null             // 内存索引
        );
    }

    @AfterEach
    public void tearDown() {
        if (luceneRag != null) {
            luceneRag.clear();
            luceneRag.close();
        }
    }


    @Test
    public void testChunkingMaterializedSection() {
        // 1️⃣ 构造一个模拟的 MaterializedContainerSection
        // 1️⃣ 先创建一个简单 PDF 文件
        File pdfFile = new File("C:\\Users\\gang.chen\\Downloads\\1.41-工商照面 (1).pdf");

        // 2️⃣ 使用 Embabel 的内容解析器读取
        var result = reader.parseFile(pdfFile);



        // 2️⃣ 初始化 ContentChunker
        ContentChunker.DefaultConfig config = new ContentChunker.DefaultConfig(1000,100);
        ContentChunker chunker = new ContentChunker(config);
        // 3️⃣ 执行 chunk 操作
        List<Chunk> chunks = chunker.chunk(result);

        // 4️⃣ 校验与打印结果
        assertNotNull(chunks);
        assertFalse(chunks.isEmpty());
        System.out.println("生成的 chunk 数量：" + chunks.size());

        for (int i = 0; i < chunks.size(); i++) {
            Chunk c = chunks.get(i);
            luceneRag.save(c);
        }

        // 5️⃣ 简单断言（确保每个chunk有内容）
        for (Chunk c : chunks) {
            assertNotNull(c.getText());
            assertTrue(c.getText().length() > 10);
        }

        RagRequest request = RagRequest.query("content of leaf 1")
                .withSimilarityThreshold(0.0)
                .withTopK(10);
        RagFacetResults<Chunk> search = luceneRag.search(request);

        System.out.println("--------------->"+search);
    }
}
