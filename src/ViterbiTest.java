import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;



public class ViterbiTest {
	private Viterbi readMeTest;
	private Viterbi dnaTest;
	private Viterbi myTest;
	
	private double[] pi = new double[2];
	private double[][] A = new double[2][2];
	private double[][] B = new double[2][2];
	
	private double[] dnaPi = new double[2];
	private double[][] dnaA = new double[2][2];
	private double[][] dnaB = new double[2][4];
	
	private double[] testPi = new double[2];
	private double[][] testA = new double[2][2];
	private double[][] testB = new double[2][2];
	
	int[] omega1 = {0, 0, 0, 1};
	int[] omega2 = {0, 0, 0, 1, 0};
	int[] omega3 = {0, 0, 0, 1, 0, 0};
	int[] omega4 = {0, 0, 0, 1, 0, 1, 1, 0};
	int[] omega5;
	int[] omega6;
	int[] omega7;
	int[] dnaOmega1 = {2, 2, 1, 0, 1, 3, 2, 0, 0};
	int[] dnaOmega2 = {2, 0, 2, 0, 3, 0, 3, 0, 1,
			0, 3, 0, 2, 0, 0, 3, 3, 0, 1, 2};
	
	List<Integer> testSeq = new LinkedList<Integer>();
	int[] testOmega = new int[2];
	
	@Before
    public void setUp() {
		pi[0] = 0.45;
		pi[1] = 0.55;
		A[0][0] = 0.7;
		A[0][1] = 0.3;
		A[1][0] = 0.25;
		A[1][1] = 0.75;
		B[0][0] = 0.8;
		B[0][1] = 0.2;
		B[1][0] = 0.3;
		B[1][1] = 0.7;
		
		dnaPi[0] = 0.5;
		dnaPi[1] = 0.5;
		dnaA[0][0] = 0.5;
		dnaA[0][1] = 0.5;
		dnaA[1][0] = 0.4;
		dnaA[1][1] = 0.6;
		dnaB[0][0] = 0.2;
		dnaB[0][1] = 0.3;
		dnaB[0][2] = 0.3;
		dnaB[0][3] = 0.2;
		dnaB[1][0] = 0.3;
		dnaB[1][1] = 0.2;
		dnaB[1][2] = 0.2;
		dnaB[1][3] = 0.3;
		
		testPi[0] = 0.5;
		testPi[1] = 0.5;
		testA[0][0] = 0.5;
		testA[0][1] = 0.5;
		testA[1][0] = 0.5;
		testA[1][1] = 0.5;
		testB[0][0] = 1.0;
		testB[0][1] = 0.0;
		testB[1][0] = 0.0;
		testB[1][1] = 1.0;
		
		
		
		omega5 = new int[1200];
		for (int i = 0; i < 300; i++) {
			omega5[i] = 0;
		}
		for (int i = 300; i < 600; i++) {
			omega5[i] = 1;
		}
		for (int i = 600; i < 900; i++) {
			omega5[i] = 0;
		}
		for (int i = 900; i < 1200; i++) {
			omega5[i] = 1;
		}
		
		omega6 = new int[2000];
		for (int i = 0; i < 500; i++) {
			omega6[i] = 0;
		}
		for (int i = 500; i < 1000; i++) {
			omega6[i] = 1;
		}
		for (int i =1000; i < 1500; i++) {
			omega6[i] = 0;
		}
		for (int i = 1500; i < 2000; i++) {
			omega6[i] = 1;
		}
		
		omega7 = new int[2004];
		for (int i = 0; i < 500; i++) {
			omega7[i] = 0;
		}
		for (int i = 500; i < 1000; i++) {
			omega7[i] = 1;
		}
		for (int i =1000; i < 1500; i++) {
			omega7[i] = 0;
		}
		for (int i = 1500; i < 2000; i++) {
			omega7[i] = 1;
		}
		omega7[2000] = 0;
		omega7[2001] = 0;
		omega7[2002] = 0;
		omega7[2003] = 1;
				
        readMeTest = new Viterbi(pi, A, B);
        dnaTest = new Viterbi(dnaPi, dnaA, dnaB);
        myTest = new Viterbi(testPi, testA, testB);
    }
	
	@Test
	public void test1bestSeq() {
		//readMeTest.bestSeq(omega1, false);	
		//readMeTest.maxScore(omega1, false);
	}
	
	
	@Test
	public void test2bestSeq() {
		//readMeTest.bestSeq(omega2, false);
		//readMeTest.maxScore(omega2, false);
	}
	
	@Test
	public void test3bestSeq() {
		//readMeTest.bestSeq(omega3, false);
		//readMeTest.maxScore(omega3, false);
	}
	
	@Test
	public void test4bestSeq() {
		//readMeTest.bestSeq(omega4, false);
		//readMeTest.maxScore(omega4, false);
	}
	
	@Test
	public void test5bestSeq() {
		//readMeTest.bestSeq(omega5, false);
		//readMeTest.maxScore(omega5, false);
		//readMeTest.maxScore(omega5, true);
	}
	
	@Test
	public void test6bestSeq() {
		//readMeTest.bestSeq(omega6, false);
		//readMeTest.maxScore(omega6, false);
		//readMeTest.maxScore(omega6, true);
	}
	
	@Test
	public void test7bestSeq() {
		//readMeTest.bestSeq(omega7, false);
		//readMeTest.maxScore(omega7, false);
		readMeTest.maxScore(omega7, true);
	}
	
	@Test
	public void testDnaBestSeq() {
		//dnaTest.bestSeq(dnaOmega1, false);
		//dnaTest.maxScore(dnaOmega1, false);
		//dnaTest.maxScoreSet(dnaOmega1, 2, false);
	}
	
	@Test
	public void testDnaBestSeq2() {
		//dnaTest.bestSeq(dnaOmega2, false);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullA() {
		double[][] invalidA1 = null;
		Viterbi fail1 = new Viterbi(pi, invalidA1, B);
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testImproperA() {
		double[][] invalidA2 = new double[2][2];
		invalidA2[0][0] = 0.5;
		invalidA2[0][1] = 0.4;
		invalidA2[1][0] = 0.1;
		invalidA2[1][1] = 0.1;
		Viterbi fail2 = new Viterbi(pi, invalidA2, B);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testNullB() {
		double[][] invalidB1 = null;
		Viterbi fail3 = new Viterbi(pi, A, invalidB1);
		
	}
	@Test(expected=IllegalArgumentException.class)
	public void testImproperB() {
		double[][] invalidB2 = new double[2][2];
		invalidB2[0][0] = 0.5;
		invalidB2[0][1] = 0.4;
		invalidB2[1][0] = 0.1;
		invalidB2[1][1] = 0.1;
		Viterbi fail4 = new Viterbi(pi, A, invalidB2);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testNullPi() {
		double[] invalidPi1 = null;
		Viterbi fail5 = new Viterbi(invalidPi1, A, B);
	}
	@Test(expected=IllegalArgumentException.class)
	public void testImproperPi() {
		double[] invalidPi2 = new double[3];
		invalidPi2[0] = 0.5;
		invalidPi2[1] = 0.5;
		invalidPi2[2] = 0.0;
		Viterbi fail6 = new Viterbi(invalidPi2, A, B);
	}
	@Test
	public void testProbOfSeq0() {
		testOmega[0] = 0;
		testOmega[1] = 1;
		testSeq.add(0);
		testSeq.add(0);
		assertEquals(0.0, myTest.probOfSeq(testOmega, testSeq, false), 0);
	}
	@Test
	public void testBestSeq() {
		testOmega[0] = 0;
		testOmega[0] = 0;
		testSeq.add(0);
		testSeq.add(0);
		assertTrue(testSeq.equals(myTest.bestSeq(testOmega, false)));
	}
	
	

}
