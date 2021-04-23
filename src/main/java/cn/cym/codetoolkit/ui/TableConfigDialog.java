package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.config.ExtColumn;
import cn.cym.codetoolkit.utils.GenerateUtils;
import cn.cym.codetoolkit.utils.IoUtils;
import cn.cym.codetoolkit.utils.JsonUtils;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import com.alibaba.fastjson.JSON;
import cn.cym.codetoolkit.utils.CacheDataUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TableConfigDialog extends CodeToolkitDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tableConfig;

    public TableConfigDialog(IdeaProject ideaProject) {
        DbTable selectDbTable = CacheDataUtils.getInstance().getSelectDbTable();
        Table table = GenerateUtils.getTable(selectDbTable);

        contentPane = new JPanel(new BorderLayout());
        tableConfig = new JTable();
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(tableConfig);
        contentPane.add(jScrollPane, BorderLayout.CENTER);
        buttonOK = new JButton("Ok");
        buttonCancel = new JButton("Cancel");
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jPanel.add(buttonOK, BorderLayout.WEST);
        jPanel.add(buttonCancel, BorderLayout.WEST);
        contentPane.add(jPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(table.getSqlName());

        File tableFile = new File(ideaProject.findProjectConfigRootFile(), table.getSqlName() + ".json");
        String jsonString = null;
        try {
            if (!tableFile.exists())
                tableFile.createNewFile();
            jsonString = JsonUtils.txt2String(tableFile.getAbsolutePath());
            if (StringUtils.isBlank(jsonString))
                jsonString = "{}";
        } catch (IOException e) {
            e.printStackTrace();
        }
        cn.cym.codetoolkit.entity.config.Table saveTable = JSON.parseObject(jsonString, cn.cym.codetoolkit.entity.config.Table.class);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("字段名称");
        tableModel.addColumn("字段映射名称");
        tableModel.addColumn("字段映射类型");
        tableModel.addColumn("关联策略");
        tableModel.addColumn("关联表");
        tableModel.addColumn("关联字段");
        String jdpColumnConfig = PropertiesComponent.getInstance().getValue("jdp_column_config");
        List<ExtColumn> jdpColumnConfigs = JSON.parseArray(jdpColumnConfig, ExtColumn.class);
        if (jdpColumnConfigs != null && !jdpColumnConfigs.isEmpty()) {
            for (ExtColumn ext : jdpColumnConfigs) {
                tableModel.addColumn(ext.getName());
            }
        }
        LinkedHashSet<Column> columns = table.getColumns();
        for (Column column :
                columns) {
            Vector vector = new Vector();
            vector.add(column.getSqlName());
            vector.add(column.getColumnName());
            vector.add(column.getJavaType());
            vector.add("");
            vector.add("");
            vector.add("");
            if (saveTable != null && saveTable.getColumns() != null) {
                for (cn.cym.codetoolkit.entity.config.Column saveColumn :
                        saveTable.getColumns()) {
                    if (saveColumn.getName().equals(column.getSqlName())) {
                        List<ExtColumn> exts = saveColumn.getExts();
                        for (ExtColumn extColumn :
                                exts) {
                            if ("BOOLEAN".equals(extColumn.getType())) {
                                vector.add(Boolean.valueOf(extColumn.getValue()));
                            } else {
                                vector.add(extColumn.getValue());
                            }
                        }
                        break;
                    }
                }
            }
            tableModel.addRow(vector);
        }
        tableConfig.setModel(tableModel);
        if (jdpColumnConfigs != null && !jdpColumnConfigs.isEmpty()) {
            for (int i = 0; i < jdpColumnConfigs.size(); i++) {
                ExtColumn ext = jdpColumnConfigs.get(i);
                if ("BOOLEAN".equals(ext.getType())) {
                    TableColumn tableColumn = tableConfig.getColumnModel().getColumn(6 + i);
                    tableColumn.setCellEditor(tableConfig.getDefaultEditor(Boolean.class));
                    tableColumn.setCellRenderer(tableConfig.getDefaultRenderer(Boolean.class));
                }
            }
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!tableFile.exists()) {
                    try {
                        tableFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                if (jdpColumnConfigs != null && !jdpColumnConfigs.isEmpty()) {
                    List<cn.cym.codetoolkit.entity.config.Column> list = new ArrayList<>();
                    int len = tableConfig.getModel().getRowCount();// 字段行数
                    for (int i = 0; i < len; i++) { // 遍历行
                        List<ExtColumn> extColumns = new ArrayList<>();
                        for (int l = 0; l < jdpColumnConfigs.size(); l++) {
                            extColumns.add(jdpColumnConfigs.get(l).clone());
                        }
                        int columnLen = tableConfig.getModel().getColumnCount();// 总列数
                        for (int j = 6; j < columnLen; j++) { // 第7列开始
                            for (int k = 0; k < extColumns.size(); k++) { // 扩展列
                                if (j - 6 == k) {
                                    if (tableConfig.getModel().getValueAt(i, j) != null) {
                                        extColumns.get(k).setValue(String.valueOf(tableConfig.getModel().getValueAt(i, j)));
                                        break;
                                    }
                                }
                            }
                        }
                        String columnName = (String) tableConfig.getModel().getValueAt(i, 0);
                        list.add(new cn.cym.codetoolkit.entity.config.Column(columnName, extColumns));
                    }
                    String context = JsonUtils.toString(new cn.cym.codetoolkit.entity.config.Table(table.getSqlName(), list), true);
                    try {
                        IoUtils.appendTxtFile(tableFile, context, IoUtils.ENCODING_UTF8);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        VirtualFileManager.getInstance().syncRefresh();
                        onOK();
                    }
                });
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

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
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
    }

}
