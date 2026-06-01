package com.example.loanagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm")
public class LlmConfig {
    private String provider;
    private String model;
    private String apiKey;
    private String baseUrl;
    private Timeout timeout = new Timeout();

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public Timeout getTimeout() { return timeout; }
    public void setTimeout(Timeout timeout) { this.timeout = timeout; }

    public static class Timeout {
        private long connect;
        private long read;

        public long getConnect() { return connect; }
        public void setConnect(long connect) { this.connect = connect; }
        public long getRead() { return read; }
        public void setRead(long read) { this.read = read; }
    }
}
