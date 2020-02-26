package rbtree;

/**
 * 红黑树的实现
 * <p>
 * 2-3-4树是四阶的 B树(Balance Tree)，它的结构有以下限制：
 * <p>
 * 所有叶子节点都拥有相同的深度。
 * 节点只能是 2-节点、3-节点、4-节点之一。
 * <p>
 * 2-节点：包含 1 个元素的节点，有 2 个子节点；
 * 3-节点：包含 2 个元素的节点，有 3 个子节点；
 * 4-节点：包含 3 个元素的节点，有 4 个子节点；
 * 元素始终保持排序顺序，整体上保持二叉查找树的性质，
 * 即父结点大于左子结点，小于右子结点；而且结点有多个元素时，每个元素必须大于它左边的和它的左子树中元素。
 * <p>
 * 红黑树就是2-3-4树的一种等同实现
 * <p>
 * 红黑树和 2-3-4树的结点添加和删除都有一个基本规则：避免子树高度变化。
 * 因为无论是 2-3-4树还是红黑树，一旦子树高度有变动，势必会影响其他子树进行调整，所以我们在插入和删除结点时尽量通过子树内部调整来达到平衡。
 * 2-3-4树实现平衡是通过结点的旋转和结点元素数变化，红黑树是通过结点旋转和变色。
 * <p>
 * 插入和删除方法上来对照着 2-3-4树说一下红黑树结点的添加和删除
 *
 * @param <T> 泛型Key
 */
public class RBTree<T extends Comparable<T>> {

    private RBTreeNode<T> root;// 红黑树根节点

    // Red-black mechanics

    private static final boolean RED = false;
    private static final boolean BLACK = true;

    /**
     * 红黑树节点
     *
     * @param <T>
     */
    private static final class RBTreeNode<T extends Comparable<T>> {
        T key;// 键值
        RBTreeNode<T> left;
        RBTreeNode<T> right;
        RBTreeNode<T> parent;
        boolean color = BLACK;// 节点默认黑色

        RBTreeNode(T key) {
            this(key, null);
        }

        RBTreeNode(T key, RBTreeNode<T> parent) {
            this.key = key;
            this.parent = parent;
        }

        public T getKey() {
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
    private boolean colorOf(RBTreeNode<T> node) {
        return node == null ? BLACK : node.color;
    }

    /**
     * 设置节点的颜色
     *
     * @param node  RBTNode
     * @param color color
     */
    private void setColor(RBTreeNode<T> node, boolean color) {
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
    private RBTreeNode<T> parentOf(RBTreeNode<T> node) {
        return node == null ? null : node.parent;
    }

    /**
     * 关于节点的左孩子
     *
     * @param node RBTNode
     * @return node.left
     */
    private RBTreeNode<T> leftOf(RBTreeNode<T> node) {
        return node == null ? null : node.left;
    }

    /**
     * 关于节点的右孩子
     *
     * @param node RBTNode
     * @return node.right
     */
    private RBTreeNode<T> rightOf(RBTreeNode<T> node) {
        return node == null ? null : node.right;
    }

    /**
     * 关于节点的key
     *
     * @param node RBTNode
     * @return key
     */
    private T keyOf(RBTreeNode<T> node) {
        return node == null ? null : node.key;
    }
    //--以上设计这些方法避免大量的空指针判断

    /**
     * 获取最小的key
     *
     * @return 最小key
     */
    public final T getFirstKey() {
        RBTreeNode<T> node = getFirstNode();
        return keyOf(node);
    }

    /**
     * 获取最小节点
     * <p>
     * 最左子树节点就是最小节点
     *
     * @return 最小RBTNode
     */
    private RBTreeNode<T> getFirstNode() {
        RBTreeNode<T> node = root;
        if (node != null) {
            while (node.left != null) {
                node = node.left;// 左孩子不空，一直往左子树迭代
            }
        }
        return node;
    }

    /**
     * 获取最大的key
     *
     * @return 最大key
     */
    public final T getLastKey() {
        RBTreeNode<T> node = getLastNode();
        return keyOf(node);
    }

    /**
     * 获取最大节点
     * <p>
     * 最右子树节点就是最大节点
     *
     * @return 最大RBTNode
     */
    private RBTreeNode<T> getLastNode() {
        RBTreeNode<T> node = root;
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
    RBTreeNode<T> predecessor(RBTreeNode<T> node) {
        if (node == null) {
            return null;
        }
        // 左子树不空，前驱节点为左子树的最右孩子
        if (node.left != null) {
            RBTreeNode<T> temp = node.left;
            while (temp.right != null) {
                temp = temp.right;
            }
            return temp;
        } else {
            // 左子树为空，找到node所在子树为第一个右孩子的父节点
            RBTreeNode<T> temp = node;
            RBTreeNode<T> p = temp.parent;
            while (p != null && temp == p.left) {
                // temp == p.left 表示 temp < p，则p一定在temp的后继中
                // temp往p移动，之后的temp>node是恒成立的，因为node一直在temp的左子树中
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
    RBTreeNode<T> successor(RBTreeNode<T> node) {
        if (node == null) {
            return null;
        }
        // 右子树不空，后继节点为右子树的最左孩子
        if (node.right != null) {
            RBTreeNode<T> temp = node.right;
            while (temp.left != null) {
                temp = temp.left;
            }
            return temp;
        } else {
            // 右子树为空，找到node所在子树为第一个左孩子的父节点
            RBTreeNode<T> temp = node;
            RBTreeNode<T> p = temp.parent;
            while (p != null && temp == p.right) {
                // temp == p.right 表示 temp > p，则p一定是在temp的前驱中
                // temp往p移动，之后的temp < node是恒成立的，因为node一直在temp的右子树中
                // 只有temp == p.left了，才表示 parent > temp，则parent是node的后继
                // 因为此时node没有右孩子,node就是p的直接前驱
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
    private void rotateLeft(RBTreeNode<T> node) {
        if (node == null) {
            return;
        }
        // 左旋，node和right向左转
        RBTreeNode<T> right = node.right;
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
    private void rotateRight(RBTreeNode<T> node) {
        if (node == null) {
            return;
        }
        // 右旋，node和left向右转
        RBTreeNode<T> left = node.left;
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
     * RBT查询元素（这个好像没什么意义）
     *
     * @param key 查询的key
     * @return T.key
     */
    public T searchRBTkey(T key) {
        RBTreeNode<T> node = getRBTNode(key);
        return keyOf(node);
    }

    /**
     * 通过key获取RBTNode
     *
     * @param key 查询的key
     * @return RBTNode
     */
    private RBTreeNode<T> getRBTNode(T key) {
        if (key == null) {
            return null;
        }
        // 二叉搜索树查找元素的方法
        RBTreeNode<T> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * RBT新增节点
     *
     * <p>
     * 2-3-4树中结点添加需要遵守以下规则：
     * <p>
     * 插入都是向最下面一层插入；
     * 升元：将插入结点由 2-结点升级成 3-结点，或由 3-结点升级成 4-结点；
     * 向 4-结点插入元素后，需要将中间元素提到父结点升元，原结点变成两个 2-结点，再把元素插入 2-结点中，
     * 如果父结点也是 4-结点，则递归向上层升元，至到根结点后将树高加1；
     * 而将这些规则对应到红黑树里，就是：
     * <p>
     * 新插入的结点颜色为 红 色，这样才可能不会对红黑树的高度产生影响。
     * 2-结点对应红黑树中的单个黑色结点，插入时直接成功（对应 2-结点升元）。
     * 3-结点对应红黑树中的 黑+红 子树，插入后将其修复成 红+黑+红 子树（对应 3-结点升元）；
     * 4-结点对应红黑树中的 红+黑+红 子树，插入后将其修复成 红色祖父+黑色父叔+红色孩子 子树，
     *  然后再把 祖父结点 当成新插入的红色结点 递归向上层修复，直至修复成功或遇到 root 结点；
     *
     * @param key 新增的key
     */
    public void insertRBTNode(T key) {
        if (key == null) {
            return;// 需要compare的元素，都不能为空，简单点，不报错了
        }
        RBTreeNode<T> node = root;
        if (node == null) {
            root = new RBTreeNode<>(key);// 根节点为空，生成根节点
            return;
        }
        int cmp;
        RBTreeNode<T> parent;// 父节点标记插入的位置
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
        RBTreeNode<T> newNode = new RBTreeNode<>(key);
        // 比父节点小，为左孩子，否则为右孩子
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        // 节点插入后的调整
//        fixInsert(newNode);
        fixAfterInsert(newNode);
    }

    /**
     * RBTNode插入后的调整
     * 这种写法易于理解，但是会存在大量的空指针问题，操作的思想和文字说明，请看fixAfterInsert
     *
     * @param node RBTNode
     */
    private void fixInsert(RBTreeNode<T> node) {
        node.color = RED;// 新插入的节点都是红色的
        // node不是root，且parent颜色为红，才要调整
        // 所有的调整都是基于node和parent为双红
        while (node != root && node.parent.color == RED) {
            // 父节点是祖父节点的左孩子
            if (node.parent == node.parent.parent.left) {
                RBTreeNode<T> uncle = node.parent.parent.right;
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
                    node.parent.color = BLACK;// 把父节点变黑
                    node.parent.parent.color = RED;// 把祖父节点变红
                    // 围绕祖父节点右旋
                    rotateRight(node.parent.parent);
                    // 调整结束
                    // 这里写不写break都一样，因为此时关注节点的父节点已经变成了黑色，不再满足调整的两红节点互连的情况
                    break;
                }
            } else {// symmetric
                // 父节点是祖父节点的右孩子
                RBTreeNode<T> uncle = node.parent.parent.left;
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
                    node.parent.color = BLACK;// 把父节点变黑
                    node.parent.parent.color = RED;// 把祖父节点变红
                    // 围绕祖父节点左旋
                    rotateLeft(node.parent.parent);
                    // 调整结束
                    // 这里写不写break都一样，因为此时关注节点的父节点已经变成了黑色，不再满足调整的两红节点互连的情况
                    break;
                }
            }
            root.color = BLACK;// root永远是黑色的
        }
    }

    /**
     * RBTNode插入后的调整
     * <p>
     * 插入节点的核心思想：新增的红节点，如果违反了红红不相邻，就要找兄弟树，往兄弟树转移。
     * 兄弟树如果是黑的，就把红节点转移到兄弟树。
     * 兄弟如果也是红的，就把红节点往上（祖父）移动，继续找兄弟树，直到兄弟树为黑树。
     * 如果找到根节点还没找到，就把根节点变黑，树的高度+1。
     * <p>
     * 类比2-3-4树来说
     * 2-3-4树中结点添加需要遵守以下规则：
     * <p>
     * 插入都是向最下面一层插入；
     * 升元：将插入结点由 2-结点升级成 3-结点，或由 3-结点升级成 4-结点；
     * 向 4-结点插入元素后，需要将中间元素提到父结点升元，原结点变成两个 2-结点，再把元素插入 2-结点中，
     * 如果父结点也是 4-结点，则递归向上层升元，至到根结点后将树高+1；
     *
     * 而将这些规则对应到红黑树里，就是：
     * <p>
     * 新插入的结点颜色为红色，这样才可能不会对红黑树的高度产生影响。
     * 2-结点对应红黑树中的单个黑色结点，插入时直接成功（对应 2-结点升元）。
     * 3-结点对应红黑树中的黑+红子树，插入后将其修复成 红+黑+红 子树（对应 3-结点升元）；
     * 4-结点对应红黑树中的红+黑+红子树，插入后将其修复成红色祖父+黑色父叔+红色孩子子树，
     * 然后再把祖父结点当成新插入的红色结点递归向上层修复，直至修复成功或遇到 root 结点；
     * <p>
     * 安全写法
     *
     * @param node RBTNode
     */
    private void fixAfterInsert(RBTreeNode<T> node) {
        node.color = RED;// 新插入的节点都是红色的
        // node不是root，且parent颜色为红，才要调整
        // 所有的调整都是基于node和parent为双红
        while (node != null && node != root && node.parent.color == RED) {
            // 父节点是祖父节点的左孩子
            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) {
                // 获取叔叔节点
                RBTreeNode<T> uncle = rightOf(parentOf(parentOf(node)));
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
                    setColor(parentOf(node), BLACK);// 把父节点变黑
                    setColor(parentOf(parentOf(node)), RED);// 把祖父节点变红
                    // 围绕祖父节点右旋
                    rotateRight(parentOf(parentOf(node)));
                    // 调整结束
                    // 这里写不写break都一样，因为此时关注节点的父节点已经变成了黑色，不再满足调整的两红节点互连的情况
                    break;
                }
            } else {// symmetric
                // 父节点是祖父节点的右孩子
                // 获取叔叔节点
                RBTreeNode<T> uncle = leftOf(parentOf(parentOf(node)));
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
                    setColor(parentOf(node), BLACK);// 把父节点变黑
                    setColor(parentOf(parentOf(node)), RED);// 把祖父节点变红
                    // 围绕祖父节点左旋
                    rotateLeft(parentOf(parentOf(node)));
                    // 调整结束
                    // 这里写不写break都一样，因为此时关注节点的父节点已经变成了黑色，不再满足调整的两红节点互连的情况
                    break;
                }
            }
        }
        root.color = BLACK;// root永远是黑色的
    }

    /**
     * 红黑树的删除
     * <p>
     * 删除的核心思想：
     * 主要说一下黑节点被删除，替换节点也是黑节点的情况，首先不能破坏黑色高度，也不能造成红红相邻
     * 平衡方式就是从兄弟树中“借”红色节点（一般就是兄弟节点和兄弟的直接孩子节点）过来，填充为黑色节点。
     * 如果兄弟树没有红色节点，就把黑色节点上移（父亲节点），继续找兄弟节点的红色节点。
     * 如果一直找到了根节点还是找不到可以借的红色节点，就抛弃这个黑色节点，整棵树高度-1。
     *
     * <p>
     * 红黑树的删除要比插入要复杂一些，我们还是类比 2-3-4树来讲：
     * <p>
     * 查找最近的叶子结点中的元素替代被删除元素，删除替代元素后，从替代元素所处叶子结点开始处理；
     * 降元：4-结点变 3-结点，3-结点变 2-结点；
     * 2-结点中只有一个元素，所以借兄弟结点中的元素来补充删除后的造成的空结点；
     * 当兄弟结点中也没有多个元素可以补充时，尝试将父结点降元，失败时向上递归，至到子树降元成功或到 root 结点树高减1；
     * 将这些规则对应到红黑树中即：
     * <p>
     * 查找离当前结点最近的叶子结点作为 替代结点
     * （左子树的最右结点或右子树的最左结点都能保证替换后保证二叉查找树的结点的排序性质，叶子结点的替代结点是自身）
     *
     * 替换掉被删除结点，从替代的叶子结点向上递归修复；
     * 替代结点颜色为红色（对应 2-3-4树中 4-结点或 3-结点）时删除子结点直接成功；
     * <p>
     * 替代结点为黑色（对应 2-3-4树中 2-结点）时，意味着替代结点所在的子树会降一层，需要依次检验以下三项，以恢复子树高度：
     * <p>
     * 兄弟结点的子结点中有红色结点（兄弟结点对应 3-结点或 4-结点）能够“借用”，旋转过来后修正颜色；
     * 父结点是红色结点（父结点对应 3-结点或 4-结点，可以降元）时，将父结点变黑色，自身和兄弟结点变红色后删除；
     * 父结点和兄弟结点都是黑色时，将子树降一层后把父结点当作替代结点递归向上处理。
     *
     * @param key 删除的key
     */
    public void deleteRBTNode(T key) {
        RBTreeNode<T> node = getRBTNode(key);
        if (node == null) {
            return;// key都不存在，就不管了
        }
        // 如果node有两个孩子节点，用他的后继节点来代替他，当然用前驱也可以，是一样的
        // 为什么先判断两个孩子节点呢，因为两个孩子节点会转化为只有一个孩子节点情况，将问题归纳为一种情况
        if (node.left != null && node.right != null) {
            // 找到后继节点
            RBTreeNode<T> successorNode = successor(node);
            // 后继节点内容替换当前节点内容
            node.key = successorNode.key;
            // 删除节点指针指向node的后继节点
            node = successorNode;
        }
        // 此时node只有0个或者1个孩子节点，左孩子或者右孩子来取代node的位置
        RBTreeNode<T> replaceNode = (node.left != null ? node.left : node.right);

        if (replaceNode != null) {
            // 存在孩子节点的情况
            RBTreeNode<T> parent = node.parent;
            replaceNode.parent = parent;
            if (parent == null) {// 删除的是根节点
                root = replaceNode;
            } else if (node == parent.left) {
                parent.left = replaceNode;
            } else {
                parent.right = replaceNode;
            }
            // Null out links so they are OK to use by fixAfterDeletion.
            node.parent = node.left = node.right = null;

            // 黑色节点需要调整
            if (node.color == BLACK) {
                // 调整代替的节点
                fixAfterDeletion(replaceNode);
            }
        } else {
            // 没有孩子节点的情况
            RBTreeNode<T> parent = node.parent;
            if (parent == null) {// 删除的是根节点
                root = null;// 根节点没了，也不用管后面了
                return;
            }
            // 删除的如果是黑节点，还得做调整
            if (node.color == BLACK) {
                fixAfterDeletion(node);
            }
            // 这时候父节点要重新获取，因为经过一系列调整和旋转后，node.parent很可能发生了变化
            parent = node.parent;
            // node父指针移除
            node.parent = null;
            if (parent != null) {
                if (node == parent.left) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
            }
        }
    }

    /**
     * RBTNode删除后的调整
     * <p>
     * 替代结点为黑色（对应 2-3-4树中 2-结点）时，意味着替代结点所在的子树会降一层，需要依次检验以下三项，以恢复子树高度：
     * <p>
     * 兄弟结点的子结点中有红色结点（兄弟结点对应 3-结点或 4-结点）能够“借用”，旋转过来后修正颜色，这时候兄弟节点一定是黑色的；
     * 父结点是红色结点（父结点对应 3-结点或 4-结点，可以降元）时，将父结点变黑色，自身和兄弟结点变红色后删除；
     * 父结点和兄弟结点都是黑色时，将子树降一层后把父结点当作替代结点递归向上处理。对应case2的情况
     * 安全写法
     *
     * @param node RBTNode
     */
    private void fixAfterDeletion(RBTreeNode<T> node) {
        // 如果node.color是红色，那么只需要把node的颜色设置成黑色就可以了
        // 所以，这个方法最后一步就是把node.color设置为黑色
        while (node != root && colorOf(node) == BLACK) {
            if (node == leftOf(parentOf(node))) {
                // 获取兄弟节点
                RBTreeNode<T> brother = rightOf(parentOf(node));
                // case1 ：兄弟是红色节点
                if (colorOf(brother) == RED) {
                    // 这个时候直接把兄弟节点借过来了，但是又要保持兄弟树的黑色高度稳定
                    // 就交换父节点和兄弟节点的颜色（兄弟是红，父节点肯定是黑色了），把父节点借过来了
                    setColor(brother, BLACK);// 兄弟节点变黑
                    setColor(parentOf(node), RED);// 父节点变红
                    rotateLeft(parentOf(node));// 父节点左旋
                    brother = rightOf(parentOf(node));// 重新获取兄弟节点
                    // 这个时候兄弟节点肯定是黑色了，因为之前的兄弟节点是红色的，那么他的子节点肯定是黑色的
                    // 这时候不是简单把转过来的父节点变红就可以了，
                    // 因为node是x-1(x表示原高度)，而兄弟节点（红色）转过来的孩子树的高度还是x（画图理解）
                    // 所以本身高度就不统一，所以要进入接下来的case2；(case3或case4)
                }
                // case2 : 兄弟节点是黑色，并且两个孩子也是黑色
                // 如果兄弟节点是黑色，那么node节点高度是x-1，兄弟节点孩子节点的高度也是x-1了（因为要去掉黑色兄弟节点）
                if (colorOf(leftOf(brother)) == BLACK && colorOf(rightOf(brother)) == BLACK) {
                    // 兄弟子树高度-1，往根节点递归处理，和插入不同的是，这里是到父节点，不是祖父
                    setColor(brother, RED);// 兄弟节点变成红色，兄弟子树高度-1
                    node = parentOf(node);// 关注节点变成父节点
                    // 这时候如果node节点（原父节点）是红色了，就跳出循环了，
                    // 再把node节点（原父节点）变黑，兄弟子树高度+1，原来自己子树高度也+1
                    // 新node（原父节点）是黑色，需要继续循环
                } else {
                    // case3 : 兄弟节点是黑色；兄弟节点的左孩子是红色，右孩子是黑色的
                    if (colorOf(rightOf(brother)) == BLACK) {
                        setColor(leftOf(brother), BLACK);// 兄弟左孩子变黑
                        setColor(brother, RED);// 兄弟变红
                        rotateRight(brother);// 兄弟节点右旋
                        brother = rightOf(parentOf(node));// 重新获取兄弟节点
                        // 要把红节点转过去，就要先右旋，在左旋，为了保证旋转后还是红黑树，就要交换一下兄弟左孩子和兄弟节点的颜色了
                    }
                    // case4 : 兄弟节点是黑色；兄弟节点的右孩子是红色的，x的兄弟节点的左孩子任意颜色
                    setColor(brother, colorOf(parentOf(node)));// 父节点的颜色，赋值给兄弟节点
                    setColor(parentOf(node), BLACK);// 父节点设置为黑色，准备通过旋转把这个黑色节点借过来
                    // 为什么染黑兄弟的右孩子，个人认为，真正左旋过去的，是兄弟节点的黑色，兄弟节点本身的高度-1。
                    // 为了维持这个高度平衡，就把兄弟右孩子变成黑色，
                    // 因为左旋后，右孩子的位置，就是原来兄弟节点位置，黑色高度恢复（+1-1抵消）
                    setColor(rightOf(brother), BLACK);// 兄弟孩子的右节点设置为黑色，
                    rotateLeft(parentOf(node));// 父节点左旋，这个时候就把节点借过来了，而且染黑了
                    // 这一步后，删除调整已经结束，根节点依旧要保持黑色的属性，把node执行root，与之前的情况合并处理
                    node = root;
                }
            } else { // symmetric，对称方同理，这里就不写了
                // 获取兄弟节点
                RBTreeNode<T> brother = leftOf(parentOf(node));
                // case1 ：兄弟是红色节点
                if (colorOf(brother) == RED) {
                    setColor(brother, BLACK);// 兄弟节点变黑
                    setColor(parentOf(node), RED);// 父节点变红
                    rotateRight(parentOf(node));// 父节点右旋
                    // 这个时候兄弟节点肯定是黑色了，因为之前的兄弟节点是红色的，那么他的子节点肯定是黑色的
                    brother = leftOf(parentOf(node));// 重新获取兄弟节点
                }
                // case2 : 兄弟节点是黑色，并且两个孩子也是黑色
                if (colorOf(leftOf(brother)) == BLACK && colorOf(rightOf(brother)) == BLACK) {
                    setColor(brother, RED);// 兄弟节点变成红色
                    node = parentOf(node);// 关注节点变成父节点
                } else {
                    // case3 : 兄弟节点是黑色；兄弟节点的右孩子是红色，做孩子是黑色的
                    if (colorOf(leftOf(brother)) == BLACK) {
                        setColor(rightOf(brother), BLACK);// 兄弟右孩子变黑
                        setColor(brother, RED);// 兄弟变红
                        rotateLeft(brother);// 兄弟节点左旋
                        brother = leftOf(parentOf(node));// 重新获取兄弟节点
                    }
                    // case4 : 兄弟节点是黑色；兄弟节点的左孩子是红色的，x的兄弟节点的右孩子任意颜色
                    setColor(brother, colorOf(parentOf(node)));// 父节点的颜色，赋值给兄弟节点
                    setColor(parentOf(node), BLACK);// 父节点设置为黑色
                    setColor(leftOf(brother), BLACK);// 兄弟孩子的右节点设置为黑色
                    rotateRight(parentOf(node));
                    node = root;
                }
            }
        }
        // 节点设置为黑色，兼容node两种颜色的情况
        setColor(node, BLACK);// 节点变黑
    }
}
