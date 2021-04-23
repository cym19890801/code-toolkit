package cn.org.rapid_framework.generator;


import cn.org.rapid_framework.generator.Generator.GeneratorModel;
import cn.org.rapid_framework.generator.common.GeneratorConsts;
import cn.org.rapid_framework.generator.provider.db.sql.model.Sql;
import cn.org.rapid_framework.generator.provider.db.table.TableFactory;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.provider.java.model.JavaClass;
import cn.org.rapid_framework.generator.util.BeanHelper;
import cn.org.rapid_framework.generator.util.GLogger;
import cn.org.rapid_framework.generator.util.GeneratorException;
import cn.org.rapid_framework.generator.util.StringHelper;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 
 * @author badqiu
 *
 */
public class GeneratorFacade {
    private static final Logger logger= LoggerFactory.getLogger(GeneratorFacade.class);
    
	public Generator g = new Generator();
	public GeneratorFacade(){
		g.setOutRootDir(GeneratorProperties.getProperty(GeneratorConsts.CONFIG_TEMPLATE_OUTPUT_DIRECTORY));
		g.setOverrideGenerate(Boolean.parseBoolean(GeneratorProperties.getProperty(GeneratorConsts.CONFIG_OVERRIDE_GENERATE, GeneratorConsts.CONFIG_OVERRIDE_GENERATE_DEFAULT)));
	}
	public GeneratorFacade(Map<String,Object> map){
	    logger.debug(MessageFormat.format("传入参数：{0}",map));
//	    g.setModel(map);
	    GeneratorContext.setContext(map);
		g.setOutRootDir(new GeneratorProperties(map).getProperty(GeneratorConsts.CONFIG_TEMPLATE_OUTPUT_DIRECTORY));
		g.setOverrideGenerate(Boolean.parseBoolean(GeneratorProperties.getProperty(GeneratorConsts.CONFIG_OVERRIDE_GENERATE, GeneratorConsts.CONFIG_OVERRIDE_GENERATE_DEFAULT)));
	}
	
	public static void printAllTableNames() throws Exception {
		PrintUtils.printAllTableNames(TableFactory.getInstance().getAllTables());
	}
	public static List getAllTables()throws Exception{
		return TableFactory.getInstance().getAllTables();
	}
	public void deleteOutRootDir() throws IOException {
		g.deleteOutRootDir();
	}
	
	public void generateByMap(Map map,String templateRootDir) throws Exception {
		new ProcessUtils().processByMap(map, templateRootDir,false);
	}

	public void deleteByMap(Map map,String templateRootDir) throws Exception {
		new ProcessUtils().processByMap(map, templateRootDir,true);
	}
	
	public void generateByAllTable(String templateRootDir) throws Exception {
		new ProcessUtils().processByAllTable(templateRootDir,false);
		
	}
	
	public void deleteByAllTable(String templateRootDir) throws Exception {
		new ProcessUtils().processByAllTable(templateRootDir,true);		
	}
	
    public void generateByTable(String tableName,String templateRootDir) throws Exception {
    	new ProcessUtils().processByTable(tableName,templateRootDir,false);
	}

	public void generateByTable(String tableName) throws Exception {
		Table table = TableFactory.getInstance().getTable(tableName);
		GeneratorModel tableMap = GeneratorModelUtils.newFromTable(table);
		Configuration cfg = new Configuration();
		String templateContent = (String)tableMap.templateModel.get("template");
		String outRoot = (String)tableMap.templateModel.get("outRoot");
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate("myTemplate",templateContent);
		cfg.setTemplateLoader(stringLoader);
		Template template = cfg.getTemplate("myTemplate","utf-8");
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outRoot)),"utf-8"));
		template.process(tableMap.templateModel , out);
	}

    public void deleteByTable(String tableName,String templateRootDir) throws Exception {
    	new ProcessUtils().processByTable(tableName,templateRootDir,true);
	}
    
	public void generateByClass(Class clazz,String templateRootDir) throws Exception {
		new ProcessUtils().processByClass(clazz, templateRootDir,false);
	}

	public void deleteByClass(Class clazz,String templateRootDir) throws Exception {
		new ProcessUtils().processByClass(clazz, templateRootDir,true);
	}
	
	public void generateBySql(Sql sql, String templateRootDir) throws Exception {
		new ProcessUtils().processBySql(sql,templateRootDir,false);
	}

	public void deleteBySql(Sql sql, String templateRootDir) throws Exception {
		new ProcessUtils().processBySql(sql,templateRootDir,true);
	}
	
    private Generator getGenerator(String templateRootDir) {
    	String work_space_addr= GeneratorProperties.getProperty("work_space_addr");
    	logger.debug(MessageFormat.format("templateRootDir：{0},work_space_addr：{1}",templateRootDir,work_space_addr));
    	if(StringHelper.isNotBlank(work_space_addr)){
    	    logger.info(MessageFormat.format("模板地址：{0}",new File(GeneratorProperties.getProperty("templateRootAddr")+templateRootDir).getAbsoluteFile()));
            g.setTemplateRootDir(new File(GeneratorProperties.getProperty("templateRootAddr")+templateRootDir).getAbsoluteFile());
    	}else{
    	    logger.info(MessageFormat.format("模板地址：{0}",new File(templateRootDir).getAbsolutePath()));
            g.setTemplateRootDir(new File(templateRootDir).getAbsoluteFile());
    	}
    	
        return g;
    }
    
    /** 生成器的上下文，存放的变量将可以在模板中引用 */
    public static class GeneratorContext {
        static ThreadLocal<Map> context = new ThreadLocal<Map>();
        public static void clear() {
            Map m = context.get();
            if(m != null) m.clear();
        }
        public static Map getContext() {
            Map map = context.get();
            if(map == null) {
                setContext(new HashMap());
            }
            return context.get();
        }
        public static void setContext(Map map) {
            context.set(map);
        }
        public static void put(String key,Object value) {
            getContext().put(key, value);
        }
    }
    
    public class ProcessUtils {
        
        private final Logger logger= LoggerFactory.getLogger(ProcessUtils.class);
        
    	public void processByMap(Map params, String templateRootDir,boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = getGenerator(templateRootDir);
			GeneratorModel m = GeneratorModelUtils.newFromMap(params);
			processByGeneratorModel(templateRootDir, isDelete, g, m);
    	}
    	
    	public void processBySql(Sql sql, String templateRootDir, boolean isDelete) throws Exception {
    		Generator g = getGenerator(templateRootDir);
    		GeneratorModel m = GeneratorModelUtils.newFromSql(sql);
    		PrintUtils.printBeginProcess("sql:"+sql.getSourceSql(),isDelete);
    		processByGeneratorModel(templateRootDir,isDelete,g,m);
    	}   
    	
    	public void processByClass(Class clazz, String templateRootDir,boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = getGenerator(templateRootDir);
			GeneratorModel m = GeneratorModelUtils.newFromClass(clazz);
			PrintUtils.printBeginProcess("JavaClass:"+clazz.getSimpleName(),isDelete);
			processByGeneratorModel(templateRootDir, isDelete, g, m);
    	}

        private void processByGeneratorModel(String templateRootDir,
                                             boolean isDelete, Generator g,
                                             GeneratorModel m)
                                                              throws Exception,
                                                              FileNotFoundException {
            try {
				if(isDelete)
					g.deleteBy(m.templateModel, m.filePathModel);
				else
					g.generateBy(m.templateModel, m.filePathModel);
			}catch(GeneratorException ge) {
				PrintUtils.printExceptionsSumary(ge.getMessage(),getGenerator(templateRootDir).getOutRootDir(),ge.getExceptions());
			}
        }
    	
        public void processByTable(String tableName,String templateRootDir,boolean isDelete) throws Exception {
        	if("*".equals(tableName)) {
        	    if(isDelete)
        	        deleteByAllTable(templateRootDir);
        	    else
        	        generateByAllTable(templateRootDir);
        		return;
        	}
    		Generator g = getGenerator(templateRootDir);
    		Table table = TableFactory.getInstance().getTable(tableName);
    		processByTable(g,table,isDelete);
    		/*try {
    			processByTable(g,table,isDelete);
    		}catch(GeneratorException ge) {
    			PrintUtils.printExceptionsSumary(ge.getMessage(),getGenerator(templateRootDir).getOutRootDir(),ge.getExceptions());
    		}*/
    	}    
        
		public void processByAllTable(String templateRootDir,boolean isDelete) throws Exception {
			List<Table> tables = TableFactory.getInstance().getAllTables();
			List exceptions = new ArrayList();
			for(int i = 0; i < tables.size(); i++ ) {
				try {
					processByTable(getGenerator(templateRootDir),tables.get(i),isDelete);
				}catch(GeneratorException ge) {
					exceptions.addAll(ge.getExceptions());
				}
			}
			
			PrintUtils.printExceptionsSumary("",getGenerator(templateRootDir).getOutRootDir(),exceptions);
		}
		
		public void processByTable(Generator g, Table table, boolean isDelete) throws Exception {
	        GeneratorModel m = GeneratorModelUtils.newFromTable(table);
//	        GeneratorModel m = GeneratorModelUtils.newFromModelAndTable(g, table);
	        
	        PrintUtils.printBeginProcess(table.getSqlName()+" => "+table.getClassName(),isDelete);
	        if(isDelete){
	        	g.deleteBy(m.templateModel,m.filePathModel);
	        }else{
	        	g.generateBy(m.templateModel,m.filePathModel);
	        } 
	    }        
    }
	
    @SuppressWarnings("all")
	public static class GeneratorModelUtils {
		
		public static GeneratorModel newFromTable(Table table) throws Exception {
			Map templateModel = new HashMap();
			setShareVars(templateModel);
			logger.info("=== templateModel ===");
//			logger.info(new ObjectMapper().writeValueAsString(templateModel));
			templateModel.put("table", table);
			
			Map filePathModel = new HashMap();
			setShareVars(filePathModel);
			filePathModel.putAll(BeanHelper.describe(table));
			return new GeneratorModel(templateModel,filePathModel);
		}
		
//		public static GeneratorModel newFromModelAndTable(Generator generator, Table2 table) throws Exception {
//			Map templateModel = new HashMap();
//			templateModel.put("table", table);
//			templateModel.put("model", generator.getModel());
//			setShareVars(templateModel);
//			
//			Map filePathModel = new HashMap();
//			setShareVars(filePathModel);
//			filePathModel.putAll(BeanHelper.describe(table));
//			return new GeneratorModel(templateModel,filePathModel);
//		}

		public static GeneratorModel newFromSql(Sql sql) throws Exception {
			Map templateModel = new HashMap();
			templateModel.put("sql", sql);
			setShareVars(templateModel);
			
			Map filePathModel = new HashMap();
			setShareVars(filePathModel);
			filePathModel.putAll(BeanHelper.describe(sql));
			return new GeneratorModel(templateModel,filePathModel);
		}

		public static GeneratorModel newFromClass(Class clazz) throws Exception {
			Map templateModel = new HashMap();
			templateModel.put("clazz", new JavaClass(clazz));
			setShareVars(templateModel);
			
			Map filePathModel = new HashMap();
			setShareVars(filePathModel);
			filePathModel.putAll(BeanHelper.describe(new JavaClass(clazz)));
			return new GeneratorModel(templateModel,filePathModel);
		}
		
		public static GeneratorModel newFromMap(Map params) {
			Map templateModel = new HashMap();
			templateModel.putAll(params);
			setShareVars(templateModel);
			
			Map filePathModel = new HashMap();
			setShareVars(filePathModel);
			filePathModel.putAll(params);
			return new GeneratorModel(templateModel,filePathModel);
		}
		
		public static void setShareVars(Map templateModel) {
			templateModel.putAll(GeneratorProperties.getProperties());
			System.out.println("########=====================#####");
			System.out.println(GeneratorProperties.getProperties());
			templateModel.putAll(System.getProperties());
			templateModel.put("env", System.getenv());
			templateModel.put("now", new Date());
			templateModel.putAll(GeneratorContext.getContext());
		}



	}
	
	private static class PrintUtils {
		
		private static void printExceptionsSumary(String msg,String outRoot,List<Exception> exceptions) throws FileNotFoundException {
			if(exceptions != null && exceptions.size() > 0) {
				logger.error(MessageFormat.format("[Generate Error Summary] :{}",msg));
				for(int i = 0; i < exceptions.size(); i++) {
					Exception e = exceptions.get(i);
					logger.error("[GENERATE ERROR] ",e);
					if(i == 0) e.printStackTrace();
				}
			}
		}
		
		private static void printBeginProcess(String displayText,boolean isDatele) {
		    if(logger.isDebugEnabled()) {
		        logger.debug("***************************************************************");
	            logger.debug(MessageFormat.format("* BEGIN " + (isDatele ? " delete by " : " generate by ")+"{0}", displayText));
	            logger.debug("***************************************************************");
		    }
		    
		}
		
		public static void printAllTableNames(List<Table> tables) throws Exception {
			GLogger.println("\n----All TableNames BEGIN----");
			logger.info("\n----All TableNames BEGIN----");
			for(int i = 0; i < tables.size(); i++ ) {
				String sqlName = ((Table)tables.get(i)).getSqlName();
				GLogger.println("g.generateTable(\""+sqlName+"\");");
				logger.info(MessageFormat.format("数据表:{0}",sqlName));
			}
			GLogger.println("----All TableNames END----");
			logger.info("----All TableNames END----");
		}
	}
	//==================================根据公司具体项目添加的方法=================
}
