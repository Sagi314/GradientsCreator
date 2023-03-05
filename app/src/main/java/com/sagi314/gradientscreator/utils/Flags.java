package com.sagi314.gradientscreator.utils;

import com.sagi314.gradientscreator.utils.staticutils.Validators;

import java.util.Objects;

/**
 * this class lets the user handle flags easier.
 * <p>the user can set up to 64 flags on\off and check which of them is on and off.</p>
 */
public class Flags
{
    private static final long ALL_ON_64_BIT = -1L; //64-bit number where all bits are 1's
    private static final long ALL_OFF_64_BIT = 0L; //64-bit number where all bits are 0's

    private long flagsIndexesOn;

    //region CONSTRUCTORS VARIATIONS
    public Flags()
    {
        this(false);
    }

    public Flags(int[] onFlagIndexes)
    {
        this();

        Objects.requireNonNull(onFlagIndexes, "'onFlagIndexes' can't be null, consider using the empty constructor");

        onAll(onFlagIndexes);
    }

    public Flags(int turnFlagsOnFromIncluding, int numberOfFlags)
    {
        this();

        onAll(turnFlagsOnFromIncluding, numberOfFlags);
    }
    //endregion

    //MAIN CONSTRUCTOR - any constructor should call this constructor eventually.
    public Flags(boolean isAllOn)
    {
        if (isAllOn)
        {
            onAll();
        }
        else
        {
            offAll();
        }
    }

    //region SETTERS
    public void on(int flagIndex)
    {
        requireIn64BitRange(flagIndex);

        flagsIndexesOn |= toFlag(flagIndex);
    }

    public void off(int flagIndex)
    {
        requireIn64BitRange(flagIndex);

        flagsIndexesOn &= ~toFlag(flagIndex);
    }

    public void toggle(int flagIndex)
    {
        requireIn64BitRange(flagIndex);

        flagsIndexesOn ^= toFlag(flagIndex);
    }

    public void onAll()
    {
        flagsIndexesOn = ALL_ON_64_BIT;
    }

    public void onAll(int fromFlagIncluding, int numberOfFlags)
    {
        requireIn64BitRange(fromFlagIncluding);
        requireIn64BitRange(numberOfFlags);

        var toFlagExcluding = requireIn64BitRange(fromFlagIncluding + numberOfFlags);

        for (int i = fromFlagIncluding; i < toFlagExcluding; i++)
        {
            on(i);
        }
    }

    public void onAll(int[] flagIndexes)
    {

        if (flagIndexes != null)
        {
            for (int flagIndex : flagIndexes)
            {
                on(flagIndex);
            }
        }
    }

    public void offAll()
    {
        flagsIndexesOn = ALL_OFF_64_BIT;
    }

    public void offAll(int fromFlagIncluding, int numberOfFlags)
    {
        requireIn64BitRange(fromFlagIncluding);
        requireIn64BitRange(numberOfFlags);

        var toFlagExcluding = requireIn64BitRange(fromFlagIncluding + numberOfFlags);

        for (int i = fromFlagIncluding; i < toFlagExcluding; i++)
        {
            off(i);
        }
    }

    public void offAll(int[] flagIndexes)
    {
        if (flagIndexes != null)
        {
            for (int flagIndex : flagIndexes)
            {
                off(flagIndex);
            }
        }
    }

    public void toggleAll(int fromFlagIncluding, int numberOfFlags)
    {
        requireIn64BitRange(fromFlagIncluding);
        requireIn64BitRange(numberOfFlags);

        var toFlagExcluding = requireIn64BitRange(fromFlagIncluding + numberOfFlags);

        for (int i = fromFlagIncluding; i < toFlagExcluding; i++)
        {
            toggle(i);
        }
    }

    public void toggleAll(int[] flagIndexes)
    {
        if (flagIndexes != null)
        {
            for (int flagIndex : flagIndexes)
            {
                toggle(flagIndex);
            }
        }
    }
    //endregion

    //region GETTERS
    public boolean isOn(int flagIndex)
    {
        requireIn64BitRange(flagIndex);

        var bit = 1L << flagIndex;

        return (flagsIndexesOn & bit) == bit;
    }

    public boolean isOff(int flagIndex)
    {
        return !isOn(flagIndex);
    }

    public boolean isAnythingOn()
    {
        return flagsIndexesOn != ALL_OFF_64_BIT;
    }

    public boolean isAnythingOff()
    {
        return flagsIndexesOn != ALL_ON_64_BIT;
    }

    public boolean isAllOn()
    {
        return flagsIndexesOn == ALL_ON_64_BIT;
    }

    public boolean isAllOff()
    {
        return flagsIndexesOn == ALL_OFF_64_BIT;
    }
    //endregion

    private int requireIn64BitRange(int flagIndex)
    {
        return Validators.requireInRange(0, 64, flagIndex, "'flagIndex' must be a number between 0 (including) to 63 (including)");
    }

    private long toFlag(int flagIndex)
    {
        return 1L << flagIndex;
    }
}