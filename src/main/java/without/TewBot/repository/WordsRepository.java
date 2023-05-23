package without.TewBot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import without.TewBot.model.Words;

@Repository
public interface WordsRepository extends CrudRepository<Words, Long> {
    Words findByIdAllIgnoreCase(@NonNull Long id);

    @Override
    boolean existsById(Long aLong);
}
