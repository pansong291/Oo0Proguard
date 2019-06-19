package proguard.obfuscate;

import java.util.Random;

public class CharacterNameFactory implements NameFactory
{
  // 类的常量
  private static final Random RANDOM = new Random();
  private static char[] CHARS;
  private static int[] OFFSETS;

  // 实例内的常量
  private boolean isRandom;
  private int first;
  private int offset;

  // 实例内的变量
  private int index;
  private CharacterNameFactory parent;

  public CharacterNameFactory(char[] cs, boolean random)
  {
    if(cs == null || cs.length <= 0) CHARS = new char[] //{'a', 'b', 'c', 'd', 'e'};
    {'ۖ', 'ۗ', 'ۘ', 'ۙ', 'ۚ', 'ۛ', 'ۜ', '۟', '۠', 'ۡ', 'ۢ', 'ۤ', 'ۥ', 'ۦ', 'ۧ', 'ۨ', '۫', '۬'};
    /*{0x6d6, 0x6d7, 0x6d8, 0x6d9, 0x6da, 0x6db, 0x6dc, 0x6df, 0x6e0,
       0x6e1, 0x6e2, 0x6e4, 0x6e5, 0x6e6, 0x6e7, 0x6e8, 0x6eb, 0x6ec};*/
    else CHARS = cs;
    OFFSETS = initOffsets();

    init(random);
  }

  private CharacterNameFactory(boolean random)
  {
    init(random);
  }

  private void init(boolean random)
  {
    isRandom = random;
    first = isRandom ? RANDOM.nextInt(CHARS.length) : 0;
    offset = isRandom ? OFFSETS[RANDOM.nextInt(OFFSETS.length)] : 1;
    reset();
  }

  @Override
  public void reset()
  {
    index = first;
    parent = null;
  }

  @Override
  public String nextName()
  {
    String name = getName();
    next();
    return name;
  }

  private String getName()
  {
    return parent == null ? String.valueOf(CHARS[index]) : parent.getName() + CHARS[index];
  }

  private void next()
  {
    index = (index + offset) % CHARS.length;
    if(index == first)
    {
      if(parent == null) parent = new CharacterNameFactory(isRandom);
      else parent.next();
    }
  }

  private static int[] initOffsets()
  {
    int[] offset1 = new int[CHARS.length - 1];
    int len = 0;
    for(int i = 1; i < CHARS.length; i++)
    {
      if(isCoprime(i, CHARS.length))
      {
        offset1[len++] = i;
      }
    }
    if(offset1.length == len) return offset1;
    int[] offset2 = new int[len];
    for(int i = 0; i < len; i++)
    {
      offset2[i] = offset1[i];
    }
    return offset2;
  }

  // 判断 a 与 b 是否互质
  private static boolean isCoprime(int a, int b)
  {
    if(a <= 0 || b <= 0 || a == b) return false;
    int c = 0;
    while(a != 0)
    {
      // 如果 b < a ，第一次循环会交换 a 和 b 的值
      c = b % a;
      b = a;
      a = c;
    }
    // 最大公约数为 1 ，所以互质
    return b == 1;
  }

  public static void main(String[] args)
  {
    CharacterNameFactory snf = new CharacterNameFactory(new char[]{'a', 'b', 'c'},false);
    int count = 100;
    for(int i = 0; i < count; i++)
    {
      System.out.printf("%4d - %s\n", i, snf.nextName());
      //System.out.println(snf.OFFSETS[i]);
      //System.out.println(Integer.toHexString(CHARS[i]));
    }
  }

}
