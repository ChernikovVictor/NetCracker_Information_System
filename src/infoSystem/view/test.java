package infoSystem.view;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

public class test extends JFrame {

    public static void main(String... s) {
        new test();
    }

    public test() {
        init();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final JTable t = new JTable(3,3);
    private void init() {
        for (int i = 0; i < t.getColumnCount(); i++) {
            TableColumn col = t.getColumnModel().getColumn(i);
            col.setCellEditor(new MyTableCellEditor());
            col.getCellEditor().addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    System.out.println("test1");
                    MyTableCellEditor cellEditor = (MyTableCellEditor) e.getSource();
                    System.out.println(cellEditor.getCellEditorValue());
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                    System.out.println("test2");
                }
            });
        }
        //t.setCellEditor(new MyTableCellEditor());

        t.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println("Table changed at " + t.getEditingRow() + " " + t.getEditingColumn());
            }
        });

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        t.getInputMap().put(enter, "enter");
        t.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("нажали enter");
            }
        });

        t.setRowHeight(20);
        add(new JScrollPane(t));

        JButton b = new JButton("edit");
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean success = t.editCellAt(1, 1);
                if (success) {
                    boolean toggle = false;
                    boolean extend = false;
                    t.changeSelection(1, 1, toggle, extend);
                }
            }
        });

        add(b,BorderLayout.SOUTH);
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JTextField component = new JTextField();
        private Font font = new Font("Arial Unicode MS", 0, 16);

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int rowIndex, int vColIndex) {
            component.setText((String) value);
            component.setFont(font);

            component.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                    System.out.println("focus lost " + rowIndex + " " + vColIndex);
                    component.setBackground(Color.RED);
                    t.repaint();
                }
            });

            /*component.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {

                }

                @Override
                public void focusLost(FocusEvent e) {
                    System.out.println("focus lost " + rowIndex + " " + vColIndex);
                    component.setBackground(Color.RED);
                }
            });*/

            System.out.println("we are here: " + rowIndex + " " + vColIndex);
            return component;
        }

        @Override
        public Object getCellEditorValue() {
            return component.getText();
        }
    }
}
