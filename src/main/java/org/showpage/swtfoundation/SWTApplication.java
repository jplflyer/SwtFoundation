package org.showpage.swtfoundation;

/**
 * Base class for all SWT Applications.  This is NOT a GUI
 * component.
 * 
 * You should subclass from SWTApplication as needed, generally
 * implementing a singleton architecture.
 */
public abstract class SWTApplication
{
	private static SWTApplication singleton = null;
	public boolean	backgroundSetupComplete = false;
	
	/**
	 * Our app is always a singleton.
	 * 
	 * @param app		The singleton.
	 */
	public static void setSingleton(SWTApplication app)
	{
		singleton = app;
	}
	
	/**
	 * Return our singleton.
	 * 
	 * @return Our singleton
	 */
	public static SWTApplication singleton()
	{
		return singleton;
	}
	
	/**
	 * Subclasses should override.
	 * 
	 * @return Return the application you created.
	 */
	public static SWTApplication createApplication()
	{
		System.out.println("You probably want to override createApplication");
		return null;
	}
	
	/**
	 * Subclasses should actually implement something.
	 * 
	 * @param args Arguments.
	 */
	public abstract void run(String[] args);
	
	/**
	 * Perform any startup work that we want to happen in the background.
	 * Our base class doesn't do anything.  Subclasses may override this
	 * if they need to.  They should chain to this method -or- set the
	 * Completed flag when they're done.  Other bits and pieces may be
	 * waiting for the flag to be set.
	 */
	public void backgroundInitialization()
	{
		backgroundSetupComplete = true;
	}
}
