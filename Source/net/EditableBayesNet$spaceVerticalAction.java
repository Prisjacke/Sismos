// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            EditableBayesNet

class this._cls0 extends this._cls0 {

    static final long serialVersionUID = 1L;
    final EditableBayesNet this$0;

    public void redo() {
        try {
            spaceVertical(m_nodes);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getUndoMsg() {
        return (new StringBuilder()).append("Returning ").append(m_nodes.size()).append(" from spaceng vertically.").toString();
    }

    public String getRedoMsg() {
        return (new StringBuilder()).append("Spaceng ").append(m_nodes.size()).append(" nodes vertically.").toString();
    }

    public weka.classifiers.bayes.net.EditableBayesNet.spaceVerticalAction(FastVector fastvector) {
        this$0 = EditableBayesNet.this;
        super(EditableBayesNet.this, fastvector);
    }
}
