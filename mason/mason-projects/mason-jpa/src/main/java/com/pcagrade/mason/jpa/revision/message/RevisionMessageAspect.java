package com.pcagrade.mason.jpa.revision.message;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;

@Aspect
public class RevisionMessageAspect {

    private static final Logger LOGGER = LogManager.getLogger(RevisionMessageAspect.class);

    private final RevisionMessageService revisionMessageService;

    public RevisionMessageAspect(RevisionMessageService revisionMessageService) {
        this.revisionMessageService = revisionMessageService;
    }

    @AfterReturning(value = "@annotation(revisionMessage)", returning = "returnValue")
    public void addRevisionMessage(JoinPoint joinPoint, RevisionMessage revisionMessage, Object returnValue) {
        try {
            if (revisionMessage.spel()) {
                addSpelMessage(joinPoint, revisionMessage.value(), returnValue);
            } else {
                addMessage(revisionMessage.value(), returnValue, joinPoint.getArgs());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to add revision message", e);
        }
    }

    private void addMessage(String format, Object returnValue, Object[] args) {
        if (StringUtils.isBlank(format)) {
            return;
        }

        revisionMessageService.addMessage(format.replace("{return}", "{" + args.length + "}"), ArrayUtils.add(args, returnValue));
    }

    private void addSpelMessage(JoinPoint joinPoint, String expression, Object returnValue) {
        if (StringUtils.isBlank(expression)) {
            return;
        }

        var args = joinPoint.getArgs();
        var argNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        var argumentMap = new HashMap<String, Object>();

        for (int i = 0; i < args.length && i < argNames.length; i++) {
            argumentMap.put(argNames[i], args[i]);
        }
        argumentMap.put("return", returnValue);

        var expressionParser = new SpelExpressionParser();
        var context = new StandardEvaluationContext();

        context.addPropertyAccessor(new MapAccessor());

        var message = expressionParser.parseExpression(expression).getValue(context, argumentMap);

        if (message == null) {
            return;
        }

        var stringMessage = message.toString();

        if (StringUtils.isBlank(stringMessage)) {
            return;
        }

        revisionMessageService.addMessage(stringMessage);
    }
}
