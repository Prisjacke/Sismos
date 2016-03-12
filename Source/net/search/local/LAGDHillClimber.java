// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

// Referenced classes of package weka.classifiers.bayes.net.search.local:
//            HillClimber

public class LAGDHillClimber extends HillClimber {

    static final long serialVersionUID = 0x64297d2a86a7f9d8L;
    int m_nNrOfLookAheadSteps;
    int m_nNrOfGoodOperations;

    public LAGDHillClimber() {
        m_nNrOfLookAheadSteps = 2;
        m_nNrOfGoodOperations = 5;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        int i = m_nNrOfLookAheadSteps;
        int j = m_nNrOfGoodOperations;
        lookAheadInGoodDirectionsSearch(bayesnet, instances, i, j);
    }

    protected void lookAheadInGoodDirectionsSearch(BayesNet bayesnet, Instances instances, int i, int j) throws Exception {
        System.out.println("Initializing Cache");
        initCache(bayesnet, instances);
        for (; i > 1; i--) {
            System.out.println((new StringBuilder()).append("Look Ahead Depth: ").append(i).toString());
            boolean flag = true;
            double d = 0.0D;
            HillClimber.Operation aoperation[] = new HillClimber.Operation[i];
            aoperation = getOptimalOperations(bayesnet, instances, i, j);
            for (int k = 0; k < i; k++) {
                if (aoperation[k] == null) {
                    flag = false;
                } else {
                    d += aoperation[k].m_fDeltaScore;
                }
            }

            while (flag && d > 0.0D)  {
                System.out.println("Next Iteration..........................");
                for (int l = 0; l < i; l++) {
                    performOperation(bayesnet, instances, aoperation[l]);
                }

                aoperation = getOptimalOperations(bayesnet, instances, i, j);
                d = 0.0D;
                int i1 = 0;
                while (i1 < i)  {
                    if (aoperation[i1] != null) {
                        System.out.println((new StringBuilder()).append(aoperation[i1].m_nOperation).append(" ").append(aoperation[i1].m_nHead).append(" ").append(aoperation[i1].m_nTail).toString());
                        d += aoperation[i1].m_fDeltaScore;
                    } else {
                        flag = false;
                    }
                    System.out.println((new StringBuilder()).append("DeltaScore: ").append(d).toString());
                    i1++;
                }
            }
        }

        for (HillClimber.Operation operation = getOptimalOperation(bayesnet, instances); operation != null && operation.m_fDeltaScore > 0.0D; operation = getOptimalOperation(bayesnet, instances)) {
            performOperation(bayesnet, instances, operation);
            System.out.println("Performing last greedy steps");
        }

        m_Cache = null;
    }

    protected HillClimber.Operation getAntiOperation(HillClimber.Operation operation) throws Exception {
        if (operation.m_nOperation == 0) {
            return new HillClimber.Operation(this, operation.m_nTail, operation.m_nHead, 1);
        }
        if (operation.m_nOperation == 1) {
            return new HillClimber.Operation(this, operation.m_nTail, operation.m_nHead, 0);
        } else {
            return new HillClimber.Operation(this, operation.m_nHead, operation.m_nTail, 2);
        }
    }

    protected HillClimber.Operation[] getGoodOperations(BayesNet bayesnet, Instances instances, int i) throws Exception {
        HillClimber.Operation aoperation[] = new HillClimber.Operation[i];
        for (int j = 0; j < i; j++) {
            aoperation[j] = getOptimalOperation(bayesnet, instances);
            if (aoperation[j] != null) {
                m_Cache.put(aoperation[j], -1E+100D);
            } else {
                j = i;
            }
        }

        for (int k = 0; k < i; k++) {
            if (aoperation[k] != null) {
                if (aoperation[k].m_nOperation != 2) {
                    m_Cache.put(aoperation[k], aoperation[k].m_fDeltaScore);
                } else {
                    m_Cache.put(aoperation[k], aoperation[k].m_fDeltaScore - m_Cache.m_fDeltaScoreAdd[aoperation[k].m_nHead][aoperation[k].m_nTail]);
                }
            } else {
                k = i;
            }
        }

        return aoperation;
    }

    protected HillClimber.Operation[] getOptimalOperations(BayesNet bayesnet, Instances instances, int i, int j) throws Exception {
        if (i == 1) {
            HillClimber.Operation aoperation[] = new HillClimber.Operation[1];
            aoperation[0] = getOptimalOperation(bayesnet, instances);
            return aoperation;
        }
        double d = 0.0D;
        double d1 = 0.0D;
        HillClimber.Operation aoperation1[] = new HillClimber.Operation[i];
        HillClimber.Operation aoperation2[] = new HillClimber.Operation[j];
        HillClimber.Operation aoperation3[] = new HillClimber.Operation[i - 1];
        aoperation2 = getGoodOperations(bayesnet, instances, j);
        for (int k = 0; k < j; k++) {
            if (aoperation2[k] != null) {
                performOperation(bayesnet, instances, aoperation2[k]);
                HillClimber.Operation aoperation4[] = getOptimalOperations(bayesnet, instances, i - 1, j);
                double d2 = aoperation2[k].m_fDeltaScore;
                for (int l = 0; l < i - 1; l++) {
                    if (aoperation4[l] != null) {
                        d2 += aoperation4[l].m_fDeltaScore;
                    }
                }

                performOperation(bayesnet, instances, getAntiOperation(aoperation2[k]));
                if (d2 <= d) {
                    continue;
                }
                d = d2;
                aoperation1[0] = aoperation2[k];
                for (int i1 = 1; i1 < i; i1++) {
                    aoperation1[i1] = aoperation4[i1 - 1];
                }

            } else {
                k = j;
            }
        }

        return aoperation1;
    }

    public void setMaxNrOfParents(int i) {
        m_nMaxNrOfParents = i;
    }

    public int getMaxNrOfParents() {
        return m_nMaxNrOfParents;
    }

    public void setNrOfLookAheadSteps(int i) {
        m_nNrOfLookAheadSteps = i;
    }

    public int getNrOfLookAheadSteps() {
        return m_nNrOfLookAheadSteps;
    }

    public void setNrOfGoodOperations(int i) {
        m_nNrOfGoodOperations = i;
    }

    public int getNrOfGoodOperations() {
        return m_nNrOfGoodOperations;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector();
        vector.addElement(new Option("\tLook Ahead Depth", "L", 2, "-L <nr of look ahead steps>"));
        vector.addElement(new Option("\tNr of Good Operations", "G", 5, "-G <nr of good operations>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('L', as);
        if (s.length() != 0) {
            setNrOfLookAheadSteps(Integer.parseInt(s));
        } else {
            setNrOfLookAheadSteps(2);
        }
        String s1 = Utils.getOption('G', as);
        if (s1.length() != 0) {
            setNrOfGoodOperations(Integer.parseInt(s1));
        } else {
            setNrOfGoodOperations(5);
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[9 + as.length];
        int i = 0;
        as1[i++] = "-L";
        as1[i++] = (new StringBuilder()).append("").append(m_nNrOfLookAheadSteps).toString();
        as1[i++] = "-G";
        as1[i++] = (new StringBuilder()).append("").append(m_nNrOfGoodOperations).toString();
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm uses a Look Ahead Hill Climbing algorithm called LAGD Hill Climbing. Unlike Greedy Hill Climbing it doesn't calculate a best greedy operation (adding, deleting or reversing an arc) but a sequence of nrOfLookAheadSteps operations, which leads to a network structure whose score is most likely higher in comparison to the network obtained by performing a sequence of nrOfLookAheadSteps greedy operations. The search is not restricted by an order on the variables (unlike K2). The difference with B and B2 is that this hill climber also considers arrows part of the naive Bayes structure for deletion.";
    }

    public String nrOfLookAheadStepsTipText() {
        return "Sets the Number of Look Ahead Steps. 'nrOfLookAheadSteps = 2' means that all network structures in a distance of 2 (from the current network structure) are taken into account for the decision which arcs to add, remove or reverse. 'nrOfLookAheadSteps = 1' results in Greedy Hill Climbing.";
    }

    public String nrOfGoodOperationsTipText() {
        return "Sets the Number of Good Operations per Look Ahead Step. 'nrOfGoodOperations = 5' means that for the next Look Ahead Step only the 5 best Operations (adding, deleting or reversing an arc) are taken into account for the calculation of the best sequence consisting of nrOfLookAheadSteps operations.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.7 $");
    }
}
