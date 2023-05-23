package without.TewBot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "WordsAndTranslate")
@NoArgsConstructor@AllArgsConstructor
@Getter
@Setter
public class Words {
    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    //@Column(name = "EnglishWords")
    private String EnglishWords;
    //@Column(name = "UkrainianWords")
    private String UkrainianWords;

}
