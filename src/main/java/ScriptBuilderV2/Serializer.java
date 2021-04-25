package ScriptBuilderV2;

import com.google.gson.Gson;

import java.util.List;

public class Serializer {
    public String serializeToArrayWithData(List data) {
        Gson gson = new Gson();
        return gson.toJson(data)
                .replace("[", "")
                .replace("{\"_first\":", "new Data(")
                .replace(",\"_second\":", ", ")
                .replace("\"_third\":", " ")
                .replace("},", "),\n")
                .replace("}", ")\n")
                .replace("]", "");
    }
}
