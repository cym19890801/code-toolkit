package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.entity.IdeaProject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
* 
* <br>CreateDate December 23,2019
* @author chenyouming
* @since 1.0
**/
public class UnitTestCoverageDialog extends CodeToolkitDialog {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UnitTestCoverageDialog.class);

    PsiFile[] interfaceFiles = null;
    PsiFile[] testFiles = null;

    public UnitTestCoverageDialog(IdeaProject ideaProject) {
        JPanel jPanel = new JPanel();
        setTitle("单元测试覆盖率");
        setContentPane(jPanel);

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        JPanel centerPanel = new JPanel();
        contentPane.add(centerPanel, BorderLayout.CENTER);

        JPanel upPanel = new JPanel(new GridLayout(3, 1));
        Dimension dimension = new Dimension(100, -1);

        JLabel packageLabel = new JLabel("interface");
        packageLabel.setPreferredSize(dimension);
        JTextField packageInput = new JTextField();
        packageInput.setEnabled(false);
        JButton choosePackageBtn = new JButton("choose package");
        choosePackageBtn.addActionListener(e -> {
//            PackageChooserDialog dialog = new PackageChooserDialog("Interface Package Chooser", ideaProject.getProject());
//            dialog.show();
//            PsiPackage psiPackage = dialog.getSelectedPackage();
//            if (psiPackage != null) {
//                interfaceFiles = psiPackage.getFiles(GlobalSearchScope.allScope(ideaProject.getProject()));
//                packageInput.setText(psiPackage.getQualifiedName());
//            }
        });
        JPanel inputPanel = new JPanel(new BorderLayout(5, 3));
        inputPanel.add(choosePackageBtn, BorderLayout.EAST);
        inputPanel.add(packageLabel, BorderLayout.WEST);
        inputPanel.add(packageInput, BorderLayout.CENTER);

        JLabel testLabel = new JLabel("test");
        testLabel.setPreferredSize(dimension);
        JTextField testInput = new JTextField();
        testInput.setEnabled(false);
        JButton chooseTestPackageBtn = new JButton("choose package");
        chooseTestPackageBtn.addActionListener(e -> {
//            PackageChooserDialog dialog = new PackageChooserDialog("Test Package Chooser", ideaProject.getProject());
//            dialog.show();
//            PsiPackage psiPackage = dialog.getSelectedPackage();
//            if (psiPackage != null) {
//                testFiles = psiPackage.getFiles(GlobalSearchScope.allScope(ideaProject.getProject()));
//                testInput.setText(psiPackage.getQualifiedName());
//            }
        });
        JPanel testPanel = new JPanel(new BorderLayout(5, 3));
        testPanel.add(chooseTestPackageBtn, BorderLayout.EAST);
        testPanel.add(testLabel, BorderLayout.WEST);
        testPanel.add(testInput, BorderLayout.CENTER);

        upPanel.add(inputPanel);
        upPanel.add(testPanel);

        contentPane.add(upPanel, BorderLayout.NORTH);

        JButton buttonOK = new JButton("Ok");
        JButton buttonCancel = new JButton("Cancel");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(buttonOK);
        btnPanel.add(buttonCancel);
        contentPane.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);

        buttonOK.addActionListener(e -> {
            ProgressManager.getInstance().run(new Task.Backgroundable(ideaProject.getProject(), "正在扫描，请稍候...", false) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    ApplicationManager.getApplication().runReadAction(() -> {
                        try {
                            // 进度计数
                            double count = 0;
                            if (interfaceFiles != null && testFiles != null) {
                                java.util.List<PsiFile> testFiles2 = new ArrayList<PsiFile>();
                                for (PsiFile testFile : testFiles) {
//                                    if (testFile instanceof PsiJavaFile) {
//                                        PsiJavaFile psiFile = (PsiJavaFile) testFile;
//                                        PsiClass[] classes = psiFile.getClasses();
//                                        if (classes[0].getName().endsWith("Test")) {
//                                            testFiles2.add(testFile);
//                                        }
//                                    }
                                }

                                java.util.List<String> unfindTests = new ArrayList<String>();

                                for (PsiFile interfaceFile : interfaceFiles) {
//                                    if (interfaceFile instanceof PsiJavaFile && interfaceFile.getName().endsWith(".java")) {
//                                        PsiJavaFile psiFile = (PsiJavaFile) interfaceFile;
//                                        PsiClass[] classes = psiFile.getClasses();
//                                        if (classes[0].isInterface()) {
//                                            PsiMethod[] methods = classes[0].getMethods();
//                                            for (PsiMethod method : methods) {
//                                                boolean isFindTest = false;
//                                                Query<PsiReference> search = ReferencesSearch.search(method);
//                                                for (PsiReference psiReference : search) {
//                                                    // 引用该接口方法的类
//                                                    PsiFile containingFile = psiReference.getElement().getContainingFile();
//                                                    if (testFiles2.contains(containingFile)) {
//                                                        isFindTest = true;
//                                                        break ;
//                                                    }
//                                                }
//                                                if (!isFindTest)
//                                                    unfindTests.add("【"+interfaceFile.getName()+"】, 方法【" + method.getName() + "】未找到单元测试");
//                                            }
//                                        }
//                                    }
                                    progressIndicator.setFraction(count++ / (double) interfaceFiles.length);
                                }

                                for (String unfindTest : unfindTests) {
                                    logger.warn(unfindTest);
                                }
                            }
                            progressIndicator.setText("扫描完成....");
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            dispose();
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
}

