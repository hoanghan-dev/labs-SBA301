package fu.se.chapter15api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import fu.se.chapter15api.entities.Orchid;

public interface IOrchidRepository extends JpaRepository<Orchid, Integer> {
}
