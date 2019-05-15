package proguard.obfuscate;

import java.util.Random;

public class SNameFactory implements NameFactory
{
 private static final Random RANDOM = new Random();
 private static final char[] CHARS = //{'a', 'b', 'c', 'd', 'e'};
 {'ۖ', 'ۗ', 'ۘ', 'ۙ', 'ۚ', 'ۛ', 'ۜ', '۟', '۠',
  'ۡ', 'ۢ', 'ۤ', 'ۥ', 'ۦ', 'ۧ', 'ۨ', '۫', '۬'};
 /*{0x6d6, 0x6d7, 0x6d8, 0x6d9, 0x6da, 0x6db, 0x6dc, 0x6df, 0x6e0,
    0x6e1, 0x6e2, 0x6e4, 0x6e5, 0x6e6, 0x6e7, 0x6e8, 0x6eb, 0x6ec};*/
 private static final int[] OFFSETS = initOffsets();
 private int index = 0;
 private SNameFactory parent = null;
 private int offset = OFFSETS[RANDOM.nextInt(OFFSETS.length)];
  
 public void reset()
 {
  index = 0;
  parent = null;
 }
 
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
  if(index == 0)
  {
   if(parent == null)
    parent = new SNameFactory();
   else
    parent.next();
  }
 }
 
 private static int[] initOffsets()
 {
  int[] offset1 = new int[CHARS.length - 1];
  int len = 0;
  offset1[len++] = 1;
  for(int i = 2; i < CHARS.length; i++)
  {
   if(!hasCommonFactor(i, CHARS.length))
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
 
 private static boolean hasCommonFactor(int a, int b)
 {
  if(a == b || a == 1 || b == 1) return true;
  if(a > b)
  {
   int temp = a;
   a = b;
   b = temp;
  }
  if(b % a == 0) return true;
  int halfB = b / 2;
  for(int i = 2; i <= halfB; i++)
  {
   if(a % i == 0 && b % i == 0)
    return true;
  }
  return false;
 }

 public static void main(String[]args)
 {
  SNameFactory snf = new SNameFactory();
  int count = 100;
  for(int i = 0; i < count; i++)
  {
   System.out.printf("%4d - %s\n", i, snf.nextName());
   //System.out.println(snf.OFFSETS[i]);
  }
 }
 
}
