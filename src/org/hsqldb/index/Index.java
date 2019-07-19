/* Copyright (c) 2001-2019, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb.index;

import org.hsqldb.RangeVariable.RangeVariableConditions;
import org.hsqldb.Row;
import org.hsqldb.SchemaObject;
import org.hsqldb.Session;
import org.hsqldb.TableBase;
import org.hsqldb.navigator.RowIterator;
import org.hsqldb.persist.PersistentStore;
import org.hsqldb.types.Type;

/**
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 * @version 2.5.0
 * @since 1.9.0
 */
public interface Index extends SchemaObject {

    int INDEX_NONE       = 0;
    int INDEX_NON_UNIQUE = 1;
    int INDEX_UNIQUE     = 2;

    //
    double minimumSelectivity = 16;
    double cachedFactor       = 8;
    int    probeDepth         = 4;

    //
    Index[]    emptyArray    = new Index[]{};
    IndexUse[] emptyUseArray = new IndexUse[]{};

    IndexUse[] asArray();

    RowIterator emptyIterator();

    int getPosition();

    void setPosition(int position);

    long getPersistenceId();

    /**
     * Returns the count of visible columns used
     */
    int getColumnCount();

    /**
     * Is this a PRIMARY_KEY index?
     */
    boolean isPrimaryKey();

    /**
     * Is this a UNIQUE index?
     */
    boolean isUnique();

    /**
     * Does this index belong to a constraint?
     */
    boolean isConstraint();

    /**
     * Returns the array containing column indexes for index
     *
     * @return int[]
     */
    int[] getColumns();

    /**
     * Returns the array containing column indexes for index
     *
     * @return Type[]
     */
    Type[] getColumnTypes();

    /**
     * Returns the count of visible columns used
     *
     * @return boolean[]
     */
    boolean[] getColumnDesc();

    /**
     * Returns the array containing 0, 1, .. column indexes
     *
     * @return int[]
     */
    int[] getDefaultColumnMap();

    /**
     * Returns a value indicating the order of different types of index in
     * the list of indexes for a table. The position of the groups of Indexes
     * in the list in ascending order is as follows:
     *
     * primary key index
     * unique constraint indexes
     * autogenerated foreign key indexes for FK's that reference this table or
     *  tables created before this table
     * user created indexes (CREATE INDEX)
     * autogenerated foreign key indexes for FK's that reference tables created
     *  after this table
     *
     * Among a group of indexes, the order is based on the order of creation
     * of the index.
     *
     * @return ordinal value
     */
    int getIndexOrderValue();

    boolean isForward();

    void setTable(TableBase table);

    TableBase getTable();

    void setClustered(boolean clustered);

    boolean isClustered();

    /**
     * Returns the node count.
     */
    long size(Session session, PersistentStore store);

    long sizeUnique(PersistentStore store);

    double[] searchCost(Session session, PersistentStore store);

    long getNodeCount(Session session, PersistentStore store);

    boolean isEmpty(PersistentStore store);

    int checkIndex(Session session, PersistentStore store);

    /**
     * Insert a node into the index
     */
    void insert(Session session, PersistentStore store, Row row
                );

    void delete(Session session, PersistentStore store, Row row);

    boolean existsParent(Session session, PersistentStore store,
                         Object[] rowdata, int[] rowColMap);

    /**
     * Return the first node equal to the indexdata object. The rowdata has the
     * same column mapping as this index.
     *
     * @param session session object
     * @param store store object
     * @param rowdata Object[]
     * @param matchCount count of columns to match
     * @param distinctCount int
     * @param compareType int
     * @param reversed boolean
     * @param map boolean[]
     * @return iterator
     */
    RowIterator findFirstRow(Session session, PersistentStore store,
                             Object[] rowdata, int matchCount,
                             int distinctCount, int compareType,
                             boolean reversed, boolean[] map);

    /**
     * Return the first node equal to the rowdata object.
     * The rowdata has the same column mapping as this table.
     *
     * @param session session object
     * @param store store object
     * @param rowdata array containing table row data
     * @return iterator
     */
    RowIterator findFirstRow(Session session, PersistentStore store,
                             Object[] rowdata);

    /**
     * Return the first node equal to the rowdata object. The rowdata has the
     * column mapping provided in rowColMap.
     *
     * @param session session object
     * @param store store object
     * @param rowdata array containing table row data
     * @param rowColMap int[]
     * @return iterator
     */
    RowIterator findFirstRow(Session session, PersistentStore store,
                             Object[] rowdata, int[] rowColMap);

    /**
     * Finds the first node where the data is not null.
     *
     * @return iterator
     * @param session Session
     * @param store PersistentStore
     */
    RowIterator findFirstRowNotNull(Session session, PersistentStore store);

    RowIterator firstRow(PersistentStore store);

    /**
     * Returns the row for the first node of the index
     *
     * @return Iterator for first row
     */
    RowIterator firstRow(Session session, PersistentStore store,
                         RangeVariableConditions[] conditions,
                         int distinctCount, boolean[] map);

    /**
     * Returns the row for the last node of the index
     *
     * @return last row
     * @param session Session
     * @param store PersistentStore
     * @param distinctCount int
     * @param map boolean[]
     */
    RowIterator lastRow(Session session, PersistentStore store,
                        int distinctCount, boolean[] map);

    /**
     * Compares two table rows based on the columns of this index. The rowColMap
     * parameter specifies which columns of the other table are to be compared
     * with the colIndex columns of this index. The rowColMap can cover all or
     * only some columns of this index.
     *
     * @param session Session
     * @param a row from another table
     * @param b a full row in this table
     * @param rowColMap column indexes in the other table
     * @return comparison result, -1,0,+1
     */
    int compareRowNonUnique(Session session, Object[] a, Object[] b,
                            int[] rowColMap);

    int compareRowNonUnique(Session session, Object[] a, Object[] b,
                            int[] rowColMap, int fieldCount);

    /**
     * As above but use the index column data
     */
    int compareRowNonUnique(Session session, Object[] a, Object[] b,
                            int fieldcount);

    int compareRow(Session session, Object[] a, Object[] b);

    class IndexUse {

        public Index index;
        public int   columnCount;

        public IndexUse(Index index, int columnCount) {
            this.index       = index;
            this.columnCount = columnCount;
        }
    }
}
