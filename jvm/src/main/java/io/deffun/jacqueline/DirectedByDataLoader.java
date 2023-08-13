package io.deffun.jacqueline;

import org.dataloader.BatchLoader;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DirectedByDataLoader implements BatchLoader<String, Director> {
    private static final List<Director> DIRECTORS = List.of(
            new Director("Quentin Tarantino", LocalDate.of(1963, 3, 27)),
            new Director("Tony Scott", LocalDate.of(1944, 6, 21))
    );

    @Override
    public CompletionStage<List<Director>> load(List<String> names) {
        return CompletableFuture.supplyAsync(() -> performSqlInQuery(names));
    }

    private static List<Director> performSqlInQuery(List<String> names) {
        return DIRECTORS.stream()
                .filter(d -> names.stream().anyMatch(n -> n.equals(d.name())))
                .toList();
    }
}
