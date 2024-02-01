/*
 * Copyright (c) 2023 Kentyou.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *    Kentyou - initial API and implementation
 */
package com.kentyou.assistiot.cheaas.enabler.impl;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsApplicationBase;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import jakarta.ws.rs.core.Application;

@Component(service = Application.class)
@JakartarsName("assist-iot-rest")
@JakartarsApplicationBase("/v1")
public class HealthAccessApplication extends Application {

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(BundleContextProvider.class, JacksonJsonProvider.class,
                HealthNorthbound.class);
    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of("bundle.context", bundleContext);
    }
}
