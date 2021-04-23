package cn.cym.codetoolkit.action;

import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.ProWizardContext;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class PrintLogAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        IdeaProject ideaProject = new IdeaProject(anActionEvent, anActionEvent.getProject());
        ProWizardContext localProjectInfo = ideaProject.getProWizardContext();
        if (localProjectInfo.isPrintLog()) {
            localProjectInfo.setPrintLog(false);
            Messages.showInfoMessage("关闭日志", "开/关日志");
        } else {
            localProjectInfo.setPrintLog(true);
            Messages.showInfoMessage("开启日志", "开/关日志");
        }

        ideaProject.saveProWizardContext();
    }

}
