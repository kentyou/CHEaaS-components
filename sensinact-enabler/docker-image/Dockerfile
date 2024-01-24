#####################################################################
# Copyright (c) 2024 Kentyou
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#   Kentyou - initial implementation
######################################################################

FROM ghcr.io/kentyou/eclipse-sensinact-container/sensinact:0.0.2-SNAPSHOT
COPY data/ext-repository /opt/sensinact/ext-repository
COPY data/ext-features /opt/sensinact/ext-features
COPY data/configuration.tpl.json /opt/sensinact/configuration.tpl.json
COPY data/entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh
ENTRYPOINT [ "/opt/entrypoint.sh" ]
CMD [ ]
