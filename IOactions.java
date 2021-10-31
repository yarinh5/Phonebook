import java.io.*;
import java.util.HashMap;
import java.util.Map;

class IOactions {
    IOactions() {

    }

    Map<String, String> Import(String filePath) throws Error, IOException {
        Map<String, String> map = new HashMap<>();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ", 3);
            if (parts.length >= 3) {
                String key = String.format("%s %s", parts[0], parts[1]);
                String value = parts[2];
                if (!value.matches("[0-9]+")) {
                    throw new Error("imported file not supported");
                }
                map.put(key, value);
            } else {
                throw new Error("imported file not supported");
            }
        }
        reader.close();
        return map;
    }

    void Export(Map<String, String> map,String saveAs) throws IOException {
        FileWriter fstream;
        BufferedWriter out;
        fstream = new FileWriter(saveAs +".txt");
        out = new BufferedWriter(fstream);
        for (Map.Entry<String, String> pairs : map.entrySet()) {
            out.write(pairs.getKey() + " ");
            out.write(pairs.getValue() + "\n");
        }
        out.close();
    }
}
