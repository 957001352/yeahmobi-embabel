package com.yeahmobi.embabel.risk;

import com.embabel.agent.rag.Chunk;
import com.embabel.agent.rag.LeafSection;
import com.embabel.agent.rag.MaterializedSection;
import com.embabel.agent.rag.Section;
import com.embabel.agent.rag.ingestion.ContentChunker;
import com.embabel.agent.rag.ingestion.HierarchicalContentReader;
import com.embabel.agent.rag.ingestion.MaterializedContainerSection;
import com.embabel.agent.rag.lucene.LuceneRagFacetProvider;
import com.embabel.agent.tools.file.FileReadTools;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.net.ContentHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HierarchicalContentReaderTest {

    private final HierarchicalContentReader reader = new HierarchicalContentReader();

    @Test
    void testParseSimpleMarkdownContent() throws IOException {
        String markdown = "# Main Title\n" +
        "This is the introduction.\n\n" +
                "## Section 1\n" +
                "Content for section 1.\n\n" +
                "### Subsection 1.1\n" +
                "Content for subsection 1.1.\n\n" +
                "## Section 2\n" +
                "Content for section 2.";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(markdown.getBytes());

        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "test.md");
        metadata.set(TikaCoreProperties.CONTENT_TYPE_HINT, "text/markdown");

        var result = reader.parseContent(inputStream, "test://example.md",metadata);

        assertEquals(4, result.getChildren().size(),"是4个吗");
        assertEquals("test://example.md", result.getUri());
        assertNotNull(result.getId());

        List<String> titles = result.getChildren().stream().map(s -> ((LeafSection) s).getTitle()).toList();
        assertTrue(titles.contains("Main Title"));
        assertTrue(titles.contains("Section 1"));
        assertTrue(titles.contains("Subsection 1.1"));
        assertTrue(titles.contains("Section 2"));

        result.getChildren().forEach(section -> {
            assertEquals("test://example.md", section.getUri());
            assertNotNull(section.getId());
        });
    }

    @Test
    void testParseMarkdownWithNestedStructure() throws IOException {
        String markdown = "# Document Title\n" +
        "Introduction paragraph.\n\n" +
                "## Chapter 1\n" +
                "Chapter introduction.\n\n" +
                "### Section 1.1\n" +
                "Section content here.\n\n" +
                "#### Subsection 1.1.1\n" +
                "Detailed content.\n\n" +
                "### Section 1.2\n" +
                "More content.\n\n" +
                "## Chapter 2\n" +
                "Second chapter content.";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(markdown.getBytes());
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, "document.md");

        var result = reader.parseContent(inputStream, "x", metadata);

        assertEquals(6, result.getChildren().size());
        assertNotNull(result.getId());

        List<String> titles = result.getChildren().stream().map(s -> ((LeafSection) s).getTitle()).toList();
        assertTrue(titles.contains("Document Title"));
        assertTrue(titles.contains("Chapter 1"));
        assertTrue(titles.contains("Section 1.1"));
        assertTrue(titles.contains("Subsection 1.1.1"));
        assertTrue(titles.contains("Section 1.2"));
        assertTrue(titles.contains("Chapter 2"));

        result.getChildren().forEach(section -> {
            LeafSection leaf = (LeafSection) section;
            assertTrue(leaf.getContent() != null && !leaf.getContent().isBlank());
            assertNotNull(leaf.getId());
        });
    }

    @Test
    void testParsePlainTextContent() throws IOException {
        String text = "This is a simple text document.\nIt has multiple lines.\nBut no special formatting.";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(text.getBytes());
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.CONTENT_TYPE_HINT, "text/plain");

        var result = reader.parseContent(inputStream, "test://plain.txt", metadata);

        assertEquals(1, result.getChildren().size());
        assertEquals("test://plain.txt", result.getUri());
        assertNotNull(result.getId());

        LeafSection section = (LeafSection) result.getChildren().get(0);
        assertEquals("This is a simple text document.", section.getTitle());
        assertEquals(text, section.getContent());
        assertEquals("test://plain.txt", section.getUri());
        assertNotNull(section.getId());
    }

    @Test
    void testParseFileFromDisk(@TempDir Path tempDir) throws IOException {
        Path markdownFile = tempDir.resolve("test.md");
        String markdown = "# Test Document\nThis is a test document.\n\n" +
        "## First Section\nContent of the first section.\n\n" +
                "## Second Section\nContent of the second section.";
        Files.writeString(markdownFile, markdown);

        var result = reader.parseFile(markdownFile.toFile());

        assertEquals(3, result.getChildren().size());
        assertNotNull(result.getId());
        List<String> titles = result.getChildren().stream().map(s -> ((LeafSection) s).getTitle()).toList();
        assertTrue(titles.contains("Test Document"));
        assertTrue(titles.contains("First Section"));
        assertTrue(titles.contains("Second Section"));

        result.getChildren().forEach(section -> {
            assertTrue(section.getUri().contains("test.md"));
            assertNotNull(section.getId());
        });
    }

    // 其余测试方法可按照同样模式转换
    // 注意：
    // 1. Kotlin 的 .trimIndent() 在 Java 中可以直接使用原始字符串拼接
    // 2. Kotlin 的 mockk 可以用 Mockito 或者静态模拟工具替代
    // 3. 默认参数需要在 Java 中显式传入
    // 4. stream().map(...).toList() 需要 JDK 16+ 或者用 Collectors.toList()

    /**
     * 模拟从 PDF 文件中提取文本并封装为 Markdown Section
     */

    @Test
    void testParsePdfFile() throws Exception {
        // 1️⃣ 先创建一个简单 PDF 文件
        File pdfFile = new File("C:\\Users\\gang.chen\\Downloads\\1.41-工商照面 (1).pdf");

        // 2️⃣ 使用 Embabel 的内容解析器读取
        var result = reader.parseFile(pdfFile);

        // 3️⃣ 验证结果结构
        assertNotNull(result);
        assertNotNull(result.getId());
        List<?> children = result.getChildren();
        assertNotNull(children);
        assertFalse(children.isEmpty(), "❌ 没有解析出任何内容 section");

        // 4️⃣ 打印解析结果
        System.out.println("=== PDF 解析结果 ===");
        for (Object obj : children) {
            if (obj instanceof LeafSection section) {
                System.out.println("标题：" + section.getTitle());
                System.out.println("内容：" + section.getContent());
                System.out.println("---------------------------");
            }
        }

        // 5️⃣ 验证解析内容
        LeafSection first = (LeafSection) children.get(0);
        assertNotNull(first.getContent());
        assertFalse(first.getContent().isBlank());
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
            String text = c.getText();
            System.out.println("---- Chunk #" + (i + 1) + " ----");
            System.out.println("ID: " + c.getId());
            System.out.println("URI: " + c.getUri());
            System.out.println("内容前100字: " + text.substring(0, Math.min(100, text.length())));
            System.out.println();
        }

        // 5️⃣ 简单断言（确保每个chunk有内容）
        for (Chunk c : chunks) {
            assertNotNull(c.getText());
            assertTrue(c.getText().length() > 10);
        }
    }
}
