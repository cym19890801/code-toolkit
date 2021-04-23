package cn.org.rapid_framework.generator.provider;

import cn.org.rapid_framework.generator.provider.db.table.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * 元数据缓存
 */
public final class DatabaseMetaDatCache {
    private static final Logger logger= LoggerFactory.getLogger(DatabaseMetaDatCache.class);
    
    private static ConcurrentMap<String, Table> caches = new ConcurrentHashMap<String, Table>();

    public static void add(String tableName, Table table) {
        logger.info(MessageFormat.format("写入元数据缓存... {0}", tableName.toLowerCase()));
        caches.put(tableName.toLowerCase(), table);
    }
    public static Table get(String tableName) {
        logger.info(MessageFormat.format("读取元数据缓存... {0}", tableName.toLowerCase()));
        return caches.get(tableName.toLowerCase());
    }
    public static Boolean exist(String tableName) {
        return caches.containsKey(tableName.toLowerCase());
    }
    public static void clear() {
        caches.clear();
        logger.info("清理数据表元数据缓存");
    }
}
