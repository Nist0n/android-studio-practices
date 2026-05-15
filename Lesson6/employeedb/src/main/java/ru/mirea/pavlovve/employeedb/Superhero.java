package ru.mirea.pavlovve.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Superhero {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String heroName;

    public String realName;

    public int powerLevel;
}