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

import org.eclipse.sensinact.core.metrics.IMetricsListener;
import org.eclipse.sensinact.core.push.dto.BulkGenericDto;
import org.eclipse.sensinact.core.push.dto.GenericDto;
import org.osgi.service.component.annotations.Component;

@Component(service = { IMetricsListener.class, MetricsHolder.class })
public class MetricsHolder implements IMetricsListener {

    static final String METRICS_ENABLED_KEY = "metrics.enabled";

    /**
     * Keep track of last report
     */
    private Map<String, Object> lastReport = Map.of(METRICS_ENABLED_KEY, false);

    @Override
    public void onMetricsReport(BulkGenericDto bulk) {
        final Map<String, Object> report = new HashMap<>();
        report.put(METRICS_ENABLED_KEY, true);
        for (GenericDto dto : bulk.dtos) {
            final String svc = dto.service;
            final String rc = dto.resource;
            final String metric;
            if ("metrics".equals(svc)) {
                metric = rc;
            } else {
                metric = svc + "." + rc;
            }
            report.put(metric, dto.value);
        }

        this.lastReport = report;
    }

    Map<String, Object> getLastReport() {
        return lastReport;
    }
}
