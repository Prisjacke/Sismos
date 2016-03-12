// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JTextField;

// Referenced classes of package weka.classifiers.bayes.net:
//            BayesNetGenerator, BIFReader, EditableBayesNet, GUI

class val.jTfSeed
    implements ActionListener {

    final JTextField val$jTfNrOfNodes;
    final JTextField val$jTfNrOfArcs;
    final JTextField val$jTfCardinality;
    final JTextField val$jTfSeed;
    final g this$1;

    public void actionPerformed(ActionEvent actionevent) {
        try {
            BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
            m_BayesNet = bayesnetgenerator;
            m_BayesNet.clearUndoStack();
            String as[] = new String[8];
            as[0] = "-N";
            as[1] = (new StringBuilder()).append("").append(val$jTfNrOfNodes.getText()).toString();
            as[2] = "-A";
            as[3] = (new StringBuilder()).append("").append(val$jTfNrOfArcs.getText()).toString();
            as[4] = "-C";
            as[5] = (new StringBuilder()).append("").append(val$jTfCardinality.getText()).toString();
            as[6] = "-S";
            as[7] = (new StringBuilder()).append("").append(val$jTfSeed.getText()).toString();
            bayesnetgenerator.setOptions(as);
            bayesnetgenerator.generateRandomNetwork();
            BIFReader bifreader = new BIFReader();
            bifreader.processString(m_BayesNet.toXMLBIF03());
            m_BayesNet = new EditableBayesNet(bifreader);
            updateStatus();
            layoutGraph();
            a_datagenerator.setEnabled(true);
            m_Instances = null;
            a_learn.setEnabled(false);
            a_learnCPT.setEnabled(false);
            g.setVisible(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    () {
        this$1 = final_;
        val$jTfNrOfNodes = jtextfield;
        val$jTfNrOfArcs = jtextfield1;
        val$jTfCardinality = jtextfield2;
        val$jTfSeed = JTextField.this;
        super();
    }
}
