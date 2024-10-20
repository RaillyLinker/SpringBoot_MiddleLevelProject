package raillylinker.module_api_service_v1.filters;

import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

// [API 별 Request / Response 로깅 필터]
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {
    private final Logger classLogger = LoggerFactory.getLogger(this.getClass());

    // 로깅 body 에 표시할 데이터 타입
    private final List<MediaType> visibleTypeList = List.of(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LocalDateTime requestTime = LocalDateTime.now();

        // 요청자 Ip (ex : 127.0.0.1)
        String clientAddressIp = request.getRemoteAddr();

        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper httpServletRequest = request instanceof ContentCachingRequestWrapper ?
                (ContentCachingRequestWrapper) request :
                new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper httpServletResponse = response instanceof ContentCachingResponseWrapper ?
                (ContentCachingResponseWrapper) response :
                new ContentCachingResponseWrapper(response);

        boolean isError = false;
        try {
            if ("text/event-stream".equals(httpServletRequest.getHeader("accept"))) {
                filterChain.doFilter(httpServletRequest, response);
            } else {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
        } catch (Exception e) {
            if (!(e instanceof ServletException && e.getCause() instanceof AccessDeniedException)) {
                isError = true;
            }
            throw e;
        } finally {
            String queryString = httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "";
            String endpoint = httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + queryString;

            String requestBody = httpServletRequest.getContentAsByteArray().length > 0 ?
                    getContentByte(httpServletRequest.getContentAsByteArray(), httpServletRequest.getContentType()) : "";

            String responseBody = httpServletResponse.getContentAsByteArray().length > 0 ?
                    getContentByte(httpServletResponse.getContentAsByteArray(), httpServletResponse.getContentType()) : "";

            HttpStatus responseStatus = HttpStatus.resolve(httpServletResponse.getStatus());
            String responseStatusPhrase = responseStatus != null ? responseStatus.getReasonPhrase() : "";

            long duration = Duration.between(requestTime, LocalDateTime.now()).toMillis();

            String loggingStart = ">>ApiFilterLog>>";
            if (isError) {
                classLogger.error(String.format(
                        "%s\nrequestTime : %s\nendPoint : %s\nclient Ip : %s\nrequest Body : %s\n" +
                                "response Status : %s %s\nprocessing duration(ms) : %d\nresponse Body : %s\n",
                        loggingStart, requestTime, endpoint, clientAddressIp, requestBody,
                        httpServletResponse.getStatus(), responseStatusPhrase, duration, responseBody
                ));
            } else {
                classLogger.info(String.format(
                        "%s\nrequestTime : %s\nendPoint : %s\nclient Ip : %s\nrequest Body : %s\n" +
                                "response Status : %s %s\nprocessing duration(ms) : %d\nresponse Body : %s\n",
                        loggingStart, requestTime, endpoint, clientAddressIp, requestBody,
                        httpServletResponse.getStatus(), responseStatusPhrase, duration, responseBody
                ));
            }

            if (httpServletRequest.isAsyncStarted()) {
                httpServletRequest.getAsyncContext().addListener(new AsyncListener() {
                    @Override
                    public void onComplete(AsyncEvent event) throws IOException {
                        httpServletResponse.copyBodyToResponse();
                    }

                    @Override
                    public void onTimeout(AsyncEvent event) {
                    }

                    @Override
                    public void onError(AsyncEvent event) {
                    }

                    @Override
                    public void onStartAsync(AsyncEvent event) {
                    }
                });
            } else {
                httpServletResponse.copyBodyToResponse();
            }
        }
    }

    private String getContentByte(byte[] content, String contentType) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = visibleTypeList.stream().anyMatch(visibleType -> visibleType.includes(mediaType));

        if (visible) {
            try {
                return new String(content, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return content.length + " bytes content";
            }
        } else {
            return content.length + " bytes content";
        }
    }
}