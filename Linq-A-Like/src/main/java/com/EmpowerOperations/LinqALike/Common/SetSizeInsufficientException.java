package com.EmpowerOperations.LinqALike.Common;

import java.lang.IndexOutOfBoundsException;public class SetSizeInsufficientException extends IndexOutOfBoundsException {
    public SetSizeInsufficientException(int neededSize, int actualSize) {
        super("attempted to access into a set that was too small. The require size was " + neededSize + ", " +
                "but the set was of size " + actualSize);
    }
}
