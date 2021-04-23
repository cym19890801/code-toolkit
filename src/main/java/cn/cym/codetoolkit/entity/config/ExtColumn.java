package cn.cym.codetoolkit.entity.config;

/**
* 
* <br>CreateDate November 25,2019
* @author chenyouming
* @since 1.0
**/
public class ExtColumn {

    private String name;
    private String code;
    private String type;
    private String value;

    public ExtColumn(){}

    public ExtColumn(String name, String code, String type) {
        this.name = name;
        this.code = code;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public ExtColumn clone(){
        return new ExtColumn(this.name, this.code, this.type);
    }
}

