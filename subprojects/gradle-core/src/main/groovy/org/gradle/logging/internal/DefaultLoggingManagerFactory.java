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

package org.gradle.logging.internal;

import org.gradle.api.logging.LoggingOutput;
import org.gradle.logging.LoggingManagerFactory;
import org.gradle.logging.LoggingManagerInternal;
import org.gradle.logging.StyledTextOutputFactory;

public class DefaultLoggingManagerFactory implements LoggingManagerFactory {
    private final LoggingSystem slfLoggingSystem;
    private final LoggingSystem stdOutLoggingSystem;
    private final LoggingSystem stdErrLoggingSystem;
    private final LoggingOutput loggingOutput;

    public DefaultLoggingManagerFactory(LoggingConfigurer loggingConfigurer, LoggingOutput loggingOutput, StyledTextOutputFactory textOutputFactory) {
        this.loggingOutput = loggingOutput;
        slfLoggingSystem = new LoggingSystemAdapter(loggingConfigurer);
        stdOutLoggingSystem = new StdOutLoggingSystem(textOutputFactory);
        stdErrLoggingSystem = new StdErrLoggingSystem(textOutputFactory);
    }

    public LoggingManagerInternal create() {
        return new DefaultLoggingManager(slfLoggingSystem, stdOutLoggingSystem, stdErrLoggingSystem, loggingOutput);
    }
}