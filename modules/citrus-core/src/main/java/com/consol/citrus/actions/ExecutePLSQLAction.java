/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.SqlUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Class executes PLSQL statements either declared inline as PLSQL statements or given by an
 * external file resource.
 *
 * @author Christoph Deppisch
 * @since 2008
 */
public class ExecutePLSQLAction extends AbstractDatabaseConnectingTestAction {
    /** In line script */
    private String script = null;

    /** boolean flag marking that possible SQL errors will be ignored */
    private boolean ignoreErrors = false;

    /** Special statement endoing character sequence */
    public static final String PLSQL_STMT_ENDING = "/";

    /**
     * Default constructor.
     */
    public ExecutePLSQLAction() {
        setName("plsql");
    }

    @Override
    public void doExecute(TestContext context) {
        if (StringUtils.hasText(script)) {
            statements = createStatementsFromScript(context);
        } else if (StringUtils.hasText(sqlResourcePath)) {
            statements = createStatementsFromFileResource(context, new SqlUtils.LastScriptLineDecorator() {
                @Override
                public String getStatementEndingCharacter() {
                    return PLSQL_STMT_ENDING;
                }

                @Override
                public String decorate(String line) {
                    return line.trim().substring(0, (line.trim().length() - 1));
                }
            });
        }

        if (getTransactionManager() != null) {
            if (log.isDebugEnabled()) {
                log.debug("Using transaction manager: " + getTransactionManager().getClass().getName());
            }

            TransactionTemplate transactionTemplate = new TransactionTemplate(getTransactionManager());
            transactionTemplate.setTimeout(Integer.valueOf(context.replaceDynamicContentInString(getTransactionTimeout())));
            transactionTemplate.setIsolationLevelName(context.replaceDynamicContentInString(getTransactionIsolationLevel()));
            transactionTemplate.execute(status -> {
                executeStatements(context);
                return null;
            });
        } else {
            executeStatements(context);
        }
    }

    /**
     * Run all PLSQL statements.
     * @param context
     */
    protected void executeStatements(TestContext context) {
        for (String stmt : statements) {
            try {
                final String toExecute = context.replaceDynamicContentInString(stmt.trim());

                if (log.isDebugEnabled()) {
                    log.debug("Executing PLSQL statement: " + toExecute);
                }

                getJdbcTemplate().execute(toExecute);

                log.info("PLSQL statement execution successful");
            } catch (DataAccessException e) {
                if (ignoreErrors) {
                    log.warn("Ignoring error while executing PLSQL statement: " + e.getMessage());
                    continue;
                } else {
                    throw new CitrusRuntimeException("Failed to execute PLSQL statement", e);
                }
            }
        }
    }

    /**
     * Create SQL statements from inline script.
     * @param context the current test context.
     * @return list of SQL statements.
     */
    private List<String> createStatementsFromScript(TestContext context) {
        List<String> stmts = new ArrayList<>();
        
        script = context.replaceDynamicContentInString(script);
        if (log.isDebugEnabled()) {
            log.debug("Found inline PLSQL script " + script);
        }

        StringTokenizer tok = new StringTokenizer(script, PLSQL_STMT_ENDING);
        while (tok.hasMoreTokens()) {
            String next = tok.nextToken().trim();
            if (StringUtils.hasText(next)) {
                stmts.add(next);
            }
        }
        
        return stmts;
    }

    /**
     * Setter for inline script.
     * @param script
     */
    public ExecutePLSQLAction setScript(String script) {
        this.script = script;
        return this;
    }

    /**
     * Ignore errors during execution.
     * @param ignoreErrors boolean flag to set
     */
    public ExecutePLSQLAction setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
        return this;
    }

    /**
     * Gets the script.
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * Gets the ignoreErrors.
     * @return the ignoreErrors
     */
    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }
}
