package com.sagi314.gradientscreator.utils;

public final class OutParameter<T>
{
    private T value;

    public T get()
    {
        return value;
    }

    public void set(T value)
    {
        this.value = value;
    }
}