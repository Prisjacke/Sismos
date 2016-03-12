// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;


// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class m_nBase extends m_nBase {

    static final long serialVersionUID = 1L;
    int m_nBase;
    String m_sXML;
    final EditableBayesNet this$0;

    public void undo() {
        try {
            for (int i = getNrOfNodes() - 1; i >= m_nBase; i--) {
                deleteNode(i);
            }

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void redo() {
        try {
            paste(m_sXML, 1);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    (String s, int i) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this);
        m_sXML = s;
        m_nBase = i;
    }
}
