package com.kor.tomcat;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class EmbeddedTomcatRunner {

    public static void main(String[] args) throws Exception {
        String webappDir = "src/main/webapp/";
        String absoluteWebappPath = new File(webappDir).getAbsolutePath();

        System.setProperty("catalina.base", "temp/base");
        System.setProperty("catalina.home", "temp/home");
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp/base");
        tomcat.setPort(8080);
        tomcat.getConnector();

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", absoluteWebappPath);

        File additionWebInfClasses = new File("build/classes/java/main");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("Tomcat started on port 8080");
        tomcat.getServer().await();
    }
}
