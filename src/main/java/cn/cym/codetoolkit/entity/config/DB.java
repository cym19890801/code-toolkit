package cn.cym.codetoolkit.entity.config;

import java.util.List;

/**
* 
* <br>CreateDate November 24,2019
* @author chenyouming
* @since 1.0
**/
public class DB {

    private String name;

    private List<Table> tables;

    public DB() {
    }

    public DB(String name, List<Table> tables) {
        this.name = name;
        this.tables = tables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}

