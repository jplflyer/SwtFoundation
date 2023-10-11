package org.showpage.swtfoundation;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Use CenterLayout to center an object on a point within the container.
 * It is presumed we're being used within a FormLayout.  We can not
 * base this off an offset from another control.
 */
public class CenterLayout extends BaseLayout {
	public Control			control;
	public int				xCenter;

	/**
	 * Constructor.  _xCenter should be the numerator
	 * in the FormAttachment (0..100).
	 * 
	 * @param _control		The widget being placed.
	 * @param _xCenter		The X-location from 0..100.
	 */
	public CenterLayout(Control _control, int _xCenter) {
		super();
		control = _control;
		xCenter = _xCenter;
	}

	/**
	 * Adjust the location of the controlled widget.
	 */
	public void adjust() {
		Point size = control.getSize();
		
		if (size == null) {
			return;
		}
		FormData fd = (FormData)control.getLayoutData();
		fd.left =  new FormAttachment(xCenter, - (size.x / 2));
		control.setLayoutData(fd);
		control.getParent().layout();
	}

}
