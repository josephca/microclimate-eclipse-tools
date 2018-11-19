package com.ibm.microclimate.ui.internal.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.ibm.microclimate.ui.internal.messages.messages"; //$NON-NLS-1$

	public static String ConnectionPrefsPage_AddBtn;
	public static String ConnectionPrefsPage_LinkedProjectsColumn;
	public static String ConnectionPrefsPage_NoLinkedAppsDisplay;
	public static String ConnectionPrefsPage_PageTitle;
	public static String ConnectionPrefsPage_RemoveBtn;
	public static String ConnectionPrefsPage_ShellTitle;
	public static String ConnectionPrefsPage_TableTitleLabel;
	public static String ConnectionPrefsPage_URLColumn;

	public static String PrefsParentPage_DebugTimeoutLabel;
	public static String PrefsParentPage_ErrInvalidDebugTimeout;

	public static String NewConnectionPage_ConnectSucceeded;
	public static String NewConnectionPage_ErrAConnectionAlreadyExists;
	public static String NewConnectionPage_ErrCouldNotConnectToMC;
	public static String NewConnectionPage_HostnameLabel;
	public static String NewConnectionPage_NotValidPortNum;
	public static String NewConnectionPage_OnlyLocalhostSupported;
	public static String NewConnectionPage_PortLabel;
	public static String NewConnectionPage_ShellTitle;
	public static String NewConnectionPage_TestConnectionBtn;
	public static String NewConnectionPage_TestToProceed;
	public static String NewConnectionPage_WizardDescription;
	public static String NewConnectionPage_WizardTitle;
	public static String NewConnectionWizard_ShellTitle;

	public static String OpenAppAction_CantOpenNotRunningAppMsg;
	public static String OpenAppAction_CantOpenNotRunningAppTitle;

	public static String StartBuildAction_AppMissingTitle;
	public static String StartBuildAction_AppMissingMsg;
	public static String StartBuildAction_AlreadyBuildingTitle;
	public static String StartBuildAction_AlreadyBuildingMsg;
	
	public static String MicroclimateConnectionLabel;
	public static String MicroclimateDisconnected;
	public static String MicroclimateProjectDisabled;
	public static String MicroclimateConnectionNoProjects;
	
	public static String RestartInDebugMode;
	public static String RestartInRunMode;
	public static String ErrorOnRestartDialogTitle;
	
	public static String EnableProjectLabel;
	public static String DisableProjectLabel;
	public static String ErrorOnEnableDisableProjectDialogTitle;
	
	public static String EnableAutoBuildLabel;
	public static String DisableAutoBuildLabel;
	public static String ErrorOnEnableDisableAutoBuildDialogTitle;
	
	public static String ActionNewConnection;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {}
}
