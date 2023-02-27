package com.sbmgames.gradientscreator.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class FlagsTest
{
    private static final int FLAGS = 64;

    private static final int iterations = 10;

    private Flags flags;

    private final Random random = new Random();

    @Before
    public void init()
    {
        flags = new Flags();

        //randomly setting flags on
        for (int i = 0; i < FLAGS; i++)
        {
            if (random.nextBoolean())
            {
                flags.on(i);
            }
        }
    }

    @Test
    public void on()
    {
        assertThrows(IllegalArgumentException.class, () -> flags.on(-1));
        assertThrows(IllegalArgumentException.class, () -> flags.on(FLAGS));

        for (int i=0; i<iterations; i++)
        {
            var randomIndex = random.nextInt(FLAGS);
            flags.on(randomIndex);
            assertTrue(flags.isOn(randomIndex));
        }
    }

    @Test
    public void off()
    {
        assertThrows(IllegalArgumentException.class, () -> flags.off(-1));
        assertThrows(IllegalArgumentException.class, () -> flags.off(FLAGS));

        for (int i=0; i<iterations; i++)
        {
            var randomIndex = random.nextInt(FLAGS);
            flags.off(randomIndex);
            assertTrue(flags.isOff(randomIndex));
        }
    }

    @Test
    public void toggle()
    {
        assertThrows(IllegalArgumentException.class, () -> flags.off(-1));
        assertThrows(IllegalArgumentException.class, () -> flags.off(FLAGS));

        for (int i=0; i<iterations; i++)
        {
            var randomIndex = random.nextInt(FLAGS);

            var isFlagOnBeforeToggle = flags.isOn(randomIndex);
            flags.toggle(randomIndex);
            var isFlagOnAfterToggle = flags.isOn(randomIndex);

            assertNotEquals(isFlagOnBeforeToggle, isFlagOnAfterToggle);
        }
    }

    @Test
    public void onAll()
    {
        flags.onAll();

        assertTrue(flags.isAllOn());
    }

    @Test
    public void onAllIntInt()
    {
        //if numberOfFlags equal 0 - should not turn on any flag
        flags.offAll();
        flags.onAll(5, 0);
        assertTrue(flags.isAllOff());

        for (int i = 0; i < iterations; i++)
        {
            flags.offAll();

            var negativeNumber = random.nextInt(Integer.MAX_VALUE) - Integer.MAX_VALUE;
            var tooBigNumber = random.nextInt(Integer.MAX_VALUE - FLAGS) + FLAGS;

            //if fromFlagIncluding isn't ok - should throw IllegalArgumentException
            var okLength = random.nextInt(FLAGS - 10);
            assertThrows(IllegalArgumentException.class, () -> flags.onAll(negativeNumber, okLength));
            assertThrows(IllegalArgumentException.class, () -> flags.onAll(tooBigNumber, okLength));

            //if numberOfFlags isn't ok - should throw IllegalArgumentException
            var okStart = random.nextInt(FLAGS - okLength);
            assertThrows(IllegalArgumentException.class, () -> flags.onAll(okStart, negativeNumber));
            assertThrows(IllegalArgumentException.class, () -> flags.onAll(okStart, tooBigNumber));


            //if fromFlagIncluding and numberOfFlags are ok - anything in this range should be on
            flags.onAll(okStart, okLength);
            for (int j = 0; j < FLAGS; j++)
            {
                if (j >= okStart && j < okStart + okLength)
                {
                    assertTrue(flags.isOn(j));
                }
                else
                {
                    assertTrue(flags.isOff(j));
                }
            }
        }
    }

    @Test
    public void onAllIndexesArray()
    {
        flags.offAll();
        flags.onAll(null);
        assertTrue(flags.isAllOff());

        for (int i = 0; i < iterations; i++)
        {
            flags.offAll();

            var arrLength = random.nextInt(FLAGS);
            var arr = randomizeArrayOfUniqueFlags(arrLength);

            flags.onAll(arr);

            for (int j = 0; j < FLAGS; j++)
            {
                //if j is in the array, check if it's on, else check if its off
                int finalJ = j;
                if (Arrays.stream(arr).anyMatch(index -> index == finalJ))
                {
                    assertTrue(flags.isOn(j));
                }
                else
                {
                    assertTrue(flags.isOff(j));
                }
            }
        }
    }

    @Test
    public void offAll()
    {
        flags.offAll();

        assertTrue(flags.isAllOff());
    }

    @Test
    public void offAllIntInt()
    {
        //if numberOfFlags equal 0 - should not turn off any flag
        flags.onAll();
        flags.offAll(5, 0);
        assertTrue(flags.isAllOn());

        for (int i = 0; i < iterations; i++)
        {
            flags.onAll();

            var negativeNumber = random.nextInt(Integer.MAX_VALUE) - Integer.MAX_VALUE;
            var tooBigNumber = random.nextInt(Integer.MAX_VALUE - FLAGS) + FLAGS;

            //if fromFlagIncluding isn't ok - should throw IllegalArgumentException
            var okLength = random.nextInt(FLAGS - 10);
            assertThrows(IllegalArgumentException.class, () -> flags.offAll(negativeNumber, okLength));
            assertThrows(IllegalArgumentException.class, () -> flags.offAll(tooBigNumber, okLength));

            //if numberOfFlags isn't ok - should throw IllegalArgumentException
            var okStart = random.nextInt(FLAGS - okLength);
            assertThrows(IllegalArgumentException.class, () -> flags.offAll(okStart, negativeNumber));
            assertThrows(IllegalArgumentException.class, () -> flags.offAll(okStart, tooBigNumber));


            //if fromFlagIncluding and numberOfFlags are ok - anything in this range should be off
            flags.offAll(okStart, okLength);
            for (int j = 0; j < FLAGS; j++)
            {
                if (j >= okStart && j < okStart + okLength)
                {
                    assertTrue(flags.isOff(j));
                }
                else
                {
                    assertTrue(flags.isOn(j));
                }
            }
        }
    }

    @Test
    public void offAllIndexesArray()
    {
        flags.onAll();
        flags.offAll(null);
        assertTrue(flags.isAllOn());

        for (int i = 0; i < iterations; i++)
        {
            flags.onAll();

            var arrLength = random.nextInt(FLAGS);
            var arr = randomizeArrayOfUniqueFlags(arrLength);

            flags.offAll(arr);

            for (int j = 0; j < FLAGS; j++)
            {
                //if j is in the array, check if it's off, else check if its on
                int finalJ = j;
                if (Arrays.stream(arr).anyMatch(index -> index == finalJ))
                {
                    assertTrue(flags.isOff(j));
                }
                else
                {
                    assertTrue(flags.isOn(j));
                }
            }
        }
    }

    @Test
    public void toggleAllIntInt()
    {
        for (int i = 0; i < iterations; i++)
        {
            reInit();

            var negativeNumber = random.nextInt(Integer.MAX_VALUE) - Integer.MAX_VALUE;
            var tooBigNumber = random.nextInt(Integer.MAX_VALUE - FLAGS) + FLAGS;

            //if fromFlagIncluding isn't ok - should throw IllegalArgumentException
            var okLength = random.nextInt(FLAGS - 10);
            assertThrows(IllegalArgumentException.class, () -> flags.toggleAll(negativeNumber, okLength));
            assertThrows(IllegalArgumentException.class, () -> flags.toggleAll(tooBigNumber, okLength));

            //if numberOfFlags isn't ok - should throw IllegalArgumentException
            var okStart = random.nextInt(FLAGS - okLength);
            assertThrows(IllegalArgumentException.class, () -> flags.toggleAll(okStart, negativeNumber));
            assertThrows(IllegalArgumentException.class, () -> flags.toggleAll(okStart, tooBigNumber));

            var isIndexOnBeforeToggle = new boolean[FLAGS];
            for (int j = 0; j < FLAGS; j++)
            {
                isIndexOnBeforeToggle[j] = flags.isOn(j);
            }

            flags.toggleAll(okStart, okLength);

            for (int j = 0; j < FLAGS; j++)
            {
                if (j >= okStart && j < okStart + okLength)
                {
                    assertNotEquals(isIndexOnBeforeToggle[j], flags.isOn(j));
                }
                else
                {
                    assertEquals(isIndexOnBeforeToggle[j], flags.isOn(j));
                }
            }
        }
    }

    @Test
    public void toggleAllIndexesArray()
    {
        for (int i = 0; i < iterations; i++)
        {
            reInit();

            var arrLength = random.nextInt(FLAGS);
            var arr = randomizeArrayOfUniqueFlags(arrLength);

            var isIndexOnBeforeToggle = new boolean[FLAGS];
            for (int j = 0; j < FLAGS; j++)
            {
                isIndexOnBeforeToggle[j] = flags.isOn(j);
            }

            flags.toggleAll(arr);

            for (int j = 0; j < FLAGS; j++)
            {
                int finalJ = j;
                if (Arrays.stream(arr).anyMatch(index -> index == finalJ))
                {
                    assertNotEquals(isIndexOnBeforeToggle[j], flags.isOn(j));
                }
                else
                {
                    assertEquals(isIndexOnBeforeToggle[j], flags.isOn(j));
                }
            }
        }
    }

    @Test
    public void isOn()
    {
        for (int i = 0; i < iterations; i++)
        {
            flags.offAll();

            var randomIndex = random.nextInt(FLAGS);

            flags.on(randomIndex);

            assertTrue(flags.isOn(randomIndex));
        }
    }

    @Test
    public void isOff()
    {
        for (int i = 0; i < iterations; i++)
        {
            flags.onAll();

            var randomIndex = random.nextInt(FLAGS);
            flags.off(randomIndex);

            assertTrue(flags.isOff(randomIndex));
        }
    }

    @Test
    public void isAnythingOn()
    {
        flags.offAll();
        assertFalse(flags.isAnythingOn());

        for (int i = 0; i < iterations; i++)
        {
            flags.offAll();

            var randomIndex = random.nextInt(FLAGS);
            flags.on(randomIndex);

            assertTrue(flags.isAnythingOn());
        }
    }

    @Test
    public void isAnythingOff()
    {
        flags.onAll();
        assertFalse(flags.isAnythingOff());

        for (int i = 0; i < iterations; i++)
        {
            flags.onAll();

            var randomIndex = random.nextInt(FLAGS);
            flags.off(randomIndex);

            assertTrue(flags.isAnythingOff());
        }
    }

    @Test
    public void isAllOn()
    {
        flags.offAll();
        assertFalse(flags.isAllOn());

        for (int i = 0; i < FLAGS - 1; i++)
        {
            flags.on(i);

            assertFalse(flags.isAllOn());
        }
        flags.on(FLAGS - 1);
        assertTrue(flags.isAllOn());
    }

    @Test
    public void isAllOff()
    {
        flags.onAll();
        assertFalse(flags.isAllOff());

        for (int i = 0; i < FLAGS - 1; i++)
        {
            flags.off(i);

            assertFalse(flags.isAllOff());
        }
        flags.off(FLAGS - 1);
        assertTrue(flags.isAllOff());
    }

    private int[] randomizeArrayOfUniqueFlags(int length)
    {
        var arr = new int[length];
        var set = new HashSet<Integer>();

        for (int i = 0; i < arr.length; i++)
        {
            int num;
            do
            {
                num = random.nextInt(FLAGS);
            }
            while (!set.add(num));

            arr[i] = num;
        }

        return arr;
    }

    private void reInit()
    {
        init();
    }
}