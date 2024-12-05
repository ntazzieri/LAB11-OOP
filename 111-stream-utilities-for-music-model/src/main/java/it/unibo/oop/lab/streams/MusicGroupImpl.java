package it.unibo.oop.lab.streams;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream()
            .sorted((a, b) -> a.getSongName().compareTo(b.getSongName()))
            .map(Song::getSongName);
    }

    @Override
    public Stream<String> albumNames() {
        return songs.stream()
            .map(s -> s.getAlbumName().orElse(""));
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return albums.keySet().stream()
            .filter(a -> albums.get(a) == year);
    }

    @Override
    public int countSongs(final String albumName) {
        return songs.stream()
            .filter(s -> s.getAlbumName().orElse("").equals(albumName))
            .mapToInt(a -> 1)
            .sum();
    }

    @Override
    public int countSongsInNoAlbum() {
        return songs.stream()
            .filter(s -> !albums.containsKey(s.getAlbumName().orElse("")))
            .mapToInt(s -> 1)
            .sum();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream()
            .filter(s -> s.getAlbumName().orElse("").equals(albumName))
            .mapToDouble(Song::getDuration)
            .average();
    }

    @Override
    public Optional<String> longestSong() {
        return songs.stream()
        .max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration()))
        .map(Song::getSongName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return songs.stream()
            .collect(
                groupingBy(s -> s.getAlbumName().orElse(""),
                summingDouble(Song::getDuration)))
            .entrySet()
            .stream()
            .max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()))
            .map(Map.Entry::getKey);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
