package it.dturek.cloudhosting.domain;

public class Sort {

    private String column;
    private String order = "ASC";

    public Sort() {
    }

    public Sort(String column, String order) {
        this.column = column;
        this.order = order;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Sort{" +
                "column='" + column + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
