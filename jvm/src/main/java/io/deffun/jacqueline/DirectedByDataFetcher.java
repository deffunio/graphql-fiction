package io.deffun.jacqueline;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;

import java.util.concurrent.CompletableFuture;

public class DirectedByDataFetcher implements DataFetcher<CompletableFuture<Director>> {
    @Override
    public CompletableFuture<Director> get(DataFetchingEnvironment environment)
            throws Exception {
        Movie movie = environment.getSource();
        String directedBy = movie.directedBy();
        DataLoader<String, Director> dataLoader = environment
                .getDataLoader("directedByDataLoader");
        return dataLoader.load(directedBy);
    }
}
