/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parser.sql.dialect.sqlserver.parser;

import com.dangdang.ddframe.rdb.sharding.parser.sql.dialect.sqlserver.lexer.SQLServerKeyword;
import com.dangdang.ddframe.rdb.sharding.parser.sql.lexer.DefaultKeyword;
import com.dangdang.ddframe.rdb.sharding.parser.sql.lexer.Symbol;
import com.dangdang.ddframe.rdb.sharding.parser.sql.parser.AbstractSelectParser;
import com.dangdang.ddframe.rdb.sharding.parser.sql.parser.ParserUnsupportedException;
import com.dangdang.ddframe.rdb.sharding.parser.sql.parser.SQLExprParser;

public class SQLServerSelectParser extends AbstractSelectParser {
    
    public SQLServerSelectParser(final SQLExprParser exprParser) {
        super(exprParser);
    }
    
    @Override
    protected void customizedSelect() {
        if (getExprParser().equal(DefaultKeyword.FOR)) {
            parseFor();
        }
        if (getExprParser().equal(SQLServerKeyword.OFFSET)) {
            ((SQLServerExprParser) getExprParser()).parseOffset(getSqlContext());
        }
    }
    
    @Override
    public void query() {
        if (getExprParser().skipIfEqual(DefaultKeyword.SELECT)) {
            parseDistinct();
            if (getExprParser().equal(SQLServerKeyword.TOP)) {
                // TODO save topContext
                ((SQLServerExprParser) getExprParser()).parseTop();
            }
            parseSelectList();
        }
        if (getExprParser().equal(DefaultKeyword.INTO)) {
            throw new ParserUnsupportedException(getExprParser().getLexer().getToken().getType());
        }
        parseFrom();
        parseWhere();
        parseGroupBy();
        queryRest();
    }
    
    @Override
    protected void parseJoinTable() {
        if (getExprParser().skipIfEqual(DefaultKeyword.WITH)) {
            getExprParser().skipParentheses();
        }
        super.parseJoinTable();
    }
    
    private void parseFor() {
        getExprParser().getLexer().nextToken();
        if (getExprParser().equal(SQLServerKeyword.BROWSE)) {
            getExprParser().getLexer().nextToken();
        } else if (getExprParser().skipIfEqual(SQLServerKeyword.XML)) {
            while (true) {
                if (getExprParser().equal(SQLServerKeyword.AUTO, SQLServerKeyword.TYPE, SQLServerKeyword.XMLSCHEMA)) {
                    getExprParser().getLexer().nextToken();
                } else if (getExprParser().skipIfEqual(SQLServerKeyword.ELEMENTS)) {
                    getExprParser().skipIfEqual(SQLServerKeyword.XSINIL);
                } else {
                    break;
                }
                if (getExprParser().equal(Symbol.COMMA)) {
                    getExprParser().getLexer().nextToken();
                } else {
                    break;
                }
            }
        } else {
            throw new ParserUnsupportedException(getExprParser().getLexer().getToken().getType());
        }
    }
}