package cn.org.rapid_framework.generator.common;

/**
 * 生成器常量
 * @author wuyy
 * @date 2017年12月22日下午2:03:36
 *
 */
public final class GeneratorConsts {
    /**
     * 覆盖生成
     */
    public static final String CONFIG_OVERRIDE_GENERATE="overrideGenerate";
    /**
     * 覆盖生成的默认值
     */
    public static final String CONFIG_OVERRIDE_GENERATE_DEFAULT="false";
    
    
    /**
     * 模板根目录
     */
    public static final String CONFIG_TEMPLATE_ROOT_DIRECTORY="templateRootAddr";
    /**
     * 模板相对目录
     */
    public static final String CONFIG_TEMPLATE_RELATIVE_DIRECTORY="templateAddr";
    /**
     * 输出目录
     */
    public static final String CONFIG_TEMPLATE_OUTPUT_DIRECTORY="outRoot";
    
    
    /**
     * 模块名称
     */
    public static final String CONFIG_MOUDLE="module";
    
    public static final String CONFIG_TABLES="tables";
    /**
     * 表名称
     */
    public static final String CONFIG_TABLE_NAME="table_name";
    public static final String CONFIG_TABLE_COLUMNS="table_columns";
    /**
     * 字段信息
     */
    public static final String CONFIG_COLUMNS="columns";
    /**
     * 字段名称
     */
    public static final String CONFIG_COLUMN_NAME="column_name";
    
    
    /**
     * 元数据，表类型 表
     */
    public static final String DATABASE_METADATA_TABLE_TYPE_TABLE="TABLE";
    public static final String DATABASE_METADATA_TABLE_SCHEM="TABLE_SCHEM";
    public static final String DATABASE_METADATA_TABLE_NAME="TABLE_NAME";
    public static final String DATABASE_METADATA_TABLE_TYPE="TABLE_TYPE";
    public static final String DATABASE_METADATA_REMARKS="REMARKS";
    public static final String DATABASE_METADATA_COLUMN_NAME="COLUMN_NAME";
    
    
    public    static final String DATABASE_METADATA_TABLE_PKTABLE_NAME  = "PKTABLE_NAME";
    public    static final String DATABASE_METADATA_TABLE_PKCOLUMN_NAME = "PKCOLUMN_NAME";
    public    static final String DATABASE_METADATA_TABLE_FKTABLE_NAME  = "FKTABLE_NAME";
    public    static final String DATABASE_METADATA_TABLE_FKCOLUMN_NAME = "FKCOLUMN_NAME";
    public    static final String DATABASE_METADATA_TABLE_KEY_SEQ       = "KEY_SEQ";
}
