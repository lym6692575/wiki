package com.dad.wiki.req;

public class EbookQueryReq extends PageReq {
    private Long id;

    private String name;

    private Long categoryId2Param;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId2Param() {
        return categoryId2Param;
    }

    public void setCategoryId2Param(Long categoryId2Param) {
        this.categoryId2Param = categoryId2Param;
    }

    @Override
    public String toString() {
        return "EbookQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId2Param=" + categoryId2Param +
                '}';
    }
}