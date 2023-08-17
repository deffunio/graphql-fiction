package io.deffun.jacqueline;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.SchemaTransformer;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.deffun.snatch.core.GraphQLDirectiveVisitor;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderRegistry;

public class Main {
    private static final String SCHEMA = """
            input Header {
              name: String!, value: String!
            }
            input Renames {
              operationName: String!
              typePrefix: String!
            }
            directive @graphql(
              renames: Renames!
              endpoint: String!
              headers: [Header!]
            ) repeatable on OBJECT
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
            type Query
            @graphql(
              renames: {
                operationName: "rickAndMorty"
                typePrefix: "RM"
              }
              endpoint: "https://rickandmortyapi.com/graphql"
            ) {
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
        GraphQLSchema transformed = new SchemaTransformer().transform(graphQLSchema, new GraphQLDirectiveVisitor());
        GraphQL graphQL = GraphQL.newGraphQL(transformed).build();

        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register("directedByDataLoader",
                DataLoaderFactory.newDataLoader(new DirectedByDataLoader()));
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query("{ movies { directedBy { name } } }")
                .dataLoaderRegistry(dataLoaderRegistry)
                .build();

        ExecutionResult executionResult = graphQL.execute(executionInput);
        System.out.println(executionResult.toSpecification());

        ExecutionInput execInput = ExecutionInput.newExecutionInput()
                .query("{ rickAndMorty { charactersByIds(ids: [1, 2]) { name } } }")
                .build();
        ExecutionResult execResult = graphQL.execute(execInput);
        System.out.println(execResult.toSpecification());
    }
}
