package com.dad.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PageReq {
    private int page;
    @NotNull(message = "【页码】不能为空")
    @Max(value= 1000, message = "【每页条数】不能超过100")
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}