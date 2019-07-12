/*
 * Copyright: 2018 smarabbit studio.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @作   者： 黄开晖 (117227773@qq.com)
 * @日   期:  2019年1月27日
 */
package com.massyframework.assembly.web.taglib.scan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;

import org.apache.jasper.compiler.JarScannerFactory;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.servlet.TldScanner;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.xml.sax.SAXException;

import com.massyframework.assembly.Assembly;
import com.massyframework.logging.Logger;

/**
 * Tld文件扫描
 * @author huangkaihui
 *
 */
final class TldScannerEx extends TldScanner {
	
	
	
	private static final Log log = LogFactory.getLog(TldScannerEx.class);
    private static final String MSG = "org.apache.jasper.servlet.TldScanner";
    private static final String WEB_INF = "/WEB-INF/";
    private static final String TLD_EXT = ".tld";
    private static final String SCAN_CLASSPATH = "taglib.scan.classpath";
    
    private final Assembly assembly;
    private final ServletContext context;
    
    private final TldParserEx tldParser;
    private ClassLoader loader;
	
	/**
	 * @param context
	 * @param namespaceAware
	 * @param validation
	 * @param blockExternal
	 */
	public TldScannerEx(Assembly assembly, ServletContext context, ClassLoader classLoader, boolean namespaceAware, boolean validation, boolean blockExternal) {
		super(context, namespaceAware, validation, blockExternal);
		this.assembly = assembly;
		this.tldParser = new TldParserEx(namespaceAware, validation, blockExternal);
		this.context = context;
		this.loader = Objects.requireNonNull(classLoader, "\"classLoader\" cannot be null.");
	}
		
	@Override
	public void scanJars() {
		try {
			JarScanner scanner = JarScannerFactory.getJarScanner(context);	
			
			boolean scanClassPath = Boolean.parseBoolean(
					this.assembly.getAssemblyConfig().getInitParameter(SCAN_CLASSPATH, "true"));
			if (!scanClassPath) {
				if (scanner instanceof StandardJarScanner) {
					((StandardJarScanner)scanner).setScanClassPath(scanClassPath);
				}
			}
			
	        TldScannerCallback callback = new TldScannerCallback();
	        scanner.scan(JarScanType.TLD, context, callback);
	        if (callback.scanFoundNoTLDs()) {
	            log.info(Localizer.getMessage("jsp.tldCache.noTldSummary"));
	        }
		}catch(Exception e) {
			
		}
        
		if (this.loader != null){
			List<String> tldResources = this.getTldFiles();
			for (String resource: tldResources){
				try{
					this.parseTld(resource);
				}catch(Exception e){
					
				}
					
			}
		}
	}
		
	private List<String> getTldFiles(){
		List<String> result = new ArrayList<String>();
	
		//扫描META-INF目录下的所有.tld文件	
		Set<String> paths = this.context.getResourcePaths("META-INF/");
		for (String path: paths){
			if (path.endsWith(".tld")){
				result.add(path);
			}
		}
				
		return result;
	}

	@Override
	protected void scanJspConfig() throws IOException, SAXException {
		JspConfigDescriptor jspConfigDescriptor = context.getJspConfigDescriptor();
        if (jspConfigDescriptor == null) {
            return;
        }

        Collection<TaglibDescriptor> descriptors = jspConfigDescriptor.getTaglibs();
        for (TaglibDescriptor descriptor : descriptors) {
            String taglibURI = descriptor.getTaglibURI();
            String resourcePath = descriptor.getTaglibLocation();
            // Note: Whilst the Servlet 2.4 DTD implies that the location must
            // be a context-relative path starting with '/', JSP.7.3.6.1 states
            // explicitly how paths that do not start with '/' should be
            // handled.
            if (!resourcePath.startsWith("/")) {
                resourcePath = WEB_INF + resourcePath;
            }
            if (this.getUriTldResourcePathMap().containsKey(taglibURI)) {
                log.warn(Localizer.getMessage(MSG + ".webxmlSkip",
                        resourcePath,
                        taglibURI));
                continue;
            }

            if (log.isTraceEnabled()) {
                log.trace(Localizer.getMessage(MSG + ".webxmlAdd",
                        resourcePath,
                        taglibURI));
            }

            URL url = context.getResource(resourcePath);
            if (url != null) {
                TldResourcePath tldResourcePath;
                if (resourcePath.endsWith(".jar")) {
                    // if the path points to a jar file, the TLD is presumed to be
                    // inside at META-INF/taglib.tld
                    tldResourcePath = new TldResourcePath(url, resourcePath, "META-INF/taglib.tld");
                } else {
                    tldResourcePath = new TldResourcePath(url, resourcePath);
                }
                // parse TLD but store using the URI supplied in the descriptor
                TaglibXml tld = tldParser.parse(tldResourcePath);
                this.getUriTldResourcePathMap().put(taglibURI, tldResourcePath);
                this.getTldResourcePathTaglibXmlMap().put(tldResourcePath, tld);
                if (tld.getListeners() != null) {
                    this.getListeners().addAll(tld.getListeners());
                }
            } else {
                log.warn(Localizer.getMessage(MSG + ".webxmlFailPathDoesNotExist",
                        resourcePath,
                        taglibURI));
                continue;
            }
        }
	}
		
	@Override
	protected void parseTld(TldResourcePath path) throws IOException, SAXException {
        if (this.getTldResourcePathTaglibXmlMap().containsKey(path)) {
            // TLD has already been parsed as a result of processing web.xml
            return;
        }
        
        
        TaglibXml tld = tldParser.parse(path);
        String uri = tld.getUri();
        if (uri != null) {
            if (!this.getUriTldResourcePathMap().containsKey(uri)) {
                this.getUriTldResourcePathMap().put(uri, path);
            }
        }
        this.getTldResourcePathTaglibXmlMap().put(path, tld);
        if (tld.getListeners() != null) {
            this.getListeners().addAll(tld.getListeners());
        }
        
    }
	
	class TldScannerCallback implements JarScannerCallback {
        private boolean foundJarWithoutTld = false;
        private boolean foundFileWithoutTld = false;


        @Override
        public void scan(Jar jar, String webappPath, boolean isWebapp) throws IOException {
            boolean found = false;
            URL jarFileUrl = jar.getJarFileURL();
            jar.nextEntry();
            for (String entryName = jar.getEntryName();
                entryName != null;
                jar.nextEntry(), entryName = jar.getEntryName()) {
                if (!(entryName.startsWith("META-INF/") &&
                        entryName.endsWith(TLD_EXT))) {
                    continue;
                }
                found = true;
                TldResourcePath tldResourcePath =
                        new TldResourcePath(jarFileUrl, webappPath, entryName);
                try {
                    parseTld(tldResourcePath);
                } catch (SAXException e) {
                	Logger logger = assembly.getLogger();
                	if (logger.isDebugEnabled()) {
                		logger.debug("load taglib failed, skip.", e);
                	}
                    break;
                }
            }
            if (found) {
                if (log.isDebugEnabled()) {
                    log.debug(Localizer.getMessage("jsp.tldCache.tldInJar", jarFileUrl.toString()));
                }
            } else {
                foundJarWithoutTld = true;
                if (log.isDebugEnabled()) {
                    log.debug(Localizer.getMessage(
                            "jsp.tldCache.noTldInJar", jarFileUrl.toString()));
                }
            }
        }

        @Override
        public void scan(File file, final String webappPath, boolean isWebapp)
                throws IOException {
            File metaInf = new File(file, "META-INF");
            if (!metaInf.isDirectory()) {
                return;
            }
            foundFileWithoutTld = false;
            final Path filePath = file.toPath();
            Files.walkFileTree(metaInf.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file,
                                                 BasicFileAttributes attrs)
                        throws IOException {
                    Path fileName = file.getFileName();
                    if (fileName == null || !fileName.toString().toLowerCase(
                            Locale.ENGLISH).endsWith(TLD_EXT)) {
                        return FileVisitResult.CONTINUE;
                    }

                    foundFileWithoutTld = true;
                    String resourcePath;
                    if (webappPath == null) {
                        resourcePath = null;
                    } else {
                        String subPath = file.subpath(
                                filePath.getNameCount(), file.getNameCount()).toString();
                        if ('/' != File.separatorChar) {
                            subPath = subPath.replace(File.separatorChar, '/');
                        }
                        resourcePath = webappPath + "/" + subPath;
                    }

                    try {
                        URL url = file.toUri().toURL();
                        TldResourcePath path = new TldResourcePath(url, resourcePath);
                        parseTld(path);
                    } catch (SAXException e) {
                        throw new IOException(e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            if (foundFileWithoutTld) {
                if (log.isDebugEnabled()) {
                    log.debug(Localizer.getMessage("jsp.tldCache.tldInDir",
                            file.getAbsolutePath()));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(Localizer.getMessage("jsp.tldCache.noTldInDir",
                            file.getAbsolutePath()));
                }
            }
        }

        @Override
        public void scanWebInfClasses() throws IOException {
            // This is used when scanAllDirectories is enabled and one or more
            // JARs have been unpacked into WEB-INF/classes as happens with some
            // IDEs.

            Set<String> paths = context.getResourcePaths(WEB_INF + "classes/META-INF");
            if (paths == null) {
                return;
            }

            for (String path : paths) {
                if (path.endsWith(TLD_EXT)) {
                    try {
                        parseTld(path);
                    } catch (SAXException e) {
                        throw new IOException(e);
                    }
                }
            }
        }


        boolean scanFoundNoTLDs() {
            return foundJarWithoutTld;
        }
    }
}
