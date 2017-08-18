package com.kcsl.sscv;

import com.kcsl.sscv.helpers.BTree;

/*
 * This example is based on a database application. The database stores keys and a user can insert keys into it.
 * The keys are of two types - public or private.
 * The secret here is whether an attacker can find value of the private key.
 * For simplicity, there is only one private key. Actual application can have multiple private keys.
 * 
 * Question : Is there a side channel in time in the following code, which enables an attacker to find out a private key?
 * 
 * Additional Information : Attacker can interact with the given code and insert keys into the data.
 * 
 * Patterns covered - Pattern 1
 */

public class SSCV_5_LEEDS {
	
	public static Integer KEY_MIN = 100000;
    public static Integer KEY_MAX = 10000000;
	
    public static BTree btree = new BTree(10);
    
	public static void insert(Integer key) {
		
		if(key < KEY_MIN && key > KEY_MAX) {
			System.out.println("Invalid Key");
			return;
		}
		else if(isPrivate(key)) {
			return;
		}
		else {
			// Loop is in the add method
			btree.add(key, null, false);
			return;
		}
	}
	
	public static boolean isPrivate(Integer key) {
		if(key == 2543747) {
			return true;
		}
		return false;
	}
}
