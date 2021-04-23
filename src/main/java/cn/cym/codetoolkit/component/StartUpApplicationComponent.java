package cn.cym.codetoolkit.component;

import cn.cym.codetoolkit.utils.CodeToolkitUtils;
import com.intellij.openapi.components.ApplicationComponent;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IDEA启动
 * @author cym
 * @date 2018/10/3
 */
public class StartUpApplicationComponent implements ApplicationComponent {

    static {
        PropertyConfigurator.configure(StartUpApplicationComponent.class.getResource("/log4j.properties"));
    }

    private static Logger logger = LoggerFactory.getLogger(StartUpApplicationComponent.class);

    @Override
    public void initComponent() {
        CodeToolkitUtils.mkdirCodeToolkitWorkspace();

        CodeToolkitUtils.mkdirCodeToolkitTemplate();
    }

}
