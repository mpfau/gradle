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
package org.gradle.api.internal.project.taskfactory;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Task;
import org.gradle.api.tasks.OutputDirectory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

public class OutputDirectoryPropertyAnnotationHandler implements PropertyAnnotationHandler {
    private final ValidationAction outputDirValidation = new ValidationAction() {
        public void validate(String propertyName, Object value) throws InvalidUserDataException {
            File fileValue = (File) value;
            if (!fileValue.isDirectory() && !fileValue.mkdirs()) {
                throw new InvalidUserDataException(String.format(
                        "Cannot create directory '%s' specified for property '%s'.", fileValue, propertyName));
            }
        }
    };

    public Class<? extends Annotation> getAnnotationType() {
        return OutputDirectory.class;
    }

    public void attachActions(PropertyActionContext context) {
        context.setValidationAction(outputDirValidation);
        context.setConfigureAction(new UpdateAction() {
            public void update(Task task, Callable<Object> futureValue) {
                task.getOutputs().files(futureValue);
            }
        });
    }
}
