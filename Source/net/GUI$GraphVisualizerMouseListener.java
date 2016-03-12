// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import weka.core.FastVector;
import weka.core.SerializedObject;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet, MarginCalculator

private class m_nPosY extends MouseAdapter {

    int m_nPosX;
    int m_nPosY;
    final GUI this$0;

    public void mouseClicked(MouseEvent mouseevent) {
        Rectangle rectangle = new Rectangle(0, 0, (int)((double)GUI.access$100(GUI.this) * GUI.access$000(GUI.this)), (int)((double)GUI.access$200(GUI.this) * GUI.access$000(GUI.this)));
        int i = mouseevent.getX();
        int j = mouseevent.getY();
        for (int k = 0; k < m_BayesNet.getNrOfNodes(); k++) {
            rectangle.x = (int)((double)m_BayesNet.getPositionX(k) * GUI.access$000(GUI.this));
            rectangle.y = (int)((double)m_BayesNet.getPositionY(k) * GUI.access$000(GUI.this));
            if (rectangle.contains(i, j)) {
                m_nCurrentNode = k;
                if (mouseevent.getButton() == 3) {
                    handleRightNodeClick(mouseevent);
                }
                if (mouseevent.getButton() == 1) {
                    if ((mouseevent.getModifiersEx() & 0x80) != 0) {
                        m_Selection.handleRightNodeClick(m_nCurrentNode);
                    } else
                    if ((mouseevent.getModifiersEx() & 0x40) != 0) {
                        m_Selection.handleRightNodeClick(m_nCurrentNode);
                    } else {
                        m_Selection.handleRightNodeClick();
                        m_Selection.handleRightNodeClick(m_nCurrentNode);
                    }
                    repaint();
                }
                return;
            }
        }

        if (mouseevent.getButton() == 3) {
            handleRightClick(mouseevent, (int)((double)i / GUI.access$000(GUI.this)), (int)((double)j / GUI.access$000(GUI.this)));
        }
    }

    public void mouseReleased(MouseEvent mouseevent) {
        if (m_nSelectedRect != null) {
            if ((mouseevent.getModifiersEx() & 0x80) != 0) {
                m_Selection._mth0(m_nSelectedRect);
            } else
            if ((mouseevent.getModifiersEx() & 0x40) != 0) {
                m_Selection._mth0(m_nSelectedRect);
            } else {
                m_Selection._mth0();
                m_Selection._mth0(m_nSelectedRect);
            }
            m_nSelectedRect = null;
            repaint();
        }
    }

    void handleRightClick(MouseEvent mouseevent, int i, int j) {
        ActionListener actionlistener = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                if (actionevent.getActionCommand().equals("Add node")) {
                    a_addnode.addNode(m_nPosX, m_nPosY);
                    return;
                } else {
                    repaint();
                    return;
                }
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        JPopupMenu jpopupmenu = new JPopupMenu("Choose a value");
        JMenuItem jmenuitem = new JMenuItem("Add node");
        jmenuitem.addActionListener(actionlistener);
        jpopupmenu.add(jmenuitem);
        FastVector fastvector = m_Selection.m_nPosY();
        JMenu jmenu = new JMenu("Add parent");
        jpopupmenu.add(jmenu);
        if (fastvector.size() == 0) {
            jmenu.setEnabled(false);
        } else {
            int k = m_BayesNet.getNrOfNodes();
            boolean aflag[] = new boolean[k];
            for (int l = 0; l < fastvector.size(); l++) {
                aflag[((Integer)fastvector.elementAt(l)).intValue()] = true;
            }

            for (int i1 = 0; i1 < k; i1++) {
                for (int k1 = 0; k1 < k; k1++) {
                    for (int j2 = 0; j2 < m_BayesNet.getNrOfParents(k1); j2++) {
                        if (aflag[m_BayesNet.getParent(k1, j2)]) {
                            aflag[k1] = true;
                        }
                    }

                }

            }

            for (int j1 = 0; j1 < fastvector.size(); j1++) {
                int l1 = ((Integer)fastvector.elementAt(j1)).intValue();
                for (int k2 = 0; k2 < m_BayesNet.getNrOfParents(l1); k2++) {
                    aflag[m_BayesNet.getParent(l1, k2)] = true;
                }

            }

            ActionListener actionlistener1 = new ActionListener() {

                final GUI.GraphVisualizerMouseListener this$1;

                public void actionPerformed(ActionEvent actionevent) {
                    try {
                        m_BayesNet.addArc(actionevent.getActionCommand(), m_Selection.getSelected());
                        m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                        updateStatus();
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
            };
            int i2 = 0;
            for (int l2 = 0; l2 < k; l2++) {
                if (!aflag[l2]) {
                    JMenuItem jmenuitem1 = new JMenuItem(m_BayesNet.getNodeName(l2));
                    jmenuitem1.addActionListener(actionlistener1);
                    jmenu.add(jmenuitem1);
                    i2++;
                }
            }

            if (i2 == 0) {
                jmenu.setEnabled(false);
            }
        }
        m_nPosX = i;
        m_nPosY = j;
        jpopupmenu.setLocation(mouseevent.getX(), mouseevent.getY());
        jpopupmenu.show(m_GraphPanel, mouseevent.getX(), mouseevent.getY());
    }

    void handleRightNodeClick(MouseEvent mouseevent) {
        m_Selection.m_nPosY();
        repaint();
        ActionListener actionlistener = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                renameValue(m_nCurrentNode, actionevent.getActionCommand());
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener1 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                delValue(m_nCurrentNode, actionevent.getActionCommand());
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener2 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                try {
                    m_BayesNet.addArc(actionevent.getActionCommand(), m_BayesNet.getNodeName(m_nCurrentNode));
                    m_jStatusBar.setText(m_BayesNet.lastActionMsg());
                    updateStatus();
                }
                catch (Exception exception1) {
                    exception1.printStackTrace();
                }
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener3 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                deleteArc(m_nCurrentNode, actionevent.getActionCommand());
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener4 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                deleteArc(actionevent.getActionCommand(), m_nCurrentNode);
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener5 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

            public void actionPerformed(ActionEvent actionevent) {
                try {
                    String as1[] = m_BayesNet.getValues(m_nCurrentNode);
                    int i3;
                    for (i3 = 0; i3 < as1.length && !as1[i3].equals(actionevent.getActionCommand()); i3++) { }
                    if (i3 == as1.length) {
                        i3 = -1;
                    }
                    if (i3 < as1.length) {
                        m_jStatusBar.setText((new StringBuilder()).append("Set evidence for ").append(m_BayesNet.getNodeName(m_nCurrentNode)).toString());
                        if (m_BayesNet.getEvidence(m_nCurrentNode) < 0 && i3 >= 0) {
                            m_BayesNet.setEvidence(m_nCurrentNode, i3);
                            m_marginCalculatorWithEvidence.setEvidence(m_nCurrentNode, i3);
                        } else {
                            m_BayesNet.setEvidence(m_nCurrentNode, i3);
                            SerializedObject serializedobject = new SerializedObject(m_marginCalculator);
                            m_marginCalculatorWithEvidence = (MarginCalculator)serializedobject.getObject();
                            for (int k3 = 0; k3 < m_BayesNet.getNrOfNodes(); k3++) {
                                if (m_BayesNet.getEvidence(k3) >= 0) {
                                    m_marginCalculatorWithEvidence.setEvidence(k3, m_BayesNet.getEvidence(k3));
                                }
                            }

                        }
                        for (int j3 = 0; j3 < m_BayesNet.getNrOfNodes(); j3++) {
                            m_BayesNet.setMargin(j3, m_marginCalculatorWithEvidence.getMargin(j3));
                        }

                    }
                }
                catch (Exception exception1) {
                    exception1.printStackTrace();
                }
                repaint();
            }

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        ActionListener actionlistener6 = new ActionListener() {

            final GUI.GraphVisualizerMouseListener this$1;

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

             {
                this$1 = GUI.GraphVisualizerMouseListener.this;
                super();
            }
        };
        try {
            JPopupMenu jpopupmenu = new JPopupMenu("Choose a value");
            JMenu jmenu = new JMenu("Set evidence");
            String as[] = m_BayesNet.getValues(m_nCurrentNode);
            for (int i = 0; i < as.length; i++) {
                JMenuItem jmenuitem5 = new JMenuItem(as[i]);
                jmenuitem5.addActionListener(actionlistener5);
                jmenu.add(jmenuitem5);
            }

            jmenu.addSeparator();
            JMenuItem jmenuitem = new JMenuItem("Clear");
            jmenuitem.addActionListener(actionlistener5);
            jmenu.add(jmenuitem);
            jpopupmenu.add(jmenu);
            jmenu.setEnabled(m_bViewMargins);
            jpopupmenu.addSeparator();
            JMenuItem jmenuitem6 = new JMenuItem("Rename");
            jmenuitem6.addActionListener(actionlistener6);
            jpopupmenu.add(jmenuitem6);
            JMenuItem jmenuitem7 = new JMenuItem("Delete node");
            jmenuitem7.addActionListener(actionlistener6);
            jpopupmenu.add(jmenuitem7);
            JMenuItem jmenuitem8 = new JMenuItem("Edit CPT");
            jmenuitem8.addActionListener(actionlistener6);
            jpopupmenu.add(jmenuitem8);
            jpopupmenu.addSeparator();
            JMenu jmenu1 = new JMenu("Add parent");
            jpopupmenu.add(jmenu1);
            int j = m_BayesNet.getNrOfNodes();
            boolean aflag[] = new boolean[j];
            aflag[m_nCurrentNode] = true;
            for (int k = 0; k < j; k++) {
                for (int j1 = 0; j1 < j; j1++) {
                    for (int l1 = 0; l1 < m_BayesNet.getNrOfParents(j1); l1++) {
                        if (aflag[m_BayesNet.getParent(j1, l1)]) {
                            aflag[j1] = true;
                        }
                    }

                }

            }

            for (int l = 0; l < m_BayesNet.getNrOfParents(m_nCurrentNode); l++) {
                aflag[m_BayesNet.getParent(m_nCurrentNode, l)] = true;
            }

            int i1 = 0;
            for (int k1 = 0; k1 < j; k1++) {
                if (!aflag[k1]) {
                    JMenuItem jmenuitem1 = new JMenuItem(m_BayesNet.getNodeName(k1));
                    jmenuitem1.addActionListener(actionlistener2);
                    jmenu1.add(jmenuitem1);
                    i1++;
                }
            }

            if (i1 == 0) {
                jmenu1.setEnabled(false);
            }
            JMenu jmenu2 = new JMenu("Delete parent");
            jpopupmenu.add(jmenu2);
            if (m_BayesNet.getNrOfParents(m_nCurrentNode) == 0) {
                jmenu2.setEnabled(false);
            }
            for (int i2 = 0; i2 < m_BayesNet.getNrOfParents(m_nCurrentNode); i2++) {
                JMenuItem jmenuitem2 = new JMenuItem(m_BayesNet.getNodeName(m_BayesNet.getParent(m_nCurrentNode, i2)));
                jmenuitem2.addActionListener(actionlistener3);
                jmenu2.add(jmenuitem2);
            }

            JMenu jmenu3 = new JMenu("Delete child");
            jpopupmenu.add(jmenu3);
            FastVector fastvector = m_BayesNet.getChildren(m_nCurrentNode);
            if (fastvector.size() == 0) {
                jmenu3.setEnabled(false);
            }
            for (int j2 = 0; j2 < fastvector.size(); j2++) {
                JMenuItem jmenuitem3 = new JMenuItem(m_BayesNet.getNodeName(((Integer)fastvector.elementAt(j2)).intValue()));
                jmenuitem3.addActionListener(actionlistener4);
                jmenu3.add(jmenuitem3);
            }

            jpopupmenu.addSeparator();
            JMenuItem jmenuitem9 = new JMenuItem("Add value");
            jmenuitem9.addActionListener(actionlistener6);
            jpopupmenu.add(jmenuitem9);
            JMenu jmenu4 = new JMenu("Rename value");
            jpopupmenu.add(jmenu4);
            for (int k2 = 0; k2 < as.length; k2++) {
                JMenuItem jmenuitem4 = new JMenuItem(as[k2]);
                jmenuitem4.addActionListener(actionlistener);
                jmenu4.add(jmenuitem4);
            }

            JMenu jmenu5 = new JMenu("Delete value");
            jpopupmenu.add(jmenu5);
            if (m_BayesNet.getCardinality(m_nCurrentNode) <= 2) {
                jmenu5.setEnabled(false);
            }
            for (int l2 = 0; l2 < as.length; l2++) {
                JMenuItem jmenuitem10 = new JMenuItem(as[l2]);
                jmenuitem10.addActionListener(actionlistener1);
                jmenu5.add(jmenuitem10);
            }

            jpopupmenu.setLocation(mouseevent.getX(), mouseevent.getY());
            jpopupmenu.show(m_GraphPanel, mouseevent.getX(), mouseevent.getY());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private _cls9.this._cls1() {
        this$0 = GUI.this;
        super();
        m_nPosX = 0;
        m_nPosY = 0;
    }


    // Unreferenced inner class weka/classifiers/bayes/net/GUI$1

/* anonymous class */
    class GUI._cls1
        implements ActionListener {

        final GUI this$0;

        public void actionPerformed(ActionEvent actionevent) {
            JTextField jtextfield = (JTextField)actionevent.getSource();
            try {
                int i = -1;
                i = jtextfield.getText().indexOf('%');
                if (i == -1) {
                    i = Integer.parseInt(jtextfield.getText());
                } else {
                    i = Integer.parseInt(jtextfield.getText().substring(0, i));
                }
                if (i <= 999) {
                    GUI.access$002(GUI.this, (double)i / 100D);
                }
                jtextfield.setText((new StringBuilder()).append((int)(GUI.access$000(GUI.this) * 100D)).append("%").toString());
                if (GUI.access$000(GUI.this) > 0.10000000000000001D) {
                    if (!a_zoomout.isEnabled()) {
                        a_zoomout.setEnabled(true);
                    }
                } else {
                    a_zoomout.setEnabled(false);
                }
                if (GUI.access$000(GUI.this) < 9.9900000000000002D) {
                    if (!a_zoomin.isEnabled()) {
                        a_zoomin.setEnabled(true);
                    }
                } else {
                    a_zoomin.setEnabled(false);
                }
                setAppropriateSize();
                m_GraphPanel.repaint();
                m_GraphPanel.invalidate();
                m_jScrollPane.revalidate();
            }
            catch (NumberFormatException numberformatexception) {
                JOptionPane.showMessageDialog(getParent(), "Invalid integer entered for zoom.", "Error", 0);
                jtextfield.setText((new StringBuilder()).append(GUI.access$000(GUI.this) * 100D).append("%").toString());
            }
        }

             {
                this$0 = GUI.this;
                super();
            }
    }

}
