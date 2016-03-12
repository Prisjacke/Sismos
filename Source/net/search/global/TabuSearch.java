// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            HillClimber

public class TabuSearch extends HillClimber
    implements TechnicalInformationHandler {

    static final long serialVersionUID = 0x10547f869b329b24L;
    int m_nRuns;
    int m_nTabuList;
    HillClimber.Operation m_oTabuList[];

    public TabuSearch() {
        m_nRuns = 10;
        m_nTabuList = 5;
        m_oTabuList = null;
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation technicalinformation = new TechnicalInformation(weka.core.TechnicalInformation.Type.PHDTHESIS);
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.AUTHOR, "R.R. Bouckaert");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.YEAR, "1995");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.TITLE, "Bayesian Belief Networks: from Construction to Inference");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.INSTITUTION, "University of Utrecht");
        technicalinformation.setValue(weka.core.TechnicalInformation.Field.ADDRESS, "Utrecht, Netherlands");
        return technicalinformation;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        m_oTabuList = new HillClimber.Operation[m_nTabuList];
        int i = 0;
        double d1 = calcScore(bayesnet);
        double d = d1;
        BayesNet bayesnet1 = new BayesNet();
        bayesnet1.m_Instances = instances;
        bayesnet1.initStructure();
        copyParentSets(bayesnet1, bayesnet);
        for (int j = 0; j < m_nRuns; j++) {
            HillClimber.Operation operation = getOptimalOperation(bayesnet, instances);
            performOperation(bayesnet, instances, operation);
            if (operation == null) {
                throw new Exception("Panic: could not find any step to make. Tabu list too long?");
            }
            m_oTabuList[i] = operation;
            i = (i + 1) % m_nTabuList;
            d1 += operation.m_fScore;
            if (d1 > d) {
                d = d1;
                copyParentSets(bayesnet1, bayesnet);
            }
            if (bayesnet.getDebug()) {
                printTabuList();
            }
        }

        copyParentSets(bayesnet, bayesnet1);
        bayesnet1 = null;
    }

    void copyParentSets(BayesNet bayesnet, BayesNet bayesnet1) {
        int i = bayesnet1.getNrOfNodes();
        for (int j = 0; j < i; j++) {
            bayesnet.getParentSet(j).copy(bayesnet1.getParentSet(j));
        }

    }

    boolean isNotTabu(HillClimber.Operation operation) {
        for (int i = 0; i < m_nTabuList; i++) {
            if (operation.equals(m_oTabuList[i])) {
                return false;
            }
        }

        return true;
    }

    void printTabuList() {
        for (int i = 0; i < m_nTabuList; i++) {
            HillClimber.Operation operation = m_oTabuList[i];
            if (operation == null) {
                continue;
            }
            if (operation.m_nOperation == 0) {
                System.out.print(" +(");
            } else {
                System.out.print(" -(");
            }
            System.out.print((new StringBuilder()).append(operation.m_nTail).append("->").append(operation.m_nHead).append(")").toString());
        }

        System.out.println();
    }

    public int getRuns() {
        return m_nRuns;
    }

    public void setRuns(int i) {
        m_nRuns = i;
    }

    public int getTabuList() {
        return m_nTabuList;
    }

    public void setTabuList(int i) {
        m_nTabuList = i;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(4);
        vector.addElement(new Option("\tTabu list length", "L", 1, "-L <integer>"));
        vector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
        vector.addElement(new Option("\tMaximum number of parents", "P", 1, "-P <nr of parents>"));
        vector.addElement(new Option("\tUse arc reversal operation.\n\t(default false)", "R", 0, "-R"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('L', as);
        if (s.length() != 0) {
            setTabuList(Integer.parseInt(s));
        }
        String s1 = Utils.getOption('U', as);
        if (s1.length() != 0) {
            setRuns(Integer.parseInt(s1));
        }
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[7 + as.length];
        int i = 0;
        as1[i++] = "-L";
        as1[i++] = (new StringBuilder()).append("").append(getTabuList()).toString();
        as1[i++] = "-U";
        as1[i++] = (new StringBuilder()).append("").append(getRuns()).toString();
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public String globalInfo() {
        return (new StringBuilder()).append("This Bayes Network learning algorithm uses tabu search for finding a well scoring Bayes network structure. Tabu search is hill climbing till an optimum is reached. The following step is the least worst possible step. The last X steps are kept in a list and none of the steps in this so called tabu list is considered in taking the next step. The best network found in this traversal is returned.\n\nFor more information see:\n\n").append(getTechnicalInformation().toString()).toString();
    }

    public String runsTipText() {
        return "Sets the number of steps to be performed.";
    }

    public String tabuListTipText() {
        return "Sets the length of the tabu list.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.5 $");
    }
}
