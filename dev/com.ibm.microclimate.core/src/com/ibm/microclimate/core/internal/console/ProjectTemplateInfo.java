/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.microclimate.core.internal.console;

import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.microclimate.core.internal.MCLogger;

public class ProjectTemplateInfo {
	
	public static final String LABEL_KEY = "label";
	public static final String DESCRIPTION_KEY = "description";
	public static final String EXTENSION_KEY = "extension";
	public static final String LANGUAGE_KEY = "language";
	
	private JSONObject projectInfo;
	
	public ProjectTemplateInfo(JSONObject projectInfo) {
		this.projectInfo = projectInfo;
	}
	
	public String getLabel() {
		return getString(LABEL_KEY);
	}
	
	public String getDescription() {
		return getString(DESCRIPTION_KEY);
	}
	
	public String getLanguage() {
		return getString(LANGUAGE_KEY);
	}
	
	public String getExtension() {
		return getString(EXTENSION_KEY);
	}
	
	private String getString(String key) {
		String value = null;
		if (projectInfo.has(key)) {
			try {
				value = projectInfo.getString(key);
			} catch (JSONException e) {
				MCLogger.logError("An error occurred retrieving the value from the project template object for key: " + key, e);
			}
		} else {
			MCLogger.logError("The project template object did not have the expected key: " + key);
		}
		return value;
	}

}
