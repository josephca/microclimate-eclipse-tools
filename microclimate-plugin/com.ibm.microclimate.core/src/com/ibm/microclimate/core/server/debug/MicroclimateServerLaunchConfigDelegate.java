package com.ibm.microclimate.core.server.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;

import com.ibm.microclimate.core.MCLogger;
import com.ibm.microclimate.core.server.MicroclimateServer;
import com.ibm.microclimate.core.server.MicroclimateServerBehaviour;

public class MicroclimateServerLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration config, String launchMode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		MCLogger.log("Launching!!!!!! mode=" + launchMode);

        final IServer server = ServerUtil.getServer(config);
        if (server == null) {
            MCLogger.logError("Could not find server from configuration " + config.getName());
            return;
        }

        final MicroclimateServerBehaviour serverBehaviour =
        		(MicroclimateServerBehaviour) server.loadAdapter(MicroclimateServerBehaviour.class, null);

        final String projectName = server.getAttribute(MicroclimateServer.ATTR_ECLIPSE_PROJECT_NAME, "");
        if (projectName.isEmpty()) {
        	// Need the project name to set the source path - in this case the user can work around it by
        	// adding the project to the source path manually.
        	MCLogger.logError(MicroclimateServer.ATTR_ECLIPSE_PROJECT_NAME + " was not set on server "
        			+ server.getName());
        }


        ILaunchConfigurationWorkingCopy configWc = config.getWorkingCopy();
        configWc.setAttribute(MicroclimateServer.ATTR_ECLIPSE_PROJECT_NAME, projectName);
        config = configWc.doSave();

        //configWc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, JavaSourceLocator.ID_JAVA_SOURCE_LOCATOR);
        //config = configWc.doSave();


        setDefaultSourceLocator(launch, config);
        serverBehaviour.setLaunch(launch);


        serverBehaviour.doRestart(config, launchMode, launch, monitor);
	}

}
