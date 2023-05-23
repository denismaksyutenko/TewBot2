package without.TewBot.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    public List<String> listOfWords = new ArrayList();
    public int rightIndexOfWord = 0;
    public String wordForQuestion;

}
