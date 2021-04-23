package cn.cym.codetoolkit.factory;

import cn.cym.codetoolkit.action.*;
import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.ProWizardContext;
import cn.cym.codetoolkit.log.JdpAppender;
import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.ui.*;
import cn.cym.codetoolkit.utils.CodeToolkitDialogUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.slf4j.LoggerFactory;

/**
 * 对话框工厂
 *
 * @author cym
 * @date 2018/10/3
 */
public class DialogFactory {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DialogFactory.class);

    private DialogFactory() {
    }

    public static void showDialog(Class actionClass, AnActionEvent event) {
        IdeaProject ideaProject = new IdeaProject(event, event.getProject());

        ProWizardContext localProjectInfo = ideaProject.getProWizardContext();
        if (localProjectInfo != null && localProjectInfo.isPrintLog()) {
            // 日志
            JdpAppender jdpAppender = (JdpAppender) Logger.getRootLogger().getAppender("jdpAppender");
            if (jdpAppender == null) {
                jdpAppender = new JdpAppender(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n"), event.getProject());
                jdpAppender.setName("jdpAppender");
                org.apache.log4j.Logger.getRootLogger().addAppender(jdpAppender);
                jdpAppender.getMyLog().showConsole();
            } else {
                jdpAppender.change(event.getProject());
            }
        }
        LogUtils.println("showDialog... actionClass:" + actionClass.getName());

        LogUtils.println("PathManager.getSystemPath()：" + PathManager.getSystemPath());
        LogUtils.println("PathManager.getHomePath()：" + PathManager.getHomePath());
        LogUtils.println("PathManager.getConfigPath():" + PathManager.getConfigPath());
        LogUtils.println("PathManager.getPluginsPath():" + PathManager.getPluginsPath());
        LogUtils.println("PathManager.getLibPath():" + PathManager.getLibPath());
        LogUtils.println("PathManager.getLogPath():" + PathManager.getLogPath());
        LogUtils.println("PathManager.getOptionsPath():" + PathManager.getOptionsPath());
        LogUtils.println("PathManager.getPluginTempPath():" + PathManager.getPluginTempPath());

        if (actionClass == GeneratorCodeAction.class) {
            CodeToolkitDialogUtils.show(new GeneratorCodeDialog(ideaProject), 1000,600);
        } else if (actionClass == CodeTemplateAction.class) {
            CodeToolkitDialogUtils.show(new CodeTemplateDialog(ideaProject), 1000, 600);
        } else if (actionClass == ColumnConfigAction.class) {
            CodeToolkitDialogUtils.show(new ColumnConfigDialog(ideaProject), 500, 400);
        } else if (actionClass == MainAction.class) {
            CodeToolkitDialogUtils.show(new GeneratorCodeDialog(ideaProject), 1000, 800);
        } else if (actionClass == TableConfigAction.class) {
            CodeToolkitDialogUtils.show(new TableConfigDialog(ideaProject), 800, 300);
        } else if (actionClass == UnitTestCoverageAction.class) {
            CodeToolkitDialogUtils.show(new UnitTestCoverageDialog(ideaProject), 500, 300);
        } else if (actionClass == ConverTemplateAction.class) {
            CodeToolkitDialogUtils.show(new ConverTemplateDialog(ideaProject), 720, 600);
        }
    }
}
