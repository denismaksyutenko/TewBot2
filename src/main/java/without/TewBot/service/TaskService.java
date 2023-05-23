package without.TewBot.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import without.TewBot.model.MessageResponse;
import without.TewBot.model.TrueOrFalseResponse;
import without.TewBot.model.Words;
import without.TewBot.repository.WordsRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
public class TaskService {

    private Words returnRightWordKey(WordsRepository repository){
        Words words = new Words();
        long tmp = RandomNumberGenerator.randomNumberGenerator(1, 100);
        words.setId(tmp);
        words.setEnglishWords(repository.findByIdAllIgnoreCase(tmp).getEnglishWords());
        words.setUkrainianWords(repository.findByIdAllIgnoreCase(tmp).getUkrainianWords());
        return words;
    }

    public MessageResponse listOfWords(WordsRepository repository, boolean boolValue){
        Words words = returnRightWordKey(repository);
        MessageResponse response = new MessageResponse();
        List<String> tmplist = new ArrayList<>();
        boolean boolenValue = false;
        long randomNumber;

        if(boolValue){
        response.setWordForQuestion(words.getEnglishWords());
        for (int i=0; i<4; i++)
        {
            randomNumber = RandomNumberGenerator.randomNumberGenerator(1,100);
            if (!boolenValue && (randomNumber % 3 == 0)){
                tmplist.add(words.getUkrainianWords());
                response.setRightIndexOfWord(i);
                boolenValue = true;
            }
            else if (!boolenValue && (i==3)){
                tmplist.add(words.getUkrainianWords());
                response.setRightIndexOfWord(i);
            }
            else
                tmplist.add(repository.findByIdAllIgnoreCase(randomNumber).getUkrainianWords());
        }}
        else {
            response.setWordForQuestion(words.getUkrainianWords());
        for (int i=0; i<4; i++)
        {
            randomNumber = RandomNumberGenerator.randomNumberGenerator(1,100);
            if (!boolenValue && (randomNumber % 3 == 0)){
                tmplist.add(words.getEnglishWords());
                response.setRightIndexOfWord(i);
                boolenValue = true;
            }
            else if (!boolenValue && (i==3)){
                tmplist.add(words.getEnglishWords());
                response.setRightIndexOfWord(i);
            }
            else
                tmplist.add(repository.findByIdAllIgnoreCase(randomNumber).getEnglishWords());
        }}

        response.setListOfWords(tmplist);
        return response;
    }

    public TrueOrFalseResponse TrueOrFalseTask(WordsRepository repository){
        TrueOrFalseResponse response = new TrueOrFalseResponse();
        long tmp = RandomNumberGenerator.randomNumberGenerator(1, 100);
        response.setFirstWord(repository.findByIdAllIgnoreCase(tmp).getEnglishWords());
        response.setValueBoolean(false);
        if (tmp%2==0)
        {
            response.setSecondWord(repository.findByIdAllIgnoreCase(tmp).getUkrainianWords());
            response.setValueBoolean(true);
        }
        else
            response.setSecondWord(repository.findByIdAllIgnoreCase(RandomNumberGenerator.randomNumberGenerator(1,100)).getUkrainianWords());
        return response;
    }
}
