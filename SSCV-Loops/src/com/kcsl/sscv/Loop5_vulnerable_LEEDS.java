package com.kcsl.sscv;

import com.kcsl.sscv.library.BTree;

public class Loop5_vulnerable_LEEDS {
	
	public static Integer KEY_MIN = 100000;
    public static Integer KEY_MAX = 10000000;
	
    public static BTree btree = new BTree(10);
    
	public static void insert(Integer key) {
		
		if(key < KEY_MIN && key > KEY_MAX) {
			System.out.println("Invalid Key");
			return;
		}
		else if(isRestricted(key)) {
			return;
		}
		else {
			btree.add(key, null, false);
			return;
		}
	}
	
	public static boolean isRestricted(Integer key) {
		if(key == 2543747) {
			return true;
		}
		return false;
	}
}
