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
package com.kentyou.assistiot.cheaas.enabler.api;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Produces(APPLICATION_JSON)
@Path("")
public interface IAssistIoTHealthNorthbound {

    @Path("health")
    @GET
    Response healthCheck();

    @Path("version")
    @GET
    String getVersion();

    @Path("metrics")
    @GET
    Map<String, Object> getMetrics();

    @Path("/api-export/openapi")
    @GET
    Response getOpenApi();

    @Path("/api-export/docs")
    @GET
    Response getDocs();
}
