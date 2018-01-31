package gui.panel;

import javax.swing.DefaultListModel;

import domains.DataChangeListener;
import domains.Fields;
import domains.Fields.Field;
import domains.PersistentObject;
import gui.panel.dialog.EntityDialog;
import gui.panel.dialog.FieldDialog;

@SuppressWarnings("serial")
public class FieldListPanel extends AbstractListPanel<Field> implements DataChangeListener {

	public FieldListPanel(String string, SelectedListener selectedListener){
		super(string, selectedListener, new Field());
	}

	@Override
	public void populateListData(DefaultListModel<Field> model) {
		model.clear();
		for (PersistentObject m : Fields.getEntities()) {
			model.addElement((Field) m);
		}
	}

	@Override
	public EntityDialog<Field> assignDialog() {
		return new FieldDialog(this);
	}
	
}
