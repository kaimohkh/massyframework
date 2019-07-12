package com.massyframework.assembly.runtime.service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massyframework.assembly.service.Filter;
import com.massyframework.assembly.service.ServiceReference;

/**
 * copy from apache felix
 */
public class LDAPFilter implements Filter {

	 /** 操作、属性和值 **/
    private final int op;
    private final String attr;
    private final Object value;

    /** 过滤字符串 **/
    private transient volatile String filterString;

    /**
     * 构造方法
     * @param operation 操作符
     * @param attr 属性
     * @param value 值
     */
    LDAPFilter(int operation, String attr, Object value) {
        this.op = operation;
        this.attr = attr;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.smarabbit.massy.service.registry.Filter#match(java.util.Map)
     */
    @Override
    public boolean match(Map<String, Object> props) {
        return this.match0(
                new CaseInsensitiveMap(props));
    }

    /* (non-Javadoc)
     * @see org.smarabbit.massy.service.registry.Filter#match(org.smarabbit.massy.service.registry.ServiceReference)
     */
    @Override
    public <S> boolean match(ServiceReference<S> reference) {
        return this.match0(
                new CaseInsensitiveServiceReference<S>(reference));
    }

    /**
     * 是否匹配
     * @param map
     * @return
     */
    public boolean matchCase(Map<String,Object> map) {
        return match0(map);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof LDAPFilter)) {
            return false;
        }

        return this.toString().equals(obj.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String result = filterString;
        if (result == null) {
            filterString = result = normalize();
        }
        return result;
    }

    private String normalize() {
        StringBuffer sb = new StringBuffer();
        sb.append('(');

        switch (op) {
            case LDAPFilterFactory.AND: {
                sb.append('&');

                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    sb.append(filters[i].normalize());
                }

                break;
            }

            case LDAPFilterFactory.OR: {
                sb.append('|');

                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    sb.append(filters[i].normalize());
                }

                break;
            }

            case LDAPFilterFactory.NOT: {
                sb.append('!');
                LDAPFilter filter = (LDAPFilter)value;
                sb.append(filter.normalize());

                break;
            }

            case LDAPFilterFactory.SUBSTRING: {
                sb.append(attr);
                sb.append('=');

                String[] substrings = (String[])value;

                for (int i = 0, size = substrings.length; i < size; i++) {
                    String substr = substrings[i];

                    if (substr == null) /* * */{
                        sb.append('*');
                    } else /* xxx */{
                        sb.append(LDAPFilterFactory.encodeValue(substr));
                    }
                }

                break;
            }
            case LDAPFilterFactory.EQUAL: {
                sb.append(attr);
                sb.append('=');
                sb.append(LDAPFilterFactory.encodeValue((String)value));

                break;
            }
            case LDAPFilterFactory.GREATER: {
                sb.append(attr);
                sb.append(">=");
                sb.append(LDAPFilterFactory.encodeValue((String)value));

                break;
            }
            case LDAPFilterFactory.LESS: {
                sb.append(attr);
                sb.append("<=");
                sb.append(LDAPFilterFactory.encodeValue((String)value));

                break;
            }
            case LDAPFilterFactory.APPROX: {
                sb.append(attr);
                sb.append("~=");
                sb.append(LDAPFilterFactory.encodeValue(
                        LDAPFilterFactory.approxString((String)value)));

                break;
            }

            case LDAPFilterFactory.PRESENT: {
                sb.append(attr);
                sb.append("=*");

                break;
            }
        }

        sb.append(')');

        return sb.toString();
    }

    /**
     *
     * 内部执行对map的过滤
     *
     * @param map {@link Map}.
     * @return 匹配返回<code>true</code>. 否则返回<code>false</code>.
     */
    private boolean match0(Map<String, Object> map) {
        switch (op) {
            case LDAPFilterFactory.AND: {
                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    if (!filters[i].match0(map)) {
                        return false;
                    }
                }

                return true;
            }

            case LDAPFilterFactory.OR: {
                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    if (filters[i].match0(map)) {
                        return true;
                    }
                }

                return false;
            }

            case LDAPFilterFactory.NOT: {
                LDAPFilter filter = (LDAPFilter)value;

                return !filter.match0(map);
            }

            case LDAPFilterFactory.SUBSTRING:
            case LDAPFilterFactory.EQUAL:
            case LDAPFilterFactory.GREATER:
            case LDAPFilterFactory.LESS:
            case LDAPFilterFactory.APPROX: {
                Object prop = (map == null) ? null : map.get(attr);

                return compare(op, prop, value);
            }

            case LDAPFilterFactory.PRESENT: {
                Object prop = (map == null) ? null : map.get(attr);

                return prop != null;
            }
        }

        return false;
    }

    /**
     * 内部执行对服务引用的过滤方法
     * @param reference {@link ServiceReference}
     * @return true匹配，false不匹配
     */
    private <S> boolean match0(ServiceReference<S> reference) {
        switch (op) {
            case LDAPFilterFactory.AND: {
                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    if (!filters[i].match0(reference)) {
                        return false;
                    }
                }

                return true;
            }

            case LDAPFilterFactory.OR: {
                LDAPFilter[] filters = (LDAPFilter[])value;
                for (int i = 0, size = filters.length; i < size; i++) {
                    if (filters[i].match0(reference)) {
                        return true;
                    }
                }

                return false;
            }

            case LDAPFilterFactory.NOT: {
                LDAPFilter filter = (LDAPFilter)value;

                return !filter.match0(reference);
            }

            case LDAPFilterFactory.SUBSTRING:
            case LDAPFilterFactory.EQUAL:
            case LDAPFilterFactory.GREATER:
            case LDAPFilterFactory.LESS:
            case LDAPFilterFactory.APPROX: {
                Object prop = (reference == null) ? null : reference.getProperty(attr);

                return compare(op, prop, value);
            }

            case LDAPFilterFactory.PRESENT: {
                Object prop = (reference == null) ? null : reference.getProperty(attr);

                return prop != null;
            }
        }

        return false;
    }

    /**
     * 对象比较
     * @param operation
     * @param value1
     * @param value2
     * @return
     */
    private boolean compare(int operation, Object value1, Object value2) {
        if (value1 == null) {
            return false;
        }
        if (value1 instanceof String) {
            return compare_String(operation, (String)value1, value2);
        }
        if (value1 instanceof Class){
            return compare_String(operation, ((Class<?>)value1).getName(), value2);
        }

        Class<?> clazz = value1.getClass();
        if (clazz.isArray()) {
            Class<?> type = clazz.getComponentType();
            if (type.isPrimitive()) {
                return compare_PrimitiveArray(operation, type, value1, value2);
            }
            return compare_ObjectArray(operation, (Object[])value1, value2);
        }
        if (value1 instanceof Collection) {
            return compare_Collection(operation, (Collection<?>)value1, value2);
        }
        if (value1 instanceof Integer) {
            return compare_Integer(operation, ((Integer)value1).intValue(), value2);
        }
        if (value1 instanceof Long) {
            return compare_Long(operation, ((Long)value1).longValue(), value2);
        }
        if (value1 instanceof Byte) {
            return compare_Byte(operation, ((Byte)value1).byteValue(), value2);
        }
        if (value1 instanceof Short) {
            return compare_Short(operation, ((Short)value1).shortValue(), value2);
        }
        if (value1 instanceof Character) {
            return compare_Character(operation, ((Character)value1).charValue(), value2);
        }
        if (value1 instanceof Float) {
            return compare_Float(operation, ((Float)value1).floatValue(), value2);
        }
        if (value1 instanceof Double) {
            return compare_Double(operation, ((Double)value1).doubleValue(), value2);
        }
        if (value1 instanceof Boolean) {
            return compare_Boolean(operation, ((Boolean)value1).booleanValue(), value2);
        }
        if (value1 instanceof Comparable) {
            return compare_Comparable(operation, (Comparable<?>)value1, value2);
        }
        return compare_Unknown(operation, value1, value2); // RFC 59
    }

    /**
     * 集合对象比较
     * @param operation
     * @param collection
     * @param value2
     * @return
     */
    private boolean compare_Collection(int operation, Collection<?> collection, Object value2) {
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
            if (compare(operation, iterator.next(), value2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组对象比较
     * @param operation
     * @param array
     * @param value2
     * @return
     */
    private boolean compare_ObjectArray(int operation, Object[] array, Object value2) {
        for (int i = 0, size = array.length; i < size; i++) {
            if (compare(operation, array[i], value2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 基础类型数组比较
     * @param operation
     * @param type
     * @param primarray
     * @param value2
     * @return
     */
    private boolean compare_PrimitiveArray(int operation, Class<?> type, Object primarray, Object value2) {
        if (Integer.TYPE.isAssignableFrom(type)) {
            int[] array = (int[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Integer(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Long.TYPE.isAssignableFrom(type)) {
            long[] array = (long[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Long(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Byte.TYPE.isAssignableFrom(type)) {
            byte[] array = (byte[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Byte(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Short.TYPE.isAssignableFrom(type)) {
            short[] array = (short[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Short(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Character.TYPE.isAssignableFrom(type)) {
            char[] array = (char[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Character(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Float.TYPE.isAssignableFrom(type)) {
            float[] array = (float[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Float(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Double.TYPE.isAssignableFrom(type)) {
            double[] array = (double[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Double(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        if (Boolean.TYPE.isAssignableFrom(type)) {
            boolean[] array = (boolean[])primarray;
            for (int i = 0, size = array.length; i < size; i++) {
                if (compare_Boolean(operation, array[i], value2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 字符串比较
     * @param operation
     * @param string
     * @param value2
     * @return
     */
    private boolean compare_String(int operation, String string, Object value2) {
        switch (operation) {
            case LDAPFilterFactory.SUBSTRING: {
                String[] substrings = (String[])value2;
                int pos = 0;
                for (int i = 0, size = substrings.length; i < size; i++) {
                    String substr = substrings[i];

                    if (i + 1 < size) /* if this is not that last substr */{
                        if (substr == null) /* * */{
                            String substr2 = substrings[i + 1];

                            if (substr2 == null) /* ** */
                                continue; /* ignore first star */
                            /* xxx */
                            int index = string.indexOf(substr2, pos);
                            if (index == -1) {
                                return false;
                            }

                            pos = index + substr2.length();
                            if (i + 2 < size) // if there are more
                                // substrings, increment
                                // over the string we just
                                // matched; otherwise need
                                // to do the last substr
                                // check
                                i++;
                        } else /* xxx */{
                            int len = substr.length();
                            if (string.regionMatches(pos, substr, 0, len)) {
                                pos += len;
                            } else {
                                return false;
                            }
                        }
                    } else /* last substr */{
                        if (substr == null) /* * */{
                            return true;
                        }
                        /* xxx */
                        return string.endsWith(substr);
                    }
                }

                return true;
            }
            case LDAPFilterFactory.EQUAL: {
                return string.equals(value2);
            }
            case LDAPFilterFactory.APPROX: {
                string = LDAPFilterFactory.approxString(string);
                String string2 = LDAPFilterFactory.approxString((String)value2);

                return string.equalsIgnoreCase(string2);
            }
            case LDAPFilterFactory.GREATER: {
                return string.compareTo((String)value2) >= 0;
            }
            case LDAPFilterFactory.LESS: {
                return string.compareTo((String)value2) <= 0;
            }
        }
        return false;
    }

    /**
     * 整形比较
     * @param operation
     * @param intval
     * @param value2
     * @return
     */
    private boolean compare_Integer(int operation, int intval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        int intval2 = Integer.parseInt(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return intval == intval2;
            }
            case LDAPFilterFactory.GREATER: {
                return intval >= intval2;
            }
            case LDAPFilterFactory.LESS: {
                return intval <= intval2;
            }
        }
        return false;
    }

    /**
     * 长整形比较
     * @param operation
     * @param longval
     * @param value2
     * @return
     */
    private boolean compare_Long(int operation, long longval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        long longval2 = Long.parseLong(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return longval == longval2;
            }
            case LDAPFilterFactory.GREATER: {
                return longval >= longval2;
            }
            case LDAPFilterFactory.LESS: {
                return longval <= longval2;
            }
        }
        return false;
    }

    /**
     * 字节比较
     * @param operation
     * @param byteval
     * @param value2
     * @return
     */
    private boolean compare_Byte(int operation, byte byteval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        byte byteval2 = Byte.parseByte(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return byteval == byteval2;
            }
            case LDAPFilterFactory.GREATER: {
                return byteval >= byteval2;
            }
            case LDAPFilterFactory.LESS: {
                return byteval <= byteval2;
            }
        }
        return false;
    }

    /**
     * 短整形比较
     * @param operation
     * @param shortval
     * @param value2
     * @return
     */
    private boolean compare_Short(int operation, short shortval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        short shortval2 = Short.parseShort(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return shortval == shortval2;
            }
            case LDAPFilterFactory.GREATER: {
                return shortval >= shortval2;
            }
            case LDAPFilterFactory.LESS: {
                return shortval <= shortval2;
            }
        }
        return false;
    }

    /**
     * 字符比较
     * @param operation
     * @param charval
     * @param value2
     * @return
     */
    private boolean compare_Character(int operation, char charval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        char charval2 = (((String)value2).trim()).charAt(0);
        switch (operation) {
            case LDAPFilterFactory.EQUAL: {
                return charval == charval2;
            }
            case LDAPFilterFactory.APPROX: {
                return (charval == charval2) || (Character.toUpperCase(charval) == Character.toUpperCase(charval2))
                        || (Character.toLowerCase(charval) == Character.toLowerCase(charval2));
            }
            case LDAPFilterFactory.GREATER: {
                return charval >= charval2;
            }
            case LDAPFilterFactory.LESS: {
                return charval <= charval2;
            }
        }
        return false;
    }

    /**
     * 布尔比较
     * @param operation
     * @param boolval
     * @param value2
     * @return
     */
    private boolean compare_Boolean(int operation, boolean boolval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        boolean boolval2 = Boolean.valueOf(((String)value2).trim()).booleanValue();
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL:
            case LDAPFilterFactory.GREATER:
            case LDAPFilterFactory.LESS: {
                return boolval == boolval2;
            }
        }
        return false;
    }

    /**
     * 单精度浮点比较
     * @param operation
     * @param floatval
     * @param value2
     * @return
     */
    private boolean compare_Float(int operation, float floatval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        float floatval2 = Float.parseFloat(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return Float.compare(floatval, floatval2) == 0;
            }
            case LDAPFilterFactory.GREATER: {
                return Float.compare(floatval, floatval2) >= 0;
            }
            case LDAPFilterFactory.LESS: {
                return Float.compare(floatval, floatval2) <= 0;
            }
        }
        return false;
    }

    /**
     * 双精度浮点比较
     * @param operation
     * @param doubleval
     * @param value2
     * @return
     */
    private boolean compare_Double(int operation, double doubleval, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        double doubleval2 = Double.parseDouble(((String)value2).trim());
        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return Double.compare(doubleval, doubleval2) == 0;
            }
            case LDAPFilterFactory.GREATER: {
                return Double.compare(doubleval, doubleval2) >= 0;
            }
            case LDAPFilterFactory.LESS: {
                return Double.compare(doubleval, doubleval2) <= 0;
            }
        }
        return false;
    }

    private static final Class<?>[] constructorType = new Class[] {String.class};

    /**
     * 支持Comparable对象的比较
     * @param operation
     * @param value1
     * @param value2
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	private boolean compare_Comparable(int operation, Comparable value1, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        Constructor<?> constructor;
        try {
            constructor = value1.getClass().getConstructor(constructorType);
        } catch (NoSuchMethodException e) {
            return false;
        }
        try {
            if (!constructor.isAccessible())
                AccessController.doPrivileged(new SetAccessibleAction(constructor));
            value2 = constructor.newInstance(new Object[] {((String)value2).trim()});
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (InstantiationException e) {
            return false;
        }

        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL: {
                return value1.compareTo(value2) == 0;
            }
            case LDAPFilterFactory.GREATER: {
                return value1.compareTo(value2) >= 0;
            }
            case LDAPFilterFactory.LESS: {
                return value1.compareTo(value2) <= 0;
            }
        }
        return false;
    }

    /**
     * 未知对象比较
     * @param operation
     * @param value1
     * @param value2
     * @return
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    private boolean compare_Unknown(int operation, Object value1, Object value2) {
        if (operation == LDAPFilterFactory.SUBSTRING) {
            return false;
        }
        Constructor<?> constructor;
        try {
            constructor = value1.getClass().getConstructor(constructorType);
        } catch (NoSuchMethodException e) {
            return false;
        }
        try {
            if (!constructor.isAccessible())
                AccessController.doPrivileged(new SetAccessibleAction(constructor));
            value2 = constructor.newInstance(new Object[] {((String)value2).trim()});
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (InstantiationException e) {
            return false;
        }

        switch (operation) {
            case LDAPFilterFactory.APPROX:
            case LDAPFilterFactory.EQUAL:
            case LDAPFilterFactory.GREATER:
            case LDAPFilterFactory.LESS: {
                return value1.equals(value2);
            }
        }
        return false;
    }



    /**
     * 不区分大小写的服务引用
     * @author huangkaihui
     *
     */
    static class CaseInsensitiveServiceReference<S> implements ServiceReference<S> {

        private ServiceReference<S> reference;
        private List<String> keys;

        public CaseInsensitiveServiceReference(ServiceReference<S> reference){
            this.reference = reference;
            this.keys = Collections.unmodifiableList(
                    this.reference.getPropertyNames());
        }


        @Override
        public long getServiceId() {
            Object value = this.reference.getProperty(ServiceReference.ID);
            return ((Long)value).longValue();
        }

        @Override
        public Object getProperty(String key) {
            for (String elem: this.keys){
                if (elem.equalsIgnoreCase(key)){
                    return this.reference.getProperty(elem);
                }
            }
            return null;
        }


        /* (non-Javadoc)
         * @see org.smarabbit.massy.service.ServiceReference#getProperty(java.lang.String, java.lang.Class)
         */
        @Override
        public <P> P getProperty(String key, Class<P> propType) {
            Object value = this.getProperty(key);
            return value == null ? null : propType.cast(value);
        }

        @Override
        public List<String> getPropertyNames() {
            return this.keys;
        }
    }

    /**
     * 不区分大小写的Map
     * @author huangkaihui
     *
     */
    static class CaseInsensitiveMap extends AbstractMap<String, Object> {

        private Map<String, Object> map;

        public CaseInsensitiveMap(Map<String, Object> map){
            this.map = map;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractMap#entrySet()
         */
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return this.map.entrySet();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractMap#containsKey(java.lang.Object)
         */
        @Override
        public boolean containsKey(Object key) {
            boolean result = this.map.containsKey(key);
            if (!result){
                String k = (String)key;
                for (String name: this.map.keySet()){
                    if (name.equalsIgnoreCase(k)){
                        result = true;
                        break;
                    }
                }
            }
            return result;
        }

        /* (non-Javadoc)
         * @see java.util.AbstractMap#get(java.lang.Object)
         */
        @Override
        public Object get(Object key) {
            Object result = this.map.get(key);
            if (result == null){
                String k = (String)key;
                for (String name: this.map.keySet()){
                    if (name.equalsIgnoreCase(k)){
                        result = this.map.get(name);
                        break;
                    }
                }
            }
            return result;
        }
    }

    /**
     * 设置可访问操作
     * @author huangkaihui
     *
     */
    @SuppressWarnings("rawtypes")
    static class SetAccessibleAction implements PrivilegedAction {
        private final AccessibleObject accessible;

        SetAccessibleAction(AccessibleObject accessible) {
            this.accessible = accessible;
        }

        public Object run() {
            accessible.setAccessible(true);
            return null;
        }
    }

}
