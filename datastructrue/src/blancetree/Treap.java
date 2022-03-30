package blancetree;

import java.util.Random;

/**
 * 树堆，满足平衡树的性质，还带上了rank的查询
 */
public class Treap {
    // 树节点
    private static class TreeNode {
        long value;
        int priority;
        int count;
        int size;// 以他为root的子树总节点个数
        TreeNode left;
        TreeNode right;

        TreeNode(long value, int priority) {
            this.value = value;
            this.priority = priority;
            this.count = 1;
            this.size = 1;
        }

        TreeNode leftRotate() {
            int preSize = size;
            int curSize = (left == null ? 0 : left.size) + (right.left == null ? 0 : right.left.size) + count;
            TreeNode root = right;
            right = root.left;
            root.left = this;
            this.size = curSize;
            root.size = preSize;
            return root;
        }

        TreeNode rightRotate() {
            int preSize = size;
            int curSize = (right == null ? 0 : right.size) + (left.right == null ? 0 : left.right.size) + count;
            TreeNode root = left;
            left = root.right;
            root.right = this;
            this.size = curSize;
            root.size = preSize;
            return root;
        }
    }

    private TreeNode root;

    private final Random random;

    public Treap() {
        this.random = new Random();
    }

    public int getSize() {
        return root == null ? 0 : root.size;
    }

    public void insert(long x) {
        root = insert(root, x);
    }

    private TreeNode insert(TreeNode root, long x) {
        if (root == null) {
            return new TreeNode(x, random.nextInt());
        }
        root.size++;
        if (x < root.value) {
            root.left = insert(root.left, x);
            if (root.left.priority > root.priority) {
                root = root.rightRotate();
            }
        } else if (x > root.value) {
            root.right = insert(root.right, x);
            if (root.right.priority > root.priority) {
                root = root.leftRotate();
            }
        } else {
            root.count++;
        }
        return root;
    }

    // 第一个小于等于x的数
    public long floor(long x) {
        long ret = Long.MIN_VALUE;
        TreeNode node = root;
        while (node != null) {
            if (node.value == x) {
                return x;
            } else if (node.value > x) {
                node = node.left;
            } else {
                ret = node.value;
                node = node.right;
            }
        }
        return ret;
    }

    //第一个小于x的数(从小到大排序)
    public long lower(long x) {
        long ret = Long.MIN_VALUE;
        TreeNode node = root;
        while (node != null) {
            if (node.value < x) {
                ret = node.value;
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return ret;
    }

    // 第一个大于等于x的数(从小到大排序)
    public long ceiling(long x) {
        long ret = Long.MAX_VALUE;
        TreeNode node = root;
        while (node != null) {
            if (node.value == x) {
                return x;
            } else if (node.value > x) {
                ret = node.value;
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return ret;
    }

    // 第一个大于x的数(从小到大排序)
    public long higher(long x) {
        long ret = Long.MAX_VALUE;
        TreeNode node = root;
        while (node != null) {
            if (node.value > x) {
                ret = node.value;
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return ret;
    }

    // 返回x的排名，从1开始。返回数组ret，ret[0]表示第一个x的rank，ret[1]表示最后一个x的rank。
    public int[] rank(long x) {
        TreeNode node = root;
        int ans = 0;
        while (node != null) {
            if (node.value > x) {
                node = node.left;
            } else {
                ans += (node.left == null ? 0 : node.left.size) + node.count;
                if (x == node.value) {
                    return new int[]{ans - node.count + 1, ans};
                }
                node = node.right;
            }
        }
        return new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
    }

    // 返回排名为k的元素(从小到大)
    public long kth(int k) {
        return kth(root, k);
    }

    private long kth(TreeNode root, int k) {
        if (root == null) {
            return Long.MAX_VALUE;
        }
        int leftRank = root.left == null ? 0 : root.left.size;
        int curRank = leftRank + root.count;
        if (k <= leftRank) {
            return kth(root.left, k);
        } else if (k <= curRank) {
            return root.value;
        } else {
            return kth(root.right, k - curRank);
        }
    }

    // 返回排名为k的元素(从大到小)
    public long reverseKth(int k) {
        return reverseKth(root, k);
    }

    public long reverseKth(TreeNode root, int k) {
        if (root == null) {
            return Long.MIN_VALUE;
        }
        int rightRank = root.right == null ? 0 : root.right.size;
        int curRank = rightRank + root.count;
        if (k <= rightRank) {
            return reverseKth(root.right, k);
        } else if (k <= curRank) {
            return root.value;
        } else {
            return reverseKth(root.left, k - curRank);
        }
    }

    public void delete(long val) {
        root = delete(root, val);
    }

    private TreeNode delete(TreeNode root, long value) {
        if (root == null) {
            return null;
        }
        if (root.value > value) {
            root.left = delete(root.left, value);
        } else if (root.value < value) {
            root.right = delete(root.right, value);
        } else {
            if (root.count > 1) {
                root.count--;
                root.size--;
                return root;
            }
            if (root.left == null || root.right == null) {
                root.size--;
                root.count--;
                return root.left == null ? root.right : root.left;
            } else if (root.left.priority > root.right.priority) {
                root = root.rightRotate();
                root.right = delete(root.right, value);
            } else {
                root = root.leftRotate();
                root.left = delete(root.left, value);
            }
        }
        root.size = (root.left == null ? 0 : root.left.size) + (root.right == null ? 0 : root.right.size) + root.count;
        return root;
    }

    public boolean contains(long value) {
        return contains(root, value);
    }

    private boolean contains(TreeNode root, long value) {
        if (root == null) {
            return false;
        }
        if (root.value == value) {
            return true;
        } else if (root.value > value) {
            return contains(root.left, value);
        } else {
            return contains(root.right, value);
        }
    }
}