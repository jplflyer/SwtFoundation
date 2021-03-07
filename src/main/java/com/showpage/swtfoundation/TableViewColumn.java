package com.showpage.swtfoundation;

import java.lang.reflect.*;
import java.util.*;

import org.eclipse.swt.*;

/**
 * This class is used by TableView to display one column of data.
 */
public class TableViewColumn implements Comparator<Object> {
	/** The header text for this column. */
	public String	rowHeader;

	/** Use during reflection to grab the data from our containing objects. */
	public String	name;

	/** This is the getter retrieved using reflection. */
	public Method	getter;

	/**
	 * This is confusing.  When retrieving values for display, we might
	 * be calling a getter directly on the object that represents a row.
	 * Or we might have to call a method on the page that contains the
	 * table.  In the first case, getterObject is null.  In the second
	 * case, getterObject will be the page in question.
	 */
	public Object	getterObject;
	public Method	formatterMethod;
	public Object	formatterObject;
	public Field		field;
	public Comparator<String>	sortComparator;
	public boolean	sortAscending = true;
	public int		alignment;

	/**
	 * Constructor.  TheClass should be the class that actually contains
	 * the getter referenced by name.  In this case, we assume that theClass
	 * is the class of the data types the table holds.
	 * 
	 * @param _header		Text to display
	 * @param _name			Method name
	 * @param theClass		Class of objects we're storing
	 */
	public TableViewColumn(String _header, String _name, Class<?> theClass) {
		rowHeader = _header;
		name = _name;

		lookForGetter(theClass, null);
		alignment = SWT.LEFT;
	}

	/**
	 * Constructor.  In this case, the getter is probably on the window that
	 * contains the table.  The window object is _object.  The window's class
	 * is getterClass.  And the data objects we're storing are of type
	 * getterArgClass.  We assume that the window has a getter with the given
	 * method name and expects a single argument of type getterArgClass.
	 *
	 * Of course, the getter could exist on any generic object.  The point
	 * is that it's NOT on the object representing the row, but on some
	 * sort of container or controller.
	 * 
	 * @param _header		Text to display
	 * @param _name			Callback method name
	 * @param _object		Object to hit
	 * @param getterClass	Store the getter method here
	 * @param getterArgClass	We're looking for a getter for this Class type.
	 */
	public TableViewColumn(String _header, String _name, Object _object, Class<?> getterClass, Class<?> getterArgClass) {
		rowHeader = _header;
		name = _name;
		getterObject = _object;

		lookForGetter(getterClass, getterArgClass);
		alignment = SWT.LEFT;
	}

	/**
	 * Retrieve the value for this column from this object.
	 * Note that we might be getting the value directly from the
	 * object we're iterating over -or- we might be getting the value
	 * from a method that expects us to pass in the object.
	 * 
	 * @param from	The object we're calling data from
	 * @return The value.
	 */
	public Object getValue(Object from) {
		Object retVal = null;
		try {
			if (getter != null) {
				if (getterObject != null)
				{
					retVal = getter.invoke(getterObject, from);
				}
				else
				{
					retVal = getter.invoke(from);
				}
			}
			else if (field != null) {
				retVal = field.get(from);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if ((formatterMethod != null) && (formatterObject != null) && (retVal != null))
		{
			try
			{
				retVal = formatterMethod.invoke(formatterObject, retVal);
			}
			catch (Exception ex)
			{
				System.err.printf("Exception: %s", ex.getMessage());
				ex.printStackTrace();
			}
		}

		return retVal;
	}

	/**
	 * Try to look up our getter.  If not found, then grab the field.
	 * 
	 * @param getterClass Desired return type
	 * @param getterArgClass If here are arguments.
	 */
	private void lookForGetter(Class<?> getterClass, Class<?> getterArgClass) {
		getter = tryMethod(getterClass, name, getterArgClass);
		if (getter == null) {
			getter = tryMethod(getterClass, "get" + name.substring(0,1).toUpperCase() + name.substring(1), getterArgClass);
		}
		if (getter == null) {
			field = tryField(getterClass, name);
			if (field == null) {
				System.out.println("No such field / getter " + name + " for class " + getterClass.getName());
			}
		}
	}

	/**
	 * Try calling a getter.
	 * 
	 * @param getterClass		Class of object we're working against
	 * @param methodName		Name of the method
	 * @param getterArgClass	Return type
	 * @return The method
	 */
	private Method tryMethod(Class<?> getterClass, String methodName, Class<?> getterArgClass) {
		Method retVal = null;
		try {
			Class<?>[] argsList = (getterArgClass == null) ? null : new Class<?>[] { getterArgClass };
			retVal = getterClass.getMethod(methodName, argsList);
		}
		catch (Exception e) {
			// Do nothing
		}

		return retVal;
	}

	/**
	 * If we can't find getters and setters, we see if we can hit the field directly.
	 * 
	 * @param theClass		The class
	 * @param fieldName		Name of the field
	 * @return A public field of this name.
	 */
	private Field tryField(Class<?> theClass, String fieldName) {
		Field retVal = null;

		try {
			retVal = theClass.getField(fieldName);
		}
		catch (Exception e) {
			// do nothing.
		}

		return retVal;
	}

	/**
	 * Compare these two objects.
	 * 
	 * @param arg1	First object
	 * @param arg2	Second object
	 */
	@SuppressWarnings("unchecked")
	public int compare(Object arg1, Object arg2) {
		int retVal = 0;
		Object o1 = getValue(arg1);
		Object o2 = getValue(arg2);

		Comparable c1 = (Comparable)o1;
		Comparable c2 = (Comparable)o2;

		if (sortComparator != null)
		{
			retVal = sortComparator.compare((String)o1, (String)o2);
		}
		else
		{
			if ((arg1 == null) || (arg2 == null)) {
				if (arg1 == arg2) {
					return 0;
				}
				if (arg1 == null) {
					return -1;
				}
				return 1;
			}

			retVal = c1.compareTo(c2);
		}

		if (!sortAscending) {
			retVal = -retVal;
		}
		return retVal;
	}

	/**
	 * Return an int only if obj is a non-null Number
	 * 
	 * @param obj An object
	 * @return 0 on null or non-number
	 */
	public int objectToInt(Object obj)
	{
		if (obj == null)
		{
			return 0;
		}
		if (obj instanceof Number)
		{
			return ((Number)obj).intValue();
		}

		return 0;
	}
}
