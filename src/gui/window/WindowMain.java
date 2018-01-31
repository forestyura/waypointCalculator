package gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

import calculator.App;
import domains.Fields;
import domains.Machinery;
import domains.Points;
import geometry.Displayable;
import geometry.Point;
import geometry.Polygon;
import gui.ConsolePanel;
import gui.DisplayPanel;
import gui.panel.FieldListPanel;
import gui.panel.MachineListPanel;
import logginig.Logger;
import logic.WaypointFinder;
import tools.Config;
import tools.ExportImport;
import tools.IOTools;

@SuppressWarnings("serial")
public class WindowMain extends JFrame{
	protected static Logger logger = Logger.getLogger(WindowMain.class);
	
	final static int windowWidth = 1280;
	final static int windowhHeight = 924;
	
	public static WindowMain instance;
	
	private FieldListPanel fieldList;
	private MachineListPanel machineList;
	private DisplayPanel display;	
	private ConsolePanel console;

	private WaypointFinder wpf;

	private WindowAbout aboutFrame = new WindowAbout();
	
	public static double workWidth = 0;
	
	public WindowMain() throws HeadlessException {
		super();
    	Image icon = IOTools.readImageFromUrl(App.config.getString("resource.image.icon", Config.APP_ICON_PATH));
    	setIconImage(icon); 
        
		try {
			initUI();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.info("Failed to initialize interface");
			logger.info(e);
		}	
		
		WindowMain.instance = this;
	}

	public void initUI() throws InstantiationException, IllegalAccessException {    
        setTitle("Waypoint Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initMenu();       
        JPanel windowContainer = new JPanel(new BorderLayout());	    
        JPanel sidePanel = new JPanel(new BorderLayout());
             
        fieldList = new FieldListPanel("Fields", () -> {
        	
				display.field = fieldList.getSelected();
		    	
		    	Polygon polygon = new Polygon(display.field.points);
				display.setMapForArea(polygon.getDimention());
				
				display.getCanvas().clear();
				
				display.addDisplayObject(DisplayPanel.GROUP_FIELD, (ArrayList<Point>) polygon, new Color(0, 255, 0, 127));
				display.addDisplayObject(DisplayPanel.GROUP_FIELD, (Displayable) polygon, new Color(50, 30, 210, 32));
				
				display.getCanvas().render();
				display.label.setText("Now please select harvester");
				
		});
        
        machineList = new MachineListPanel("Harvesters",() -> {
        	
        	display.machine = machineList.getSelected(); 		
    		
    		logger.info("Invoking building waypoints"); 
    		if(display.field == null){
    			logger.info("Field is not ready");
    			display.label.setText("Please select field[!!!]");
    			return;
    		}

    		wpf = new WaypointFinder(display.field.points, display.machine.workWidth);
    		
    		display.clearDisplayObject(DisplayPanel.GROUP_WP);
    		
    		display.addDisplayObject(DisplayPanel.GROUP_WP, wpf.getWaypoints(), Color.RED);
    		display.addDisplayObject(DisplayPanel.GROUP_WP, wpf.getPath(), Color.YELLOW);		
    		display.render();
    		
    		double distance = wpf.getPath().getTotalDistance();
    		int turns = wpf.getPath().getWaypoints().size();
    		double fuelConsumption = display.machine.fuel;
    		double totalConsumption = fuelConsumption * distance / 1000;
    		String result = String.format("Overal distance: %.2f m, "
						    				+ "Number of turns: %d, "
						    				+ "Fuel consumption: %.2f litres(%.4f l/km)"
						    				, distance, turns, totalConsumption, fuelConsumption);
    		logger.info(result);
    		display.label.setText(result);
    		
        });
        display = new DisplayPanel();    
        console = new ConsolePanel();       

        windowContainer.add(display);
        JSplitPane mainSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, windowContainer, sidePanel);
        mainSplitPanel.setResizeWeight(0.9);

        JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fieldList, machineList);
        JSplitPane splitPanel2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPanel, console);
        
        splitPanel.setResizeWeight(0.5);
        splitPanel2.setResizeWeight(0.5);
        sidePanel.add(splitPanel2, BorderLayout.CENTER);
        
        this.add(mainSplitPanel, BorderLayout.CENTER);   
        
        Fields.addDataChangedListener(fieldList);
        Machinery.addDataChangedListener(machineList);
	}
	private void runAbout() {
		aboutFrame .display();
	}

	private void runExport() {
		JFileChooser fileChooser = new JFileChooser(
				new File(App.config.getString("resource.dir.export", Config.APP_EXPORT_DIR)));
		fileChooser.setDialogTitle("Export to");
		fileChooser.setSelectedFile(
				new File(App.config.getString("resource.dir.export", Config.APP_EXPORT_DIR)
						+ "/export.xml"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("XML document", "xml"));
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				ExportImport.exportXML(fileChooser.getSelectedFile());
				JOptionPane.showMessageDialog(this,
					    "Exported completed");
			} catch (JAXBException e) {
				JOptionPane.showMessageDialog(this,
					    "Error occured while export:\n" + e.getCause().getMessage(),
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void runImport() {
		JFileChooser fileChooser = new JFileChooser(
				new File(App.config.getString("resource.dir.export", Config.APP_EXPORT_DIR)));
		
		fileChooser.setDialogTitle("Import from");
		fileChooser.setSelectedFile(
				new File(App.config.getString("resource.dir.export", Config.APP_EXPORT_DIR) + "/export.xml"));
		
		fileChooser.setFileFilter(new FileNameExtensionFilter("XML document", "xml"));
		
		logger.info("Import data");
		if (fileChooser.showDialog(this, "Import") == JFileChooser.APPROVE_OPTION) {
			try {
				ExportImport.importXML(new FileInputStream(fileChooser.getSelectedFile()));
				Machinery.loadAll();
				Points.loadAll();
				Fields.loadAll();
				
				logger.info("Import successfull");
				JOptionPane.showMessageDialog(this,
					    "Import completed");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
					    "Error occured while import:\n" + e.getCause().getMessage(),
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		} else {
			logger.info("Canceled import");
		}
	}
	
	public void initMenu(){
		 JMenuBar menubar = new JMenuBar();

       JMenu file = new JMenu("File");
       file.setMnemonic(KeyEvent.VK_F);
       
       JMenu tools = new JMenu("Tools");
       tools.setMnemonic(KeyEvent.VK_T);
       
       JMenu help = new JMenu("Help");
       help.setMnemonic(KeyEvent.VK_H);

       JMenuItem eMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
       eMenuItem.setToolTipText("Exit application");
       eMenuItem.addActionListener((ActionEvent event) -> {
           System.exit(0);
       });
       
       JMenuItem importMenuItem = new JMenuItem("Import", KeyEvent.VK_I);
       importMenuItem.setToolTipText("Import from XML file");
       importMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
       importMenuItem.addActionListener((ActionEvent event) -> {
           runImport();
       });
       
       JMenuItem exportMenuItem = new JMenuItem("Export", KeyEvent.VK_E);
       exportMenuItem.setToolTipText("Export to XML file");
       exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
       exportMenuItem.addActionListener((ActionEvent event) -> {
           runExport();
       });
       
       JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
       aboutMenuItem.setToolTipText("Show information");
       aboutMenuItem.addActionListener((ActionEvent event) -> {
           runAbout();
       });

       file.add(eMenuItem);
       
       tools.add(importMenuItem);
       tools.add(exportMenuItem);
       
       help.add(aboutMenuItem);

       menubar.add(file);
       menubar.add(tools);
       menubar.add(help);

       setJMenuBar(menubar);
	}
}

