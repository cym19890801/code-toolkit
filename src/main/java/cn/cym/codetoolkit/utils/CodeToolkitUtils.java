package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.constant.ProjectConstants;
import com.intellij.ide.fileTemplates.impl.UrlUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.application.impl.ApplicationInfoImpl;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 插件工具类
 *
 * @author cym
 * @date 2018/9/11
 */
public class CodeToolkitUtils {

    private static final Logger logger = LoggerFactory.getLogger(CodeToolkitUtils.class);

    /**
     * 获取IDEA的版本
     *
     * @return
     */
    public static String getIdeaVersion() {
        ApplicationInfoEx appInfo = ApplicationInfoImpl.getShadowInstance();
        return appInfo.getMajorVersion() + "." + appInfo.getMinorVersion();
    }

    /**
     * 获取IDEA 版本名称以及版本
     *
     * @return
     */
    public static String getIdeaDevNameAndVersion() {
        ApplicationInfoEx appInfo = ApplicationInfoImpl.getShadowInstance();
        return appInfo.getVersionName() + " " + appInfo.getMajorVersion() + "." + appInfo.getMinorVersion();
    }

    /**
     * 获取IDE环境信息
     *
     * @return
     */
    public static Map<String, String> getEnvirnment() {
        Map<String, String> envMap = new HashMap<String, String>();
        Properties properties = System.getProperties();
        Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            envMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
//        envMap.put("idea_version", getIdeaVersion());
//        envMap.put("idea_path", getIdeaInstallLocation());
        return envMap;
    }

    /**
     * 是否是windows操作系统
     *
     * @return
     */
    public static boolean isWindowOperateSystem() {
        String operateSystem = getEnvirnment().get("os.name");
        if (StringUtils.isBlank(operateSystem)) {
            return true;
        }
        if (operateSystem.toUpperCase().contains("windows".toUpperCase())) {
            return true;
        }
        return false;
    }

    /**
     * 用系统默认浏览器打开链接
     *
     * @param url
     */
    public static void open(String url) {
        try {
            if (isWindowOperateSystem()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                Runtime.getRuntime().exec("open " + url);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static File mkdirCodeToolkitWorkspace() {
        if (isWindowOperateSystem()) {
            // IDEA 安装路径
            File file = new File(PathManager.getHomePath(), ProjectConstants.CODE_TOOLKIT);
            if (file.exists())
                return file;

            return mkdirCodeToolkitWorkspaceToIdeaSystemPath();
        } else {
            File file = new File(PathManager.getSystemPath(), ProjectConstants.CODE_TOOLKIT);
            if (file.exists())
                return file;

            return mkdirCodeToolkitWorkspaceToIdeaSystemPath();
        }
    }

    public static File mkdirCodeToolkitTemplate() {
        // 模板目录
        File templateRootDir = getJdpTemplateWorkspace();

        try {
            List<String> templates = UrlUtil.getChildrenRelativePaths(CodeToolkitUtils.class.getResource("/" + ProjectConstants.TEMPLATE_ROOT));
            for (String templateName : templates) {
                File templateDir = new File(templateRootDir, templateName);
                if (templateDir.exists()) {
                    continue ;
                }
                templateDir.mkdir();
                File ftlFile = new File(templateDir, templateName + ProjectConstants.FTL_SUFFIX);
                if (!ftlFile.exists()) {
                    ftlFile.createNewFile();
                }
                String templateContext = UrlUtil.loadText(GenerateUtils.class.getResource("/" + ProjectConstants.TEMPLATE_ROOT + "/" + templateName));
                IoUtils.appendTxtFile(ftlFile, templateContext);
            }
            return templateRootDir;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getCommonFtlContent() {
        try {
            return UrlUtil.loadText(CodeToolkitUtils.class.getResource("/" + ProjectConstants.COMMON_FTL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGeneratorCodeHelpContent() {
        try {
            return UrlUtil.loadText(CodeToolkitUtils.class.getResource("/" + ProjectConstants.GENERATOR_CODE_HELP));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCodeTemplateHelpContent() {
        try {
            return UrlUtil.loadText(CodeToolkitUtils.class.getResource("/" + ProjectConstants.CODE_TEMPLATE_HELP));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File mkdirCodeToolkitWorkspaceToIdeaSystemPath() {
        File file = new File(PathManager.getSystemPath(), ProjectConstants.CODE_TOOLKIT);
        if (file.exists())
            return file;

        if (file.mkdir())
            return file;

        ApplicationManager.getApplication().invokeLater(() -> {
            Messages.showMessageDialog("插件目录创建失败，请确保当前用户在IDEA安装目录的写权限！", "提示", Messages.getErrorIcon());
        });
        return null;
    }

    public static File getJdpTemplateWorkspace() {
        File file = new File(mkdirCodeToolkitWorkspace(), ProjectConstants.TEMPLATE_ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

}
