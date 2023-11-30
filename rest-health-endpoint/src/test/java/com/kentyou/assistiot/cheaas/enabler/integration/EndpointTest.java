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
package com.kentyou.assistiot.cheaas.enabler.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.InjectConfiguration;
import org.osgi.test.common.annotation.config.WithConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.ws.rs.core.Application;

@ExtendWith({ ServiceExtension.class, ConfigurationExtension.class, BundleContextExtension.class })
public class EndpointTest {

    @InjectBundleContext
    BundleContext context;

    @BeforeEach
    public void await(
            @InjectConfiguration(withConfig = @WithConfiguration(pid = "assist.iot.northbound.rest", location = "?", properties = {
                    @Property(key = "allow.anonymous", value = "true"),
                    @Property(key = "foo", value = "bar") })) Configuration cm,
            @InjectService(filter = "(foo=bar)", cardinality = 0) ServiceAware<Application> a)
            throws InterruptedException {
        a.waitForService(5000);
        for (int i = 0; i < 10; i++) {
            try {
                if (utils.queryStatus("/version") == 200)
                    return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(200);
        }
        throw new AssertionFailedError("Health API did not appear");
    }

    @AfterEach
    public void clear(@InjectConfiguration("assist.iot.northbound.rest") Configuration cm) throws Exception {
        cm.delete();
        Thread.sleep(500);
    }

    final TestUtils utils = new TestUtils("v1");

    @Test
    void testVersion() throws Exception {
        final Version bundleVersion = context.getBundle().getVersion();
        final Version restVersion = Version.parseVersion(utils.queryString("/version"));
        assertEquals(bundleVersion, restVersion);
    }

    @Test
    void testHealth() throws Exception {
        assertEquals(200, utils.queryStatus("/health"));
    }

    @Test
    void testMetrics() throws Exception {
        assertEquals(200, utils.queryStatus("/metrics"));
        boolean tested = false;
        for (int i = 0; i < 50; i++) {
            final Map<?, ?> metrics = utils.queryJson("/metrics", Map.class);
            assertNotNull(metrics, "Null received from endpoint");
            assertTrue(metrics.containsKey("metrics.enabled"), "Metrics flag not found");
            final boolean metricsEnabled = (boolean) metrics.get("metrics.enabled");
            if (!metricsEnabled || metrics.size() <= 1) {
                // Not enough metrics yet, wait a bit
                Thread.sleep(100);
                continue;
            }

            // Check some sensiNact metrics
            assertTrue(metrics.containsKey("sensinact.sessions"), "No sessions metric");
            assertNotNull(metrics.get("sensinact.sessions"), "Null sessions metric");

            tested = true;
            break;
        }
        assertTrue(tested, "Metrics weren't received in time");
    }

    @Test
    void testOpenAPI() throws Exception {
        assertEquals(404, utils.queryStatus("/api-export/openai"));
    }

    @Test
    void testDocs() throws Exception {
        assertEquals(404, utils.queryStatus("/api-export/docs"));
    }

    @Test
    void testSensiNactAccess() throws Exception {
        final TestUtils utils = new TestUtils("sensinact");
        final Map<?, ?> version = utils.queryJson("/providers/sensiNact/services/system/resources/version/GET",
                Map.class);
        assertEquals("GET_RESPONSE", version.get("type"));
        assertEquals(200, version.get("statusCode"));
        assertEquals("/sensiNact/system/version", version.get("uri"));
    }
}
