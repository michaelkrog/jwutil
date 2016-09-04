package dk.apaq.orderly.common.controller;


public interface PropertyReferenceConverter<T> {

    public Iterable<String> translate(T input);
}
