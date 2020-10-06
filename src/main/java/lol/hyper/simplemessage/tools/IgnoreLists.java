package lol.hyper.simplemessage.tools;

import lol.hyper.simplemessage.SimpleMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class IgnoreLists {

    public static ArrayList<UUID> get(UUID player) throws IOException, ParseException {
        ArrayList<UUID> list = new ArrayList<>();

        File ignoredList = new File(SimpleMessage.getInstance().ignoreLists.toFile(), player.toString() + ".json");

        if (!ignoredList.exists()) {
            return null;
        } else {
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(ignoredList);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            reader.close();
            JSONArray partyMembers = (JSONArray) jsonObject.get("ignored");
            for (String partyMember : (Iterable<String>) partyMembers) {
                list.add(UUID.fromString(partyMember));
            }
            return list;
        }
    }
}
