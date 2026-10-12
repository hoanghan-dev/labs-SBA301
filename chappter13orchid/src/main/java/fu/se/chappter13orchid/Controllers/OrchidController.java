package fu.se.chappter13orchid.Controllers;

import fu.se.chappter13orchid.entities.Orchid;
import fu.se.chappter13orchid.services.IOrchidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/orchids")
public class OrchidController {
    @Autowired
    private IOrchidService iOrchidService;

    // fetchAll orchids
    @GetMapping("/")
    public ResponseEntity<List<Orchid>> fetchAll() {
        return ResponseEntity.ok(iOrchidService.getAllOrchids());
    }

    // getOById
    @GetMapping("/{id}")
    public ResponseEntity<Orchid> getOById(@PathVariable("id") Integer id) {
        return iOrchidService.getOrchidByID(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //saveOrchid
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Orchid> saveOrchid(@RequestBody Orchid orchid) {
        if (orchid == null) {
            // Handle when orchid is not provided
            return ResponseEntity.badRequest().body(null);
        }
        System.out.println("Received Orchid: " + orchid.toString());
        return ResponseEntity.ok(iOrchidService.insertOrchid(orchid));
    }

    //updateOrchid
    @PutMapping("/{id}")
    public ResponseEntity<Orchid> updateOrchid(@PathVariable int id, @RequestBody Orchid orchid) {
        Orchid updated = iOrchidService.updateOrchid(id, orchid);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    //deleteOrchid
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrchid(@PathVariable int id) {
        if (iOrchidService.getOrchidByID(id).isPresent()) {
            iOrchidService.deleteOrchid(id);
            return ResponseEntity.ok("Orchid deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

}
