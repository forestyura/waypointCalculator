package logginig;

import java.io.PrintStream;

public class PrintStreamLogger extends LogListenerImpl{

	private PrintStream ps;
	
	public PrintStreamLogger(PrintStream ps) {
		super();
		this.ps = ps;
	}
	
	public void appendText(final String text) {
		ps.print(text);
    }

}
