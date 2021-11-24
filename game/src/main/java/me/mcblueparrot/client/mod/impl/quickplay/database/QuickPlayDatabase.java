package me.mcblueparrot.client.mod.impl.quickplay.database;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;
import me.mcblueparrot.client.util.Utils;

// Credit to original QuickPlay for database.
public class QuickPlayDatabase {

    @Getter
    private Map<String, QuickPlayGame> games = new LinkedHashMap<>();

    public QuickPlayDatabase() {
        initDatabase();
    }

    public QuickPlayGame getGame(String id) {
        return games.get(id);
    }

    private void initDatabase() {
        try {
            JsonArray array = new JsonParser().parse(Utils.urlToString(Utils.sneakyParse("https://bugg.co/quickplay/mod/gamelist")))
                    .getAsJsonObject().get("content").getAsJsonObject().get("games").getAsJsonArray();
            for(JsonElement gameElement : array) {
                games.put(gameElement.getAsJsonObject().get("unlocalizedName").getAsString(), new QuickPlayGame(gameElement.getAsJsonObject()));
            }
        }
        catch(IOException | JsonSyntaxException error) {
            throw new IllegalStateException(error);
        }
//        addGames(GameType.ARCADE,
//                "Blocking Dead",                "arcade_day_one",
//                "Bounty Hunters",               "arcade_bounty_hunters",
//                "Capture the Wool",             "arcade_pvp_ctw",
//                "Creeper Attack",               "arcade_creeper_defense",
//                "Dragon Wars",                  "arcade_dragon_wars",
//                "Ender Spleef",                 "arcade_ender_spleef",
//                "Farm Hunt",                    "arcade_farm_hunt",
//                "Football",                     "arcade_soccer",
//                "Galaxy Wars",                  "arcade_starwars",
//                "Hide and Seek - Party Pooper", "arcade_hide_and_seek_party_pooper",
//                "Hide and Seek - Prop Hunt",    "arcade_hide_and_seek_prop_hunt",
//                "Hole in the Wall",             "arcade_hole_in_the_wall",
//                "Hypixel Says",                 "arcade_simon_says",
//                "Mini Walls",                   "arcade_mini_walls",
//                "Party Games",                  "arcade_party_games_1",
//                "Pixel Painters",               "arcade_pixel_painters",
//                "Throw Out",                    "arcade_throw_out",
//                "Zombies: Dead End",            "arcade_zombies_dead_end",
//                "Zombies: Bad Blood",           "arcade_zombies_bad_blood",
//                "Zombies: Alien Arcadium",      "arcade_zombies_alien_arcadium",
//                "Halloween Simulator"
//        );
    }
//
//    private void addGames(GameType type, String... commands) {
//        List<Game> finalGames = new ArrayList<>();
//
//        String name = null;
//        for(int i = 0; i < commands.length; i++) {
//            String str = commands[i];
//            if(i % 2 != 0) {
//                name = str;
//            }
//            else {
//                finalGames.add(new Game(name, str));
//            }
//        }
//
//        games.put(type, finalGames);
//    }

}
