package com.empowerops.linqalike;

import com.empowerops.linqalike.common.Tuple;
import com.empowerops.linqalike.delegate.*;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Geoff on 2015-10-18.
 */
public interface BiQueryable<TLeft, TRight> extends Iterable<Tuple<TLeft, TRight>>{

    Queryable<TLeft> lefts();
    Queryable<TRight> rights();

    <TAccumulate> TAccumulate aggregate(TAccumulate seed,
                                        Func3<? super TAccumulate, ? super TLeft, ? super TRight, TAccumulate> aggregator);

    boolean all(BiCondition<? super TLeft, ? super TRight> condition);

    boolean any();

    boolean any(BiCondition<? super TLeft, ? super TRight> condition);

    double average(Func2<? super TLeft, ? super TRight, Double> valueSelector);

    <TDesiredLeft> BiQueryable<TDesiredLeft, TRight> castLeft();

    <TDesiredRight> BiQueryable<TLeft, TDesiredRight> castRight();

    <TDesiredLeft> BiQueryable<TDesiredLeft, TRight> castLeft(Class<TDesiredLeft> desiredType);

    <TDesiredRight> BiQueryable<TLeft, TDesiredRight> castRight(Class<TDesiredRight> desiredType);

    int count();

    int count(BiCondition<? super TLeft, ? super TRight> condition);

    BiQueryable<TLeft, TRight> distinct();

    <TCompared> BiQueryable<TLeft, TRight> distinct(Func2<? super TLeft, ? super TRight, TCompared> comparableSelector);


    //TODO docs, smartly.
    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude);

    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1);

    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2);

    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2, Tuple<? extends TLeft, ? extends TRight> toExclude3);

    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight> toExclude0, Tuple<? extends TLeft, ? extends TRight> toExclude1, Tuple<? extends TLeft, ? extends TRight> toExclude2, Tuple<? extends TLeft, ? extends TRight> toExclude3, Tuple<? extends TLeft, ? extends TRight> toExclude4);

    BiQueryable<TLeft, TRight> except(Tuple<? extends TLeft, ? extends TRight>... toExclude);

    BiQueryable<TLeft, TRight> except(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toExclude);

    <TCompared>
    BiQueryable<TLeft, TRight> except(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toExclude,
                               Func2<? super TLeft, ? super TRight, TCompared> comparableSelector);


    Tuple<TLeft, TRight> first();

    Tuple<TLeft, TRight> first(BiCondition<? super TLeft, ? super TRight> condition);

    BiQueryable<TLeft, TRight> first(int count);

    Optional<Tuple<TLeft, TRight>> firstOrDefault();

    Optional<Tuple<TLeft, TRight>> firstOrDefault(BiCondition<? super TLeft, ? super TRight> condition);

    Tuple<TLeft, TRight> second();

    Tuple<TLeft, TRight> second(BiCondition<? super TLeft, ? super TRight> condition);

    Optional<Tuple<TLeft, TRight>> secondOrDefault();

    Optional<Tuple<TLeft, TRight>> secondOrDefault(BiCondition<? super TLeft, ? super TRight> condition);

    BiQueryable<TLeft, TRight> intersect(BiQueryable<? extends TLeft, ? extends TRight> toInclude);

    Tuple<TLeft, TRight> last();

    Tuple<TLeft, TRight> last(BiCondition<? super TLeft, ? super TRight> condition);

    BiQueryable<TLeft, TRight> last(int count);

    Optional<Tuple<TLeft, TRight>> lastOrDefault();

    Optional<Tuple<TLeft, TRight>> lastOrDefault(BiCondition<? super TLeft, ? super TRight> condition);

    TRight getValueFor(TLeft key);

    Queryable<TRight> getAll(Iterable<? extends TLeft> keys);

    <TCompared extends Comparable<TCompared>>
    Optional<TCompared> max(Func2<? super TLeft, ? super TRight, TCompared> valueSelector);

    <TCompared extends Comparable<TCompared>>
    Optional<Tuple<TLeft, TRight>> withMax(Func2<? super TLeft, ? super TRight, TCompared> valueSelector);

    <TCompared extends Comparable<TCompared>>
    Optional<TCompared> min(Func2<? super TLeft, ? super TRight, TCompared> valueSelector);

    <TCompared extends Comparable<TCompared>>
    Optional<Tuple<TLeft, TRight>> withMin(Func2<? super TLeft, ? super TRight, TCompared> valueSelector);

    <TSubclassLeft extends TLeft>
    BiQueryable<TSubclassLeft, TRight> ofLeftType(Class<TSubclassLeft> desiredLeftClass);

    <TSubclassRight extends TRight>
    BiQueryable<TLeft, TSubclassRight> ofRightType(Class<TSubclassRight> desiredRightClass);

    <TCompared extends Comparable<TCompared>>
    BiQueryable<TLeft, TRight> orderBy(Func2<? super TLeft, ? super TRight, TCompared> comparableSelector);

    /**
     * Alias for {@link #lefts()}, to be used idiomatically with {@link Queryable#pushSelect(Func1)}
     */
    Queryable<TLeft> popSelect();

    BiQueryable<TLeft, TRight> reversed();

    <TTransformed>
    Queryable<TTransformed> select(Func2<? super TLeft, ? super TRight, TTransformed> transform);

    <TLeftTransformed, TRightTransformed>
    BiQueryable<TLeftTransformed, TRightTransformed> select(Func2<? super TLeft, ? super TRight, TLeftTransformed> leftTransform,
                                                            Func2<? super TLeft, ? super TRight, TRightTransformed> rightTransform);

    <TTransformed> Queryable<TTransformed> selectMany(Func2<? super TLeft, ? super TRight, ? extends Iterable<TTransformed>> selector);

    <TLeftTransformed> BiQueryable<TLeftTransformed, TRight> selectLeft(Func2<? super TLeft, ? super TRight, TLeftTransformed> leftTransform);
    <TLeftTransformed> BiQueryable<TLeftTransformed, TRight> selectLeft(Func1<? super TLeft, TLeftTransformed> leftTransform);

    <TRightTransformed> BiQueryable<TLeft, TRightTransformed> selectRight(Func2<? super TLeft, ? super TRight, TRightTransformed> leftTransform);
    <TRightTransformed> BiQueryable<TLeft, TRightTransformed> selectRight(Func1<? super TRight, TRightTransformed> leftTransform);

    //select Many left & right, these are very similar to joins.

    Tuple<TLeft, TRight> single();

    Tuple<TLeft, TRight> single(BiCondition<? super TLeft, ? super TRight> uniqueConstraint);

    Optional<Tuple<TLeft, TRight>> singleOrDefault();

    Optional<Tuple<TLeft, TRight>> singleOrDefault(BiCondition<? super TLeft, ? super TRight> uniqueConstraint);

    boolean setEquals(BiQueryable<? extends TLeft, ? extends TRight> otherCollection);

    boolean sequenceEquals(BiQueryable<? extends TLeft, ? extends TRight> otherOrderedCollection);

    BiQueryable<TLeft, TRight> skipWhile(BiCondition<? super TLeft, ? super TRight> toExclude);

    BiQueryable<TLeft, TRight> skip(int numberToSkip);

    double sum(Func2<? super TLeft, ? super TRight, Double> valueSelector);

    BiQueryable<TLeft, TRight> immediately();

    LinqingMap<TLeft, TRight> toMap();

    <TKey, TValue> LinqingMap<TKey, TValue> toMap(Func2<? super TLeft, ? super TRight, TKey> keySelector,
                                                  Func2<? super TLeft, ? super TRight, TValue> valueSelector);

    <TDesired> TDesired[] toArray(TDesired[] typedArray);

    <TDesired> TDesired[] toArray(Func1<Integer, TDesired[]> arrayFactory);

    Object[] toArray();

    LinqingList<Tuple<TLeft, TRight>> toList();

    //TODO docs, smartly.
    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude);

    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1);

    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2);

    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3);

    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3, Tuple<? extends TLeft, ? extends TRight> toInclude4);

    BiQueryable<TLeft, TRight> union(Tuple<? extends TLeft, ? extends TRight>... toInclude);

    BiQueryable<TLeft, TRight> union(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude);

    <TCompared> BiQueryable<TLeft, TRight> union(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude,
                                                 Func2<? super TLeft, ? super TRight, TCompared> comparableSelector);

    BiQueryable<TLeft, TRight> where(BiCondition<? super TLeft, ? super TRight> condition);

    //TODO docs, smartly.
    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude);

    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1);

    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2);

    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3);

    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight> toInclude0, Tuple<? extends TLeft, ? extends TRight> toInclude1, Tuple<? extends TLeft, ? extends TRight> toInclude2, Tuple<? extends TLeft, ? extends TRight> toInclude3, Tuple<? extends TLeft, ? extends TRight> toInclude4);

    BiQueryable<TLeft, TRight> with(Tuple<? extends TLeft, ? extends TRight>... toInclude);

    BiQueryable<TLeft, TRight> with(Iterable<? extends Tuple<? extends TLeft, ? extends TRight>> toInclude);

    int size();

    boolean isSingle();

    boolean isMany();

    boolean isEmpty();

    boolean isSubsetOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSuperset);

    boolean isSupersetOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSubset);

    boolean isSubsequenceOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSupersequence);

    boolean isSupersequenceOf(BiQueryable<? extends TLeft, ? extends TRight> possibleSubsequence);

    boolean isDistinct();
    <TCompared> boolean isDistinct(Func2<? super TLeft, ? super TRight, TCompared> equatableSelector);

    <TThird, TJoined>
    Queryable<TJoined> zip(Iterable<TThird> rightElements,
                           Func3<? super TLeft, ? super TRight, ? super TThird, TJoined> joinedElementFactory);

    Queryable<Tuple<TLeft, TRight>> asTuples();

    void forEach(Action2<? super TLeft, ? super TRight> consumer);

    BiQueryable<TLeft, TRight> inlineForEach(Action2<? super TLeft, ? super TRight> consumer);
}

