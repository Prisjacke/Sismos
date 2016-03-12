// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class this._cls1
    implements ActionListener {

    final this._cls1 this$1;

    public void actionPerformed(ActionEvent actionevent) {
        if (actionevent.getActionCommand().equals("Rename")) {
            renameNode(m_nCurrentNode);
            return;
        }
        if (actionevent.getActionCommand().equals("Add parent")) {
            addArcInto(m_nCurrentNode);
            return;
        }
        if (actionevent.getActionCommand().equals("Add value")) {
            addValue();
            return;
        }
        if (actionevent.getActionCommand().equals("Delete node")) {
            deleteNode(m_nCurrentNode);
            return;
        }
        if (actionevent.getActionCommand().equals("Edit CPT")) {
            editCPT(m_nCurrentNode);
            return;
        } else {
            repaint();
            return;
        }
    }

    () {
        this$1 = this._cls1.this;
        super();
    }
}
