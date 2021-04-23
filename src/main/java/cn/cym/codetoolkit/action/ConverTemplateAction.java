package cn.cym.codetoolkit.action;

import cn.cym.codetoolkit.factory.DialogFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ConverTemplateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        DialogFactory.showDialog(getClass(), anActionEvent);
    }

}
