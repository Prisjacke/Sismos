// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 

package weka.classifiers.bayes.net.search.local;


public interface Scoreable {

    public static final int BAYES = 0;
    public static final int BDeu = 1;
    public static final int MDL = 2;
    public static final int ENTROPY = 3;
    public static final int AIC = 4;

    public abstract double logScore(int i, int j);
}
