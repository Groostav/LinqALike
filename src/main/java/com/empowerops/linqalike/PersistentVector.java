/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jul 5, 2007 */

package clojure.lang;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

class clojure {

    public interface IReduce {
        Object reduce(IFn f);
        Object reduce(IFn f, Object start);
    }

    public interface IHashEq{
        int hasheq();
    }

    public interface ISeq extends IPersistentCollection {

        Object first();

        ISeq next();

        ISeq more();

        ISeq cons(Object o);

    }

    public interface Seqable {
        ISeq seq();
    }

    public interface IPersistentCollection extends Seqable {

        int count();

        IPersistentCollection cons(Object o);

        IPersistentCollection empty();

        boolean equiv(Object o);
    }

    public interface ITransientCollection{

        ITransientCollection conj(Object val);

        IPersistentCollection persistent();
    }

    public interface ITransientAssociative extends ITransientCollection, ILookup{

        ITransientAssociative assoc(Object key, Object val);
    }

    public interface ITransientVector extends ITransientAssociative, Indexed{

        ITransientVector assocN(int i, Object val);

        ITransientVector pop();
    }

    public interface IEditableCollection{
        ITransientCollection asTransient();
    }


    public interface IMapEntry extends Map.Entry{
        Object key();

        Object val();
    }

    public interface Sequential {
    }

    public interface ILookup{
        Object valAt(Object key);

        Object valAt(Object key, Object notFound);
    }

    public interface IChunk extends Indexed{

        IChunk dropFirst();

        Object reduce(IFn f, Object start) ;
    }

    public interface IChunkedSeq extends ISeq, Sequential {

        IChunk chunkedFirst() ;

        ISeq chunkedNext() ;

        ISeq chunkedMore() ;

    }

    public interface Associative extends IPersistentCollection, ILookup{
        boolean containsKey(Object key);

        IMapEntry entryAt(Object key);

        Associative assoc(Object key, Object val);

    }

    public interface IPersistentMap extends Iterable, Associative, Counted{


        IPersistentMap assoc(Object key, Object val);

        IPersistentMap assocEx(Object key, Object val) ;

        IPersistentMap without(Object key) ;

    }

    public interface Counted {
        int count();
    }

    public interface Indexed extends Counted{
        Object nth(int i);

        Object nth(int i, Object notFound);
    }

    public interface Reversible{
        ISeq rseq() ;
    }

    public interface IPersistentStack extends IPersistentCollection{
        Object peek();

        IPersistentStack pop();
    }

    public interface IPersistentVector extends Associative, Sequential, IPersistentStack, Reversible, Indexed{
        int length();

        IPersistentVector assocN(int i, Object val);

        IPersistentVector cons(Object o);

    }

    public static class Util{

        static public boolean equiv(Object k1, Object k2){
            if(k1 == k2)
                return true;
            if(k1 != null)
            {
                if(k1 instanceof Number && k2 instanceof Number)
                    return Numbers.equal((Number)k1, (Number)k2);
                else if(k1 instanceof IPersistentCollection || k2 instanceof IPersistentCollection)
                    return pcequiv(k1,k2);
                return k1.equals(k2);
            }
            return false;
        }

        public interface EquivPred{
            boolean equiv(Object k1, Object k2);
        }

        static EquivPred equivNull = new EquivPred() {
            public boolean equiv(Object k1, Object k2) {
                return k2 == null;
            }
        };

        static EquivPred equivEquals = new EquivPred(){
            public boolean equiv(Object k1, Object k2) {
                return k1.equals(k2);
            }
        };

        static EquivPred equivNumber = new EquivPred(){
            public boolean equiv(Object k1, Object k2) {
                if(k2 instanceof Number)
                    return Numbers.equal((Number) k1, (Number) k2);
                return false;
            }
        };

        static EquivPred equivColl = new EquivPred(){
            public boolean equiv(Object k1, Object k2) {
                if(k1 instanceof IPersistentCollection || k2 instanceof IPersistentCollection)
                    return pcequiv(k1, k2);
                return k1.equals(k2);
            }
        };

        static public EquivPred equivPred(Object k1){
            if(k1 == null)
                return equivNull;
            else if (k1 instanceof Number)
                return equivNumber;
            else if (k1 instanceof String || k1 instanceof Symbol)
                return equivEquals;
            else if (k1 instanceof Collection || k1 instanceof Map)
                return equivColl;
            return equivEquals;
        }

        static public boolean equiv(long k1, long k2){
            return k1 == k2;
        }

        static public boolean equiv(Object k1, long k2){
            return equiv(k1, (Object)k2);
        }

        static public boolean equiv(long k1, Object k2){
            return equiv((Object)k1, k2);
        }

        static public boolean equiv(double k1, double k2){
            return k1 == k2;
        }

        static public boolean equiv(Object k1, double k2){
            return equiv(k1, (Object)k2);
        }

        static public boolean equiv(double k1, Object k2){
            return equiv((Object)k1, k2);
        }

        static public boolean equiv(boolean k1, boolean k2){
            return k1 == k2;
        }

        static public boolean equiv(Object k1, boolean k2){
            return equiv(k1, (Object)k2);
        }

        static public boolean equiv(boolean k1, Object k2){
            return equiv((Object)k1, k2);
        }

        static public boolean equiv(char c1, char c2) {
            return c1 == c2;
        }

        static public boolean pcequiv(Object k1, Object k2){
            if(k1 instanceof IPersistentCollection)
                return ((IPersistentCollection)k1).equiv(k2);
            return ((IPersistentCollection)k2).equiv(k1);
        }

        static public boolean equals(Object k1, Object k2){
            if(k1 == k2)
                return true;
            return k1 != null && k1.equals(k2);
        }

        static public boolean identical(Object k1, Object k2){
            return k1 == k2;
        }

        static public Class classOf(Object x){
            if(x != null)
                return x.getClass();
            return null;
        }

        static public int compare(Object k1, Object k2){
            if(k1 == k2)
                return 0;
            if(k1 != null)
            {
                if(k2 == null)
                    return 1;
                if(k1 instanceof Number)
                    return Numbers.compare((Number) k1, (Number) k2);
                return ((Comparable) k1).compareTo(k2);
            }
            return -1;
        }

        static public int hash(Object o){
            if(o == null)
                return 0;
            return o.hashCode();
        }

        public static int hasheq(Object o){
            if(o == null)
                return 0;
            if(o instanceof IHashEq)
                return dohasheq((IHashEq) o);
            if(o instanceof Number) {}
//                return Numbers.hasheq((Number)o); //--geoff
            if(o instanceof String) {}
//                return Murmur3.hashInt(o.hashCode()); //--geoff

            return o.hashCode();
        }

        private static int dohasheq(IHashEq o) {
            return o.hasheq();
        }

        static public int hashCombine(int seed, int hash){
            //a la boost
            seed ^= hash + 0x9e3779b9 + (seed << 6) + (seed >> 2);
            return seed;
        }

        static public boolean isPrimitive(Class c){
            return c != null && c.isPrimitive() && !(c == Void.TYPE);
        }

        static public boolean isInteger(Object x){
            return x instanceof Integer
                    || x instanceof Long
                    || x instanceof BigInteger;
        }

        static public Object ret1(Object ret, Object nil){
            return ret;
        }

        static public ISeq ret1(ISeq ret, Object nil){
            return ret;
        }

        static public <K,V> void clearCache(ReferenceQueue rq, ConcurrentHashMap<K, Reference<V>> cache){
            //cleanup any dead entries
            if(rq.poll() != null)
            {
                while(rq.poll() != null)
                    ;
                for(Map.Entry<K, Reference<V>> e : cache.entrySet())
                {
                    Reference<V> val = e.getValue();
                    if(val != null && val.get() == null)
                        cache.remove(e.getKey(), val);
                }
            }
        }

        static public RuntimeException runtimeException(String s){
            return new RuntimeException(s);
        }

        static public RuntimeException runtimeException(String s, Throwable e){
            return new RuntimeException(s, e);
        }

        /**
         * Throw even checked exceptions without being required
         * to declare them or catch them. Suggested idiom:
         * <p>
         * <code>throw sneakyThrow( some exception );</code>
         */
        static public RuntimeException sneakyThrow(Throwable t) {
            // http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html
            if (t == null)
                throw new NullPointerException();
            Util.<RuntimeException>sneakyThrow0(t);
            return null;
        }

        @SuppressWarnings("unchecked")
        static private <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
            throw (T) t;
        }


    }

    public static abstract class APersistentVector implements IPersistentVector, Iterable,
            List, IMapEntry,
            RandomAccess, Comparable,
            Serializable, IHashEq {
        int _hash = -1;
        int _hasheq = -1;

        public ISeq seq(){
            if(count() > 0)
                return new Seq(this, 0);
            return null;
        }

        public ISeq rseq(){
            if(count() > 0)
                return new RSeq(this, count() - 1);
            return null;
        }

        static boolean doEquals(IPersistentVector v, Object obj){
            if(obj instanceof IPersistentVector)
            {
                IPersistentVector ov = (IPersistentVector) obj;
                if(ov.count() != v.count())
                    return false;
                for(int i = 0;i< v.count();i++)
                {
                    if(!Util.equals(v.nth(i), ov.nth(i)))
                        return false;
                }
                return true;
            }
            else if(obj instanceof List)
            {
                Collection ma = (Collection) obj;
                if(ma.size() != v.count() || ma.hashCode() != v.hashCode())
                    return false;
                for(Iterator i1 = ((List) v).iterator(), i2 = ma.iterator();
                    i1.hasNext();)
                {
                    if(!Util.equals(i1.next(), i2.next()))
                        return false;
                }
                return true;
            }
            else
            {
                if(!(obj instanceof Sequential))
                    return false;
                ISeq ms = RT.seq(obj);
                for(int i = 0; i < v.count(); i++, ms = ms.next())
                {
                    if(ms == null || !Util.equals(v.nth(i), ms.first()))
                        return false;
                }
                if(ms != null)
                    return false;
            }

            return true;

        }

        static boolean doEquiv(IPersistentVector v, Object obj){
            if(obj instanceof IPersistentVector)
            {
                IPersistentVector ov = (IPersistentVector) obj;
                if(ov.count() != v.count())
                    return false;
                for(int i = 0;i< v.count();i++)
                {
                    if(!Util.equiv(v.nth(i), ov.nth(i)))
                        return false;
                }
                return true;
            }
            else if(obj instanceof List)
            {
                Collection ma = (Collection) obj;
                if(ma.size() != v.count())
                    return false;
                for(Iterator i1 = ((List) v).iterator(), i2 = ma.iterator();
                    i1.hasNext();)
                {
                    if(!Util.equiv(i1.next(), i2.next()))
                        return false;
                }
                return true;
            }
            else
            {
                if(!(obj instanceof Sequential))
                    return false;
                ISeq ms = RT.seq(obj);
                for(int i = 0; i < v.count(); i++, ms = ms.next())
                {
                    if(ms == null || !Util.equiv(v.nth(i), ms.first()))
                        return false;
                }
                if(ms != null)
                    return false;
            }

            return true;

        }

        @Override
        public Object getKey(){
            return key();
        }

        @Override
        public Object getValue(){
            return val();
        }

        @Override
        public Object setValue(Object value){
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object obj){
            if(obj == this)
                return true;
            return doEquals(this, obj);
        }

        public boolean equiv(Object obj){
            if(obj == this)
                return true;
            return doEquiv(this, obj);
        }

        public int hashCode(){
            if(_hash == -1)
            {
                int hash = 1;
                for(int i = 0;i<count();i++)
                {
                    Object obj = nth(i);
                    hash = 31 * hash + (obj == null ? 0 : obj.hashCode());
                }
                this._hash = hash;
            }
            return _hash;
        }

        public int hasheq(){
            if(_hasheq == -1) {
                int n;
                int hash = 1;

                for(n=0;n<count();++n)
                {
                    hash = 31 * hash + Util.hasheq(nth(n));
                }

                _hasheq = Murmur3.mixCollHash(hash, n);
            }
            return _hasheq;
        }

        public Object get(int index){
            return nth(index);
        }

        public Object nth(int i, Object notFound){
            if(i >= 0 && i < count())
                return nth(i);
            return notFound;
        }

        public Object remove(int i){
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o){
            for(int i = 0; i < count(); i++)
                if(Util.equiv(nth(i), o))
                    return i;
            return -1;
        }

        public int lastIndexOf(Object o){
            for(int i = count() - 1; i >= 0; i--)
                if(Util.equiv(nth(i), o))
                    return i;
            return -1;
        }

        public ListIterator listIterator(){
            return listIterator(0);
        }

        public ListIterator listIterator(final int index){
            return new ListIterator(){
                int nexti = index;

                public boolean hasNext(){
                    return nexti < count();
                }

                public Object next(){
                    if(nexti < count())
                        return nth(nexti++);
                    else
                        throw new NoSuchElementException();
                }

                public boolean hasPrevious(){
                    return nexti > 0;
                }

                public Object previous(){
                    if(nexti > 0)
                        return nth(--nexti);
                    else
                        throw new NoSuchElementException();
                }

                public int nextIndex(){
                    return nexti;
                }

                public int previousIndex(){
                    return nexti - 1;
                }

                public void remove(){
                    throw new UnsupportedOperationException();
                }

                public void set(Object o){
                    throw new UnsupportedOperationException();
                }

                public void add(Object o){
                    throw new UnsupportedOperationException();
                }
            };
        }

        Iterator rangedIterator(final int start, final int end){
            return new Iterator(){
                int i = start;

                public boolean hasNext(){
                    return i < end;
                }

                public Object next(){
                    if(i < end)
                        return nth(i++);
                    else
                        throw new NoSuchElementException();
                }

                public void remove(){
                    throw new UnsupportedOperationException();
                }
            };
        }

        public List subList(int fromIndex, int toIndex){
            return (List) RT.subvec(this, fromIndex, toIndex);
        }


        public Object set(int i, Object o){
            throw new UnsupportedOperationException();
        }

        public void add(int i, Object o){
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int i, Collection c){
            throw new UnsupportedOperationException();
        }


        public Object invoke(Object arg1) {
            if(Util.isInteger(arg1))
                return nth(((Number) arg1).intValue());
            throw new IllegalArgumentException("Key must be integer");
        }

        public Iterator iterator(){
            //todo - something more efficient
            return new Iterator(){
                int i = 0;

                public boolean hasNext(){
                    return i < count();
                }

                public Object next(){
                    if(i < count())
                        return nth(i++);
                    else throw new NoSuchElementException();
                }

                public void remove(){
                    throw new UnsupportedOperationException();
                }
            };
        }

        public Object peek(){
            if(count() > 0)
                return nth(count() - 1);
            return null;
        }

        public boolean containsKey(Object key){
            if(!(Util.isInteger(key)))
                return false;
            int i = ((Number) key).intValue();
            return i >= 0 && i < count();
        }

        public IMapEntry entryAt(Object key){
            if(Util.isInteger(key))
            {
                int i = ((Number) key).intValue();
                if(i >= 0 && i < count())
                    return (IMapEntry) Tuple.create(key, nth(i));
            }
            return null;
        }

        public IPersistentVector assoc(Object key, Object val){
            if(Util.isInteger(key))
            {
                int i = ((Number) key).intValue();
                return assocN(i, val);
            }
            throw new IllegalArgumentException("Key must be integer");
        }

        public Object valAt(Object key, Object notFound){
            if(Util.isInteger(key))
            {
                int i = ((Number) key).intValue();
                if(i >= 0 && i < count())
                    return nth(i);
            }
            return notFound;
        }

        public Object valAt(Object key){
            return valAt(key, null);
        }

// java.util.Collection implementation

        public Object[] toArray(){
            Object[] ret = new Object[count()];
            for(int i=0;i<count();i++)
                ret[i] = nth(i);
            return ret;
        }

        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o){
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public void clear(){
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection c){
            for(Object o : c)
            {
                if(!contains(o))
                    return false;
            }
            return true;
        }

        public Object[] toArray(Object[] a){
            return RT.seqToPassedArray(seq(), a);
        }

        public int size(){
            return count();
        }

        public boolean isEmpty(){
            return count() == 0;
        }

        public boolean contains(Object o){
            for(ISeq s = seq(); s != null; s = s.next())
            {
                if(Util.equiv(s.first(), o))
                    return true;
            }
            return false;
        }

        public int length(){
            return count();
        }

        public int compareTo(Object o){
            IPersistentVector v = (IPersistentVector) o;
            if(count() < v.count())
                return -1;
            else if(count() > v.count())
                return 1;
            for(int i = 0; i < count(); i++)
            {
                int c = Util.compare(nth(i),v.nth(i));
                if(c != 0)
                    return c;
            }
            return 0;
        }

        @Override
        public Object key(){
            if(count() == 2)
                return nth(0);
            throw new UnsupportedOperationException();
        }

        @Override
        public Object val(){
            if(count() == 2)
                return nth(1);
            throw new UnsupportedOperationException();
        }

        static class Seq extends ASeq implements IndexedSeq, IReduce{
            //todo - something more efficient
            final IPersistentVector v;
            final int i;


            public Seq(IPersistentVector v, int i){
                this.v = v;
                this.i = i;
            }

            Seq(IPersistentMap meta, IPersistentVector v, int i){
                super(meta);
                this.v = v;
                this.i = i;
            }

            public Object first(){
                return v.nth(i);
            }

            public ISeq next(){
                if(i + 1 < v.count())
                    return new APersistentVector.Seq(v, i + 1);
                return null;
            }

            public int index(){
                return i;
            }

            public int count(){
                return v.count() - i;
            }

            public APersistentVector.Seq withMeta(IPersistentMap meta){
                return new APersistentVector.Seq(meta, v, i);
            }

            public Object reduce(IFn f) {
                Object ret = v.nth(i);
                for(int x = i + 1; x < v.count(); x++) {
                    ret = f.invoke(ret, v.nth(x));
                    if (RT.isReduced(ret)) return ((IDeref)ret).deref();
                }
                return ret;
            }

            public Object reduce(IFn f, Object start) {
                Object ret = f.invoke(start, v.nth(i));
                for(int x = i + 1; x < v.count(); x++) {
                    if (RT.isReduced(ret)) return ((IDeref)ret).deref();
                    ret = f.invoke(ret, v.nth(x));
                }
                if (RT.isReduced(ret)) return ((IDeref)ret).deref();
                return ret;
            }
        }

        public static class RSeq extends ASeq implements IndexedSeq, Counted{
            final IPersistentVector v;
            final int i;

            public RSeq(IPersistentVector vector, int i){
                this.v = vector;
                this.i = i;
            }

            RSeq(IPersistentMap meta, IPersistentVector v, int i){
                super(meta);
                this.v = v;
                this.i = i;
            }

            public Object first(){
                return v.nth(i);
            }

            public ISeq next(){
                if(i > 0)
                    return new APersistentVector.RSeq(v, i - 1);
                return null;
            }

            public int index(){
                return i;
            }

            public int count(){
                return i + 1;
            }

            public APersistentVector.RSeq withMeta(IPersistentMap meta){
                return new APersistentVector.RSeq(meta, v, i);
            }
        }

        public static class SubVector extends APersistentVector implements IObj{
            public final IPersistentVector v;
            public final int start;
            public final int end;
            final IPersistentMap _meta;



            public SubVector(IPersistentMap meta, IPersistentVector v, int start, int end){
                this._meta = meta;

                if(v instanceof APersistentVector.SubVector)
                {
                    APersistentVector.SubVector sv = (APersistentVector.SubVector) v;
                    start += sv.start;
                    end += sv.start;
                    v = sv.v;
                }
                this.v = v;
                this.start = start;
                this.end = end;
            }

            public Iterator iterator(){
                if (v instanceof APersistentVector) {
                    return ((APersistentVector)v).rangedIterator(start,end);
                }
                return super.iterator();
            }

            public Object nth(int i){
                if((start + i >= end) || (i < 0))
                    throw new IndexOutOfBoundsException();
                return v.nth(start + i);
            }

            public IPersistentVector assocN(int i, Object val){
                if(start + i > end)
                    throw new IndexOutOfBoundsException();
                else if(start + i == end)
                    return cons(val);
                return new SubVector(_meta, v.assocN(start + i, val), start, end);
            }

            public int count(){
                return end - start;
            }

            public IPersistentVector cons(Object o){
                return new SubVector(_meta, v.assocN(end, o), start, end + 1);
            }

            public IPersistentCollection empty(){
                return PersistentVector.EMPTY.withMeta(meta());
            }

            public IPersistentStack pop(){
                if(end - 1 == start)
                {
                    return PersistentVector.EMPTY;
                }
                return new SubVector(_meta, v, start, end - 1);
            }

            public SubVector withMeta(IPersistentMap meta){
                if(meta == _meta)
                    return this;
                return new SubVector(meta, v, start, end);
            }

            public IPersistentMap meta(){
                return _meta;
            }
        }
    }

    public static class PersistentVector extends APersistentVector implements IEditableCollection, IReduce {

        public static class Node implements Serializable {
            transient public final AtomicReference<Thread> edit;
            public final Object[] array;

            public Node(AtomicReference<Thread> edit, Object[] array) {
                this.edit = edit;
                this.array = array;
            }

            Node(AtomicReference<Thread> edit) {
                this.edit = edit;
                this.array = new Object[32];
            }
        }

        final static AtomicReference<Thread> NOEDIT = new AtomicReference<Thread>(null);
        public final static Node EMPTY_NODE = new Node(NOEDIT, new Object[32]);

        final int cnt;
        public final int shift;
        public final Node root;
        public final Object[] tail;
        final IPersistentMap _meta;


        public final static PersistentVector EMPTY = new PersistentVector(0, 5, EMPTY_NODE, new Object[]{});

        private static final IFn TRANSIENT_VECTOR_CONJ = new AFn() {
            public Object invoke(Object coll, Object val) {
                return ((ITransientVector) coll).conj(val);
            }

            public Object invoke(Object coll) {
                return coll;
            }
        };

        static public PersistentVector adopt(Object[] items) {
            return new PersistentVector(items.length, 5, EMPTY_NODE, items);
        }

        static public PersistentVector create(IReduceInit items) {
            TransientVector ret = EMPTY.asTransient();
            items.reduce(TRANSIENT_VECTOR_CONJ, ret);
            return ret.persistent();
        }

        static public PersistentVector create(ISeq items) {
            Object[] arr = new Object[32];
            int i = 0;
            for (; items != null && i < 32; items = items.next())
                arr[i++] = items.first();

            if (items != null) {  // >32, construct with array directly
                PersistentVector start = new PersistentVector(32, 5, EMPTY_NODE, arr);
                TransientVector ret = start.asTransient();
                for (; items != null; items = items.next())
                    ret = ret.conj(items.first());
                return ret.persistent();
            } else if (i == 32) {   // exactly 32, skip copy
                return new PersistentVector(32, 5, EMPTY_NODE, arr);
            } else {  // <32, copy to minimum array and construct
                Object[] arr2 = new Object[i];
                System.arraycopy(arr, 0, arr2, 0, i);
                return new PersistentVector(i, 5, EMPTY_NODE, arr2);
            }
        }

        static public PersistentVector create(List list) {
            int size = list.size();
            if (size <= 32)
                return new PersistentVector(size, 5, PersistentVector.EMPTY_NODE, list.toArray());

            TransientVector ret = EMPTY.asTransient();
            for (int i = 0; i < size; i++)
                ret = ret.conj(list.get(i));
            return ret.persistent();
        }

        static public PersistentVector create(Iterable items) {
            // optimize common case
            if (items instanceof ArrayList)
                return create((ArrayList) items);

            Iterator iter = items.iterator();
            TransientVector ret = EMPTY.asTransient();
            while (iter.hasNext())
                ret = ret.conj(iter.next());
            return ret.persistent();
        }

        static public PersistentVector create(Object... items) {
            TransientVector ret = EMPTY.asTransient();
            for (Object item : items)
                ret = ret.conj(item);
            return ret.persistent();
        }

        PersistentVector(int cnt, int shift, Node root, Object[] tail) {
            this._meta = null;
            this.cnt = cnt;
            this.shift = shift;
            this.root = root;
            this.tail = tail;
        }


        PersistentVector(IPersistentMap meta, int cnt, int shift, Node root, Object[] tail) {
            this._meta = meta;
            this.cnt = cnt;
            this.shift = shift;
            this.root = root;
            this.tail = tail;
        }

        public TransientVector asTransient() {
            return new TransientVector(this);
        }

        final int tailoff() {
            if (cnt < 32)
                return 0;
            return ((cnt - 1) >>> 5) << 5;
        }

        public Object[] arrayFor(int i) {
            if (i >= 0 && i < cnt) {
                if (i >= tailoff())
                    return tail;
                Node node = root;
                for (int level = shift; level > 0; level -= 5)
                    node = (Node) node.array[(i >>> level) & 0x01f];
                return node.array;
            }
            throw new IndexOutOfBoundsException();
        }

        public Object nth(int i) {
            Object[] node = arrayFor(i);
            return node[i & 0x01f];
        }

        public Object nth(int i, Object notFound) {
            if (i >= 0 && i < cnt)
                return nth(i);
            return notFound;
        }

        public PersistentVector assocN(int i, Object val) {
            if (i >= 0 && i < cnt) {
                if (i >= tailoff()) {
                    Object[] newTail = new Object[tail.length];
                    System.arraycopy(tail, 0, newTail, 0, tail.length);
                    newTail[i & 0x01f] = val;

                    return new PersistentVector(meta(), cnt, shift, root, newTail);
                }

                return new PersistentVector(meta(), cnt, shift, doAssoc(shift, root, i, val), tail);
            }
            if (i == cnt)
                return cons(val);
            throw new IndexOutOfBoundsException();
        }

        private static Node doAssoc(int level, Node node, int i, Object val) {
            Node ret = new Node(node.edit, node.array.clone());
            if (level == 0) {
                ret.array[i & 0x01f] = val;
            } else {
                int subidx = (i >>> level) & 0x01f;
                ret.array[subidx] = doAssoc(level - 5, (Node) node.array[subidx], i, val);
            }
            return ret;
        }

        public int count() {
            return cnt;
        }

        public PersistentVector withMeta(IPersistentMap meta) {
            return new PersistentVector(meta, cnt, shift, root, tail);
        }

        public IPersistentMap meta() {
            return _meta;
        }


        public PersistentVector cons(Object val) {
            //room in tail?
            //	if(tail.length < 32)
            if (cnt - tailoff() < 32) {
                Object[] newTail = new Object[tail.length + 1];
                System.arraycopy(tail, 0, newTail, 0, tail.length);
                newTail[tail.length] = val;
                return new PersistentVector(meta(), cnt + 1, shift, root, newTail);
            }
            //full tail, push into tree
            Node newroot;
            Node tailnode = new Node(root.edit, tail);
            int newshift = shift;
            //overflow root?
            if ((cnt >>> 5) > (1 << shift)) {
                newroot = new Node(root.edit);
                newroot.array[0] = root;
                newroot.array[1] = newPath(root.edit, shift, tailnode);
                newshift += 5;
            } else
                newroot = pushTail(shift, root, tailnode);
            return new PersistentVector(meta(), cnt + 1, newshift, newroot, new Object[]{val});
        }

        private Node pushTail(int level, Node parent, Node tailnode) {
            //if parent is leaf, insert node,
            // else does it map to an existing child? -> nodeToInsert = pushNode one more level
            // else alloc new path
            //return  nodeToInsert placed in copy of parent
            int subidx = ((cnt - 1) >>> level) & 0x01f;
            Node ret = new Node(parent.edit, parent.array.clone());
            Node nodeToInsert;
            if (level == 5) {
                nodeToInsert = tailnode;
            } else {
                Node child = (Node) parent.array[subidx];
                nodeToInsert = (child != null) ?
                        pushTail(level - 5, child, tailnode)
                        : newPath(root.edit, level - 5, tailnode);
            }
            ret.array[subidx] = nodeToInsert;
            return ret;
        }

        private static Node newPath(AtomicReference<Thread> edit, int level, Node node) {
            if (level == 0)
                return node;
            Node ret = new Node(edit);
            ret.array[0] = newPath(edit, level - 5, node);
            return ret;
        }

        public IChunkedSeq chunkedSeq() {
            if (count() == 0)
                return null;
            return new ChunkedSeq(this, 0, 0);
        }

        public ISeq seq() {
            return chunkedSeq();
        }

        @Override
        Iterator rangedIterator(final int start, final int end) {
            return new Iterator() {
                int i = start;
                int base = i - (i % 32);
                Object[] array = (start < count()) ? arrayFor(i) : null;

                public boolean hasNext() {
                    return i < end;
                }

                public Object next() {
                    if (i < end) {
                        if (i - base == 32) {
                            array = arrayFor(i);
                            base += 32;
                        }
                        return array[i++ & 0x01f];
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public Iterator iterator() {
            return rangedIterator(0, count());
        }

        public Object reduce(IFn f) {
            Object init;
            if (cnt > 0)
                init = arrayFor(0)[0];
            else
                return f.invoke();
            int step = 0;
            for (int i = 0; i < cnt; i += step) {
                Object[] array = arrayFor(i);
                for (int j = (i == 0) ? 1 : 0; j < array.length; ++j) {
                    init = f.invoke(init, array[j]);
                    if (RT.isReduced(init))
                        return ((IDeref) init).deref();
                }
                step = array.length;
            }
            return init;
        }

        public Object reduce(IFn f, Object init) {
            int step = 0;
            for (int i = 0; i < cnt; i += step) {
                Object[] array = arrayFor(i);
                for (int j = 0; j < array.length; ++j) {
                    init = f.invoke(init, array[j]);
                    if (RT.isReduced(init))
                        return ((IDeref) init).deref();
                }
                step = array.length;
            }
            return init;
        }

        public Object kvreduce(IFn f, Object init) {
            int step = 0;
            for (int i = 0; i < cnt; i += step) {
                Object[] array = arrayFor(i);
                for (int j = 0; j < array.length; ++j) {
                    init = f.invoke(init, j + i, array[j]);
                    if (RT.isReduced(init))
                        return ((IDeref) init).deref();
                }
                step = array.length;
            }
            return init;
        }

        static public final class ChunkedSeq extends ASeq implements IChunkedSeq, Counted {

            public final PersistentVector vec;
            final Object[] node;
            final int i;
            public final int offset;

            public ChunkedSeq(PersistentVector vec, int i, int offset) {
                this.vec = vec;
                this.i = i;
                this.offset = offset;
                this.node = vec.arrayFor(i);
            }

            ChunkedSeq(IPersistentMap meta, PersistentVector vec, Object[] node, int i, int offset) {
                super(meta);
                this.vec = vec;
                this.node = node;
                this.i = i;
                this.offset = offset;
            }

            ChunkedSeq(PersistentVector vec, Object[] node, int i, int offset) {
                this.vec = vec;
                this.node = node;
                this.i = i;
                this.offset = offset;
            }

            public IChunk chunkedFirst() {
                return new ArrayChunk(node, offset);
            }

            public ISeq chunkedNext() {
                if (i + node.length < vec.cnt)
                    return new ChunkedSeq(vec, i + node.length, 0);
                return null;
            }

            public ISeq chunkedMore() {
                ISeq s = chunkedNext();
                if (s == null)
                    throw new UnsupportedOperationException();
//                    return PersistentList.EMPTY; //--Geoff
                return s;
            }

            //what is this meta nonsense? --Geoff
//            public Object withMeta(IPersistentMap meta) {
//                if (meta == this._meta)
//                    return this;
//                return new ChunkedSeq(meta, vec, node, i, offset);
//            }

            public Object first() {
                return node[offset];
            }

            public ISeq next() {
                if (offset + 1 < node.length)
                    return new ChunkedSeq(vec, node, i, offset + 1);
                return chunkedNext();
            }

            public int count() {
                return vec.cnt - (i + offset);
            }
        }

        public IPersistentCollection empty() {
            return EMPTY.withMeta(meta());
        }


        public PersistentVector pop() {
            if (cnt == 0)
                throw new IllegalStateException("Can't pop empty vector");
            if (cnt == 1)
                return EMPTY.withMeta(meta());
            //if(tail.length > 1)
            if (cnt - tailoff() > 1) {
                Object[] newTail = new Object[tail.length - 1];
                System.arraycopy(tail, 0, newTail, 0, newTail.length);
                return new PersistentVector(meta(), cnt - 1, shift, root, newTail);
            }
            Object[] newtail = arrayFor(cnt - 2);

            Node newroot = popTail(shift, root);
            int newshift = shift;
            if (newroot == null) {
                newroot = EMPTY_NODE;
            }
            if (shift > 5 && newroot.array[1] == null) {
                newroot = (Node) newroot.array[0];
                newshift -= 5;
            }
            return new PersistentVector(meta(), cnt - 1, newshift, newroot, newtail);
        }

        private Node popTail(int level, Node node) {
            int subidx = ((cnt - 2) >>> level) & 0x01f;
            if (level > 5) {
                Node newchild = popTail(level - 5, (Node) node.array[subidx]);
                if (newchild == null && subidx == 0)
                    return null;
                else {
                    Node ret = new Node(root.edit, node.array.clone());
                    ret.array[subidx] = newchild;
                    return ret;
                }
            } else if (subidx == 0)
                return null;
            else {
                Node ret = new Node(root.edit, node.array.clone());
                ret.array[subidx] = null;
                return ret;
            }
        }

        static final class TransientVector implements ITransientVector, Counted {
            volatile int cnt;
            volatile int shift;
            volatile Node root;
            volatile Object[] tail;

            TransientVector(int cnt, int shift, Node root, Object[] tail) {
                this.cnt = cnt;
                this.shift = shift;
                this.root = root;
                this.tail = tail;
            }

            TransientVector(PersistentVector v) {
                this(v.cnt, v.shift, editableRoot(v.root), editableTail(v.tail));
            }

            public int count() {
                ensureEditable();
                return cnt;
            }

            Node ensureEditable(Node node) {
                if (node.edit == root.edit)
                    return node;
                return new Node(root.edit, node.array.clone());
            }

            void ensureEditable() {
                if (root.edit.get() == null)
                    throw new IllegalAccessError("Transient used after persistent! call");

                //		root = editableRoot(root);
                //		tail = editableTail(tail);
            }

            static Node editableRoot(Node node) {
                return new Node(new AtomicReference<Thread>(Thread.currentThread()), node.array.clone());
            }

            public PersistentVector persistent() {
                ensureEditable();

                root.edit.set(null);
                Object[] trimmedTail = new Object[cnt - tailoff()];
                System.arraycopy(tail, 0, trimmedTail, 0, trimmedTail.length);
                return new PersistentVector(cnt, shift, root, trimmedTail);
            }

            static Object[] editableTail(Object[] tl) {
                Object[] ret = new Object[32];
                System.arraycopy(tl, 0, ret, 0, tl.length);
                return ret;
            }

            public TransientVector conj(Object val) {
                ensureEditable();
                int i = cnt;
                //room in tail?
                if (i - tailoff() < 32) {
                    tail[i & 0x01f] = val;
                    ++cnt;
                    return this;
                }
                //full tail, push into tree
                Node newroot;
                Node tailnode = new Node(root.edit, tail);
                tail = new Object[32];
                tail[0] = val;
                int newshift = shift;
                //overflow root?
                if ((cnt >>> 5) > (1 << shift)) {
                    newroot = new Node(root.edit);
                    newroot.array[0] = root;
                    newroot.array[1] = newPath(root.edit, shift, tailnode);
                    newshift += 5;
                } else
                    newroot = pushTail(shift, root, tailnode);
                root = newroot;
                shift = newshift;
                ++cnt;
                return this;
            }

            private Node pushTail(int level, Node parent, Node tailnode) {
                //if parent is leaf, insert node,
                // else does it map to an existing child? -> nodeToInsert = pushNode one more level
                // else alloc new path
                //return  nodeToInsert placed in parent
                parent = ensureEditable(parent);
                int subidx = ((cnt - 1) >>> level) & 0x01f;
                Node ret = parent;
                Node nodeToInsert;
                if (level == 5) {
                    nodeToInsert = tailnode;
                } else {
                    Node child = (Node) parent.array[subidx];
                    nodeToInsert = (child != null) ?
                            pushTail(level - 5, child, tailnode)
                            : newPath(root.edit, level - 5, tailnode);
                }
                ret.array[subidx] = nodeToInsert;
                return ret;
            }

            final private int tailoff() {
                if (cnt < 32)
                    return 0;
                return ((cnt - 1) >>> 5) << 5;
            }

            private Object[] arrayFor(int i) {
                if (i >= 0 && i < cnt) {
                    if (i >= tailoff())
                        return tail;
                    Node node = root;
                    for (int level = shift; level > 0; level -= 5)
                        node = (Node) node.array[(i >>> level) & 0x01f];
                    return node.array;
                }
                throw new IndexOutOfBoundsException();
            }

            private Object[] editableArrayFor(int i) {
                if (i >= 0 && i < cnt) {
                    if (i >= tailoff())
                        return tail;
                    Node node = root;
                    for (int level = shift; level > 0; level -= 5)
                        node = ensureEditable((Node) node.array[(i >>> level) & 0x01f]);
                    return node.array;
                }
                throw new IndexOutOfBoundsException();
            }

            public Object valAt(Object key) {
                //note - relies on ensureEditable in 2-arg valAt
                return valAt(key, null);
            }

            public Object valAt(Object key, Object notFound) {
                ensureEditable();
                if (Util.isInteger(key)) {
                    int i = ((Number) key).intValue();
                    if (i >= 0 && i < cnt)
                        return nth(i);
                }
                return notFound;
            }

            public Object invoke(Object arg1) {
                //note - relies on ensureEditable in nth
                if (Util.isInteger(arg1))
                    return nth(((Number) arg1).intValue());
                throw new IllegalArgumentException("Key must be integer");
            }

            public Object nth(int i) {
                ensureEditable();
                Object[] node = arrayFor(i);
                return node[i & 0x01f];
            }

            public Object nth(int i, Object notFound) {
                if (i >= 0 && i < count())
                    return nth(i);
                return notFound;
            }

            public TransientVector assocN(int i, Object val) {
                ensureEditable();
                if (i >= 0 && i < cnt) {
                    if (i >= tailoff()) {
                        tail[i & 0x01f] = val;
                        return this;
                    }

                    root = doAssoc(shift, root, i, val);
                    return this;
                }
                if (i == cnt)
                    return conj(val);
                throw new IndexOutOfBoundsException();
            }

            public TransientVector assoc(Object key, Object val) {
                //note - relies on ensureEditable in assocN
                if (Util.isInteger(key)) {
                    int i = ((Number) key).intValue();
                    return assocN(i, val);
                }
                throw new IllegalArgumentException("Key must be integer");
            }

            private Node doAssoc(int level, Node node, int i, Object val) {
                node = ensureEditable(node);
                Node ret = node;
                if (level == 0) {
                    ret.array[i & 0x01f] = val;
                } else {
                    int subidx = (i >>> level) & 0x01f;
                    ret.array[subidx] = doAssoc(level - 5, (Node) node.array[subidx], i, val);
                }
                return ret;
            }

            public TransientVector pop() {
                ensureEditable();
                if (cnt == 0)
                    throw new IllegalStateException("Can't pop empty vector");
                if (cnt == 1) {
                    cnt = 0;
                    return this;
                }
                int i = cnt - 1;
                //pop in tail?
                if ((i & 0x01f) > 0) {
                    --cnt;
                    return this;
                }

                Object[] newtail = editableArrayFor(cnt - 2);

                Node newroot = popTail(shift, root);
                int newshift = shift;
                if (newroot == null) {
                    newroot = new Node(root.edit);
                }
                if (shift > 5 && newroot.array[1] == null) {
                    newroot = ensureEditable((Node) newroot.array[0]);
                    newshift -= 5;
                }
                root = newroot;
                shift = newshift;
                --cnt;
                tail = newtail;
                return this;
            }

            private Node popTail(int level, Node node) {
                node = ensureEditable(node);
                int subidx = ((cnt - 2) >>> level) & 0x01f;
                if (level > 5) {
                    Node newchild = popTail(level - 5, (Node) node.array[subidx]);
                    if (newchild == null && subidx == 0)
                        return null;
                    else {
                        Node ret = node;
                        ret.array[subidx] = newchild;
                        return ret;
                    }
                } else if (subidx == 0)
                    return null;
                else {
                    Node ret = node;
                    ret.array[subidx] = null;
                    return ret;
                }
            }
        }
    /*
    static public void main(String[] args){
        if(args.length != 3)
            {
            System.err.println("Usage: PersistentVector size writes reads");
            return;
            }
        int size = Integer.parseInt(args[0]);
        int writes = Integer.parseInt(args[1]);
        int reads = Integer.parseInt(args[2]);
    //	Vector v = new Vector(size);
        ArrayList v = new ArrayList(size);
    //	v.setSize(size);
        //PersistentArray p = new PersistentArray(size);
        PersistentVector p = PersistentVector.EMPTY;
    //	MutableVector mp = p.mutable();

        for(int i = 0; i < size; i++)
            {
            v.add(i);
    //		v.set(i, i);
            //p = p.set(i, 0);
            p = p.cons(i);
    //		mp = mp.conj(i);
            }

        Random rand;

        rand = new Random(42);
        long tv = 0;
        System.out.println("ArrayList");
        long startTime = System.nanoTime();
        for(int i = 0; i < writes; i++)
            {
            v.set(rand.nextInt(size), i);
            }
        for(int i = 0; i < reads; i++)
            {
            tv += (Integer) v.get(rand.nextInt(size));
            }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("time: " + estimatedTime / 1000000);
        System.out.println("PersistentVector");
        rand = new Random(42);
        startTime = System.nanoTime();
        long tp = 0;

    //	PersistentVector oldp = p;
        //Random rand2 = new Random(42);

        MutableVector mp = p.mutable();
        for(int i = 0; i < writes; i++)
            {
    //		p = p.assocN(rand.nextInt(size), i);
            mp = mp.assocN(rand.nextInt(size), i);
    //		mp = mp.assoc(rand.nextInt(size), i);
            //dummy set to force perverse branching
            //oldp =	oldp.assocN(rand2.nextInt(size), i);
            }
        for(int i = 0; i < reads; i++)
            {
    //		tp += (Integer) p.nth(rand.nextInt(size));
            tp += (Integer) mp.nth(rand.nextInt(size));
            }
    //	p = mp.immutable();
        //mp.cons(42);
        estimatedTime = System.nanoTime() - startTime;
        System.out.println("time: " + estimatedTime / 1000000);
        for(int i = 0; i < size / 2; i++)
            {
            mp = mp.pop();
    //		p = p.pop();
            v.remove(v.size() - 1);
            }
        p = (PersistentVector) mp.immutable();
        //mp.pop();  //should fail
        for(int i = 0; i < size / 2; i++)
            {
            tp += (Integer) p.nth(i);
            tv += (Integer) v.get(i);
            }
        System.out.println("Done: " + tv + ", " + tp);

    }
    //  */
    }

    /**
     * <p><code>IFn</code> provides complete access to invoking
     * any of Clojure's <a href="http://clojure.github.io/clojure/">API</a>s.
     * You can also access any other library written in Clojure, after adding
     * either its source or compiled form to the classpath.</p>
     */
    public interface IFn extends Callable, Runnable {

        public Object invoke();

        public Object invoke(Object arg1);

        public Object invoke(Object arg1, Object arg2);

        public Object invoke(Object arg1, Object arg2, Object arg3);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7)
                ;

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                             Object arg8);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                             Object arg8, Object arg9);

        public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                             Object arg8, Object arg9, Object arg10);

        public Object applyTo(ISeq arglist);

    }

    public static abstract class ASeq implements ISeq, Sequential, List, Serializable, IHashEq {
        transient int _hash;
        transient int _hasheq;

        public IPersistentCollection empty(){
            return PersistentList.EMPTY;
        }

        protected ASeq(IPersistentMap meta){
            super(meta);
        }


        protected ASeq(){
        }

        public boolean equiv(Object obj){

            if(!(obj instanceof Sequential || obj instanceof List))
                return false;
            ISeq ms = RT.seq(obj);
            for(ISeq s = seq(); s != null; s = s.next(), ms = ms.next())
            {
                if(ms == null || !Util.equiv(s.first(), ms.first()))
                    return false;
            }
            return ms == null;

        }

        public boolean equals(Object obj){
            if(this == obj) return true;
            if(!(obj instanceof Sequential || obj instanceof List))
                return false;
            ISeq ms = RT.seq(obj);
            for(ISeq s = seq(); s != null; s = s.next(), ms = ms.next())
            {
                if(ms == null || !Util.equals(s.first(), ms.first()))
                    return false;
            }
            return ms == null;

        }

        public int hashCode(){
            if(_hash == 0)
            {
                int hash = 1;
                for(ISeq s = seq(); s != null; s = s.next())
                {
                    hash = 31 * hash + (s.first() == null ? 0 : s.first().hashCode());
                }
                this._hash = hash;
            }
            return _hash;
        }

        public int hasheq(){
            if(_hasheq == 0)
            {
		int hash = 1;
		for(ISeq s = seq(); s != null; s = s.next())
			{
			hash = 31 * hash + Util.hasheq(s.first());
			}
		this._hasheq = hash;
//                _hasheq  = Murmur3.hashOrdered(this);
            }
            return _hasheq;
        }


//public Object reduce(IFn f) {
//	Object ret = first();
//	for(ISeq s = rest(); s != null; s = s.rest())
//		ret = f.invoke(ret, s.first());
//	return ret;
//}
//
//public Object reduce(IFn f, Object start) {
//	Object ret = f.invoke(start, first());
//	for(ISeq s = rest(); s != null; s = s.rest())
//		ret = f.invoke(ret, s.first());
//	return ret;
//}

//public Object peek(){
//	return first();
//}
//
//public IPersistentList pop(){
//	return rest();
//}

        public int count(){
            int i = 1;
            for(ISeq s = next(); s != null; s = s.next(), i++)
                if(s instanceof Counted)
                    return i + s.count();
            return i;
        }

        final public ISeq seq(){
            return this;
        }

        public ISeq cons(Object o){
            return new Cons(o, this);
        }

        public ISeq more(){
            ISeq s = next();
            if(s == null)
                return PersistentList.EMPTY;
            return s;
        }

//final public ISeq rest(){
//    Seqable m = more();
//    if(m == null)
//        return null;
//    return m.seq();
//}

// java.util.Collection implementation

        public Object[] toArray(){
            return RT.seqToArray(seq());
        }

        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o){
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public void clear(){
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c){
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection c){
            for(Object o : c)
            {
                if(!contains(o))
                    return false;
            }
            return true;
        }

        public Object[] toArray(Object[] a){
            return RT.seqToPassedArray(seq(), a);
        }

        public int size(){
            return count();
        }

        public boolean isEmpty(){
            return seq() == null;
        }

        public boolean contains(Object o){
            for(ISeq s = seq(); s != null; s = s.next())
            {
                if(Util.equiv(s.first(), o))
                    return true;
            }
            return false;
        }


        public Iterator iterator(){
            return new SeqIterator(this);
        }



        //////////// List stuff /////////////////
        private List reify(){
            return Collections.unmodifiableList(new ArrayList(this));
        }

        public List subList(int fromIndex, int toIndex){
            return reify().subList(fromIndex, toIndex);
        }

        public Object set(int index, Object element){
            throw new UnsupportedOperationException();
        }

        public Object remove(int index){
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o){
            ISeq s = seq();
            for(int i = 0; s != null; s = s.next(), i++)
            {
                if(Util.equiv(s.first(), o))
                    return i;
            }
            return -1;
        }

        public int lastIndexOf(Object o){
            return reify().lastIndexOf(o);
        }

        public ListIterator listIterator(){
            return reify().listIterator();
        }

        public ListIterator listIterator(int index){
            return reify().listIterator(index);
        }

        public Object get(int index){
            return RT.nth(this, index);
        }

        public void add(int index, Object element){
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection c){
            throw new UnsupportedOperationException();
        }

    }

    public final class ArrayChunk implements IChunk, Serializable {

        final Object[] array;
        final int off;
        final int end;

        public ArrayChunk(Object[] array){
            this(array, 0, array.length);
        }

        public ArrayChunk(Object[] array, int off){
            this(array, off, array.length);
        }

        public ArrayChunk(Object[] array, int off, int end){
            this.array = array;
            this.off = off;
            this.end = end;
        }

        public Object nth(int i){
            return array[off + i];
        }

        public Object nth(int i, Object notFound){
            if(i >= 0 && i < count())
                return nth(i);
            return notFound;
        }

        public int count(){
            return end - off;
        }

        public IChunk dropFirst(){
            if(off==end)
                throw new IllegalStateException("dropFirst of empty chunk");
            return new ArrayChunk(array, off + 1, end);
        }

        public Object reduce(IFn f, Object start) {
            Object ret = f.invoke(start, array[off]);
            if(RT.isReduced(ret))
                return ret;
            for(int x = off + 1; x < end; x++)
            {
                ret = f.invoke(ret, array[x]);
                if(RT.isReduced(ret))
                    return ret;
            }
            return ret;
        }
    }

    public class SeqIterator implements Iterator{

        static final Object START = new Object();
        Object seq;
        Object next;

        public SeqIterator(Object o){
            seq = START;
            next = o;
        }

        //preserved for binary compatibility
        public SeqIterator(ISeq o){
            seq = START;
            next = o;
        }

        public boolean hasNext(){
            if(seq == START){
                seq = null;
                next = RT.seq(next);
            }
            else if(seq == next)
                next = RT.next(seq);
            return next != null;
        }

        public Object next() throws NoSuchElementException {
            if(!hasNext())
                throw new NoSuchElementException();
            seq = next;
            return RT.first(next);
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
