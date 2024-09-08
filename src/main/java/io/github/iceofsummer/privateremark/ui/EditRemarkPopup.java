package io.github.iceofsummer.privateremark.ui;

import com.intellij.ide.plugins.newui.UpdateButton;

import javax.swing.*;

public class EditRemarkPopup {


    private JTextArea remark;
    private JPanel root;
    private JButton cancelBtn;
    private UpdateButton saveBtn;


    public JPanel getRoot() {
        return root;
    }

    public JTextArea getRemark() {
        return remark;
    }


    public JButton getCancelBtn() {
        return cancelBtn;
    }

    public UpdateButton getSaveBtn() {
        return saveBtn;
    }
}
