package ru.integrations.testRail.print;

import io.restassured.filter.log.LogDetail;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.ResponseBody;
import io.restassured.response.ResponseOptions;
import org.apache.commons.lang3.StringUtils;

public class ResponsePrinter {
    private static final String HEADER_NAME_AND_VALUE_SEPARATOR = ": ";

    public ResponsePrinter() {
    }

    public static String print(ResponseOptions responseOptions, ResponseBody responseBody,
                               LogDetail logDetail, boolean shouldPrettyPrint) {
        StringBuilder builder = new StringBuilder();
        if (logDetail == LogDetail.ALL || logDetail == LogDetail.STATUS) {
            builder.append(responseOptions.statusLine());
        }

        if (logDetail != LogDetail.ALL && logDetail != LogDetail.HEADERS) {
            if (logDetail == LogDetail.COOKIES) {
                Cookies cookies = responseOptions.detailedCookies();
                if (cookies.exist()) {
                    appendNewLineIfAll(logDetail, builder).append(cookies.toString());
                }
            }
        } else {
            Headers headers = responseOptions.headers();
            if (headers.exist()) {
                appendNewLineIfAll(logDetail, builder).append(toString(headers));
            }
        }

        String responseBodyToAppend;
        if (logDetail == LogDetail.ALL || logDetail == LogDetail.BODY) {
            if (shouldPrettyPrint) {
                responseBodyToAppend = (new Prettifier()).getPrettifiedBodyIfPossible(responseOptions, responseBody);
            } else {
                responseBodyToAppend = responseBody.asString();
            }

            if (logDetail == LogDetail.ALL && !StringUtils.isBlank(responseBodyToAppend)) {
                builder.append("\n\n");
            }

            builder.append(responseBodyToAppend);
        }

        responseBodyToAppend = builder.toString();
        return responseBodyToAppend;
    }

    private static String toString(Headers headers) {
        if (!headers.exist()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();

            for (Header header : headers) {
                builder.append(header.getName()).append(HEADER_NAME_AND_VALUE_SEPARATOR).append(header.getValue()).append("\n");
            }

            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }
    }

    private static StringBuilder appendNewLineIfAll(LogDetail logDetail, StringBuilder builder) {
        if (logDetail == LogDetail.ALL) {
            builder.append("\n");
        }

        return builder;
    }
}

