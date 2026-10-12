package fu.se.chappter13orchid.services;

import fu.se.chappter13orchid.entities.Orchid;
import fu.se.chappter13orchid.repositories.OrchidRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrchidService implements IOrchidService {

    private final OrchidRepository orchidRepository;

    public OrchidService(OrchidRepository orchidRepository) {
        this.orchidRepository = orchidRepository;
    }

    @Override
    public List<Orchid> getAllOrchids() {
        return orchidRepository.findAll();
    }

    @Override
    public Orchid insertOrchid(Orchid orchid) {
        return orchidRepository.save(orchid);
    }

    @Override
    public Orchid updateOrchid(int orchidID, Orchid orchid) {
        return orchidRepository.findById(orchidID)
                .map(existingOrchid -> {
                    existingOrchid.setOrchidName(orchid.getOrchidName());
                    existingOrchid.setNatural(orchid.isNatural());
                    existingOrchid.setOrchidDescription(orchid.getOrchidDescription());
                    existingOrchid.setOrchidCategory(orchid.getOrchidCategory());
                    existingOrchid.setAttractive(orchid.isAttractive());
                    existingOrchid.setOrchidURL(orchid.getOrchidURL());
                    return orchidRepository.save(existingOrchid);
                })
                .orElse(null);
    }

    @Override
    public void deleteOrchid(int orchid) {
        orchidRepository.deleteById(orchid);
    }

    @Override
    public Optional<Orchid> getOrchidByID(int orchidID) {
        return orchidRepository.findById(orchidID);
    }
}
