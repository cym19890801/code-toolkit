package cn.cym.codetoolkit.log;

import com.intellij.execution.ui.ConsoleViewContentType;
import org.apache.log4j.Logger;

/**
* 
* <br>CreateDate November 19,2020
* @author chenyouming
* @since 1.0
**/
public class LogUtils {

    private static JdpAppender jdpAppender = (JdpAppender) Logger.getRootLogger().getAppender("jdpAppender");

    public static void println(String log) {
        if (jdpAppender != null)
            jdpAppender.getMyLog().getConsoleView().print(log + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
    }
}

