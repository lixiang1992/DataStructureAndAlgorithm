package segmenttree;

/**
 * 线段树板子
 */
public class SegTree {

    private TreeNode root;

    private int mod = (int) (1e9 + 7);

    private static class TreeNode {
        long val;

        long left;
        long right;

        TreeNode leftNode;
        TreeNode rightNode;

        long lazyAdd;

        long lazyMul;
        TreeNode(long left, long right) {
            this(left, right, 0L);
        }

        TreeNode(long left, long right, long val) {
            this.val = val;
            this.left = left;
            this.right = right;
            this.lazyAdd = 0L;
            this.lazyMul = 1L;
        }

        private long getMid() {
            return left + (right - left >> 1);
        }

        private TreeNode getLeftNode() {
            if (leftNode == null) {
                leftNode = new TreeNode(left, getMid(),val);
            }
            return leftNode;
        }

        private TreeNode getRightNode() {
            if (rightNode == null) {
                rightNode = new TreeNode(getMid() + 1, right,val);
            }
            return rightNode;
        }
    }

    public SegTree() {
        root = new TreeNode(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public SegTree(long left, long right) {
        root = new TreeNode(left, right);
    }

    public SegTree(long left, long right, long val) {
        root = new TreeNode(left, right, val);
    }

    public long query(long left,long right) {
        return query(root,left,right);
    }

    private long query(TreeNode node,long left,long right) {
        if(right < node.left || left > node.right) {
            return 0L;
        }
        if(left <= node.left && node.right <= right) {
            return node.val;
        }
        // 更新子树
        pushDown(node);
        return (query(node.getLeftNode(),left,right) + query(node.getRightNode(),left,right)) % mod;
    }

    public void add(long left,long right,long val) {
        add(root,left,right,val);
    }

    private void add(TreeNode node,long left,long right,long val) {
        if(left > node.right || node.left > right) {
            return;
        }
        if(left <= node.left && node.right <= right) {
            node.val += (node.right - node.left + 1) * val;
            node.val %= mod;
            node.lazyAdd += val;
            node.lazyAdd %= mod;
            return;
        }

        // 更新子树
        pushDown(node);

        long mid = node.getMid();
        if(right <= mid) {
            add(node.getLeftNode(),left,right,val);
        } else if(left > mid) {
            add(node.getRightNode(),left,right,val);
        } else {
            add(node.getLeftNode(),left,mid,val);
            add(node.getRightNode(),mid + 1,right,val);
        }
        pushUp(node);
    }

    public void mul(long left,long right,long val) {
        mul(root,left,right,val);
    }

    private void mul(TreeNode node,long left,long right,long val) {
        if(left > node.right || node.left > right) {
            return;
        }
        if(left <= node.left && node.right <= right) {
            node.val *= val;
            node.val %= mod;
            node.lazyMul *= val;
            node.lazyMul %= mod;
            node.lazyAdd *= val;
            node.lazyAdd %= mod;
            return;
        }

        // 更新子树
        pushDown(node);
        long mid = node.getMid();
        if(right <= mid) {
            mul(node.getLeftNode(),left,right,val);
        } else if(left > mid) {
            mul(node.getRightNode(),left,right,val);
        } else {
            mul(node.getLeftNode(),left,mid,val);
            mul(node.getRightNode(),mid + 1,right,val);
        }
        pushUp(node);
    }

    private void pushUp(TreeNode node) {
        node.val = (node.getLeftNode().val + node.getRightNode().val) % mod;
    }


    private void pushDown(TreeNode node) {
        if(node.lazyMul != 1L) {
            if(node.leftNode != null) {
                node.leftNode.val = (node.leftNode.val * node.lazyMul) % mod;
                node.leftNode.lazyMul = (node.leftNode.lazyMul * node.lazyMul) % mod;
                node.leftNode.lazyAdd = (node.leftNode.lazyAdd * node.lazyMul) % mod;

            }
            if(node.rightNode != null) {
                node.rightNode.val = (node.rightNode.val * node.lazyMul) % mod;
                node.rightNode.lazyMul = (node.rightNode.lazyMul * node.lazyMul) % mod;
                node.rightNode.lazyAdd = (node.rightNode.lazyAdd * node.lazyMul) % mod;
            }
            node.lazyMul = 1L;
        }
        if(node.lazyAdd != 0L) {
            if(node.leftNode != null) {
                node.leftNode.val += (node.leftNode.right - node.leftNode.left + 1) * node.lazyAdd;
                node.leftNode.val %= mod;
                node.leftNode.lazyAdd += node.lazyAdd;
                node.leftNode.lazyAdd %= mod;
            }
            if(node.rightNode != null) {
                node.rightNode.val += (node.rightNode.right - node.rightNode.left + 1) * node.lazyAdd;
                node.rightNode.val %= mod;
                node.rightNode.lazyAdd += node.lazyAdd;
                node.rightNode.lazyAdd %= mod;
            }
            node.lazyAdd = 0L;
        }
    }
}
