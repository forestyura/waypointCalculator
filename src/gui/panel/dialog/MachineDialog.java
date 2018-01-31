package gui.panel.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import domains.DataChangeListener;
import domains.Machinery.Machine;
import domains.PersistentObject;

@SuppressWarnings("serial")
public class MachineDialog extends EntityDialog<Machine> {

	public MachineDialog(DataChangeListener dataChangeListener) {
		super(dataChangeListener);
	}

	private JTextField txtId;
	private JTextField txtName;
	private JTextField txtWidth;
	private JTextField txtFuel;

	@Override
	public void composeEntityElements(JPanel panel, PersistentObject entity) {
		Machine machine = (Machine) entity;
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel lblId = new JLabel("Id");
		lblId.setVisible(machine.id != 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblId, gbc);

		txtId = new JTextField();
		txtId.setVisible(machine.id != 0);
		txtId.setText(String.valueOf(machine.id));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 0, 10);
		gbc.weightx = 1;
		txtId.setEditable(false);
		panel.add(txtId, gbc);

		JLabel lblName = new JLabel("Name");
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblName, gbc);

		txtName = new JTextField(machine.name, 15);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 10);
		gbc.weightx = 1;
		panel.add(txtName, gbc);

		JLabel lblWodth = new JLabel("Work width, m");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 1;
		panel.add(lblWodth, gbc);

		txtWidth = new JTextField(String.valueOf(machine.workWidth));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 0, 0, 10);
		gbc.weightx = 1;
		panel.add(txtWidth, gbc);

		JLabel lblFuel = new JLabel("Fuel consumption, l/100 km");
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(0, 10, 5, 0);
		gbc.weightx = 1;
		panel.add(lblFuel, gbc);

		txtFuel = new JTextField(String.valueOf(machine.workWidth));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 5, 10);
		gbc.weightx = 1;
		panel.add(txtFuel, gbc);
	}

	@Override
	public Machine collectEntity(PersistentObject entity) {
		Machine machine = (Machine) entity;
		machine.id = Integer.valueOf(txtId.getText());
		machine.name = txtName.getText();
		machine.workWidth = Double.valueOf(txtWidth.getText());
		machine.fuel = Double.valueOf(txtFuel.getText());
		return machine;
	}
}
