package com.project.staynest.auth.validation;

import java.util.List;
import java.util.Map;

public final class Validation {
    private Validation(){}

    public static void validate(
            Object object,
            String objectName,
            String className
    ){
        if(object == null){
            throw new IllegalArgumentException(
                    objectName + " cannot be null in " + className
            );
        }
    }

    public static void validate(
            Map<?,?> map,
            String mapName,
            String className
    ){
        if(map == null || map.isEmpty()){
            throw new IllegalArgumentException(
                    mapName + " cannot be null or empty in " + className
            );
        }

        for(Map.Entry<?,?> entry : map.entrySet()){
            Validation.validate(entry.getKey(), "Key in " + mapName, className);
            Validation.validate(entry.getValue(), "Value in " + mapName, className);


        }
    }

    public static void validate(
            String text,
            String textName,
            String className
    ){
        if(text == null || text.isBlank()){
            throw new IllegalArgumentException(
                    textName + " cannot be null or blank in " + className
            );
        }
    }

    public static void validate(
            List<?> list,
            String listName,
            String className
    ){
        if(list == null || list.isEmpty()){
            throw new IllegalArgumentException(
                    listName + " cannot be null or empty in " + className
            );
        }
        for(Object object : list){
            Validation.validate(object, listName, className);
        }
    }

    public static void validate(
            byte[] bytes,
            String bytesName,
            String className
    ) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException(
                    bytesName + " cannot be null or empty in " + className
            );
        }
    }


}
