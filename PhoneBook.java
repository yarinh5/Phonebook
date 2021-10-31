import java.util.HashMap;
import java.util.Map;

public class PhoneBook {
    private Map<String, String> book;

    public PhoneBook() {
        book = new HashMap<>();
    }

    public PhoneBook(Map<String, String> map) {
        book = new HashMap<>(map);
    }

    public void setBook(Map<String, String> book) {
        this.book = book;
    }

    public void AddContact(String telephone, String fname, String lname) throws Error {
        if (fname.equals("") || lname.equals("")) {
            throw new Error("Values can't be empty");
        }
        if (telephone.matches("[0-9]+")) {
            String fullName = String.format("%s %s", fname, lname);
            book.put(fullName, telephone);
        } else {
            throw new Error("Invalid phone number");
        }
    }

    public boolean isExist(String telephone) {
        return this.book.containsKey(telephone);
    }

    public void DeleteContact(String telephone) {
        book.remove(telephone);
    }

    public Map<String, String> getBook() {
        return book;
    }

    public Map<String, String> Search(String searchCriteria) {
        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String, String> entry : this.book.entrySet()) {
            String fname = entry.getKey().split(" ")[0];
            String lname = entry.getKey().split(" ")[1];
            if (fname.startsWith(searchCriteria) || lname.startsWith(searchCriteria)) {
                data.put(entry.getKey(), entry.getValue());
            }
        }
        return data;
    }
}
