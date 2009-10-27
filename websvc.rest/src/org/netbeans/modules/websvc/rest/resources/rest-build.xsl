<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:webproject1="http://www.netbeans.org/ns/web-project/1"
                xmlns:webproject2="http://www.netbeans.org/ns/web-project/2"
                xmlns:webproject3="http://www.netbeans.org/ns/web-project/3"
                xmlns:projdeps="http://www.netbeans.org/ns/ant-project-references/1"
                xmlns:jaxrs="http://www.netbeans.org/ns/jax-rs/1"
                exclude-result-prefixes="xalan p projdeps">
                    
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>

    <xsl:template match="/">
        <![CDATA[        ]]>
        <xsl:comment><![CDATA[
        *** GENERATED - DO NOT EDIT  ***
        ]]></xsl:comment>
        
        <xsl:variable name="name" select="/p:project/p:configuration/webproject3:data/webproject3:name"/>
        <!-- Synch with build-impl.xsl: -->
        <xsl:variable name="codename" select="translate($name, ' ', '_')"/>
        <project name="{$codename}-rest-build">
            <xsl:attribute name="basedir">..</xsl:attribute>
            
            <target name="-check-trim">
                <condition property="do.trim">
                    <and>
                        <isset property="client.urlPart"/>
                        <length string="${{client.urlPart}}" when="greater" length="0" />
                    </and>
                </condition>
            </target>
            <target name="-trim-url" if="do.trim">
                <pathconvert pathsep="/" property="rest.base.url">
                    <propertyset>
                        <propertyref name="client.url"/>
                    </propertyset>
                    <globmapper from="*${{client.urlPart}}" to="*/" />
                </pathconvert>
            </target>
            <target name="-spare-url" unless="do.trim">
                <property name="rest.base.url" value="${{client.url}}"/>
            </target>
            
            <target name="test-restbeans" depends="run-deploy,-init-display-browser,-check-trim,-trim-url,-spare-url">
                <!--<xmlproperty file="${{webinf.dir}}/web.xml"/>-->
                <!--<replace file="${{restbeans.test.file}}" token="${{base.url.token}}" value="${{rest.base.url}}||${{web-app.servlet-mapping.servlet-name}}||${{web-app.servlet-mapping.url-pattern}}"/>-->
                <replace file="${{restbeans.test.file}}" token="${{base.url.token}}" value="${{rest.base.url}}||${{rest.application.path}}"/>
                <condition property="do.browse-url">
                    <istrue value="${{display.browser}}"/>
                </condition>
                <antcall target="browse-url"/>
            </target>
            <target name="browse-url" if="do.browse-url">
                <nbbrowse url="${{restbeans.test.url}}"/>
            </target>

            <target name="check-rest-config-props" depends="-do-init">
                <condition property="do-generate-rest-config">
                    <and>
                        <equals arg1="${{rest.config.type}}" arg2="ide"/>
                        <isset property="rest.root.resources"/>
                        <isset property="rest.resources.path"/>
                    </and>
                </condition>
            </target>
            <target name = "generate-rest-config" depends="check-rest-config-props" if="do-generate-rest-config">
                <mkdir dir="${{build.generated.sources.dir}}/rest/org/netbeans/rest/application/config/"/>
                <echo file="${{build.generated.sources.dir}}/rest/org/netbeans/rest/application/config/ApplicationConfig.java"><![CDATA[/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.rest.application.config;

@javax.ws.rs.ApplicationPath("___APPLICATION_PATH___")
public class ApplicationConfig extends javax.ws.rs.core.Application {

    private static final Class<?>[] resourceClasses = {
        ___RESOURCE_CLASSES___
    };

    @Override
    public java.util.Set<Class<?>> getClasses() {
        java.util.Set<Class<?>> classes = new java.util.HashSet<Class<?>>();
        for (Class clazz : resourceClasses) {
            classes.add(clazz);
        }
        return classes;
    }

}]]></echo>
                <replace file="${{build.generated.sources.dir}}/rest/org/netbeans/rest/application/config/ApplicationConfig.java"
                         token="___RESOURCE_CLASSES___"
                         value="${{rest.root.resources}}"/>
                <replace file="${{build.generated.sources.dir}}/rest/org/netbeans/rest/application/config/ApplicationConfig.java"
                         token="___APPLICATION_PATH___"
                         value="${{rest.resources.path}}"/>
            </target>

        </project>
            
    </xsl:template>
    
</xsl:stylesheet>
