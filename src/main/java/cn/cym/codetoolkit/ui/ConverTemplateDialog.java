package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.utils.CodeToolkitUtils;
import cn.cym.codetoolkit.utils.GenerateUtils;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.IconUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ConverTemplateDialog extends CodeToolkitDialog {

    private Map<JTextField, JComboBox<String>> map = new HashMap<>();

    public ConverTemplateDialog(IdeaProject ideaProject) {
        super();
        setTitle("转化模板");
        super.setIdeaProject(ideaProject);
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 3));
        centerPanel.add(createItemPanel());
        centerPanel.add(createItemPanel());
        centerPanel.add(createItemPanel());
        centerPanel.add(createItemPanel());
        centerPanel.add(createItemPanel());
        centerPanel.add(createOperatePanel());
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jPanel.add(createButtonConver(), BorderLayout.WEST);
        jPanel.add(createButtonCancel(), BorderLayout.WEST);
        contentPane.add(jPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);

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

    private JButton createButtonCancel() {
        JButton buttonCancel = new JButton("Cancel");

        buttonCancel.addActionListener(e -> {
            onCancel();
        });

        return buttonCancel;
    }

    private void onCancel() {
        dispose();
    }

    private void onConver() {
        String input = Messages.showInputDialog(ConverTemplateDialog.this, "Please enter a template name：", "Transformation of the template", Messages.getInformationIcon());
        if (StringUtils.isNotBlank(input)) {
            Editor editor = ideaProject.getAnActionEvent().getData(LangDataKeys.EDITOR);
            if (editor != null && editor.getDocument() != null) {
                String templateContext = editor.getDocument().getText();
                // 替换 $ #
                templateContext = templateContext.replaceAll("\\$", "\\${r'\\$'}").replaceAll("#", "\\${r'#'}");
                // 替换
                for (JTextField key : map.keySet()) {
                    if (key != null && StringUtils.isNotBlank(key.getText())) {
                        JComboBox<String> value = map.get(key);
                        String item = (String) value.getEditor().getItem();
                        if (value != null && StringUtils.isNotBlank(item)) {
                            templateContext = templateContext.replaceAll(key.getText(), "\\${".concat(item).concat("}"));
                        }
                    }
                }
                try {
                    // 插入变量
                    templateContext = CodeToolkitUtils.getCommonFtlContent() + "\n" + templateContext;
                    GenerateUtils.saveTemplateContextToFile(input, templateContext);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.println(e.getMessage());
                }
            }
        }
        dispose();
    }

    private JButton createButtonConver() {
        JButton buttonOK = new JButton("Conver");

        buttonOK.addActionListener(e -> {
            onConver();
        });

        return buttonOK;
    }

    private JPanel createItemPanel() {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 3));
        JTextField key = new JTextField();
        key.setPreferredSize(new Dimension(280, 40));
        rowPanel.add(key);
        JLabel jLabel3 = new JLabel(" => ");
        rowPanel.add(jLabel3);
        JLabel jLabel1 = new JLabel("${");
        rowPanel.add(jLabel1);
//        JTextField value = new JTextField();
        JComboBox<String> value = new JComboBox<>();
        value.setEditable(true);
        value.addItem("className");
        value.addItem("classNameLower");
        value.addItem("basepackage");
        value.addItem("remarks");
        value.addItem("author");
        value.setPreferredSize(new Dimension(280, 40));
        rowPanel.add(value);
        JLabel jLabel2 = new JLabel("}");
        rowPanel.add(jLabel2);
        JButton jButton = new JButton(IconUtil.getRemoveIcon());
        jButton.setPreferredSize(new Dimension(60, 40));
        rowPanel.add(jButton);
        map.put(key, value);
        return rowPanel;
    }

    private JPanel createOperatePanel() {
        JPanel rowPanel = new JPanel(new GridLayout(1, 1));
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 3));
        JButton add = new JButton(IconUtil.getAddIcon());
        add.setPreferredSize(new Dimension(60, 40));
        JButton clear = new JButton("clear");
        clear.setPreferredSize(new Dimension(60, 40));
        clear.addActionListener((e) -> {
            for (JTextField jTextField : map.keySet()) {
                jTextField.setText("");
                map.get(jTextField).getEditor().setItem("");
            }
        });
        // todo add
        jPanel.add(add);
        jPanel.add(clear);
        rowPanel.add(jPanel);
        return rowPanel;
    }
}
