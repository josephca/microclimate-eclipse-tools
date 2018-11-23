/*******************************************************************************
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2018 All Rights Reserved.
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *******************************************************************************/

package com.ibm.microclimate.core.internal.constants;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.microclimate.core.internal.MCLogger;

public class ProjectCapabilities {
	
	public static final ProjectCapabilities emptyCapabilities = new ProjectCapabilities();
	
	private final Set<StartMode> startModes = new HashSet<StartMode>();
	private final Set<ControlCommand> controlCommands = new HashSet<ControlCommand>();
	
	public enum StartMode {
		RUN("run"),
		DEBUG("debug");
		
		private final String name;
		
		private StartMode(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static StartMode getStartMode(String name) {
			for (StartMode mode : StartMode.values()) {
				if (mode.name.equals(name)) {
					return mode;
				}
			}
			return null;
		}
	};
	
	public enum ControlCommand {
		RESTART("restart");
		
		private final String name;
		
		private ControlCommand(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static ControlCommand getControlCommand(String name) {
			for (ControlCommand command : ControlCommand.values()) {
				if (command.name.equals(name)) {
					return command;
				}
			}
			return null;
		}
	}
	
	public ProjectCapabilities(JSONObject capabilities) {
		try {
			if (capabilities.has(MCConstants.KEY_START_MODES)) {
				JSONArray modes = capabilities.getJSONArray(MCConstants.KEY_START_MODES);
				for (int i = 0; i < modes.length(); i++) {
					StartMode startMode = StartMode.getStartMode(modes.getString(i));
					if (startMode != null) {
						this.startModes.add(startMode);
					}
				}
			}
		} catch (JSONException e) {
			MCLogger.logError("Failed to parse the start mode capabilities.", e);
		}
		
		try {
			if (capabilities.has(MCConstants.KEY_CONTROL_COMMANDS)) {
				JSONArray commands = capabilities.getJSONArray(MCConstants.KEY_CONTROL_COMMANDS);
				for (int i = 0; i < commands.length(); i++) {
					ControlCommand controlCommand = ControlCommand.getControlCommand(commands.getString(i));
					if (controlCommand != null) {
						this.controlCommands.add(controlCommand);
					}
				}
			}
		} catch (JSONException e) {
			MCLogger.logError("Failed to parse the control command capabilities.", e);
		}
	}
	
	private ProjectCapabilities() {
		// Intentionally empty
	}
	
	public boolean canRestart() {
		return controlCommands.contains(ControlCommand.RESTART);
	}
	
	public boolean supportsDebugMode() {
		return startModes.contains(StartMode.DEBUG);
	}

}
