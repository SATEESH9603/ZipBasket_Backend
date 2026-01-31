package com.example.onlinetest.Configuration.Logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.logging")
public class LoggingProperties {

    /** Enable/disable method entry/exit logs (AOP). */
    private boolean method = true;

    /** Enable/disable HTTP request logs (filter). */
    private boolean http = true;

    /** If true, include argument values in method logs (can leak PII). */
    private boolean includeArgs = false;

    public boolean isMethod() {
        return method;
    }

    public void setMethod(boolean method) {
        this.method = method;
    }

    public boolean isHttp() {
        return http;
    }

    public void setHttp(boolean http) {
        this.http = http;
    }

    public boolean isIncludeArgs() {
        return includeArgs;
    }

    public void setIncludeArgs(boolean includeArgs) {
        this.includeArgs = includeArgs;
    }
}
