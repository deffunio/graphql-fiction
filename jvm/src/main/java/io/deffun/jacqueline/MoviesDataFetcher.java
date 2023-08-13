package io.deffun.jacqueline;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class MoviesDataFetcher implements DataFetcher<Iterable<Movie>> {
    private static final List<Movie> MOVIES = List.of(
            new Movie("Reservoir Dogs", 1992, "...", "Quentin Tarantino"),
            new Movie("True Romance", 1993, "...", "Tony Scott"),
            new Movie("Pulp Fiction", 1994, "...", "Quentin Tarantino")
    );

    @Override
    public Iterable<Movie> get(DataFetchingEnvironment environment)
            throws Exception {
        return MOVIES;
    }
}
