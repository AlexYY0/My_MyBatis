package club.emperorws.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * HikariCP数据库连接配置
 *
 * @author: EmperorWS
 * @date: 2023/3/4 12:00
 * @description: HikariDataSourceFactory: HikariCP数据库连接配置
 */
public class HikariDataSourceFactory extends UnpooledDataSourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(HikariDataSourceFactory.class);

    public HikariDataSourceFactory(){
        URL resource = HikariDataSourceFactory.class.getClassLoader().getResource("hikariPool.properties");
        logger.info("hikariPool.properties getPath:{}", resource.getPath());
        logger.info("hikariPool.properties getFile:{}", resource.getFile());
        HikariConfig config = new HikariConfig(resource.getPath());
        config.setMaximumPoolSize(5);
        this.dataSource = new HikariDataSource(config);
    }
}
