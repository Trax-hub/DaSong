package com.example.pts3;

import java.net.URL;

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
    private Artist artist;
    private Album album;
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

    public String getType() {
        return type;
    }

    public class Album{
        public int id;
        public String title;
        public String cover;
        public String cover_small;
        public String cover_medium;
        public String cover_big;
        public String cover_xl;

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

        public String getTitle() {
            return title;
        }

        public String getCover() {
            return cover;
        }

        public String getCoverSmall() {
            return cover_small;
        }

        public String getCover_medium() {
            return cover_medium;
        }

        public String getCover_big() {
            return cover_big;
        }

        public String getCover_xl() {
            return cover_xl;
        }
    }

    public class Artist{
        public int id;
        public String name;
        public URL link;
        public URL picture;
        public URL picture_small;
        public URL picture_medium;
        public URL picture_big;
        public URL picture_xl;

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

        public URL getLink() {
            return link;
        }

        public URL getPicture() {
            return picture;
        }

        public URL getPicture_small() {
            return picture_small;
        }

        public URL getPicture_medium() {
            return picture_medium;
        }

        public URL getPicture_big() {
            return picture_big;
        }

        public URL getPicture_xl() {
            return picture_xl;
        }
    }

}


