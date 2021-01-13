package net.marvk.fs.vatsim.map.data;

import lombok.Value;
import net.marvk.fs.vatsim.api.data.VatsimControllerRating;

import java.util.HashMap;
import java.util.Map;

@Value
public class ControllerRating implements Comparable<ControllerRating> {
    private static final Map<Integer, ControllerRating> RATINGS = new HashMap<>();

    int id;
    String shortName;
    String longName;

    public static ControllerRating of(final int id, final String shortName, final String longName) {
        return RATINGS.computeIfAbsent(id, s -> new ControllerRating(id, shortName, longName));
    }

    public static ControllerRating of(final VatsimControllerRating rating) {
        return of(Integer.parseInt(rating.getId()), rating.getShortName(), rating.getLongName());
    }

    public static ControllerRating[] values() {
        return RATINGS.values().toArray(ControllerRating[]::new);
    }

    @Override
    public int compareTo(final ControllerRating o) {
        return Integer.compare(id, o.id);
    }
}
