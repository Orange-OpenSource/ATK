package com.orange.atk.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.orange.atk.atkUI.corecli.Configuration;
import com.orange.atk.platform.Platform;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {
	private static final String TAG = NanoHTTPD.class.getName();
	private static NanoHTTPD server = null;
    /**
     * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
     */
    private static final Map<String, String> MIME_TYPES;
    static {
        Map<String, String> mime = new HashMap<String, String>();
        mime.put("css", "text/css");
        mime.put("htm", "text/html");
        mime.put("html", "text/html");
        mime.put("xml", "text/xml");
        mime.put("txt", "text/plain");
        mime.put("asc", "text/plain");
        mime.put("gif", "image/gif");
        mime.put("jpg", "image/jpeg");
        mime.put("jpeg", "image/jpeg");
        mime.put("png", "image/png");
        mime.put("mp3", "audio/mpeg");
        mime.put("m3u", "audio/mpeg-url");
        mime.put("mp4", "video/mp4");
        mime.put("ogv", "video/ogg");
        mime.put("flv", "video/x-flv");
        mime.put("mov", "video/quicktime");
        mime.put("swf", "application/x-shockwave-flash");
        mime.put("js", "application/javascript");
        mime.put("pdf", "application/pdf");
        mime.put("doc", "application/msword");
        mime.put("ogg", "application/x-ogg");
        mime.put("zip", "application/octet-stream");
        mime.put("exe", "application/octet-stream");
        mime.put("class", "application/octet-stream");
        MIME_TYPES = mime;
    }

    private File rootDir;

    public WebServer(String host, int port, File wwwroot) {
        super(host, port);
        this.rootDir = wwwroot;
    }

    public File getRootDir() {
        return rootDir;
    }

    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20' instead of '+'.
     */
    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                newUri += "/";
            else if (tok.equals(" "))
                newUri += "%20";
            else {
                try {
                    newUri += URLEncoder.encode(tok, "UTF-8");
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        return newUri;
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI, ignores all headers and HTTP parameters.
     */
    public Response serveFile(String uri, Map<String, String> header, File homeDir) {
        Response res = null;

        // Make sure we won't die of an exception later
        if (!homeDir.isDirectory())
            res = new Response(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.");

        if (res == null) {
            // Remove URL arguments
            uri = uri.trim().replace(File.separatorChar, '/');
            if (uri.indexOf('?') >= 0)
                uri = uri.substring(0, uri.indexOf('?'));

            // Prohibit getting out of current directory
            if (uri.startsWith("src/main") || uri.endsWith("src/main") || uri.contains("../"))
                res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Won't serve ../ for security reasons.");
        }

        File f = new File(homeDir, uri);
        if (res == null && !f.exists())
            res = new Response(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Error 404, file not found.");

        // List the directory, if necessary
        if (res == null && f.isDirectory()) {
            // Browsers get confused without '/' after the
            // directory, send a redirect.
            if (!uri.endsWith("/")) {
                uri += "/";
                res = new Response(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, "<html><body>Redirected: <a href=\"" + uri + "\">" + uri
                        + "</a></body></html>");
                res.addHeader("Location", uri);
            }

            if (res == null) {
                // First try index.html and index.htm
                if (new File(f, "index.html").exists())
                    f = new File(homeDir, uri + "/index.html");
                else if (new File(f, "index.htm").exists())
                    f = new File(homeDir, uri + "/index.htm");
                    // No index file, list the directory if it is readable
                else if (f.canRead()) {
                    String[] files = f.list();
                    String msg = "<html><body><h1>Directory " + uri + "</h1><br/>";

                    if (uri.length() > 1) {
                        String u = uri.substring(0, uri.length() - 1);
                        int slash = u.lastIndexOf('/');
                        if (slash >= 0 && slash < u.length())
                            msg += "<b><a href=\"" + uri.substring(0, slash + 1) + "\">..</a></b><br/>";
                    }

                    if (files != null) {
                        for (int i = 0; i < files.length; ++i) {
                            File curFile = new File(f, files[i]);
                            boolean dir = curFile.isDirectory();
                            if (dir) {
                                msg += "<b>";
                                files[i] += "/";
                            }

                            msg += "<a href=\"" + encodeUri(uri + files[i]) + "\">" + files[i] + "</a>";

                            // Show file size
                            if (curFile.isFile()) {
                                long len = curFile.length();
                                msg += " &nbsp;<font size=2>(";
                                if (len < 1024)
                                    msg += len + " bytes";
                                else if (len < 1024 * 1024)
                                    msg += len / 1024 + "." + (len % 1024 / 10 % 100) + " KB";
                                else
                                    msg += len / (1024 * 1024) + "." + len % (1024 * 1024) / 10 % 100 + " MB";

                                msg += ")</font>";
                            }
                            msg += "<br/>";
                            if (dir)
                                msg += "</b>";
                        }
                    }
                    msg += "</body></html>";
                    res = new Response(msg);
                } else {
                    res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: No directory listing.");
                }
            }
        }

        try {
            if (res == null) {
                // Get MIME type from file name extension, if possible
                String mime = null;
                int dot = f.getCanonicalPath().lastIndexOf('.');
                if (dot >= 0)
                    mime = MIME_TYPES.get(f.getCanonicalPath().substring(dot + 1).toLowerCase());
                if (mime == null)
                    mime = NanoHTTPD.MIME_DEFAULT_BINARY;

                // Calculate etag
                String etag = Integer.toHexString((f.getAbsolutePath() + f.lastModified() + "" + f.length()).hashCode());

                // Support (simple) skipping:
                long startFrom = 0;
                long endAt = -1;
                String range = header.get("range");
                if (range != null) {
                    if (range.startsWith("bytes=")) {
                        range = range.substring("bytes=".length());
                        int minus = range.indexOf('-');
                        try {
                            if (minus > 0) {
                                startFrom = Long.parseLong(range.substring(0, minus));
                                endAt = Long.parseLong(range.substring(minus + 1));
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                // Change return code and add Content-Range header when skipping is requested
                long fileLen = f.length();
                if (range != null && startFrom >= 0) {
                    if (startFrom >= fileLen) {
                        res = new Response(Response.Status.RANGE_NOT_SATISFIABLE, NanoHTTPD.MIME_PLAINTEXT, "");
                        res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                        res.addHeader("ETag", etag);
                    } else {
                        if (endAt < 0)
                            endAt = fileLen - 1;
                        long newLen = endAt - startFrom + 1;
                        if (newLen < 0)
                            newLen = 0;

                        final long dataLen = newLen;
                        FileInputStream fis = new FileInputStream(f) {
                            @Override
                            public int available() throws IOException {
                                return (int) dataLen;
                            }
                        };
                        fis.skip(startFrom);

                        res = new Response(Response.Status.PARTIAL_CONTENT, mime, fis);
                        res.addHeader("Content-Length", "" + dataLen);
                        res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                        res.addHeader("ETag", etag);
                    }
                } else {
                    if (etag.equals(header.get("if-none-match")))
                        res = new Response(Response.Status.NOT_MODIFIED, mime, "");
                    else {
                        res = new Response(Response.Status.OK, mime, new FileInputStream(f));
                        res.addHeader("Content-Length", "" + fileLen);
                        res.addHeader("ETag", etag);
                    }
                }
            }
        } catch (IOException ioe) {
            res = new Response(Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
        }

        res.addHeader("Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
        return res;
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
        System.out.println(method + " '" + uri + "' ");

        Iterator<String> e = header.keySet().iterator();
        while (e.hasNext()) {
            String value = e.next();
            System.out.println("  HDR: '" + value + "' = '" + header.get(value) + "'");
        }
        e = parms.keySet().iterator();
        while (e.hasNext()) {
            String value = e.next();
            System.out.println("  PRM: '" + value + "' = '" + parms.get(value) + "'");
        }
        e = files.keySet().iterator();
        while (e.hasNext()) {
            String value = e.next();
            System.out.println("  UPLOADED: '" + value + "' = '" + files.get(value) + "'");
        }

        return serveFile(uri, header, getRootDir());
    }

    public static void run() {
    	Logger.getLogger(TAG).info("NanoHTTPD 1.25 (C) 2001,2005-2011 Jarno Elonen and (C) 2010 Konstantinos Togias");

        // Defaults
        int port = 8080;
        String host = "127.0.0.1";
        File wwwroot = new File(Configuration.getProperty("outputDir")+Platform.FILE_SEPARATOR+"benchmark").getAbsoluteFile();
        server = new WebServer(host, port, wwwroot);
        try {
			server.start();
			Logger.getLogger(TAG).info("server running");
		} catch (IOException e) {
			Logger.getLogger(TAG).info("server not running");
		}
    }

	public static void kill() {
			server.stop();
	}
}