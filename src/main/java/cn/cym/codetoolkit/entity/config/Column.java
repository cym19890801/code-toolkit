package cn.cym.codetoolkit.entity.config;

import java.util.ArrayList;
import java.util.List;

/**
* 
* <br>CreateDate November 24,2019
* @author chenyouming
* @since 1.0
**/
public class Column {

    private String name;

    public List<ExtColumn> exts = new ArrayList();

    public List<ExtColumn> getExts() {
        return exts;
    }

    public void setExts(List<ExtColumn> exts) {
        this.exts = exts;
    }

    public Column() {
    }

    public Column(String name, List<ExtColumn> exts) {
        this.name = name;
        this.exts = exts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

