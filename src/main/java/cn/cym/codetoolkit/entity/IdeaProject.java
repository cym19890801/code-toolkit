package cn.cym.codetoolkit.entity;

import cn.cym.codetoolkit.constant.ProjectConstants;
import cn.cym.codetoolkit.log.LogUtils;
import cn.cym.codetoolkit.utils.IoUtils;
import cn.cym.codetoolkit.utils.JsonUtils;
import cn.cym.codetoolkit.utils.ZipUtils;
import com.alibaba.fastjson.JSON;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.VfsImplUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author cym
 * @date 2018/10/16
 */
public class IdeaProject {

    private static final Logger logger = LoggerFactory.getLogger(IdeaProject.class);

    /**
     * 当前选中的VirtualFile
     */
    private VirtualFile virtualFile;

    /**
     * 当前选中的目录路径
     */
    private String basePath;

    /**
     * 当前选中的目录名称
     */
    private String name;

    /**
     * 当前窗口打开的工程
     */
    private Project project;

    private boolean createModule;

    private AnActionEvent anActionEvent;

    private ProWizardContext proWizardContext;

    public IdeaProject(AnActionEvent anActionEvent, Project project){
        logger.info("idea project init...");
        this.anActionEvent = anActionEvent;
        this.virtualFile = anActionEvent.getData(LangDataKeys.VIRTUAL_FILE);
        logger.info("virtualFile:" + this.virtualFile);
        if (this.virtualFile != null){
            this.basePath = this.virtualFile.getCanonicalPath();
            logger.info("basePath:" + this.basePath);
            this.name = this.virtualFile.getName();
            logger.info("name:" + this.name);
        }
        this.project = project;
        this.proWizardContext = loadProjectGenerteInfoFromDisk();
        logger.info("project:" + project);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public boolean isCreateModule() {
        return createModule;
    }

    public void setCreateModule(boolean createModule) {
        this.createModule = createModule;
    }

    public AnActionEvent getAnActionEvent() {
        return anActionEvent;
    }

    public void setAnActionEvent(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
    }

    public ProWizardContext getProWizardContext() {
        return proWizardContext;
    }

    public void setProWizardContext(ProWizardContext proWizardContext) {
        this.proWizardContext = proWizardContext;
    }

    @Override
    public String toString() {
        return "IdeaProject{" +
                "virtualFile=" + virtualFile +
                ", basePath='" + basePath + '\'' +
                ", name='" + name + '\'' +
                ", project=" + project.getName() +
                ", createModule=" + createModule +
                '}';
    }

    /**
     * 查找工程配置根文件
     * @return
     */
    public File findProjectConfigRootFile() {
        File file = new File(project.getProjectFile().getParent().getCanonicalPath(), ProjectConstants.CODE_TOOLKIT);
        if (!file.exists())
            file.mkdir();

        return file;
    }

    public File findProjectConfigLabelRootFile() {
        File file = new File(findProjectConfigRootFile(), ProjectConstants.CONFIG_LABEL);
        if (!file.exists())
            file.mkdir();

        return file;
    }

    /**
     * 查找工程生成配置文件
     * @return
     * @throws IOException
     */
    public File findProjectGenerteInfoFile() throws IOException {
        File file = new File(findProjectConfigRootFile(), ProjectConstants.GENERATE_INFO_JSON);
        if (!file.exists())
            file.createNewFile();

//        LocalFileSystem.getInstance().refresh(false);

        return file;
    }

    public File createNewProjectGenerteInfoZipFile() throws IOException {
        File file = new File(findProjectConfigRootFile(), ProjectConstants.GENERATE_INFO_JSON_ZIP);
        if (file.exists())
            file.delete();

        file.createNewFile();

        return file;
    }

    public File findProjectGenerteInfoBakFile() throws IOException {
        return new File(findProjectConfigRootFile(), ProjectConstants.GENERATE_INFO_JSON_BAK);
    }

    public File createNewProjectGenerteInfoBakFile() throws IOException {
        File file = findProjectGenerteInfoBakFile();
        if (file.exists())
            file.delete();

        file.createNewFile();

        return file;
    }

    /**
     * 生成配置文件标签根目录
     * @return
     */
    public File findProjectGenerteConfigLabelRootFile() {
        File file = new File(findProjectConfigRootFile(), ProjectConstants.CONFIG_LABEL);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    /**
     * 工程生成配置文件
     * @return
     */
    public ProWizardContext loadProjectGenerteInfoFromDisk() {
        try {
            File projectGenerteInfoFile = findProjectGenerteInfoFile();
//            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(projectGenerteInfoFile);
            String jsonString = JsonUtils.txt2String(projectGenerteInfoFile.getCanonicalPath());
            if (StringUtils.isBlank(jsonString))
                jsonString = "{}";

            return JSON.parseObject(jsonString, ProWizardContext.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO generateInfo.json文件异常，可能的原因是generateInfo.json文件不存在，或者generateInfo.json文件不符合");
        }
    }

    public void reloadProWizardContextFromDisk() {
        VirtualFileManager.getInstance().syncRefresh();
        proWizardContext = loadProjectGenerteInfoFromDisk();
    }

    public void saveProWizardContext() {
        String context = JsonUtils.toString(proWizardContext, true);
        try {
            File file = findProjectGenerteInfoFile();
            IoUtils.writeTxtFile(file, context, IoUtils.ENCODING_UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveProWizardContext(String configName, ProWizardContext proWizardContext) throws Exception {
        String context = JsonUtils.toString(proWizardContext, true);
        File file = new File(findProjectGenerteConfigLabelRootFile(), configName + ".json");
        IoUtils.writeTxtFile(file, context);
    }

    public void saveProWizardContextForBak() {
        try {
            File bakFile = createNewProjectGenerteInfoBakFile();
            ProWizardContext proWizardContext = JSON.parseObject(JsonUtils.toString(loadProjectGenerteInfoFromDisk(), true), ProWizardContext.class);
            String rootPath = proWizardContext.getRootPath();
            proWizardContext.setRootPath(rootPath.replace(project.getBasePath(), "{}"));
            String context = JsonUtils.toString(proWizardContext, true);
            IoUtils.writeTxtFile(bakFile, context, IoUtils.ENCODING_UTF8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cutProjectConfigBy(String configName) {
        try {
            File file = new File(findProjectGenerteConfigLabelRootFile(), configName + ".json");
            String readTxtFile = IoUtils.readTxtFile(file);
            IoUtils.writeTxtFile(findProjectGenerteInfoFile(), readTxtFile);
            setProWizardContext(loadProjectGenerteInfoFromDisk());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void saveLabelConfig(String configName) {
        String context = JsonUtils.toString(proWizardContext, true);
        try {
            File file = new File(findProjectConfigLabelRootFile(), configName + ".json");
            IoUtils.writeTxtFile(file, context);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void zipBakFile() {
        try {
            saveProWizardContextForBak();
            File zip = createNewProjectGenerteInfoZipFile();
            //　压缩文件
            ZipUtils.zipFile(findProjectGenerteInfoBakFile().getCanonicalPath(), zip.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
