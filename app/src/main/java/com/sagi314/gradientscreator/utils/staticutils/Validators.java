package com.sagi314.gradientscreator.utils.staticutils;

import java.io.File;
import java.util.function.Supplier;

/**
 * Utility class that helps throwing exceptions for invalid parameters
 */
public final class Validators
{
    public static <E> E ifEqualGet(E actualValue, E expectedValue, E alternative)
    {
        if (actualValue == null)
        {
            return expectedValue == null ? alternative : null;
        }

        return actualValue.equals(expectedValue) ? alternative : actualValue;
    }

    public static <E> E ifNullGet(E object, Supplier<E> objectSupplier)
    {
        return object == null ? objectSupplier.get() : object;
    }

    public static <E> E ifNullGet(E object, E objectEoReturn)
    {
        return object == null ? objectEoReturn : object;
    }

     /**
      * this method is for cases we want to use the object itself and not as parameter (meaning: {@code object.method()}).
      * <p>for cases we want to use the object only as parameter see {@link #illegalNull(Object, String)}</p>
     */
    public static <E> E requireNotNull(E object, String errorMessage) throws NullPointerException
    {
        if (object == null)
        {
            throw new NullPointerException(errorMessage);
        }

        return object;
    }

    /**
     * this method is for cases we only want to send the object as a parameter to other class (meaning: {@code anotherObject.method(object)} or {@code anotherMethod(object)}).
     * <p>for cases we want to use the object itself see {@link #requireNotNull(Object, String)}</p>
     */
    public static <E> E illegalNull(E object, String errorMessage) throws IllegalArgumentException
    {
        if (object == null)
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return object;
    }

    public static <E, T> E requireOfClass(E object, Class<T> allowedClass, String errorMessage)
    {
        requireNotNull(allowedClass, "can't check if object is instance of 'allowedClass' when 'allowedClass' is null");

        if (!allowedClass.isInstance(object))
        {
            throw new IllegalArgumentException(errorMessage);
        }
        
        return object;
    }


    //region Numbers only
    public static <E extends Number> E illegalBelow(E number, E minLegal, String errorMessage)
    {
        requireNotNull(number, "can't check if 'number' is smaller than 'minLegal' when 'number' is null");
        requireNotNull(number, "can't check if 'number' is smaller than 'minLegal' when 'minLegal' is null");

        if (number.doubleValue() < minLegal.doubleValue())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return number;
    }

    public static <E extends Number> E illegalAbove(E number, E maxLegal, String errorMessage)
    {
        requireNotNull(number, "can't check if 'number' is greater than 'minLegal' when 'number' is null");
        requireNotNull(number, "can't check if 'number' is greater than 'minLegal' when 'minLegal' is null");

        if (number.doubleValue() > maxLegal.doubleValue())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return number;
    }

    public static <E extends Number> E requireAbove(E number, E maxIllegal, String errorMessage)
    {
        if (number.doubleValue() <= maxIllegal.doubleValue())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return number;
    }

    public static <E extends Number> E illegalEqual(E number1, E number2, String errorMessage)
    {
        if (number1.doubleValue() == number2.doubleValue())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return number1;
    }

    public static <E extends Number> E requireInRange(E fromInclusive, E toExclusive, E value, String errorMessage)
    {
        requireNotNull(fromInclusive, "can't check if 'value' in range when 'fromInclusive' is null");
        requireNotNull(toExclusive, "can't check if 'value' in range when 'toExclusive' is null");
        requireNotNull(value, "can't check if 'value' in range when 'value' is null");

        if (fromInclusive.doubleValue() >= toExclusive.doubleValue())
        {
            throw new IllegalArgumentException("'fromInclusive' must be smaller than 'toExclusive'");
        }

        if (value.doubleValue() < fromInclusive.doubleValue() || value.doubleValue() >= toExclusive.doubleValue())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return value;
    }

    public static <E extends Number> E[] requireInRange(E fromInclusive, E toExclusive, E[] values, String errorMessage)
    {
        requireNotNull(fromInclusive, "can't check if 'values' in range when 'fromInclusive' is null");
        requireNotNull(toExclusive, "can't check if 'values' in range when 'toExclusive' is null");
        requireNotNull(values, "can't check if 'values' in range when 'value' is null");

        for (E value : values)
        {
            requireInRange(fromInclusive, toExclusive, value, errorMessage);
        }

        return values;
    }
    //endregion

    //region Files only
    public static File requireNotFolder(File file, String errorMessage)
    {
        requireNotNull(file, "can't check if a file is a folder when it is null...");

        if (file.isDirectory())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return file;
    }

    public static File requireFolder(File hopefullyFolder, String errorMessage)
    {
        requireNotNull(hopefullyFolder, "can't check if a file is a folder when it is null...");

        if (!hopefullyFolder.isDirectory())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return hopefullyFolder;
    }
    //endregion

    //region Strings only
    public static String illegalBlank(String s, String errorMessage)
    {
        requireNotNull(s, "can't check if a string is blank when it is null...");

        if (s.isBlank())
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return s;
    }

    public static String illegalNullOrBlank(String s, String errorMessage)
    {
        illegalNull(s, errorMessage);
        illegalBlank(s, errorMessage);

        return s;
    }

    public static String requireMatches(String s, String regex, String errorMessage)
    {
        requireNotNull(s, "can't check if there is a match when 's' is null");

        if (!s.matches(regex))
        {
            throw new IllegalArgumentException(errorMessage);
        }

        return s;
    }
    //endregion

    //prevents creating instances
    private Validators() { }
}