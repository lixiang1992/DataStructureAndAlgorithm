package bit;

/**
 * 树状数组
 */
public class BIT {

    int[] tree;
    int n;

    public BIT(int n) {
        tree = new int[n + 1];
        this.n = n;
    }

    public int lowbit(int x) {
        return x & (-x);
    }

    public int query(int x) {
        int res = 0;
        while(x > 0) {
            res += tree[x];
            x -= lowbit(x);
        }
        return res;
    }

    public void update(int x,int v) {
        while(x <= n) {
            tree[x] += v;
            x += lowbit(x);
        }
    }

}
