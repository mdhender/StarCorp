package starcorp.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import starcorp.client.gui.windows.MainWindow;

public abstract class AWindow implements IComponent {

	protected final Display display;
	protected final Shell shell;

	public AWindow(Display display) {
		this(display, SWT.CLOSE);
	}
	
	public AWindow(Display display, int style) {
		this.display = display;
		this.shell = new Shell(display,style);
		this.shell.addListener(SWT.Close, new Listener() {
		      public void handleEvent(Event event) {
		    	  close();
		      }
		});
	}
	
	public abstract MainWindow getMainWindow();
	
	protected void center() {
		Rectangle bounds = display.getBounds();
		Point size = shell.getSize();
		
		int x = (bounds.width - size.x) / 2;
		int y = (bounds.height - size.y) / 2;
		
		shell.setBounds(x, y, size.x, size.y);
	}

	public void focus() {
		shell.forceFocus();
	}
	
	public void redraw() {
		shell.pack();
	}

	public int messageBox(String heading, String text, int style) {
		MessageBox msgBox = new MessageBox(shell, style);
		msgBox.setText(heading);
		msgBox.setMessage(text);
		return msgBox.open();
	}
	
	public Point computeSize() {
		return shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	protected void close() {
		dispose();
	}

	public Shell getShell() {
		return shell;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((shell == null) ? 0 : shell.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AWindow other = (AWindow) obj;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (shell == null) {
			if (other.shell != null)
				return false;
		} else if (!shell.equals(other.shell))
			return false;
		return true;
	}
}