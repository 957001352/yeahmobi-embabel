package com.yeahmobi.embabel.advice;

import java.util.List;

public class ResponseWithMeta<T> {

    private T data;
    private List<MetaFieldInfo> meta;

    public ResponseWithMeta(T data, List<MetaFieldInfo> meta) {
        this.data = data;
        this.meta = meta;
    }

    public T getData() { return data; }
    public List<MetaFieldInfo> getMeta() { return meta; }

    public void setData(T data) { this.data = data; }
    public void setMeta(List<MetaFieldInfo> meta) { this.meta = meta; }
}
