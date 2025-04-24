package com.example.demo.swagger;

public class SwaggerResponseExamples {

    public static final String CONVERT_400 = """
        {
          "error": "Input amount must be greater than 0."
        }
        """;

    public static final String CONVERT_404 = """
        {
          "error": "Exchange rate for currency XYZ was not found."
        }
        """;

    public static final String CONVERT_500 = """
        {
          "error": "Unexpected error occurred while processing the request."
        }
        """;

    public static final String HISTORY_400 = """
        {
          "error": "Currency code parameter is missing or invalid."
        }
        """;

    public static final String HISTORY_404 = """
        {
          "error": "No exchange rate history found for currency code XYZ."
        }
        """;

    public static final String HISTORY_500 = """
        {
          "error": "Unexpected error occurred while retrieving exchange rate history."
        }
        """;
}
