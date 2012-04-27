/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.sampler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Sampler class provides API for self-sampling of NetBeans
 * platform application. The self-sampling should be used for
 * diagnostic purposes and should help NetBeans platform developers
 * with solving CPU related performance problems. Sampled data are
 * stored in NPSS file, which can be opened by NetBeans Profiler or
 * Java VisualVM for later analysis of sampled data.
 * 
 * @author Jaroslav Bachorik, Tomas Hurka, Jaroslav Tulach
 */
public abstract class Sampler {
    private static final int SAMPLER_RATE = 10;
    private static final double MAX_AVERAGE = SAMPLER_RATE * 3;
    private static final double MAX_STDDEVIATION = SAMPLER_RATE * 4;
    private static final int MAX_SAMPLING_TIME = 5*60;  // 5 minutes
    private static final int MIN_SAMPLES = 50;
    private static final int MAX_SAMPLES = MAX_SAMPLING_TIME * (1000/SAMPLER_RATE);
    
    private final String name;
    private Timer timer;
    private ByteArrayOutputStream out;
    private SamplesOutputStream samplesStream;
    private long startTime;
    private long nanoTimeCorrection;
    private long samples;
    private long laststamp;
    private double max;
    private double min = Long.MAX_VALUE;
    private double sum;
    private double devSquaresSum;
    private volatile boolean stopped;
    private volatile boolean running;

    /**
     * Factory method for creating Sampler suitable for automatic reporting
     * of the slow operation (i.e. AWT blocked for some time). This method can
     * return <code>null</code> if the sampled application is in nonstandard mode
     * which will produce unrealistic data - for example application is  
     * running under debugger or profiler. It can return <code>null</code> if it
     * is running on some exotic variant of JDK, where sampling is not supported.
     * @param name which identifies the sampler thread
     * @return instance of the {@link Sampler} or <code>null</code> if application
     * is in nonstandard mode or sampling is not supported.
     */
    public static @CheckForNull Sampler createSampler(@NonNull String name) {
        return InternalSampler.createInternalSampler(name);
    }
    
    /**
     * Factory method for creating Sampler suitable for manual or user invoked
     * scanning. This method can return <code>null</code> if it is running on 
     * some exotic variant of JDK, where sampling is not supported.
     * @param name which identifies the sampler thread 
     * @return instance of the {@link Sampler} or <code>null</code> if sampling 
     * is not supported.
     */
    public static @CheckForNull Sampler createManualSampler(@NonNull String name) {
        if (SamplesOutputStream.isSupported()) {
            return new InternalSampler(name);
        }
        return null;
    }
    
    Sampler(String n) {
        name = n;
    }
    
    /** Returns the bean to use for sampling.
     * @return instance of the bean to take thread dumps from
     */
    abstract ThreadMXBean getThreadMXBean();

    /** Allows subclasses to handle created snapshot
     * @param arr the content of the snapshot
     * @throws IOException thrown in case of I/O error
     */
    abstract void saveSnapshot(byte[] arr) throws IOException;
    
    /** How to report an exception.
     * 
     * @param ex exception
     */
    abstract void printStackTrace(Throwable ex);
    
    /** Methods for displaying progress.
     */
    abstract void openProgress(int steps);
    abstract void closeProgress();
    abstract void progress(int i);
    
    private void updateStats(long timestamp) {
        if (laststamp != 0) {
            double diff = (timestamp - laststamp) / 1000000.0;
            samples++;
            sum += diff;
            devSquaresSum += (diff - SAMPLER_RATE) * (diff - SAMPLER_RATE);
            if (diff > max) {
                max = diff;
            } else if (diff < min) {
                min = diff;
            }
        }
        laststamp = timestamp;
    }

    /**
     * Start self-sampling. This method starts timer identified by <code>name</code>
     * for actual sampling and returns immediately.
     */
    public final synchronized void start() {
        if (running) throw new IllegalStateException("sampling is already running");    // NOI18N
        if (stopped) throw new IllegalStateException("it is not possible to restart sampling");   // NOI18N
        running = true;
        final ThreadMXBean threadBean = getThreadMXBean();
        out = new ByteArrayOutputStream(64 * 1024);
        try {
            samplesStream = new SamplesOutputStream(out, this, MAX_SAMPLES);
        } catch (IOException ex) {
            printStackTrace(ex);
            return;
        }
        startTime = System.currentTimeMillis();
        nanoTimeCorrection = startTime * 1000000 - System.nanoTime();
        // make sure that the sampler thread can be detected in a thread dump - add prefix
        timer = new Timer("sampler-"+name);   // NOI18N
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                synchronized (Sampler.this) {
                    if (stopped) {
                        return;
                    }
                    try {
                        ThreadInfo[] infos = threadBean.dumpAllThreads(false, false);
                        long timestamp = System.nanoTime() + nanoTimeCorrection;
                        samplesStream.writeSample(infos, timestamp, Thread.currentThread().getId());
                        updateStats(timestamp);
                    } catch (Throwable ex) {
                        printStackTrace(ex);
                    }
                }
            }
        }, SAMPLER_RATE, SAMPLER_RATE);
    }

    /**
     * Cancels the self-sampling. All sampled data are discarded.
     */
    public final void cancel() {
        stopSampling(true, null);
    }
    
    /**
     * Stop the self-sampling started by {@link #start()} method and writes the data to 
     * {@link DataOutputStream}. If the internal sampling logic detects that
     * the data are distorted for example due to heavy system I/O, collected 
     * samples are discarded and nothing is written to {@link DataOutputStream}.
     * <br>
     * This method can take a long time and should not be invoked from EDT.
     * @param dos {@link DataOutputStream} where sampled data is written to.
     */
    public final void stopAndWriteTo(@NonNull DataOutputStream dos) {
        stopSampling(false, dos);
    }
    
    /**
     * Stop the self-sampling, save and open gathered data. If there is no
     * sampled data, this method does nothing and returns immediately.
     * <br>
     * This method can take a long time and should not be invoked from EDT.
     */
    public final void stop() {
        stopSampling(false, null);
    }
    
    private synchronized void stopSampling(boolean cancel, DataOutputStream dos) {
        try {
            if (!running) throw new IllegalStateException("sampling was not started"); // NOI18N
            if (stopped) throw new IllegalStateException("sampling is not running");    // NOI18N
            stopped = true;
            timer.cancel();
            if (cancel || samples < 1) {
                return;
            }
            if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("sampling cannot be stopped from EDT");  //NOI18N
            double average = sum / samples;
            double std_deviation = Math.sqrt(devSquaresSum / samples);
            boolean writeCommand = dos != null;
            if (writeCommand) {
                Object[] params = new Object[]{startTime, "Samples", samples, "Average", average, "Minimum", min, "Maximum", max, "Std. deviation", std_deviation};  // NOI18N
                Logger.getLogger("org.netbeans.ui.performance").log(Level.CONFIG, "Snapshot statistics", params); // NOI18N
                if (average > MAX_AVERAGE || std_deviation > MAX_STDDEVIATION || samples < MIN_SAMPLES) {
                    // do not take snapshot if the sampling was not regular enough
                    return;
                }
            }
            samplesStream.close();
            samplesStream = null;
            if (writeCommand) {
                dos.write(out.toByteArray());
                dos.close();
                return;
            }
            saveSnapshot(out.toByteArray());
        } catch (Exception ex) {
            printStackTrace(ex);
        } finally {
            // just to be sure
            out = null;
            samplesStream = null;
        }
    }    
}
