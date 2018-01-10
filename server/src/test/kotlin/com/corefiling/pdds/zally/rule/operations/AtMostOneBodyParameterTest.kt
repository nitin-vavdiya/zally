package com.corefiling.pdds.zally.rule.operations

import io.swagger.models.Swagger
import io.swagger.parser.SwaggerParser
import org.assertj.core.api.Assertions
import org.junit.Test

class AtMostOneBodyParameterTest {

    val cut = AtMostOneBodyParameter()

    @Test
    fun withEmptyReturnsNull() {
        Assertions.assertThat(cut.validate(Swagger())).isNull()
    }

    @Test
    fun withOneBodyParameterReturnsNull() {
        val yaml = """
            swagger: '2.0'
            info:
              title: API Title
              version: 1.0.0
            paths:
              '/things':
                post:
                  parameters:
                    - in: body
                      name: thing
                      required: true
                      schema:
                        type: string
                  responses:
                    200:
                      description: Done
            """.trimIndent()
        Assertions.assertThat(cut.validate(SwaggerParser().parse(yaml))).isNull()
    }

    @Test
    fun withTwoBodyParameterReturnsViolation() {
        val yaml = """
            swagger: '2.0'
            info:
              title: API Title
              version: 1.0.0
            paths:
              '/things':
                post:
                  parameters:
                    - in: body
                      name: thing
                      required: true
                      schema:
                        type: string
                    - in: body
                      name: anotherThing
                      required: true
                      schema:
                        type: string
                  responses:
                    200:
                      description: Done
            """.trimIndent()

        Assertions.assertThat(cut.validate(SwaggerParser().parse(yaml))!!.paths)
                .hasSameElementsAs(listOf("/things POST has multiple body parameters [thing, anotherThing]"))
    }
}