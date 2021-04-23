package cn.cym.codetoolkit.entity.config;

import java.util.List;

/**
* 
* <br>CreateDate November 24,2019
* @author chenyouming
* @since 1.0
**/
public class Table {

    private String name;

    private List<Column> columns;

    public Table() {
    }

    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}

