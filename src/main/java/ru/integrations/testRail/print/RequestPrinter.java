package ru.integrations.testRail.print;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.internal.NoParameterValue;
import io.restassured.internal.support.Prettifier;
import io.restassured.parsing.Parser;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.ProxySpecification;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestPrinter {
    private static final String TAB = "\t";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String EQUALS = "=";
    private static final String NONE = "<none>";

    public RequestPrinter() {
    }

    public static String print(FilterableRequestSpecification requestSpec, String requestMethod, String completeRequestUri, LogDetail logDetail, boolean shouldPrettyPrint) {
        StringBuilder builder = new StringBuilder();
        if (logDetail == LogDetail.ALL || logDetail == LogDetail.METHOD) {
            addSingle(builder, "Request method:", requestMethod);
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.URI) {
            addSingle(builder, "Request URI:", completeRequestUri);
        }

        if (logDetail == LogDetail.ALL) {
            addProxy(requestSpec, builder);
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.PARAMS) {
            addMapDetails(builder, "Request params:", requestSpec.getRequestParams());
            addMapDetails(builder, "Query params:", requestSpec.getQueryParams());
            addMapDetails(builder, "Form params:", requestSpec.getFormParams());
            addMapDetails(builder, "Path params:", requestSpec.getNamedPathParams());
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.HEADERS) {
            addHeaders(requestSpec, builder);
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.COOKIES) {
            addCookies(requestSpec, builder);
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.PARAMS) {
            addMultiParts(requestSpec, builder);
        }

        if (logDetail == LogDetail.ALL || logDetail == LogDetail.BODY) {
            addBody(requestSpec, builder, shouldPrettyPrint);
        }

        String logString = builder.toString();
        if (logString.endsWith("\n")) {
            logString = StringUtils.removeEnd(logString, "\n");
        }
        return logString;
    }

    private static void addProxy(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Proxy:");
        ProxySpecification proxySpec = requestSpec.getProxySpecification();
        appendThreeTabs(builder);
        if (proxySpec == null) {
            builder.append(NONE);
        } else {
            builder.append(proxySpec.toString());
        }

        builder.append(NEW_LINE);
    }

    private static void addBody(FilterableRequestSpecification requestSpec, StringBuilder builder, boolean shouldPrettyPrint) {
        builder.append("Body:");
        if (requestSpec.getBody() != null) {
            String body;
            if (shouldPrettyPrint) {
                body = (new Prettifier()).getPrettifiedBodyIfPossible(requestSpec);
            } else {
                body = requestSpec.getBody();
            }

            builder.append(NEW_LINE).append(body);
        } else {
            appendTab(appendTwoTabs(builder)).append(NONE);
        }

    }

    private static void addCookies(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Cookies:");
        Cookies cookies = requestSpec.getCookies();
        if (!cookies.exist()) {
            appendTwoTabs(builder).append(NONE).append(NEW_LINE);
        }

        int i = 0;

        Cookie cookie;
        for (Iterator var4 = cookies.iterator(); var4.hasNext(); builder.append(cookie).append(NEW_LINE)) {
            cookie = (Cookie) var4.next();
            if (i++ == 0) {
                appendTwoTabs(builder);
            } else {
                appendFourTabs(builder);
            }
        }

    }

    private static void addHeaders(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Headers:");
        Headers headers = requestSpec.getHeaders();
        if (!headers.exist()) {
            appendTwoTabs(builder).append(NONE).append(NEW_LINE);
        } else {
            int i = 0;

            Header header;
            for (Iterator var4 = headers.iterator(); var4.hasNext(); builder.append(header).append(NEW_LINE)) {
                header = (Header) var4.next();
                if (i++ == 0) {
                    appendTwoTabs(builder);
                } else {
                    appendFourTabs(builder);
                }
            }
        }

    }

    private static void addMultiParts(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Multiparts:");
        List<MultiPartSpecification> multiParts = requestSpec.getMultiPartParams();
        if (multiParts.isEmpty()) {
            appendTwoTabs(builder).append(NONE).append(NEW_LINE);
        } else {
            for (int i = 0; i < multiParts.size(); ++i) {
                MultiPartSpecification multiPart = multiParts.get(i);
                if (i == 0) {
                    appendTwoTabs(builder);
                } else {
                    appendFourTabs(builder.append(NEW_LINE));
                }

                builder.append("------------");
                appendFourTabs(appendFourTabs(builder.append(NEW_LINE))
                        .append("Content-Disposition: ")
                        .append(requestSpec.getContentType().replace("multipart/", ""))
                        .append("; name = ")
                        .append(multiPart.getControlName())
                        .append(multiPart.hasFileName() ? "; filename = " + multiPart.getFileName() : "")
                        .append(NEW_LINE))
                        .append("Content-Type: ")
                        .append(multiPart.getMimeType());
                if (multiPart.getContent() instanceof InputStream) {
                    appendFourTabs(builder.append(NEW_LINE)).append("<inputstream>");
                } else {
                    Parser parser = Parser.fromContentType(multiPart.getMimeType());
                    String prettified = (new Prettifier()).prettify(multiPart.getContent().toString(), parser);
                    String prettifiedIndented = StringUtils.replace(prettified,
                            NEW_LINE, NEW_LINE + TAB + TAB + TAB + TAB);
                    appendFourTabs(builder.append(NEW_LINE)).append(prettifiedIndented);
                }
            }

            builder.append(NEW_LINE);
        }

    }

    private static void addSingle(StringBuilder builder, String str, String requestPath) {
        appendTab(builder.append(str)).append(requestPath).append(NEW_LINE);
    }

    private static void addMapDetails(StringBuilder builder, String title, Map<String, ?> map) {
        appendTab(builder.append(title));
        if (map.isEmpty()) {
            builder.append(NONE).append(NEW_LINE);
        } else {
            int i = 0;

            for (Iterator var4 = map.entrySet().iterator(); var4.hasNext(); builder.append(NEW_LINE)) {
                Map.Entry entry = (Map.Entry) var4.next();
                if (i++ != 0) {
                    appendFourTabs(builder);
                }

                Object value = entry.getValue();
                builder.append(entry.getKey());
                if (!(value instanceof NoParameterValue)) {
                    builder.append(EQUALS).append(value);
                }
            }
        }

    }

    private static StringBuilder appendFourTabs(StringBuilder builder) {
        appendTwoTabs(appendTwoTabs(builder));
        return builder;
    }

    private static StringBuilder appendTwoTabs(StringBuilder builder) {
        appendTab(appendTab(builder));
        return builder;
    }

    private static StringBuilder appendThreeTabs(StringBuilder builder) {
        appendTwoTabs(appendTab(builder));
        return builder;
    }

    private static StringBuilder appendTab(StringBuilder builder) {
        return builder.append(TAB);
    }
}