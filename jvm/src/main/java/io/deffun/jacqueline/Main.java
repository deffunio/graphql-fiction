package io.deffun.jacqueline;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;

public class Main {
    private static final String SCHEMA = """
            type Movie {
                title: String!
                released: Int
                tagline: String
                directedBy: Director
            }
            scalar Date
            type Director {
                name: String!
                born: Date
            }
            type Query {
                movies: [Movie!]
            }
            """;

    public static void main(String[] args) {
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(SCHEMA);
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.Date)
                .type("Query", builder -> builder
                        .dataFetcher("movies", new MoviesDataFetcher()))
                .type("Movie", builder -> builder
                        .dataFetcher("directedBy", new DirectedByDataFetcher()))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator
                .makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register("directedByDataLoader",
                DataLoaderFactory.newDataLoader(new DirectedByDataLoader()));
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("{ movies { directedBy { name } } }")
                .dataLoaderRegistry(dataLoaderRegistry)
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);
        System.out.println(executionResult.toSpecification());
    }
}
