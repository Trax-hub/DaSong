package com.example.pts3;

public class Search {

    private int id;
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private String link;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private String preview;
    private Artiste artist;
    private Album album;
    private String type;

    public Search(int id, boolean readable, String title, String title_short, String title_version, String link, int duration, int rank, boolean explicit_lyrics, String preview, Artiste artist, Album album) {
        this.id = id;
        this.readable = readable;
        this.title = title;
        this.title_short = title_short;
        this.title_version = title_version;
        this.link = link;
        this.duration = duration;
        this.rank = rank;
        this.explicit_lyrics = explicit_lyrics;
        this.preview = preview;
        this.artist = artist;
        this.album = album;
    }

    @Override
    public String toString() {
        return "Search{" +
                "id=" + id +
                ", readable=" + readable +
                ", title='" + title + '\'' +
                ", title_short='" + title_short + '\'' +
                ", title_version='" + title_version + '\'' +
                ", link=" + link +
                ", duration=" + duration +
                ", rank=" + rank +
                ", explicit_lyrics=" + explicit_lyrics +
                ", preview=" + preview +
                ", artist=" + artist +
                ", album=" + album +
                ", type='" + type + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public boolean isReadable() {
        return readable;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle_short() {
        return title_short;
    }

    public String getTitle_version() {
        return title_version;
    }

    public String getLink() {
        return link;
    }

    public int getDuration() {
        return duration;
    }

    public int getRank() {
        return rank;
    }

    public boolean isExplicit_lyrics() {
        return explicit_lyrics;
    }

    public String getPreview() {
        return preview;
    }

    public Artiste getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    public String getType() {
        return type;
    }
}
