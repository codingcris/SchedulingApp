package edu.wgu.cristianreyes.application.customer;

/**
* Represents a customer.
*/
public class Customer {
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int id;
    private int divisionId;
    private String firstLevelDiv;

    /**
     * Constructor initializes all fields in the Customer instance.
     * @param id The customer ID.
     * @param name the customer name.
     * @param address the customer address.
     * @param postalCode the customer's postal code.
     * @param phone the customer's phone number.
     * @param divisionId the customer's first level division ID.
     */
    public Customer(int id, String name, String address, String postalCode, String phone, int divisionId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    /**
     * Constructor initializes all fields except for the id field in the Customer instance.
     * @param name the customer name.
     * @param address the customer address.
     * @param postalCode the customer's postal code.
     * @param phone the customer's phone number.
     * @param divisionId the customer's first level division ID.
     */
    public Customer(String name, String address, String postalCode, String phone, int divisionId) {
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    /**
     * Setter method for id field.
     * @param id new value of id field.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter method for name field.
     * @param name new value of name field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter method for address field.
     * @param address new value of address field.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Setter method for postalCode field.
     * @param postalCode new value postalCode of field.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Setter method phone for field.
     * @param phone new value of phone field.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Setter method for divisionId field.
     * @param divisionId new value of field.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Getter method for id field.
     * @return customer id.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter method for postalCode field.
     * @return customer postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Getter method for phone field.
     * @return customer phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Getter method for name field.
     * @return customer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for address field.
     * @return customer address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Getter method for divisionId field.
     * @return customer divisionId.
     */
    public int getDivisionId() {
        return divisionId;
    }
}
