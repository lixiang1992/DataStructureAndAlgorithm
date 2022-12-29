package olddriver;

import java.util.Map;
import java.util.TreeMap;

public class ODTTree {

    static class Node {
        int l, r, v;
        public Node(int l, int r, int v) {
            this.l = l;
            this.r = r;
            this.v = v;
        }
    }

    TreeMap<Integer, Node> tree = new TreeMap<>();

    public ODTTree() {
    }

    public ODTTree(int l, int r, int v) {
        tree.put(l, new Node(l, r, v));
    }

    public void split(int l) {
        Map.Entry<Integer, Node> prev = tree.floorEntry(l);
        if (prev == null || prev.getKey() == l || prev.getValue().r < l) {
            return;
        }

        Node cur = prev.getValue();
        int r = cur.r, v = cur.v;

        tree.put(prev.getKey(), new Node(cur.l, l - 1, v));
        tree.put(l, new Node(l, r, v));
    }

    public void merge(int l, int r, int v) {
        this.split(l);
        this.split(r + 1);

        // l闭区间, r+1开区间
        tree.subMap(l, r + 1).clear();

        // 再做一个合并
        // 和前一个元素进行合并
        Map.Entry<Integer, Node> prev = tree.floorEntry(l - 1);
        if (prev != null && prev.getValue().v == v && prev.getValue().r == l - 1) {
            tree.remove(prev.getKey());
            l = prev.getKey();
        }

        //      和后一个区间进行合并
        Map.Entry<Integer, Node> next = tree.ceilingEntry(r + 1);
        if (next != null && next.getValue().v == v && next.getKey() == r + 1) {
            tree.remove(next.getKey());
            r = next.getValue().r;
        }

        tree.put(l, new Node(l, r, v));
    }

}

