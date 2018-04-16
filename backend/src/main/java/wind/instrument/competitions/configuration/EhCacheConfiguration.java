package wind.instrument.competitions.configuration;

import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;

@Configuration
@EnableCaching
public class EhCacheConfiguration {

    @Bean
    public PersistentCacheManager persistentCacheManager() {
        PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                        .with(CacheManagerBuilder.persistence(new File("D:/projects/JavaWebForum/cache", "myData")))
                        .withCache("allStatistic",  CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ArrayList.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(10, EntryUnit.ENTRIES)
                                .offheap(1, MemoryUnit.MB)
                                .disk(2, MemoryUnit.MB, true))
                        ).build(true);
        return cacheManager;
    }


}
