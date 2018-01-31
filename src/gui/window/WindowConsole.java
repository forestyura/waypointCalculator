package gui.window;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import calculator.App;
import gui.ConsolePanel;

@SuppressWarnings("serial")
public class WindowConsole extends JFrame {
	private ConsolePanel console;

	public WindowConsole(ConsolePanel console) throws HeadlessException {
		super("Console");
		this.console = console;
		setSize(App.dim.width*2/4, App.dim.height*2/4);
		setLocationByPlatform(true);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BorderLayout());	
	}
	
	public void display(){
		this.getContentPane().removeAll();
		
		this.add(new JScrollPane(new JTextArea(console.output.getText())), BorderLayout.CENTER);

		setVisible(true);
	}
}
