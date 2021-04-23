package cn.cym.codetoolkit.action;

import cn.cym.codetoolkit.factory.DialogFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
* 
* <br>CreateDate December 23,2019
* @author chenyouming
* @since 1.0
**/
public class UnitTestCoverageAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        DialogFactory.showDialog(getClass(), anActionEvent);
    }
}

