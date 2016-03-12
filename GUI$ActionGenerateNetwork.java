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
import javax.swing.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, BayesNetGenerator, BIFReader, EditableBayesNet

class dlg extends dlg {

    private static final long serialVersionUID = 0xfff8c19f04fe8463L;
    int m_nNrOfNodes;
    int m_nNrOfArcs;
    int m_nCardinality;
    int m_nSeed;
    JDialog dlg;
    final GUI this$0;

    public void actionPerformed(ActionEvent actionevent) {
        if (dlg == null) {
            dlg = new JDialog();
            dlg.setTitle("Generate Random Bayesian Network Options");
            JLabel jlabel = new JLabel("Nr of nodes");
            final JTextField jTfNrOfNodes = new JTextField(3);
            jTfNrOfNodes.setHorizontalAlignment(0);
            jTfNrOfNodes.setText((new StringBuilder()).append("").append(m_nNrOfNodes).toString());
            JLabel jlabel1 = new JLabel("Nr of arcs");
            final JTextField jTfNrOfArcs = new JTextField(3);
            jTfNrOfArcs.setHorizontalAlignment(0);
            jTfNrOfArcs.setText((new StringBuilder()).append("").append(m_nNrOfArcs).toString());
            JLabel jlabel2 = new JLabel("Cardinality");
            final JTextField jTfCardinality = new JTextField(3);
            jTfCardinality.setHorizontalAlignment(0);
            jTfCardinality.setText((new StringBuilder()).append("").append(m_nCardinality).toString());
            JLabel jlabel3 = new JLabel("Random seed");
            final JTextField jTfSeed = new JTextField(3);
            jTfSeed.setHorizontalAlignment(0);
            jTfSeed.setText((new StringBuilder()).append("").append(m_nSeed).toString());
            JButton jbutton = new JButton("Generate Network");
            jbutton.addActionListener(new ActionListener() {

                final JTextField val$jTfNrOfNodes;
                final JTextField val$jTfNrOfArcs;
                final JTextField val$jTfCardinality;
                final JTextField val$jTfSeed;
                final GUI.ActionGenerateNetwork this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    try {
                        BayesNetGenerator bayesnetgenerator = new BayesNetGenerator();
                        m_BayesNet = bayesnetgenerator;
                        m_BayesNet.clearUndoStack();
                        String as[] = new String[8];
                        as[0] = "-N";
                        as[1] = (new StringBuilder()).append("").append(jTfNrOfNodes.getText()).toString();
                        as[2] = "-A";
                        as[3] = (new StringBuilder()).append("").append(jTfNrOfArcs.getText()).toString();
                        as[4] = "-C";
                        as[5] = (new StringBuilder()).append("").append(jTfCardinality.getText()).toString();
                        as[6] = "-S";
                        as[7] = (new StringBuilder()).append("").append(jTfSeed.getText()).toString();
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
                        dlg.setVisible(false);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

             {
                this$1 = GUI.ActionGenerateNetwork.this;
                jTfNrOfNodes = jtextfield;
                jTfNrOfArcs = jtextfield1;
                jTfCardinality = jtextfield2;
                jTfSeed = jtextfield3;
                super();
            }
            });
            JButton jbutton1 = new JButton("Cancel");
            jbutton1.setMnemonic('C');
            jbutton1.addActionListener(new ActionListener() {

                final GUI.ActionGenerateNetwork this$1;

                public void actionPerformed(ActionEvent actionevent1) {
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionGenerateNetwork.this;
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
            container.add(jlabel, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfNrOfNodes, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jlabel1, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfNrOfArcs, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jlabel2, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfCardinality, gridbagconstraints);
            gridbagconstraints.gridwidth = -1;
            container.add(jlabel3, gridbagconstraints);
            gridbagconstraints.gridwidth = 0;
            container.add(jTfSeed, gridbagconstraints);
            gridbagconstraints.fill = 2;
            dlg.add(container, gridbagconstraints);
            dlg.add(jbutton);
            gridbagconstraints.gridwidth = 0;
            dlg.add(jbutton1);
        }
        dlg.setLocation(100, 100);
        dlg.setVisible(true);
        dlg.setSize(dlg.getPreferredSize());
        dlg.setVisible(false);
        dlg.setVisible(true);
        dlg.repaint();
    }

    public _cls2.this._cls1() {
        this$0 = GUI.this;
        super(GUI.this, "Generate Network", "Generate Random Bayesian Network", "generate.network", "ctrl N");
        m_nNrOfNodes = 10;
        m_nNrOfArcs = 15;
        m_nCardinality = 2;
        m_nSeed = 123;
        dlg = null;
    }
}
