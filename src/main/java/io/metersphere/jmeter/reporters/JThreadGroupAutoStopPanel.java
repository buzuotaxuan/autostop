package io.metersphere.jmeter.reporters;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JThreadGroupAutoStopPanel extends javax.swing.JPanel {

    /**
     * Creates new form JAutoStopPanel
     */
    public JThreadGroupAutoStopPanel() {
        initComponents();
        registerJTextfieldForValidation(jTextFieldDelaySec, false);

        initFields();
    }

    public void configure(ThreadGroupAutoStop testElement) {
        jTextFieldDelaySec.setText(testElement.getDelaySecs());
    }

    public void modifyTestElement(ThreadGroupAutoStop testElement) {
        testElement.setDelaySecs(jTextFieldDelaySec.getText());
    }

    public final void initFields() {
        jTextFieldDelaySec.setText("30");
    }

    private int getIntValue(JTextField tf) {
        int ret;
        try {
            ret = Integer.valueOf(tf.getText());
        } catch (NumberFormatException ex) {
            ret = -1;
        }
        return ret;
    }

    private float getFloatValue(JTextField tf) {
        float ret;
        try {
            ret = Float.valueOf(tf.getText());
        } catch (NumberFormatException ex) {
            ret = -1;
        }
        return ret;
    }

    private boolean isVariableValue(JTextField tf) {
        String value = tf.getText();
        if (value != null) {
            return value.startsWith("${") && value.endsWith("}");
        } else {
            return false;
        }
    }

    private void processBullets() {
    }

    private void setJTextFieldColor(final JTextField tf, boolean isFloat) {
        if (!isFloat && (getIntValue(tf) > -1 || isVariableValue(tf))) {
            tf.setForeground(Color.black);
        } else if (isFloat && (getFloatValue(tf) > -1 || isVariableValue(tf))) {
            tf.setForeground(Color.black);
        } else {
            tf.setForeground(Color.red);
        }
    }

    private void registerJTextfieldForValidation(final JTextField tf, final boolean isFloat) {
        tf.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                setJTextFieldColor(tf, isFloat);
                processBullets();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                setJTextFieldColor(tf, isFloat);
                processBullets();
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                setJTextFieldColor(tf, isFloat);
                processBullets();
            }
        });
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabelBulletRespTime = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDelaySec = new JTextField();
        jLabel3 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("ThreadGroup Shutdown Criteria(only for ThreadGroup or ConcurrencyThreadGroup)"));
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("The ThreadGroup will be automatically stopped when threadgroup reaches duration ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jLabel1, gridBagConstraints);

        jPanel1.add(jLabelBulletRespTime);

        jLabel10.setText("delay for");
        jPanel1.add(jLabel10);

        jTextFieldDelaySec.setColumns(5);
        jTextFieldDelaySec.setHorizontalAlignment(JTextField.RIGHT);
        jTextFieldDelaySec.setInheritsPopupMenu(true);
        jTextFieldDelaySec.setMaximumSize(new java.awt.Dimension(100, 20));
        jPanel1.add(jTextFieldDelaySec);

        jLabel3.setText("seconds");
        jPanel1.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jPanel1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelBulletRespTime;
    private javax.swing.JPanel jPanel1;
    private JTextField jTextFieldDelaySec;
    // End of variables declaration//GEN-END:variables

}