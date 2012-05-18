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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.embedder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.mapping.Lifecycle;
import org.apache.maven.lifecycle.mapping.LifecycleMapping;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.maven.embedder.exec.ProgressTransferListener;
import org.netbeans.modules.maven.embedder.impl.NbWorkspaceReader;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.sonatype.aether.impl.internal.SimpleLocalRepositoryManager;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.repository.DefaultAuthenticationSelector;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;
import org.sonatype.aether.util.repository.DefaultProxySelector;

/**
 * Handle for the embedded Maven system, used to parse POMs and more.
 */
public final class MavenEmbedder {

    private static final Logger LOG = Logger.getLogger(MavenEmbedder.class.getName());
    private final PlexusContainer plexus;
    private final DefaultMaven maven;
    private final ProjectBuilder projectBuilder;
    private final RepositorySystem repositorySystem;
    private final MavenExecutionRequestPopulator populator;
    private final SettingsBuilder settingsBuilder;
    private final EmbedderConfiguration embedderConfiguration;
    private final SettingsDecrypter settingsDecrypter;
    private long settingsTimestamp;
    private Settings settings;

    MavenEmbedder(EmbedderConfiguration configuration) throws ComponentLookupException {
        embedderConfiguration = configuration;
        plexus = configuration.getContainer();
        this.maven = (DefaultMaven) plexus.lookup(Maven.class);
        this.projectBuilder = plexus.lookup(ProjectBuilder.class);
        this.repositorySystem = plexus.lookup(RepositorySystem.class);
        this.settingsBuilder = plexus.lookup(SettingsBuilder.class);
        this.populator = plexus.lookup(MavenExecutionRequestPopulator.class);
        settingsDecrypter = plexus.lookup(SettingsDecrypter.class);
    }
    
    public PlexusContainer getPlexus() {
        return plexus;
    }

    public Properties getSystemProperties() {
        return embedderConfiguration.getSystemProperties();
    }

    boolean isOffline() {
        return embedderConfiguration.isOffline();
    }

    public ArtifactRepository getLocalRepository() {
        try {
            String localRepositoryPath = getSettings().getLocalRepository();
            if (localRepositoryPath != null) {
                return repositorySystem.createLocalRepository(FileUtil.normalizeFile(new File(localRepositoryPath)));
            }
            return repositorySystem.createDefaultLocalRepository();
        } catch (InvalidRepositoryException ex) {
            // can't happen
            throw new IllegalStateException(ex);
        }
    }
    
    /**
     * 
     * @return normalized File for local repository root
     * @since 2.26
     */
    public File getLocalRepositoryFile() {
        String s = getLocalRepository().getBasedir();
        return FileUtil.normalizeFile(new File(s));
    }

    public synchronized Settings getSettings() {
        if (Boolean.getBoolean("no.local.settings")) { // for unit tests
            return new Settings(); // could instead make public void setSettings(Settings settingsOverride)
        }
        File settingsXml = embedderConfiguration.getSettingsXml();
        long newSettingsTimestamp = settingsXml.hashCode() ^ settingsXml.lastModified() ^ MavenCli.DEFAULT_USER_SETTINGS_FILE.lastModified();
        // could be included but currently constant: hashCode() of those files; getSystemProperties.hashCode()
        if (settings != null && settingsTimestamp == newSettingsTimestamp) {
            LOG.log(Level.FINER, "settings.xml cache hit for {0}", this);
            return settings;
        }
        LOG.log(Level.FINE, "settings.xml cache miss for {0}", this);
        SettingsBuildingRequest req = new DefaultSettingsBuildingRequest();
        req.setGlobalSettingsFile(settingsXml);
        req.setUserSettingsFile(MavenCli.DEFAULT_USER_SETTINGS_FILE);
        req.setSystemProperties(getSystemProperties());
        try {
            settings = settingsBuilder.build(req).getEffectiveSettings();
            settingsTimestamp = newSettingsTimestamp;
            return settings;
        } catch (SettingsBuildingException x) {
            LOG.log(Level.FINE, null, x); // #192768: do not even bother logging to console by default, too noisy
            return new Settings();
        }
    }
    
    @Deprecated
    public MavenExecutionResult readProjectWithDependencies(MavenExecutionRequest req) {
        return readProjectWithDependencies(req, true);
    }
    
    public MavenExecutionResult readProjectWithDependencies(MavenExecutionRequest req, boolean useWorkspaceResolution) {
        if (useWorkspaceResolution) {
            req.setWorkspaceReader(new NbWorkspaceReader());
        }
        File pomFile = req.getPom();
        MavenExecutionResult result = new DefaultMavenExecutionResult();
        try {
            ProjectBuildingRequest configuration = req.getProjectBuildingRequest();
            configuration.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
            configuration.setResolveDependencies(true);
            configuration.setRepositorySession(maven.newRepositorySession(req));
            ProjectBuildingResult projectBuildingResult = projectBuilder.build(pomFile, configuration);
            result.setProject(projectBuildingResult.getProject());
            result.setDependencyResolutionResult(projectBuildingResult.getDependencyResolutionResult());
        } catch (ProjectBuildingException ex) {
            //don't add the exception here. this should come out as a build marker, not fill
            //the error logs with msgs
            return result.addException(ex);
        }
        return result;
    }

    public Artifact createArtifactWithClassifier(@NonNull String groupId, @NonNull String artifactId, @NonNull String version, String type, String classifier) {
        return repositorySystem.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
    }

    public Artifact createArtifact(@NonNull String groupId, @NonNull String artifactId, @NonNull String version, @NonNull String packaging) {
         return repositorySystem.createArtifact(groupId,  artifactId,  version,  packaging);
    }

    public Artifact createArtifact(@NonNull String groupId, @NonNull String artifactId, @NonNull String version, String scope, String type) {
         return repositorySystem.createArtifact( groupId,  artifactId,  version,   scope,  type);
    }

    public Artifact createProjectArtifact(@NonNull String groupId, @NonNull String artifactId, @NonNull String version) {
        return repositorySystem.createProjectArtifact(groupId, artifactId, version);
    }
    
    
    /**
     * using this method one creates an ArtifactRepository instance with injected mirrors and proxies
     * @param url
     * @param id
     * @return 
     */
    public ArtifactRepository createRemoteRepository(String url, String id) {
        setUpLegacySupport();
        ArtifactRepositoryFactory fact = lookupComponent(ArtifactRepositoryFactory.class);
        assert fact!=null : "ArtifactRepositoryFactory component not found in maven";
        ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
        ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy(true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);
        return fact.createArtifactRepository(id, url, new DefaultRepositoryLayout(), snapshotsPolicy, releasesPolicy);
    }
    

    /**
     * 
     * @param sources
     * @param remoteRepositories - these instances need to be properly mirrored and proxied. Either by creating via EmbedderFactory.createRemoteRepository()
     *              or by using instances from MavenProject
     * @param localRepository
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException 
     */
    public void resolve(Artifact sources, List<ArtifactRepository> remoteRepositories, ArtifactRepository localRepository) throws ArtifactResolutionException, ArtifactNotFoundException {
        setUpLegacySupport();
        ArtifactResolutionRequest req = new ArtifactResolutionRequest();
        req.setLocalRepository(localRepository);
        req.setRemoteRepositories(remoteRepositories);
        req.setArtifact(sources);
        req.setOffline(isOffline());
        ArtifactResolutionResult result = repositorySystem.resolve(req);
        // XXX check result for exceptions and throw them now?
        for (Exception ex : result.getExceptions()) {
            LOG.log(Level.FINE, null, ex);
        }
    }

    //TODO possibly rename.. build sounds like something else..
    public ProjectBuildingResult buildProject(Artifact art, ProjectBuildingRequest req) throws ProjectBuildingException {
        if (req.getLocalRepository() == null) {
           req.setLocalRepository(getLocalRepository());
        }
        MavenExecutionRequest request = createMavenExecutionRequest();
        req.setProcessPlugins(false);
        req.setRepositorySession(maven.newRepositorySession(request));
        return projectBuilder.build(art, req);
    }

    public MavenExecutionResult execute(MavenExecutionRequest req) {
        return maven.execute(req);
    }
    
    /**
     * Creates a list of POM models in an inheritance lineage.
     * Each resulting model is "raw", so contains no interpolation or inheritance.
     * In particular beware that groupId and/or version may be null if inherited from a parent; use {@link Model#getParent} to resolve.
     * @param pom a POM to inspect
     * @param embedder an embedder to use
     * @return a list of models, starting with the specified POM, going through any parents, finishing with the Maven superpom (with a null artifactId)
     * @throws ModelBuildingException if the POM or parents could not even be parsed; warnings are not reported
     */
    public List<Model> createModelLineage(File pom) throws ModelBuildingException {
        ModelBuilder mb = lookupComponent(ModelBuilder.class);
        assert mb!=null : "ModelBuilder component not found in maven";
        ModelBuildingRequest req = new DefaultModelBuildingRequest();
        req.setPomFile(pom);
        req.setProcessPlugins(false);
        req.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
        req.setModelResolver(new NBRepositoryModelResolver(this));
        req.setSystemProperties(getSystemProperties());
        
        ModelBuildingResult res = mb.build(req);
        List<Model> toRet = new ArrayList<Model>();

        for (String id : res.getModelIds()) {
            Model m = res.getRawModel(id);
            toRet.add(m);
        }
//        for (ModelProblem p : res.getProblems()) {
//            System.out.println("problem=" + p);
//            if (p.getException() != null) {
//                p.getException().printStackTrace();
//            }
//        }
        return toRet;
    }    
    
    public List<String> getLifecyclePhases() {

        LifecycleMapping lifecycleMapping = lookupComponent(LifecycleMapping.class);
        if (lifecycleMapping != null) {
            Set<String> phases = new TreeSet<String>();
            Map<String, Lifecycle> lifecycles = lifecycleMapping.getLifecycles();
            for (Lifecycle lifecycle : lifecycles.values()) {
                phases.addAll(lifecycle.getPhases().keySet());
            }
            return new ArrayList<String>(phases);
        }

        return Collections.<String>emptyList();
    }

    public  <T> T lookupComponent(Class<T> clazz) {
        try {
            return plexus.lookup(clazz);
        } catch (ComponentLookupException ex) {
            LOG.warning(ex.getMessage());
        }
        return null;
    }

    public MavenExecutionRequest createMavenExecutionRequest(){
        MavenExecutionRequest req = new DefaultMavenExecutionRequest();

        ArtifactRepository localRepository = getLocalRepository();
        req.setLocalRepository(localRepository);
        req.setLocalRepositoryPath(localRepository.getBasedir());

        //TODO: do we need to validate settings files?
        File settingsXml = embedderConfiguration.getSettingsXml();
        if (settingsXml !=null && settingsXml.exists()) {
            req.setGlobalSettingsFile(settingsXml);
        }
        if (MavenCli.DEFAULT_USER_SETTINGS_FILE != null && MavenCli.DEFAULT_USER_SETTINGS_FILE.exists()) {
          req.setUserSettingsFile(MavenCli.DEFAULT_USER_SETTINGS_FILE);
        }
        
        req.setSystemProperties(getSystemProperties());
        try {
            //#212214 populating from settings needs to come first
            //it adds mirrors and proxies to the request
            //later on populateDefaults() will use these to replace/configure the default "central" repository
            // and the repository id used is important down the road for resolution in EnhancedLocalRepositoryManager
            populator.populateFromSettings(req, getSettings());
            populator.populateDefaults(req);
        } catch (MavenExecutionRequestPopulationException x) {
            // XXX where to display this?
            Exceptions.printStackTrace(x);
        }
        req.setOffline(isOffline());

        return req;
    }

    /**
     * Needed to avoid an NPE in {@link org.sonatype.aether.impl.internal.DefaultArtifactResolver#resolveArtifacts} under some conditions.
     * (Also {@link org.sonatype.aether.impl.internal.DefaultMetadataResolver#resolve}; wherever a {@link org.sonatype.aether.RepositorySystemSession} is used.)
     * Should be called in the same thread as whatever thread was throwing the NPE.
     */
    public void setUpLegacySupport() {
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();
        session.setOffline(isOffline());
        session.setLocalRepositoryManager(new SimpleLocalRepositoryManager(getLocalRepository().getBasedir()));
        // Adapted from DefaultMaven.newRepositorySession, but does not look like that can be called directly:
        DefaultMirrorSelector mirrorSelector = new DefaultMirrorSelector();
        Settings _settings = getSettings();
        for (Mirror m : _settings.getMirrors()) {
            mirrorSelector.add(m.getId(), m.getUrl(), m.getLayout(), false, m.getMirrorOf(), m.getMirrorOfLayouts());
        }
        session.setMirrorSelector(mirrorSelector);
        SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(new DefaultSettingsDecryptionRequest(_settings));
        DefaultProxySelector proxySelector = new DefaultProxySelector();
        for (Proxy p : decryptionResult.getProxies()) {
            if (p.isActive()) {
               //#null -> getProtocol() #209499
               proxySelector.add(new org.sonatype.aether.repository.Proxy(p.getProtocol(), p.getHost(), p.getPort(), new Authentication(p.getUsername(), p.getPassword())), p.getNonProxyHosts());
            }
        }
        session.setProxySelector(proxySelector);
        DefaultAuthenticationSelector authenticationSelector = new DefaultAuthenticationSelector();
        for (Server s : decryptionResult.getServers()) {
            authenticationSelector.add(s.getId(), new Authentication(s.getUsername(), s.getPassword(), s.getPrivateKey(), s.getPassphrase()));
        }
        session.setAuthenticationSelector(authenticationSelector);
        DefaultMavenExecutionRequest mavenExecutionRequest = new DefaultMavenExecutionRequest();
        mavenExecutionRequest.setOffline(isOffline());
        mavenExecutionRequest.setTransferListener(ProgressTransferListener.activeListener());
        session.setTransferListener(ProgressTransferListener.activeListener());
        lookupComponent(LegacySupport.class).setSession(new MavenSession(getPlexus(), session, mavenExecutionRequest, new DefaultMavenExecutionResult()));
    }

}
