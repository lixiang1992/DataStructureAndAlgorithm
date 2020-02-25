package rbtree;

/**
 * 红黑树的实现
 *
 * @param <K> 泛型Key
 */
public class RBTree<K extends Comparable<K>> {

    private RBTreeNode<K> root;// 红黑树根节点

    // Red-black mechanics

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    /**
     * 红黑树节点
     *
     * @param <K>
     */
    private static final class RBTreeNode<K extends Comparable<K>> {
        K key;// 键值
        RBTreeNode<K> left;
        RBTreeNode<K> right;
        RBTreeNode<K> parent;
        boolean color = BLACK;// 节点默认黑色

        RBTreeNode(K key) {
            this(key, null);
        }

        RBTreeNode(K key, RBTreeNode<K> parent) {
            this.key = key;
            this.parent = parent;
        }

        public K getKey() {
            return key;
        }

        @Override
        public int hashCode() {
            return key == null ? 0 : key.hashCode();
        }

    }

    //--以下设计这些方法避免大量的空指针判断

    /**
     * 关于节点的颜色
     * <p>
     * 空节点都是黑色
     *
     * @param node RBTNode
     * @return color
     */
    private boolean colorOf(RBTreeNode<K> node) {
        return node == null ? BLACK : node.color;
    }

    /**
     * 设置节点的颜色
     *
     * @param node RBTNode
     * @param color color
     */
    private void setColor(RBTreeNode<K> node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    /**
     * 关于节点的父亲
     *
     * @param node RBTNode
     * @return node.parent
     */
    private RBTreeNode<K> parentOf(RBTreeNode<K> node) {
        return node == null ? null : node.parent;
    }

    /**
     * 关于节点的左孩子
     *
     * @param node RBTNode
     * @return node.left
     */
    private RBTreeNode<K> leftOf(RBTreeNode<K> node) {
        return node == null ? null : node.left;
    }

    /**
     * 关于节点的右孩子
     *
     * @param node RBTNode
     * @return node.right
     */
    private RBTreeNode<K> rightOf(RBTreeNode<K> node) {
        return node == null ? null : node.right;
    }
    //--以上设计这些方法避免大量的空指针判断

    /**
     * 获取最小节点
     * <p>
     * 最左子树节点就是最小节点
     *
     * @return 最小RBTNode
     */
    public final RBTreeNode<K> getFristNode() {
        RBTreeNode<K> node = root;
        if (node != null) {
            while (node.left != null) {
                node = node.left;// 左孩子不空，一直往左子树迭代
            }
        }
        return node;
    }

    /**
     * 获取最大节点
     * <p>
     * 最右子树节点就是最大节点
     *
     * @return 最大RBTNode
     */
    public final RBTreeNode<K> getLastNode() {
        RBTreeNode<K> node = root;
        if (node != null) {
            while (node.right != null) {
                node = node.right;// 右孩子不空，一直往右子树迭代
            }
        }
        return node;
    }

    /**
     * 寻找当前节点的前驱节点
     *
     * @param node RBTNode
     * @return 当前node的前驱节点
     */
    RBTreeNode<K> predecessor(RBTreeNode<K> node) {
        if (node == null) {
            return null;
        }
        // 左子树不空，前驱节点为左子树的最右孩子
        if (node.left != null) {
            RBTreeNode<K> temp = node.left;
            while (temp.right != null) {
                temp = temp.right;
            }
            return temp;
        } else {
            // 左子树为空，找到node所在子树为第一个右孩子的父节点
            RBTreeNode<K> temp = node;
            RBTreeNode<K> p = temp.parent;
            while (p != null && temp == p.left) {
                // temp == p.left 表示 temp < p，则p一定在temp的后继中
                // temp往p移动，之后的temp>node是恒成立的
                // 只有temp == p.right了，才表示 parent < temp，则parent是node的前驱
                // 因为此时node没有左孩子,node就是p的直接后继
                temp = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 寻找当前节点的后继节点
     *
     * @param node RBTNode
     * @return 当前node的前驱节点
     */
    RBTreeNode<K> successor(RBTreeNode<K> node) {
        if (node == null) {
            return null;
        }
        if (node.right != null) {
            RBTreeNode<K> temp = node.right;
            while (temp.left != null) {
                temp = temp.left;
            }
            return temp;
        }else {
            RBTreeNode<K> temp = node;
            RBTreeNode<K> p = temp.parent;
            while (p != null && temp == p.right){
                temp = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 对当前节点进行左旋
     *
     * @param node RBTNode
     */
    private void rotateLeft(RBTreeNode<K> node) {
        if (node == null) {
            return;
        }
        // 左旋，node和right向左转
        RBTreeNode<K> right = node.right;
        // node和right左孩子建立父-右孩子关系
        node.right = right.left;
        if (right.left != null) {
            right.left.parent = node;
        }
        // node.parent和right建立父子关系
        right.parent = node.parent;
        if (node.parent == null) {
            root = right;
        } else if (node == node.parent.right) {
            node.parent.right = right;// right节点取代node在node.parent的孩子位置
        } else {
            node.parent.left = right;
        }
        // 交换node和right的父子关系
        right.left = node;
        node.parent = right;
    }

    /**
     * 对当前节点进行右旋
     *
     * @param node RBTNode
     */
    private void rotateRight(RBTreeNode<K> node) {
        if (node == null) {
            return;
        }
        // 右旋，node和left向右转
        RBTreeNode<K> left = node.left;
        // node和left右孩子建立父-左孩子关系
        node.left = left.right;
        if (left.right != null) {
            left.right.parent = node;
        }
        left.parent = node.parent;
        // node.parent和left建立父子关系
        if (node.parent == null) {// root节点的特殊处理
            root = left;// 父节点是空的，就是root
        } else if (node == node.parent.left) {
            node.parent.left = left;// left节点取代node在node.parent的孩子位置
        } else {
            node.parent.right = left;
        }
        // 交换node和left的父子关系
        left.right = node;
        node.parent = left;
    }

    /**
     * RBT新增节点
     *
     * @param key 新增的key
     */
    public void insertRBTNode(K key) {
        if (key == null) {
            return;// 需要compare的元素，都不能为空，简单点，不报错了
        }
        RBTreeNode<K> node = root;
        if (node == null) {
            root = new RBTreeNode<>(key);// 根节点为空，生成根节点
            return;
        }
        int cmp;
        RBTreeNode<K> parent;// 父节点标记插入的位置
        do {
            parent = node;// 父节点记录
            cmp = key.compareTo(node.key);
            // 比当前节点小，往左子树迭代，否则往右子树迭代
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return;// 相同的，不插入了，直接返回
            }
        } while (node != null);
        // 新节点生成
        RBTreeNode<K> newNode = new RBTreeNode<>(key);
        // 比父节点小，为左孩子，否则为右孩子
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        // 节点插入后的调整
        fixInsert(newNode);
//        fixAfterInsert(newNode);
    }

    /**
     * RBTNode插入后的调整
     * 这种写法易于理解，但是会存在大量的空指针问题
     *
     * @param node RBTNode
     */
    private void fixInsert(RBTreeNode<K> node) {
        node.color = RED;// 新插入的节点都是红色的
        // node不是root，且parent颜色为红，才要调整
        // 所有的调整都是基于node和parent为双红
        while (node != root && node.parent.color == RED) {
            // 父节点是祖父节点的左孩子
            if (node.parent == node.parent.parent.left) {
                RBTreeNode<K> uncle = node.parent.parent.right;
                // case1 : 叔叔节点是红色
                if (uncle.color == RED) {
                    // 父节点和叔叔节点颜色变黑
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    // 祖父节点颜色变红
                    node.parent.parent.color = RED;
                    // 关注节点变为祖父节点
                    node = node.parent.parent;
                    // 根据叔叔节点的颜色，决定是继续case1还是进入case2,case3
                } else {// 叔叔节点是黑色
                    // case2 : node是父节点的右孩子
                    if (node == node.parent.right) {
                        // 关注节点变为父节点
                        node = node.parent;
                        // 对父节点进行左旋，进入case3
                        rotateLeft(node);
                    }
                    // case3 : node是父节点的左孩子
                    // 父节点和祖父节点颜色互换，父节点一定红色，祖父节点一定黑色
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    // 围绕祖父节点右旋
                    rotateRight(node.parent.parent);
                    // 调整结束
                    break;
                }
            } else {
                // 父节点是祖父节点的右孩子
                RBTreeNode<K> uncle = node.parent.parent.left;
                // case1 : 叔叔节点是红色
                if (uncle.color == RED) {
                    // 父节点和叔叔节点颜色变黑
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    // 祖父节点颜色变红
                    node.parent.parent.color = RED;
                    // 关注节点变为祖父节点
                    node = node.parent.parent;
                    // 根据叔叔节点的颜色，决定是继续case1还是进入case2,case3
                } else {
                    // case2 : node是父节点的左孩子
                    if (node == node.parent.left) {
                        // 关注节点变为父节点
                        node = node.parent;
                        // 对父节点进行右旋，进入case3
                        rotateRight(node);
                    }
                    // case3 : node是父节点的左孩子
                    // 父节点和祖父节点颜色互换，父节点一定红色，祖父节点一定黑色
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    // 围绕祖父节点左旋
                    rotateLeft(node.parent.parent);
                    // 调整结束
                    break;
                }
            }
            root.color = BLACK;// root永远是黑色的
        }
    }

    /**
     * RBTNode插入后的调整
     * 安全写法
     *
     * @param node RBTNode
     */
    private void fixAfterInsert(RBTreeNode<K> node) {
        node.color = RED;// 新插入的节点都是红色的
        // node不是root，且parent颜色为红，才要调整
        // 所有的调整都是基于node和parent为双红
        while (node != null && node != root && node.parent.color == RED) {
            // 父节点是祖父节点的左孩子
            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
                // 获取叔叔节点
                RBTreeNode<K> uncle = rightOf(parentOf(parentOf(node)));
                // case1 : 叔叔节点是红色
                if (colorOf(uncle) == RED) {
                    // 父节点和叔叔节点颜色变黑
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    // 祖父节点颜色变红
                    setColor(parentOf(parentOf(node)), RED);
                    // 关注节点变为祖父节点
                    node = parentOf(parentOf(node));
                    // 根据叔叔节点的颜色，决定是继续case1还是进入case2,case3
                } else {// 叔叔节点是黑色
                    // case2 : node是父节点的右孩子
                    if (node == rightOf(parentOf(node))) {
                        // 关注节点变为父节点
                        node = parentOf(node);
                        // 对父节点进行左旋，进入case3
                        rotateLeft(node);
                    }
                    // case3 : node是父节点的左孩子
                    // 父节点和祖父节点颜色互换，父节点一定红色，祖父节点一定黑色
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    // 围绕祖父节点右旋
                    rotateRight(parentOf(parentOf(node)));
                    // 调整结束
                    break;
                }
            } else {
                // 父节点是祖父节点的右孩子
                // 获取叔叔节点
                RBTreeNode<K> uncle = leftOf(parentOf(parentOf(node)));
                // case1 : 叔叔节点是红色
                if (colorOf(uncle) == RED) {
                    // 父节点和叔叔节点颜色变黑
                    setColor(parentOf(node), BLACK);
                    setColor(uncle, BLACK);
                    // 祖父节点颜色变红
                    setColor(parentOf(parentOf(node)), RED);
                    // 关注节点变为祖父节点
                    node = parentOf(parentOf(node));
                    // 根据叔叔节点的颜色，决定是继续case1还是进入case2,case3
                } else {
                    // case2 : node是父节点的左孩子
                    if (node == leftOf(parentOf(node))) {
                        // 关注节点变为父节点
                        node = parentOf(node);
                        // 对父节点进行右旋，进入case3
                        rotateRight(node);
                    }
                    // case3 : node是父节点的左孩子
                    // 父节点和祖父节点颜色互换，父节点一定红色，祖父节点一定黑色
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    // 围绕祖父节点左旋
                    rotateLeft(parentOf(parentOf(node)));
                    // 调整结束
                    break;
                }
            }
        }
        root.color = BLACK;// root永远是黑色的
    }
}
