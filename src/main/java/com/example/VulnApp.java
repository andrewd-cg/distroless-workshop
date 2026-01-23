package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@RestController
public class VulnApp {

    public static void main(String[] args) {
        SpringApplication.run(VulnApp.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Vulnerable Spring Cloud Function Demo - CVE-2022-22963";
    }

    @GetMapping("/result")
    public String result(@RequestParam(required = false) String file) {
        try {
            if (file != null && !file.isEmpty()) {
                // Display specific file contents
                String content = new String(Files.readAllBytes(Paths.get("/tmp/" + file)));
                return "<html><head><title>File Contents</title></head><body>"
                    + "<a href='/result'>← Back to /tmp listing</a>"
                    + "<h1>Contents of /tmp/" + file + ":</h1><pre>" + content + "</pre></body></html>";
            } else {
                // Display directory listing
                File tmpDir = new File("/tmp");
                File[] files = tmpDir.listFiles();
                StringBuilder html = new StringBuilder("<html><head><title>/tmp Directory</title></head><body>"
                    + "<h1>/tmp Directory Listing:</h1><ul>");

                if (files != null && files.length > 0) {
                    for (File f : files) {
                        if (f.isFile()) {
                            html.append("<li><a href='/result?file=").append(f.getName()).append("'>")
                                .append(f.getName()).append("</a></li>");
                        }
                    }
                } else {
                    html.append("<li>No files yet. Run an exploit to create files in /tmp.</li>");
                }

                html.append("</ul></body></html>");
                return html.toString();
            }
        } catch (Exception e) {
            return "<html><head><title>Error</title></head><body><h1>Error:</h1><pre>"
                + e.getMessage() + "</pre></body></html>";
        }
    }
}
