package com.example.graphql.scalar;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DgsComponent
public class DateTimeScalar {

    @DgsRuntimeWiring
    public RuntimeWiring.Builder addScalar(RuntimeWiring.Builder builder) {
        return builder.scalar(GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("DateTime scalar")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingSerializeException("Expected LocalDateTime");
                }

                @Override
                public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingParseValueException("Expected String");
                }

                @Override
                public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    throw new CoercingParseLiteralException("Expected String");
                }
            })
            .build());
    }
}