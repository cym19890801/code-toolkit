package cn.cym.codetoolkit.bundle;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

/**
 * 配置常量
 * @author cym
 * @date 2018/9/11
 */
public class JdpResourceBundle {
    @NotNull
    private static final String BUNDLE_NAME = "cn.cym.codetoolkit.bundle.JdpResourceBundle";
    @NotNull
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * 模板、POM服务器的地址等
     */
    public static final String NET_ADDRESS = JdpResourceBundle.message("NET_ADDRESS");
    public static final String NET_PRECEPT = JdpResourceBundle.message("NET_PRECEPT");
    public static final String NET_POM = JdpResourceBundle.message("NET_POM");
    /**
     * 服务端地址
     */
    public static final String JDP_SERVER_URL = JdpResourceBundle.message("JDP_SERVER_URL");
    /**
     * 环境信息接口地址
     */
    public static final String JDP_SERVER_API_ENV_INFO = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_ENV_INFO");
    /**
     * 操作信息采集接口地址
     */
    public static final String JDP_SERVER_API_OPERATION = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_OPERATION");
    /**
     * 操作信息批量采集接口地址
     */
    public static final String JDP_SERVER_API_OPERATION_BATCH = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_OPERATION_BATCH");
    /**
     * 获取所有Springboot版本号
     */
    public static final String JDP_SERVER_API_SPRING_BOOT_ALL_VERSION = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_SPRING_BOOT_ALL_VERSION");
    /**
     * 根据springboot版本获取组件列表
     */
    public static final String JDP_SERVER_API_COMPONENTS = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_COMPONENTS");
    /**
     * 使用手册
     */
    public static final String JDP_SERVER_API_MANUAL = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_MANUAL");
    /**
     * 根据组件坐标版本获取组件信息
     */
    public static final String JDP_SERVER_API_COMPONENT_INFO = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_COMPONENT_INFO");
    /**
     * 组件使用计数+1
     */
    public static final String JDP_SERVER_API_COMPONENT_USAGE = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_COMPONENT_USAGE");
    /**
     * jdp log
     */
    public static final String JDP_LOGO = JDP_SERVER_URL + JdpResourceBundle.message("JDP_LOGO");
    /**
     * jdp 最新版本号
     */
    public static final String JDP_SERVER_API_VERSION_NEWEST = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_VERSION_NEWEST");
    /**
     * 插件版本列表
     */
    public static final String JDP_SERVER_API_PLUG_VERSION_LIST = JDP_SERVER_URL + JdpResourceBundle.message("JDP_SERVER_API_PLUG_VERSION_LIST");
    public static final String USER_LOGIN_URL =  JdpResourceBundle.message("USER_LOGIN_URL");
    public static final String CLIENT_ID =  JdpResourceBundle.message("CLIENT_ID");
    public static final String CLIENT_SECRET =  JdpResourceBundle.message("CLIENT_SECRET");
    public static final String USER_INFO_URL =  JdpResourceBundle.message("USER_INFO_URL");

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
        return CommonBundle.message(BUNDLE, key, params);
    }

}