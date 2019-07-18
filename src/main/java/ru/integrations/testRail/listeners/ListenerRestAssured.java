package ru.integrations.testRail.listeners;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import ru.integrations.testRail.print.RequestPrinter;
import ru.integrations.testRail.print.ResponsePrinter;


public class ListenerRestAssured implements OrderedFilter {
    private static String requestFil = null;
    private static String responseFil = null;

    public static String getRequestFil() {
        return requestFil;
    }

    public static String getResponseFil() {
        return responseFil;
    }

    @Override
    public Response filter(final FilterableRequestSpecification requestSpec,
                           final FilterableResponseSpecification responseSpec,
                           final FilterContext filterContext) {

        requestFil = RequestPrinter.print(requestSpec, requestSpec.getMethod(), requestSpec.getURI(),
                LogDetail.ALL, true);
        final Response response = filterContext.next(requestSpec, responseSpec);
        responseFil = ResponsePrinter.print(response, response, LogDetail.ALL, true);
        return response;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
