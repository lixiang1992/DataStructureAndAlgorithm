package blancetree;

/**
 * 带rank的avl树
 * @param <T>
 */
public class AvlTree<T extends Comparable<T>> {

    private TreeNode<T> root;

    private static final int ALLOWED_IMBALANCE = 1;

    private static class TreeNode<T> {
        T value;
        int height;

        long count;
        long size;

        TreeNode<T> left;
        TreeNode<T> right;

        public TreeNode(T value) {
            this(value,1);
        }

        public TreeNode(T value,long cnt) {
            this.value = value;
            this.height = 0;
            this.size = cnt;
            this.count = cnt;
        }
    }

    private int height(TreeNode<T> node) {
        return node == null ? -1 : node.height;
    }

    private long getSize(TreeNode<T> node) {
        return node == null ? 0 : node.size;
    }

    public void clear() {
        root = null;
    }

    public boolean isEmpty() {
        return getSize() == 0L;
    }

    public long getSize(){
        return getSize(root);
    }

    public T first() {
        TreeNode<T> node = root;
        if(node != null) {
            while(node.left != null) {
                node = node.left;
            }
        }
        return node == null ? null : node.value;
    }

    public T last() {
        TreeNode<T> node = root;
        if(node != null) {
            while(node.right != null) {
                node = node.right;
            }
        }
        return node == null ? null : node.value;
    }

    public T lower(T x) {
        T res = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if(cmp <= 0) {
                node = node.left;
            } else {
                res = node.value;
                node = node.right;
            }
        }
        return res;
    }

    public T floor(T x) {
        T res = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if(cmp == 0) {
                return x;
            }
            if(cmp < 0) {
                node = node.left;
            } else {
                res = node.value;
                node = node.right;
            }
        }
        return res;
    }

    public T higher(T x) {
        T res = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if(cmp >= 0) {
                node = node.right;
            } else {
                res = node.value;
                node = node.left;
            }
        }
        return res;
    }

    public T ceiling(T x) {
        T res = null;
        TreeNode<T> node = root;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if(cmp == 0) {
                return x;
            }
            if(cmp > 0) {
                node = node.right;
            } else {
                res = node.value;
                node = node.left;
            }
        }
        return res;
    }

    public void insert(T x) {
        insert(x,1);
    }

    public void insert(T x,long cnt) {
        root = insert(root,x,cnt);
    }

    private TreeNode<T> insert(TreeNode<T> root,T x,long cnt) {
        if(root == null) {
            return new TreeNode<>(x,cnt);
        }
        root.size += cnt;
        int cmp = x.compareTo(root.value);
        if(cmp < 0) {
            root.left = insert(root.left,x,cnt);
        } else if(cmp > 0) {
            root.right = insert(root.right,x,cnt);
        } else {
            root.count += cnt;
        }
        return balance(root);
    }

    public void delete(T x) {
        delete(x,1);
    }

    public void delete(T x,int cnt) {
        root = delete(root,x,cnt);
    }

    private TreeNode<T> delete(TreeNode<T> root,T x,int cnt) {
        if(root == null) {
            return null;
        }
        int cmp = x.compareTo(root.value);
        if(cmp < 0) {
            root.left = delete(root.left,x,cnt);
        } else if(cmp > 0){
            root.right = delete(root.right,x,cnt);
        } else {
            if(root.count > cnt) {
                root.size -= cnt;
                root.count -= cnt;
                return root;
            }
            if(root.left == null || root.right == null) {
                root.size = 0;
                root.count = 0;
                root = root.left == null ? root.right : root.left;
                return balance(root);
            } else {
                // 找当前节点的后继
                TreeNode<T> rightMin = findMin(root.right);
                // 属性替换
                root.value = rightMin.value;
                root.count = rightMin.count;
                // 后继节点变为cnt个
                rightMin.count = cnt;
                // 去删除后继
                root.right = delete(root.right,root.value,cnt);
            }
        }
        root.size = getSize(root.left) + getSize(root.right) + root.count;
        return balance(root);
    }

    private TreeNode<T> balance(TreeNode<T> root) {
        if(root == null) {
            return null;
        }
        if(height(root.left) - height(root.right) > ALLOWED_IMBALANCE) {
            if(height(root.left.left) < height(root.left.right)) {
                root.left = leftRotate(root.left);
            }
            root = rightRotate(root);
        } else if(height(root.right) - height(root.left) > ALLOWED_IMBALANCE) {
            if(height(root.right.right) < height(root.right.left)) {
                root.right = rightRotate(root.right);
            }
            root = leftRotate(root);
        }
        root.height = Math.max(height(root.left),height(root.right)) + 1;
        return root;
    }

    public long[] rank(T x) {
        TreeNode<T> node = root;
        long ans = 0L;
        while (node != null) {
            int cmp = x.compareTo(node.value);
            if (cmp < 0) {
                node = node.left;
            } else {
                ans += getSize(node.left) + node.count;
                if (cmp == 0) {
                    return new long[]{ans - node.count + 1, ans};
                }
                node = node.right;
            }
        }
        return new long[]{-1L,-1L};
    }

    // 返回排名为k的元素(从小到大)
    public T kth(long k) {
        return kth(root, k);
    }

    private T kth(TreeNode<T> root, long k) {
        if (root == null) {
            return null;
        }
        long leftRank = getSize(root.left);
        long curRank = leftRank + root.count;
        if (k <= leftRank) {
            return kth(root.left, k);
        } else if (k <= curRank) {
            return root.value;
        } else {
            return kth(root.right, k - curRank);
        }
    }

    // 返回排名为k的元素(从大到小)
    public T reverseKth(long k) {
        return reverseKth(root, k);
    }

    private T reverseKth(TreeNode<T> root, long k) {
        if (root == null) {
            return null;
        }
        long rightRank = getSize(root.right);
        long curRank = rightRank + root.count;
        if (k <= rightRank) {
            return reverseKth(root.right, k);
        } else if (k <= curRank) {
            return root.value;
        } else {
            return reverseKth(root.left, k - curRank);
        }
    }

    private TreeNode<T> leftRotate(TreeNode<T> node) {
        long preSize = node.size;
        long curSize = getSize(node.left) + getSize(node.right.left) + node.count;
        TreeNode<T> root = node.right;
        node.right = root.left;
        root.left = node;
        node.size = curSize;
        root.size = preSize;
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        root.height = Math.max(height(root.right),height(node)) + 1;
        return root;
    }

    private TreeNode<T> rightRotate(TreeNode<T> node) {
        long preSize = node.size;
        long curSize = getSize(node.right) + getSize(node.left.right) + node.count;
        TreeNode<T> root = node.left;
        node.left = root.right;
        root.right = node;
        node.size = curSize;
        root.size = preSize;
        node.height = Math.max(height(node.left),height(node.right)) + 1;
        root.height = Math.max(height(root.left),height(node)) + 1;
        return root;
    }

    public boolean contains(T value) {
        if (root == null) {
            return false;
        }
        TreeNode<T> node = root;
        int cmp;
        while(node != null) {
            cmp = value.compareTo(node.value);
            if(cmp == 0) {
                return true;
            } else if(cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return false;
    }

    private TreeNode<T> findMin(TreeNode<T> node) {
        if(node != null) {
            while(node.left != null) {
                node = node.left;
            }
        }
        return node;
    }

    private TreeNode<T> findMax(TreeNode<T> node) {
        if(node != null) {
            while(node.right != null) {
                node = node.right;
            }
        }
        return node;
    }
}
