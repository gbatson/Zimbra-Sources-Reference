/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on Apr 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.zimbra.cs.db;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.PoolingDataSource;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * @author schemers
 */
public abstract class Db {

    public static enum Error {
        DEADLOCK_DETECTED,
        DUPLICATE_ROW,
        FOREIGN_KEY_CHILD_EXISTS,
        FOREIGN_KEY_NO_PARENT,
        NO_SUCH_DATABASE,
        NO_SUCH_TABLE,
        TOO_MANY_SQL_PARAMS;
    }

    public static enum Capability {
        BITWISE_OPERATIONS,
        BOOLEAN_DATATYPE,
        CASE_SENSITIVE_COMPARISON,
        CAST_AS_BIGINT,
        CLOB_COMPARISON,
        DISABLE_CONSTRAINT_CHECK,
        FILE_PER_DATABASE,
        LIMIT_CLAUSE,
        MULTITABLE_UPDATE,
        NON_BMP_CHARACTERS,
        ON_DUPLICATE_KEY,
        ON_UPDATE_CASCADE,
        READ_COMMITTED_ISOLATION,
        REPLACE_INTO,
        ROW_LEVEL_LOCKING,
        UNIQUE_NAME_INDEX,
        AVOID_OR_IN_WHERE_CLAUSE, // if set, then try to avoid ORs in WHERE clauses, run them as separate queries and mergesort in memory
        REQUEST_UTF8_UNICODE_COLLATION, // for mysql
        FORCE_INDEX_EVEN_IF_NO_SORT, // for derby
        SQL_PARAM_LIMIT
        ;
    }


    private static Db sDatabase;

    public synchronized static Db getInstance() {
        if (sDatabase == null) {
            String className = LC.zimbra_class_database.value();
            if (className != null && !className.equals("")) {
                try {
                    sDatabase = (Db) Class.forName(className).newInstance();
                } catch (Exception e) {
                    ZimbraLog.system.error("could not instantiate database configuration '" + className + "'; defaulting to MySQL", e);
                }
            }
            if (sDatabase == null)
                sDatabase = new MySQL();
        }
        return sDatabase;
    }


    /** Returns whether the currently-configured database supports the given
     *  {@link Db.Capability}. */
    public static boolean supports(Db.Capability capability) {
        return getInstance().supportsCapability(capability);
    }

    abstract boolean supportsCapability(Db.Capability capability);


    /** Returns whether the given {@link SQLException} is an instance of the
     *  specified {@link Db.Error}. */
    public static boolean errorMatches(SQLException e, Db.Error error) {
        return getInstance().compareError(e, error);
    }

    abstract boolean compareError(SQLException e, Db.Error error);


    /** Returns the set of configuration settings necessary to initialize the
     *  appropriate database connection pool.
     * @see DbPool#getPool() */
    abstract DbPool.PoolConfig getPoolConfig();

    /** Callback invoked immediately after the initialization of the
     *  connection pool.  Permits the DB implementation to iterate over
     *  the connections or to operate on the pool itself before any
     *  connections are returned to callers. */
    void startup(PoolingDataSource pool, int poolSize) throws SQLException {
        // default is to do nothing
    }

    /** Completely shut down the database.  Warning: You may not use the DB
     *  at all after calling this method!  <i>Currently only applicable to
     *  derby.</i> */
    void shutdown() {
        // default is to do nothing
    }

    /** Callback invoked immediately after a new connection is created for
     *  the pool. */
    void postCreate(java.sql.Connection conn) throws SQLException {
        // default is to do nothing
    }

    /** Callback invoked immediately before a connection is fetched from
     *  the pool and returned to the user. */
    void postOpen(Connection conn) throws SQLException {
        // default is to do nothing
    }

    /** optimize and optionally compact a database
     * level 0: analysis tuning only
     * level 1: quick file optimization and analysis
     * level 2: full file optimization and analysis
     */
    public void optimize(Connection conn, String name, int level) throws ServiceException {}

    /** Indicates that the connection will be accessing the given Mailbox's
     *  database in the scope of the current transaction.  Must be called
     *  <em>before</em> any SQL commands are executed in the transaction. */
    public static void registerDatabaseInterest(Connection conn, Mailbox mbox) throws ServiceException {
        try {
            getInstance().registerDatabaseInterest(conn, DbMailbox.getDatabaseName(mbox));
        } catch (SQLException e) {
            throw ServiceException.FAILURE("error registering interest in database " + DbMailbox.getDatabaseName(mbox), e);
        }
    }

    void registerDatabaseInterest(Connection conn, String dbname) throws SQLException, ServiceException {
        // default is to do nothing
    }

    /** Callback invoked immediately before a connection is returned to the
     *  pool by the user.  Note that <tt>COMMIT</tt>/<tt>ROLLBACK</tt> must
     *  already have been called before this method is invoked. */
    void preClose(Connection conn) throws SQLException {
        // default is to do nothing
    }


    /** Returns <tt>true</tt> if the database with the given name exists. */
    abstract public boolean databaseExists(Connection conn, String dbname)
    throws ServiceException;

    /** Callback executed immediately before creating a user database. */
    void precreateDatabase(String dbname)  { }

    void deleteDatabaseFile(String dbname) {
        // not supported by default
        throw new UnsupportedOperationException("DB is not file-per-database");
    }


    /** Generates the correct SQL to direct the current database engine to use
     *  a particular index to perform a SELECT query.  This string should come
     *  after the FROM clause and before the WHERE clause in the final SQL
     *  query.  If the database does not support this type of hinting, the
     *  function will return <tt>""</tt>. */
    public static String forceIndex(String index) {
        if (index == null || index.trim().equals(""))
            return "";
        return getInstance().forceIndexClause(index);
    }

    abstract String forceIndexClause(String index);

    /** Returns the string used to delimit commands in multi-line scripts.
     *  This is usually '<tt>;</tt>' (in keeping with SQL conventions), but
     *  it may be an alternate character in order to permit '<tt>;</tt>'
     *  within a script. */
    public String scriptCommandDelimiter() {
        return ";";
    }

    private static final int DEFAULT_IN_CLAUSE_BATCH_SIZE = 400;
    
    protected int getInClauseBatchSize() { return DEFAULT_IN_CLAUSE_BATCH_SIZE; }

    /** Returns the maximum number of items to include in an "IN (?, ?, ...)"
     *  clause.  For databases with a broken or hugely nonperformant IN clause,
     *  e.g. Derby pre-10.3 (see DERBY-47 JIRA), this may be 1 */
    public static int getINClauseBatchSize() {
        return getInstance().getInClauseBatchSize();
    }

    /** Generates a SELECT expression representing a BOOLEAN.  For databases
     *  that don't support a BOOLEAN datatype, returns an appropriate CASE
     *  clause that evaluates to 1 when the given BOOLEAN clause is true and
     *  0 when it's false. */
    static String selectBOOLEAN(String clause) {
        if (supports(Capability.BOOLEAN_DATATYPE))
            return clause;
        else
            return "CASE WHEN " + clause + " THEN 1 ELSE 0 END";
    }

    /** Generates a WHERE-type clause that evaluates to true when the given
     *  column equals a string later specified by <tt>stmt.setString()</tt>
     *  under a case-insensitive comparison.  Note that the caller *MUST NOT*
     *  pass an upcased version of the comparison string in the subsequent
     *  call to <tt>stmt.setString()</tt>. */
    static String equalsSTRING(String column) {
        if (supports(Capability.CASE_SENSITIVE_COMPARISON))
            return "UPPER(" + column + ") = UPPER(?)";
        else
            return column + " = ?";
    }

    /** Generates a WHERE-type clause that evaluates to true when the given
     *  column is a case-insensitive match to a SQL pattern string later
     *  specified by <tt>stmt.setString()</tt> under a  comparison.  Note that
     *  the caller *MUST NOT* pass an upcased version of the comparison string in
     *  the subsequent call to <tt>stmt.setString()</tt>. */
    static String likeSTRING(String column) {
        if (supports(Capability.CASE_SENSITIVE_COMPARISON))
            return "UPPER(" + column + ") LIKE UPPER(?)";
        else
            return column + " LIKE ?";
    }


    /** Generates a WHERE-type clause that evaluates to true when the given
     *  column matches a bitmask later specified by <tt>stmt.setLong()</tt>.
     *  Note that this is only valid when the bitmask has only 1 bit set. */
    static String bitmaskAND(String column) {
        if (supports(Capability.BITWISE_OPERATIONS))
            return column + " & ?";
        else
            return "MOD(" + column + " / ?, 2) = 1";
    }

    /** Generates a WHERE-type clause that evaluates to true when the given
     *  column matches the given bitmask.  Note that this is only valid when
     *  the bitmask has only 1 bit set. */
    static String bitmaskAND(String column, long bitmask) {
        if (supports(Capability.BITWISE_OPERATIONS))
            return column + " & " + bitmask;
        else
            return "MOD(" + column + " / " + bitmask + ", 2) = 1";
    }
    
    public void enableStreaming(Statement stmt) throws SQLException {}

    /** Generates a WHERE-type clause that evaluates to <code>expr1</code> if
     *  its value is non-<tt>NULL</tt> and <code>expr2</code> otherwise. */
    public static String clauseIFNULL(String expr1, String expr2) {
        return getInstance().getIFNULLClause(expr1, expr2);
    }

    abstract String getIFNULLClause(String expr1, String expr2);

    /** Force the database engine to flush committed changes to physical disk. */
    public abstract void flushToDisk();
}
