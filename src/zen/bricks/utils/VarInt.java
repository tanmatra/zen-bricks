package zen.bricks.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VarInt
{
    private VarInt() { }

    public static void encodeInt(OutputStream os, int n) throws IOException {
        do {
            int b = n & 0x7F;
            n >>>= 7;
            if (n != 0) {
                b |= 0x80;
            }
            os.write(b);
        } while (n != 0);
    }

    public static void encodeLong(OutputStream os, long n) throws IOException {
        do {
            int b = (int) (n & 0x7F);
            n >>>= 7;
            if (n != 0) {
                b |= 0x80;
            }
            os.write(b);
        } while (n != 0);
    }

    public static int decodeInt(InputStream is) throws IOException {
        int n = 0;
        int s = 0;
        for (;;) {
            final int b = is.read();
            if (b < 0) {
                throw new EOFException();
            }
            n |= (b & 0x7F) << s;
            if ((b & 0x80) == 0) {
                break;
            }
            s += 7;
        }
        return n;
    }

    public static long decodeLong(InputStream is) throws IOException {
        long n = 0L;
        int s = 0;
        for (;;) {
            final int b = is.read();
            if (b < 0) {
                throw new EOFException();
            }
            n |= (b & 0x7FL) << s;
            if ((b & 0x80) == 0) {
                break;
            }
            s += 7;
        }
        return n;
    }

    public static void main(String[] args) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encodeLong(baos, 0x123456789ABCDEF0L);
        final byte[] bs = baos.toByteArray();
        for (final byte b : bs) {
            System.out.format("%02X ", b);
        }
        System.out.println();
        final ByteArrayInputStream bais = new ByteArrayInputStream(bs);
        final long n = decodeLong(bais);
        System.out.format("%d, 0x%08X%n", n, n);
    }
}
