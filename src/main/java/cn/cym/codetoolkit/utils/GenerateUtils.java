package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * 生成工具类
 *
 * @author admin
 * @date 2018年1月4日下午3:53:08
 */
public class GenerateUtils {

    public static File getCreateTemplateFile(String name) throws IOException {
        File templateDir = getMkdirTemplateDir(name);

        File ftlFile = new File(templateDir, name + ProjectConstants.FTL_SUFFIX);
        if (!ftlFile.exists()) {
            ftlFile.createNewFile();
        }
        return ftlFile;
    }

    public static File getMkdirTemplateDir(String name){
        File templateDir = new File(CodeToolkitUtils.getJdpTemplateWorkspace(), name);
        if (templateDir.exists()) {
            return templateDir;
        } else {
            if (templateDir.mkdir())
                return templateDir;
        }
        return null;
    }

    public static void saveTemplateContextToFile(String name, String context) throws Exception {
        File ftlFile = GenerateUtils.getCreateTemplateFile(name);
        IoUtils.writeTxtFile(ftlFile, context);
    }

    public static String getTemplateContextByName(String name) throws Exception {
        File templateDir = new File(CodeToolkitUtils.getJdpTemplateWorkspace(), name);
        File ftlFile = new File(templateDir, name + ProjectConstants.FTL_SUFFIX);
        return IoUtils.readTxtFile(ftlFile);
    }

    public static Table getTable(DbTable dbTable) {
        Table table = new Table();
        table.setSqlName(dbTable.getName());
        table.setClassName(NameUtils.getInstance().getClassName(dbTable.getName()));
        table.setRemarks(dbTable.getComment());
        table.setColumns(new LinkedHashSet<>());
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dbTable);
        for (DasColumn column : columns) {
            Column c = new Column(
                    column.getName(),
                    GenerateUtils.dataTypeToJDBC(column.getDataType().typeName),
                    GenerateUtils.dataTypeToJava(column.getDataType().typeName),
                    column.getDataType().size,
                    column.getComment());
            table.getColumns().add(c);
            if (DasUtil.isPrimary(column)) {
                table.getPkColumns().add(c);
            } else {
                table.getOtherColumns().add(c);
            }
        }
        return table;
    }

    public static String converUnsignedDataType(String dataType) {
        String[] split = dataType.split(" ");
        if (split.length > 1)
            return split[0];

        return dataType;
    }

    public static String dataTypeToJDBC(String dataType) {
        if (dataType == null || dataType.trim().length() == 0) return dataType;
        dataType = dataType.toLowerCase();
        dataType = converUnsignedDataType(dataType);
        switch (dataType) {
            case "nvarchar":
                return "VARCHAR";
            case "char":
                return "CHAR";
            case "varchar":
                return "VARCHAR";
            case "text":
                return "LONGVARCHAR";
            case "ntext":
                return "LONGVARCHAR";
            case "longtext":
                return "LONGVARCHAR";
            case "blob":
                return "BLOB";
            case "int":
                return "INTEGER";
            case "tinyint":
                return "TINYINT";
            case "smallint":
                return "SMALLINT";
            case "bit":
                return "BOOLEAN";
            case "bigint":
                return "BIGINT";
            case "float":
                return "FLOAT";
            case "double":
                return "DOUBLE";
            case "money":
                return "Double";
            case "smallmoney":
                return "Double";
            case "decimal":
                return "DECIMAL";
            case "boolean":
                return "BOOLEAN";
            case "date":
                return "DATE";
            case "datetime":
                return "TIMESTAMP";
            case "year":
                return "DATE";
            case "time":
                return "TIME";
            case "smalldatetime":
                return "DATE";
            case "timestamp":
                return "TIMESTAMP";
            default:
                System.out.println("-----------------》转化失败：未发现的类型" + dataType);
                break;
        }
        return dataType;
    }

    public static String dataTypeToJava(String dataType) {
        if (dataType == null || dataType.trim().length() == 0) return dataType;
        dataType = dataType.toLowerCase();
        dataType = converUnsignedDataType(dataType);
        switch (dataType) {
            case "nvarchar":
                return "String";
            case "ntext":
                return "String";
            case "char":
                return "String";
            case "varchar":
                return "String";
            case "text":
                return "String";
            case "longtext":
                return "String";
            case "nchar":
                return "String";
            case "int":
                return "Integer";
            case "tinyint":
                return "Integer";
            case "smallint":
                return "Integer";
            case "bit":
                return "Boolean";
            case "bigint":
                return "Long";
            case "float":
                return "Float";
            case "double":
                return "Double";
            case "decimal":
                return "BigDecimal";
            case "boolean":
                return "Boolean";
            case "id":
                return "Long";
            case "date":
                return "Date";
            case "datetime":
                return "Date";
            case "year":
                return "Date";
            case "smalldatetime":
                return "DATE";
            case "time":
                return "Time";
            case "timestamp":
                return "Timestamp";
            case "numeric":
                return "java.math.BigDecimal";
            case "real":
                return "java.math.BigDecimal";
            case "money":
                return "Double";
            case "smallmoney":
                return "Double";
            case "image":
                return "byte[]";
            default:
                System.out.println("-----------------》转化失败：未发现的类型" + dataType);
                break;
        }
        return dataType;
    }

//    public static ProWizardContext getLocalProjectInfo(Project project) {
//        try {
//            String jsonString = JsonUtils.txt2String(getProjectGenerteInfoFile(project).getCanonicalPath());
//            if (StringUtils.isBlank(jsonString))
//                jsonString = "{}";
//            return JSON.parseObject(jsonString, ProWizardContext.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("IO generateInfo.json文件异常，可能的原因是generateInfo.json文件不存在，或者generateInfo.json文件不符合");
//        }
//    }

//    public static File getConfigRootFile(Project project) {
//        File file = new File(project.getProjectFile().getParent().getCanonicalPath(), ProjectConstants.CODE_TOOLKIT);
//        if (!file.exists())
//            file.mkdir();
//        return file;
//    }

//    public static File getConfigLabelRootFile(Project project) {
//        File file = new File(getConfigRootFile(project), ProjectConstants.CONFIG_LABEL);
//        if (!file.exists())
//            file.mkdir();
//        return file;
//    }

    /**
     * 项目生成信息位置
     *
     * @param project
     * @return
     */
//    public static File getProjectGenerteInfoFile(Project project) throws IOException {
//        File generateInfoFile = new File(getConfigRootFile(project), ProjectConstants.GENERATE_INFO_JSON);
//        if (!generateInfoFile.exists())
//            generateInfoFile.createNewFile();
//        return generateInfoFile;
//    }

    /**
     * @param path
     * @return
     */
    public static boolean isFtlFile(String path) {
        if (path.toLowerCase().endsWith(ProjectConstants.FTL_SUFFIX.toLowerCase())) {
            return true;
        }
        return false;
    }

//    public static void saveProWizardContext(ProWizardContext proWizardContext, Project project) {
//        String context = JsonUtils.toString(proWizardContext, true);
//        try {
//            File file = GenerateUtils.getProjectGenerteInfoFile(project);
//            IoUtils.writeTxtFile(file, context, IoUtils.ENCODING_UTF8);
//        } catch (Exception e1) {
//            LogUtils.println(e1.getMessage());
//            e1.printStackTrace();
//        }
//    }

//    public static void saveProWizardContext(String configName, ProWizardContext proWizardContext, Project project) {
//        String context = JsonUtils.toString(proWizardContext, true);
//        try {
//            File file = new File(getConfigLabelRootFile(project), configName + ".json");
//            IoUtils.writeTxtFile(file, context);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }

//    public static ProWizardContext replaceProWizardContext(String configName, Project project) {
//        try {
//            File file = new File(getConfigLabelRootFile(project), configName + ".json");
//            String readTxtFile = IoUtils.readTxtFile(file);
//            IoUtils.writeTxtFile(GenerateUtils.getProjectGenerteInfoFile(project), readTxtFile);
//            return GenerateUtils.getLocalProjectInfo(project);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return null;
//    }
}
