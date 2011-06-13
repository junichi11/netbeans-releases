/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.profiler.spi.project;

import org.netbeans.lib.profiler.common.ProfilingSettings;
import org.netbeans.lib.profiler.common.filters.FilterUtils;
import org.netbeans.modules.profiler.api.project.ProfilingSettingsSupport.SettingsCustomizer;

/**
 *
 * @author Jiri Sedlacek
 */
public abstract class ProfilingSettingsSupportProvider {    
    
    public abstract float getProfilingOverhead(ProfilingSettings settings);
    
    public abstract SettingsCustomizer getSettingsCustomizer();
    
    //    public abstract SettingsConfigurator getSettingsConfigurator();
    
    
    public static class Basic extends ProfilingSettingsSupportProvider {

        @Override
        public float getProfilingOverhead(ProfilingSettings settings) {
            return -1;
        }

        @Override
        public SettingsCustomizer getSettingsCustomizer() {
            return null;
        }
        
//        @Override
//        public SettingsConfigurator getSettingsConfigurator() {
//            return null;;
//        }
        
    }
    
    public static class Default extends Basic {

        @Override
        public float getProfilingOverhead(ProfilingSettings settings) {
            float o = 0.0f;

            if (ProfilingSettings.isMonitorSettings(settings)) {
                //} else if (ProfilingSettings.isAnalyzerSettings(settings)) {
            } else if (ProfilingSettings.isCPUSettings(settings)) {
                if (settings.getProfilingType() == ProfilingSettings.PROFILE_CPU_ENTIRE) {
                    o += 0.5f; // entire app
                } else if (settings.getProfilingType() == ProfilingSettings.PROFILE_CPU_PART) {
                    o += 0.2f; // part of app
                }

                if (FilterUtils.NONE_FILTER.equals(settings.getSelectedInstrumentationFilter())) {
                    o += 0.5f; // profile all classes
                }
            } else if (ProfilingSettings.isMemorySettings(settings)) {
                if (settings.getProfilingType() == ProfilingSettings.PROFILE_MEMORY_ALLOCATIONS) {
                    o += 0.5f; // object allocations
                } else if (settings.getProfilingType() == ProfilingSettings.PROFILE_MEMORY_LIVENESS) {
                    o += 0.7f; // object liveness
                }

                if (settings.getAllocStackTraceLimit() != 0) {
                    o += 0.3f; // record allocation stack traces
                }
            }

            return o;
        }
        
    }
    
}
