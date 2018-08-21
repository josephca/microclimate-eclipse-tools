package com.ibm.microclimate.core.server.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import com.ibm.microclimate.core.Activator;
import com.ibm.microclimate.core.MCLogger;
import com.ibm.microclimate.core.internal.MCConstants;
import com.ibm.microclimate.core.internal.MicroclimateApplication;

public class MicroclimateServerConsole extends IOConsole {

	private static final String MC_CONSOLE_TYPE = "microclimate-console";

	public static Set<MicroclimateServerConsole> createApplicationConsoles(MicroclimateApplication app) {

		Set<MicroclimateServerConsole> consoles = new HashSet<>();

		for (IPath logPath : app.getLogFilePaths()) {
			File logFile = new File(logPath.toOSString());

			// We want the console name to be "myApp build.log" or "myApp messages.log".
			// the build.log has a different structure - it's mc-$projectId-$uuid.build.log, but we just want build.log

			String fileName = logFile.getName();

			if (fileName.endsWith(MCConstants.BUILD_LOG_SHORTNAME)) {
				fileName = MCConstants.BUILD_LOG_SHORTNAME;
			}

			String consoleName = app.name + " - " + fileName;

			try {
				consoles.add(new MicroclimateServerConsole(consoleName, logFile));
			}
			catch(FileNotFoundException e) {
				MCLogger.logError("FileNotFound when creating console " + consoleName, e);
			}
		}

		return consoles;
	}

	private final MicroclimateServerLogMonitorThread logMonitorThread;
	private final IOConsoleOutputStream outputStream;

	public MicroclimateServerConsole(String consoleName, File logFile) throws FileNotFoundException {
		super(consoleName, MC_CONSOLE_TYPE,
				com.ibm.microclimate.core.Activator.getIcon(Activator.CONSOLE_ICON_PATH),
				true);

		outputStream = newOutputStream();
		logMonitorThread = new MicroclimateServerLogMonitorThread(consoleName, logFile, outputStream);
		logMonitorThread.start();

		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();

		// See if a console exists matching this one and remove it if it does,
		// so that we don't have multiple of the same console (they would be identical anyway)
		IConsole[] existingMCConsoles = consoleManager.getConsoles();
		for (IConsole console : existingMCConsoles) {
			if (console instanceof MicroclimateServerConsole && console.getName().equals(consoleName)) {
				consoleManager.removeConsoles(new IConsole[] { console } );
				break;
			}
		}

		consoleManager.addConsoles(new IConsole[] { this });
	}

	@Override
	protected void dispose() {
		MCLogger.log("Dispose console " + getName());

		logMonitorThread.disable();
		logMonitorThread.interrupt();
		try {
			outputStream.close();
		} catch (IOException e) {
			MCLogger.logError("Error closing console output stream", e);
		}

		super.dispose();
	}
}