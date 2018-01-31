package gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import domains.DataChangeListener;
import domains.PersistentObject;
import domains.ToolTipRecord;
import gui.panel.dialog.EntityDialog;
import gui.window.WindowMain;
import logginig.Logger;

@SuppressWarnings("serial")
public abstract class AbstractListPanel<T extends PersistentObject> extends JPanel 
							implements DataChangeListener, MouseListener, ActionListener{

	private Logger logger = Logger.getLogger(AbstractListPanel.class);
	
	private SelectedListener selectedListener;
	
	public interface SelectedListener {
		public void onDoubleClick();
	}
	
	protected T emptyEntity;
	private EntityDialog<T> entityDialog = null;
	private JScrollPane scrollPane;
	public JList<T> displayList;
	
	protected JButton buttonAdd;
	protected JButton buttonRemove;
	protected JButton buttonModify;

	public AbstractListPanel(String listName, SelectedListener selectedListener, T emptyEntity) {
		super();
		this.selectedListener = selectedListener;
		this.emptyEntity = emptyEntity;
		entityDialog = assignDialog();
		initAbstractUI(listName);
		setPreferredSize(new Dimension(150, 150));
		
	    dataChanged();
	}
	
	public abstract void populateListData(DefaultListModel<T> model);
	
	public abstract EntityDialog<T> assignDialog();
	

	private void initAbstractUI(String listName) {
		this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(listName));
        
        /*
         * Tooltips are configured to be shown for entities with type
         * ToolTipRecord
         */
		this.displayList = new JList<T>(){
			
			@Override
			public String getToolTipText(MouseEvent evt) {
		        int index = locationToIndex(evt.getPoint());
		        if(index < 0) return null;
		        T item = getModel().getElementAt(index);
		        if(item instanceof ToolTipRecord){
		        	return ((ToolTipRecord) item).getTooltip();
		        }
		        return item.toString();
		      }

		};
		
		this.displayList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    this.displayList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	    this.displayList.setModel(new DefaultListModel<T>());	    
	    this.displayList.addMouseListener(this);
	    
        scrollPane = new JScrollPane(displayList
        		, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        		, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    this.add(scrollPane, BorderLayout.CENTER);
	    
	    /*
	     * 3-button section below list
	     */
	    buttonAdd = new JButton("Add");
		buttonAdd.addActionListener(this);	
		
	    buttonRemove = new JButton("Remove");
	    buttonRemove.setEnabled(false);
	    buttonRemove.addActionListener(this);
	    
	    buttonModify = new JButton("Edit");
	    buttonModify.setEnabled(false);
		buttonModify.addActionListener(this);

	    /*
	     * Button are enabled/disabled depending on selection
	     */
	    displayList.getSelectionModel().addListSelectionListener(e -> {
	    	
	    	buttonAdd.setEnabled(true);
    		buttonRemove.setEnabled(!displayList.isSelectionEmpty());
	    	buttonModify.setEnabled(!displayList.isSelectionEmpty()
	    			&& displayList.getSelectedValuesList().size() == 1);
	    });
	    
	    JPanel managePanel = new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 1;
	    managePanel.add(buttonAdd, c);
	    managePanel.add(buttonRemove, c);
	    managePanel.add(buttonModify, c);
	    this.add(managePanel, BorderLayout.SOUTH);
	}

	public T getSelected() {
		return displayList.getSelectedValue();
	}

	@Override
	public void dataChanged() {
		populateListData((DefaultListModel<T>) displayList.getModel());
	    revalidate();
		repaint();
	}

	private void remove(){
		int cnt = displayList.getSelectedValuesList().size();
		String elements = displayList.getSelectedValuesList()
				.stream()
				.map( e -> e.toString())
				.collect(Collectors.joining("</li><li>"));
		
		Object[] options = {"Ok", "Cancel"};
		int n = JOptionPane.showOptionDialog(WindowMain.instance,
		String.format("<html>You are about to delete %d record%s:<br><ul><li>%s</ul></html>"
				, cnt, cnt > 1 ? "s" : "", elements),
		"Deleting",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[1]);
		
		if(n == JOptionPane.OK_OPTION){		
			for(T entity : displayList.getSelectedValuesList()){
				try {
					entity.delete();
					entity.loadAll();;
				} catch (SQLException e1) {
					logger.info("Failed to delete record");
					logger.info(e1.getMessage());
					JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(entityDialog),
							e1.getMessage(),
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == buttonAdd){
			
			entityDialog.showDialog(emptyEntity);
			
		} else if(source == buttonModify){
			
			entityDialog.showDialog(displayList.getSelectedValue());
		
		} else if(source == buttonRemove){
			
			remove();			
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.displayList.isEnabled()){
			if(this.displayList.getModel().getSize() > 0 
					&& e.getClickCount()==2 && !e.isConsumed()){
		    	e.consume();
		    	if(selectedListener != null) 
		    		selectedListener.onDoubleClick();
		    }
		}				
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}