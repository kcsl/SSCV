package com.kcsl.sscv;

import java.math.BigInteger;
import java.util.Random;

public class Loop2_not_vulnerable {
	
	private static int MAX_INPUT = 500;
	
	private static BigInteger ONE = BigInteger.ONE;
	
	private static int mode = 0;
	
	public static void main(int myInput) {
		
		BigInteger[] arr = new BigInteger[MAX_INPUT+1];
		
		for(int i = 0; i <= MAX_INPUT; i++) {
			arr[i] = BigInteger.valueOf(i);
		}
		
		BigInteger prime = BigInteger.probablePrime(127, new Random());
		
		for (int j = 0; j <= MAX_INPUT; j++){
           BigInteger d = compose(j == myInput, arr[j], prime);
		}
	}
	
	public static BigInteger compose(boolean b, BigInteger a, BigInteger p) {
		return differ(a, MAX_INPUT / 2, p, ONE);
	}
	
	public static BigInteger differ(BigInteger a, int b, BigInteger n, BigInteger o) {
		
		if(mode == 0) {
			return ONE;
		}
		
		if (b%2 == 1) {
			return oddDiff(a, b, n, o);
		}
		if (b == 0) {
	        return o;
	    } else {
	    	return oddDiff(a, b - 1, n, o.multiply(a).mod(n));
		}
		
	}
	
	public static BigInteger oddDiff(BigInteger a, int b, BigInteger p, BigInteger curr) {
		return differ(a, b - (b % 2), p, curr.multiply(a).mod(p));

	}
}
