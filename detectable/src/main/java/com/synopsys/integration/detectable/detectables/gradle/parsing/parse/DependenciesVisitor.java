/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.gradle.parsing.parse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class DependenciesVisitor extends CodeVisitorSupport {
    private final Logger logger = LoggerFactory.getLogger(DependenciesVisitor.class);

    private final ExternalIdFactory externalIdFactory;
    private final List<Dependency> dependencies = new ArrayList<>();

    private boolean inDependenciesBlock;

    public DependenciesVisitor(ExternalIdFactory externalIdFactory) {
        super();
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        inDependenciesBlock = "dependencies".equals(methodCallExpression.getMethodAsString());
        super.visitMethodCallExpression(methodCallExpression);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {
        if (inDependenciesBlock) {
            List<Expression> expressions = argumentListExpression.getExpressions();

            if (expressions.size() == 1 && expressions.get(0) instanceof ClosureExpression) {
                ClosureExpression closureExpression = (ClosureExpression) expressions.get(0);
                if (closureExpression.getCode() instanceof BlockStatement) {
                    BlockStatement blockStatement = (BlockStatement) closureExpression.getCode();
                    blockStatement.getStatements().forEach(this::addDependencyFromStatement);
                }
            }
        }

        super.visitArgumentlistExpression(argumentListExpression);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    private void addDependencyFromStatement(Statement statement) {
        Expression expression = null;
        try {
            expression = determineExpression(statement);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(String.format("ExpressionStatement/ReturnStatement no longer have a 'getExpression' method: %s", e.getMessage()), e);
        }

        if (expression instanceof MethodCallExpression) {
            MethodCallExpression methodCallExpression = (MethodCallExpression) expression;
            Expression argumentsExpression = methodCallExpression.getArguments();
            if (argumentsExpression instanceof ArgumentListExpression) {
                processArgumentListExpression((ArgumentListExpression) argumentsExpression);
            } else if (argumentsExpression instanceof TupleExpression) {
                processTupleExpression((TupleExpression) argumentsExpression);
            }
        }
    }

    private void processArgumentListExpression(ArgumentListExpression methodArgumentListExpression) {
        List<Expression> methodExpressions = methodArgumentListExpression.getExpressions();
        if (methodExpressions.size() == 1 && methodExpressions.get(0) instanceof ConstantExpression) {
            ConstantExpression methodConstantExpression = (ConstantExpression) methodExpressions.get(0);
            addDependencyFromConstantExpression(methodConstantExpression);
        }
    }

    private void processTupleExpression(TupleExpression tupleExpression) {
        if (tupleExpression.getExpressions().size() == 1 && tupleExpression.getExpression(0) instanceof MapExpression) {
            addDependencyFromMapExpression((MapExpression) tupleExpression.getExpression(0));
        }
    }

    private Expression determineExpression(Statement statement) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getExpression;
        if (statement instanceof ExpressionStatement) {
            getExpression = ExpressionStatement.class.getMethod("getExpression");
        } else if (statement instanceof ReturnStatement) {
            getExpression = ReturnStatement.class.getMethod("getExpression");
        } else {
            throw new NoSuchMethodException("Failed to find an expression.");
        }

        return (Expression) getExpression.invoke(statement);
    }

    private void addDependencyFromMapExpression(MapExpression mapExpression) {
        List<MapEntryExpression> mapEntryExpressions = mapExpression.getMapEntryExpressions();

        String group = null;
        String name = null;
        String version = null;
        for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
            String key = mapEntryExpression.getKeyExpression().getText();
            String value = mapEntryExpression.getValueExpression().getText();
            if ("group".equals(key)) {
                group = value;
            } else if ("name".equals(key)) {
                name = value;
            } else if ("version".equals(key)) {
                version = value;
            }
        }

        addDependency(group, name, version);
    }

    private void addDependencyFromConstantExpression(ConstantExpression constantExpression) {
        String dependencyString = constantExpression.getText();
        String[] pieces = dependencyString.split(":");

        if (pieces.length == 3) {
            String group = pieces[0];
            String name = pieces[1];
            String version = pieces[2];
            addDependency(group, name, version);
        }
    }

    private void addDependency(String group, String name, String version) {
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        Dependency dependency = new Dependency(name, version, externalId);
        dependencies.add(dependency);
    }

}
