package it.units.karatepassport;

public enum Rank {
    WHITE(R.id.white_belt_date, "White belt"),
    YELLOW(R.id.yellow_belt_date, "Yellow belt"),
    ORANGE(R.id.orange_belt_date, "Orange belt"),
    GREEN(R.id.green_belt_date, "Green belt"),
    BLUE(R.id.blue_belt_date, "Blue belt"),
    BROWN(R.id.brown_belt_date, "Brow belt"),
    FIRST(R.id.first_dan_date, "First dan"),
    SECOND(R.id.second_dan_date, "Second dan"),
    THIRD(R.id.third_dan_date, "Third dan"),
    FOURTH(R.id.fourth_dan_date, "Fourth dan"),
    FIFTH(R.id.fifth_dan_date, "Fifth dan"),
    SIXTH(R.id.sixth_dan_date, "Sixth dan"),
    SEVENTH(R.id.seventh_dan_date, "Seventh dan"),
    EIGHTH(R.id.eighth_dan_date, "Eighth dan"),
    NINTH(R.id.ninth_dan_date, "Ninth dan"),
    TENTH(R.id.tenth_dan_date, "Tenth dan");

    public final int date;
    public final String name;

    Rank(int date, String name) {
        this.date = date;
        this.name = name;
    }
}
