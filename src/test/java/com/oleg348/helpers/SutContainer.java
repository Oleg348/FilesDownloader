package com.oleg348.helpers;

public abstract class SutContainer<T> {
    private T sut;

    protected abstract T createSut();

    public T getSut() {
        if (sut == null) {
            sut = createSut();
        }

        return sut;
    }
}
