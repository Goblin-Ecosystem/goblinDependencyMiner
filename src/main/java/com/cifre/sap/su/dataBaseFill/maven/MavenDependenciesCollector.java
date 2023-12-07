package com.cifre.sap.su.dataBaseFill.maven;

import com.cifre.sap.su.utils.FileUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.util.*;

public class MavenDependenciesCollector {
    private final RemoteRepository centralRepo = new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build();
    private final DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    private final RepositorySystem system = newRepositorySystem(locator);
    private final RepositorySystemSession session = newSession(system);

    public MavenDependenciesCollector() {
        FileUtils.createDirectory("workspace");
    }

    public List<Dependency> getArtifactDirectDependencies(String gav){
        Artifact artifact = new DefaultArtifact(gav);
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest(artifact, List.of(centralRepo), null);
        try {
            ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
            return descriptorResult.getDependencies();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository("workspace");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }
}
