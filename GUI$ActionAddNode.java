// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class m_X extends m_X {

    private static final long serialVersionUID = 0xfff8c19f40994e61L;
    JDialog dlg;
    JTextField jTfName;
    JTextField jTfCard;
    int m_X;
    int m_Y;
    final GUI this$0;

    public void addNode(int i, int j) {
        m_X = i;
        m_Y = j;
        addNode();
    }

    void addNode() {
        if (dlg == null) {
            dlg = new JDialog();
            dlg.setTitle("Add node");
            JLabel jlabel = new JLabel("Name");
            jTfName.setHorizontalAlignment(0);
            JLabel jlabel1 = new JLabel("Cardinality");
            jTfCard.setHorizontalAlignment(0);
            jTfCard.setText("2");
            JButton jbutton = new JButton("Cancel");
            jbutton.setMnemonic('C');
            jbutton.addActionListener(new ActionListener() {

                final GUI.ActionAddNode this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionAddNode.this;
                super();
            }
            });
            JButton jbutton1 = new JButton("Ok");
            jbutton1.setMnemonic('O');
            jbutton1.addActionListener(new ActionListener() {

                final GUI.ActionAddNode this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    String s = jTfName.getText();
                    if (s.length() <= 0) {
                        JOptionPane.showMessageDialog(null, "Name should have at least one character");
                        return;
                    }
                    int i = (new Integer(jTfCard.getText())).intValue();
                    if (i <= 1) {
                        JOptionPane.showMessageDialog(null, "Cardinality should be larger than 1");
                        return;
                    }
                    try {
                        if (m_X < 0x7fffffff) {
                            m_BayesNet.addNode(s, i, m_X, m_Y);
                        } else {
                            m_BayesNet.addNode(s, i);
                        }
                        m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                        a_undo.setEnabled(true);
                        a_redo.setEnabled(false);
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    repaint();
                    dlg.setVisible(false);
                }

             {
                this$1 = GUI.ActionAddNode.this;
                super();
            }
            });
            dlg.setLayout(new GridLayout(3, 2, 10, 10));
            dlg.add(jlabel);
            dlg.add(jTfName);
            dlg.add(jlabel1);
            dlg.add(jTfCard);
            dlg.add(jbutton1);
            dlg.add(jbutton);
            dlg.setSize(dlg.getPreferredSize());
        }
        jTfName.setText((new StringBuilder()).append("Node").append(m_BayesNet.getNrOfNodes() + 1).toString());
        dlg.setVisible(true);
    }

    public void actionPerformed(ActionEvent actionevent) {
        m_X = 0x7fffffff;
        addNode();
    }

    public _cls2.this._cls1() {
        this$0 = GUI.this;
        super(GUI.this, "Add Node", "Add Node", "addnode", "");
        dlg = null;
        jTfName = new JTextField(20);
        jTfCard = new JTextField(3);
        m_X = 0x7fffffff;
    }
}
