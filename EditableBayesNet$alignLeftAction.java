// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class t> extends t> {

    static final long serialVersionUID = 1L;
    final EditableBayesNet this$0;

    public void redo() {
        try {
            alignLeft(m_nodes);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from aliging nodes to the left.").toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Aligning ").append(m_nodes.size()).append(" nodes to the left.").toString();
    }

    public (FastVector fastvector) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this, fastvector);
    }
}
