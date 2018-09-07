package com.ibm.microclimate.ui.internal.prefs;

import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.server.core.IServer;

import com.ibm.microclimate.core.MicroclimateCorePlugin;
import com.ibm.microclimate.core.internal.MCLogger;
import com.ibm.microclimate.core.internal.MicroclimateApplication;
import com.ibm.microclimate.core.internal.connection.MicroclimateConnection;
import com.ibm.microclimate.core.internal.connection.MicroclimateConnectionManager;
import com.ibm.microclimate.ui.MicroclimateUIPlugin;
import com.ibm.microclimate.ui.internal.wizards.NewMicroclimateConnectionWizard;
import com.ibm.microclimate.ui.internal.wizards.WizardLauncher;

/**
 * This preferences page lists the current Microclimate connections, allows adding new ones, and removing existing ones.
 * It can be launched through Preferences, or from the LinkMicroclimateProjectPage.
 *
 * @author timetchells@ibm.com
 *
 */
public class MicroclimateConnectionPrefsPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String
			MC_CONNECTIONS_PREFSKEY = "com.ibm.microclimate.ui.prefs.connections",
			PAGE_ID = "MicroclimateConnectionsPage";		// must match the value in plugin.xml

	private static final String CONNECTION_GOOD = "Yes", CONNECTION_BAD = "No";

	//private static MicroclimateConnectionPrefsPage instance;

	private Table connectionsTable;

	public MicroclimateConnectionPrefsPage() {
		super("Microclimate Connections", MicroclimateUIPlugin.getDefaultIcon());
		setMessage("Manage Microclimate Connections");
	}

	@Override
	public void init(IWorkbench arg0) {
		// Note that ConfigurationScope is used. This means that our list of MCConnections is shared
		// between different workspaces.
		setPreferenceStore(new ScopedPreferenceStore(ConfigurationScope.INSTANCE, MC_CONNECTIONS_PREFSKEY));

		// these buttons don't do anything
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {

		parent.setLayout(new GridLayout(1, true));

		Composite composite = new Composite(parent, SWT.CENTER);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, true));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(data);

		Label existingConnections = new Label(composite, SWT.NONE);
		existingConnections.setText("Create or remove connections:");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		existingConnections.setLayoutData(gridData);

		connectionsTable = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		connectionsTable.setLinesVisible(true);
		connectionsTable.setHeaderVisible(true);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gridData.widthHint = 450;
		gridData.heightHint = 300;
		connectionsTable.setLayoutData(gridData);

		TableColumn urlsCol = new TableColumn(connectionsTable, SWT.BORDER);
		urlsCol.setText("URL");
		urlsCol.setWidth((int)(gridData.widthHint / 2.5));

		TableColumn linkedProjectsCol = new TableColumn(connectionsTable, SWT.BORDER);
		linkedProjectsCol.setText("Linked Projects");
		linkedProjectsCol.setWidth(urlsCol.getWidth());

		TableColumn connectedCol = new TableColumn(connectionsTable, SWT.BORDER);
		connectedCol.setText("Connected");
		connectedCol.setWidth(gridData.widthHint - urlsCol.getWidth() - linkedProjectsCol.getWidth());

		Button addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add...");
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		addButton.setLayoutData(gridData);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				Wizard wizard = new NewMicroclimateConnectionWizard(false);
				WizardLauncher.launchWizardWithoutSelection(wizard);
			}
		});

		Button removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
		removeButton.setLayoutData(gridData);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {

				int[] selected = connectionsTable.getSelectionIndices();
				for(int i : selected) {
					// The URL is in the 0th column of the table
					String url = connectionsTable.getItem(i).getText(0);

					MicroclimateConnection connection = MicroclimateConnectionManager.getConnection(url);
					if (connection != null) {
						List<MicroclimateApplication> linkedApps = connection.getLinkedApps();

						if (linkedApps.isEmpty()) {
							MicroclimateConnectionManager.remove(connection);
						}
						else {
							handleDeleteActiveServers(connection, linkedApps);
						}
					}
					else {
						MicroclimateConnectionManager.removeBrokenConnection(
								MicroclimateConnectionManager.getBrokenConnection(url));
					}
				}

				refreshConnectionsList();
			}
		});

		connectionsTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int count = connectionsTable.getSelectionCount();
				if (count > 0) {
					removeButton.setEnabled(true);
				} else {
					removeButton.setEnabled(false);
				}
			}
		});

		refreshConnectionsList();
		addButton.setEnabled(true);
		removeButton.setEnabled(false);

		MicroclimateCorePlugin.getDefault().getPreferenceStore()
			.addPropertyChangeListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty() == MicroclimateConnectionManager.CONNECTION_LIST_PREFSKEY) {
						MCLogger.log("Reloading preferences in MCCPP");
						// calling refreshConnectionsList here results in WidgetDisposed exception if
						// the window is not in focus
						refreshConnectionsList();
					}
				}
			});

		return parent;
	}

	private void refreshConnectionsList() {
		if (!connectionsTable.isDisposed()) {
			connectionsTable.removeAll();
		}

		for(MicroclimateConnection mcc : MicroclimateConnectionManager.connections()) {
			addTableRow(mcc.baseUrl, getLinkedAppNamesForConnection(mcc), false);
		}

		for (URI brokenConnectionUrl : MicroclimateConnectionManager.brokenConnections()) {
			addTableRow(brokenConnectionUrl, "", true);
		}
	}

	private void addTableRow(URI url, String linkedApps, boolean isBroken) {
		if (linkedApps == null || linkedApps.isEmpty()) {
			linkedApps = "none";
		}

		try {
			TableItem ti = new TableItem(connectionsTable, SWT.NONE);

			// TODO error icon
			ti.setText(new String[] { url.toString(), linkedApps, isBroken ? CONNECTION_BAD : CONNECTION_GOOD });
		}
		catch(SWTException e) {
			// suppress widget disposed exception - It gets thrown if the window is out of focus,
			// but then the table populates just fine anyway, so I don't know why it is a problem.
			if (!"Widget is disposed".equals(e.getMessage())) {
				throw e;
			}
		}
	}

	private static String getLinkedAppNamesForConnection(MicroclimateConnection mcc) {
		StringBuilder linkedAppsBuilder = new StringBuilder();
		final String separator = ", ";

		mcc.getLinkedApps().stream()
				.forEachOrdered(app -> linkedAppsBuilder.append(app.name).append(separator));

		// Remove the last separator
		if (linkedAppsBuilder.length() > separator.length()) {
			linkedAppsBuilder.setLength(linkedAppsBuilder.length() - separator.length());
		}

		return linkedAppsBuilder.toString();
	}

	/**
	 * Proceeding with deleting this connection will delete all its linked servers too.
	 * Make this clear to the user, then delete the servers if they still wish to delete the connection.
	 */
	private void handleDeleteActiveServers(MicroclimateConnection connection,
			List<MicroclimateApplication> linkedApps) {

		String[] buttons = new String[] { "Cancel", "Delete Servers"  };
		final int deleteBtnIndex = 1;

		String message =
				"The following Microclimate applications have linked servers in the workspace: " +
				getLinkedAppNamesForConnection(connection) + "\n\n" +
				"If you still wish to delete the connection to " + connection.baseUrl + ", " +
				"ALL these servers will be DELETED from your Eclipse workspace.\n" +
				"Are you sure you want to proceed?";

		MessageDialog dialog = new MessageDialog(
				getShell(), "Connection has active servers",
				Display.getDefault().getSystemImage(SWT.ICON_WARNING),
				message, MessageDialog.WARNING, buttons,
				// Below is the index of the initially selected button - This unfortunately is always
				// the rightmost button in the dialog. So it's not possible to have the normal order
				// (ie Cancel to the left of OK) but also have Cancel selected initially.
				deleteBtnIndex);

		boolean delete = dialog.open() == deleteBtnIndex;

		if (delete) {
			boolean deletionSuccess = true;
			for (MicroclimateApplication app : linkedApps) {
				IServer server = app.getLinkedServer().getServer();
				try {
					server.delete();
				} catch (CoreException e) {
					MCLogger.logError("Error deleting server when deleting MCConnection", e);
					MessageDialog.openError(getShell(),
							"Error deleting server " + server.getName(),
							e.getMessage());

					deletionSuccess = false;
				}
			}

			if (deletionSuccess) {
				MicroclimateConnectionManager.remove(connection);
			}
		}
	}
}
