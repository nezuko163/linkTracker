package backend.academy.bot.config;

import backend.academy.dto.LinkResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisTemplate<String, LinkResponse> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, LinkResponse> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<LinkResponse> valueSerializer =
                new Jackson2JsonRedisSerializer<>(LinkResponse.class);
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public ReactiveRedisTemplate<String, LinkResponse> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<LinkResponse> valueSerializer =
                new Jackson2JsonRedisSerializer<>(LinkResponse.class);

        RedisSerializationContext<String, LinkResponse> context =
                RedisSerializationContext.<String, LinkResponse>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .hashKey(keySerializer)
                        .hashValue(valueSerializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
