package org.showpage.swtfoundation;

import org.eclipse.swt.graphics.*;

/**
 * Defines the callbacks used from our TableView.
 */
public interface ITableViewUser {
	/**
	 *  Return this row's background color.
	 *  
	 *  @param object contained in the row.
	 *  @return Color to use
	 */
	public Color backgroundColorForRow(Object object);
	
	/**
	 * Return this row's background color.
	 * 
	 *  @param object contained in the row.
	 *  @return Color to use
	 */
	public Color foregroundColorForRow(Object object);
}
