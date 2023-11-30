/*********************************************************************
* Copyright (c) 2022 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   Kentyou - initial implementation
**********************************************************************/
package com.kentyou.assistiot.cheaas.enabler.integration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String prefix;

    static final HttpClient client = HttpClient.newHttpClient();

    public TestUtils(String prefix) {
        if (prefix == null) {
            this.prefix = "";
        } else {
            if (prefix.startsWith("/")) {
                prefix = prefix.substring(1);
            }
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            this.prefix = prefix;
        }
    }

    private URI makeTargetUri(final String path) {
        if (path.startsWith("/")) {
            return URI.create("http://localhost:8185/" + prefix + path.substring(1));
        } else {
            return URI.create("http://localhost:8185/" + prefix + path);
        }
    }

    /**
     * Executes a GET request and returns its response
     */
    public int queryStatus(final String path) throws IOException, InterruptedException {
        // Normalize URI
        final URI targetUri = makeTargetUri(path);
        final HttpRequest req = HttpRequest.newBuilder(targetUri).build();
        return client.send(req, (x) -> BodySubscribers.discarding()).statusCode();
    }

    /**
     * Executes a GET request and returns its content
     */
    public String queryString(final String path) throws IOException, InterruptedException {
        // Normalize URI
        final URI targetUri = makeTargetUri(path);
        final HttpRequest req = HttpRequest.newBuilder(targetUri).build();
        return client.send(req, (x) -> BodySubscribers.ofString(StandardCharsets.UTF_8)).body();
    }

    /**
     * Executes a GET request and returns its parsed content
     */
    public <T> T queryJson(final String path, final Class<T> resultType) throws IOException, InterruptedException {
        return queryJson(path, resultType, Map.of());
    }

    public <T> T queryJson(final String path, final Class<T> resultType, Map<String, String> headers)
            throws IOException, InterruptedException {
        // Normalize URI
        final URI targetUri = makeTargetUri(path);
        Builder builder = HttpRequest.newBuilder(targetUri);
        headers.forEach((a, b) -> builder.header(a, b));

        final HttpRequest req = builder.build();
        final HttpResponse<InputStream> response = client.send(req, (x) -> BodySubscribers.ofInputStream());
        return mapper.createParser(response.body()).readValueAs(resultType);
    }
}
