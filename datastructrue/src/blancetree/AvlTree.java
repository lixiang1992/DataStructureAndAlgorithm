package blancetree;

/**
 * 带rank的avl树
 * @param <T>
 */
public class AvlTree<T extends Comparable<T>> {

    private static class AvlNode<T> {
        T value;
        int height;

        int count;
        int size;

        AvlNode<T> left;
        AvlNode<T> right;

        public AvlNode(T value) {
            this.value = value;
            this.height = 0;
            this.size = 1;
            this.count = 1;
        }

        private int height(AvlNode<T> node) {
            return node == null ? -1 : node.height;
        }
    }
}
