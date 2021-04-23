package cn.cym.codetoolkit.entity;

import java.util.*;

/**
 * 项目向导设置载体类
 * @author xgchen cym
 */
public class ProWizardContext {

	private static final long serialVersionUID = 3864806341273062875L;

	/**
	 * 配置名称
	 */
	public String name;
	/**
	 * 生成文件根路径
	 */
	private String rootPath;
	private String requestMapping;
	public String projectName;
	public String locationPath;
	public String packageStr;
	public boolean likeInputFillIn = true;
	public boolean printLog = false;
	private JdpServer jdpServer;
	public Map<String, String> templates = new HashMap<>();
	public Map<String, String> outRoots = new HashMap<>();
	private Map<String, Model> map = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getRequestMapping() {
		return requestMapping;
	}

	public void setRequestMapping(String requestMapping) {
		this.requestMapping = requestMapping;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getLocationPath() {
		return locationPath;
	}

	public void setLocationPath(String locationPath) {
		this.locationPath = locationPath;
	}

	public String getPackageStr() {
		return packageStr;
	}

	public void setPackageStr(String packageStr) {
		this.packageStr = packageStr;
	}

	public boolean isLikeInputFillIn() {
		return likeInputFillIn;
	}

	public void setLikeInputFillIn(boolean likeInputFillIn) {
		this.likeInputFillIn = likeInputFillIn;
	}

	public boolean isPrintLog() {
		return printLog;
	}

	public void setPrintLog(boolean printLog) {
		this.printLog = printLog;
	}

	public JdpServer getJdpServer() {
		return jdpServer;
	}

	public void setJdpServer(JdpServer jdpServer) {
		this.jdpServer = jdpServer;
	}

	public Map<String, String> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, String> templates) {
		this.templates = templates;
	}

	public Map<String, String> getOutRoots() {
		return outRoots;
	}

	public void setOutRoots(Map<String, String> outRoots) {
		this.outRoots = outRoots;
	}

	public Map<String, Model> getMap() {
		return map;
	}

	public void setMap(Map<String, Model> map) {
		this.map = map;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProWizardContext that = (ProWizardContext) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public static class JdpServer {
		private String host;

		private Integer port;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}
	}
}
