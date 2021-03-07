package com.showpage.swtfoundation;

import java.lang.reflect.*;

import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * This class lets you use reflection to set up bidirectionly setting of
 * a value from your object to a Text or Combo widget. At initialization,
 * the Text/Combo will have its value set from your object's value. If the
 * user makes changes, the setter will be called inside your object.
 */
public class BoundText implements ModifyListener {
	public Text			text;
	public Combo		combo;
	public Object		object;
	public Method		setter;
	public Method		getter;

	/**
	 * Constructor.
	 * 
	 * @param widget		This should be a Text or Combo widget.
	 * @param _object		Your object
	 * @param methodName	The field name. We assume set[MethodName] and get[MethodName] exist.
	 */
	public BoundText(Control widget, Object _object, String methodName) {
		if (widget instanceof Text) {
			text = (Text)widget;
		}
		else if (widget instanceof Combo) {
			combo = (Combo)widget;
		}
		object = _object;
		
		String setterName = "set" + methodName.substring(0,1).toUpperCase() + methodName.substring(1);
		setter = ReflectUtils.findCallback(object, setterName, String.class);
		getter = ReflectUtils.findCallback(object, methodName, null);
		if (getter == null) {
			String getterName = "get" + methodName.substring(0,1).toUpperCase() + methodName.substring(1);
			getter = ReflectUtils.findCallback(object, getterName, null);
		}
		
		if (getter != null) {
			try {
				String val = (String) getter.invoke(object, (Object[])null);
				setText(val != null ? val : "");
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (text != null) {
			text.addModifyListener(this);
		}
		else if (combo != null) {
			combo.addModifyListener(this);
		}
	}
	
	/**
	 * Queries the widget to get the current value.
	 * 
	 * @return The current value from the widget.
	 */
	public String getText() {
		String retVal = null;
		
		if (text != null) {
			retVal = text.getText();
		}
		else if (combo != null) {
			retVal = combo.getText();
		}
		return retVal;
	}
	
	/**
	 * Set your widget's value.
	 * 
	 * @param val Value to set
	 */
	public void setText(String val) {
		if (val == null) {
			val = "";
		}
		
		if (text != null) {
			text.setText(val);
		}
		else if (combo != null) {
			combo.setText(val);
		}
	}

	/**
	 * Our text was modified.
	 * 
	 * @param event Ignored, but required in the callback.
	 */
	public void modifyText(ModifyEvent event) {
		if (setter != null) {
			Object[] args = new Object[1];
			args[0] = getText();
			try {
				setter.invoke(object, args);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
