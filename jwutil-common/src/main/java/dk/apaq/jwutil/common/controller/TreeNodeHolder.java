package dk.apaq.jwutil.common.controller;

import com.fasterxml.jackson.core.TreeNode;


public class TreeNodeHolder {

    private static final ThreadLocal<TreeNode> TREE_NODE = new ThreadLocal<>();
    
    public static TreeNode get() {
        return TREE_NODE.get();
    }
    
    public static void set(TreeNode treenode) {
        TREE_NODE.set(treenode);
    }
}
