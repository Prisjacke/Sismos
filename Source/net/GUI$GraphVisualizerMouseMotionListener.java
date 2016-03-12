// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

private class m_nLastNode extends MouseMotionAdapter {

    int m_nLastNode;
    int m_nPosX;
    int m_nPosY;
    final GUI this$0;

    int getGraphNode(MouseEvent mouseevent) {
        m_nPosX = m_nPosY = 0;
        Rectangle rectangle = new Rectangle(0, 0, (int)((double)GUI.access$100(GUI.this) * GUI.access$000(GUI.this)), (int)((double)GUI.access$200(GUI.this) * GUI.access$000(GUI.this)));
        m_nPosX += mouseevent.getX();
        m_nPosY += mouseevent.getY();
        for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
            rectangle.x = (int)((double)m_BayesNet.getPositionX(i) * GUI.access$000(GUI.this));
            rectangle.y = (int)((double)m_BayesNet.getPositionY(i) * GUI.access$000(GUI.this));
            if (rectangle.contains(m_nPosX, m_nPosY)) {
                return i;
            }
        }

        return -1;
    }

    public void mouseDragged(MouseEvent mouseevent) {
        if (m_nSelectedRect != null) {
            m_nSelectedRect.width = mouseevent.getPoint().x - m_nSelectedRect.x;
            m_nSelectedRect.height = mouseevent.getPoint().y - m_nSelectedRect.y;
            repaint();
            return;
        }
        int i = getGraphNode(mouseevent);
        if (i >= 0) {
            if (m_Selection.getGraphNode().size() > 0) {
                if (m_Selection.getGraphNode().contains(Integer.valueOf(i))) {
                    m_BayesNet.setPosition(i, (int)((double)m_nPosX / GUI.access$000(GUI.this) - (double)(GUI.access$100(GUI.this) / 2)), (int)((double)m_nPosY / GUI.access$000(GUI.this) - (double)(GUI.access$200(GUI.this) / 2)), m_Selection._mth0());
                } else {
                    m_Selection._mth0();
                    m_BayesNet.setPosition(i, (int)((double)m_nPosX / GUI.access$000(GUI.this) - (double)(GUI.access$100(GUI.this) / 2)), (int)((double)m_nPosY / GUI.access$000(GUI.this) - (double)(GUI.access$200(GUI.this) / 2)));
                }
                repaint();
            } else {
                m_BayesNet.setPosition(i, (int)((double)m_nPosX / GUI.access$000(GUI.this) - (double)(GUI.access$100(GUI.this) / 2)), (int)((double)m_nPosY / GUI.access$000(GUI.this) - (double)(GUI.access$200(GUI.this) / 2)));
            }
            m_jStatusBar.setText(m_BayesNet.lastActionMsg());
            a_undo.setEnabled(true);
            a_redo.setEnabled(false);
            m_GraphPanel._mth0(i);
        }
        if (i < 0) {
            if (m_nLastNode >= 0) {
                m_GraphPanel.m_nLastNode();
                m_nLastNode = -1;
            } else {
                m_nSelectedRect = new Rectangle(mouseevent.getPoint().x, mouseevent.getPoint().y, 1, 1);
                m_GraphPanel.m_nLastNode();
            }
        }
    }

    public void mouseMoved(MouseEvent mouseevent) {
        int i = getGraphNode(mouseevent);
        if (i >= 0 && i != m_nLastNode) {
            m_GraphPanel.m_nLastNode(i);
            if (m_nLastNode >= 0) {
                m_GraphPanel.m_nLastNode(m_nLastNode);
            }
            m_nLastNode = i;
        }
        if (i < 0 && m_nLastNode >= 0) {
            m_GraphPanel.m_nLastNode();
            m_nLastNode = -1;
        }
    }

    private () {
        this$0 = GUI.this;
        super();
        m_nLastNode = -1;
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
