/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JJTree: Do not edit this line. AstListData.java Version 4.3 */
package org.zkoss.zel.impl.parser;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zel.ELException;
import org.zkoss.zel.impl.lang.EvaluationContext;

public class AstListData extends SimpleNode {
    public AstListData(int id) {
        super(id);
    }

    
    public Object getValue(EvaluationContext ctx) throws ELException {
        List<Object> result = new ArrayList<>();

        if (children != null) {
            for (Node child : children) {
                result.add(child.getValue(ctx));
            }
        }

        return result;
    }

    
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return List.class;
    }
}
/* JavaCC - OriginalChecksum=7f2694086a9ba64558ee39d1cd719db1 (do not edit this line) */