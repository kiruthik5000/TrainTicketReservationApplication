package dto;

public class PassengerRequestDto {
    private String name;
    private int age;

    public PassengerRequestDto(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
