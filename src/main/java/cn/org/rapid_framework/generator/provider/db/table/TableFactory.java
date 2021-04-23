package cn.org.rapid_framework.generator.provider.db.table;


import cn.org.rapid_framework.generator.GeneratorProperties;
import cn.org.rapid_framework.generator.common.GeneratorConsts;
import cn.org.rapid_framework.generator.provider.DatabaseMetaDatCache;
import cn.org.rapid_framework.generator.provider.db.DataSourceProvider;
import cn.org.rapid_framework.generator.provider.db.table.model.Column;
import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import cn.org.rapid_framework.generator.util.*;
import cn.org.rapid_framework.generator.util.XMLHelper.NodeData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 
 * 根据数据库表的元数据(metadata)创建Table对象
 * 
 * <pre>
 * getTable(sqlName) : 根据数据库表名,得到table对象
 * getAllTable() : 搜索数据库的所有表,并得到table对象列表
 * </pre>
 * @author badqiu
 * @email badqiu(a)gmail.com
 */
public class TableFactory  {
    private static final Logger logger= LoggerFactory.getLogger(TableFactory.class);
	
	private DbHelper dbHelper = new DbHelper();
	private static TableFactory instance = null;
	
	private TableFactory() {
	}
	
	public synchronized static TableFactory getInstance() {
		if(instance == null) instance = new TableFactory();
		return instance;
	}
	
	public String getCatalog() {
		return GeneratorProperties.getNullIfBlank("jdbc.catalog");
	}

	public String getSchema() {
		return GeneratorProperties.getNullIfBlank("jdbc.schema");
	}

	private Connection getConnection() {
	    return DataSourceProvider.getConnection();
	}
	

	public List getAllTables() {
		try {
			Connection conn = getConnection();
			return getAllTables(conn);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Table getTable(String tableName) {
		return getTable(getSchema(),tableName);
	}

	private Table getTable(String schema, String tableName) {
		return getTable(getCatalog(),schema,tableName);
	}
	
	private Table getTable(String catalog, String schema, String tableName) {
		Table t = null;
		try {
			t = buildTableMetadata(catalog, schema, tableName);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
//		if(t == null) {
//			throw new NotFoundTableException("not found table with give name:"+tableName+ (dbHelper.isOracleDataBase() ? " \n databaseStructureInfo:"+getDatabaseStructureInfo() : ""));
//		}
		return t;
	}
	
	public static class NotFoundTableException extends RuntimeException {
		private static final long serialVersionUID = 5976869128012158628L;
		public NotFoundTableException(String message) {
			super(message);
		}
	}

	private Table _getTable(String catalog, String schema, String tableName) throws SQLException {
	    if(tableName== null || tableName.trim().length() == 0) 
	         throw new IllegalArgumentException("tableName must be not empty");
	    catalog = StringHelper.defaultIfEmpty(catalog, null);
	    schema = StringHelper.defaultIfEmpty(schema, null);
	    
		Connection conn = getConnection();
		DatabaseMetaData dbMetaData = conn.getMetaData();
		
		String localCatalog=catalog;
        String localSchema=schema;
        if(!dbMetaData.storesLowerCaseIdentifiers() && dbMetaData.storesUpperCaseIdentifiers()) {
            localCatalog=localCatalog.toUpperCase();
            localSchema=localSchema.toUpperCase();
            tableName=tableName.toUpperCase();
        }else {
            localCatalog=localCatalog.toLowerCase();
            localSchema=localSchema.toLowerCase();
            tableName=tableName.toLowerCase();
        }
        
		ResultSet rs = dbMetaData.getTables(localCatalog, localSchema, tableName, new String[] {GeneratorConsts.DATABASE_METADATA_TABLE_TYPE_TABLE});
		while(rs.next()) {
			Table table = createTable(conn, rs);
			return table;
		}
		return null;
	}
	/**
	 * 构建数据表元数据
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private Table buildTableMetadata(String catalog, String schema, String tableName) throws SQLException{
	    if(StringUtils.isNotEmpty(tableName) && DatabaseMetaDatCache.exist(tableName)) {
	        return DatabaseMetaDatCache.get(tableName);
	    }
	    
	    List<Table> tables=listTableMetadata(catalog, schema, tableName);
	    if(tables.size()<1) {
	        logger.error(MessageFormat.format("数据库表： {0} 不存在", tableName));
	        return null;
	    }
	    Table table=tables.get(0);
	     List<String> primaryKeys = listTablePrimaryKey(table);
	     
	     //listTableIndex(table);  oracle有些表取数据表索引信息会卡死，影响代码生成
	     List<String> indexs =null; 
	    List<Map<Integer, Object>> exportkeys = listTableExportForeignKey(table);
	    for (Map<Integer, Object> map : exportkeys) {
            table.addExportedKey(map);
        }
	    List<Map<Integer, Object>> importKeys = listTableImportForeignKey(table);
	    for (Map<Integer, Object> map : importKeys) {
            table.addImportedKey(map);
        }	     
	     LinkedList<Column> columns = listColumnMetadata(table, primaryKeys,indexs);
	     for (Column column : columns) {
	         table.addColumn(column);
        }
	     
	     DatabaseMetaDatCache.add(table.getSqlName(), table);
	    return table;
	}
	
	/**
	 * 返回表基本元数据
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private List<Table> listTableMetadata(String catalog, String schema, String tableName) throws SQLException {
	    if(StringUtils.isEmpty(tableName)) {
	        tableName=null;
	    }
       
       Connection conn = getConnection();
       DatabaseMetaData dbMetaData = conn.getMetaData();
       
       String localCatalog=catalog;
       String localSchema=schema;
       if(!dbMetaData.storesLowerCaseIdentifiers() && dbMetaData.storesUpperCaseIdentifiers()) {
           localCatalog=localCatalog.toUpperCase();
           localSchema=localSchema.toUpperCase();
           //tableName=tableName.toUpperCase();
       }else {
           localCatalog=localCatalog.toLowerCase();
           localSchema=localSchema.toLowerCase();
           //tableName=tableName.toLowerCase();
       }
       ResultSet rs=null;
       List<Table> tables=new ArrayList<Table>();
       try {
           rs = dbMetaData.getTables(localCatalog, localSchema, tableName, new String[] {GeneratorConsts.DATABASE_METADATA_TABLE_TYPE_TABLE});
           Table table=null;
           while(rs.next()) {
               table = new Table();
               table.setSqlName(rs.getString(GeneratorConsts.DATABASE_METADATA_TABLE_NAME));
               table.setRemarks(rs.getString(GeneratorConsts.DATABASE_METADATA_REMARKS));
               tables.add(table);
           }
       }catch (SQLException e) {
           logger.warn(MessageFormat.format("取数据库表元数据信息异常，表：{0}", tableName),e);
           throw e;
       }finally {
           try {
               rs.close();
           }catch (Exception e) {
           }
       }
       return tables;
	}
	/**
	 * 取主键
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	private List<String> listTablePrimaryKey(Table table) throws SQLException {
	    String localCatalog=getCatalog();
        String localSchema=getSchema();
        String tableName=table.getSqlName();
        if(!getMetaData().storesLowerCaseIdentifiers() && getMetaData().storesUpperCaseIdentifiers()) {
            localCatalog=localCatalog.toUpperCase();
            localSchema=localSchema.toUpperCase();
            //tableName=tableName.toUpperCase();
        }else {
            localCatalog=localCatalog.toLowerCase();
            localSchema=localSchema.toLowerCase();
            //tableName=tableName.toLowerCase();
        }
        // get the primary keys
          List<String> primaryKeys = new LinkedList<String>();
          ResultSet primaryKeyRs = null;
          primaryKeyRs = getMetaData().getPrimaryKeys(localCatalog, localSchema, tableName);
          
          while (primaryKeyRs.next()) {
             String columnName = primaryKeyRs.getString(GeneratorConsts.DATABASE_METADATA_COLUMN_NAME);
             logger.trace(MessageFormat.format("primary key:", columnName));
             primaryKeys.add(columnName);
          }
          primaryKeyRs.close();
        return primaryKeys;
    }
	/**
	 * 取导出外键
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	private List<Map<Integer,Object>> listTableExportForeignKey(Table table) throws SQLException {
	    String localCatalog=getCatalog();
        String localSchema=getSchema();
        String tableName=table.getSqlName();
        if(!getMetaData().storesLowerCaseIdentifiers() && getMetaData().storesUpperCaseIdentifiers()) {
            localCatalog=localCatalog.toUpperCase();
            localSchema=localSchema.toUpperCase();
            //tableName=tableName.toUpperCase();
        }else {
            localCatalog=localCatalog.toLowerCase();
            localSchema=localSchema.toLowerCase();
            //tableName=tableName.toLowerCase();
        }
        ResultSet fkeys=null;
        List<Map<Integer,Object>> keys=new ArrayList<Map<Integer,Object>>();
        try {
            fkeys = getMetaData().getExportedKeys(localCatalog , localSchema, tableName);
            Map<Integer,Object> key=null;
            while ( fkeys.next()) {
              String pktable = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_PKTABLE_NAME);
              String pkcol   = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_PKCOLUMN_NAME);
              String fktable = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_FKTABLE_NAME);
              String fkcol   = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_FKCOLUMN_NAME);
              String seq     = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_KEY_SEQ);
              Integer iseq   = new Integer(seq);
              key=new HashMap<Integer, Object>();
              key.put(0, fktable);
              key.put(0, fkcol);
              key.put(0, pkcol);
              key.put(0, iseq);
              keys.add(key);
            }
        }catch (SQLException e) {
            logger.warn(MessageFormat.format("取数据库表的导出外键信息异常，表：{0}", tableName),e);
            throw e;
        }finally {
            try {
                fkeys.close();
            }catch (Exception e) {
            }
        }
	    
        
        return keys;
    }
	/**
	 * 取导入外键
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	private List<Map<Integer,Object>> listTableImportForeignKey(Table table) throws SQLException {
	    String localCatalog=getCatalog();
        String localSchema=getSchema();
        String tableName=table.getSqlName();
        if(!getMetaData().storesLowerCaseIdentifiers() && getMetaData().storesUpperCaseIdentifiers()) {
            localCatalog=localCatalog.toUpperCase();
            localSchema=localSchema.toUpperCase();
            //tableName=tableName.toUpperCase();
        }else {
            localCatalog=localCatalog.toLowerCase();
            localSchema=localSchema.toLowerCase();
            //tableName=tableName.toLowerCase();
        }
        ResultSet fkeys=null;
        List<Map<Integer,Object>> keys=new ArrayList<Map<Integer,Object>>();
        try {
            fkeys = getMetaData().getImportedKeys(localCatalog, localSchema, tableName);
            Map<Integer,Object> key=null;
            while ( fkeys.next()) {
              String pktable = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_PKTABLE_NAME);
              String pkcol   = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_PKCOLUMN_NAME);
              String fktable = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_FKTABLE_NAME);
              String fkcol   = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_FKCOLUMN_NAME);
              String seq     = fkeys.getString(GeneratorConsts.DATABASE_METADATA_TABLE_KEY_SEQ);
              Integer iseq   = new Integer(seq);
              key=new HashMap<Integer, Object>();
              key.put(0, fktable);
              key.put(0, fkcol);
              key.put(0, pkcol);
              key.put(0, iseq);
              keys.add(key);
            }
        }catch (SQLException e) {
            logger.warn(MessageFormat.format("取数据库表的导入外键信息异常，表：{0}", tableName),e);
            throw e;
        }finally {
            try {
                fkeys.close();
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        return keys;
    }
	
	/**
	 * 返回表索引
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public List<String> listTableIndex(Table table) throws SQLException{
	    List<String> indices=new ArrayList<String>();
	    String localCatalog=getCatalog();
	    String localSchema=getSchema();
	    String tableName=table.getSqlName();
	    if(!getMetaData().storesLowerCaseIdentifiers() && getMetaData().storesUpperCaseIdentifiers()) {
	        localCatalog=localCatalog.toUpperCase();
	        localSchema=localSchema.toUpperCase();
	        //tableName=tableName.toUpperCase();
	    }else {
	        localCatalog=localCatalog.toLowerCase();
	        localSchema=localSchema.toLowerCase();
	        //tableName=tableName.toLowerCase();
	    }
	    ResultSet indexRs = null;
	    try {
	        
	        indexRs = getMetaData().getIndexInfo(localCatalog, localSchema, tableName, false, true);
	         while (indexRs.next()) {
	            String columnName = indexRs.getString(GeneratorConsts.DATABASE_METADATA_COLUMN_NAME);
	            if (columnName != null) {
	               logger.trace(MessageFormat.format("表：{0}  索引：", tableName, columnName));
	               indices.add(columnName);
	            }
	         }
	         //indexRs.close();
	         return indices;
	    }catch (SQLException e) {
            logger.warn(MessageFormat.format("取数据库的索引信息异常，表：{0}", tableName),e);
            //throw e;
        }finally {
            try {
                indexRs.close();
            }catch (Exception e) {
                
            }
            
        }
	    return indices;
	}
	
	/**
	 * 取字段元数据
	 * @param table
	 * @param primaryKeys
	 * @return
	 * @throws SQLException
	 */
	private LinkedList<Column> listColumnMetadata(Table table, List<String> primaryKeys, List<String> indices) throws SQLException{
	    String localCatalog=getCatalog();
        String localSchema=getSchema();
        String tableName=table.getSqlName();
        if(!getMetaData().storesLowerCaseIdentifiers() && getMetaData().storesUpperCaseIdentifiers()) {
            localCatalog=localCatalog.toUpperCase();
            localSchema=localSchema.toUpperCase();
            //tableName=tableName.toUpperCase();
        }else {
            localCatalog=localCatalog.toLowerCase();
            localSchema=localSchema.toLowerCase();
            //tableName=tableName.toLowerCase();
        }
        
	    ResultSet columnRs = null;
	    LinkedList<Column> columns=new LinkedList<Column>();
	    try {
	        columnRs = getMetaData().getColumns(localCatalog, localSchema, tableName, null);
	        while (columnRs.next()) {
	            int sqlType = columnRs.getInt("DATA_TYPE");
	            String sqlTypeName = columnRs.getString("TYPE_NAME");
	            String columnName = columnRs.getString("COLUMN_NAME");
	            String columnDefaultValue = columnRs.getString("COLUMN_DEF");
	            
	            String remarks = columnRs.getString("REMARKS");
	            if(remarks == null && dbHelper.isOracleDataBase()) {
	                remarks = getOracleColumnComments(table.getSqlName(), columnName);
	            }
	            
	            // if columnNoNulls or columnNullableUnknown assume "not nullable"
	            boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
	            int size = columnRs.getInt("COLUMN_SIZE");
	            int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

	            boolean isPk = primaryKeys.contains(columnName);
	            boolean isIndexed =indices==null ? false : indices.contains(columnName);


	            Column column = new Column(
	                  table,
	                  sqlType,
	                  sqlTypeName,
	                  columnName,
	                  size,
	                  decimalDigits,
	                  isPk,
	                  isNullable,
	                  isIndexed,
	                  false,
	                  columnDefaultValue,
	                  remarks);
//	            BeanHelper.copyProperties(column, TableOverrideValuesProvider.getColumnOverrideValues(table,column));
	            columns.add(column);
	       }
	    }catch (SQLException e) {
            logger.error(MessageFormat.format("取得数据表字段元数据异常，表: {0}",tableName),e);
            throw e;
        }finally {
            try {
                columnRs.close();
            }catch (Exception e) {
                // TODO: handle exception
            }
        }      
        return columns;
	}
	
	
	

	private Table createTable(Connection conn, ResultSet rs) throws SQLException {
		String realTableName = null;
		try {
			//ResultSetMetaData rsMetaData = rs.getMetaData();
			String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
			realTableName = rs.getString("TABLE_NAME");
			String tableType = rs.getString("TABLE_TYPE");
			String remarks = rs.getString("REMARKS");
			
			//rs.close();
			
			if(remarks == null && dbHelper.isOracleDataBase()) {
				remarks = getOracleTableComments(realTableName);
			}
			
			Table table = new Table();
			table.setSqlName(realTableName);
			table.setRemarks(remarks);
			
			if ("SYNONYM".equals(tableType) && dbHelper.isOracleDataBase()) {
			    table.setOwnerSynonymName(getSynonymOwner(realTableName));
			}
			
			retriveTableColumns(table);
			
			table.initExportedKeys(conn.getMetaData());
			table.initImportedKeys(conn.getMetaData());
//			BeanHelper.copyProperties(table, TableOverrideValuesProvider.getTableOverrideValues(table.getSqlName()));
			return table;
		}catch(SQLException e) {
			throw new RuntimeException("create table object error,tableName:"+realTableName,e);
		}
	}
	
	private List getAllTables(Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(getCatalog(), getSchema(), null, new String[] {GeneratorConsts.DATABASE_METADATA_TABLE_TYPE_TABLE});
		List tables = new ArrayList();
		while(rs.next()) {
			tables.add(createTable(conn, rs));
		}
		return tables;
	}

	private String getSynonymOwner(String synonymName)  {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      String ret = null;
	      try {
	         ps = getConnection().prepareStatement("select table_owner from sys.all_synonyms where table_name=? and owner=?");
	         ps.setString(1, synonymName);
	         ps.setString(2, getSchema());
	         rs = ps.executeQuery();
	         if (rs.next()) {
	            ret = rs.getString(1);
	         }
	         else {
	            String databaseStructure = getDatabaseStructureInfo();
	            throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
	         }
	      } catch (SQLException e) {
	         String databaseStructure = getDatabaseStructureInfo();
	         GLogger.error(e.getMessage(), e);
	         throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
	      } finally {
	         dbHelper.close(rs,ps);
	      }
	      return ret;
	   }
   
   private String getDatabaseStructureInfo() {
	      ResultSet schemaRs = null;
	      ResultSet catalogRs = null;
	      String nl = System.getProperty("line.separator");
	      StringBuffer sb = new StringBuffer(nl);
	      // Let's give the user some feedback. The exception
	      // is probably related to incorrect schema configuration.
	      sb.append("Configured schema:").append(getSchema()).append(nl);
	      sb.append("Configured catalog:").append(getCatalog()).append(nl);

	      try {
	         schemaRs = getMetaData().getSchemas();
	         sb.append("Available schemas:").append(nl);
	         while (schemaRs.next()) {
	            sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
	         }
	      } catch (SQLException e2) {
	         GLogger.warn("Couldn't get schemas", e2);
	         sb.append("  ?? Couldn't get schemas ??").append(nl);
	      } finally {
	         dbHelper.close(schemaRs,null);
	      }

	      try {
	         catalogRs = getMetaData().getCatalogs();
	         sb.append("Available catalogs:").append(nl);
	         while (catalogRs.next()) {
	            sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
	         }
	      } catch (SQLException e2) {
	         GLogger.warn("Couldn't get catalogs", e2);
	         sb.append("  ?? Couldn't get catalogs ??").append(nl);
	      } finally {
	         dbHelper.close(catalogRs,null);
	      }
	      return sb.toString();
    }
	   
	private DatabaseMetaData getMetaData() throws SQLException {
		return getConnection().getMetaData();
	}
	
	private void retriveTableColumns(Table table) throws SQLException {
	      GLogger.trace("-------setColumns(" + table.getSqlName() + ")");

	      List primaryKeys = getTablePrimaryKeys(table);
	      table.setPrimaryKeyColumns(primaryKeys);
	      
	      // get the indices and unique columns
	      List indices = new LinkedList();
	      // maps index names to a list of columns in the index
	      Map uniqueIndices = new HashMap();
	      // maps column names to the index name.
	      Map uniqueColumns = new HashMap();
	      ResultSet indexRs = null;

	      try {

	         if (table.getOwnerSynonymName() != null) {
	            indexRs = getMetaData().getIndexInfo(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), false, true);
	         }
	         else {
	            indexRs = getMetaData().getIndexInfo(getCatalog(), getSchema(), table.getSqlName(), false, true);
	         }
	         while (indexRs.next()) {
	            String columnName = indexRs.getString("COLUMN_NAME");
	            if (columnName != null) {
	               GLogger.trace("index:" + columnName);
	               indices.add(columnName);
	            }

	            // now look for unique columns
	            String indexName = indexRs.getString("INDEX_NAME");
	            boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");

	            if (!nonUnique && columnName != null && indexName != null) {
	               List l = (List)uniqueColumns.get(indexName);
	               if (l == null) {
	                  l = new ArrayList();
	                  uniqueColumns.put(indexName, l);
	               }
	               l.add(columnName);
	               uniqueIndices.put(columnName, indexName);
	               GLogger.trace("unique:" + columnName + " (" + indexName + ")");
	            }
	         }
	      } catch (Throwable t) {
	         // Bug #604761 Oracle getIndexInfo() needs major grants
	         // http://sourceforge.net/tracker/index.php?func=detail&aid=604761&group_id=36044&atid=415990
	          logger.error("取数据库表索引信息失败", t);
	      } finally {
	         dbHelper.close(indexRs,null);
	      }

	      List columns = getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);

	      for (Iterator i = columns.iterator(); i.hasNext(); ) {
	         Column column = (Column)i.next();
	         table.addColumn(column);
	      }

	      // In case none of the columns were primary keys, issue a warning.
	      if (primaryKeys.size() == 0) {
	         //GLogger.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
	         logger.warn(MessageFormat.format("WARNING: The JDBC driver didn't report any primary key columns in {0}", table.getSqlName()));
	      }
	}

	private List getTableColumns(Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns) throws SQLException {
		// get the columns
	      List columns = new LinkedList();
	      ResultSet columnRs = getColumnsResultSet(table);
	      
	      while (columnRs.next()) {
	         int sqlType = columnRs.getInt("DATA_TYPE");
	         String sqlTypeName = columnRs.getString("TYPE_NAME");
	         String columnName = columnRs.getString("COLUMN_NAME");
	         String columnDefaultValue = columnRs.getString("COLUMN_DEF");
	         
	         String remarks = columnRs.getString("REMARKS");
	         if(remarks == null && dbHelper.isOracleDataBase()) {
	        	 remarks = getOracleColumnComments(table.getSqlName(), columnName);
	         }
	         
	         // if columnNoNulls or columnNullableUnknown assume "not nullable"
	         boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
	         int size = columnRs.getInt("COLUMN_SIZE");
	         int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

	         boolean isPk = primaryKeys.contains(columnName);
	         boolean isIndexed = indices.contains(columnName);
	         String uniqueIndex = (String)uniqueIndices.get(columnName);
	         List columnsInUniqueIndex = null;
	         if (uniqueIndex != null) {
	            columnsInUniqueIndex = (List)uniqueColumns.get(uniqueIndex);
	         }

	         boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
	         if (isUnique) {
	            GLogger.trace("unique column:" + columnName);
	         }
	         Column column = new Column(
	               table,
	               sqlType,
	               sqlTypeName,
	               columnName,
	               size,
	               decimalDigits,
	               isPk,
	               isNullable,
	               isIndexed,
	               isUnique,
	               columnDefaultValue,
	               remarks);
//	         BeanHelper.copyProperties(column, TableOverrideValuesProvider.getColumnOverrideValues(table,column));
	         columns.add(column);
	    }
	    columnRs.close();
		return columns;
	}
	
	private ResultSet getColumnsResultSet(Table table) throws SQLException {
		ResultSet columnRs = null;
	    if (table.getOwnerSynonymName() != null) {
	         columnRs = getMetaData().getColumns(getCatalog(), table.getOwnerSynonymName(), table.getSqlName(), null);
	    } else {
	         columnRs = getMetaData().getColumns(getCatalog(), getSchema(), table.getSqlName(), null);
	    }
		return columnRs;
	}

	private List<String> getTablePrimaryKeys(Table table) throws SQLException {
		// get the primary keys
	      List primaryKeys = new LinkedList();
	      ResultSet primaryKeyRs = null;
	      if (table.getOwnerSynonymName() != null) {
	         primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), table.getOwnerSynonymName(), table.getSqlName());
	      }
	      else {
	         primaryKeyRs = getMetaData().getPrimaryKeys(getCatalog(), getSchema(), table.getSqlName());
	      }
	      while (primaryKeyRs.next()) {
	         String columnName = primaryKeyRs.getString("COLUMN_NAME");
	         logger.trace(MessageFormat.format("primary key:", columnName));
	         //GLogger.trace("primary key:" + columnName);
	         primaryKeys.add(columnName);
	      }
	      primaryKeyRs.close();
		return primaryKeys;
	}

	private String getOracleTableComments(String table)  {
		String sql = "SELECT comments FROM user_tab_comments WHERE table_name='"+table+"'";
		return dbHelper.queryForString(sql);
	}

	private String getOracleColumnComments(String table,String column)  {
		String sql = "SELECT comments FROM user_col_comments WHERE table_name='"+table+"' AND column_name = '"+column+"'";
		return dbHelper.queryForString(sql);
	}
	
	/** 得到表的自定义配置信息 */
	public static class TableOverrideValuesProvider {
		
		private static Map getTableOverrideValues(String tableSqlName){
			NodeData nd = getTableConfigXmlNodeData(tableSqlName);
			if(nd == null) {
				return new HashMap();
			}
			return nd == null ? new HashMap() : nd.attributes;
		}
	
		private static Map getColumnOverrideValues(Table table, Column column) {
			NodeData root = getTableConfigXmlNodeData(table.getSqlName());
			if(root != null){
				 for(NodeData item : root.childs) {
					 if(item.nodeName.equals("column")) {
					     if(column.getSqlName().equalsIgnoreCase(item.attributes.get("sqlName"))) {
					         return item.attributes;
					     }
					 }
			     }
			}
			return new HashMap();
		}
		
		private static NodeData getTableConfigXmlNodeData(String tableSqlName){
			NodeData nd = getTableConfigXmlNodeData0(tableSqlName);
			if(nd == null) {
				nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase());
				if(nd == null) {
					nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase());
				}
			}
			return nd;
		}

		private static NodeData getTableConfigXmlNodeData0(String tableSqlName) {
			try {
				File file = FileHelper.getFileByClassLoader("generator_config/table/"+tableSqlName+".xml");
				GLogger.trace("getTableConfigXml() load nodeData by tableSqlName:"+tableSqlName+".xml");
				return new XMLHelper().parseXML(file);
			}catch(Exception e) {//ignore
				GLogger.trace("not found config xml for table:"+tableSqlName+", exception:"+e);
				return null;
			}
		}
	}
	
	class DbHelper {
		public void close(ResultSet rs,PreparedStatement ps,Statement... statements) {
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
				for(Statement s : statements) {s.close();}
			}catch(Exception e){
			}
		}
		public boolean isOracleDataBase() {
			try {
				return DatabaseMetaDataUtils.isOracleDataBase(getMetaData());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		public String queryForString(String sql) {
			Statement s = null;
			ResultSet rs = null;
			try {
				s =  getConnection().createStatement();
				rs = s.executeQuery(sql);
				if(rs.next()) {
					return rs.getString(1);
				}
				return null;
			}catch(SQLException e) {
				e.printStackTrace();
				return null;
			}finally {
				close(rs,null,s);
			}
		}		
	}
	
	public static class DatabaseMetaDataUtils {
		public static boolean isOracleDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = (metadata.getDatabaseProductName().toLowerCase()
							.indexOf("oracle") != -1);
				return ret;
			}catch(SQLException s) {
				return false;
//				throw new RuntimeException(s);
			}
		}
		public static boolean isHsqlDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = (metadata.getDatabaseProductName().toLowerCase()
							.indexOf("hsql") != -1);
				return ret;
			}catch(SQLException s) {
				return false;
//				throw new RuntimeException(s);
			}
		}		
	}

//    public void close() throws IOException {
//        
//        try {
//            if(getConnection()!=null || !getConnection().isClosed()) {
//                getConnection().close();
//            }
//        } catch (SQLException e) {
//            logger.error("关闭数据库连接异常", e);
//        }
//        
//    }
}
