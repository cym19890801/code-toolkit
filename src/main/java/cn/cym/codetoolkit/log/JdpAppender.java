package cn.cym.codetoolkit.log;

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cym
 * @date 2018/10/11
 */
public class JdpAppender extends ConsoleAppender {

    private Project project;
    private MyLog myLog;
    private Map<Project, MyLog> myLogMap;

    public MyLog getMyLog() {
        return myLog;
    }

    public void setMyLog(MyLog myLog) {
        this.myLog = myLog;
    }

    public JdpAppender(Layout layout, Project project) {
        super(layout, "System.out");
        this.project = project;
        myLog = new MyLog(this.project);
        myLogMap = new HashMap<>();
        myLogMap.put(project, myLog);
    }

    public void change(Project project){
        if (myLogMap.get(project) == null){
            myLog = new MyLog(project);
            myLogMap.put(project, myLog);
        } else {
            myLog = myLogMap.get(project);
        }
    }

    public void append(LoggingEvent event) {
        System.out.print(this.layout.format(event));
        myLog.showConsole();
        String[] s = event.getThrowableStrRep();
        if (s != null) {
            if (this.layout.ignoresThrowable()){
                int len = s.length;

                for(int i = 0; i < len; ++i) {
                    myLog.consoleView.print(s[i], ConsoleViewContentType.ERROR_OUTPUT);
                    myLog.consoleView.print(Layout.LINE_SEP, ConsoleViewContentType.ERROR_OUTPUT);
                }
            }
        } else {
            myLog.consoleView.print(this.layout.format(event), ConsoleViewContentType.SYSTEM_OUTPUT);
        }
    }

    public class MyLog{
        private ConsoleView consoleView;
        private RunContentDescriptor descriptor;
        private Executor executor;
        private Project project;
        private boolean isExist = false;

        public MyLog(Project project) {
            this.project = project;
            executor = DefaultRunExecutor.getRunExecutorInstance();
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

            final DefaultActionGroup toolbarActions = new DefaultActionGroup();
            descriptor =
                    new RunContentDescriptor(consoleView, null, new MyConsolePanel(consoleView, toolbarActions), "jdpConsole") {
                        @Override
                        public boolean isContentReuseProhibited() {
                            return true;
                        }
                    };
        }

        public synchronized void showConsole(){
//            for (RunContentDescriptor runContentDescriptor:
//                    ExecutionManager.getInstance(this.project).getContentManager().getAllDescriptors()) {
////                System.out.println(runContentDescriptor.getDisplayName() + "," + runContentDescriptor.getContentToolWindowId());
////                if (runContentDescriptor.getDisplayName().equals("jdpconsole")){
////                    isExist = true;
////                }
//                System.out.println(runContentDescriptor.getDisplayName() + ":" + ExecutionManager.getInstance(this.project).getContentManager().getToolWindowByDescriptor(runContentDescriptor).isVisible());
//                ExecutionManager.getInstance(this.project).getContentManager().getToolWindowByDescriptor(runContentDescriptor).activate(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
            if (!isExist){
                try {
                    final Project project = this.project;
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ExecutionManager.getInstance(project).getContentManager().showRunContent(executor, descriptor);
                            System.out.println("showRunContent...");
                        }
                    });
                    isExist = true;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        public ConsoleView getConsoleView() {
            return consoleView;
        }

        public void setConsoleView(ConsoleView consoleView) {
            this.consoleView = consoleView;
        }
    }

    private static final class MyConsolePanel extends JPanel {
        public MyConsolePanel(ExecutionConsole consoleView, ActionGroup toolbarActions) {
            super(new BorderLayout());
            JPanel toolbarPanel = new JPanel(new BorderLayout());
            toolbarPanel.add(ActionManager.getInstance()
                    .createActionToolbar(ActionPlaces.ANALYZE_STACKTRACE_PANEL_TOOLBAR, toolbarActions, false)
                    .getComponent());
            add(toolbarPanel, BorderLayout.WEST);
            add(consoleView.getComponent(), BorderLayout.CENTER);
        }
    }
}
