// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import javax.swing.Action;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI

class e {

    String m_sText;
    final GUI this$0;

    public boolean hasText() {
        return m_sText != null;
    }

    public String getText() {
        return m_sText;
    }

    public void setText(String s) {
        m_sText = s;
        a_pastenode.setEnabled(true);
    }

    public () {
        this$0 = GUI.this;
        super();
        m_sText = null;
        if (a_pastenode != null) {
            a_pastenode.setEnabled(false);
        }
    }
}
