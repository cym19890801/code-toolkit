package cn.cym.codetoolkit.bundle;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;

/**
 * 信息常量
 * @author cym
 * @date 2018/9/11
 */
public class JdpMsgResourceBundle {
    @NotNull
    private static final String BUNDLE_NAME = "cn.cym.codetoolkit.bundle.JdpMsgResourceBundle";
    @NotNull
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 默认archetype目录配置
     */
    public static String defaultArchetypeCatalogConfig = JdpMsgResourceBundle.message("defaultArchetypeCatalogConfig");
    public static String DEFAULT_VERSION = JdpMsgResourceBundle.message("DEFAULT_VERSION");

    /**
     * 向导标题
     */
    public static String CreateProjectWizard_Title = JdpMsgResourceBundle.message("CreateProjectWizard_Title");

    public static String CreateProjectWizard_Success_Title = JdpMsgResourceBundle.message("CreateProjectWizard_Success_Title");
    public static String CreateProjectWizard_Success_Content = JdpMsgResourceBundle.message("CreateProjectWizard_Success_Content");
    public static String CreateProjectWizard_Fail_Title = JdpMsgResourceBundle.message("CreateProjectWizard_Fail_Title");
    public static String CreateProjectWizard_Fail_Content = JdpMsgResourceBundle.message("CreateProjectWizard_Fail_Content");

    /**
     * 位置页标题
     */
    public static String CreateProjectWizard_LocationPage_Title = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_Title");
    public static String CreateProjectWizard_LocationPage_Description = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_Description");
    public static String CreateProjectWizard_LocationPage_lblProject = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_lblProject");
    public static String CreateProjectWizard_LocationPage_btnUserDefault = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_btnUserDefault");
    public static String CreateProjectWizard_LocationPage_lblLocation = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_lblLocation");
    public static String CreateProjectWizard_LocationPage_btnLocation = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_btnLocation");
    public static String CreateProjectWizard_LocationPage_selectLocation = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_selectLocation");
    public static String CreateProjectWizard_LocationPage_error_emptyName = JdpMsgResourceBundle.message("CreateProjectWizard_LocationPage_error_emptyName");


    public static String CreateProjectWizard_SelectTemplatePage_Title = JdpMsgResourceBundle.message("CreateProjectWizard_SelectTemplatePage_Title");
    public static String CreateProjectWizard_SelectTemplatePage_Description = JdpMsgResourceBundle.message("CreateProjectWizard_SelectTemplatePage_Description");
    public static String CreateProjectWizard_SelectTemplatePage_error = JdpMsgResourceBundle.message("CreateProjectWizard_SelectTemplatePage_error");
    public static String CreateProjectWizard_InputPage_Title = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_Title");
    public static String CreateProjectWizard_InputPage_Description = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_Description");

    public static String CreateProjectWizard_InputPage_artifactComponentGroupId = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_artifactComponentGroupId");
    public static String CreateProjectWizard_InputPage_artifactComponentArtifactId = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_artifactComponentArtifactId");
    public static String CreateProjectWizard_InputPage_artifactComponentVersion = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_artifactComponentVersion");
    public static String CreateProjectWizard_InputPage_artifactComponentPackage = JdpMsgResourceBundle.message("CreateProjectWizard_InputPage_artifactComponentPackage");
    public static String CreateProjectWizard_JobName_Prefix = JdpMsgResourceBundle.message("CreateProjectWizard_JobName_Prefix");
    public static String CreateProjectWizard_JobName_Start_TIP = JdpMsgResourceBundle.message("CreateProjectWizard_JobName_Start_TIP");
    public static String CreateProjectWizard_JobName_UpdateProject_Prefix = JdpMsgResourceBundle.message("CreateProjectWizard_JobName_UpdateProject_Prefix");

    public static String CodeGenerator_Title = JdpMsgResourceBundle.message("CodeGenerator_Title");
    public static String CodeGenerator_Success_Title = JdpMsgResourceBundle.message("CodeGenerator_Success_Title");
    public static String CodeGenerator_Success_Content = JdpMsgResourceBundle.message("CodeGenerator_Success_Content");
    public static String CodeGenerator_Fail_Title = JdpMsgResourceBundle.message("CodeGenerator_Fail_Title");
    public static String CodeGenerator_Fail_Content = JdpMsgResourceBundle.message("CodeGenerator_Fail_Content");
    public static String CodeGenerator_Fail_Content2 = JdpMsgResourceBundle.message("CodeGenerator_Fail_Content2");

    /**数据源界面字符串资源**/
    public static String JdpDatasourceDialog_Title = JdpMsgResourceBundle.message("JdpDatasourceDialog_Title");
    public static String CreateProjectWizardSelectTemplatePage_tblclmnNewColumn_text = JdpMsgResourceBundle.message("CreateProjectWizardSelectTemplatePage_tblclmnNewColumn_text");
    public static String CreateProjectWizardInputPage_groupIdCombo_text = JdpMsgResourceBundle.message("CreateProjectWizardInputPage_groupIdCombo_text");

    /** 项目向导 **/
    public static String CreateProjectWizard_common_project_label = JdpMsgResourceBundle.message("CreateProjectWizard_common_project_label");
    public static String CreateProjectWizard_common_project_ok = JdpMsgResourceBundle.message("CreateProjectWizard_common_project_ok");
    public static String CreateProjectWizard_common_project_noOK = JdpMsgResourceBundle.message("CreateProjectWizard_common_project_noOK");
    public static String CreateProjectWizard_common_project_had = JdpMsgResourceBundle.message("CreateProjectWizard_common_project_had");

    /** 代码生成向导 **/
    public static String GenerateCodeWizard_CodePage_label = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_label");
    public static String GenerateCodeWizard_CodePage_tableName = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_tableName");
    public static String GenerateCodeWizard_CodePage_tabley_alias = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_tabley_alias");
    public static String GenerateCodeWizard_CodePage_moduleName = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_moduleName");
    public static String GenerateCodeWizard_CodePage_AssociationStrategy = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_AssociationStrategy");
    public static String GenerateCodeWizard_CodePage_AssociationTable = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_AssociationTable");
    public static String GenerateCodeWizard_CodePage_AssociationColumn = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_AssociationColumn");
    public static String GenerateCodeWizard_CodePage_ManyToOne = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_ManyToOne");
    public static String GenerateCodeWizard_CodePage_OneToMany = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_OneToMany");
    public static String GenerateCodeWizard_CodePage_OneToOne = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_OneToOne");
    public static String GenerateCodeWizard_CodePage_OverrideGenerate = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_OverrideGenerate");
    public static String GenerateCodeWizard_CodePage_refresh = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_refresh");
    public static String GenerateCodeWizard_CodePage_allSelect = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_allSelect");
    public static String GenerateCodeWizard_CodePage_noSelect = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_noSelect");
    public static String GenerateCodeWizard_CodePage_selectTable = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_selectTable");

    public static String GenerateCodeWizard_common_genertateCodeing = JdpMsgResourceBundle.message("GenerateCodeWizard_common_genertateCodeing");
    public static String GenerateCodeWizard_common_err = JdpMsgResourceBundle.message("GenerateCodeWizard_common_err");
    public static String GenerateCodeWizard_common_errRemind = JdpMsgResourceBundle.message("GenerateCodeWizard_common_errRemind");
    public static String GenerateCodeWizard_common_errRemind_selectProject = JdpMsgResourceBundle.message("GenerateCodeWizard_common_errRemind_selectProject");
    public static String GenerateCodeWizard_common_genertate_ok = JdpMsgResourceBundle.message("GenerateCodeWizard_common_genertate_ok");
    public static String GenerateCodeWizard_common_genertate_noOK = JdpMsgResourceBundle.message("GenerateCodeWizard_common_genertate_noOK");

    public static String GenerateCodeWizard_conifDB = JdpMsgResourceBundle.message("GenerateCodeWizard_conifDB");
    public static String GenerateCodeWizard_conifDB_OK = JdpMsgResourceBundle.message("GenerateCodeWizard_conifDB_OK");
    public static String GenerateCodeWizard_conifDB_noOK = JdpMsgResourceBundle.message("GenerateCodeWizard_conifDB_noOK");

    public static String GenerateCodeWizard_SelectTemplatePage_Title = JdpMsgResourceBundle.message("GenerateCodeWizard_SelectTemplatePage_Title");
    public static String GenerateCodeWizard_SelectTemplatePage_Description = JdpMsgResourceBundle.message("GenerateCodeWizard_SelectTemplatePage_Description");
    public static String GenerateCodeWizard_LocationTemplatePage_lblLocation = JdpMsgResourceBundle.message("GenerateCodeWizard_LocationTemplatePage_lblLocation");

    public static String GenerateCodeWizard_CodePage_InputTip = JdpMsgResourceBundle.message("GenerateCodeWizard_CodePage_InputTip");
//    public static String GenerateCodePage_filter_toolTipText = JdpMsgResourceBundle.message("GenerateCodePage_filter_toolTipText");
    public static String GenerateCodeChoiceTemplatePage_text_text = JdpMsgResourceBundle.message("GenerateCodeChoiceTemplatePage_text_text");
    public static String GenerateCodeChoiceTemplatePage_label_text = JdpMsgResourceBundle.message("GenerateCodeChoiceTemplatePage_label_text");
    public static String GenerateCodeChoiceTemplatePage_templateExplain = JdpMsgResourceBundle.message("GenerateCodeChoiceTemplatePage_templateExplain");
    public static String Component_Title = JdpMsgResourceBundle.message("Component_Title");
    public static String Component_Description = JdpMsgResourceBundle.message("Component_Description");
    public static String Component_Spring_Boot_Version = JdpMsgResourceBundle.message("Component_Spring_Boot_Version");
    public static String Component_Name = JdpMsgResourceBundle.message("Component_Name");
    public static String Component_Detail = JdpMsgResourceBundle.message("Component_Detail");
    public static String Component_More = JdpMsgResourceBundle.message("Component_More");
    public static String Read_Pom_Error = JdpMsgResourceBundle.message("Read_Pom_Error");
    public static String UpatePom_JobName_Start_TIP = JdpMsgResourceBundle.message("UpatePom_JobName_Start_TIP");
    public static String FormatPom_JobName_Start_TIP = JdpMsgResourceBundle.message("FormatPom_JobName_Start_TIP");
    public static String UpatePom_Title = JdpMsgResourceBundle.message("UpatePom_Title");
    public static String UpatePom_common_doing = JdpMsgResourceBundle.message("UpatePom_common_doing");
    public static String FormatPom_common_doing = JdpMsgResourceBundle.message("FormatPom_common_doing");
    public static String UpatePom_common_update_noOK = JdpMsgResourceBundle.message("UpatePom_common_update_noOK");
    public static String FormatPom_common_update_noOK = JdpMsgResourceBundle.message("FormatPom_common_update_noOK");
    public static String UpatePom_common_update_ok = JdpMsgResourceBundle.message("UpatePom_common_update_ok");
    public static String FormatPom_common_update_ok = JdpMsgResourceBundle.message("FormatPom_common_update_ok");
    public static String UpatePom_common_err = JdpMsgResourceBundle.message("UpatePom_common_err");
    public static String FormatPom_common_err = JdpMsgResourceBundle.message("FormatPom_common_err");
    public static String FormatPom_Title = JdpMsgResourceBundle.message("FormatPom_Title");
    public static String FormatPom_Title_Description = JdpMsgResourceBundle.message("FormatPom_Title_Description");
    public static String FormatPom_Pom_Property = JdpMsgResourceBundle.message("FormatPom_Pom_Property");
    public static String FormatPom_Pom_Value = JdpMsgResourceBundle.message("FormatPom_Pom_Value");
    public static String LOGIN_TITLE = JdpMsgResourceBundle.message("LOGIN_TITLE");
    public static String VIEW_TITLE = JdpMsgResourceBundle.message("VIEW_TITLE");

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        try {
            return new String(CommonBundle.message(BUNDLE, key, params).getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}