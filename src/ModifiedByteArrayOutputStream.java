/**
This class was wrote as a task:
Youtube.com.
Golovach Courses.
Java I/O
https://youtu.be/iiJ2JXJDrr8
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifiedByteArrayOutputStream extends ByteArrayOutputStream {

    private static final int DEFAULT_START_SIZE = 16;
    private static final AllocateStrategy DEFAULT_ALLOCATE_STRATEGY = new DoubleAllocateStrategy();
    private final AllocateStrategy strategy;
    private List<byte[]> bufferList = new ArrayList<>();
    private int count = 0;

    public ModifiedByteArrayOutputStream() {
        this(DEFAULT_START_SIZE,DEFAULT_ALLOCATE_STRATEGY);
    }

    public ModifiedByteArrayOutputStream(int startSize) {
        this(startSize, DEFAULT_ALLOCATE_STRATEGY);
    }

    public ModifiedByteArrayOutputStream(AllocateStrategy strategy) {
        this(DEFAULT_START_SIZE,strategy);
    }

    public ModifiedByteArrayOutputStream(int startSize, AllocateStrategy strategy) {
        bufferList.add(new byte[startSize]);
        this.strategy = strategy;
    }

    /** Modified method. Allows to exclude array copying when array size changed.
     *
     * @param b
     * @throws IOException
     */
    public synchronized void write(int b) {
        byte[] lastBuffer = bufferList.get(bufferList.size()-1);
        if (count == lastBuffer.length) {
            int newSize = strategy.nextAfter(lastBuffer.length);
            byte[] newLastBuffer = new byte[newSize];
            bufferList.add(newLastBuffer);
            count = 0;
            lastBuffer = newLastBuffer;
        }
        lastBuffer[count++] = (byte) b;
    }

    public synchronized void write(byte[] b) {
        write(b, 0, b.length);
    }

    /** Modified method. Allows to exclude arrays copying when array size changed.
     *  List of byte arrays used for collect amount of byte[] that must be written.
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    public synchronized void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        byte[] lastBuffer = bufferList.get(bufferList.size() - 1);
        if ((count + len) > lastBuffer.length) {
            System.arraycopy(b, off, lastBuffer, count, lastBuffer.length - count);
            int newSize = strategy.nextAfter(lastBuffer.length);
            if (newSize < len) {
                newSize = len;
            }
            byte[] newLastBuffer = new byte[newSize];
            System.arraycopy(b, (off + (lastBuffer.length - count)), newLastBuffer, 0, len - (lastBuffer.length - count));
            count = len - (lastBuffer.length - count);
            bufferList.add(newLastBuffer);

        } else {
            System.arraycopy(b, off, lastBuffer, count, len);
            count += len;
        }
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(toByteArray());
    }

    public synchronized void reset() {
        bufferList = new ArrayList<>();
        bufferList.add(new byte[DEFAULT_START_SIZE]);
        count = 0;
    }

    public synchronized int size() {
        int result = count;
        for (int i = 0; i < bufferList.size() - 1; i++) {
            result += bufferList.get(i).length;
        }
        return result;
    }

    public synchronized String toString() {
        return Arrays.toString(this.toByteArray());
    }

    /** Modified method. It sums length of all arrays in list and create a byte[] with all elements inside.
     *
     * @return
     */
    public synchronized byte[] toByteArray() {
        int resultArraySize = count;
        for (int i = 0; i < bufferList.size() - 1; i++) {
            resultArraySize += bufferList.get(i).length;
        }
        byte[] result = new byte[resultArraySize];
        int currentResultCount = 0;
        for (int i = 0; i < bufferList.size() - 1; i++) {
            System.arraycopy(bufferList.get(i), 0, result, currentResultCount, bufferList.get(i).length);
            currentResultCount = bufferList.get(i).length;
        }
        System.arraycopy(bufferList.get(bufferList.size()-1), 0, result, currentResultCount, count);
        return result;
    }
}