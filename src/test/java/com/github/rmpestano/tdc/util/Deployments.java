package com.github.rmpestano.tdc.util;

import com.github.adminfaces.template.exception.AccessDeniedException;
import com.github.adminfaces.template.exception.BusinessException;
import com.github.adminfaces.template.session.AdminSession;
import com.github.adminfaces.template.util.Assert;
import com.github.rmpestano.tdc.pipeline.infra.security.LogonMB;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;

import java.io.File;
import java.util.UUID;

import static com.github.adminfaces.template.util.Assert.has;

/**
 * @author rafael-pestano
 *         Arquillian WebArchive factory
 */
public class Deployments {


    protected static final String WEB_INF= "src/main/webapp/WEB-INF";

    /**
     * @return base WebArchive for all arquillian tests
     */
    public static WebArchive createDeployment() {
        return createDeployment(null);
    }

    public static WebArchive createDeployment(String name) {
        if(!has(name)) {
            name = UUID.randomUUID().toString() +".war";
        }
        WebArchive war = ShrinkWrap.create(WebArchive.class,name);
        war.addPackages(true, "com.github.rmpestano.tdc.pipeline");
        war.addClasses(BusinessException.class, Assert.class, AccessDeniedException.class, LogonMB.class, AdminSession.class);
        //LIBS
        MavenResolverSystem resolver = Maven.resolver();
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("com.github.adminfaces:admin-persistence").withTransitivity().asFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.primefaces.extensions:primefaces-extensions").withTransitivity().asFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.omnifaces:omnifaces:2.1").withTransitivity().asFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-security-module-api").withTransitivity().asFile());
        war.addAsLibraries(resolver.loadPomFromFile("pom.xml").resolve("org.apache.deltaspike.modules:deltaspike-security-module-impl").withTransitivity().asFile());

        //WEB-INF

        war.addAsWebInfResource(new File(WEB_INF,"beans.xml"), "beans.xml");
        war.addAsWebInfResource(new File(WEB_INF,"web.xml"), "web.xml");
        war.addAsWebInfResource(new File(WEB_INF,"faces-config.xml"), "faces-config.xml");
        war.addAsWebInfResource("cars-test-ds.xml", "cars-ds.xml");

        //resources
        war.addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml");

        war.addAsResource(new File("src/main/resources/admin-config.properties"), "admin-config.properties");
        war.addAsResource(new File("src/main/resources/messages.properties"), "messages.properties");

        return war;
    }
}
