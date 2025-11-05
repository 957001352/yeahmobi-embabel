//package com.yeahmobi.embabel.contoller.tools;
//
//import com.embabel.agent.rag.*;
//import com.embabel.agent.rag.ingestion.ContentChunker;
//import com.embabel.agent.rag.ingestion.HierarchicalContentReader;
//import com.embabel.agent.rag.ingestion.MaterializedDocument;
//import com.embabel.agent.rag.lucene.LuceneRagFacetProvider;
//import com.embabel.agent.rag.support.RagFacetResults;
//import com.embabel.common.ai.model.EmbeddingService;
//import com.embabel.common.ai.model.ModelProvider;
//import com.embabel.common.ai.model.ModelSelectionCriteria;
//import com.yeahmobi.embabel.advice.MetaFieldInfo;
//import com.yeahmobi.embabel.model.CustomerDto;
//import com.yeahmobi.embabel.model.order.OrderDto;
//import com.yeahmobi.embabel.service.MetaGeneratorService;
//import jakarta.annotation.Resource;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
//@RestController
//@RequestMapping("/meta")
//@RequiredArgsConstructor
//public class MetaController {
//
//    private final MetaGeneratorService metaGeneratorService;
//
//    private LuceneRagFacetProvider luceneRag;
//
//    private final HierarchicalContentReader reader = new HierarchicalContentReader();
//
//    @GetMapping("/all")
//    public ResponseEntity<Map<String, Map<String, MetaFieldInfo>>> all() {
//        return ResponseEntity.ok(metaGeneratorService.getAllMeta());
//    }
//
//    @GetMapping("/{className}")
//    public ResponseEntity<?> byClass(@PathVariable("className") String className) {
//        return metaGeneratorService.getMetaBySimpleClassName(className)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/one")
//    public ResponseEntity<CustomerDto> getCustomer() {
//        CustomerDto dto = new CustomerDto();
//        dto.setCustomerCode("C2025001");
//        dto.setCustomerName("张三");
//        dto.setTotalReceived(new BigDecimal("12345.67"));
//        return ResponseEntity.ok(dto);
//    }
//
//    @GetMapping("/list")
//    public ResponseEntity<List<CustomerDto>> getCustomerList() {
//        CustomerDto dto1 = new CustomerDto();
//        dto1.setCustomerCode("C2025001");
//        dto1.setCustomerName("张三");
//        dto1.setTotalReceived(new BigDecimal("12345.67"));
//
//        CustomerDto dto2 = new CustomerDto();
//        dto2.setCustomerCode("C2025002");
//        dto2.setCustomerName("李四");
//        dto2.setTotalReceived(new BigDecimal("9876.54"));
//
//        return ResponseEntity.ok(List.of(dto1, dto2));
//    }
//
//    @GetMapping("/orderList")
//    public ResponseEntity<List<OrderDto>> getOrderList() {
//        OrderDto orderDto = new OrderDto();
//        orderDto.setAmount(BigDecimal.ONE);
//
//        List<CustomerDto> customerDtos = new ArrayList<>();
//        CustomerDto dto1 = new CustomerDto();
//        dto1.setCustomerCode("C2025001");
//        dto1.setCustomerName("张三");
//        dto1.setTotalReceived(new BigDecimal("12345.67"));
//        customerDtos.add(dto1);
//
//        orderDto.setAmount(BigDecimal.TEN);
//        CustomerDto dto2 = new CustomerDto();
//        dto2.setCustomerCode("C2025002");
//        dto2.setCustomerName("李四");
//        dto2.setTotalReceived(new BigDecimal("9876.54"));
//        customerDtos.add(dto2);
//        orderDto.setCustomer(customerDtos);
//
//        OrderDto orderDto2 = new OrderDto();
//        List<CustomerDto> customerDtos2 = new ArrayList<>();
//        CustomerDto dto3 = new CustomerDto();
//        dto3.setCustomerCode("C2025001");
//        dto3.setCustomerName("张三");
//        dto3.setTotalReceived(new BigDecimal("12345.67"));
//        customerDtos.add(dto3);
//
//        orderDto2.setAmount(BigDecimal.TEN);
//        CustomerDto dto4 = new CustomerDto();
//        dto4.setCustomerCode("C2025002");
//        dto4.setCustomerName("李四");
//        dto4.setTotalReceived(new BigDecimal("9876.54"));
//        customerDtos2.add(dto4);
//        orderDto2.setCustomer(customerDtos2);
//        return ResponseEntity.ok(List.of(orderDto, orderDto2));
//    }
//    @Resource
//    private ModelProvider modelProvider;
//
//    @GetMapping("/test")
//    public ResponseEntity test() {
//        // 假设你有默认的 EmbeddingModel，可以直接注入
//        ;
//        EmbeddingService embeddingService = modelProvider.getEmbeddingService(ModelSelectionCriteria.getPlatformDefault());
//        EmbeddingModel embeddingModel = embeddingService.getModel();
//        luceneRag = new LuceneRagFacetProvider(
//                "testLuceneRagVector",
//                embeddingModel,  // 提供向量模型
//                0.7,             // 向量权重
//                new ContentChunker.DefaultConfig(1000,100),
//                null             // 内存索引
//        );
//        testChunkingMaterializedSection();
//        return ResponseEntity.ok(luceneRag.findAll());
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity search(@RequestParam(value = "query",required = false)String query,
//                                 @RequestParam(value = "threshold",required = false)Double threshold) {
//        if(StringUtils.isNotEmpty(query)){
//            RagRequest request = RagRequest.query(query)
//                    .withSimilarityThreshold(threshold)
//                    .withTopK(10);
//            return ResponseEntity.ok(luceneRag.search(request));
//        }else {
//            return ResponseEntity.ok(luceneRag.findAll());
//        }
//
//    }
//
//    public void testChunkingMaterializedSection() {
//        // 1️⃣ 构造一个模拟的 MaterializedContainerSection
//        // 1️⃣ 先创建一个简单 PDF 文件
//        File pdfFile = new File("C:\\Users\\gang.chen\\Downloads\\1.41-工商照面.pdf");
//
//        // 2️⃣ 使用 Embabel 的内容解析器读取
//        var result = reader.parseFile(pdfFile);
//
////        // 2️⃣ 初始化 ContentChunker
////        ContentChunker.DefaultConfig config = new ContentChunker.DefaultConfig(1000,100);
////        ContentChunker chunker = new ContentChunker(config);
//        // 3️⃣ 执行 chunk 操作
////        List<Chunk> chunks = chunker.chunk(result);
//
//        // 4️⃣ 校验与打印结果
////        System.out.println("生成的 chunk 数量：" + chunks.size());
//
//        // 5️⃣ 简单断言（确保每个chunk有内容）
////        for (Chunk c : chunks) {
////            ContentElement c1 = luceneRag.save(c);
////        }
//
//        luceneRag.writeContent(result);
//    }
//
//
//}
