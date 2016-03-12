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

class this._cls2 extends WindowAdapter {

    final Options this$2;

    public void windowClosing(WindowEvent windowevent) {
        PropertyEditor propertyeditor = ((PropertyDialog)windowevent.getSource()).getEditor();
        Object obj = propertyeditor.getValue();
        String s = "";
        if (obj instanceof OptionHandler) {
            s = Utils.joinOptions(((OptionHandler)obj).getOptions());
            try {
                m_BayesNet.setOptions(((OptionHandler)obj).getOptions());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        System.out.println((new StringBuilder()).append(obj.getClass().getName()).append(" ").append(s).toString());
        System.exit(0);
    }

    is._cls1() {
        this$2 = this._cls2.this;
        super();
    }

    // Unreferenced inner class weka/classifiers/bayes/net/GUI$ActionLearn$1

/* anonymous class */
    class GUI.ActionLearn._cls1
        implements ActionListener {

        final GUI.ActionLearn this$1;

        public void actionPerformed(ActionEvent actionevent) {
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
                propertydialog.addWindowListener(new GUI.ActionLearn._cls1._cls1());
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
    }

}
