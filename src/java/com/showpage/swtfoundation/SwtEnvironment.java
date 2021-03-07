package com.showpage.swtfoundation;

import java.util.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * This singleton class contains info about the SWT environment that we
 * might care about, like a pointer to Display.  This means we don't
 * have to pass this info around everywhere.
 */
public class SwtEnvironment implements DisposeListener {
	private static SwtEnvironment singleton = null;
	private ArrayList<MainFrame> windowList = new ArrayList<MainFrame>();
	
	private Display display;
	public Color black;
	public Color white;
	public Color green;
	public Color red;
	public Color yellow;
	public Color blue;
	
	/**
	 * Private constructor to enforce singleton.
	 */
	private SwtEnvironment() {
	}
	
	/**
	 * Return the singleton, creating it if needed.
	 */
	public static SwtEnvironment singleton() {
		if (singleton == null) {
			createSingleton();
		}
		return singleton;
	}

	/**
	 * The method to actually create the singleton.  We
	 * make this method synchronized so we'll never create
	 * two, but we don't require singleton() to be synchronized,
	 * as he'll get called a lot, long after we've created the
	 * singleton.
	 *
	 */
	private static synchronized void createSingleton() {
		if (singleton == null) {
			singleton = new SwtEnvironment();
			singleton.initialize();
		}
	}
	
	/**
	 * Basic initialization.
	 */
	private synchronized void initialize() {
		display = new Display();
		black	= new Color(display, 0, 0, 0);
		white	= new Color(display, 255, 255, 255);
		green	= new Color(display, 0, 255, 0);
		red		= new Color(display, 255, 0, 0);
		yellow	= new Color(display, 255, 255, 0);
		blue		= new Color(display, 0, 0, 255);
	}

	/**
	 * Return the display.
	 */
	public Display display() {
		if (display == null) {
			initialize();
		}
		return display;
	}
	
	/**
	 * Run until no windows exist.
	 */
	public void run()
	{
		boolean shouldSleep = false;
		while (windowList.size() > 0)
		{
			if (shouldSleep)
			{
				display.sleep();
				shouldSleep = false;
			}
			shouldSleep = !display.readAndDispatch();
		}
	}
	
	/**
	 * Register this window for our run() loop.
	 */
	public void registerWindow(MainFrame win)
	{
		if (!windowList.contains(win))
		{
			windowList.add(win);
			win.shell.addDisposeListener(this);
		}
	}
	
	/**
	 * Return all the registered windows of this type.
	 */
	public ArrayList<MainFrame> registeredWindowsOfType(Class<?> windowClass)
	{
		ArrayList<MainFrame>retVal = new ArrayList<MainFrame>();
		
		for (MainFrame win : windowList)
		{
			if (windowClass.isAssignableFrom(win.getClass()))
			{
				retVal.add(win);
			}
		}
		
		return retVal;
	}
	
	/**
	 * A window has been disposed.
	 */
	public void widgetDisposed( DisposeEvent e )
	{
		Widget wid = e.widget;
		for (MainFrame mf : windowList)
		{
			if (mf.shell == wid)
			{
				windowList.remove(mf);
				break;
			}
		}
	}
}
