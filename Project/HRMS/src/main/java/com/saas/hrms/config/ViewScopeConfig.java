package com.saas.hrms.config;

import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ViewScopeConfig {

    public ViewScopeConfig() {
        // no-op, bean registration happens in configurer below
    }

    @org.springframework.context.annotation.Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("view", new JsfViewScope());
        return configurer;
    }

    static class JsfViewScope implements Scope {

        private Map<String, Object> getViewMap() {
            return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        }

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            Map<String, Object> viewMap = getViewMap();
            return viewMap.computeIfAbsent(name, key -> objectFactory.getObject());
        }

        @Override
        public Object remove(String name) {
            return getViewMap().remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
        }

        @Override
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return FacesContext.getCurrentInstance().getViewRoot().getViewId();
        }
    }
    
}