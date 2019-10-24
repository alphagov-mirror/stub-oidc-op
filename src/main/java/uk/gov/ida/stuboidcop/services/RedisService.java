package uk.gov.ida.stuboidcop.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stuboidcop.configuration.StubOidcOpConfiguration;

public class RedisService {

    private RedisCommands<String, String> commands;
    private static final Logger LOG = LoggerFactory.getLogger(RedisService.class);


    public RedisService(StubOidcOpConfiguration config) {
        LOG.info("Connecting to redis");
        startup(config);
    }

    public void startup(StubOidcOpConfiguration config) {
        String vcap = System.getenv("VCAP_SERVICES");
        String redisUri = config.getRedisURI();

        if (vcap != null && vcap.length() > 0) {
            String redisURIFromVcap = getRedisURIFromVcap(vcap);
            if (redisURIFromVcap != null) {
                redisUri = redisURIFromVcap;
            }
        }
        RedisClient client = RedisClient.create(redisUri + "/0");
        commands = client.connect().sync();
    }

    public void set(String key, String value) {
        commands.set(key, value);
    }

    public String get(String key) {
        return commands.get(key);
    }

    private String getRedisURIFromVcap(String vcap) {
        JsonElement root = new JsonParser().parse(vcap);
        JsonObject redis = null;
        if (root != null) {
            if (root.getAsJsonObject().has("redis")) {
                redis = root.getAsJsonObject().get("redis").getAsJsonArray().get(0).getAsJsonObject();
            }
            if (redis != null) {
                JsonObject creds = redis.get("credentials").getAsJsonObject();
                String redisURI = creds.get("uri").getAsString();
                LOG.info("This is the Redis URI from VCAP: " + redisURI);
                return redisURI;
            }
        }
        return null;
    }
}
