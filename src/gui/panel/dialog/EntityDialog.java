package gui.panel.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import domains.DataChangeListener;
import domains.PersistentObject;
import logginig.Logger;

@SuppressWarnings("serial")
public abstract class EntityDialog<T extends PersistentObject> extends JPanel 
	implements ActionListener {

	private Logger logger = Logger.getLogger(EntityDialog.class);
	
	public JDialog dialog = null;
	private Window owner = null;
	private PersistentObject entity = null;
	private JPanel entityPanel = new JPanel();
	private JButton buttonOk;
	private JButton buttonCancel;

	private DataChangeListener dataChangeListener;

	public EntityDialog(DataChangeListener dataChangeListener){
		this.owner = SwingUtilities.getWindowAncestor((Component) dataChangeListener);
		this.dataChangeListener = dataChangeListener;
		initBasicElements();
	}

	private void initBasicElements() {
		buttonOk = new JButton("Ok");
		buttonOk.addActionListener(this);

		buttonCancel = new JButton("Cencel");
		buttonCancel.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weightx = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		add(buttonOk, c);
		add(buttonCancel, c);
	}

	public abstract void composeEntityElements(JPanel panel, PersistentObject entity);
	public abstract PersistentObject collectEntity(PersistentObject entity);

	public void showDialog(PersistentObject entity) {
		this.entity = entity.clone();
		String title = entity.getClass().getSimpleName() + " entity";

		dialog = new LocalDialog((Frame) owner);		
		dialog.setModal(true);
		dialog.setTitle(title);
		dialog.setLocationByPlatform(true);
		dialog.getRootPane().setLayout(new GridBagLayout());
		dialog.getRootPane().setDefaultButton(buttonOk);
		dialog.setResizable(false);
		buttonOk.requestFocus();
		installEscapeCloseOperation(dialog);
		
		/*
		 * Delegates composition of entity-specific elements to instance
		 */
		composeEntityElements(entityPanel, entity);
		
		JPanel container = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.gridy = 1;
		dialog.getRootPane().add(container, gbc);
		
		entityPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = GridBagConstraints.REMAINDER;
		container.add(entityPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.gridy = 2;
		dialog.getRootPane().add(this, gbc);
		
		dialog.pack();
		dialog.setVisible(true);
	}

	private void closeDialog() {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog.getRootPane().removeAll();
			dialog.removeAll();
			dialog.dispose();

			entity = null; 	
			entityPanel = new JPanel();
		}
	}

	private class LocalDialog extends JDialog {

		public LocalDialog(Frame owner) {
			super(owner);
		}

		@Override
		protected void processWindowEvent(WindowEvent e) {
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				closeDialog();
			}
			super.processWindowEvent(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == buttonOk) {
				
				entity = collectEntity(entity);
				String error = entity.validate(); 
				
				if(!"".equals(error)){
					JOptionPane.showMessageDialog(this,
							error,
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}else{
					boolean zeroId = entity.id == 0;
					logger.info(zeroId ? "Creating new entity " : "Modifying entity " + entity);
					entity.save();					
					logger.info(zeroId ? "\tEntity created" : "\nEntity changed");					
					
					dataChangeListener.dataChanged();					
					closeDialog();
				}
				
			} else if (e.getSource() == buttonCancel) {
				entity.dispose();
				closeDialog();
				
			}
		} catch (SQLException e1) {
			logger.info("Failed to save record");
			logger.info(e1);
			JOptionPane.showMessageDialog(this,
					e1.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	} 

	public static void installEscapeCloseOperation(final JDialog dialog) {
		Action dispatchClosing = new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		};
		JRootPane root = dialog.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)
				, "com.spodding.tackline.dispatch:WINDOW_CLOSING");
		root.getActionMap().put("com.spodding.tackline.dispatch:WINDOW_CLOSING", dispatchClosing);
	}
}