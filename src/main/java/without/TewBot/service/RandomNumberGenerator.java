package without.TewBot.service;

import java.util.Random;

public class RandomNumberGenerator {
    public static long randomNumberGenerator(int min, int max) {
        //int min = 1; // Мінімальне значення діапазону
        //int max = 100; // Максимальне значення діапазону
        // Створення об'єкту Random
        Random random = new Random();
        // Генерація випадкового числа в межах заданого діапазону
        int randomNumber = random.nextInt(max - min + 1) + min;
        //long randomNumber = (int) ( Math.random() * 100 );
        // Виведення випадкового числа
        //System.out.println("rand num is = " + randomNumber);
        return randomNumber;
    }
}
