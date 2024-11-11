package org.automation.apiTest.utils;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.automation.apiTest.utils.LoggerUtil.logError;
import static org.automation.apiTest.utils.LoggerUtil.logException;

public class FileUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<String, String> baseUrls;

    static {
        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> config;
        try (InputStream in = FileUtils.class.getClassLoader().getResourceAsStream("config.yml")) {
            config = yaml.load(in);
            baseUrls = config.getOrDefault("baseUrls", Collections.emptyMap());
        } catch (Exception e) {
            logException(e);
            throw new RuntimeException("Failed to load YAML configuration file");
        }
    }

    public static String getBaseUrlFromYaml(String key) {
        Optional<String> urlOpt = Optional.ofNullable(baseUrls.get(key));
        if (urlOpt.isPresent()) {
            return urlOpt.get();
        } else {
            logError(String.format("Could not find base url for key [%s]", key));
            throw new IllegalArgumentException("Could not find base url for key [" + key + "]");
        }
    }

    public static JsonNode getJsonFromFile(String typeOfJson, String filePath) {
        //typeOfJson is like request, response, schema

        try (InputStream inputStream = FileUtils.class
                .getClassLoader()
                .getResourceAsStream(String.format("%s/%s", typeOfJson, filePath))) {

            if (inputStream == null) {
                throw new IllegalArgumentException(String.format("File not found:[%s/%s]", typeOfJson, filePath));
            }

            return mapper.readTree(inputStream);
        } catch (IOException e) {
            logException(e);
            return null;
        }

    }
}
