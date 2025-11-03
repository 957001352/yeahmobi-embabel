package com.yeahmobi.embabel.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * Meta 字段信息结构（供序列化和前端展示）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // null 属性不序列化
public class MetaFieldInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String fieldName;
        private String type;
        private String desc;

        private List<MetaFieldInfo> nested; // 如果字段是对象类型，则递归填充

        public MetaFieldInfo() {
        }

        public MetaFieldInfo(String fieldName, String type, String desc) {
                this.fieldName = fieldName;
                this.type = type;
                this.desc = desc;
        }

        public MetaFieldInfo(String fieldName, String type, String desc, List<MetaFieldInfo> nested) {
                this.fieldName = fieldName;
                this.type = type;
                this.desc = desc;
                this.nested = nested;
        }
}

