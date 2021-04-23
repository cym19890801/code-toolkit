package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.ProWizardContext;
import cn.cym.codetoolkit.utils.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.EditorTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;

/**
 * 配置模板对话框
 *
 * @author chenyouming
 * @since 1.0
 **/
public class CodeTemplateDialog extends CodeToolkitDialog {

    /**
     * 模板名称列表
     */
    private JList templates;

    /**
     * 模板编辑器
     */
    private EditorTextField editorTextField;

    public CodeTemplateDialog(IdeaProject ideaProject) {
        setTitle("代码模板");
        setIdeaProject(ideaProject);
        JPanel contentPane = new JPanel(new BorderLayout());
        editorTextField = UIComponentUtils.createIdeaEditorTextField(ideaProject.getProject(), "");
//        editorTextField.setAutoscrolls(true);
//        editorTextField.setPreferredSize(new Dimension(200, 200));
//        editorTextField.setDocument(document);
        // 跳过格式检查
//        psiFile.getViewProvider().putUserData(FileTemplateManager.DEFAULT_TEMPLATE_PROPERTIES, FileTemplateManager.getInstance(ideaProject.getProject()).getDefaultProperties());
        // 创建文档对象
//        Document document = PsiDocumentManager.getInstance(ideaProject.getProject()).getDocument(psiFile);
        // 创建编辑框
//        editor = editorFactory.createEditor(document, ideaProject.getProject(), ftlFileType, false);

        JScrollPane jscrollPane = new JScrollPane();
        jscrollPane.setViewportView(editorTextField);

        JPanel listPanel = new JPanel(new BorderLayout());
        templates = new JList();
        listPanel.setPreferredSize(new Dimension(160, -1));
        try {
            templates.setModel(getTemplates());
            templates.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    JList<String> source = (JList<String>) e.getSource();
//                    System.out.println(source);
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            if (source.getSelectedIndex() == -1)
                                return ;
                            String name = source.getModel().getElementAt(source.getSelectedIndex());
                            try {
                                String template = GenerateUtils.getTemplateContextByName(name);
                                editorTextField.getDocument().setText(template);
//                                editorTextField.repaint();
//                                editor.getDocument().setText(template);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            });
            templates.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {

                }
            });

            templates.setSelectedIndex(0);
            listPanel.add(templates);
            contentPane.add(listPanel, BorderLayout.WEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contentPane.add(jscrollPane, BorderLayout.CENTER);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jPanel.add(createButtonHelp(), BorderLayout.WEST);
        jPanel.add(createButtonOK(), BorderLayout.WEST);
        jPanel.add(createButtonCancel(), BorderLayout.WEST);
        jPanel.add(createButtonApply(), BorderLayout.WEST);
        contentPane.add(jPanel, BorderLayout.SOUTH);

        contentPane.add(createUpPanel(), BorderLayout.NORTH);

        setContentPane(contentPane);
    }

    public void showDialog() {
        templates.setModel(getTemplates());
        templates.updateUI();
        this.setVisible(true);
    }

    private DefaultListModel getTemplates() {
        final DefaultListModel listModel = new DefaultListModel();
        for (File file : CodeToolkitUtils.getJdpTemplateWorkspace().listFiles()) {
            listModel.addElement(file.getName());
        }
        return listModel;
    }

    private JPanel createUpPanel() {
        // 1行1列
        JPanel upJPanel = new JPanel(new GridLayout(1, 1, 3, 0));

        JPanel northJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northJPanel.add(createButtonAdd(), BorderLayout.WEST);
        northJPanel.add(createButtonRemove(), BorderLayout.WEST);
        northJPanel.add(createButtonCopy(), BorderLayout.WEST);
        northJPanel.add(createButtonSync(), BorderLayout.WEST);
        northJPanel.add(createButtonTemplateServer(), BorderLayout.WEST);

        upJPanel.add(northJPanel);

//        JPanel southJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        southJPanel.add(createButtonAdd(), BorderLayout.WEST);
//        southJPanel.add(createButtonRemove(), BorderLayout.WEST);
//        southJPanel.add(createButtonCopy(), BorderLayout.WEST);
//
//        upJPanel.add(southJPanel);

        return upJPanel;
    }

    private JButton createButtonTemplateServer() {
        JButton serverButton = new JButton("Template Server");
        serverButton.setToolTipText("启动为模板服务");

        serverButton.addActionListener((e) -> {
            int op = Messages.showDialog("请选择客户端的操作系统", "提示", new String[]{"window", "mac"}, 0, Messages.getInformationIcon());
            int result = Messages.showYesNoDialog(ideaProject.getProject(), "确定启动/关闭模板服务吗？", "同步模板提示", Messages.getInformationIcon());
            if (result == 0) {
                SocketServer socketServer = new SocketServer(op == 0 ? ProjectConstants.DEFAULT_PORT : ProjectConstants.MAC_PORT);
                try {
                    String zipFile = CodeToolkitUtils.getJdpTemplateWorkspace().getCanonicalPath() + ".zip";
                    ZipUtils.zipMultiFile(CodeToolkitUtils.getJdpTemplateWorkspace().getCanonicalPath(), zipFile, op == 0 ? "\\" : "/");

                    ProgressManager.getInstance().run(new Task.Backgroundable(ideaProject.getProject(), "模板服务运行中...") {
                        @Override
                        public void run(@NotNull ProgressIndicator progressIndicator) {
                            socketServer.start(new File(zipFile));
                            progressIndicator.setText("关闭服务....");
                        }
                    });
                } catch (SocketException socketException) {
                    socketServer.shutdown();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return serverButton;
    }

    /**
     * 创建同步模板的按钮
     * @return {@link JButton}
     */
    private JButton createButtonSync() {
        JButton syncButton = new JButton("Sync All");
        syncButton.setToolTipText("同步模板");

        syncButton.addActionListener((e) -> {
            int result = Messages.showYesNoDialog("此操作会同步模板覆盖已存在的本地模板且无法撤销，确认操作吗？", "同步模板", Messages.getWarningIcon());
            if (result == 0) {// 确认
                ProWizardContext proWizardContext = ideaProject.getProWizardContext();
                if (proWizardContext.getJdpServer() == null)
                    proWizardContext.setJdpServer(new ProWizardContext.JdpServer());

                ProWizardContext.JdpServer jdpServer = proWizardContext.getJdpServer();
                if (StringUtils.isBlank(jdpServer.getHost())) {
                    String input = Messages.showInputDialog(CodeTemplateDialog.this, "请输入host", "设置模板服务host", Messages.getInformationIcon());
                    if (StringUtils.isNotBlank(input)) {
                        jdpServer.setHost(input);

                        saveProWizardContext();

                        syncTemplateByServer();
                    }
                } else {
                    syncTemplateByServer();
                }
            }
        });

        return syncButton;
    }

    /**
     * 通过服务同步模板
     */
    private void syncTemplateByServer() {
        ProgressManager.getInstance().run(new Task.Backgroundable(ideaProject.getProject(), "同步模板中...") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    File file = new File(CodeToolkitUtils.mkdirCodeToolkitWorkspace(), ProjectConstants.TEMPLATE_ROOT_ZIP);
                    if (file.exists()) {
                        if (!file.canExecute()) {
                            Messages.showInfoMessage(ideaProject.getProject(), "无法操作文件，稍后再试", "同步模板提示");
                            return ;
                        }
                        file.delete();
                    } else {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    progressIndicator.setText("开始下载模板...");
                    ProWizardContext proWizardContext = ideaProject.getProWizardContext();
                    new SocketClient(proWizardContext.getJdpServer().getHost(), SystemInfo.isMac ? ProjectConstants.MAC_PORT : ProjectConstants.DEFAULT_PORT).downloadFile(file);
                    progressIndicator.setText("下载模板完成...");

                    // todo 备份模板
                    // copyBackTemplate();

                    try {
                        progressIndicator.setText("开始解压模板...");
                        // 解压文件
                        ZipUtils.unzip(file.getCanonicalPath(), file.getParentFile().getCanonicalPath());
                        progressIndicator.setText("解压模板完成...");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    progressIndicator.setText("开始更新模板...");
                    templates.setModel(getTemplates());
                    templates.updateUI();
                    progressIndicator.setText("更新模板完成...");
                });
            }
        });
    }

    private JButton createButtonHelp() {
        JButton helpButton = new JButton("Help");

        helpButton.addActionListener((e) -> {
            CodeToolkitDialog codeToolkitDialog = new CodeToolkitDialog();
            codeToolkitDialog.setTitle("编写模板帮助");
            JScrollPane jscrollPane = new JScrollPane();
            String helpTxt = CodeToolkitUtils.getCodeTemplateHelpContent();
            EditorTextField editorTextField = UIComponentUtils.createIdeaEditorTextField(ideaProject.getProject(), helpTxt);
            jscrollPane.setViewportView(editorTextField);
            int width = 800,height = 600;
            jscrollPane.setPreferredSize(new Dimension(width, height));
            codeToolkitDialog.setContentPane(jscrollPane);
            CodeToolkitDialogUtils.show(codeToolkitDialog, width + 50, height);
        });

        return helpButton;
    }

    private JButton createButtonApply() {
        JButton applyButton = new JButton("Apply");

        applyButton.addActionListener((e) -> {
            saveTemplate();
        });

        return applyButton;
    }

    private JButton createButtonCancel() {
        JButton buttonCancel = new JButton("Cancel");

        buttonCancel.addActionListener(e -> {
            onCancel();
        });

        return buttonCancel;
    }

    private JButton createButtonOK() {
        JButton buttonOK = new JButton("Ok");

        buttonOK.addActionListener(e -> {
            saveTemplate();
            onOK();
        });

        return buttonOK;
    }

    private JButton createButtonCopy() {
        JButton buttonCopy = new JButton("Copy");

        buttonCopy.addActionListener(e -> {

        });

        return buttonCopy;
    }

    private JButton createButtonRemove() {
        JButton buttonRemove = new JButton("Remove");

        buttonRemove.addActionListener(e -> {
            int result = Messages.showYesNoDialog(CodeTemplateDialog.this, "确认删除该模板吗？", "删除模板提示", Messages.getWarningIcon());
            if (result == 0) {
                String name = templates.getSelectedValue().toString();
                File templateDir = GenerateUtils.getMkdirTemplateDir(name);
                File ftlFile = new File(templateDir, name + ProjectConstants.FTL_SUFFIX);
                if (ftlFile.exists())
                    ftlFile.delete();

                if (templateDir.exists()) {
                    if (templateDir.delete()) {
                        DefaultListModel templatesModel = (DefaultListModel) templates.getModel();
                        templatesModel.removeElement(name);

                        ApplicationManager.getApplication().runWriteAction(() -> {
                            editorTextField.getDocument().setText("");
                        });
                    }
                }
            }
        });

        return buttonRemove;
    }

    private JButton createButtonAdd() {
        JButton buttonAdd = new JButton("Add");

        buttonAdd.addActionListener(e -> {
            String inputItemName = inputItemName("");
            if (inputItemName == null) {
                return;
            }
            File dir = GenerateUtils.getMkdirTemplateDir(inputItemName);
            if (dir != null) {
                DefaultListModel templatesModel = (DefaultListModel) templates.getModel();
                templatesModel.addElement(inputItemName);
            }
        });

        return buttonAdd;
    }

    private void saveTemplate() {
        if (templates.getSelectedIndex() == -1)
            return ;
        String text = editorTextField.getDocument().getText();
        String name = templates.getSelectedValue().toString();
        try {
            GenerateUtils.saveTemplateContextToFile(name, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * 输入元素名称
     *
     * @param initValue 初始值
     * @return 获得的名称，为null表示取消输入
     */
    private String inputItemName(String initValue) {
        return Messages.showInputDialog("新名称：", "Code Toolkit Title Info", Messages.getQuestionIcon(), initValue, new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                // 非空校验
                if (StringUtils.isEmpty(inputString)) {
                    return false;
                }
                // 不能出现同名
                for (File file : CodeToolkitUtils.getJdpTemplateWorkspace().listFiles()) {
                    if (file.getName().equals(inputString))
                        return false;
                }
                return true;
            }

            @Override
            public boolean canClose(String inputString) {
                return this.checkInput(inputString);
            }
        });
    }

}

