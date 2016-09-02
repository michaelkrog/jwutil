package dk.apaq.jwutil.common.controller;


public interface PropertyReferenceConverter<T> {

    public Iterable<String> translate(T input);
}
