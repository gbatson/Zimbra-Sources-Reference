/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.DelegatingConnection;

/**
 * PreparedStatement wrapper that allows for retry on exception.
 * Current implementation retries when exception message matches 'SQLITE_BUSY'
 * Retry occurs a maximum of 5 times with a delay of 250ms between each attempt
 * If retry limit is reached the last instance of the exception is wrapped and thrown.
 *
 */
public class RetryPreparedStatement extends DebugPreparedStatement {
    
    RetryPreparedStatement(DelegatingConnection conn,
            PreparedStatement stmt, String sql) {
        super(conn, stmt, sql);
    }
    
    @Override
    public boolean execute() throws SQLException {
        AbstractRetry<Boolean> exec = new AbstractRetry<Boolean> () {
            @Override
            public ExecuteResult<Boolean> execute() throws SQLException {
                return new ExecuteResult<Boolean>(superExecute());
            }
        };
        return exec.doRetry().getResult();
    }
    
    private boolean superExecute() throws SQLException {
        return super.execute();
    }

    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys)
            throws SQLException {
        AbstractRetry<Boolean> exec = new AbstractRetry<Boolean> () {
            @Override
            public ExecuteResult<Boolean> execute() throws SQLException {
                return new ExecuteResult<Boolean>(superExecute(sql, autoGeneratedKeys));
            }
        };
        return exec.doRetry().getResult();
    }
    
    private boolean superExecute(String sql, int autoGeneratedKeys) throws SQLException {
        return super.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        AbstractRetry<Boolean> exec = new AbstractRetry<Boolean> () {
            @Override
            public ExecuteResult<Boolean> execute() throws SQLException {
                return new ExecuteResult<Boolean>(superExecute(sql, columnIndexes));
            }
        };
        return exec.doRetry().getResult();
    }

    private boolean superExecute(String sql, int[] columnIndexes) throws SQLException {
        return super.execute(sql, columnIndexes);
    }
    
    @Override
    public boolean execute(final String sql, final String[] columnNames)
            throws SQLException {
        AbstractRetry<Boolean> exec = new AbstractRetry<Boolean> () {
            @Override
            public ExecuteResult<Boolean> execute() throws SQLException {
                return new ExecuteResult<Boolean>(superExecute(sql, columnNames));
            }
        };
        return exec.doRetry().getResult();
    }

    private boolean superExecute(String sql, String[] columnNames) throws SQLException {
        return super.execute(sql, columnNames);
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        AbstractRetry<Boolean> exec = new AbstractRetry<Boolean> () {
            @Override
            public ExecuteResult<Boolean> execute() throws SQLException {
                return new ExecuteResult<Boolean>(superExecute(sql));
            }
        };
        return exec.doRetry().getResult();
    }

    private boolean superExecute(String sql) throws SQLException {
        return super.execute(sql);
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        AbstractRetry<int[]> exec = new AbstractRetry<int[]> () {
            @Override
            public ExecuteResult<int[]> execute() throws SQLException {
                return new ExecuteResult<int[]>(superExecuteBatch());
            }
        };
        return exec.doRetry().getResult();
    }
        
    private int[] superExecuteBatch() throws SQLException {
        return super.executeBatch();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        AbstractRetry<ResultSet> exec = new AbstractRetry<ResultSet> () {
            @Override
            public ExecuteResult<ResultSet> execute() throws SQLException {
                return new ExecuteResult<ResultSet>(superExecuteQuery());
            }
        };
        return exec.doRetry().getResult();
    }

    private ResultSet superExecuteQuery() throws SQLException {
        return super.executeQuery();
    }

    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        AbstractRetry<ResultSet> exec = new AbstractRetry<ResultSet> () {
            @Override
            public ExecuteResult<ResultSet> execute() throws SQLException {
                return new ExecuteResult<ResultSet>(superExecuteQuery(sql));
            }
        };
        return exec.doRetry().getResult();
    }

    private ResultSet superExecuteQuery(String sql) throws SQLException {
        return super.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        AbstractRetry<Integer> exec = new AbstractRetry<Integer> () {
            @Override
            public ExecuteResult<Integer> execute() throws SQLException {
                return new ExecuteResult<Integer>(superExecuteUpdate());
            }
        };
        return exec.doRetry().getResult();
    }

    private int superExecuteUpdate() throws SQLException {
        return super.executeUpdate();
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys)
            throws SQLException {
        AbstractRetry<Integer> exec = new AbstractRetry<Integer> () {
            @Override
            public ExecuteResult<Integer> execute() throws SQLException {
                return new ExecuteResult<Integer>(superExecuteUpdate(sql, autoGeneratedKeys));
            }
        };
        return exec.doRetry().getResult();
    }

    private int superExecuteUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return super.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes)
            throws SQLException {
        AbstractRetry<Integer> exec = new AbstractRetry<Integer> () {
            @Override
            public ExecuteResult<Integer> execute() throws SQLException {
                return new ExecuteResult<Integer>(superExecuteUpdate(sql, columnIndexes));
            }
        };
        return exec.doRetry().getResult();
    }

    private int superExecuteUpdate(String sql, int[] columnIndexes) throws SQLException {
        return super.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(final String sql, final String[] columnNames)
            throws SQLException {
        AbstractRetry<Integer> exec = new AbstractRetry<Integer> () {
            @Override
            public ExecuteResult<Integer> execute() throws SQLException {
                return new ExecuteResult<Integer>(superExecuteUpdate(sql, columnNames));
            }
        };
        return exec.doRetry().getResult();
    }

    private int superExecuteUpdate(String sql, String[] columnNames) throws SQLException {
        return super.executeUpdate(sql, columnNames);
    }

    @Override
    public int executeUpdate(final String sql) throws SQLException {
        AbstractRetry<Integer> exec = new AbstractRetry<Integer> () {
            @Override
            public ExecuteResult<Integer> execute() throws SQLException {
                return new ExecuteResult<Integer>(superExecuteUpdate(sql));
            }
        };
        return exec.doRetry().getResult();
    }

    private int superExecuteUpdate(String sql) throws SQLException {
        return super.executeUpdate(sql);
    }

}

