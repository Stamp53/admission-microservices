package com.example.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/admission")
    public ResponseEntity<String> admissionFallback() {
        String htmlResponse = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Service Unavailable - Circuit Breaker</title>
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f8fafc; color: #1e293b; display: flex; align-items: center; justify-content: center; height: 100vh; margin: 0; }
                    .card { background: white; border: 1px solid #e2e8f0; border-radius: 12px; padding: 2.5rem; max-width: 500px; text-align: center; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); }
                    .badge { display: inline-block; background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; padding: 0.35rem 0.85rem; border-radius: 9999px; font-weight: 700; font-size: 0.8rem; margin-bottom: 1rem; }
                    h1 { font-size: 1.5rem; margin-bottom: 0.5rem; color: #0f172a; }
                    p { color: #64748b; font-size: 0.95rem; line-height: 1.5; margin-bottom: 1.5rem; }
                    .btn { display: inline-block; background: #4f46e5; color: white; padding: 0.6rem 1.25rem; border-radius: 6px; text-decoration: none; font-weight: 600; font-size: 0.9rem; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="badge">⚡ Resilience4j Circuit Breaker Active</div>
                    <h1>Admission Service Temporarily Unavailable</h1>
                    <p>The backend Admission Service instance is currently starting up or undergoing maintenance. The API Gateway circuit breaker intercepted the request to maintain system stability.</p>
                    <a href="/" class="btn">Retry Request</a>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }
}
