/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 * Modified By Geoff Groos, Empower Operations Corp, 2016. 
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * --- 
 * 
 * This code was copied and modified by Empower Operations Corp 
 * under the terms set out by the Apache 2.0, for use with OASIS
 * released under the empower operations proprietary license.
 */

package com.empowerops.linqalike.experimental;

import java.util.Map;

/**
 * A support class for dealing with descriptors.
 *
 * <p>See chapter 4.3 in "The Java Virtual Machine Specification (2nd ed.)"
 */
public class Descriptor {
    /**
     * Converts a class name into the internal representation used in
     * the JVM.
     *
     * <p>Note that <code>toJvmName(toJvmName(s))</code> is equivalent
     * to <code>toJvmName(s)</code>.
     */
    public static String toJvmName(String classname) {
        return classname.replace('.', '/');
    }

    /**
     * Converts a class name from the internal representation used in
     * the JVM to the normal one used in Java.
     * This method does not deal with an array type name such as
     * "[Ljava/lang/Object;" and "[I;".  For such names, use
     * <code>toClassName()</code>.
     *
     * @see #toClassName(String)
     */
    public static String toJavaName(String classname) {
        return classname.replace('/', '.');
    }

    /**
     * Converts to a Java class name from a descriptor.
     *
     * @param descriptor        type descriptor.
     */
    public static String toClassName(String descriptor) {
        int arrayDim = 0;
        int i = 0;
        char c = descriptor.charAt(0);
        while (c == '[') {
            ++arrayDim;
            c = descriptor.charAt(++i);
        }

        String name;
        if (c == 'L') {
            int i2 = descriptor.indexOf(';', i++);
            name = descriptor.substring(i, i2).replace('/', '.');
            i = i2;
        }
        else if (c == 'V')
            name =  "void";
        else if (c == 'I')
            name = "int";
        else if (c == 'B')
            name = "byte";
        else if (c == 'J')
            name = "long";
        else if (c == 'D')
            name = "double";
        else if (c == 'F')
            name = "float";
        else if (c == 'C')
            name = "char";
        else if (c == 'S')
            name = "short";
        else if (c == 'Z')
            name = "boolean";
        else
            throw new RuntimeException("bad descriptor: " + descriptor);

        if (i + 1 != descriptor.length())
            throw new RuntimeException("multiple descriptors?: " + descriptor);

        if (arrayDim == 0)
            return name;
        else {
            StringBuffer sbuf = new StringBuffer(name);
            do {
                sbuf.append("[]");
            } while (--arrayDim > 0);

            return sbuf.toString();
        }
    }

    /**
     * Converts to a descriptor from a Java class name
     */
    public static String of(String classname) {
        if (classname.equals("void"))
            return "V";
        else if (classname.equals("int"))
            return "I";
        else if (classname.equals("byte"))
            return "B";
        else if (classname.equals("long"))
            return "J";
        else if (classname.equals("double"))
            return "D";
        else if (classname.equals("float"))
            return "F";
        else if (classname.equals("char"))
            return "C";
        else if (classname.equals("short"))
            return "S";
        else if (classname.equals("boolean"))
            return "Z";
        else
            return "L" + toJvmName(classname) + ";";
    }

    /**
     * Substitutes a class name
     * in the given descriptor string.
     *
     * @param desc    descriptor string
     * @param oldname replaced JVM class name
     * @param newname substituted JVM class name
     *
     * @see Descriptor#toJvmName(String)
     */
    public static String rename(String desc, String oldname, String newname) {
        if (desc.indexOf(oldname) < 0)
            return desc;

        StringBuffer newdesc = new StringBuffer();
        int head = 0;
        int i = 0;
        for (;;) {
            int j = desc.indexOf('L', i);
            if (j < 0)
                break;
            else if (desc.startsWith(oldname, j + 1)
                     && desc.charAt(j + oldname.length() + 1) == ';') {
                newdesc.append(desc.substring(head, j));
                newdesc.append('L');
                newdesc.append(newname);
                newdesc.append(';');
                head = i = j + oldname.length() + 2;
            }
            else {
                i = desc.indexOf(';', j) + 1;
                if (i < 1)
                    break; // ';' was not found.
            }
        }

        if (head == 0)
            return desc;
        else {
            int len = desc.length();
            if (head < len)
                newdesc.append(desc.substring(head, len));

            return newdesc.toString();
        }
    }

    /**
     * Substitutes class names in the given descriptor string
     * according to the given <code>map</code>.
     *
     * @param map a map between replaced and substituted
     *            JVM class names.
     * @see Descriptor#toJvmName(String)
     */
    public static String rename(String desc, Map map) {
        if (map == null)
            return desc;

        StringBuffer newdesc = new StringBuffer();
        int head = 0;
        int i = 0;
        for (;;) {
            int j = desc.indexOf('L', i);
            if (j < 0)
                break;

            int k = desc.indexOf(';', j);
            if (k < 0)
                break;

            i = k + 1;
            String name = desc.substring(j + 1, k);
            String name2 = (String)map.get(name);
            if (name2 != null) {
                newdesc.append(desc.substring(head, j));
                newdesc.append('L');
                newdesc.append(name2);
                newdesc.append(';');
                head = i;
            }
        }

        if (head == 0)
            return desc;
        else {
            int len = desc.length();
            if (head < len)
                newdesc.append(desc.substring(head, len));

            return newdesc.toString();
        }
    }

    /**
     * Appends a parameter type to the parameter list represented
     * by the given descriptor.
     *
     * <p><code>classname</code> must not be an array type.
     *
     * @param classname parameter type (not primitive type)
     * @param desc      descriptor
     */
    public static String appendParameter(String classname, String desc) {
        int i = desc.indexOf(')');
        if (i < 0)
            return desc;
        else {
            StringBuffer newdesc = new StringBuffer();
            newdesc.append(desc.substring(0, i));
            newdesc.append('L');
            newdesc.append(classname.replace('.', '/'));
            newdesc.append(';');
            newdesc.append(desc.substring(i));
            return newdesc.toString();
        }
    }

    /**
     * Returns the <code>CtClass</code> objects representing the parameter
     * types specified by the given descriptor.
     *
     * @param desc descriptor
     */
    public static Class[] getParameterTypes(String desc)
    {
        if (desc.charAt(0) != '(')
            throw new IllegalArgumentException("desc");
        else {
            int num = numOfParameters(desc);
            Class[] args = new Class[num];
            int n = 0;
            int i = 1;
            do {
                i = toClass(desc, i, args, n++);
            } while (i > 0);
            return args;
        }
    }

    /**
     * Returns true if the list of the parameter types of desc1 is equal to
     * that of desc2.
     * For example, "(II)V" and "(II)I" are equal.
     */
    public static boolean eqParamTypes(String desc1, String desc2) {
        if (desc1.charAt(0) != '(')
            return false;

        for (int i = 0; true; ++i) {
            char c = desc1.charAt(i);
            if (c != desc2.charAt(i))
                return false;

            if (c == ')')
                return true;
        }
    }

    /**
     * Returns the signature of the given descriptor.  The signature does
     * not include the return type.  For example, the signature of "(I)V"
     * is "(I)".
     */
    public static String getParamDescriptor(String decl) {
        return decl.substring(0, decl.indexOf(')') + 1);
    }

    /**
     * Returns the <code>CtClass</code> object representing the return
     * type specified by the given descriptor.
     *
     * @param desc descriptor
     */
    public static Class getReturnType(String desc) {
        int i = desc.indexOf(')');
        if (i < 0)
            return null;
        else {
            Class[] type = new Class[1];
            toClass(desc, i + 1, type, 0);
            return type[0];
        }
    }

    /**
     * Returns the number of the prameters included in the given
     * descriptor.
     *
     * @param desc descriptor
     */
    public static int numOfParameters(String desc) {
        int n = 0;
        int i = 1;
        for (;;) {
            char c = desc.charAt(i);
            if (c == ')')
                break;

            while (c == '[')
                c = desc.charAt(++i);

            if (c == 'L') {
                i = desc.indexOf(';', i) + 1;
                if (i <= 0)
                    throw new IndexOutOfBoundsException("bad descriptor");
            }
            else
                ++i;

            ++n;
        }

        return n;
    }

    /**
     * Returns a <code>CtClass</code> object representing the type
     * specified by the given descriptor.
     *
     * <p>This method works even if the package-class separator is
     * not <code>/</code> but <code>.</code> (period).  For example,
     * it accepts <code>Ljava.lang.Object;</code>
     * as well as <code>Ljava/lang/Object;</code>.
     *
     * @param desc descriptor.
     */
    public static Class toClass(String desc)
    {
        Class[] clazz = new Class[1];
        int res = toClass(desc, 0, clazz, 0);
        if (res >= 0)
            return clazz[0];
        else {
            // maybe, you forgot to surround the class name with
            // L and ;.  It violates the protocol, but I'm tolerant...
            try {
                return Class.forName(desc.replace('/', '.'));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int toClass(String desc, int i,
                               Class[] args, int n)
    {
        int i2;
        String name;

        int arrayDim = 0;
        char c = desc.charAt(i);
        while (c == '[') {
            ++arrayDim;
            c = desc.charAt(++i);
        }

        if (c == 'L') {
            i2 = desc.indexOf(';', ++i);
            name = desc.substring(i, i2++).replace('/', '.');
        }
        else {
            Class type = toPrimitiveClass(c);
            if (type == null)
                return -1; // error

            i2 = i + 1;
            if (arrayDim == 0) {
                args[n] = type;
                return i2; // neither an array type or a class type
            }
            else
                name = type.getName();
        }

        if (arrayDim > 0) {
            StringBuffer sbuf = new StringBuffer(name);
            while (arrayDim-- > 0)
                sbuf.append("[]");

            name = sbuf.toString();
        }

        try {
            args[n] = Class.forName(name);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return i2;
    }

    static Class toPrimitiveClass(char c) {
        Class type = null;
        switch (c) {
        case 'Z' :
            type = boolean.class;
            break;
        case 'C' :
            type = char.class;
            break;
        case 'B' :
            type = byte.class;
            break;
        case 'S' :
            type = short.class;
            break;
        case 'I' :
            type = int.class;
            break;
        case 'J' :
            type = long.class;
            break;
        case 'F' :
            type = float.class;
            break;
        case 'D' :
            type = double.class;
            break;
        case 'V' :
            type = void.class;
            break;
        }

        return type;
    }

    /**
     * Computes the dimension of the array represented by the given
     * descriptor.  For example, if the descriptor is <code>"[[I"</code>,
     * then this method returns 2.
     *
     * @param desc the descriptor.
     * @return 0        if the descriptor does not represent an array type.
     */
    public static int arrayDimension(String desc) {
        int dim = 0;
        while (desc.charAt(dim) == '[')
            ++dim;

        return dim;
    }

    /**
     * Returns the descriptor of the type of the array component.
     * For example, if the given descriptor is
     * <code>"[[Ljava/lang/String;"</code> and the given dimension is 2,
     * then this method returns <code>"Ljava/lang/String;"</code>.
     *
     * @param desc the descriptor.
     * @param dim  the array dimension.
     */
    public static String toArrayComponent(String desc, int dim) {
        return desc.substring(dim);
    }

    /**
     * Computes the data size specified by the given descriptor.
     * For example, if the descriptor is "D", this method returns 2.
     *
     * <p>If the descriptor represents a method type, this method returns
     * (the size of the returned value) - (the sum of the data sizes
     * of all the parameters).  For example, if the descriptor is
     * <code>"(I)D"</code>, then this method returns 1 (= 2 - 1).
     *
     * @param desc descriptor
     */
    public static int dataSize(String desc) {
        return dataSize(desc, true);
    }

    /**
     * Computes the data size of parameters.
     * If one of the parameters is double type, the size of that parameter
     * is 2 words.  For example, if the given descriptor is
     *  <code>"(IJ)D"</code>, then this method returns 3.  The size of the
     * return type is not computed.
     * 
     * @param desc      a method descriptor.
     */
    public static int paramSize(String desc) {
        return -dataSize(desc, false);
    }

    private static int dataSize(String desc, boolean withRet) {
        int n = 0;
        char c = desc.charAt(0);
        if (c == '(') {
            int i = 1;
            for (;;) {
                c = desc.charAt(i);
                if (c == ')') {
                    c = desc.charAt(i + 1);
                    break;
                }

                boolean array = false;
                while (c == '[') {
                    array = true;
                    c = desc.charAt(++i);
                }

                if (c == 'L') {
                    i = desc.indexOf(';', i) + 1;
                    if (i <= 0)
                        throw new IndexOutOfBoundsException("bad descriptor");
                }
                else
                    ++i;

                if (!array && (c == 'J' || c == 'D'))
                    n -= 2;
                else
                    --n;
            }
        }

        if (withRet)
            if (c == 'J' || c == 'D')
                n += 2;
            else if (c != 'V')
                ++n;

        return n;
    }

    /**
     * Returns a human-readable representation of the
     * given descriptor.  For example, <code>Ljava/lang/Object;</code>
     * is converted into <code>java.lang.Object</code>.
     * <code>(I[I)V</code> is converted into <code>(int, int[])</code>
     * (the return type is ignored). 
     */
    public static String toString(String desc) {
        return PrettyPrinter.toString(desc);
    }

    static class PrettyPrinter {
        static String toString(String desc) {
            StringBuffer sbuf = new StringBuffer();
            if (desc.charAt(0) == '(') {
                int pos = 1;
                sbuf.append('(');
                while (desc.charAt(pos) != ')') {
                    if (pos > 1)
                        sbuf.append(',');

                    pos = readType(sbuf, pos, desc);
                }

                sbuf.append(')');
            }
            else
                readType(sbuf, 0, desc);

            return sbuf.toString();
        }

        static int readType(StringBuffer sbuf, int pos, String desc) {
            char c = desc.charAt(pos);
            int arrayDim = 0;
            while (c == '[') {
                arrayDim++;
                c = desc.charAt(++pos);
            }

            if (c == 'L')
                while (true) {
                    c = desc.charAt(++pos);
                    if (c == ';')
                        break;

                    if (c == '/')
                        c = '.';

                    sbuf.append(c);
                }
            else {
                Class t = toPrimitiveClass(c);
                sbuf.append(t.getName());
            }

            while (arrayDim-- > 0)
                sbuf.append("[]");

            return pos + 1;
        }
    }

    /**
     * An Iterator over a descriptor.
     */
    public static class Iterator {
        private String desc;
        private int index, curPos;
        private boolean param;

        /**
         * Constructs an iterator.
         *
         * @param s         descriptor.
         */
        public Iterator(String s) {
            desc = s;
            index = curPos = 0;
            param = false;
        }

        /**
         * Returns true if the iteration has more elements.
         */
        public boolean hasNext() {
            return index < desc.length();
        }

        /**
         * Returns true if the current element is a parameter type.
         */
        public boolean isParameter() { return param; }

        /**
         * Returns the first character of the current element.
         */
        public char currentChar() { return desc.charAt(curPos); }

        /**
         * Returns true if the current element is double or long type.
         */
        public boolean is2byte() {
            char c = currentChar();
            return c == 'D' || c == 'J';
        }

        /**
         * Returns the position of the next type character.
         * That type character becomes a new current element.
         */
        public int next() {
            int nextPos = index;
            char c = desc.charAt(nextPos);
            if (c == '(') {
                ++index;
                c = desc.charAt(++nextPos);
                param = true;
            }

            if (c == ')') {
                ++index;
                c = desc.charAt(++nextPos);
                param = false;
            }

            while (c == '[')
                c = desc.charAt(++nextPos);

            if (c == 'L') {
                nextPos = desc.indexOf(';', nextPos) + 1;
                if (nextPos <= 0)
                    throw new IndexOutOfBoundsException("bad descriptor");
            }
            else
                ++nextPos;

            curPos = index;
            index = nextPos;
            return curPos;
        }
    }
}
