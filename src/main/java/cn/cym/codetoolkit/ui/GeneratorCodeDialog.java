package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.Model;
import cn.cym.codetoolkit.entity.ProWizardContext;
import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.socket.FileClient;
import cn.cym.codetoolkit.socket.FileServer;
import cn.cym.codetoolkit.utils.*;
import cn.org.rapid_framework.generator.Generator;
import cn.org.rapid_framework.generator.GeneratorFacade;
import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import com.alibaba.fastjson.JSON;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.EditorTextField;
import com.intellij.util.IconUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 生成代码对话框，设置生成代码时的输出模型
 *
 * @author chenyouming
 * @since 1.0.3
 **/
public class GeneratorCodeDialog extends CodeToolkitDialog {

    private Dimension dimension = new Dimension(100, -1);
    JTextField packageInput = new JTextField();
    JTextField rootPathInput = new JTextField();
    JTextField requestMappingInput = new JTextField();
    VirtualFile rootPathFile;
    JTable jTable;
    JPanel configLabelPanel;
    JScrollPane jScrollPane;
    JComboBox<String> moduleJComboBox;
    final FileServer fileServer = new FileServer(ProjectConstants.CONFIG_PORT);
    JRadioButton likeInputWay = null;

    public GeneratorCodeDialog(IdeaProject ideaProject) {
        setIdeaProject(ideaProject);

        setTitle("设置生成代码输出模型");
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        JPanel centerPanel = new JPanel();
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JPanel upPanel = new JPanel(new GridLayout(6, 1));

        // 配置标签面板
        upPanel.add(createConfigLabelPanel());
        upPanel.add(createModulePanel());
        upPanel.add(createInputPanel());
        upPanel.add(createRequestMappingPanel());
        upPanel.add(createRootPathPanel());
        upPanel.add(createSmartPathPanel());

        contentPane.add(upPanel, BorderLayout.NORTH);

        JButton buttonOK = new JButton("Generate");
        JButton buttonCancel = new JButton("Cancel");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(createButtonHelp());
        btnPanel.add(buttonOK);
        btnPanel.add(buttonCancel);
        contentPane.add(btnPanel, BorderLayout.SOUTH);

        init();

        jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTable);
        contentPane.add(jScrollPane, BorderLayout.CENTER);

        setContentPane(contentPane);

        buttonOK.addActionListener(e -> {

            updateProWizardContext("default");

//            GenerateUtils.saveProWizardContext(proWizardContext, ideaProject.getProject());
            ideaProject.saveProWizardContext();

            dispose();
            generateCode(ideaProject);
        });

        buttonCancel.addActionListener(e -> {
            dispose();
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void init() {
        ProWizardContext proWizardContext = ideaProject.getProWizardContext();
        if (StringUtils.isNotBlank(proWizardContext.getPackageStr())) {
            packageInput.setText(proWizardContext.getPackageStr());
        }
        if (StringUtils.isNotBlank(proWizardContext.getRequestMapping())) {
            requestMappingInput.setText(proWizardContext.getRequestMapping());
        }
        if (StringUtils.isNotBlank(proWizardContext.getRootPath())) {
            this.rootPathFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(proWizardContext.getRootPath()));
            rootPathInput.setText(proWizardContext.getRootPath());
        } else {
            this.rootPathFile = ideaProject.getProject().getBaseDir();
            rootPathInput.setText(rootPathFile.getCanonicalPath());
        }
        likeInputWay.setSelected(proWizardContext.isLikeInputFillIn());
//        LogUtils.println("likeInputWay.isSelected():" + likeInputWay.isSelected());

        jTable = createJTable();
    }

    private void updateProWizardContext(String name) {
        ProWizardContext proWizardContext = ideaProject.getProWizardContext();
        proWizardContext.setName(name);
        proWizardContext.setPackageStr(packageInput.getText());
        proWizardContext.setRequestMapping(requestMappingInput.getText());
        proWizardContext.setRootPath(rootPathInput.getText());

        int rowCount = jTable.getRowCount();
        for (int j = 0; j < rowCount; j++) {
            Object check = jTable.getValueAt(j, 0);
            Object templateName = jTable.getValueAt(j, 1);
            Object outFileName = jTable.getValueAt(j, 2);
            Object suffix = jTable.getValueAt(j, 3);// 后缀
            Object out = jTable.getValueAt(j, 4);
            if (templateName != null) {
                Model model = proWizardContext.getMap().get(templateName);
                if (model == null) {
                    model = new Model();
                    proWizardContext.getMap().put(templateName.toString(), model);
                }
                model.setOutEnable(Boolean.valueOf(check.toString()));
                if (outFileName != null)
                    model.setOutFileName(outFileName.toString() + suffix.toString());

                if (out != null)
                    model.setOut(out.toString());
            }
        }
    }

    /**
     * 创建配置标签面板
     *
     * @return
     */
    private JPanel createConfigLabelPanel() {
        configLabelPanel = new JPanel();
        configLabelPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
        // 创建保存标签button
        configLabelPanel.add(createConfigServerButton());
        configLabelPanel.add(createSynConfigButton());
        configLabelPanel.add(createSaveButton());

        // 初始化配置标签
        initConfigLabels();

        return configLabelPanel;
    }

    private JButton createSynConfigButton() {
        JButton syncConfig = new JButton("Sync Config");
        syncConfig.addActionListener(o -> {
            int result = Messages.showYesNoDialog(ideaProject.getProject(), "确认操作吗？", "操作提示", Messages.getWarningIcon());
            if (result == 0) {
                ProWizardContext proWizardContext = ideaProject.getProWizardContext();
                if (proWizardContext.getJdpServer() == null)
                    proWizardContext.setJdpServer(new ProWizardContext.JdpServer());

                ProWizardContext.JdpServer jdpServer = proWizardContext.getJdpServer();
                if (StringUtils.isBlank(jdpServer.getHost())) {
                    String input = Messages.showInputDialog(ideaProject.getProject(), "请输入host", "设置模板服务host", Messages.getInformationIcon());
                    if (StringUtils.isNotBlank(input)) {
                        jdpServer.setHost(input);

                        saveProWizardContext();

                        syncConfigByServer();
                    }
                } else {
                    syncConfigByServer();
                }
            }
        });
        return syncConfig;
    }

    private void syncConfigByServer() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                LogUtils.println("runWriteAction...");
                File zip = ideaProject.createNewProjectGenerteInfoZipFile();
                ProWizardContext proWizardContext = ideaProject.getProWizardContext();
                new FileClient(proWizardContext.getJdpServer().getHost(), ProjectConstants.CONFIG_PORT).downloadFile(ProjectConstants.GENERATE_INFO_JSON_ZIP, zip);
                File projectGenerteInfoBakFile = ideaProject.findProjectGenerteInfoBakFile();
                if (projectGenerteInfoBakFile.exists())
                    projectGenerteInfoBakFile.delete();

                ZipUtils.unzip(zip.getCanonicalPath(), ideaProject.findProjectConfigRootFile().getCanonicalPath());
                VirtualFile generateInfoVFile = LocalFileSystem.getInstance().findFileByIoFile(ideaProject.findProjectGenerteInfoFile());
                if (generateInfoVFile.exists())
                    generateInfoVFile.delete(null);
                LogUtils.println("generateInfoVFile delete...");

                VirtualFile bakVFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(projectGenerteInfoBakFile);
                LogUtils.println("refreshAndFindFileByIoFile bakVFile...");
                bakVFile.rename(null, ProjectConstants.GENERATE_INFO_JSON);

                ProWizardContext.JdpServer jdpServer = proWizardContext.getJdpServer();
                byte[] contentsToByteArray = bakVFile.contentsToByteArray(false);
                ProWizardContext proWizardContextBak = JSON.parseObject(new String(contentsToByteArray, IoUtils.ENCODING_UTF8), ProWizardContext.class);
                LogUtils.println("proWizardContextBak.isLikeInputFillIn():" + proWizardContextBak.isLikeInputFillIn());
                proWizardContextBak.setJdpServer(jdpServer);
                String rootPath = proWizardContextBak.getRootPath();
                rootPath = rootPath.replaceAll("/", File.separator);
                rootPath = rootPath.replaceAll("\\\\", File.separator);
                rootPath = rootPath.replace("{}", ideaProject.getProject().getBasePath());
                proWizardContextBak.setRootPath(rootPath);
                String context = JsonUtils.toString(proWizardContextBak, true);
                bakVFile.setBinaryContent(context.getBytes(IoUtils.ENCODING_UTF8));
                bakVFile.refresh(false, false);
                ideaProject.setProWizardContext(proWizardContextBak);

                ApplicationManager.getApplication().invokeLater(() -> {
                    init();

                    jScrollPane.setViewportView(jTable);
                    jScrollPane.updateUI();
                    LogUtils.println("updateUI...");
                });
            } catch (Exception e) {
                LogUtils.println(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private JButton createConfigServerButton() {
        JButton configServer = new JButton("Start Config Server");
        configServer.addActionListener(o -> {
            LogUtils.println("fileServer.isStart():" + fileServer.isStart());
            if (fileServer.isStart()) {
                fileServer.shutdown();
            } else {
                ProgressManager.getInstance().run(new Task.Backgroundable(ideaProject.getProject(), "配置服务运行中...") {
                    @Override
                    public void run(@NotNull ProgressIndicator progressIndicator) {
                        try {
                            fileServer.start(ideaProject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return configServer;
    }

    private JButton createSaveButton() {
        JButton save = new JButton("Save Config");
        save.addActionListener(o -> {
            ProWizardContext proWizardContext = ideaProject.getProWizardContext();
            String inputString = Messages.showInputDialog("配置名称：", ProjectConstants.DIALOG_INPUT_TITLE, Messages.getQuestionIcon(), proWizardContext.getName(), new InputValidator() {
                @Override
                public boolean checkInput(String inputString) {
                    // 非空校验
                    if (StringUtils.isBlank(inputString))
                        return false;

                    return true;
                }

                @Override
                public boolean canClose(String inputString) {
                    return this.checkInput(inputString);
                }
            });

            if (StringUtils.isBlank(inputString))
                return;

            // 更新上下文
            updateProWizardContext(inputString);

            // 保存配置
            ideaProject.saveLabelConfig(inputString);
//            GenerateUtils.saveProWizardContext(inputString, this.proWizardContext, ideaProject.getProject());

            // 添加配置标签
            addConfigLabels(inputString);

            // 持久华当前配置
            ideaProject.saveProWizardContext();
        });
        return save;
    }

    private void initConfigLabels() {
        File configLabelRootFile = ideaProject.findProjectConfigLabelRootFile();
        File[] files = configLabelRootFile.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                addConfigLabels(file.getName().replace(".json", ""));
            }
        }
    }

    private void addConfigLabels(String configName) {
        Set<Component> components = new ArrayList<>(Arrays.asList(configLabelPanel.getComponents())).stream()
                .filter(o -> (
                        StringUtils.isNotBlank(o.getName()) && (o.getName().equals(configName) || o.getName().equals(configName + "_edit") || o.getName().equals(configName + "_remove"))
                )).collect(Collectors.toSet());
        if(components.size() > 0)
            return ;

        JLabel configLabel = new JLabel(configName, null, SwingConstants.LEFT);
        configLabel.setName(configName);

        ProWizardContext proWizardContext = ideaProject.getProWizardContext();
        if (configName.equals(proWizardContext.getName())) {
            for (Component component : configLabelPanel.getComponents()) {
                component.setForeground(Color.BLACK);
            }

            configLabel.setForeground(Color.red);
        }

        configLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /**
                 * 切换配置
                 */
                JLabel source = (JLabel) e.getSource();

                ideaProject.cutProjectConfigBy(source.getName());

                init();

//                GenerateUtils.saveProWizardContext(proWizardContext, ideaProject.getProject());

                focus(source);
            }
        });
        configLabelPanel.add(configLabel);
        configLabelPanel.add(createEditLabel(configLabel));
        configLabelPanel.add(createRemoveLabel(configLabel));

        configLabelPanel.updateUI();
    }

    private void focus(JLabel source) {
        for (Component component : configLabelPanel.getComponents()) {
            component.setForeground(Color.BLACK);
        }

        source.setForeground(Color.red);

        source.updateUI();

        jScrollPane.setViewportView(jTable);
        jScrollPane.updateUI();
    }

    private JLabel createRemoveLabel(JLabel configLabel) {
        JLabel remove = new JLabel("", IconUtil.getRemoveIcon(), SwingConstants.LEFT);
        remove.setName(configLabel.getName() + "_remove");
        remove.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int result = Messages.showYesNoCancelDialog("确认删除该配置吗？", ProjectConstants.DIALOG_CONFIRM_TITLE, null);
                if (result == 0) {
                    String configLabelName = configLabel.getText();
                    // 删除配置
//                    proWizardContext.getProWizardContexts().removeIf(o -> o.getName().equals(configLabelName));

//                    GenerateUtils.saveProWizardContext(proWizardContext, ideaProject.getProject());

                    File configLabelRootFile = new File(ideaProject.findProjectConfigLabelRootFile(), configLabelName + ".json");
                    if (configLabelRootFile.exists() && configLabelRootFile.delete()) {
                        Set<Component> components = new ArrayList<>(Arrays.asList(configLabelPanel.getComponents())).stream()
                                .filter(o -> (
                                        StringUtils.isNotBlank(o.getName()) && (o.getName().equals(configLabelName) || o.getName().equals(configLabelName + "_edit") || o.getName().equals(configLabelName + "_remove"))
                                )).collect(Collectors.toSet());

                        for (Component component : components) {
                            configLabelPanel.remove(component);
                        }

                        // 面板重绘
                        configLabelPanel.updateUI();
                    }
                }
            }
        });
        return remove;
    }

    private JLabel createEditLabel(JLabel configLabel) {
        JLabel edit = new JLabel("", IconUtil.getEditIcon(), SwingConstants.LEFT);
        edit.setName(configLabel.getName() + "_edit");
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String old = configLabel.getText();
                String inputString = Messages.showInputDialog("Config Name：", "Code Toolkit Title Info", Messages.getQuestionIcon(), old, new InputValidator() {
                    @Override
                    public boolean checkInput(String inputString) {
                        // 非空校验
                        if (StringUtils.isBlank(inputString))
                            return false;

                        return true;
                    }

                    @Override
                    public boolean canClose(String inputString) {
                        return this.checkInput(inputString);
                    }
                });

                if (StringUtils.isBlank(inputString))
                    return;

                File configLabelRootFile = new File(ideaProject.findProjectConfigLabelRootFile(), old + ".json");
                if (configLabelRootFile.exists() && configLabelRootFile.delete()) {
//                    GenerateUtils.saveProWizardContext(inputString, proWizardContext, ideaProject.getProject());
                    ideaProject.saveLabelConfig(inputString);

                    updateConfigLabel(old, inputString);
                }
            }
        });
        return edit;
    }

    private void updateConfigLabel(String old, String inputString) {
        List<Component> components = new ArrayList<>(Arrays.asList(configLabelPanel.getComponents())).stream().filter(c -> (StringUtils.isNotBlank(c.getName()) && c.getName().contains(old))).collect(Collectors.toList());

        for (Component component : components) {
            JLabel jLabel = (JLabel) component;
            String name = jLabel.getName();
            jLabel.setName(name.replace(old, inputString));
            if (StringUtils.isNotBlank(jLabel.getText()))
                jLabel.setText(inputString);

        }

        configLabelPanel.updateUI();
    }

    private JTable createJTable() {
        final JTable jTable = new JTable() {
            public String getToolTipText(MouseEvent e) {
                int row = this.rowAtPoint(e.getPoint());
                int col = this.columnAtPoint(e.getPoint());
                String tiptextString = null;
                if (row > -1 && col > -1) {
                    Object value = this.getValueAt(row, col);
                    if (null != value && !"".equals(value))
                        tiptextString = value.toString();//悬浮显示单元格内容
                }
                return tiptextString;
            }
        };

        try {
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("是否输出");
            tableModel.addColumn("模板");
            tableModel.addColumn("生成文件名称");
            tableModel.addColumn("生成文件后缀");
            tableModel.addColumn("生成文件路径");
            ProWizardContext proWizardContext = ideaProject.getProWizardContext();
            Map<String, Model> map = proWizardContext.getMap();
            for (File file : CodeToolkitUtils.getJdpTemplateWorkspace().listFiles()) {
                String templateName = file.getName();
                Vector vector = new Vector();
                if (map != null) {
                    Model model = proWizardContext.getMap().get(templateName);
                    if (model != null) {
                        vector.add(Boolean.valueOf(model.isOutEnable()));
                    }
                }
                if (vector.isEmpty())
                    vector.add(Boolean.valueOf(false));

                vector.add(templateName);
                if (map != null) {
                    Model model = proWizardContext.getMap().get(templateName);
                    if (model != null) {
                        if (StringUtils.isNotBlank(model.getOutFileName())) {
                            String[] split = model.getOutFileName().split("\\.");
                            vector.add(split[0]);
                            vector.add("." + split[1]);
                        } else {
                            vector.add("{}");
                            vector.add(".java");
                        }
                        if (StringUtils.isNotBlank(model.getOut()))
                            vector.add(model.getOut());
                        else
                            vector.add("");

                    }
                }
                tableModel.addRow(vector);
            }
            jTable.setModel(tableModel);
            TableColumn tableColumn = jTable.getColumnModel().getColumn(0);
            tableColumn.setMaxWidth(100);
            tableColumn.setWidth(100);
            tableColumn.setCellEditor(jTable.getDefaultEditor(Boolean.class));
            tableColumn.setCellRenderer(jTable.getDefaultRenderer(Boolean.class));
            jTable.getColumnModel().getColumn(1).setWidth(300);
            jTable.getColumnModel().getColumn(1).setMaxWidth(300);
            jTable.getColumnModel().getColumn(2).setWidth(400);
            jTable.getColumnModel().getColumn(2).setMaxWidth(500);
            jTable.getColumnModel().getColumn(3).setWidth(240);
            jTable.getColumnModel().getColumn(3).setMaxWidth(300);
            jTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (jTable.getSelectedColumn() == 4) {// 输出路径
                        choosePath();
                    }
                }
            });
            jTable.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        choosePath();
                    }
                }
            });
            String[] suffixs = {".java", ".xml"};
            JComboBox jbox = new JComboBox(suffixs);
            jbox.setEditable(true);
            jTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(jbox));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jTable;
    }

    private void choosePath() {
        VirtualFile toSelect = rootPathFile;
        ProWizardContext proWizardContext = ideaProject.getProWizardContext();
        if (proWizardContext.isLikeInputFillIn()) {
            String input = Messages.showInputDialog(GeneratorCodeDialog.this, "请输入目录名称", "设置路径", Messages.getInformationIcon());
            if (StringUtils.isNotBlank(input)) {
                // todo

                // 根据输入搜索全局的文件名
                List<File> filesByMask = FileUtil.findFilesOrDirsByMask(Pattern.compile(".*".concat(input).concat(".*")), new File(rootPathFile.getCanonicalPath()));
                // 筛选出的结果
                List<VirtualFile> findResults = new ArrayList<>();
                if (!filesByMask.isEmpty()) {
                    filesByMask.forEach(e -> {
                        try {
                            // 是目录且包含项目根路径
                            if (e.isDirectory()
                                    && e.getCanonicalPath().contains(new File(rootPathFile.getCanonicalPath()).getCanonicalPath())
                            ) {
                                VirtualFile fileByRelativePath = rootPathFile.findFileByRelativePath(
                                        e.getCanonicalPath().replace(
                                                new File(rootPathFile.getCanonicalPath()).getCanonicalPath(), ""
                                        ).replaceAll("\\\\", "/")
                                );
                                if (fileByRelativePath != null
                                        && GlobalSearchScope.projectScope(ideaProject.getProject()).accept(fileByRelativePath)) {
                                    findResults.add(fileByRelativePath);
                                }
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                }

                if (!findResults.isEmpty()) {
                    List<VirtualFile> virtualFiles = findResults.stream().collect(Collectors.toList());

                    if (virtualFiles.size() == 1) {
                        VirtualFile chooseFile = virtualFiles.get(0);
                        jTable.setValueAt(chooseFile.getCanonicalPath().replace(rootPathInput.getText(), ""), jTable.getSelectedRow(), jTable.getSelectedColumn());
                        return;
                    } else {
                        CodeToolkitDialog codeToolkitDialog = new CodeToolkitDialog();
                        codeToolkitDialog.setTitle("匹配结果，请选择");
                        JScrollPane jscrollPane = new JScrollPane();

                        JList<String> jList = new JList<>();
                        final DefaultListModel<String> listModel = new DefaultListModel<String>();
                        virtualFiles.stream().map(VirtualFile::getCanonicalPath).collect(Collectors.toList()).forEach(o -> {
                            listModel.addElement(o.replace(rootPathFile.getCanonicalPath(), ""));
                        });
                        jList.setModel(listModel);
                        jList.setSelectedIndex(0);
                        KeyListener keyListener = new KeyAdapter() {
                            @Override
                            public void keyReleased(KeyEvent e) {
                                // 回车
                                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                                    jTable.setValueAt(jList.getSelectedValue(), jTable.getSelectedRow(), jTable.getSelectedColumn());
                                    codeToolkitDialog.dispose();
                                } else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                                    codeToolkitDialog.dispose();
                                }
                            }
                        };
                        jList.addKeyListener(keyListener);
                        jList.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                // 双击
                                int clickCount = e.getClickCount();
                                if (clickCount == 2) {
                                    jTable.setValueAt(jList.getSelectedValue(), jTable.getSelectedRow(), jTable.getSelectedColumn());
                                    codeToolkitDialog.dispose();
                                }
                            }
                        });
                        jscrollPane.setViewportView(jList);
                        int width = 800, height = 400;
                        jscrollPane.setPreferredSize(new Dimension(width, height));
                        codeToolkitDialog.setContentPane(jscrollPane);
                        CodeToolkitDialogUtils.pack(codeToolkitDialog, width + 50, height);

                        return;
                    }

                }
            }
        } else {
            String value = String.valueOf(jTable.getValueAt(jTable.getSelectedRow(), jTable.getSelectedColumn()));

            if (StringUtils.isNotBlank(value)) {
                toSelect = rootPathFile.findFileByRelativePath(value);
            }
        }

        final FileChooserDescriptor descriptor = new OpenProjectFileChooserDescriptor(true);

        FileChooser.chooseFile(descriptor, ideaProject.getProject(), GeneratorCodeDialog.this, toSelect, chooseFile -> {
            if (StringUtils.isBlank(chooseFile.getCanonicalPath()))
                return;

            if (StringUtils.isBlank(rootPathInput.getText())) {
                jTable.setValueAt(rootPathInput.getText(), jTable.getSelectedRow(), jTable.getSelectedColumn());
            } else {
                if (chooseFile.getCanonicalPath().indexOf(rootPathInput.getText()) >= 0)
                    jTable.setValueAt(chooseFile.getCanonicalPath().replace(rootPathInput.getText(), ""), jTable.getSelectedRow(), jTable.getSelectedColumn());

            }
        });
    }

    private JButton createButtonHelp() {
        JButton buttonHelp = new JButton("Help");

        buttonHelp.addActionListener(e -> {
            CodeToolkitDialog codeToolkitDialog = new CodeToolkitDialog();
            codeToolkitDialog.setTitle("生成代码帮助");
            JScrollPane jscrollPane = new JScrollPane();
            String helpTxt = CodeToolkitUtils.getGeneratorCodeHelpContent();
            EditorTextField editorTextField = UIComponentUtils.createIdeaEditorTextField(ideaProject.getProject(), helpTxt);
            jscrollPane.setViewportView(editorTextField);
            int width = 400, height = 300;
            jscrollPane.setPreferredSize(new Dimension(width, height));
            codeToolkitDialog.setContentPane(jscrollPane);
            CodeToolkitDialogUtils.show(codeToolkitDialog, width + 50, height);
        });
        return buttonHelp;
    }

    private JPanel createSmartPathPanel() {
        JLabel label = new JLabel("路径填写方式");
        label.setPreferredSize(dimension);
        JPanel panel = new JPanel(new BorderLayout(5, 3));
        panel.add(label, BorderLayout.WEST);
        likeInputWay = new JRadioButton("优先按输入匹配");
        ProWizardContext proWizardContext = ideaProject.getProWizardContext();
        likeInputWay.setSelected(proWizardContext.isLikeInputFillIn());
        likeInputWay.addActionListener((e) -> {
            proWizardContext.setLikeInputFillIn(likeInputWay.isSelected());
            saveProWizardContext();
        });
        panel.add(likeInputWay, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRootPathPanel() {
        JLabel rootPathLabel = new JLabel("项目根目录");
        rootPathLabel.setPreferredSize(dimension);
        JButton choosePathBtn = new JButton("choose path");
        choosePathBtn.addActionListener(e -> {
            final FileChooserDescriptor descriptor = new OpenProjectFileChooserDescriptor(true);

            FileChooser.chooseFile(descriptor, ideaProject.getProject(), GeneratorCodeDialog.this, ideaProject.getProject().getProjectFile(), chooseFile -> {
                rootPathInput.setText(chooseFile.getCanonicalPath());
                rootPathFile = chooseFile.getCanonicalFile();
            });
        });
        JPanel rootPathPanel = new JPanel(new BorderLayout(5, 3));
        rootPathPanel.add(choosePathBtn, BorderLayout.EAST);
        rootPathPanel.add(rootPathLabel, BorderLayout.WEST);
        rootPathPanel.add(rootPathInput, BorderLayout.CENTER);
        return rootPathPanel;
    }

    private JPanel createRequestMappingPanel() {
        JLabel requestMappingLabel = new JLabel("requestMapping");
        requestMappingLabel.setPreferredSize(dimension);
        JPanel requestMappingPanel = new JPanel(new BorderLayout(5, 3));
        requestMappingPanel.add(requestMappingLabel, BorderLayout.WEST);
        requestMappingPanel.add(requestMappingInput, BorderLayout.CENTER);
        return requestMappingPanel;
    }

    private JPanel createInputPanel() {
        JLabel packageLabel = new JLabel("package");
        packageLabel.setPreferredSize(dimension);
//        JButton choosePackageBtn = new JButton("choose package");
//        choosePackageBtn.addActionListener(e -> {
//            PackageChooserDialog dialog = new PackageChooserDialog("Package Chooser", ideaProject.getProject());
//            dialog.show();
//            PsiPackage psiPackage = dialog.getSelectedPackage();
//            if (psiPackage != null) {
//                packageInput.setText(psiPackage.getQualifiedName());
//            }
//        });
        JPanel inputPanel = new JPanel(new BorderLayout(5, 3));
//        inputPanel.add(choosePackageBtn, BorderLayout.EAST);
        inputPanel.add(packageLabel, BorderLayout.WEST);
        inputPanel.add(packageInput, BorderLayout.CENTER);
        return inputPanel;
    }

    private JPanel createModulePanel() {
        JLabel moduleLabel = new JLabel("Module");
        moduleLabel.setPreferredSize(dimension);
        moduleJComboBox = new JComboBox<>();
        for (Module module : ModuleManager.getInstance(ideaProject.getProject()).getModules()) {
            moduleJComboBox.addItem(module.getName());
        }
        JPanel modulePanel = new JPanel(new BorderLayout(5, 3));
        modulePanel.add(moduleLabel, BorderLayout.WEST);
        modulePanel.add(moduleJComboBox, BorderLayout.CENTER);
        return modulePanel;
    }

    private void generateCode(IdeaProject ideaProject) {
        ProWizardContext proWizardContext = ideaProject.getProWizardContext();

        try {
            Map mapModel = new HashMap();
            mapModel.put("requestMapping", proWizardContext.getRequestMapping());
            mapModel.put("basepackage", proWizardContext.getPackageStr());
            for (String template :
                    proWizardContext.getMap().keySet()) {
                if (proWizardContext.getMap().get(template).isOutEnable()) {

                    for (File file : CodeToolkitUtils.getJdpTemplateWorkspace().listFiles()) {
                        if (file.getName().equals(template)) {
                            mapModel.put("template", GenerateUtils.getTemplateContextByName(file.getName()));
                            break;
                        }
                    }

                    List<DbTable> dbTableList = CacheDataUtils.getInstance().getDbTableList();
                    for (DbTable dbTable :
                            dbTableList) {
                        Table table = GenerateUtils.getTable(dbTable);

                        String className = table.getClassName();
                        // 文件生成路径
                        String out = proWizardContext.getMap().get(template).getOut() + File.separator + proWizardContext.getMap().get(template).getOutFileName().replace("{}", className);
                        mapModel.put("outRoot", out);

                        GeneratorProperties.setProperties(new Properties(), mapModel);
                        String tableName = table.getSqlName();
                        File file = new File(ideaProject.findProjectConfigRootFile(), tableName + ".json");
                        if (file.exists()) {
                            String jsonString = JsonUtils.txt2String(file.getCanonicalPath());
                            if (StringUtils.isNotBlank(jsonString)) {
                                cn.cym.codetoolkit.entity.config.Table tableConfig = JSON.parseObject(jsonString, cn.cym.codetoolkit.entity.config.Table.class);
                                for (Column column :
                                        table.getColumns()) {
                                    for (cn.cym.codetoolkit.entity.config.Column columnConfig : tableConfig.getColumns()) {
                                        if (column.getSqlName().equals(columnConfig.getName())) {
                                            column.setExts(columnConfig.getExts());
                                        }
                                    }
                                }
                            }
                        }

                        Generator.GeneratorModel generatorModel = GeneratorFacade.GeneratorModelUtils.newFromTable(table);
                        Configuration cfg = new Configuration();
                        String templateContent = (String) generatorModel.templateModel.get("template");
                        String outRoot = (String) generatorModel.templateModel.get("outRoot");
                        StringTemplateLoader stringLoader = new StringTemplateLoader();
                        stringLoader.putTemplate("myTemplate", templateContent);
                        cfg.setTemplateLoader(stringLoader);
                        Template cfgTemplate = cfg.getTemplate("myTemplate", "utf-8");
                        File outFile = new File(rootPathInput.getText(), outRoot);
                        if (outFile.exists()) {
                            // 生成备份的文件
                            String fileName = outFile.getName();
                            if (fileName.contains(".")) {
                                String ext = fileName.substring(fileName.lastIndexOf("."));
                                String name = fileName.substring(0, fileName.lastIndexOf("."));
                                fileName = name + "_2" + ext;
                            } else {
                                fileName += "_2";
                            }
                            outFile = new File(outFile.getParentFile(), fileName);
                        }
                        FileOutputStream fileOutputStream = null;
                        OutputStreamWriter outputStreamWriter = null;
                        Writer writer = null;
                        try {
                            fileOutputStream = new FileOutputStream(outFile);
                            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
                            writer = new BufferedWriter(outputStreamWriter);
                            Map<String, Object> map = new HashMap<>();
                            for (Object key :
                                    generatorModel.templateModel.keySet()) {
                                map.put(key.toString().replace(".", "_"), generatorModel.templateModel.get(key));
                            }
                            generatorModel.templateModel.putAll(map);
                            cfgTemplate.process(generatorModel.templateModel, writer);
                        } finally {
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                            if (outputStreamWriter != null) {
                                outputStreamWriter.close();
                            }
                            if (writer != null) {
                                writer.close();
                            }
                        }
                    }

                }
            }

//            GenerateUtils.saveProWizardContext(proWizardContext, ideaProject.getProject());
            ideaProject.saveProWizardContext();

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    String genertateCodeing = "正在生成代码...";
                    String genertateOk = "生成代码完毕";
                    Messages.showMessageDialog(ideaProject.getProject(), genertateOk, genertateCodeing, Messages.getInformationIcon());
                    VirtualFileManager.getInstance().syncRefresh();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtils.println(e.getMessage());
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    Messages.showMessageDialog(ideaProject.getProject(), e.getMessage(), "发生错误!", Messages.getErrorIcon());
                }
            });
        }
    }

}

