// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            LocalScoreSearchAlgorithm

public class HillClimber extends LocalScoreSearchAlgorithm {
    class Cache
        implements RevisionHandler {

        double m_fDeltaScoreAdd[][];
        double m_fDeltaScoreDel[][];
        final HillClimber this$0;

        public void put(Operation operation, double d) {
            if (operation.m_nOperation == 0) {
                m_fDeltaScoreAdd[operation.m_nTail][operation.m_nHead] = d;
            } else {
                m_fDeltaScoreDel[operation.m_nTail][operation.m_nHead] = d;
            }
        }

        public double get(Operation operation) {
            switch (operation.m_nOperation) {
            case 0: // '\0'
                return m_fDeltaScoreAdd[operation.m_nTail][operation.m_nHead];

            case 1: // '\001'
                return m_fDeltaScoreDel[operation.m_nTail][operation.m_nHead];

            case 2: // '\002'
                return m_fDeltaScoreDel[operation.m_nTail][operation.m_nHead] + m_fDeltaScoreAdd[operation.m_nHead][operation.m_nTail];
            }
            return 0.0D;
        }

        public String getRevision() {
            return RevisionUtils.extract("$Revision: 1.9 $");
        }

        Cache(int i) {
            this$0 = HillClimber.this;
            super();
            m_fDeltaScoreAdd = new double[i][i];
            m_fDeltaScoreDel = new double[i][i];
        }
    }

    class Operation
        implements Serializable, RevisionHandler {

        static final long serialVersionUID = 0xbc4399815214fbc9L;
        static final int OPERATION_ADD = 0;
        static final int OPERATION_DEL = 1;
        static final int OPERATION_REVERSE = 2;
        public int m_nTail;
        public int m_nHead;
        public int m_nOperation;
        public double m_fDeltaScore;
        final HillClimber this$0;

        public boolean equals(Operation operation) {
            if (operation == null) {
                return false;
            } else {
                return m_nOperation == operation.m_nOperation && m_nHead == operation.m_nHead && m_nTail == operation.m_nTail;
            }
        }

        public String getRevision() {
            return RevisionUtils.extract("$Revision: 1.9 $");
        }

        public Operation() {
            this$0 = HillClimber.this;
            super();
            m_fDeltaScore = -1E+100D;
        }

        public Operation(int i, int j, int k) {
            this$0 = HillClimber.this;
            super();
            m_fDeltaScore = -1E+100D;
            m_nHead = j;
            m_nTail = i;
            m_nOperation = k;
        }
    }


    static final long serialVersionUID = 0x3bfd9cc6866a68a3L;
    Cache m_Cache;
    boolean m_bUseArcReversal;

    public HillClimber() {
        m_Cache = null;
        m_bUseArcReversal = false;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        initCache(bayesnet, instances);
        for (Operation operation = getOptimalOperation(bayesnet, instances); operation != null && operation.m_fDeltaScore > 0.0D; operation = getOptimalOperation(bayesnet, instances)) {
            performOperation(bayesnet, instances, operation);
        }

        m_Cache = null;
    }

    void initCache(BayesNet bayesnet, Instances instances) throws Exception {
        double ad[] = new double[instances.numAttributes()];
        int i = instances.numAttributes();
        m_Cache = new Cache(i);
        for (int j = 0; j < i; j++) {
            updateCache(j, i, bayesnet.getParentSet(j));
        }

        for (int k = 0; k < i; k++) {
            ad[k] = calcNodeScore(k);
        }

        for (int l = 0; l < i; l++) {
            for (int i1 = 0; i1 < i; i1++) {
                if (l != i1) {
                    Operation operation = new Operation(i1, l, 0);
                    m_Cache.put(operation, calcScoreWithExtraParent(l, i1) - ad[l]);
                }
            }

        }

    }

    boolean isNotTabu(Operation operation) {
        return true;
    }

    Operation getOptimalOperation(BayesNet bayesnet, Instances instances) throws Exception {
        Operation operation = new Operation();
        operation = findBestArcToAdd(bayesnet, instances, operation);
        operation = findBestArcToDelete(bayesnet, instances, operation);
        if (getUseArcReversal()) {
            operation = findBestArcToReverse(bayesnet, instances, operation);
        }
        if (operation.m_fDeltaScore == -1E+100D) {
            return null;
        } else {
            return operation;
        }
    }

    void performOperation(BayesNet bayesnet, Instances instances, Operation operation) throws Exception {
        switch (operation.m_nOperation) {
        default:
            break;

        case 0: // '\0'
            applyArcAddition(bayesnet, operation.m_nHead, operation.m_nTail, instances);
            if (bayesnet.getDebug()) {
                System.out.print((new StringBuilder()).append("Add ").append(operation.m_nHead).append(" -> ").append(operation.m_nTail).toString());
            }
            break;

        case 1: // '\001'
            applyArcDeletion(bayesnet, operation.m_nHead, operation.m_nTail, instances);
            if (bayesnet.getDebug()) {
                System.out.print((new StringBuilder()).append("Del ").append(operation.m_nHead).append(" -> ").append(operation.m_nTail).toString());
            }
            break;

        case 2: // '\002'
            applyArcDeletion(bayesnet, operation.m_nHead, operation.m_nTail, instances);
            applyArcAddition(bayesnet, operation.m_nTail, operation.m_nHead, instances);
            if (bayesnet.getDebug()) {
                System.out.print((new StringBuilder()).append("Rev ").append(operation.m_nHead).append(" -> ").append(operation.m_nTail).toString());
            }
            break;
        }
    }

    void applyArcAddition(BayesNet bayesnet, int i, int j, Instances instances) {
        ParentSet parentset = bayesnet.getParentSet(i);
        parentset.addParent(j, instances);
        updateCache(i, instances.numAttributes(), parentset);
    }

    void applyArcDeletion(BayesNet bayesnet, int i, int j, Instances instances) {
        ParentSet parentset = bayesnet.getParentSet(i);
        parentset.deleteParent(j, instances);
        updateCache(i, instances.numAttributes(), parentset);
    }

    Operation findBestArcToAdd(BayesNet bayesnet, Instances instances, Operation operation) {
        int i = instances.numAttributes();
        for (int j = 0; j < i; j++) {
            if (bayesnet.getParentSet(j).getNrOfParents() >= m_nMaxNrOfParents) {
                continue;
            }
            for (int k = 0; k < i; k++) {
                if (!addArcMakesSense(bayesnet, instances, j, k)) {
                    continue;
                }
                Operation operation1 = new Operation(k, j, 0);
                if (m_Cache.get(operation1) > operation.m_fDeltaScore && isNotTabu(operation1)) {
                    operation = operation1;
                    operation.m_fDeltaScore = m_Cache.get(operation1);
                }
            }

        }

        return operation;
    }

    Operation findBestArcToDelete(BayesNet bayesnet, Instances instances, Operation operation) {
        int i = instances.numAttributes();
        for (int j = 0; j < i; j++) {
            ParentSet parentset = bayesnet.getParentSet(j);
            for (int k = 0; k < parentset.getNrOfParents(); k++) {
                Operation operation1 = new Operation(parentset.getParent(k), j, 1);
                if (m_Cache.get(operation1) > operation.m_fDeltaScore && isNotTabu(operation1)) {
                    operation = operation1;
                    operation.m_fDeltaScore = m_Cache.get(operation1);
                }
            }

        }

        return operation;
    }

    Operation findBestArcToReverse(BayesNet bayesnet, Instances instances, Operation operation) {
        int i = instances.numAttributes();
        for (int j = 0; j < i; j++) {
            ParentSet parentset = bayesnet.getParentSet(j);
            for (int k = 0; k < parentset.getNrOfParents(); k++) {
                int l = parentset.getParent(k);
                if (!reverseArcMakesSense(bayesnet, instances, j, l) || bayesnet.getParentSet(l).getNrOfParents() >= m_nMaxNrOfParents) {
                    continue;
                }
                Operation operation1 = new Operation(parentset.getParent(k), j, 2);
                if (m_Cache.get(operation1) > operation.m_fDeltaScore && isNotTabu(operation1)) {
                    operation = operation1;
                    operation.m_fDeltaScore = m_Cache.get(operation1);
                }
            }

        }

        return operation;
    }

    void updateCache(int i, int j, ParentSet parentset) {
        double d = calcNodeScore(i);
        int k = parentset.getNrOfParents();
        for (int l = 0; l < j; l++) {
            if (l == i) {
                continue;
            }
            if (!parentset.contains(l)) {
                if (k < m_nMaxNrOfParents) {
                    Operation operation = new Operation(l, i, 0);
                    m_Cache.put(operation, calcScoreWithExtraParent(i, l) - d);
                }
            } else {
                Operation operation1 = new Operation(l, i, 1);
                m_Cache.put(operation1, calcScoreWithMissingParent(i, l) - d);
            }
        }

    }

    public void setMaxNrOfParents(int i) {
        m_nMaxNrOfParents = i;
    }

    public int getMaxNrOfParents() {
        return m_nMaxNrOfParents;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(2);
        vector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
        vector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
        vector.addElement(new Option("\tInitial structure is empty (instead of Naive Bayes)", "N", 0, "-N"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        setUseArcReversal(Utils.getFlag('R', as));
        setInitAsNaiveBayes(!Utils.getFlag('N', as));
        String s = Utils.getOption('P', as);
        if (s.length() != 0) {
            setMaxNrOfParents(Integer.parseInt(s));
        } else {
            setMaxNrOfParents(0x186a0);
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[7 + as.length];
        int i = 0;
        if (getUseArcReversal()) {
            as1[i++] = "-R";
        }
        if (!getInitAsNaiveBayes()) {
            as1[i++] = "-N";
        }
        as1[i++] = "-P";
        as1[i++] = (new StringBuilder()).append("").append(m_nMaxNrOfParents).toString();
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public void setInitAsNaiveBayes(boolean flag) {
        m_bInitAsNaiveBayes = flag;
    }

    public boolean getInitAsNaiveBayes() {
        return m_bInitAsNaiveBayes;
    }

    public boolean getUseArcReversal() {
        return m_bUseArcReversal;
    }

    public void setUseArcReversal(boolean flag) {
        m_bUseArcReversal = flag;
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm uses a hill climbing algorithm adding, deleting and reversing arcs. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
    }

    public String useArcReversalTipText() {
        return "When set to true, the arc reversal operation is used in the search.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.9 $");
    }
}
