

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Anddew on 15.06.2017.
 */
public class BAOSTester {
    public static void main(String[] args) throws IOException {

        byte[] array = createByteArray();
        ByteArrayOutputStream os = new ModifiedByteArrayOutputStream();
        ByteArrayOutputStream nos = new ModifiedByteArrayOutputStream();
        os.write(array);
        os.writeTo(nos);
        printArray(nos.toByteArray());
    }

    public static byte[] createByteArray() {
        byte[] array = new byte[] {1,2,3,4,5,6,7,8,9,10};
        return array;
    }

    public static void printArray(byte[] b) {
        System.out.println(Arrays.toString(b));
    }
}
