package com.deepconnect.ats.common.utils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class HashMapUtils {

    public static <KeyType, ValueType> void putValueInSet(HashMap<KeyType, Set<ValueType>> map, KeyType key, ValueType valueToAdd){
        if(map.containsKey(key)){
            Set<ValueType> set = map.get(key);
            set.add(valueToAdd);
            map.put(key, set);
        }
        else{
            map.put(key, new HashSet<ValueType>(Set.of(valueToAdd)));
        }
    }

    public static <KeyType, ValueType> void putValueInList(HashMap<KeyType, List<ValueType>> map, KeyType key, ValueType valueToAdd){
        if(map.containsKey(key)){
            List<ValueType> list = map.get(key);
            list.add(valueToAdd);
            map.put(key, list);
        }
        else{
            map.put(key, new ArrayList<ValueType>(List.of(valueToAdd)));
        }
    }

    public static <KeyType, ValueType> void putValuesInList(HashMap<KeyType, List<ValueType>> map, KeyType key, List<ValueType> valuesToAdd){
        if(map.containsKey(key)){
            List<ValueType> list = map.get(key);
            list.addAll(valuesToAdd);
            map.put(key, list);
        }
        else{
            map.put(key, new ArrayList<ValueType>(valuesToAdd));
        }
    }

    public static <KeyType> void putOrAddValue(HashMap<KeyType, Integer> map, KeyType key, Integer valueToAdd){
        if(map.containsKey(key)){
            map.put(key, map.get(key) + valueToAdd);
        }
        else{
            map.put(key, valueToAdd);
        }
    }

    public static <KeyType> void putOrIncrementValue(HashMap<KeyType, Integer> map, KeyType key){
        if(map.containsKey(key)){
            map.put(key, map.get(key) + 1);
        }
        else{
            map.put(key, 1);
        }
    }

    public static <KeyType> void putOrAddValue(HashMap<KeyType, Double> map, KeyType key, Double valueToAdd){
        valueToAdd = DoubleUtils.get(valueToAdd);
        if(map.containsKey(key)){
            map.put(key, map.get(key) + valueToAdd);
        }
        else{
            map.put(key, valueToAdd);
        }
    }

    public static HashMap<Integer, Double> convertTypeToIntKeyDoubleValue(Map<String, Object> map){
        if(map == null || map.isEmpty()){
            return null;
        }

        HashMap<Integer, Double> result = new HashMap<>();
        for(String key : map.keySet()){
            result.put(Integer.parseInt(key), (double)map.get(key));
        }
        
        return result;
    }

    public static HashMap<LocalDate, Double> convertTypeToLocalDateKeyDoubleValue(Map<String, Object> map){
        if(map == null || map.isEmpty()){
            return null;
        }

        HashMap<LocalDate, Double> result = new HashMap<>();
        for(String key : map.keySet()){
            result.put(LocalDate.parse(key), (double)map.get(key));
        }
        
        return result;
    }

    public static HashMap<LocalDateTime, Double> convertTypeToLocalDateTimeKeyDoubleValue(Map<String, Object> map){
        if(map == null || map.isEmpty()){
            return null;
        }

        HashMap<LocalDateTime, Double> result = new HashMap<>();
        for(String key : map.keySet()){
            result.put(LocalDateTime.parse(key), (double)map.get(key));
        }
        
        return result;
    }

    public static HashMap<LocalDate, HashMap<Integer, Double>> convertTypeToLocalDateKeyLeavesPerYearValue(Map<String, Object> map){
        if(map == null || map.isEmpty()){
            return null;
        }

        HashMap<LocalDate, HashMap<Integer, Double>> result = new HashMap<>();
        for(String key : map.keySet()){
            HashMap<Integer, Double> leavesPerYear = (HashMap<Integer, Double>)map.get(key);
            result.put(LocalDate.parse(key), leavesPerYear);
        }
        
        return result;
    }

    //This method is used to convert a list of objects into a HashMap where the key is a composite key made up of two fields from the object.
    //The method takes a list of objects, the names of the two fields to be used as keys, and the classes of the key and value types.
    //It returns a HashMap where the key is the value of field name specified by subKeyFieldName and the value is the object itself.
    //The method uses reflection to access the fields of the object and create the composite key.
    public static <MainKeyType, SubKeyType, ValueType> HashMap<SubKeyType, ValueType> getMapFromListForCompositeKey(List<ValueType> list, String mainKeyFieldName, String subKeyFieldName, Class<MainKeyType> clazzMainKeyType, Class<SubKeyType> clazzSubKeyType, Class<ValueType> clazzValueType) {
        HashMap<SubKeyType, ValueType> result = new HashMap<>();
        
        try {
            Field mainKeyField = clazzValueType.getDeclaredField(mainKeyFieldName);
            for(ValueType listItem : list){
                mainKeyField.setAccessible(true);
                MainKeyType mainKey = (MainKeyType)mainKeyField.get(listItem); 
                //MainKeyType subKey = (SubKeyType)mainKey.get(listItem); 
                Field subKeyField = mainKey.getClass().getDeclaredField(subKeyFieldName);
                subKeyField.setAccessible(true);
                result.put((SubKeyType)subKeyField.get(mainKey), listItem);
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;    
    }

    public static <KeyType, ValueType> HashMap<KeyType, ValueType> getMapFromList(List<ValueType> list, String keyFieldName, Class<KeyType> clazzKeyType, Class<ValueType> clazzValueType) {
        HashMap<KeyType, ValueType> result = new HashMap<>();

        try {
            Field keyField = clazzValueType.getDeclaredField(keyFieldName);
            for(ValueType listItem : list){
                keyField.setAccessible(true);
                result.put((KeyType)keyField.get(listItem), listItem);
            } 
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;    
    }

    public static <KeyType, ValueType> HashMap<KeyType, List<ValueType>> getMapFromListForListValueType(List<ValueType> list, String keyFieldName, Class<KeyType> clazzKeyType, Class<ValueType> clazzValueType) {
        HashMap<KeyType, List<ValueType>> result = new HashMap<>();
        if(list == null || list.size() == 0){
            return result;
        }
        
        try {
            Field keyField = clazzValueType.getDeclaredField(keyFieldName);
            for(ValueType listItem : list){
                keyField.setAccessible(true);
                HashMapUtils.putValueInList(result, (KeyType)keyField.get(listItem), listItem);
            }
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;    
    }

}
