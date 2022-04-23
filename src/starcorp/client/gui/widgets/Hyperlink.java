package starcorp.client.gui.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * A control that looks like a web browser hyperlink.
 * You may add SelectionListeners to this control and detect selection events as 
 * if it were a button.  Selection events are fired when the user clicks on the 
 * link. 
 * 
 * @author ted stockwell
 */
public class Hyperlink extends Composite {

    private Label _text;

    private ArrayList<SelectionListener> _listeners = new ArrayList<SelectionListener>();

    private Cursor _handCursor;
    private Cursor _arrowCursor;

    // underline font
    private Font _font= null;
    
    static public void main(String[] args) {
        final Shell shell = new Shell();
        shell.setLayout(new GridLayout());
        new Text(shell, SWT.NONE).setLayoutData(new GridData(GridData.BEGINNING));

        Label lLabel = new Label(shell, SWT.NONE);
        lLabel.setLayoutData(new GridData(GridData.BEGINNING));
        lLabel.setText("This is a Label");
        Hyperlink lHyperlink = new Hyperlink(shell, SWT.NONE);
        lHyperlink.setLayoutData(new GridData(GridData.BEGINNING));
        lHyperlink.setText("This is a Hyperlink");
        lHyperlink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                MessageBox lMessageBox = new MessageBox(shell);
                lMessageBox.setText("Hello from the hyperlink");
                lMessageBox.open();
            }
        });
        new Text(shell, SWT.NONE).setLayoutData(new GridData(GridData.BEGINNING));
        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!shell.getDisplay().readAndDispatch())
                shell.getDisplay().sleep();
        }

    }

    public Hyperlink(Composite parent, int style) {
        super(parent, SWT.NONE);

        _handCursor = new Cursor(getDisplay(), SWT.CURSOR_HAND);
        _arrowCursor = new Cursor(getDisplay(), SWT.CURSOR_ARROW);

        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                _handCursor.dispose();
                _arrowCursor.dispose();
                if (_font != null)
                    _font.dispose();
            }
        });

        GridLayout lLayout = new GridLayout();
        lLayout.marginHeight = lLayout.marginWidth = 0;
        setLayout(lLayout);

        _text = new Label(this, SWT.NO_FOCUS);
//        _text.setDoubleClickEnabled(false);

        /*
         * Create a hidden text control.
         * Neither Composites, nor Labels, will be included in a parent 
         * Composite's tab control list.  So, in order to get our hyperlink 
         * control to act like a regular control (included in the tab order) I 
         * embed a hidden text control in the composite.  This hidden text 
         * control will be included in the tab order.  When the text control 
         * is given the focus then a focus outline is painted around the label. 
         */
        final Text lText = new Text(this, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        GridData lGridData= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        lGridData.widthHint= 1;
        lText.setLayoutData(lGridData);

        _text.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                if (lText.isFocusControl() || isFocusControl() || _text.isFocusControl()) {
                    Point s = _text.getSize();
                    e.gc.drawFocus(0, 0, s.x, s.y);
                }
            }
        });

        lText.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(Event event) {
                switch (event.detail) {
                    case SWT.TRAVERSE_RETURN :
                        fireWidgetSelected(event);

                    case SWT.TRAVERSE_ARROW_NEXT :
                    case SWT.TRAVERSE_ARROW_PREVIOUS :
                    case SWT.TRAVERSE_PAGE_NEXT :
                    case SWT.TRAVERSE_PAGE_PREVIOUS :
                        event.detail = SWT.TRAVERSE_NONE;

                }
            }
        });

        _text.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                lText.setFocus();
            }
        });

        lText.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                _text.redraw();
            }
            public void focusLost(FocusEvent e) {
                _text.redraw();
            }
        });

        _text.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        _text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

        _text.setForeground(new Color(getDisplay(), 52, 2, 253));

        // change cursor to hand on mouse over
        _text.addMouseTrackListener(new MouseTrackAdapter() {
            public void mouseEnter(MouseEvent e) {
                ((Control)e.widget).setCursor(_handCursor);
            }
            public void mouseExit(MouseEvent e) {
                ((Control)e.widget).setCursor(_arrowCursor);
            }
        });

        _text.addListener(SWT.MouseDown, new Listener() {
            public void handleEvent(Event event) {
                fireWidgetSelected(event);
            }
        });
        
        FontData[] lFontData = _text.getFont().getFontData();
        for (int i = 0; i < lFontData.length; i++) {
            lFontData[i].data.lfUnderline = 1;
        }
        _font = new Font(getDisplay(), lFontData);
        _text.setFont(_font);
                
    }

    public void setForeground(Color color) {
        _text.setForeground(color);
    }
    public void setBackground(Color color) {
        _text.setBackground(color);
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
        // hide the second text control by restricting the size of the composite 
        // so that the 2nd control is not visible.
        return _text.computeSize(wHint, hHint, changed);
    }

    protected void fireWidgetSelected(Event e) {
        SelectionEvent lEvent = new SelectionEvent(e);
        lEvent.widget = this;
        for (Iterator<SelectionListener> i = _listeners.iterator(); i.hasNext();) {
            SelectionListener lListener = i.next();
            lListener.widgetSelected(lEvent);
        }
    }

    public void addSelectionListener(SelectionListener listener) {
        if (!_listeners.contains(listener))
            _listeners.add(listener);
    }
    public void removeSelectionListener(SelectionListener listener) {
        _listeners.remove(listener);
    }

    public void setText(String text) {
        _text.setText(text);
    }

    public String getText() {
        return _text.getText();
    }
    
    public void setImage(Image image) {
    	_text.setImage(image);
    }
    
    public Image getImage() {
    	return _text.getImage();
    }
    
    public void setBounds(Rectangle rect) {
    	_text.setBounds(rect);
    }
    
    public Rectangle getBounds() {
    	return _text.getBounds();
    }
}