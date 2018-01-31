package gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import calculator.App;
import domains.Fields;
import domains.Machinery;
import domains.Points;
import logginig.AbstractLogger.LogLevel;
import sqlutils.DBHelper;
import tools.Config;
import tools.IOTools;
import logginig.LogListener;
import logginig.Logger;

public class WindowLogo extends JWindow implements ActionListener {
	private String imagePath = App.config.getString("resource.image.logo", Config.APP_LOGO_IMAGE);
	private StatusLabel label;
	
	private static Logger logger = Logger.getLogger(WindowLogo.class);
	
	public WindowLogo(){

		int width = App.dim.width*2/5;
		int height = App.dim.height*2/5;
		
		initIface(width, height);		
		this.setLocation((App.dim.width/2) - width/2, (App.dim.height/2) - height/2);		
		this.setSize((int)width, (int)height);
		this.setVisible(true);
		
		boolean deleteDBonStart = false;	//tmp
		try {
			if(deleteDBonStart){
				File file = new File("database.db");
				if(file.exists()){
					file.delete();
				}
			}
			initApplication();
		} catch (SQLException | IOException e) {
			logger.info("Application failed to start: " + e.getMessage());
		}
	}
	
	private void initApplication() throws SQLException, IOException {
		Logger.subscribe(label);
		
		logger.info("Starting application");
		DBHelper.checkDB();

		logger.info("Loading database");
		new Points();
		new Fields();
		new Machinery();
		
		logger.info("Application data ready");

		/*
		 * Прошу не змінювати і не видаляти наступний текст(копірайт) без відома автора.
		 * Це все ж таки моя робота, зроблена з альтруыстичних міркувань.
		 * Користуйтеся нею на на здоров`я. Буду дуже радий якщо ця робота принесе користь.
		 * З повагою, Олексій
		 */
		logger.info("Developed by Oleksii Polishchuk");
		Logger.unsubscribe(label);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.setVisible(false);
		WindowMain mw= new WindowMain();
		mw.setSize(App.dim.width*3/4, App.dim.height*3/4);
		mw.setLocationByPlatform(true);
		EventQueue.invokeLater(() -> {
			mw.setVisible(true);
        });
	}

	private void initIface(double width, double height){
		this.setLayout(new BorderLayout());
		
		Image image = IOTools.readImageFromUrl(imagePath);
		if (image != null) {
			image = image.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
			this.add(new ImagePanel(image), BorderLayout.CENTER);
		}


		JPanel panel = new JPanel();
		panel.setBackground(new Color(237, 237, 237));
		
		JButton button = new JButton("Abort");
		label = new StatusLabel();
		label.setPreferredSize(new Dimension((int)( width - button.getPreferredSize().getWidth()-10), (int )label.getPreferredSize().getHeight()));
		panel.add(label);
		panel.add(button);

		this.add(panel, BorderLayout.SOUTH);
		
		button.addActionListener(this);
	}
	
	public static class ImagePanel extends JPanel{
		Image image;
		
		public ImagePanel(Image image) {
			super();
			this.image = image;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if(image != null){
				g.drawImage(image, 0, 0, this);
			}
		}
	}
	
	public static class StatusLabel extends JLabel implements LogListener{

		public StatusLabel() {
			super("Component initialized");
			this.setFont(new Font("Consolas", Font.PLAIN, 16));
		}

		@Override
		public void update(LogLevel level, Class<?> clazz, String message) {
			this.setText(message);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().getClass().isAssignableFrom(JButton.class)){
			System.exit(ABORT);
		}
		
	}
}
