package dk.apaq.jwutil.common.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class FormPropertyReferenceConverter implements PropertyReferenceConverter<Map<String, String[]>> {

    private static final Pattern FORM_MAP_REFERENCE_PATTERN = Pattern.compile("((\\[)([a-zA-Z]{1}[a-zA-Z0-9\\_]*)(\\]))");
    
    @Override
    public Iterable<String> translate(Map<String, String[]> input) {
        List<String> refs = new ArrayList<>();
        for(String key: input.keySet()) {
            refs.add(FORM_MAP_REFERENCE_PATTERN.matcher(key).replaceAll("($3)"));
        }
        return refs;
    }

}
