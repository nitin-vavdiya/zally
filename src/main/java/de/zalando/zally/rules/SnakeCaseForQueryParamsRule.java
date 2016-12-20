package de.zalando.zally.rules;

import de.zalando.zally.Violation;
import de.zalando.zally.ViolationType;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lint for snake case for query params
 */
class SnakeCaseForQueryParamsRule implements Rule {

    private static String SNAKE_CASE_PATTERN = "^[a-z]+(?:_[a-z]+)*$";

    @Override
    public List<Violation> validate(Swagger swagger) {
        if (swagger == null) return new ArrayList<>();

        List<Violation> violations = new ArrayList<>();
        swagger.getPaths().forEach((path, pathObject) -> {
            pathObject.getOperationMap().forEach((httpMethod, operation) -> {
                operation.getParameters().forEach(param -> {
                    if (param instanceof QueryParameter) {
                        if (!isSnakeCase(param.getName())) {
                            violations.add(getViolation(param));
                        }
                    }
                });
            });
        });
        return violations;
    }

    private boolean isSnakeCase(String input) {
        return input.matches(SNAKE_CASE_PATTERN);
    }

    private Violation getViolation(Parameter parameter) {
        return new Violation(
            "Must: Use snake_case (never camelCase) for Query Parameters",
            String.format("Query parameter '%s' does not use snake_case.", parameter.getName()),
            ViolationType.MUST,
            "http://zalando.github.io/restful-api-guidelines/naming/Naming.html#must-use-snakecase-never-camelcase-for-query-parameters"
        );
    }
}