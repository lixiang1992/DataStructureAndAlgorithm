package blancetree;

import java.util.Random;

/**
 * 树堆，满足平衡树的性质，还带上了rank的查询
 */
public class Treap<T extends Comparable<T>> {

    private static class TreeNode<T> {
        T value;
        int priority;
        int count;// 这个节点的数量
        int size;// 以他为root的子树总节点个数
        TreeNode<T> left;
        TreeNode<T> right;

        TreeNode(T value, int priority) {
            this.value = value;
            this.priority = priority;
            this.count = 1;
            this.size = 1;
        }

        TreeNode<T> leftRotate() {
            int preSize = size;
            int curSize = (left == null ? 0 : left.size) + (right.left == null ? 0 : right.left.size) + count;
            TreeNode<T> root = right;
            right = root.left;
            root.left = this;
            this.size = curSize;
            root.size = preSize;
            return root;
        }

        TreeNode<T> rightRotate() {
            int preSize = size;
            int curSize = (right == null ? 0 : right.size) + (left.right == null ? 0 : left.right.size) + count;
            TreeNode<T> root = left;
            left = root.right;
            root.right = this;
            this.size = curSize;
            root.size = preSize;
            return root;
        }

    }

    private TreeNode<T> root;

    private final Random random;

    public Treap() {
        this.random = new Random();
    }

    public int getSize() {
        return root == null ? 0 : root.size;
    }

    public T first() {
        TreeNode<T> node = root;
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node.value;
    }

    public T last() {
        TreeNode<T> node = root;
        if (node == null) {
            return null;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node.value;
    }

    public void insert(T x) {
        root = insert(root, x);
    }

    private TreeNode<T> insert(TreeNode<T> root, T x) {
        if (root == null) {
            return new TreeNode<>(x, random.nextInt());
        }
        root.size++;
        int cmp = x.compareTo(root.value);
        if (cmp < 0) {
            root.left = insert(root.left, x);
            if (root.left.priority > root.priority) {
                root = root.rightRotate();
            }
        } else if (cmp > 0) {
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
    public T floor(T x) {
        T ret = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (cmp == 0) {
                return x;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                ret = node.value;
                node = node.right;
            }
        }
        return ret;
    }

    //第一个小于x的数(从小到大排序)
    public T lower(T x) {
        T ret = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (cmp > 0) {
                ret = node.value;
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return ret;
    }

    // 第一个大于等于x的数(从小到大排序)
    public T ceiling(T x) {
        T ret = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (node.value == x) {
                return x;
            } else if (cmp < 0) {
                ret = node.value;
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return ret;
    }

    // 第一个大于x的数(从小到大排序)
    public T higher(T x) {
        T ret = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (cmp < 0) {
                ret = node.value;
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return ret;
    }

    // 返回x的排名，从1开始。返回数组ret，ret[0]表示第一个x的rank，ret[1]表示最后一个x的rank。
    public int[] rank(T x) {
        TreeNode<T> node = root;
        int ans = 0;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (cmp < 0) {
                node = node.left;
            } else {
                ans += (node.left == null ? 0 : node.left.size) + node.count;
                if (cmp == 0) {
                    return new int[]{ans - node.count + 1, ans};
                }
                node = node.right;
            }
        }
        return new int[]{0,0};
    }

    // 返回排名为k的元素(从小到大)
    public T kth(int k) {
        return kth(root, k);
    }

    private T kth(TreeNode<T> root, int k) {
        if (root == null) {
            return null;
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
    public T reverseKth(int k) {
        return reverseKth(root, k);
    }

    private T reverseKth(TreeNode<T> root, int k) {
        if (root == null) {
            return null;
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

    public void delete(T val) {
        root = delete(root, val);
    }

    private TreeNode<T> delete(TreeNode<T> root, T value) {
        if (root == null) {
            return null;
        }
        int cmp = value.compareTo(root.value);
        if (cmp < 0) {
            root.left = delete(root.left, value);
        } else if (cmp > 0) {
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

    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(TreeNode<T> root, T value) {
        if (root == null) {
            return false;
        }
        int cmp = value.compareTo(root.value);
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return contains(root.left, value);
        } else {
            return contains(root.right, value);
        }
    }
}