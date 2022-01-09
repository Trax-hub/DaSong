package com.example.pts3.model;

import java.net.URL;

public class Search {

    private final int id;
    private final boolean readable;
    private final String title;
    private final String title_short;
    private final String title_version;
    private final String link;
    private final int duration;
    private final int rank;
    private final boolean explicit_lyrics;
    private final String preview;
    private final Artist artist;
    private final Album album;
    private String type;

    public Search(int id, boolean readable, String title, String title_short, String title_version, String link, int duration, int rank, boolean explicit_lyrics, String preview, Artist artist, Album album) {
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

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    public class Album{
        private final int id;
        private final String title;
        private final String cover;
        private final String cover_small;
        private final String cover_medium;
        private final String cover_big;
        private final String cover_xl;

        public Album(int id, String title, String cover, String cover_small, String cover_medium, String cover_big, String cover_xl) {
            this.id = id;
            this.title = title;
            this.cover = cover;
            this.cover_small = cover_small;
            this.cover_medium = cover_medium;
            this.cover_big = cover_big;
            this.cover_xl = cover_xl;
        }

        public int getId() {
            return id;
        }

        public String getCover() {
            return cover;
        }

        public String getCover_big() {
            return cover_big;
        }
    }

    public class Artist{
        private final int id;
        private final String name;
        private final URL link;
        private final URL picture;
        private final URL picture_small;
        private final URL picture_medium;
        private final URL picture_big;
        private final URL picture_xl;

        public Artist(int id, String name, URL link, URL picture, URL picture_small, URL picture_medium, URL picture_big, URL picture_xl) {
            this.id = id;
            this.name = name;
            this.link = link;
            this.picture = picture;
            this.picture_small = picture_small;
            this.picture_medium = picture_medium;
            this.picture_big = picture_big;
            this.picture_xl = picture_xl;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}


