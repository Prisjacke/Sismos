// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;
import java.io.PrintStream;
import javax.swing.Action;
import weka.classifiers.Classifier;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyDialog;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class this._cls1
    implements ActionListener {

    final learUndoStack this$1;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            GenericObjectEditor.registerEditors();
            GenericObjectEditor genericobjecteditor = new GenericObjectEditor(true);
            genericobjecteditor.setClassType(weka/classifiers/Classifier);
            genericobjecteditor.setValue(m_BayesNet);
            PropertyDialog propertydialog;
            if (PropertyDialog.getParentDialog(_fld0) != null) {
                propertydialog = new PropertyDialog(PropertyDialog.getParentDialog(_fld0), genericobjecteditor, 100, 100);
            } else {
                propertydialog = new PropertyDialog(PropertyDialog.getParentFrame(_fld0), genericobjecteditor, 100, 100);
            }
            propertydialog.addWindowListener(new WindowAdapter() {

                final GUI.ActionLearn._cls1 this$2;

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
                this$2 = GUI.ActionLearn._cls1.this;
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

    _cls1.this._cls2() {
        this$1 = this._cls1.this;
        super();
    }
}
