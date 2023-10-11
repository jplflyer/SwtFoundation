package org.showpage.swtfoundation;

import java.lang.reflect.*;

/**
 * Various widgets that need to implement a callback by name
 * can use this to do it.
 */
public class ReflectionCallback
{
	private Object	callbackObject;
	private String	callbackName;
	private Object	callbackData;
	private Method	actionMethod;
	private boolean	passData;

	/**
	 * Constructor.
	 * 
	 * @param cbObject	The object.
	 * @param cbName	Method name -- object.method().
	 * @param cbData	User data (may be null).
	 */
	public ReflectionCallback(Object cbObject, String cbName, Object cbData)
	{
		callbackObject = cbObject;
		callbackName = cbName;
		callbackData = cbData;
		
		if (callbackObject != null)
		{
			actionMethod = ReflectUtils.findCallback(callbackObject, callbackName, cbData);
			if (actionMethod != null)
			{
				passData = (actionMethod.getParameterTypes().length == 1);
			}
		}
	}
	
	/**
	 * Invoke the callback.
	 */
	public void invoke() {
		try
		{
			if (actionMethod != null)
			{
				Object[] args = null;
				if (passData)
				{
					args = new Object[] {callbackData};
				}
				actionMethod.invoke(callbackObject, args);
			}
		}
		catch (Exception ex)
		{
			System.err.printf("Exception: %s", ex.getMessage());
			ex.printStackTrace();
		}
	}
}
