package com.example.trading.core;

import com.example.trading.core.kafka.error.KafkaErrorRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public Object getBean(Class<?> beanClass) {
        return context.getBean(beanClass);
    }
}
