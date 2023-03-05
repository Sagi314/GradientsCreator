package com.sagi314.gradientscreator.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class OutParameterTest
{

    @Test
    public void getBeforeSetReturnNull()
    {
        var out = new OutParameter<>();

        assertNull(out.get());
    }


    @Test
    public void getObjectAfterSetEquals()
    {
        var object = new Object();

        var out = new OutParameter<>();

        out.set(object);

        assertEquals(object, out.get());
    }

    @Test
    public void getObjectAfterSetNoNull()
    {
        var out = new OutParameter<>();

        out.set(new Object());

        assertNotNull(out.get());
    }

    @Test
    public void setAfterSetChangesObject()
    {
        var object1 = new Object();
        var object2 = new Object();

        var out = new OutParameter<>();

        out.set(object1);
        out.set(object2);

        assertEquals(object2, out.get());
    }
}