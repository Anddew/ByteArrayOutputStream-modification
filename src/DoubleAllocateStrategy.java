

/**
 * Created by Anddew on 15.06.2017.
 */
public class DoubleAllocateStrategy implements AllocateStrategy {

    @Override
    public int nextAfter(int now) {
        return now * 2;
    }
}
