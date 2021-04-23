package cn.cym.codetoolkit.constant;

/**
 * 项目配置相关常量
 * @author cym
 * @date 2018/9/11
 *
 */
public class ProjectConstants {

	public static String DIALOG_CONFIRM_TITLE = "代码工具确认提示框";

	public static String DIALOG_INPUT_TITLE = "代码工具输出框";

	/**
	 * 模板后缀
	 */
	public static String FTL_SUFFIX=".ftl";

	public static String TEMPLATE_ROOT = "template";

	public static String HELP_ROOT = "help";

	public static String COMMON_FTL = "ftl/commonFtl.txt";

	public static String GENERATOR_CODE_HELP = HELP_ROOT + "/generatorCodeHelp.txt";

	public static String CODE_TEMPLATE_HELP = HELP_ROOT + "/codeTemplateHelp.txt";

	public static String TEMPLATE_ROOT_ZIP = TEMPLATE_ROOT.concat(".zip");

	public static int DEFAULT_PORT = 8888;

	public static int MAC_PORT = 8889;

	public static int CONFIG_PORT = 8890;

	public static String CODE_TOOLKIT = "CODE_TOOLKIT";

	public static String CONFIG_LABEL = "CONFIG_LABEL";
	//插件默认路径
//	public static String DEFAULT_TEMP_DIR = IDEUtils.getInstallLocation() + File.separator + CODE_TOOLKIT;

//	public static File TEMPLATE_ROOT_DIR = new File(DEFAULT_TEMP_DIR, TEMPLATE_ROOT);
	//方案json文件
	public static String LOCAL_PRECEPT = "precept.json";
	//项目模板目录
	public static String PROJECT_TEMPLATE = "proTemplate";
	//项目组件模板目录
	public static String TECH_TEMPLATE = "techTemplate";
	//eclipse项目文件
	public static String PROJECT_PROPERTY_FILE = ".project";
	//项目信息文件
	public static String GENERATE_INFO = "generateInfo";
	public static String GENERATE_INFO_JSON = GENERATE_INFO + ".json";
	public static String GENERATE_INFO_JSON_ZIP = GENERATE_INFO + ".zip";
	public static String GENERATE_INFO_JSON_BAK = GENERATE_INFO_JSON + ".bak";
	//表生成信息文件
	public static String GENERATE_TABLE_INFO_JSON = "generateTableInfo.json";
	//代码模板目录
	public static String CODE_TEMPLATE = "codetemplate";
	//默认代码模板目录（具体默认使用那一套代码）
	public static String DEFAULT_CODE_TEMPLATE_DIR = "jupframeforjpa";

	//生成代码输出路径定义文件
	public static String CODE_OUTPUT_JSON = "codeoutput.json";
	//工程替换字符
	public static String PROJECT_NAME_REPLACE = "${project}";
	//包路径替换字符
	public static String PACKAGE_REPLACE = "${basepackage_dir}";
	//application.yml
	public static String YML_SUFFIX = "application.yml";

	public static String YML_EXT = "yml";

	public static String PROPERTIES_SUFFIX = ".properties";

	public static String JDBC_SUFFIX = "jdbc" + PROPERTIES_SUFFIX;

	public static String XML_SUFFIX = ".xml";

	public static String POM_XML = "pom.xml";

	public static String GROUP_ID = "groupId";

	public static String ARTIFACT_ID = "artifactId";

	public static String VERSION = "version";

	public static String NAME = "name";

	public static String VERSION_ID = "versionId";

	public static String SQL = ".sql";

	public static String LOG = "log";

	public static String JDP_LOGO_FILE_NAME = "logo.png";

	public static String USER_INFO = "userinfo.json";

}
