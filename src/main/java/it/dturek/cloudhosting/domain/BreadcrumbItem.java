package it.dturek.cloudhosting.domain;

public class BreadcrumbItem {

    private Long id;
    private String name;

    public BreadcrumbItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BreadcrumbItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
