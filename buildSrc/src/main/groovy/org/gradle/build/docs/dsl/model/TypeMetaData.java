/*
 * Copyright 2010 the original author or authors.
 *
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
 */
package org.gradle.build.docs.dsl.model;

import org.gradle.api.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TypeMetaData implements Serializable, TypeContainer {
    public static final TypeMetaData VOID = new TypeMetaData("void");

    private String name;
    private int arrayDimensions;
    private boolean varargs;
    private List<TypeMetaData> typeArgs;
    private boolean wildcard;
    private TypeMetaData upperBounds;
    private TypeMetaData lowerBounds;

    public TypeMetaData(String name) {
        this.name = name;
    }

    public TypeMetaData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArrayDimensions() {
        return arrayDimensions + (varargs ? 1 : 0);
    }

    public void addArrayDimension() {
        arrayDimensions++;
    }

    public boolean isVarargs() {
        return varargs;
    }

    public void setVarargs() {
        this.varargs = true;
    }

    public String getSignature() {
        final StringBuilder builder = new StringBuilder();

        visitSignature(new SignatureVisitor() {
            public void visitText(String text) {
                builder.append(text);
            }

            public void visitType(String name) {
                builder.append(name);
            }
        });
        return builder.toString();
    }

    public String getArraySuffix() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arrayDimensions; i++) {
            builder.append("[]");
        }
        if (varargs) {
            builder.append("...");
        }
        return builder.toString();
    }

    public void addTypeArg(TypeMetaData typeArg) {
        if (typeArgs == null) {
            typeArgs = new ArrayList<TypeMetaData>();
        }
        typeArgs.add(typeArg);
    }

    public void visitTypes(Action<TypeMetaData> action) {
        if (wildcard) {
            return;
        }
        if (upperBounds != null) {
            upperBounds.visitTypes(action);
            return;
        }
        if (lowerBounds != null) {
            lowerBounds.visitTypes(action);
            return;
        }
        
        action.execute(this);
        if (typeArgs != null) {
            for (TypeMetaData typeArg : typeArgs) {
                typeArg.visitTypes(action);
            }
        }
    }

    public void visitSignature(SignatureVisitor visitor) {
        if (wildcard) {
            visitor.visitText("?");
        } else if (upperBounds != null) {
            visitor.visitText("? extends ");
            upperBounds.visitSignature(visitor);
        } else if (lowerBounds != null) {
            visitor.visitText("? super ");
            lowerBounds.visitSignature(visitor);
        } else {
            visitor.visitType(name);
            if (typeArgs != null) {
                visitor.visitText("<");
                for (int i = 0; i < typeArgs.size(); i++) {
                    if (i > 0) {
                        visitor.visitText(", ");
                    }
                    TypeMetaData typeArg = typeArgs.get(i);
                    typeArg.visitSignature(visitor);
                }
                visitor.visitText(">");
            }
            String suffix = getArraySuffix();
            if (suffix.length() > 0) {
                visitor.visitText(suffix);
            }
        }
    }

    public void setWildcard() {
        wildcard = true;
    }

    public void setUpperBounds(TypeMetaData upperBounds) {
        this.upperBounds = upperBounds;
    }

    public void setLowerBounds(TypeMetaData lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    public interface SignatureVisitor {
        void visitText(String text);

        void visitType(String name);
    }
}