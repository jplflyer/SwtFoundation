package com.showpage.swtfoundation;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * ReflectionMenuItem uses reflection to implement a callback when
 * the menu item is selected.
 */
public class ReflectionMenuItem extends MenuItem implements SelectionListener {
	private ReflectionCallback	callback = null;
	
	/**
	 * Basic constructor.
	 */
	public ReflectionMenuItem(Menu parent, int style, String label, Object cbObject, String cbName, Object cbData, int accelerator)
	{
		super(parent, style);
		setText(label);
		if (accelerator != 0)
		{
			setAccelerator(accelerator);
		}
		this.addSelectionListener(this);
		
		if (cbObject != null)
		{
			callback = new ReflectionCallback(cbObject, cbName, cbData);
		}
	}

	/**
	 * They double-clicked the action.
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * They single-clicked.  This is the one we really respond to.
	 */
	public void widgetSelected(SelectionEvent e)
	{
		if (callback != null)
		{
			callback.invoke();
		}
	}

	/**
	 * Override the one in MenuItem, cause that one pukes
	 * if you subclass.
	 */
	protected void checkSubclass() {
	}
}
