package org.showpage.swtfoundation;

import java.lang.reflect.*;
import java.util.*;

/**
 * Utilities to make reflection easier.
 */
public class ReflectUtils {
	
	/**
	 * Dump this bean.
	 * 
	 * @param obj		Object we'll dump. We call any methods beginning with "get".
	 * @param filler	A prefix to print.
	 * @param buffer	Where we're dumping to.
	 */
	public static void dumpBean(Object obj, String filler, StringBuffer buffer) {
		Class<?>		objClass = obj.getClass();
		Method[]		methods = objClass.getMethods();
		
		Arrays.sort(methods, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		for (int index = 0; index < methods.length; ++index) {
			Method m = methods[index];
			Class<?> mClass = m.getDeclaringClass();
			
			if (mClass == Object.class) {
				continue;
			}
			if (m.getName().startsWith("get")) {
				try {
					Object val = m.invoke(obj);
					buffer.append(filler);
					buffer.append(m.getName());
					buffer.append(" = ");
					dumpObject(val, filler, buffer);
				}
				catch (Exception e) {
					// Ignored
				}
			}
		}
	}
	
	/**
	 * Dump this object.  We've already done the prompt.
	 * 
	 * @param val		Object we'll dump. We call any methods beginning with "get".
	 * @param filler	A prefix to print.
	 * @param buffer	Where we're dumping to.
	 */
	public static void dumpObject(Object val, String filler, StringBuffer buffer) {
		if (val == null) {
			buffer.append("null\n");
			return;
		}
		
		if (val instanceof String) {
			buffer.append(val);
			buffer.append("\n");
			return;
		}
		if (val instanceof Number) {
			buffer.append(val.toString());
			buffer.append("\n");
			return;
		}
		if (val instanceof java.util.List<?>) {
			buffer.append("(\n");
			dumpList((java.util.List<?>)val, filler + "  ", buffer);
			buffer.append(filler);
			buffer.append(")\n");
			return;
		}
		
		buffer.append("{\n");
		dumpBean(val, filler + "  ", buffer);
		buffer.append(filler);
		buffer.append("}\n");
	}
	
	/**
	 * Dump this list's elements.
	 * 
	 * @param list		List to dump
	 * @param filler	A prefix
	 * @param buffer	The output buffer
	 */
	public static void dumpList(java.util.List<?> list, String filler, StringBuffer buffer) {
		int index = 0;
		for (java.util.Iterator<?> iter = list.iterator(); iter.hasNext(); ) {
			Object obj = iter.next();
			buffer.append(filler);
			buffer.append(index);
			buffer.append(" = ");
			dumpObject(obj, filler + "  ", buffer);
			++index;
		}
	}
	
	/**
	 * Find the named callback.
	 * 
	 * We look for a method on this object with this name.
	 * If cbData is non-null, we try to find one that can
	 * take an argument.  If cbData is null, we'll take the
	 * first method of the proper name that takes either 0 or 1
	 * arguments -- thus assuming that a null argument is legal.
	 * 
	 * @param callbackObject	Object in question
	 * @param callbackName		Method name
	 * @param cbData			User data
	 * 
	 * @return The method to invoke.
	 */
	public static Method findCallback(Object callbackObject, String callbackName, Object cbData)
	{
		Method[] methods = callbackObject.getClass().getMethods();
		for (int index = 0; index < methods.length; ++index)
		{
			Method method = methods[index];
			
			if (method.getName().equals(callbackName))
			{
				Class<?>[] params = method.getParameterTypes();
				
				if (params.length == 0)
				{
					// Parameter-less method.
					if (cbData == null)
					{
						return method;
					}
				}
				if (params.length == 1)
				{
					return method;
				}
			}
		}
		return null;
	}
}
