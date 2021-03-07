package com.showpage.swtfoundation;

import java.util.*;

import org.apache.log4j.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * A main frame is any window that can be dragged around the
 * entire screen.
 */
public class MainFrame {
	private static Logger log = Logger.getLogger(MainFrame.class);
	
	public static final int	ALIGN_TOP_TO_TOP			= 0x01;		// Top alignment
	public static final int	ALIGN_BOTTOM_TO_BOTTOM	= 0x01;		// Bottom alignment
	public static final int	ALIGN_TOP_TO_BOTTOM		= 0x04;		// Top to bottom (building a window from top edge)
	public static final int	ALIGN_BOTTOM_TO_TOP		= 0x08;		// Bottom to top (building a window from lower edge)
	
	public static final int	ALIGN_LEFT_TO_LEFT		= 0x10;		// Left-justified
	public static final int	ALIGN_RIGHT_TO_RIGHT		= 0x20;		// Right-justified
	public static final int	ALIGN_LEFT_TO_RIGHT		= 0x40;		// Left to right
	public static final int	ALIGN_RIGHT_TO_LEFT		= 0x80;		// Right to left
	
	private static int lastX = 0;
	private static int lastY = 0;
	private static int deltaX = 30;
	private static int deltaY = 20;
	
	public int			shellStyle = SWT.SHELL_TRIM;
	public Shell		parent;
	public Shell		shell;
	public Text			statusTF;
	public boolean		exitFromRunLoop = false;
	public boolean		centerWindow = false;
	private boolean		locationWasSet = false;
	public boolean		wantStatusTF = false;
	
	private ArrayList<BaseLayout>	registeredLayouts = new ArrayList<BaseLayout>();
	
	/**
	 * Initialize graphics.
	 */
	public void initialize(Shell _parent, int windowWidth, int windowHeight) {
		parent = _parent;
		
		if (parent == null) {
			shell = new Shell(SwtEnvironment.singleton().display(), shellStyle);
		}
		else {
			shell = new Shell(parent, SWT.SHELL_TRIM);
		}
		shell.setSize(windowWidth, windowHeight);
		shell.setLayout(new FormLayout());
		
		if (wantStatusTF) {
			log.debug("Creating statusTF.");
			statusTF = new Text(shell, SWT.READ_ONLY);
    		FormData fd = new FormData();
    		fd.left = new FormAttachment(0, 2);
    		fd.right = new FormAttachment(100, -2);
    		fd.bottom = new FormAttachment(100, -2);
    		statusTF.setLayoutData(fd);
		}
	}
	
	/**
	 * Run until we're gone.
	 */
	public void run()
	{
		Display display = SwtEnvironment.singleton().display();
		boolean shouldSleep = false;
		while (!exitFromRunLoop && !shell.isDisposed())
		{
			if (shouldSleep)
			{
				display.sleep();
				shouldSleep = false;
			}
			shouldSleep = !display.readAndDispatch();
			dispatchLoopHook();
		}
	}
	
	/**
	 * Set our location to a particular (x,y).
	 */
	public void setLocation(int x, int y)
	{
		shell.setLocation(x, y);
		locationWasSet = true;
	}
	
	/**
	 * Set the location of this window so it's touching another window.
	 */
	public void snapToWindow(MainFrame window, int direction)
	{
		Point windowLocation = window.shell.getLocation();
		Point windowSize = window.shell.getSize();
		Point size = shell.getSize();
		int x = 0;
		int y = 0;
		
		if ((direction & ALIGN_TOP_TO_TOP) != 0)
		{
			y = windowLocation.y;
		}
		else if ((direction & ALIGN_BOTTOM_TO_BOTTOM) != 0)
		{
			y = windowLocation.y + windowSize.y - size.y;
		}
		else if ((direction & ALIGN_TOP_TO_BOTTOM) != 0)
		{
			y = windowLocation.y + windowSize.y;
		}
		else if ((direction & ALIGN_BOTTOM_TO_TOP) != 0)
		{
			y = windowLocation.y - size.y;
		}
		else
		{
			// Default: ALign the tops.
			y = windowLocation.y;
		}
		
		
		if ((direction & ALIGN_LEFT_TO_LEFT) != 0)
		{
			x = windowLocation.x;
		}
		else if ((direction & ALIGN_RIGHT_TO_RIGHT) != 0)
		{
			x = windowLocation.x + windowSize.x - size.x;
		}
		else if ((direction & ALIGN_LEFT_TO_RIGHT) != 0)
		{
			x = windowLocation.x + windowSize.x;
		}
		else if ((direction & ALIGN_RIGHT_TO_LEFT) != 0)
		{
			x = windowLocation.x - size.x;
		}
		else
		{
			// Default: Align the left sides.
			x = windowLocation.x;
		}
		setLocation(x,y);
	}
	
	/**
	 * This method gets called at the bottom of the dispatch
	 * loop.  Subclasses can override this, but you're encouraged
	 * to be very careful to avoid high-cost methods.
	 */
	public void dispatchLoopHook()
	{
	}
	
	/**
	 * Force the window visible.
	 */
	public void open()
	{
		SwtEnvironment.singleton().registerWindow(this);
		if (!locationWasSet)
		{
			Point size = shell.getSize();
			Display display = SwtEnvironment.singleton().display();
			Monitor monitor = display.getPrimaryMonitor();
			Rectangle bounds = (monitor != null) ? monitor.getBounds() : display.getBounds();
			int x = lastX + deltaX;
			int y = lastY + deltaY;
			
			if (centerWindow)
			{
				x = (bounds.width - size.x) / 2;
				y = (bounds.height - size.y) / 2;
			}
			else
			{
				if (x < 0)
				{
					x = -x;
					deltaX = -deltaX;
				}
				else if (x + size.x > bounds.width)
				{
					int howFar = x + size.x - bounds.width;
					x = x - howFar - howFar;
					deltaX = -deltaX;
				}
				
				if (y < 0)
				{
					y = -y;
					deltaY = -deltaY;
				}
				else if (y + size.y > bounds.height)
				{
					int howFar = y + size.y - bounds.height;
					y = y - howFar - howFar;
					deltaY = -deltaY;
				}
				lastX = x;
				lastY = y;
			}
			
			shell.setLocation(x, y);
		}
		shell.open();
		
		for (BaseLayout layout : registeredLayouts)
		{
			layout.adjust();
		}
	}
	
	/**
	 * Remember a layout group that will need to be adjusted when the
	 * page is open.
	 */
	public void registerBaseLayout(BaseLayout layoutGroup)
	{
		registeredLayouts.add(layoutGroup);
	}
	
	/**
	 * A safe setter that is reasonable with nulls.
	 */
	public void setTextField(Text field, String val) {
		if (val == null) {
			val = "";
		}
		field.setText(val);
	}
	
	/**
	 * A safe setter that is reasonable with nulls.
	 */
	public void setTextField(Label field, String val) {
		if (val == null) {
			val = "";
		}
		field.setText(val);
	}
	
	/**
	 * Get the text from a text field, returning null for empty strings.
	 */
	public String getTextField(Text field) {
		String s = field.getText().trim();
		if (s.length() == 0) {
			s = null;
		}
		return s;
	}
	/**
	 * Set the text in the statusTF area.
	 */
	public void setStatus(String text) {
		if (statusTF != null) {
			log.debug("Status: " + text);
    		setTextField(statusTF, text);
		}
	}
	
	/**
	 * Clear the text in the statusTF.
	 */
	public void clearStatus() {
		if (statusTF != null) {
			statusTF.setText("");
		}
	}
	
	/**
	 * Center this object.
	 */
	public void centerOn(Control w, int location) {
		CenterLayout cl = new CenterLayout(w, location);
		registerBaseLayout(cl);
	}
	
	/**
	 * Center this object near the bottom of the page.
	 */
	public FormData centerNearBottom(Control w, int xCenter, int yOffset) {
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, xCenter);
		fd.bottom = new FormAttachment(100, -yOffset);
		w.setLayoutData(fd);
		centerOn(w, xCenter);
		return fd;
	}
	
	/**
	 * Center this object near the top of the page.
	 */
	public FormData centerNearTop(Control w, int xCenter, int yOffset) {
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, xCenter);
		fd.bottom = new FormAttachment(0, yOffset);
		w.setLayoutData(fd);
		centerOn(w, xCenter);
		return fd;
	}
	
	public FormData centerBelow(Control widgetToCenter, Control widgetAbove, int yOffset) {
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, 0);
		fd.top = new FormAttachment(widgetAbove, yOffset);
		widgetToCenter.setLayoutData(fd);
		centerOn(widgetToCenter, 50);
		return fd;
	}
	
	public void fillPanel(Control w) {
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -5);
		fd.top = new FormAttachment(0, 5);
		if (statusTF == null) {
    		fd.bottom = new FormAttachment(100, -5);
		}
		else {
    		fd.bottom = new FormAttachment(statusTF, -5);
		}
		w.setLayoutData(fd);
	}
	
	/**
	 * Used to lay out an entire widget location in a single call.
	 * For any particular edge, it will be unattached if the edge widget,
	 * percent and offset are all null/0.
	 */
	public FormData layout(Control w,
			Control leftW, int leftPercent, int leftOffset,
			Control rightW, int rightPercent, int rightOffset,
			Control topW, int topPercent, int topOffset,
			Control bottomW, int bottomPercent, int bottomOffset
			)
	{
		FormData fd = new FormData();
		if (leftW != null || leftPercent != 0 || leftOffset != 0) {
    		fd.left = formAttachment(leftW, leftPercent, leftOffset);
		}
		if (rightW != null || rightPercent != 0 || rightOffset != 0) {
    		fd.right = formAttachment(rightW, rightPercent, rightOffset);
		}
		if (topW != null || topPercent != 0 || topOffset != 0) {
    		fd.top = formAttachment(topW, topPercent, topOffset);
		}
		if (bottomW != null || bottomPercent != 0 || bottomOffset != 0) {
    		fd.bottom = formAttachment(bottomW, bottomPercent, bottomOffset);
		}
		
		w.setLayoutData(fd);
		return fd;
	}
	
	/**
	 * Quick way to create and fill in a form attachment.
	 */
	public FormAttachment formAttachment(Control w, int percent, int offset) {
		FormAttachment retVal = null;
		if (w != null) {
			retVal = new FormAttachment(w, offset);
		}
		else if (percent != 0 || offset != 0) {
			retVal = new FormAttachment(percent, offset);
		}
		return retVal;
	}
	
	/**
	 * Reset the height of a widget by adjusting his form data.
	 */
	public void setHeight(Control w, int height) {
		FormData fd = (FormData)w.getLayoutData();
		if (fd != null) {
			fd.height = height;
		}
	}
}
