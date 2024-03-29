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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kentyou.assistiot.cheaas.enabler.api.IAssistIoTHealthNorthbound;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

public class HealthNorthbound implements IAssistIoTHealthNorthbound {

    /**
     * Access to custom context resolvers
     */
    @Context
    Providers providers;

    /**
     * URI info
     */
    @Context
    UriInfo uriInfo;

    /**
     * Returns a user session
     */
    private BundleContext getBundleContext() {
        return providers.getContextResolver(BundleContext.class, MediaType.WILDCARD_TYPE).getContext(null);
    }

    @Override
    public Response healthCheck() {
        final Map<String, Integer> missingBundles = new HashMap<>();
        for (Bundle bundle : getBundleContext().getBundles()) {
            final int state = bundle.getState();
            final boolean isFragment = (bundle.adapt(BundleRevision.class).getTypes()
                    & BundleRevision.TYPE_FRAGMENT) != 0;
            if (state != Bundle.ACTIVE && !(isFragment && state == Bundle.RESOLVED)) {
                // Found a bundle not active nor a fragment
                missingBundles.put(bundle.getSymbolicName(), bundle.getState());

            }
        }

        final Map<String, Object> result = new HashMap<>();
        final boolean invalidState = !missingBundles.isEmpty();
        if (invalidState) {
            result.put("error", "Some bundles are not in active state");
            result.put("invalidBundles", missingBundles);
            return Response.status(503).entity(result).build();
        } else {
            return Response.ok().build();
        }
    }

    @Override
    public String getVersion() {
        // Use this bundle version
        try {
            return new ObjectMapper().writeValueAsString(getBundleContext().getBundle().getVersion().toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting version to JSON string", e);
        }
    }

    @Override
    public Map<String, Object> getMetrics() {
        final BundleContext ctx = getBundleContext();
        final ServiceReference<MetricsHolder> svcRef = ctx.getServiceReference(MetricsHolder.class);
        if (svcRef == null) {
            return Map.of(MetricsHolder.METRICS_ENABLED_KEY, false);
        }

        final MetricsHolder metrics = ctx.getService(svcRef);
        try {
            return metrics.getLastReport();
        } finally {
            ctx.ungetService(svcRef);
        }
    }

    @Override
    public Response getOpenApi() {
        return Response.status(404).build();
    }

    @Override
    public Response getDocs() {
        return Response.status(404).build();
    }
}
