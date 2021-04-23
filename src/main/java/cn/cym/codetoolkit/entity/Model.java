package cn.cym.codetoolkit.entity;
 
/**
* 
* <br>CreateDate November 23,2019
* @author chenyouming
* @since 1.0
**/
public class Model {

    public Model() {
    }

    public Model(String out) {
        this.out = out;
    }

    private String out;

    private boolean outEnable = true;

    private String outFileName;

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public boolean isOutEnable() {
        return outEnable;
    }

    public void setOutEnable(boolean outEnable) {
        this.outEnable = outEnable;
    }
}

