package com.example.onlinetest.Configuration.Logging;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Logs beginning/end of every Service method.
 *
 * Scope controlled via app.logging.method and app.logging.includeArgs.
 */
@Aspect
@Component
@EnableConfigurationProperties(LoggingProperties.class)
public class ServiceMethodLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceMethodLoggingAspect.class);

    private final LoggingProperties props;

    public ServiceMethodLoggingAspect(LoggingProperties props) {
        this.props = props;
    }

    @Around("execution(public * com.example.onlinetest.Service..*(..))")
    public Object logServiceMethod(ProceedingJoinPoint pjp) throws Throwable {
        if (!props.isMethod()) {
            return pjp.proceed();
        }

        String signature = pjp.getSignature().toShortString();
        String args = null;
        if (props.isIncludeArgs()) {
            args = Arrays.stream(pjp.getArgs())
                    .map(this::safeToString)
                    .collect(Collectors.joining(", "));
        }

        long start = System.currentTimeMillis();
        if (args != null) {
            log.info("METHOD START {} args=[{}]", signature, args);
        } else {
            log.info("METHOD START {}", signature);
        }

        try {
            Object result = pjp.proceed();
            long tookMs = System.currentTimeMillis() - start;
            log.info("METHOD END {} ({} ms)", signature, tookMs);
            return result;
        } catch (Throwable t) {
            long tookMs = System.currentTimeMillis() - start;
            log.error("METHOD ERROR {} ({} ms): {}", signature, tookMs, t.toString());
            throw t;
        }
    }

    private String safeToString(Object o) {
        if (o == null) return "null";
        // Avoid dumping huge data or secrets accidentally.
        String s;
        try {
            s = String.valueOf(o);
        } catch (Exception e) {
            return "<unprintable:" + o.getClass().getSimpleName() + ">";
        }
        if (s.length() > 500) {
            return s.substring(0, 500) + "...";
        }
        return s;
    }
}
