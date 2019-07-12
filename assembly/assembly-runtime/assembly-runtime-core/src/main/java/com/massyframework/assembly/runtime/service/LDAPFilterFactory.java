package com.massyframework.assembly.runtime.service;

import java.util.ArrayList;
import java.util.List;

import com.massyframework.assembly.service.InvalidSyntaxException;

/**
 * LDAPFilter工厂，提供使用filterString创建LDAPFilter的能力
 * @author huangkaihui
 *
 */
abstract class LDAPFilterFactory {

	/** 操作符 **/
    public static final int EQUAL = 1;
    public static final int APPROX = 2;
    public static final int GREATER = 3;
    public static final int LESS = 4;
    public static final int PRESENT = 5;
    public static final int SUBSTRING = 6;
    public static final int AND = 7;
    public static final int OR = 8;
    public static final int NOT = 9;

    /**
     * 根据过滤字符串创建新的过滤器实例
     *
     * @param filterString
     *            过滤字符串
     * @return {@link LDAPFilter}
     * @throws InvalidSyntaxException
     */
    public static LDAPFilter newInstance(String filterString) throws InvalidSyntaxException {
        return new Parser(filterString).parse();
    }

    /**
     *
     * @author huangkaihui
     *
     */
    private static class Parser {

        private final String filterstring;
        private final char[] filterChars;
        private int pos;

        Parser(String filterstring) {
            this.filterstring = filterstring;
            filterChars = filterstring.toCharArray();
            pos = 0;
        }

        /**
         * 解析过滤字符串，生成LDAPFilter
         *
         * @return {@link LDAPFilter}
         * @throws InvalidSyntaxException
         */
        LDAPFilter parse() throws InvalidSyntaxException {
            LDAPFilter filter;
            try {
                filter = parse_filter();
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new InvalidSyntaxException("Filter ended abruptly", filterstring);
            }

            if (pos != filterChars.length) {
                throw new InvalidSyntaxException("Extraneous trailing characters: " + filterstring.substring(pos),
                        filterstring);
            }
            return filter;
        }

        /**
         *
         * @return
         * @throws InvalidSyntaxException
         */
        private LDAPFilter parse_filter() throws InvalidSyntaxException {
            LDAPFilter filter;
            skipWhiteSpace();

            if (filterChars[pos] != '(') {
                throw new InvalidSyntaxException("Missing '(': " + filterstring.substring(pos), filterstring);
            }

            pos++;

            filter = parse_filtercomp();

            skipWhiteSpace();

            if (filterChars[pos] != ')') {
                throw new InvalidSyntaxException("Missing ')': " + filterstring.substring(pos), filterstring);
            }

            pos++;

            skipWhiteSpace();

            return filter;
        }

        private LDAPFilter parse_filtercomp() throws InvalidSyntaxException {
            skipWhiteSpace();

            char c = filterChars[pos];

            switch (c) {
                case '&': {
                    pos++;
                    return parse_and();
                }
                case '|': {
                    pos++;
                    return parse_or();
                }
                case '!': {
                    pos++;
                    return parse_not();
                }
            }
            return parse_item();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private LDAPFilter parse_and() throws InvalidSyntaxException {
            skipWhiteSpace();

            if (filterChars[pos] != '(') {
                throw new InvalidSyntaxException("Missing '(': " + filterstring.substring(pos), filterstring);
            }

            List operands = new ArrayList(10);

            while (filterChars[pos] == '(') {
                LDAPFilter child = parse_filter();
                operands.add(child);
            }

            return new LDAPFilter(AND, null, operands.toArray(new LDAPFilter[operands.size()]));
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private LDAPFilter parse_or() throws InvalidSyntaxException {
            skipWhiteSpace();

            if (filterChars[pos] != '(') {
                throw new InvalidSyntaxException("Missing '(': " + filterstring.substring(pos), filterstring);
            }

            List operands = new ArrayList(10);

            while (filterChars[pos] == '(') {
                LDAPFilter child = parse_filter();
                operands.add(child);
            }

            return new LDAPFilter(OR, null, operands.toArray(new LDAPFilter[operands.size()]));
        }

        private LDAPFilter parse_not() throws InvalidSyntaxException {
            skipWhiteSpace();

            if (filterChars[pos] != '(') {
                throw new InvalidSyntaxException("Missing '(': " + filterstring.substring(pos), filterstring);
            }

            LDAPFilter child = parse_filter();

            return new LDAPFilter(NOT, null, child);
        }

        private LDAPFilter parse_item() throws InvalidSyntaxException {
            String attr = parse_attr();

            skipWhiteSpace();

            switch (filterChars[pos]) {
                case '~': {
                    if (filterChars[pos + 1] == '=') {
                        pos += 2;
                        return new LDAPFilter(APPROX, attr, parse_value());
                    }
                    break;
                }
                case '>': {
                    if (filterChars[pos + 1] == '=') {
                        pos += 2;
                        return new LDAPFilter(GREATER, attr, parse_value());
                    }
                    break;
                }
                case '<': {
                    if (filterChars[pos + 1] == '=') {
                        pos += 2;
                        return new LDAPFilter(LESS, attr, parse_value());
                    }
                    break;
                }
                case '=': {
                    if (filterChars[pos + 1] == '*') {
                        int oldpos = pos;
                        pos += 2;
                        skipWhiteSpace();
                        if (filterChars[pos] == ')') {
                            return new LDAPFilter(PRESENT, attr, null);
                        }
                        pos = oldpos;
                    }

                    pos++;
                    Object string = parse_substring();

                    if (string instanceof String) {
                        return new LDAPFilter(EQUAL, attr, string);
                    }
                    return new LDAPFilter(SUBSTRING, attr, string);
                }
            }

            throw new InvalidSyntaxException("Invalid operator: " + filterstring.substring(pos), filterstring);
        }

        private String parse_attr() throws InvalidSyntaxException {
            skipWhiteSpace();

            int begin = pos;
            int end = pos;

            char c = filterChars[pos];

            while (c != '~' && c != '<' && c != '>' && c != '=' && c != '(' && c != ')') {
                pos++;

                if (!Character.isWhitespace(c)) {
                    end = pos;
                }

                c = filterChars[pos];
            }

            int length = end - begin;

            if (length == 0) {
                throw new InvalidSyntaxException("Missing attr: " + filterstring.substring(pos), filterstring);
            }

            return new String(filterChars, begin, length);
        }

        private String parse_value() throws InvalidSyntaxException {
            StringBuffer sb = new StringBuffer(filterChars.length - pos);

            parseloop: while (true) {
                char c = filterChars[pos];

                switch (c) {
                    case ')': {
                        break parseloop;
                    }

                    case '(': {
                        throw new InvalidSyntaxException("Invalid value: " + filterstring.substring(pos), filterstring);
                    }

                    case '\\': {
                        pos++;
                        c = filterChars[pos];
                        /* fall through into default */
                    }

                    default: {
                        sb.append(c);
                        pos++;
                        break;
                    }
                }
            }

            if (sb.length() == 0) {
                throw new InvalidSyntaxException("Missing value: " + filterstring.substring(pos), filterstring);
            }

            return sb.toString();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private Object parse_substring() throws InvalidSyntaxException {
            StringBuffer sb = new StringBuffer(filterChars.length - pos);

            List operands = new ArrayList(10);

            parseloop: while (true) {
                char c = filterChars[pos];

                switch (c) {
                    case ')': {
                        if (sb.length() > 0) {
                            operands.add(sb.toString());
                        }

                        break parseloop;
                    }

                    case '(': {
                        throw new InvalidSyntaxException("Invalid value: " + filterstring.substring(pos), filterstring);
                    }

                    case '*': {
                        if (sb.length() > 0) {
                            operands.add(sb.toString());
                        }

                        sb.setLength(0);

                        operands.add(null);
                        pos++;

                        break;
                    }

                    case '\\': {
                        pos++;
                        c = filterChars[pos];
                        /* fall through into default */
                    }

                    default: {
                        sb.append(c);
                        pos++;
                        break;
                    }
                }
            }

            int size = operands.size();

            if (size == 0) {
                throw new InvalidSyntaxException("Missing value: " + filterstring.substring(pos), filterstring);
            }

            if (size == 1) {
                Object single = operands.get(0);

                if (single != null) {
                    return single;
                }
            }

            return operands.toArray(new String[size]);
        }

        private void skipWhiteSpace() {
            for (int length = filterChars.length; (pos < length) && Character.isWhitespace(filterChars[pos]);) {
                pos++;
            }
        }
    }

    /**
     * Encode the value string such that '(', '*', ')' and '\' are escaped.
     *
     * @param value
     *            unencoded value string.
     * @return encoded value string.
     */
    static String encodeValue(String value) {
        boolean encoded = false;
        int inlen = value.length();
        int outlen = inlen << 1; /* inlen 2 */

        char[] output = new char[outlen];
        value.getChars(0, inlen, output, inlen);

        int cursor = 0;
        for (int i = inlen; i < outlen; i++) {
            char c = output[i];

            switch (c) {
                case '(':
                case '*':
                case ')':
                case '\\': {
                    output[cursor] = '\\';
                    cursor++;
                    encoded = true;

                    break;
                }
            }

            output[cursor] = c;
            cursor++;
        }

        return encoded ? new String(output, 0, cursor) : value;
    }

    /**
     * Map a string for an APPROX (~=) comparison.
     *
     * This implementation removes white spaces. This is the minimum
     * implementation allowed by the OSGi spec.
     *
     * @param input Input string.
     * @return String ready for APPROX comparison.
     */
    static String approxString(String input) {
        boolean changed = false;
        char[] output = input.toCharArray();
        int cursor = 0;
        for (int i = 0, length = output.length; i < length; i++) {
            char c = output[i];

            if (Character.isWhitespace(c)) {
                changed = true;
                continue;
            }

            output[cursor] = c;
            cursor++;
        }

        return changed ? new String(output, 0, cursor) : input;
    }

}
