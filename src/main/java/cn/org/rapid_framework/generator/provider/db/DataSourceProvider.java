package cn.org.rapid_framework.generator.provider.db;

import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.provider.DatabaseMetaDatCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;

/**
 * 用于提供生成器的数据源
 * 
 * @author badqiu
 *
 */
public class DataSourceProvider {
    private static final Logger logger= LoggerFactory.getLogger(DataSourceProvider.class);
    
    
	private static Connection connection;
	private static DataSource dataSource;
	

	public synchronized static Connection getConnection() {
		try {
			if(connection == null || connection.isClosed()) {
				//connection = getDataSource().getConnection();
			    String url= GeneratorProperties.getRequiredProperty("jdbc.url");
		        String userId= GeneratorProperties.getRequiredProperty("jdbc.username");
		        String password= GeneratorProperties.getProperty("jdbc.password");
		        String catalog= GeneratorProperties.getProperty("jdbc.catalog");
		        String schema= GeneratorProperties.getProperty("jdbc.schema");
		        Driver driver = getDriver();

		        Properties props = new Properties();
		        props.setProperty("user", userId);
		        props.setProperty("password", password);
		        props.setProperty("catalog", catalog);
		        props.setProperty("schema", schema);
		        connection =driver.connect(url, props);
		        logger.info("连接数据库成功...");
			}
			return connection;
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 关闭数据库连接
	 */
	public synchronized static void closeConnection() {
	    if(connection != null) {
	        try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("断开数据库连接...");
                }
            } catch (SQLException e) {
                logger.warn("关闭数据库连接失败", e);
            }finally {
                connection=null;
            }
	    }
	    DatabaseMetaDatCache.clear();
	}
	
	private static Driver getDriver() {
        Driver driver;
        String driverClass= GeneratorProperties.getRequiredProperty("jdbc.driver");

        try {
            Class<?> clazz =Class.forName(driverClass);
            driver = (Driver) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("加载数据库驱动失败", e); //$NON-NLS-1$
        }

        return driver;
    }
	
	public static void setDataSource(DataSource dataSource) {
		DataSourceProvider.dataSource = dataSource;
	}

	public synchronized static DataSource getDataSource() {
		if(dataSource == null) {
			dataSource = new DriverManagerDataSource(GeneratorProperties.getRequiredProperty("jdbc.url"),
					GeneratorProperties.getRequiredProperty("jdbc.username"),
					GeneratorProperties.getProperty("jdbc.password"),
					GeneratorProperties.getRequiredProperty("jdbc.driver"));
		}
		return dataSource;
	}
	
	public static class DriverManagerDataSource implements DataSource {
		private String url;
		private String username;
		private String password;
		private String driverClass;
		
		private static void loadJdbcDriver(String driver) {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("not found jdbc driver class:["+driver+"]",e);
			}
		}
		
		public DriverManagerDataSource(String url, String username,String password, String driverClass) {
			this.url = url;
			this.username = username;
			this.password = password;
			this.driverClass = driverClass;
			loadJdbcDriver(driverClass);
		}

		public Connection getConnection() throws SQLException {
			return DriverManager.getConnection(url,username,password);
		}

		public Connection getConnection(String username, String password) throws SQLException {
			return DriverManager.getConnection(url,username,password);
		}

		public PrintWriter getLogWriter() throws SQLException {
			throw new UnsupportedOperationException("getLogWriter");
		}

		public int getLoginTimeout() throws SQLException {
			throw new UnsupportedOperationException("getLoginTimeout");
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			throw new UnsupportedOperationException("setLogWriter");
		}

		public void setLoginTimeout(int seconds) throws SQLException {
			throw new UnsupportedOperationException("setLoginTimeout");
		}

		public <T> T  unwrap(Class<T> iface) throws SQLException {
			if(iface == null) throw new IllegalArgumentException("Interface argument must not be null");
			if (!DataSource.class.equals(iface)) {
				throw new SQLException("DataSource of type [" + getClass().getName() +
						"] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
			}
			return (T) this;
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return DataSource.class.equals(iface);
		}

		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
