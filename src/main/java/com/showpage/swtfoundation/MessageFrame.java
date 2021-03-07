package com.showpage.swtfoundation;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Creates a popup window with a message.
 */
public class MessageFrame extends MainFrame
{
	private Label			label;
	private ReflectionButton	okPB;
	
	/**
	 * Constructor.
	 * 
	 * @param _parent		Can probably use null.
	 * @param windowWidth	Width.
	 * @param windowHeight	Height.
	 * @param text			Text to display.
	 */
	public void initialize( Shell _parent, int windowWidth, int windowHeight, String text )
	{
		// Must call MainFrame's initialization first.
		super.initialize( _parent, windowWidth, windowHeight );
		shell.setLayout(new FormLayout());
		
		label = new Label(shell, SWT.NONE);
		okPB = new ReflectionButton(shell, SWT.NONE, "OK", this, "ok", null);
		label.setText(text);
		
		FormData fd;
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 30);
		fd.left = new FormAttachment(0, 20);
		fd.right = new FormAttachment(100, -20);
		label.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(label, 30);
		fd.left = new FormAttachment(50, -30);
		okPB.setLayoutData(fd);
		
		shell.pack();
	}
	
	/**
	 * All done.
	 */
	public void ok()
	{
		shell.close();
	}
}
