package com.kcsl.sscv;

import java.io.FileNotFoundException;
import java.math.BigInteger;

import com.kcsl.sscv.helpers.Key1;

/*
 * SSCV 3
 * 
 * Question : Is there a side channel in time which enables an attacker to impersonate another user?
 * 
 * Additional information : Attacker can connect to other users and learn about their public key (code omitted)
 * Attacker can send messages to other users and they use the given decrypt method to decrypt the ciphertext.
 * Attacker can observe the time it takes for other user to decrypt the message.
 * 
 * Patterns covered : Pattern 2 and 5
 */

public class SSCV_3 {
	
	public static void decrypt(byte[] msg, String keyFilePath) throws FileNotFoundException {
		
		Key1 key = Key1.composeKeyFromFile(keyFilePath);
		
		key.decrypt(toBigInt(msg, key.takeBitSize()));
	}
	
	public static BigInteger toBigInt(byte[] data, int bitSize) {
        if (data.length > bitSize + 1) {
        	throw new IllegalArgumentException("data length " + data.length + " is too long for bitsize " + bitSize);
        }

        return new BigInteger(1, data);
    }
	
}
