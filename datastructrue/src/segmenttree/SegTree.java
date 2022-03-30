package segmenttree;

/**
 * 线段树板子
 */
public class SegTree {
    // root 节点
    private TreeNode root;

    private int mod = (int) (1e9 + 7);

    private static class TreeNode {
        long val;

        long left;
        long right;

        TreeNode leftNode;
        TreeNode rightNode;

        long lazyAdd;

        TreeNode(long left, long right) {
            this(left, right, 0L);
        }

        TreeNode(long left, long right, long val) {
            this.val = val;
            this.left = left;
            this.right = right;
            this.lazyAdd = 0L;
        }

        private long getMid() {
            return left + (right - left >> 1);
        }

        private TreeNode getLeftNode() {
            if (leftNode == null) {
                leftNode = new TreeNode(left, getMid());
            }
            return leftNode;
        }

        private TreeNode getRightNode() {
            if (rightNode == null) {
                rightNode = new TreeNode(getMid() + 1, right);
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

    public long query(long left, long right) {
        return query(root, left, right);
    }

    private long query(TreeNode node, long left, long right) {
        if (right < node.left || left > node.right) {
            return 0L;
        }
        if (left <= node.left && node.right <= right) {
            return node.val;
        }
        // 更新子树
        pushDown(node);
        return query(node.getLeftNode(), left, right) + query(node.getRightNode(), left, right);

    }

    public void update(long left, long right, long val) {
        update(root, left, right, val);
    }

    private void update(TreeNode node, long left, long right, long val) {
        if (left <= node.left && node.right <= right) {
            node.val += (node.right - node.left + 1) * val;
            node.lazyAdd += val;
            return;
        }
        // 更新子树
        pushDown(node);

        long mid = node.getMid();
        if (right <= mid) {
            update(node.getLeftNode(), left, right, val);
        } else if (left > mid) {
            update(node.getRightNode(), left, right, val);
        } else {
            update(node.getLeftNode(), left, mid, val);
            update(node.getRightNode(), mid + 1, right, val);
        }
        pushUp(node);
    }

    private void pushUp(TreeNode node) {
        node.val = node.getLeftNode().val + node.getRightNode().val;
    }

    private void pushDown(TreeNode node) {
        if (node.lazyAdd == 0L) {
            return;
        }
        if (node.leftNode != null) {
            node.leftNode.val += (node.leftNode.right - node.leftNode.left + 1) * node.lazyAdd;
            node.leftNode.lazyAdd += node.lazyAdd;
        }
        if (node.rightNode != null) {
            node.rightNode.val += (node.rightNode.right - node.rightNode.left + 1) * node.lazyAdd;
            node.rightNode.lazyAdd += node.lazyAdd;
        }
        node.lazyAdd = 0L;
    }
}
