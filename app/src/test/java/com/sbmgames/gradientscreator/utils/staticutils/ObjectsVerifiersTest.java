package com.sbmgames.gradientscreator.utils.staticutils;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class ObjectsVerifiersTest
{
    private static final int ITERATIONS = 10;

    private final Random random = new Random();

    @Test
    public void ifEqualReturnAlternative()
    {
        Integer nullValue1 = null;
        Integer nullValue2 = null;

        Integer equal1 = 1;
        Integer equal2 = 1;

        Integer nonEqual = 2;

        Integer alternative = 314;

        //both null - equals - alternative
        assertEquals(alternative, ObjectsVerifiers.ifEqualReturnAlternative(nullValue1, nullValue2, alternative));

        //one null one non null - not equal - first value
        assertEquals(nullValue1, ObjectsVerifiers.ifEqualReturnAlternative(nullValue1, nonEqual, alternative));
        assertEquals(nonEqual, ObjectsVerifiers.ifEqualReturnAlternative(nonEqual, nullValue1, alternative));

        //equals - alternative
        assertEquals(alternative, ObjectsVerifiers.ifEqualReturnAlternative(equal1, equal2, alternative));

        //not equal - first value
        assertEquals(equal1, ObjectsVerifiers.ifEqualReturnAlternative(equal1, nonEqual, alternative));
        assertEquals(nonEqual, ObjectsVerifiers.ifEqualReturnAlternative(nonEqual, equal1, alternative));
    }

    @Test
    public void requireInRangeIntIntInt()
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            int b, m, s;

            do
            {
                b = random.nextInt();
                m = random.nextInt();
                s = random.nextInt();
            }
            while (!(s < m && m < b));

            final var big = b;
            final var medium = m;
            final var small = s;

            //checks for entered parameters:
            //from > to. value in the range
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(big, small, medium));
            //from = to. value also equal so it may be in the range in case of toExclucise acts as toInclusive
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(medium, medium, medium));

            //checks to see if its throwing the excpetion when value is not in range:
            //value < from. to must be bigger than from so it won't get thrown because of it
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(medium, big, small));
            //value = to. from must be smaller than to so it won't get thrown because of it
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(small, medium, medium));
            //value > to. from must be smaller than to so it won't get thrown because of it
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(small, medium, big));

            //checks to see if it returns the right value:
            //from = value < to
            assertEquals(medium, ObjectsVerifiers.requireInRange(medium, big, medium));
            //from < value < to
            assertEquals(medium, ObjectsVerifiers.requireInRange(small, big, medium));
        }
    }

    @Test
    public void requireInRangeIntIntIntString()
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            int b, m, s;

            do
            {
                b = random.nextInt();
                m = random.nextInt();
                s = random.nextInt();
            }
            while (!(s < m && m < b));

            final var big = b;
            final var medium = m;
            final var small = s;

            //checks for entered parameters:
            //from > to. value in the range
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(big, small, medium, ""));
            //from = to. value also equal so it may be in the range in case of toExclucise acts as toInclusive
            assertThrows(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(medium, medium, medium, ""));

            String errorMessage = "sdbsbsdbdsbdsbds";
            //checks to see if its throwing the excpetion when value is not in range:
            //value < from. to must be bigger than from so it won't get thrown because of it
            //checks to see if its throwing the excpetion when value is not in range:
            //value < from. to must be bigger than from so it won't get thrown because of it
            assertEquals(errorMessage, assertThrowableGetMessage(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(medium, big, small, errorMessage)));
            //value = to. from must be smaller than to so it won't get thrown because of it
            assertEquals(errorMessage, assertThrowableGetMessage(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(small, medium, medium, errorMessage)));
            //value > to. from must be smaller than to so it won't get thrown because of it
            assertEquals(errorMessage, assertThrowableGetMessage(IllegalArgumentException.class, () -> ObjectsVerifiers.requireInRange(small, medium, big, errorMessage)));

            //checks to see if it returns the right value:
            //from = value < to
            assertEquals(medium, ObjectsVerifiers.requireInRange(medium, big, medium, ""));
            //from < value < to
            assertEquals(medium, ObjectsVerifiers.requireInRange(small, big, medium, ""));
        }
    }

    private static <T extends Throwable> String assertThrowableGetMessage(Class<T> expectedThrowable, ThrowingRunnable runnable)
    {
        try
        {
            runnable.run();
        }
        catch (Throwable throwable)
        {
            if (expectedThrowable.isInstance(throwable))
            {
                return throwable.getMessage();
            }

            fail("expected excpetion of kind " + expectedThrowable.getName());
        }

        return null;
    }
}