package org.showpage.swtfoundation;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * This is a pushbutton that uses reflection with a callback.
 */
public class ReflectionButton extends Button implements SelectionListener
{
	private ReflectionCallback callback = null;
	
	/**
	 * Constructor.
	 * 
	 * @param parent		Container.
	 * @param style			SWT.NONE (etc)
	 * @param label			Text to display
	 * @param cbObject		Callback object.
	 * @param cbName		Callback method name.
	 * @param cbData		Any user data to pass.
	 */
	public ReflectionButton(Composite parent, int style, String label, Object cbObject, String cbName, Object cbData)
	{
		super(parent, style);
		setText(label);
		this.addSelectionListener(this);
		
		if (cbObject != null)
		{
			callback = new ReflectionCallback(cbObject, cbName, cbData);
		}
	}
	
	/**
	 * They double-clicked the action.
	 * 
	 * @param e		Ignored.
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * They single-clicked.  This is the one we really respond to.
	 * 
	 * @param e		Ignored.
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
