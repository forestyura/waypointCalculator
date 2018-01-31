package logginig;

import java.awt.EventQueue;

import gui.ConsolePanel;

public class GuiConsoleLogger extends LogListenerImpl {
	private ConsolePanel console;
	
	public GuiConsoleLogger(ConsolePanel jaConsole) {
		this.console = jaConsole;
	}

	@Override
	public void appendText(String messaage) {
		if (EventQueue.isDispatchThread()) {
			console.output.append(messaage);
			console.output.setCaretPosition(console.output.getText().length());
		} else {
		
		    EventQueue.invokeLater(new Runnable() {
		        @Override
		        public void run() {
		            appendText(messaage);
		        }
		    });
		
}
		
	}
}
