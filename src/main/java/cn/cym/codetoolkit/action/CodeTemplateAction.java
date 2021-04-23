package cn.cym.codetoolkit.action;

import cn.cym.codetoolkit.factory.DialogFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 配置代码模板
 * @author cym
 * @date 2018/9/3
 *
 */
public class CodeTemplateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        DialogFactory.showDialog(getClass(), anActionEvent);
    }

}
