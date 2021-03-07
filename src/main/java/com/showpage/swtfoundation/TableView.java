package com.showpage.swtfoundation;

import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;
import org.eclipse.swt.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * A wrapper around a table, used to provide additional
 * support as needed.
 *
 */
public class TableView implements SelectionListener, DragSourceListener {
	public static Logger log = Logger.getLogger(TableView.class);
	
	public int						style = SWT.BORDER;
	public Table					table;
	public DragSource				tableDragSource;
	public Vector<TableViewColumn>	columnInfo;
	public Vector<Object>			data = new Vector<Object>();
	public Vector<TableItem>		items = new Vector<TableItem>();
	private ITableViewUser			usingObject = null;
	private TableViewColumn			sortColumn = null;
	private Object					selectionCallbackObject;
	private Method					selectionCallbackMethod;
	private Object					singleSelectionCallbackObject;
	private Method					singleSelectionCallbackMethod;
	
	public TableView() {
	}
	
	public TableView(Composite parent, Vector<TableViewColumn> _columns) {
		initialize(parent, _columns);
	}
	
	/**
	 * Initialize the window.
	 */
	public void initialize(Composite parent, Vector<TableViewColumn> _columns) {
		table = new Table(parent, style);
		table.addSelectionListener(this);
		columnInfo = _columns;
		for (Enumeration<TableViewColumn> ptr = columnInfo.elements(); ptr.hasMoreElements(); ) {
			TableViewColumn col = ptr.nextElement();
			TableColumn tCol = new TableColumn(table, col.alignment);
			tCol.setText(col.rowHeader);
			tCol.setMoveable(true);
			tCol.setResizable(true);
			tCol.setWidth(100);
			tCol.addSelectionListener(this);
		}
		table.setHeaderVisible(true);
	}
	
	/**
	 * Set up the callback when they double-click a row.
	 */
	public void setupSelectionCallback(Object callbackObject, String callbackMethodName, Class<?> callbackArgClass)
	{
		if (callbackObject == null)
		{
			selectionCallbackObject = null;
			selectionCallbackMethod = null;
			return;
		}
		
		Class<?> cbClass = callbackObject.getClass();
		try
		{
			Method method = cbClass.getMethod(callbackMethodName, callbackArgClass);
			if (method != null)
			{
				selectionCallbackObject = callbackObject;
				selectionCallbackMethod = method;
			}
		}
		catch (Exception e)
		{
			log.warn("Exception setting up a callback", e);
		}
	}
	
	/**
	 * Set up the callback when they single-click a row.
	 */
	public void setupSingleSelectionCallback(Object callbackObject, String callbackMethodName, Class<?> callbackArgClass)
	{
		if (callbackObject == null)
		{
			singleSelectionCallbackObject = null;
			singleSelectionCallbackMethod = null;
			return;
		}
		
		Class<?> cbClass = callbackObject.getClass();
		try
		{
			Method method = cbClass.getMethod(callbackMethodName, callbackArgClass);
			if (method != null)
			{
				singleSelectionCallbackObject = callbackObject;
				singleSelectionCallbackMethod = method;
			}
		}
		catch (Exception e)
		{
			log.warn("Exception setting up a callback", e);
		}
	}
	
	public void setupDragAndDrop(Transfer transfer)
	{
		tableDragSource = new DragSource(table, DND.DROP_COPY);
		Transfer[] transferTypes = new Transfer[] { transfer };
		tableDragSource.setTransfer(transferTypes);
		tableDragSource.addDragListener(this);
	}
	
	public void pack() {
		TableColumn[] cols = table.getColumns();
		for (int index = 0; index < cols.length; ++index) {
			cols[index].pack();
		}
		
	}
	
	/**
	 * Add a vector of objects to this window.
	 */
	public void addData(Collection<?> _data)
	{
		for (Object obj : _data) {
			addUnsorted(obj);
		}
		sortByColumn(sortColumn, false);
	}
	
	/**
	 * Add the column, unsorted.  We'll sort it later.
	 */
	private void addUnsorted(Object obj)
	{
		TableItem ti = new TableItem(table, 0);
		ti.setData(obj);
		setTableItemForObject(ti, obj);
		data.addElement(obj);
		items.addElement(ti);
		table.setItemCount(data.size());
	}
	
	/**
	 * Add an object to this window.
	 */
	public void addObject(Object obj)
	{
		addUnsorted(obj);
		sortByColumn(sortColumn, false);
	}
	
	public void removeObject(Object obj)
	{
		int index = data.indexOf(obj);
		
		if (index >= 0)
		{
			data.remove(index);
			items.remove(index);
			table.remove(index);
		}
	}
	
	public void removeAll()
	{
		data.removeAllElements();
		items.removeAllElements();
		table.setItemCount(0);
	}
	
	/**
	 * Set the fields in this TableItem to properly display this object.
	 */
	private void setTableItemForObject(TableItem ti, Object obj) {
		int index = 0;
		for (Enumeration<TableViewColumn> ptr = columnInfo.elements(); ptr.hasMoreElements(); ) {
			TableViewColumn col = ptr.nextElement();
			Object val = col.getValue(obj);
			if (val == null) {
				ti.setText(index++, "");
			}
			else {
				ti.setText(index++, val.toString());
			}
			if (usingObject != null) {
				Color background = usingObject.backgroundColorForRow(obj);
				Color foreground = usingObject.foregroundColorForRow(obj);
				if (background != null) {
					ti.setBackground(background);
				}
				if (foreground != null) {
					ti.setForeground(foreground);
				}
			}
		}
	}
	
	/**
	 * We need to be able to make some callbacks to whoever is using
	 * us.  Currently, this is only for setting foreground and background
	 * colors based on the objects we're displaying, but we could end up
	 * with action methods and other cool stuff.
	 */
	public void setUser(ITableViewUser user) {
		usingObject = user;
	}
	
	/**
	 * Something has happened to this object.
	 */
	public void objectChanged(Object obj) {
		int index = data.indexOf(obj);
		if (index >= 0) {
			TableItem ti = (TableItem)items.elementAt(index);
			setTableItemForObject(ti, obj);
		}
	}

	/**
	 * Double-clicked a column.
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		Widget widget = event.widget;
		if (widget instanceof Table) {
			int[] selection = table.getSelectionIndices();
			if (selection == null)
			{
			}
			else
			{
				for (int index = 0; index < selection.length; ++index)
				{
					if (selectionCallbackMethod != null)
					{
						try
						{
							Object selectedObject = data.elementAt(selection[index]);
							selectionCallbackMethod.invoke(selectionCallbackObject, selectedObject);
						}
						catch (Exception e)
						{
							log.warn("Exception invoking callback", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Something was selected, probably a header column.
	 */
	public void widgetSelected(SelectionEvent event) {
		Widget widget = event.widget;
		if (widget instanceof TableColumn) {
			TableColumn tCol = (TableColumn)widget;
			int colNum = table.indexOf(tCol);
			TableViewColumn tvc = columnInfo.elementAt(colNum);
			sortByColumn(tvc, true);
		}
		else if (widget instanceof Table) {
			int[] selection = table.getSelectionIndices();
			if (selection == null)
			{
			}
			else
			{
				for (int index = 0; index < selection.length; ++index)
				{
					if (singleSelectionCallbackMethod != null)
					{
						try
						{
							Object selectedObject = data.elementAt(selection[index]);
							singleSelectionCallbackMethod.invoke(singleSelectionCallbackObject, selectedObject);
						}
						catch (Exception e)
						{
							log.warn("Exception invoking callback", e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Return the selected objects.
	 */
	public ArrayList<Object> getSelectedObjects() {
		ArrayList<Object> selected = new ArrayList<Object>();
		int[] selection = table.getSelectionIndices();
		if (selection != null) {
			for (int index = 0; index < selection.length; ++index)
			{
				Object selectedObject = data.elementAt(selection[index]);
				selected.add(selectedObject);
			}
		}
		return selected;
	}
	
	/**
	 * Sort the data for this column.
	 */
	private void sortByColumn(TableViewColumn tvc, boolean reverseDirection)
	{
		if (tvc == null)
		{
			tvc = columnInfo.elementAt(0);
		}
		if (reverseDirection && (sortColumn == tvc))
		{
			// Reverse sort if they resort on the currently sorting column.
			tvc.sortAscending = !tvc.sortAscending;
		}
		sortColumn = tvc;
		
		Object[] dataArray = data.toArray();
		Arrays.sort(dataArray, tvc);
		for (int index = 0; index < dataArray.length; ++index) {
			data.set(index, dataArray[index]);
			setTableItemForObject(items.elementAt(index), dataArray[index]);
		}
	}

	/**
	 * Drag and drop has completed.
	 */
	public void dragFinished(DragSourceEvent event) {
	}

	/**
	 * Drag and drop is in progress.
	 */
	public void dragSetData(DragSourceEvent event) {
		if (table.getSelectionCount() > 0)
		{
			//int[] indexes = table.getSelectionIndices();
			//Object obj = data.elementAt(indexes[0]);
			//event.data = data.elementAt(indexes[0]);
		}
	}

	/**
	 * Drag and drop has begun.
	 */
	public void dragStart(DragSourceEvent event) {
		if (table.getSelectionCount() > 0)
		{
			event.doit = true;
		}
	}
	
}
