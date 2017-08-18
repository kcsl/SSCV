package com.kcsl.sscv.helpers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class BTree {
    private static int T = 10;
    private Node mRootNode;
    private static final int LEFT_CHILD_NODE = 0;
    private static final int RIGHT_CHILD_NODE = 1;
    public boolean optimizedinserts = false;
    private Object clist;
    public boolean calledmerge = false;

    public BTree(int t) {
        T = t;
        this.mRootNode = new Node();
        this.mRootNode.mIsLeafNode = true;
    }

    public static void printNode(Node n) {
        System.out.println("isleaf:" + n.mIsLeafNode);
        for (int i = 0; i < n.mKeys.length; ++i) {
            System.out.print("" + n.mKeys[i] + " : ");
        }
        System.out.println("");
    }

    public BTree() {
        this.mRootNode = new Node();
        this.mRootNode.mIsLeafNode = true;
    }

    public boolean add(int key, Object object, boolean log) {
        Node rootNode = this.mRootNode;
        if (!this.update(this.mRootNode, key, object)) {
            if (rootNode.mNumKeys == 2 * T - 1) {
                Node newRootNode;
                this.mRootNode = newRootNode = new Node();
                newRootNode.mIsLeafNode = false;
                this.mRootNode.mChildNodes[0] = rootNode;
                this.splitChildNode(newRootNode, 0, rootNode);
                this.insertIntoNonFullNode(newRootNode, key, object, null);
            } else {
                this.insertIntoNonFullNode(rootNode, key, object, null);
            }
        }
        return true;
    }

    void splitPotentialRemoteNode(Node parentNode, int newsize, int itertions) {
        if (parentNode.mNumKeys == itertions * newsize) {
            int i;
            int x;
            Node newTempPNode = new Node(itertions);
            newTempPNode.fastSearch = new Vector();
            newTempPNode.mNumKeys = itertions;
            for (x = 0; x < itertions * newsize; x += newsize) {
                Node newNode;
                newTempPNode.mChildNodes[x / newsize] = newNode = new Node(newsize - (T * 2 - 1));
                for (int y = 0; y < newsize; ++y) {
                    newNode.mKeys[y] = parentNode.mKeys[x + y];
                    if (y == newsize - 1) {
                        newTempPNode.fastSearch.ensureCapacity(x / newsize);
                        newTempPNode.fastSearch.add(x / newsize, parentNode.mKeys[x + y]);
                        newTempPNode.mKeys[x / newsize] = parentNode.mKeys[x + y];
                    }
                    newNode.mObjects[y] = parentNode.mObjects[x + y];
                    newNode.mNumKeys = y + 1;
                    newNode.mChildNodes[y] = parentNode.mChildNodes[x + y];
                    if (x + y != itertions * newsize - 1 || parentNode.mChildNodes[x + y + 1] == null) continue;
                    newNode.mChildNodes[y + 1] = parentNode.mChildNodes[x + y + 1];
                }
            }
            parentNode.mNumKeys = newTempPNode.mNumKeys;
            parentNode.fastSearch = new Vector();
            for (i = 0; i < parentNode.mChildNodes.length; ++i) {
                parentNode.mChildNodes[i] = null;
            }
            for (i = 0; i < parentNode.mKeys.length; ++i) {
                parentNode.mKeys[i] = 0;
            }
            parentNode.mObjects = null;
            parentNode.isremotenode = true;
            for (x = 0; x < newTempPNode.mNumKeys; ++x) {
                parentNode.mChildNodes[x] = newTempPNode.mChildNodes[x];
                parentNode.fastSearch.ensureCapacity(x + 1);
                parentNode.fastSearch.add(x, newTempPNode.fastSearch.get(x));
                parentNode.mKeys[x] = newTempPNode.mKeys[x];
            }
        }
    }

    void splitChildNode(Node parentNode, int i, Node node) {
        int j;
        Node newNode = new Node();
        newNode.mIsLeafNode = node.mIsLeafNode;
        newNode.mNumKeys = T - 1;
        for (j = 0; j < T - 1; ++j) {
            newNode.mKeys[j] = node.mKeys[j + T];
            newNode.mObjects[j] = node.mObjects[j + T];
        }
        if (!newNode.mIsLeafNode) {
            for (j = 0; j < T; ++j) {
                newNode.mChildNodes[j] = node.mChildNodes[j + T];
            }
            for (j = BTree.T; j <= node.mNumKeys; ++j) {
                node.mChildNodes[j] = null;
            }
        }
        for (j = BTree.T; j < node.mNumKeys; ++j) {
            node.mKeys[j] = 0;
            node.mObjects[j] = null;
        }
        node.mNumKeys = T - 1;
        try {
            this.setupMedian(newNode, parentNode, i, node);
        }
        catch (ArrayIndexOutOfBoundsException aex) {
            parentNode.increaseCapacity(parentNode.xtrasize + 1);
            this.setupMedian(newNode, parentNode, i, node);
        }
    }

    void setupMedian(Node newNode, Node parentNode, int i, Node node) {
        int j;
        for (j = parentNode.mNumKeys; j >= i + 1; --j) {
            parentNode.mChildNodes[j + 1] = parentNode.mChildNodes[j];
        }
        parentNode.mChildNodes[i + 1] = newNode;
        for (j = parentNode.mNumKeys - 1; j >= i; --j) {
            parentNode.mKeys[j + 1] = parentNode.mKeys[j];
            parentNode.mObjects[j + 1] = parentNode.mObjects[j];
        }
        parentNode.mKeys[i] = node.mKeys[T - 1];
        parentNode.mObjects[i] = node.mObjects[T - 1];
        node.mKeys[BTree.T - 1] = 0;
        node.mObjects[BTree.T - 1] = null;
        ++parentNode.mNumKeys;
        if (this.optimizedinserts) {
            if (this.optimizedinserts) {
                parentNode.fastSearch = new Vector(1, 1);
                parentNode.instantSearch = new ConcurrentHashMap(1);
            }
            for (int k = 0; k < parentNode.mKeys.length; ++k) {
                int indexOf;
                if (parentNode.mKeys[k] <= 0 || !this.optimizedinserts) continue;
                parentNode.fastSearch.ensureCapacity(k + 1);
                parentNode.fastSearch.add(parentNode.mKeys[k]);
                String valstr = null;
                valstr = parentNode.mObjects[k] != null ? ((indexOf = (valstr = parentNode.mObjects[k].toString()).indexOf(":")) > 0 ? valstr.substring(indexOf, valstr.length()) : "null") : "null";
                parentNode.instantSearch.put(parentNode.mKeys[k], Integer.toString(k) + ":" + valstr);
            }
            if (parentNode.fastSearch != null) {
                Collections.sort(parentNode.fastSearch);
                Integer[] toArray = parentNode.fastSearch.toArray(new Integer[parentNode.fastSearch.size()]);
                parentNode.fastSearchArray = parentNode.fastSearch.toArray(toArray);
            }
        }
    }

    void insertIntoNonFullNode(Node node, int key, Object object, Node parent) {
        int i = 0;
        if (node.mIsLeafNode) {
            int itertions;
            int newsize;
            for (i = node.mNumKeys - 1; i >= 0 && key < node.mKeys[i]; --i) {
                node.mKeys[i + 1] = node.mKeys[i];
                node.mObjects[i + 1] = node.mObjects[i];
            }
            node.mKeys[++i] = key;
            if (object != null) {
                node.mObjects[i] = object;
            }
            ++node.mNumKeys;
            if (parent != null && this.optimizedinserts && !parent.mIsLeafNode && parent.mNumKeys == (newsize = 200) * (itertions = 2)) {
                this.splitPotentialRemoteNode(parent, newsize, itertions);
            }
        } else {
            if (node.fastSearch == null) {
                while (i >= 0 && key < node.mKeys[i]) {
                    --i;
                }
                ++i;
            } else if (node.fastSearch != null) {
                if (node.mNumKeys < 20) {
                    while (i >= 0 && key < node.mKeys[i]) {
                        --i;
                    }
                    ++i;
                } else {
                    int retVal = 0;
                    retVal = Arrays.binarySearch((Object[])node.fastSearchArray, (Object)key);
                    i = retVal = retVal * -1 - 1;
                }
            }
            if (node.isremotenode && node.mChildNodes[i] == null) {
                this.insertIntoNonFullNode(node.mChildNodes[i - 1], key, object, node);
                return;
            }
            if (node.mChildNodes[i].mNumKeys == 2 * T - 1) {
                if (this.optimizedinserts) {
                    if (node.mChildNodes[i].mIsLeafNode) {
                        Node cNode = node.mChildNodes[i];
                        Node tempNode = new Node(cNode.xtrasize + 10);
                        for (int x = 0; x < cNode.mNumKeys; ++x) {
                            tempNode.mChildNodes[x] = cNode.mChildNodes[x];
                            tempNode.mKeys[x] = cNode.mKeys[x];
                            tempNode.mObjects[x] = cNode.mObjects[x];
                            ++tempNode.mNumKeys;
                        }
                        tempNode.mKeys[cNode.mNumKeys] = key;
                        if (object != null) {
                            tempNode.mObjects[cNode.mNumKeys] = object;
                        }
                        ++tempNode.mNumKeys;
                        int sortTheNumbers = tempNode.mNumKeys - 1;
                        for (int a = 0; a < sortTheNumbers; ++a) {
                            for (int b = 0; b < sortTheNumbers; ++b) {
                                if (tempNode.mKeys[b] <= tempNode.mKeys[b + 1]) continue;
                                int temp = tempNode.mKeys[b];
                                Object tempobj = tempNode.mObjects[b];
                                tempNode.mKeys[b] = tempNode.mKeys[b + 1];
                                tempNode.mKeys[b + 1] = temp;
                                tempNode.mObjects[b] = tempNode.mObjects[b + 1];
                                tempNode.mObjects[b + 1] = tempobj;
                            }
                        }
                        for (int x2 = 0; x2 < tempNode.mNumKeys - 1; ++x2) {
                            cNode.mKeys[x2] = tempNode.mKeys[x2];
                            cNode.mObjects[x2] = tempNode.mObjects[x2];
                        }
                        key = tempNode.mKeys[tempNode.mNumKeys - 1];
                        object = tempNode.mObjects[tempNode.mNumKeys - 1];
                        this.splitChildNode(node, i, node.mChildNodes[i]);
                        if (key > node.mKeys[i]) {
                            ++i;
                        }
                    }
                } else {
                    this.splitChildNode(node, i, node.mChildNodes[i]);
                    if (key > node.mKeys[i]) {
                        ++i;
                    }
                }
            }
            this.insertIntoNonFullNode(node.mChildNodes[i], key, object, node);
        }
    }

    public boolean delete(int key) {
        if (!this.optimizedinserts) {
            this.delete(this.mRootNode, key);
        } else {
            Node n = this.searchForNode(key);
            this.fastDelete(n, key);
        }
        return true;
    }

    public void delete(Node node, int key) {
        if (node.mIsLeafNode) {
            int i = node.binarySearch(key);
            if (i != -1) {
                node.remove(i, 0);
            }
        } else {
            int i = node.binarySearch(key);
            if (i != -1) {
                Node leftChildNode = node.mChildNodes[i];
                Node rightChildNode = node.mChildNodes[i + 1];
                if (leftChildNode.mNumKeys >= T) {
                    Node predecessorNode;
                    Node erasureNode = predecessorNode = leftChildNode;
                    while (!predecessorNode.mIsLeafNode) {
                        erasureNode = predecessorNode;
                        predecessorNode = predecessorNode.mChildNodes[node.mNumKeys - 1];
                    }
                    node.mKeys[i] = predecessorNode.mKeys[predecessorNode.mNumKeys - 1];
                    node.mObjects[i] = predecessorNode.mObjects[predecessorNode.mNumKeys - 1];
                    this.delete(erasureNode, node.mKeys[i]);
                } else if (rightChildNode.mNumKeys >= T) {
                    Node successorNode;
                    Node erasureNode = successorNode = rightChildNode;
                    while (!successorNode.mIsLeafNode) {
                        erasureNode = successorNode;
                        successorNode = successorNode.mChildNodes[0];
                    }
                    node.mKeys[i] = successorNode.mKeys[0];
                    node.mObjects[i] = successorNode.mObjects[0];
                    this.delete(erasureNode, node.mKeys[i]);
                } else {
                    int medianKeyIndex = this.mergeNodes(leftChildNode, rightChildNode);
                    this.moveKey(node, i, 1, leftChildNode, medianKeyIndex);
                    this.delete(leftChildNode, key);
                }
            } else {
                i = node.subtreeRootNodeIndex(key);
                Node childNode = node.mChildNodes[i];
                if (childNode.mNumKeys == T - 1) {
                    Node rightChildSibling;
                    Node leftChildSibling = i - 1 >= 0 ? node.mChildNodes[i - 1] : null;
                    Node node2 = rightChildSibling = i + 1 <= node.mNumKeys ? node.mChildNodes[i + 1] : null;
                    if (leftChildSibling != null && leftChildSibling.mNumKeys >= T) {
                        childNode.shiftRightByOne();
                        childNode.mKeys[0] = node.mKeys[i - 1];
                        childNode.mObjects[0] = node.mObjects[i - 1];
                        if (!childNode.mIsLeafNode) {
                            childNode.mChildNodes[0] = leftChildSibling.mChildNodes[leftChildSibling.mNumKeys];
                        }
                        ++childNode.mNumKeys;
                        node.mKeys[i - 1] = leftChildSibling.mKeys[leftChildSibling.mNumKeys - 1];
                        node.mObjects[i - 1] = leftChildSibling.mObjects[leftChildSibling.mNumKeys - 1];
                        leftChildSibling.remove(leftChildSibling.mNumKeys - 1, 1);
                    } else if (rightChildSibling != null && rightChildSibling.mNumKeys >= T) {
                        childNode.mKeys[childNode.mNumKeys] = node.mKeys[i];
                        childNode.mObjects[childNode.mNumKeys] = node.mObjects[i];
                        if (!childNode.mIsLeafNode) {
                            childNode.mChildNodes[childNode.mNumKeys + 1] = rightChildSibling.mChildNodes[0];
                        }
                        ++childNode.mNumKeys;
                        node.mKeys[i] = rightChildSibling.mKeys[0];
                        node.mObjects[i] = rightChildSibling.mObjects[0];
                        rightChildSibling.remove(0, 0);
                    } else if (leftChildSibling != null) {
                        int medianKeyIndex = this.mergeNodes(childNode, leftChildSibling);
                        this.moveKey(node, i - 1, 0, childNode, medianKeyIndex);
                    } else if (rightChildSibling != null) {
                        int medianKeyIndex = this.mergeNodes(childNode, rightChildSibling);
                        this.moveKey(node, i, 1, childNode, medianKeyIndex);
                    }
                }
                this.delete(childNode, key);
            }
        }
    }

    int mergeNodes(Node dstNode, Node srcNode) {
        int medianKeyIndex;
        this.calledmerge = true;
        if (srcNode.mKeys[0] < dstNode.mKeys[dstNode.mNumKeys - 1]) {
            int i;
            if (!dstNode.mIsLeafNode) {
                dstNode.mChildNodes[srcNode.mNumKeys + dstNode.mNumKeys + 1] = dstNode.mChildNodes[dstNode.mNumKeys];
            }
            for (i = dstNode.mNumKeys; i > 0; --i) {
                dstNode.mKeys[srcNode.mNumKeys + i] = dstNode.mKeys[i - 1];
                dstNode.mObjects[srcNode.mNumKeys + i] = dstNode.mObjects[i - 1];
                if (dstNode.mIsLeafNode) continue;
                dstNode.mChildNodes[srcNode.mNumKeys + i] = dstNode.mChildNodes[i - 1];
            }
            medianKeyIndex = srcNode.mNumKeys;
            dstNode.mKeys[medianKeyIndex] = 0;
            dstNode.mObjects[medianKeyIndex] = null;
            for (i = 0; i < srcNode.mNumKeys; ++i) {
                dstNode.mKeys[i] = srcNode.mKeys[i];
                dstNode.mObjects[i] = srcNode.mObjects[i];
                if (srcNode.mIsLeafNode) continue;
                dstNode.mChildNodes[i] = srcNode.mChildNodes[i];
            }
            if (!srcNode.mIsLeafNode) {
                dstNode.mChildNodes[i] = srcNode.mChildNodes[i];
            }
        } else {
            int i;
            medianKeyIndex = dstNode.mNumKeys;
            dstNode.mKeys[medianKeyIndex] = 0;
            dstNode.mObjects[medianKeyIndex] = null;
            int offset = medianKeyIndex + 1;
            for (i = 0; i < srcNode.mNumKeys; ++i) {
                dstNode.mKeys[offset + i] = srcNode.mKeys[i];
                dstNode.mObjects[offset + i] = srcNode.mObjects[i];
                if (srcNode.mIsLeafNode) continue;
                dstNode.mChildNodes[offset + i] = srcNode.mChildNodes[i];
            }
            if (!srcNode.mIsLeafNode) {
                dstNode.mChildNodes[offset + i] = srcNode.mChildNodes[i];
            }
        }
        dstNode.mNumKeys += srcNode.mNumKeys;
        return medianKeyIndex;
    }

    void moveKey(Node srcNode, int srcKeyIndex, int childIndex, Node dstNode, int medianKeyIndex) {
        dstNode.mKeys[medianKeyIndex] = srcNode.mKeys[srcKeyIndex];
        dstNode.mObjects[medianKeyIndex] = srcNode.mObjects[srcKeyIndex];
        ++dstNode.mNumKeys;
        srcNode.remove(srcKeyIndex, childIndex);
        if (srcNode == this.mRootNode && srcNode.mNumKeys == 0) {
            this.mRootNode = dstNode;
        }
    }

    public Object searchRange(int key1, int key2) {
        return this.search(this.mRootNode, key1);
    }

    public Object search(int key) {
        return this.search(this.mRootNode, key);
    }

    public Object search(Node node, int key) {
        int i;
        for (i = 0; i < node.mNumKeys && key > node.mKeys[i]; ++i) {
        }
        if (i < node.mNumKeys && key == node.mKeys[i]) {
            if (node.isfastDeleted[i]) {
                return null;
            }
            return node.mObjects[i];
        }
        if (node.mIsLeafNode) {
            return null;
        }
        return this.search(node.mChildNodes[i], key);
    }

    public Node searchForNode(int key) {
        return this.searchForNode(this.mRootNode, key);
    }

    public Node searchForNode(Node node, int key) {
        int i;
        for (i = node.mNumKeys - 1; i >= 0 && key < node.mKeys[i]; --i) {
        }
        if (i >= 0 && node.mKeys[i] == key && !node.isremotenode) {
            return node;
        }
        if (!node.isremotenode) {
            ++i;
        }
        if (i < node.mNumKeys && key == node.mKeys[i] && !node.isremotenode) {
            return node;
        }
        if (node.mIsLeafNode) {
            return node;
        }
        Node n = null;
        n = this.searchForNode(node.mChildNodes[i], key);
        return n;
    }

    public Object search2(int key) {
        return this.search2(this.mRootNode, key);
    }

    public Object search2(Node node, int key) {
        while (node != null) {
            int i;
            for (i = 0; i < node.mNumKeys && key > node.mKeys[i]; ++i) {
            }
            if (i < node.mNumKeys && key == node.mKeys[i]) {
                return node.mObjects[i];
            }
            if (node.mIsLeafNode) {
                return null;
            }
            node = node.mChildNodes[i];
        }
        return null;
    }

    public void fastDelete(Node node, int key) {
        int i;
        for (i = 0; i < node.mNumKeys && key > node.mKeys[i]; ++i) {
        }
        if (i < node.mNumKeys && key == node.mKeys[i]) {
            if (node.mIsLeafNode) {
                node.remove(i, 0);
            } else {
                node.isfastDeleted[i] = true;
            }
        }
    }

    public void printOutWholetree(int key) {
        this.printOutWholetree(this.mRootNode, 1, key);
    }

    public void printOutWholetree(Node node, int level, int key) {
        int foundindex = -3;
        if (key == -1) {
            foundindex = -1;
        }
        if (node != null) {
            int i;
            for (i = 0; i < node.mKeys.length; ++i) {
                if (i >= node.mNumKeys + 1 || node.mKeys[i] != key || key == -2) continue;
                key = -1;
                foundindex = i;
            }
            if (key == -1) {
                System.out.println("n:" + node.toString() + " level:" + level);
                for (i = 0; i < node.mKeys.length; ++i) {
                    if (i >= node.mNumKeys + 1 || key != -1) continue;
                    System.out.println("" + i + ":" + node.mKeys[i]);
                }
                System.out.println("*************************");
            }
            if (!node.mIsLeafNode) {
                for (int c = 0; c < node.mChildNodes.length; ++c) {
                    if (c >= node.mNumKeys + 1) continue;
                    Node cnode = node.mChildNodes[c];
                    if (c == foundindex || c == foundindex + 1) {
                        // empty if block
                    }
                    if (key != -1) continue;
                    this.printOutWholetree(cnode, level + 1, key);
                }
            }
        }
    }

    public ArrayList<Integer> getRange(int min, int max) {
        ArrayList<Integer> results = new ArrayList<Integer>();
        this.getRange(this.mRootNode, 1, min, max, results);
        return results;
    }

    public void getRange(Node node, int level, int min, int max, ArrayList<Integer> results) {
        if (node != null) {
            for (int i = 0; i < node.mNumKeys; ++i) {
                Node cnode;
                if (!node.mIsLeafNode && i < node.mChildNodes.length) {
                    cnode = node.mChildNodes[i];
                    this.getRange(cnode, level + 1, min, max, results);
                }
                if (node.mKeys[i] >= min && node.mKeys[i] <= max && !node.isremotenode) {
                    results.add(node.mKeys[i]);
                }
                if (node.mKeys[i] >= max && !node.isremotenode) {
                    return;
                }
                if (node.mIsLeafNode || i != node.mNumKeys || node.mChildNodes[i + 1] == null) continue;
                cnode = node.mChildNodes[i + 1];
                this.getRange(cnode, level + 1, min, max, results);
            }
        }
    }

    private boolean update(Node node, int key, Object object) {
        while (node != null) {
            int i;
            for (i = 0; i < node.mNumKeys && key > node.mKeys[i]; ++i) {
            }
            if (i < node.mNumKeys && key == node.mKeys[i]) {
                if (node.mObjects == null) {
                    return false;
                }
                node.mObjects[i] = object;
                return true;
            }
            if (node.mIsLeafNode) {
                return false;
            }
            node = node.mChildNodes[i];
        }
        return false;
    }

    void recurseBTree(List<Integer> res, int min, int max, Node node, int order) {
        if (node != null) {
            if (node.mIsLeafNode) {
                for (int i = 0; i < node.mNumKeys; ++i) {
                    if (node.mKeys[i] < min || node.mKeys[i] > max) continue;
                    res.add(node.mKeys[i]);
                }
            } else {
                int i;
                for (i = 0; i < node.mNumKeys; ++i) {
                    if (node.mKeys[i] >= min && node.mKeys[i] <= max) {
                        res.add(node.mKeys[i]);
                    }
                    this.recurseBTree(res, min, max, node.mChildNodes[i], order + 1);
                }
                this.recurseBTree(res, min, max, node.mChildNodes[i], order + 1);
            }
        }
    }

    String printBTree(Node node, int order) {
        String string = "";
        if (node != null) {
            if (node.mIsLeafNode) {
                for (int i = 0; i < node.mNumKeys; ++i) {
                    string = string + node.mObjects[i] + ", ";
                }
                string = string + "\n";
            } else {
                int i;
                for (i = 0; i < node.mNumKeys; ++i) {
                    string = string + node.mObjects[i] + "order:" + order + ", \n";
                    string = string + this.printBTree(node.mChildNodes[i], order + 1);
                }
                string = string + this.printBTree(node.mChildNodes[i], order + 1);
            }
        }
        return string;
    }

    String printBTreeX(Node node, int order) {
        String string = "";
        if (node != null && !node.mIsLeafNode) {
            int i;
            for (i = 0; i < node.mNumKeys; ++i) {
                string = string + this.printBTree(node.mChildNodes[i], order + 1);
                string = string + order + ", ";
            }
            string = string + this.printBTree(node.mChildNodes[i], order + 1);
        }
        return string;
    }

    public String toString() {
        return this.printBTree(this.mRootNode, 0);
    }

    public List<Integer> toList(int min, int max) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        this.recurseBTree(res, min, max, this.mRootNode, 0);
        Collections.sort(res);
        return res;
    }

    void validate() throws Exception {
        List<Integer> array = this.getKeys(this.mRootNode);
        for (int i = 0; i < array.size() - 1; ++i) {
            if (array.get(i) < array.get(i + 1)) continue;
            throw new Exception("B-Tree invalid: " + array.get(i) + " greater than " + array.get(i + 1));
        }
    }

    List<Integer> getKeys(Node node) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        if (node != null) {
            if (node.mIsLeafNode) {
                for (int i = 0; i < node.mNumKeys; ++i) {
                    array.add(node.mKeys[i]);
                }
            } else {
                int i;
                for (i = 0; i < node.mNumKeys; ++i) {
                    array.addAll(this.getKeys(node.mChildNodes[i]));
                    array.add(node.mKeys[i]);
                }
                array.addAll(this.getKeys(node.mChildNodes[i]));
            }
        }
        return array;
    }

    public class Node {
        public int xtrasize;
        public int mNumKeys;
        public int[] mKeys;
        public Object[] mObjects;
        public Node[] mChildNodes;
        public boolean mIsLeafNode;
        public boolean[] isfastDeleted;
        public int[] permissions;
        public Vector<Integer> fastSearch;
        Node parent;
        public ConcurrentHashMap<Integer, Object> instantSearch;
        private Integer[] fastSearchArray;
        private boolean isremotenode;

        public Node() {
            this(0);
        }

        public Node(int size) {
            this.xtrasize = 0;
            this.mNumKeys = 0;
            this.isremotenode = false;
            this.xtrasize = size;
            this.mKeys = new int[2 * T - 1 + this.xtrasize];
            this.mObjects = new Object[2 * T - 1 + this.xtrasize];
            this.mChildNodes = new Node[2 * T + this.xtrasize];
            this.isfastDeleted = new boolean[2 * T - 1 + this.xtrasize];
            this.permissions = new int[2 * T - 1 + this.xtrasize];
        }

        public void increaseCapacity(int s) {
            Node copy = this.copy(s);
            this.copyin(copy);
        }

        public Node copy(int s) {
            this.xtrasize += s;
            return this.copy(null);
        }

        public Node copy(Node newnode) {
            if (newnode == null) {
                newnode = new Node(this.xtrasize);
            }
            try {
                int i;
                newnode.mNumKeys = this.mNumKeys;
                for (i = 0; i < this.mKeys.length; ++i) {
                    newnode.mKeys[i] = this.mKeys[i];
                }
                for (i = 0; i < this.mObjects.length; ++i) {
                    newnode.mObjects[i] = this.mObjects[i];
                }
                for (i = 0; i < this.mChildNodes.length; ++i) {
                    newnode.mChildNodes[i] = this.mChildNodes[i];
                }
                for (i = 0; i < this.isfastDeleted.length; ++i) {
                    newnode.isfastDeleted[i] = this.isfastDeleted[i];
                }
                for (i = 0; i < this.permissions.length; ++i) {
                    newnode.permissions[i] = this.permissions[i];
                }
            }
            catch (ArrayIndexOutOfBoundsException e) {
                return this.copy(new Node(this.xtrasize + 1));
            }
            newnode.mIsLeafNode = this.mIsLeafNode;
            return newnode;
        }

        public void copyin(Node innode) {
            this.mNumKeys = innode.mNumKeys;
            this.mKeys = innode.mKeys;
            this.mObjects = innode.mObjects;
            this.mChildNodes = innode.mChildNodes;
            this.isfastDeleted = innode.isfastDeleted;
            this.permissions = innode.permissions;
            this.mIsLeafNode = innode.mIsLeafNode;
            this.isfastDeleted = innode.isfastDeleted;
        }

        int binarySearch(int key) {
            int leftIndex = 0;
            int rightIndex = this.mNumKeys - 1;
            while (leftIndex <= rightIndex) {
                int middleIndex = leftIndex + (rightIndex - leftIndex) / 2;
                if (this.mKeys[middleIndex] < key) {
                    leftIndex = middleIndex + 1;
                    continue;
                }
                if (this.mKeys[middleIndex] > key) {
                    rightIndex = middleIndex - 1;
                    continue;
                }
                return middleIndex;
            }
            return -1;
        }

        boolean contains(int key) {
            return this.binarySearch(key) != -1;
        }

        void remove(int index, int leftOrRightChild) {
            if (index >= 0) {
                int i;
                for (i = index; i < this.mNumKeys - 1; ++i) {
                    this.mKeys[i] = this.mKeys[i + 1];
                    this.mObjects[i] = this.mObjects[i + 1];
                    if (this.mIsLeafNode || i < index + leftOrRightChild) continue;
                    this.mChildNodes[i] = this.mChildNodes[i + 1];
                }
                this.mKeys[i] = 0;
                this.mObjects[i] = null;
                if (!this.mIsLeafNode) {
                    if (i >= index + leftOrRightChild) {
                        this.mChildNodes[i] = this.mChildNodes[i + 1];
                    }
                    this.mChildNodes[i + 1] = null;
                }
                --this.mNumKeys;
            }
        }

        void shiftRightByOne() {
            if (!this.mIsLeafNode) {
                this.mChildNodes[this.mNumKeys + 1] = this.mChildNodes[this.mNumKeys];
            }
            for (int i = this.mNumKeys - 1; i >= 0; --i) {
                this.mKeys[i + 1] = this.mKeys[i];
                this.mObjects[i + 1] = this.mObjects[i];
                if (this.mIsLeafNode) continue;
                this.mChildNodes[i + 1] = this.mChildNodes[i];
            }
        }

        int subtreeRootNodeIndex(int key) {
            for (int i = 0; i < this.mNumKeys; ++i) {
                if (key >= this.mKeys[i]) continue;
                return i;
            }
            return this.mNumKeys;
        }
    }

    class IntHolderV {
        Integer[] objects;
        Vector<Integer> objs;

        public IntHolderV(int size) {
            this.objects = null;
            this.objs = new Vector(1);
        }

        public int get(int index) {
            try {
                return this.objs.get(index);
            }
            catch (IndexOutOfBoundsException e) {
                return new Integer(0);
            }
        }

        public void put(int index, int i) {
            Integer integer = new Integer(i);
            if (this.objs.size() < index) {
                // empty if block
            }
            try {
                this.objs.add(index, integer);
            }
            catch (IndexOutOfBoundsException e) {
                this.objs.add(null);
                this.objs.ensureCapacity(index);
                this.put(index, integer);
            }
        }
    }

}

