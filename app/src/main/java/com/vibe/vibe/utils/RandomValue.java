package com.vibe.vibe.utils;

import com.vibe.vibe.entities.Album;
import com.vibe.vibe.entities.Song;

import java.util.ArrayList;
import java.util.Random;

public class RandomValue {
    public static ArrayList<Album> getRandomValues(ArrayList<Album> originalList, int count) {
        ArrayList<Album> randomList = new ArrayList<>();
        Random random = new Random();

        // Ensure that count is not greater than the size of the original list
        count = Math.min(count, originalList.size());

        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(originalList.size());
            randomList.add(originalList.get(randomIndex));
            originalList.remove(randomIndex); // Ensure no duplicate values
        }

        return randomList;
    }

    public static Song getRandomValuesSong(ArrayList<Song> originalList, int count) {
        ArrayList<Song> randomList = new ArrayList<>();
        Random random = new Random();

        // Ensure that count is not greater than the size of the original list
        count = Math.min(count, originalList.size());

        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(originalList.size());
            randomList.add(originalList.get(randomIndex));
            originalList.remove(randomIndex); // Ensure no duplicate values
        }

        return randomList.get(0);
    }
}
