package cn.cym.codetoolkit.action;

import cn.cym.codetoolkit.factory.DialogFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 代码生成Action
 * @author cym
 * @date 2018/9/3
 *
 */
public class GeneratorCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        DialogFactory.showDialog(getClass(), anActionEvent);
    }

}
