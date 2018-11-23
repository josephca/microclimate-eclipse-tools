/*******************************************************************************
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2018 All Rights Reserved.
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *******************************************************************************/

package com.ibm.microclimate.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.microclimate.core.internal.MCEclipseApplication;
import com.ibm.microclimate.core.internal.MCLogger;
import com.ibm.microclimate.core.internal.MCUtil;
import com.ibm.microclimate.ui.internal.messages.Messages;

public class EnableDisableProjectAction implements IObjectActionDelegate {

    protected MCEclipseApplication app;

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            action.setEnabled(false);
            return;
        }

        IStructuredSelection sel = (IStructuredSelection) selection;
        if (sel.size() == 1) {
            Object obj = sel.getFirstElement();
            if (obj instanceof MCEclipseApplication) {
            	app = (MCEclipseApplication)obj;
            	if (app.isEnabled()) {
                	action.setText(Messages.DisableProjectLabel);
                } else {
                	action.setText(Messages.EnableProjectLabel);
                }
	            action.setEnabled(true);
            	return;
            }
        }
        
        action.setEnabled(false);
    }

    @Override
    public void run(IAction action) {
        if (app == null) {
        	// should not be possible
        	MCLogger.logError("EnableDisableProjectAction ran but no Microclimate application was selected");
			return;
		}

        try {
			app.mcConnection.requestProjectOpenClose(app, !app.isEnabled());
		} catch (Exception e) {
			MCLogger.logError("Error initiating enable/disable for project: " + app.name, e); //$NON-NLS-1$
			MCUtil.openDialog(true, Messages.ErrorOnEnableDisableProjectDialogTitle, e.getMessage());
			return;
		}
    }

	@Override
	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		// nothing
	}
}