package com.sbmgames.gradientscreator.utils.staticutils;

public class ObjectsVerifiers
{
    private ObjectsVerifiers() { }

    public static <T> T ifEqualReturnAlternative(T actualValue, T expectedValue, T alternative)
    {
        if (actualValue == null)
        {
            return expectedValue == null ? alternative : null;
        }

        return actualValue.equals(expectedValue) ? alternative : actualValue;
    }

    public static int requireInRange(int fromInclusive, int toExclusive, int value) throws IllegalArgumentException
    {
        return requireInRange(fromInclusive, toExclusive, value, "value must be >= 'fromInclusive' and < toExclusive");
    }

    public static int requireInRange(int fromInclusive, int toExclusive, int value, String errorMessage)
    {
        if (fromInclusive >= toExclusive)
        {
            throw new IllegalArgumentException("'fromInclusive' parameter must be smaller than 'toExclusive' parameter.");
        }
        if (value < fromInclusive || value >= toExclusive)
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return value;
    }
}