// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Action;
import weka.core.FastVector;

// Referenced classes of package weka.classifiers.bayes.net:
//            GUI, EditableBayesNet

class m_selected {

    FastVector m_selected;
    final GUI this$0;

    public FastVector getSelected() {
        return m_selected;
    }

    void updateGUI() {
        if (m_selected.size() > 0) {
            a_cutnode.setEnabled(true);
            a_copynode.setEnabled(true);
        } else {
            a_cutnode.setEnabled(false);
            a_copynode.setEnabled(false);
        }
        if (m_selected.size() > 1) {
            a_alignleft.setEnabled(true);
            a_alignright.setEnabled(true);
            a_aligntop.setEnabled(true);
            a_alignbottom.setEnabled(true);
            a_centerhorizontal.setEnabled(true);
            a_centervertical.setEnabled(true);
            a_spacehorizontal.setEnabled(true);
            a_spacevertical.setEnabled(true);
        } else {
            a_alignleft.setEnabled(false);
            a_alignright.setEnabled(false);
            a_aligntop.setEnabled(false);
            a_alignbottom.setEnabled(false);
            a_centerhorizontal.setEnabled(false);
            a_centervertical.setEnabled(false);
            a_spacehorizontal.setEnabled(false);
            a_spacevertical.setEnabled(false);
        }
    }

    public void addToSelection(int i) {
        for (int j = 0; j < m_selected.size(); j++) {
            if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                return;
            }
        }

        m_selected.addElement(Integer.valueOf(i));
        updateGUI();
    }

    public void addToSelection(int ai[]) {
        for (int i = 0; i < ai.length; i++) {
            addToSelection(ai[i]);
        }

        updateGUI();
    }

    public void addToSelection(Rectangle rectangle) {
        for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
            if (contains(rectangle, i)) {
                addToSelection(i);
            }
        }

    }

    public void selectAll() {
        m_selected.removeAllElements();
        for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
            m_selected.addElement(Integer.valueOf(i));
        }

        updateGUI();
    }

    boolean contains(Rectangle rectangle, int i) {
        return rectangle.intersects((double)m_BayesNet.getPositionX(i) * GUI.access$000(GUI.this), (double)m_BayesNet.getPositionY(i) * GUI.access$000(GUI.this), (double)GUI.access$100(GUI.this) * GUI.access$000(GUI.this), (double)GUI.access$200(GUI.this) * GUI.access$000(GUI.this));
    }

    public void removeFromSelection(int i) {
        for (int j = 0; j < m_selected.size(); j++) {
            if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                m_selected.removeElementAt(j);
            }
        }

        updateGUI();
    }

    public void toggleSelection(int i) {
        for (int j = 0; j < m_selected.size(); j++) {
            if (i == ((Integer)m_selected.elementAt(j)).intValue()) {
                m_selected.removeElementAt(j);
                updateGUI();
                return;
            }
        }

        addToSelection(i);
    }

    public void toggleSelection(Rectangle rectangle) {
        for (int i = 0; i < m_BayesNet.getNrOfNodes(); i++) {
            if (contains(rectangle, i)) {
                toggleSelection(i);
            }
        }

    }

    public void clear() {
        m_selected.removeAllElements();
        updateGUI();
    }

    public void draw(Graphics g) {
        if (m_selected.size() == 0) {
            return;
        }
        for (int i = 0; i < m_selected.size(); i++) {
            int j = ((Integer)m_selected.elementAt(i)).intValue();
            int k = m_BayesNet.getPositionX(j);
            int l = m_BayesNet.getPositionY(j);
            g.setColor(Color.BLACK);
            int i1 = (k + GUI.access$100(GUI.this)) - GUI.access$300(GUI.this) - (GUI.access$100(GUI.this) - GUI.access$300(GUI.this)) / 2;
            int j1 = l;
            byte byte0 = 5;
            g.fillRect(i1, j1, byte0, byte0);
            g.fillRect(i1, j1 + GUI.access$200(GUI.this), byte0, byte0);
            g.fillRect(i1 + GUI.access$300(GUI.this), j1, byte0, byte0);
            g.fillRect(i1 + GUI.access$300(GUI.this), j1 + GUI.access$200(GUI.this), byte0, byte0);
        }

    }

    public et() {
        this$0 = GUI.this;
        super();
        m_selected = new FastVector();
    }
}
