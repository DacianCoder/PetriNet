package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import gui.elements.Arc;
import gui.elements.Node;
import gui.elements.Point;
import gui.elements.Transition;
import logic.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static enums.HistoryAction.DELETE;

public class EditItem extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner value;
    private JTextArea valueTextArea;
    private JTextArea nameTextArea;
    private JTextField name;
    private JButton deleteButton;

    public EditItem(Component parent, Object editableItem, Matrix matrix) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(parent);
        setSize(300, 400);
        value.setValue(((ValueNameComponent) editableItem).getValue());
        name.setText(((ValueNameComponent) editableItem).getName());

        if (editableItem instanceof Transition) {
            value.setEnabled(false);
        }
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                name.requestFocus();
            }
        });

        buttonOK.addActionListener(e -> onOK(editableItem));
        buttonCancel.addActionListener(e -> onCancel());
        deleteButton.addActionListener(e -> onDelete(editableItem, matrix));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.pack();
        setVisible(true);
    }

    private void onDelete(Object editableItem, Matrix matrix) {
        if (editableItem instanceof Node) {
            matrix.deleteAttachedArcs((Node) editableItem, true);
            matrix.addHistory(editableItem, DELETE);
            matrix.getNodes().remove(((Node) editableItem).getName());
            dispose();
            return;
        }
        if (editableItem instanceof Arc) {
            matrix.addHistory(editableItem, DELETE);
            matrix.getArcs().remove(((Arc) editableItem).getName());
            Node origin = ((Arc) editableItem).getOrigin();
            if (origin instanceof Transition) {
                ((Transition) origin).getLeavingArcs().remove(editableItem);
            } else {
                ((Transition) ((Arc) editableItem).getDestination()).getEnteringArcs().remove(editableItem);
            }
            dispose();
        }
    }

    private void onOK(Object editableItem) {
        if (editableItem instanceof Transition) {
            ((Transition) editableItem).setName(name.getText());
            dispose();
            return;
        }
        if (editableItem instanceof Point) {
            int x = (Integer) value.getValue();
            if (x < 0) {
                valueTextArea.setForeground(Color.RED);
                return;
            }
            ((Point) editableItem).setName(name.getText());
            ((Point) editableItem).setValue(x);
            dispose();
            return;
        }
        if (editableItem instanceof Arc) {
            int x = (Integer) value.getValue();
            if (x <= 0) {
                valueTextArea.setForeground(Color.RED);
                return;
            }
            ((Arc) editableItem).setName(name.getText());
            ((Arc) editableItem).setValue(x);
            dispose();

        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
