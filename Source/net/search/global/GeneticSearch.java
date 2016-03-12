// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.global;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.ParentSet;
import weka.core.*;

// Referenced classes of package weka.classifiers.bayes.net.search.global:
//            GlobalScoreSearchAlgorithm

public class GeneticSearch extends GlobalScoreSearchAlgorithm {
    class BayesNetRepresentation
        implements RevisionHandler {

        int m_nNodes;
        boolean m_bits[];
        double m_fScore;
        final GeneticSearch this$0;

        public double getScore() {
            return m_fScore;
        }

        public void randomInit() {
            do {
                m_bits = new boolean[m_nNodes * m_nNodes];
                for (int i = 0; i < m_nNodes; i++) {
                    int j;
                    do {
                        j = m_random.nextInt(m_nNodes * m_nNodes);
                    } while (isSquare(j));
                    m_bits[j] = true;
                }

            } while (hasCycles());
            calcGlobalScore();
        }

        void calcGlobalScore() {
            for (int i = 0; i < m_nNodes; i++) {
                for (ParentSet parentset = m_BayesNet.getParentSet(i); parentset.getNrOfParents() > 0; parentset.deleteLastParent(m_BayesNet.m_Instances)) { }
            }

            for (int j = 0; j < m_nNodes; j++) {
                ParentSet parentset1 = m_BayesNet.getParentSet(j);
                for (int k = 0; k < m_nNodes; k++) {
                    if (m_bits[k + j * m_nNodes]) {
                        parentset1.addParent(k, m_BayesNet.m_Instances);
                    }
                }

            }

            try {
                m_fScore = calcScore(m_BayesNet);
            }
            catch (Exception exception) { }
        }

        public boolean hasCycles() {
            boolean aflag[] = new boolean[m_nNodes];
            for (int i = 0; i < m_nNodes; i++) {
                boolean flag = false;
                for (int j = 0; !flag && j < m_nNodes; j++) {
                    if (aflag[j]) {
                        continue;
                    }
                    boolean flag1 = true;
                    for (int k = 0; k < m_nNodes; k++) {
                        if (m_bits[k + j * m_nNodes] && !aflag[k]) {
                            flag1 = false;
                        }
                    }

                    if (flag1) {
                        aflag[j] = true;
                        flag = true;
                    }
                }

                if (!flag) {
                    return true;
                }
            }

            return false;
        }

        BayesNetRepresentation copy() {
            BayesNetRepresentation bayesnetrepresentation = new BayesNetRepresentation(m_nNodes);
            bayesnetrepresentation.m_bits = new boolean[m_bits.length];
            for (int i = 0; i < m_nNodes * m_nNodes; i++) {
                bayesnetrepresentation.m_bits[i] = m_bits[i];
            }

            bayesnetrepresentation.m_fScore = m_fScore;
            return bayesnetrepresentation;
        }

        void mutate() {
            do {
                int i;
                do {
                    i = m_random.nextInt(m_nNodes * m_nNodes);
                } while (isSquare(i));
                m_bits[i] = !m_bits[i];
            } while (hasCycles());
            calcGlobalScore();
        }

        void crossOver(BayesNetRepresentation bayesnetrepresentation) {
            boolean aflag[] = new boolean[m_bits.length];
            for (int i = 0; i < m_bits.length; i++) {
                aflag[i] = m_bits[i];
            }

            int j = m_bits.length;
            do {
                for (int k = j; k < m_bits.length; k++) {
                    m_bits[k] = aflag[k];
                }

                j = m_random.nextInt(m_bits.length);
                for (int l = j; l < m_bits.length; l++) {
                    m_bits[l] = bayesnetrepresentation.m_bits[l];
                }

            } while (hasCycles());
            calcGlobalScore();
        }

        boolean isSquare(int i) {
            if (GeneticSearch.g_bIsSquare == null || GeneticSearch.g_bIsSquare.length < i) {
                GeneticSearch.g_bIsSquare = new boolean[m_nNodes * m_nNodes];
                for (int j = 0; j < m_nNodes; j++) {
                    GeneticSearch.g_bIsSquare[j * m_nNodes + j] = true;
                }

            }
            return GeneticSearch.g_bIsSquare[i];
        }

        public String getRevision() {
            return RevisionUtils.extract("$Revision: 1.5 $");
        }

        BayesNetRepresentation(int i) {
            this$0 = GeneticSearch.this;
            super();
            m_nNodes = 0;
            m_fScore = 0.0D;
            m_nNodes = i;
        }
    }


    static final long serialVersionUID = 0x3ac9e21be0d12ffbL;
    int m_nRuns;
    int m_nPopulationSize;
    int m_nDescendantPopulationSize;
    boolean m_bUseCrossOver;
    boolean m_bUseMutation;
    boolean m_bUseTournamentSelection;
    int m_nSeed;
    Random m_random;
    static boolean g_bIsSquare[];

    public GeneticSearch() {
        m_nRuns = 10;
        m_nPopulationSize = 10;
        m_nDescendantPopulationSize = 100;
        m_bUseCrossOver = true;
        m_bUseMutation = true;
        m_bUseTournamentSelection = false;
        m_nSeed = 1;
        m_random = null;
    }

    protected void search(BayesNet bayesnet, Instances instances) throws Exception {
        if (getDescendantPopulationSize() < getPopulationSize()) {
            throw new Exception("Descendant PopulationSize should be at least Population Size");
        }
        if (!getUseCrossOver() && !getUseMutation()) {
            throw new Exception("At least one of mutation or cross-over should be used");
        }
        m_random = new Random(m_nSeed);
        double d = calcScore(bayesnet);
        BayesNet bayesnet1 = new BayesNet();
        bayesnet1.m_Instances = instances;
        bayesnet1.initStructure();
        copyParentSets(bayesnet1, bayesnet);
        BayesNetRepresentation abayesnetrepresentation[] = new BayesNetRepresentation[getPopulationSize()];
        for (int i = 0; i < getPopulationSize(); i++) {
            abayesnetrepresentation[i] = new BayesNetRepresentation(instances.numAttributes());
            abayesnetrepresentation[i].randomInit();
            if (abayesnetrepresentation[i].getScore() > d) {
                copyParentSets(bayesnet1, bayesnet);
                d = abayesnetrepresentation[i].getScore();
            }
        }

        for (int j = 0; j < m_nRuns; j++) {
            BayesNetRepresentation abayesnetrepresentation1[] = new BayesNetRepresentation[getDescendantPopulationSize()];
            for (int k = 0; k < getDescendantPopulationSize(); k++) {
                abayesnetrepresentation1[k] = abayesnetrepresentation[m_random.nextInt(getPopulationSize())].copy();
                if (getUseMutation()) {
                    if (getUseCrossOver() && m_random.nextBoolean()) {
                        abayesnetrepresentation1[k].crossOver(abayesnetrepresentation[m_random.nextInt(getPopulationSize())]);
                    } else {
                        abayesnetrepresentation1[k].mutate();
                    }
                } else {
                    abayesnetrepresentation1[k].crossOver(abayesnetrepresentation[m_random.nextInt(getPopulationSize())]);
                }
                if (abayesnetrepresentation1[k].getScore() > d) {
                    copyParentSets(bayesnet1, bayesnet);
                    d = abayesnetrepresentation1[k].getScore();
                }
            }

            boolean aflag[] = new boolean[getDescendantPopulationSize()];
            for (int l = 0; l < getPopulationSize(); l++) {
                int i1 = 0;
                if (m_bUseTournamentSelection) {
                    for (i1 = m_random.nextInt(getDescendantPopulationSize()); aflag[i1]; i1 = (i1 + 1) % getDescendantPopulationSize()) { }
                    int j1;
                    for (j1 = m_random.nextInt(getDescendantPopulationSize()); aflag[j1]; j1 = (j1 + 1) % getDescendantPopulationSize()) { }
                    if (abayesnetrepresentation1[j1].getScore() > abayesnetrepresentation1[i1].getScore()) {
                        i1 = j1;
                    }
                } else {
                    for (; aflag[i1]; i1++) { }
                    double d1 = abayesnetrepresentation1[i1].getScore();
                    for (int k1 = 0; k1 < getDescendantPopulationSize(); k1++) {
                        if (!aflag[k1] && abayesnetrepresentation1[k1].getScore() > d1) {
                            d1 = abayesnetrepresentation1[k1].getScore();
                            i1 = k1;
                        }
                    }

                }
                abayesnetrepresentation[l] = abayesnetrepresentation1[i1];
                aflag[i1] = true;
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

    public int getRuns() {
        return m_nRuns;
    }

    public void setRuns(int i) {
        m_nRuns = i;
    }

    public Enumeration listOptions() {
        Vector vector = new Vector(7);
        vector.addElement(new Option("\tPopulation size", "L", 1, "-L <integer>"));
        vector.addElement(new Option("\tDescendant population size", "A", 1, "-A <integer>"));
        vector.addElement(new Option("\tNumber of runs", "U", 1, "-U <integer>"));
        vector.addElement(new Option("\tUse mutation.\n\t(default true)", "M", 0, "-M"));
        vector.addElement(new Option("\tUse cross-over.\n\t(default true)", "C", 0, "-C"));
        vector.addElement(new Option("\tUse tournament selection (true) or maximum subpopulatin (false).\n\t(default false)", "O", 0, "-O"));
        vector.addElement(new Option("\tRandom number seed", "R", 1, "-R <seed>"));
        for (Enumeration enumeration = super.listOptions(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        return vector.elements();
    }

    public void setOptions(String as[]) throws Exception {
        String s = Utils.getOption('L', as);
        if (s.length() != 0) {
            setPopulationSize(Integer.parseInt(s));
        }
        String s1 = Utils.getOption('A', as);
        if (s1.length() != 0) {
            setDescendantPopulationSize(Integer.parseInt(s1));
        }
        String s2 = Utils.getOption('U', as);
        if (s2.length() != 0) {
            setRuns(Integer.parseInt(s2));
        }
        String s3 = Utils.getOption('R', as);
        if (s3.length() != 0) {
            setSeed(Integer.parseInt(s3));
        }
        setUseMutation(Utils.getFlag('M', as));
        setUseCrossOver(Utils.getFlag('C', as));
        setUseTournamentSelection(Utils.getFlag('O', as));
        super.setOptions(as);
    }

    public String[] getOptions() {
        String as[] = super.getOptions();
        String as1[] = new String[11 + as.length];
        int i = 0;
        as1[i++] = "-L";
        as1[i++] = (new StringBuilder()).append("").append(getPopulationSize()).toString();
        as1[i++] = "-A";
        as1[i++] = (new StringBuilder()).append("").append(getDescendantPopulationSize()).toString();
        as1[i++] = "-U";
        as1[i++] = (new StringBuilder()).append("").append(getRuns()).toString();
        as1[i++] = "-R";
        as1[i++] = (new StringBuilder()).append("").append(getSeed()).toString();
        if (getUseMutation()) {
            as1[i++] = "-M";
        }
        if (getUseCrossOver()) {
            as1[i++] = "-C";
        }
        if (getUseTournamentSelection()) {
            as1[i++] = "-O";
        }
        for (int j = 0; j < as.length; j++) {
            as1[i++] = as[j];
        }

        while (i < as1.length)  {
            as1[i++] = "";
        }
        return as1;
    }

    public boolean getUseCrossOver() {
        return m_bUseCrossOver;
    }

    public boolean getUseMutation() {
        return m_bUseMutation;
    }

    public int getDescendantPopulationSize() {
        return m_nDescendantPopulationSize;
    }

    public int getPopulationSize() {
        return m_nPopulationSize;
    }

    public void setUseCrossOver(boolean flag) {
        m_bUseCrossOver = flag;
    }

    public void setUseMutation(boolean flag) {
        m_bUseMutation = flag;
    }

    public boolean getUseTournamentSelection() {
        return m_bUseTournamentSelection;
    }

    public void setUseTournamentSelection(boolean flag) {
        m_bUseTournamentSelection = flag;
    }

    public void setDescendantPopulationSize(int i) {
        m_nDescendantPopulationSize = i;
    }

    public void setPopulationSize(int i) {
        m_nPopulationSize = i;
    }

    public int getSeed() {
        return m_nSeed;
    }

    public void setSeed(int i) {
        m_nSeed = i;
    }

    public String globalInfo() {
        return "This Bayes Network learning algorithm uses genetic search for finding a well scoring Bayes network structure. Genetic search works by having a population of Bayes network structures and allow them to mutate and apply cross over to get offspring. The best network structure found during the process is returned.";
    }

    public String runsTipText() {
        return "Sets the number of generations of Bayes network structure populations.";
    }

    public String seedTipText() {
        return "Initialization value for random number generator. Setting the seed allows replicability of experiments.";
    }

    public String populationSizeTipText() {
        return "Sets the size of the population of network structures that is selected each generation.";
    }

    public String descendantPopulationSizeTipText() {
        return "Sets the size of the population of descendants that is created each generation.";
    }

    public String useMutationTipText() {
        return "Determines whether mutation is allowed. Mutation flips a bit in the bit representation of the network structure. At least one of mutation or cross-over should be used.";
    }

    public String useCrossOverTipText() {
        return "Determines whether cross-over is allowed. Cross over combined the bit representations of network structure by taking a random first k bits of oneand adding the remainder of the other. At least one of mutation or cross-over should be used.";
    }

    public String useTournamentSelectionTipText() {
        return "Determines the method of selecting a population. When set to true, tournament selection is used (pick two at random and the highest is allowed to continue). When set to false, the top scoring network structures are selected.";
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.5 $");
    }
}
