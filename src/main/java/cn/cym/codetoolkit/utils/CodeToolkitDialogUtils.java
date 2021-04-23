package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.ui.CodeToolkitDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 窗口工具类
 * @author cym
 * @date 2018/9/7
 */
public class CodeToolkitDialogUtils {

    static Map<String, CodeToolkitDialog> dialogMap = new HashMap<>();

    /**
     * 垂直居中显示
     * @param jDialog
     * @param width
     * @param height
     */
    public synchronized static void show(CodeToolkitDialog jDialog, int width, int height) {
        LogUtils.println("show... width:" + width + ",height:" + height);
        CodeToolkitDialog dialog = dialogMap.get(jDialog.getTitle());
        if (dialog != null) {
//            if (!dialog.isShowing())
//                dialog.setVisible(true);

            if (dialog.getIdeaProject() == null || jDialog.getIdeaProject() == null) {
                dialog.showDialog();
            } else if (dialog.getIdeaProject().getProject().equals(jDialog.getIdeaProject().getProject())) {
                dialog.setIdeaProject(jDialog.getIdeaProject());
                dialog.showDialog();
            } else {
                pack(jDialog, width, height);
            }
        } else {
            pack(jDialog, width, height);
        }
    }

    public static void pack(CodeToolkitDialog jDialog, int width, int height) {
        jDialog.pack();
        jDialog.setSize(width, height);
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
        dialogMap.put(jDialog.getTitle(), jDialog);
    }

    public static void dispose(CodeToolkitDialog jDialog) {
        jDialog.dispose();
        dialogMap.remove(jDialog.getTitle());
    }


}
