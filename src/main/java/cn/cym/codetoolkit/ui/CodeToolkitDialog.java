package cn.cym.codetoolkit.ui;

import cn.cym.codetoolkit.entity.IdeaProject;
import cn.cym.codetoolkit.entity.ProWizardContext;

import javax.swing.*;

/**
 * @author cym
 * @date 2018/10/3
 */
public class CodeToolkitDialog extends JDialog {

    protected IdeaProject ideaProject;
//    protected ProWizardContext proWizardContext;

    public IdeaProject getIdeaProject() {
        return ideaProject;
    }

    public void setIdeaProject(IdeaProject ideaProject) {
        this.ideaProject = ideaProject;
//        this.proWizardContext = ideaProject.getProWizardContext();
//        proWizardContext = GenerateUtils.getLocalProjectInfo(ideaProject.getProject());
    }

    protected void saveProWizardContext() {
        ideaProject.saveProWizardContext();

//        GenerateUtils.saveProWizardContext(proWizardContext, ideaProject.getProject());
    }

    public void showDialog() {
        this.setVisible(true);
    }

}
