package proguard.obfuscate;

import java.util.Arrays;
import java.util.Random;

/**
 * @author paso
 * @version 1.1
 * @date 2021/2/22
 */
public class CharacterNameFactory implements NameFactory {
    private final char[] chars;
    private final int[] offsets;
    private Random random;

    private int first;
    private int offset;

    private int index;
    private CharacterNameFactory parent;

    public CharacterNameFactory(char[] cs, boolean isRandom) {
        if (cs == null || cs.length <= 0) chars = new char[]{'a', 'b', 'c', 'd', 'e'};
            // {'ۖ', 'ۗ', 'ۘ', 'ۙ', 'ۚ', 'ۛ', 'ۜ', '۟', '۠', 'ۡ', 'ۢ', 'ۤ', 'ۥ', 'ۦ', 'ۧ', 'ۨ', '۫', '۬'};
    /*{0x6d6, 0x6d7, 0x6d8, 0x6d9, 0x6da, 0x6db, 0x6dc, 0x6df, 0x6e0,
       0x6e1, 0x6e2, 0x6e4, 0x6e5, 0x6e6, 0x6e7, 0x6e8, 0x6eb, 0x6ec};*/
        else chars = cs;
        offsets = initOffsets(chars);

        init(isRandom ? new Random() : null);
    }

    /**
     * 只共享子对象的 chars、offsets 和 random 成员
     */
    private CharacterNameFactory(CharacterNameFactory child) {
        chars = child.chars;
        offsets = child.offsets;
        init(child.random);
    }

    /**
     * 根据是否随机来初始化部分变量
     */
    private void init(Random random) {
        this.random = random;
        if (random != null) {
            first = random.nextInt(chars.length);
            offset = offsets[random.nextInt(offsets.length)];
        } else {
            first = 0;
            offset = 1;
        }
        reset();
    }

    @Override
    public void reset() {
        index = first;
        parent = null;
    }

    @Override
    public String nextName() {
        String name = getName();
        next();
        return name;
    }

    private String getName() {
        return parent == null ? String.valueOf(chars[index]) : parent.getName() + chars[index];
    }

    private void next() {
        index = (index + offset) % chars.length;
        if (index == first) {
            if (parent == null) parent = new CharacterNameFactory(this);
            else parent.next();
        }
    }

    /**
     * 初始化偏移数组
     */
    private static int[] initOffsets(char[] chars) {
        int[] offset1 = new int[chars.length - 1];
        int len = 0;
        /*
         * 把与 chars 的长度互质的整数添加到偏移数组中。
         * 这样偏移才能完成一个完整的周期。
         */
        for (int i = 1; i < chars.length; i++) {
            if (isCoprime(i, chars.length)) {
                offset1[len++] = i;
            }
        }
        if (offset1.length == len) return offset1;
        int[] offset2 = new int[len];
        System.arraycopy(offset1, 0, offset2, 0, len);
        return offset2;
    }

    /**
     * 判断 a 与 b 是否互质
     */
    private static boolean isCoprime(int a, int b) {
        if (a <= 0 || b <= 0 || a == b) return false;
        int c;
        while (a != 0) {
            // 如果 b < a ，第一次循环会交换 a 和 b 的值
            c = b % a;
            b = a;
            a = c;
        }
        // 最大公约数为 1 ，所以互质
        return b == 1;
    }

    public static void main(String[] args) {
        CharacterNameFactory snf = new CharacterNameFactory(new char[]{'a', 'b', 'c'}, false);
        CharacterNameFactory snf2 = new CharacterNameFactory(new char[]{'1', '2', '3'}, true);
        int count = 100;
        System.out.println(Arrays.toString(snf.offsets));
        for (int i = 0; i < count; i++) {
            System.out.printf("%4d - %s - %s\n", i, snf.nextName(), snf2.nextName());
            //System.out.println(snf.offsets[i]);
            //System.out.println(Integer.toHexString(chars[i]));
        }
    }

} // proguard.obfuscate.CharacterNameFactory
