/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.microclimate.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IServer;

import com.ibm.microclimate.core.server.MicroclimateServer;

/**
 * Test the server type where type is the type specified in the
 * com.ibm.ws.st.common.core.ext.serverTypeExtension extension point.
 *
 * From com.ibm.ws.st.ui.internal.plugin.ServerPropertyTester
 */
public class ServerPropertyTester extends PropertyTester {
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (expectedValue instanceof String) {
			return checkProperty(receiver, property, (String) expectedValue);
		}
        if (expectedValue != null) {
			return checkProperty(receiver, property, expectedValue.toString());
		}

        return checkProperty(receiver, property, null);
    }

    protected static boolean checkProperty(Object target, String property, String value) {
        if ("serverType".equals(property)) {		// must match objectState name in plugin.xml
            if (value == null) {
				return false;
			}

            IServer server = Platform.getAdapterManager().getAdapter(target, IServer.class);
            return server != null && MicroclimateServer.SERVER_ID.equals(server.getServerType().getId());
        }
        return false;
    }
}
