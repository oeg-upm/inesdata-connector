package org.upm.inesdata.storageasset.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.upm.inesdata.storageasset.controller.StorageAssetApiController;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class AppConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(StorageAssetApiController.class);
        return classes;
    }
}