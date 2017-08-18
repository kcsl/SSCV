package com.kcsl.sscv.helpers;

import java.math.BigInteger;

public class Key2 {
	private BigInteger divisor; // The Rsa public modulus
    private BigInteger e; // The RSA public exponent
    private Multiplier mont; // To allow fast encryption with the Montgomery multiplication method

    public Key2(BigInteger divisor, BigInteger exponent) {
        this.divisor = divisor;
        this.e = exponent;
        this.mont = new Multiplier(divisor);
    }
}
