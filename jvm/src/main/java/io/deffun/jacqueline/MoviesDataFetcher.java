package io.deffun.jacqueline;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class MoviesDataFetcher implements DataFetcher<Iterable<Movie>> {
    @Override
    public Iterable<Movie> get(DataFetchingEnvironment environment)
            throws Exception {
        return List.of(
                new Movie("Reservoir Dogs", 1992, "..."),
                new Movie("Pulp Fiction", 1994, "...")
        );
    }
}
