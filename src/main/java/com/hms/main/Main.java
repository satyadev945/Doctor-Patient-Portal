package com.hms.main;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Embedded Tomcat bootstrap class.
 *
 * <p>Replaces the external application server (WAR deployment) with a
 * self-contained executable JAR that bundles Tomcat 9.x.  This enables
 * direct container deployment on Amazon ECS, EKS, or AWS Fargate without
 * any external application server infrastructure (cr-java-0107).</p>
 *
 * <p>The HTTP port is read from the {@code PORT} environment variable
 * (default: 8080) so the container runtime can inject the port at
 * deployment time without rebuilding the image.</p>
 */
public class Main {

    /** Environment variable name used to configure the HTTP listen port. */
    private static final String PORT_ENV = "PORT";

    /** Default HTTP port when {@code PORT} is not set. */
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = resolvePort();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);

        // Ensure the HTTP connector is created and bound to the resolved port
        Connector connector = tomcat.getConnector();
        connector.setPort(port);

        // Locate the webapp directory bundled inside the JAR (copied by
        // maven-resources-plugin into target/classes/webapp at build time).
        String webappDir = resolveWebappDir();

        Context ctx = tomcat.addWebapp("", webappDir);

        // Make compiled classes available to the embedded context so that
        // servlets registered in web.xml can be found at runtime.
        WebResourceRoot resources = new StandardRoot(ctx);
        File classesDir = new File(resolveClassesDir());
        if (classesDir.exists()) {
            resources.addPreResources(
                    new DirResourceSet(resources, "/WEB-INF/classes",
                            classesDir.getAbsolutePath(), "/"));
        }
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("Doctor-Patient-Portal started on port " + port);
        tomcat.getServer().await();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves the HTTP port from the {@code PORT} environment variable.
     * Falls back to {@value #DEFAULT_PORT} when the variable is absent or
     * cannot be parsed as an integer.
     */
    private static int resolvePort() {
        String envPort = System.getenv(PORT_ENV);
        if (envPort != null && !envPort.isEmpty()) {
            try {
                return Integer.parseInt(envPort.trim());
            } catch (NumberFormatException e) {
                System.err.println("WARNING: Invalid value for " + PORT_ENV
                        + " '" + envPort + "', using default " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }

    /**
     * Returns the absolute path to the {@code webapp} directory that was
     * copied into the JAR by maven-resources-plugin.  When running from an
     * exploded build the directory is located relative to the class-path root.
     */
    private static String resolveWebappDir() throws URISyntaxException {
        URL webappUrl = Main.class.getClassLoader().getResource("webapp");
        if (webappUrl != null) {
            return new File(webappUrl.toURI()).getAbsolutePath();
        }
        // Fallback: conventional Maven output location when running from IDE
        return new File("src/main/webapp").getAbsolutePath();
    }

    /**
     * Returns the absolute path to the compiled classes directory so that
     * servlet classes are visible to the embedded Tomcat context.
     */
    private static String resolveClassesDir() throws URISyntaxException {
        URL classesUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
        if (classesUrl != null) {
            return new File(classesUrl.toURI()).getAbsolutePath();
        }
        return new File("target/classes").getAbsolutePath();
    }
}
