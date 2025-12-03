public class Patient {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String address;
    private String bloodtype;

    public Patient(String id, String name, int age, String gender, String contact, String address, String bloodtype) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
        this.bloodtype = bloodtype;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }
    public String getBloodType() { return bloodtype; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContact(String contact) { this.contact = contact; }
    public void setAddress(String address) { this.address = address; }
    public void setBloodType(String bloodType) { this.bloodtype = bloodType; }
}
