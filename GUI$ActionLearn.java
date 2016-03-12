// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;
import java.io.PrintStream;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyDialog;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class setEnabled extends setEnabled {

    private static final long serialVersionUID = 0xfff8c19f04fe8464L;
    JDialog dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (dlg == null) {
            dlg = new JDialog();
            dlg.setTitle("Learn Bayesian Network");
            JButton jbutton = new JButton("Options");
            jbutton.addActionListener(new ActionListener() {

                final GUI.ActionLearn this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    try {
                        GenericObjectEditor.registerEditors();
                        GenericObjectEditor genericobjecteditor = new GenericObjectEditor(true);
                        genericobjecteditor.setClassType(weka/classifiers/Classifier);
                        genericobjecteditor.setValue(m_BayesNet);
                        PropertyDialog propertydialog;
                        if (PropertyDialog.getParentDialog(this$0) != null) {
                            propertydialog = new PropertyDialog(PropertyDialog.getParentDialog(this$0), genericobjecteditor, 100, 100);
                        } else {
                            propertydialog = new PropertyDialog(PropertyDialog.getParentFrame(this$0), genericobjecteditor, 100, 100);
                        }
                        propertydialog.addWindowListener(new WindowAdapter() {

                            final _cls1 this$2;

                            public void windowClosing(WindowEvent windowevent) {
                                PropertyEditor propertyeditor = ((PropertyDialog)windowevent.getSource()).getEditor();
                                Object obj = propertyeditor.getValue();
                                String s = "";
                                if (obj instanceof OptionHandler) {
                                    s = Utils.joinOptions(((OptionHandler)obj).getOptions());
                                    try {
                                        m_BayesNet.setOptions(((OptionHandler)obj).getOptions());
                                    }
                                    catch (Exception exception1) {
                                        exception1.printStackTrace();
                                    }
                                }
                                System.out.println((new StringBuilder()).append(obj.getClass().getName()).append(" ").append(s).toString());
                                System.exit(0);
                            }

                     {
                        this$2 = _cls1.this;
                        super();
                    }
                        });
                        propertydialog.setVisible(true);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                        System.err.println(exception.getMessage());
                    }
                    m_BayesNet.clearUndoStack();
                    a_undo.setEnabled(false);
                    a_redo.setEnabled(false);
                }

             {
                this$1 = GUI.ActionLearn.this;
                super();
            }
            });
            JTextField jtextfield = new JTextField(40);
            jtextfield.setHorizontalAlignment(0);
            jtextfield.setText((new StringBuilder()).append("").append(Utils.joinOptions(m_BayesNet.getOptions())).toString());
            JButton jbutton1 = new JButton("Learn");
            jbutton1.addActionListener(new ActionListener() {

                final GUI.ActionLearn this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    try {
                        m_BayesNet.buildClassifier(m_Instances);
                        layoutGraph();
                        updateStatus();
                        m_BayesNet.clearUndoStack();
                        dlg.setVisible(false);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionLearn.this;
                super();
            }
            });
            JButton jbutton2 = new JButton("Cancel");
            jbutton2.setMnemonic('C');
            jbutton2.addActionListener(new ActionListener() {

                final GUI.ActionLearn this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionLearn.this;
                super();
            }
            });
            GridBagConstraints gridbagconstraints = new GridBagConstraints();
            dlg.setLayout(new GridBagLayout());
            Container container = new Container();
            container.setLayout(new GridBagLayout());
            gridbagconstraints.gridwidth = 2;
            gridbagconstraints.insets = new Insets(8, 0, 0, 0);
            gridbagconstraints.anchor = 18;
            gridbagconstraints.gridwidth = -1;
            gridbagconstraints.fill = 2;
            container.add(jbutton, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jtextfield, gridbagconstraints);
            gridbagconstraints.fill = 2;
            dlg.add(container, gridbagconstraints);
            dlg.add(jbutton1);
            gridbagconstraints.gridwidth = 0;
            dlg.add(jbutton2);
        }
        dlg.setLocation(100, 100);
        dlg.setVisible(true);
        dlg.setSize(dlg.getPreferredSize());
        dlg.setVisible(false);
        dlg.setVisible(true);
        dlg.repaint();
    }

    public _cls3.this._cls1() {
        this$0 = GUI.this;
        super(GUI.this, "Learn Network", "Learn Bayesian Network", "learn", "ctrl L");
        dlg = null;
        setEnabled(false);
    }
}
