package com.showpage.swtfoundation;

import org.apache.log4j.*;

/**
 * BaseLayout provides our base class for cool layout tricks we use.
 */
public abstract class BaseLayout {
	public static Logger log = Logger.getLogger(BaseLayout.class);
	
	/**
	 * This gets called when the window is first painted or
	 * whenever it's resized.
	 */
	public abstract void adjust();

}
