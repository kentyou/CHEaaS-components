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

import org.osgi.framework.BundleContext;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BundleContextProvider implements ContextResolver<BundleContext> {

    @Context
    Application application;

    @Override
    public BundleContext getContext(Class<?> type) {
        return (BundleContext) application.getProperties().get("bundle.context");
    }
}
