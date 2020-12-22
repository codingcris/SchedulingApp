package edu.wgu.cristianreyes.controllers;

import edu.wgu.cristianreyes.application.appointment.AppointmentDaoImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import edu.wgu.cristianreyes.App;
import edu.wgu.cristianreyes.application.appointment.Appointment;
import edu.wgu.cristianreyes.application.contact.Contact;
import edu.wgu.cristianreyes.application.customer.Customer;
import edu.wgu.cristianreyes.application.db.ConnectionPool;
import edu.wgu.cristianreyes.application.customer.CustomerDaoImpl;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Home window controller class.
 */
public class Home {
    private final ResourceBundle resources = ResourceBundle.getBundle("edu.wgu.cristianreyes.bundles.HomeWindowResources");
    private final String dbUrl = "jdbc:mysql://wgudb.ucertify.com/WJ07mxm";
    private final String dbUsername = "U07mxm";
    private final String dbPassword = "53689073251";

    private ConnectionPool dbConnectionPool;
    private CustomerDaoImpl customerDao;
    private AppointmentDaoImpl appointmentDao;
    private ArrayList<Appointment> allAppointments;

    private Map<Integer, String> countries;
    private Map<Integer, String> firstLevelDivs;

    private ZonedDateTime thisWeek, thisMonth, startOfWeekFilter, startOfMonthFilter;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    @FXML
    private Label title, appointmentsShowingLabel, filterByLabel, reportTitle, monthTypeReportLabel, contactInteractionLabel, contactSchedulesLabel;
    @FXML
    private TabPane tabs;
    @FXML
    private Tab customersTab, appointmentsTab, reportsTab;
    @FXML
    private TableView customersTable, appointmentsTable;
    @FXML
    private Button addButton, modifyButton, deleteButton, previousAppointmentsButton, nextAppointmentsButton, generateReportButton;
    @FXML
    private RadioButton monthlyAppointmentsRb, weeklyAppointmentsRb, monthTypeReportRb, contactSchedulesRb, contactInteractionRb;
    @FXML
    private ToggleGroup appointmentsFilter;
    @FXML
    private AnchorPane reportsArea;
    @FXML
    private VBox reportsDisplay;
    @FXML
    private ComboBox<Contact> contactReportSelector;

    /**
     * Controller initialization method. Lambdas within this method make the code adding event listeners to various controls much more concise
     * than had a ChangeListener class been directly implemented.
     */
    public void initialize() {
        dbConnectionPool = ConnectionPool.create(dbUrl, dbUsername, dbPassword, 3);

        try {
            customerDao = new CustomerDaoImpl(dbConnectionPool);
            appointmentDao = new AppointmentDaoImpl(dbConnectionPool);
        } catch (ConnectionPool.ConnectionsUnavailableException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            System.exit(-1);
        }

        countries = customerDao.getCountriesOfBusiness();
        firstLevelDivs = customerDao.getFirstLevelDivisions();

        thisMonth =  LocalDate.now().atStartOfDay(ZoneId.systemDefault()).with(TemporalAdjusters.firstDayOfMonth());
        thisWeek = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        startOfMonthFilter = thisMonth;
        startOfWeekFilter = thisWeek;

        updateCustomersTable();
        initializeCustomersTableColumns();
        customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        customersTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, o, t1) -> {
                modifyButton.setDisable(t1 == null);
                deleteButton.setDisable(t1 == null);
        }));

        updateAppointmentsTable();
        initializeAppointmentsTableColumns();
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, o, t1) -> {
            modifyButton.setDisable(t1 == null);
            deleteButton.setDisable(t1 == null);
        }));

        applyResources();

        tabs.getSelectionModel().selectedItemProperty().addListener(((observableValue, tab, t1) -> {
            updateTabs(t1);
        }));
        updateTabs(tabs.getSelectionModel().getSelectedItem());

        appointmentsFilter = new ToggleGroup();
        monthlyAppointmentsRb.setToggleGroup(appointmentsFilter);
        monthlyAppointmentsRb.setSelected(true);
        weeklyAppointmentsRb.setToggleGroup(appointmentsFilter);

        appointmentsFilter.selectedToggleProperty().addListener(((observableValue, toggle, t1) -> {
            if(t1 == monthlyAppointmentsRb) {
                startOfMonthFilter = thisMonth;
                updateAppointmentsTable();
            } else if (t1 == weeklyAppointmentsRb) {
                startOfWeekFilter = thisWeek;
                updateAppointmentsTable();
            }
        }));

        contactReportSelector.setItems(FXCollections.observableList(appointmentDao.getAllContacts()));

        contactReportSelector.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                if (contact == null) {
                    return null;
                }
                return contact.getName();
            }

            @Override
            public Contact fromString(String s) {
                return null;
            }
        });

        reportsDisplay.setPadding(new Insets(20));

        updateAppointmentsTable();
        displayUpcomingAppointments();

    }

    /**
     * Applies language resources from the ResourceBundle in the resources field to the appropriate labels and conrtols.
     */
    private void applyResources() {
        title.setText(App.resources.getString("appTitle"));
        customersTab.setText(resources.getString("customers"));
        appointmentsTab.setText(resources.getString("appointments"));
        modifyButton.setText(resources.getString("modify"));
        deleteButton.setText(resources.getString("delete"));
        addButton.setText(resources.getString("add"));
        filterByLabel.setText(resources.getString("filterBy"));
        monthlyAppointmentsRb.setText(resources.getString("monthly"));
        weeklyAppointmentsRb.setText(resources.getString("weekly"));
        previousAppointmentsButton.setText("<- " + resources.getString("previous"));
        nextAppointmentsButton.setText(resources.getString("next") + " ->");
        monthTypeReportLabel.setText(resources.getString("monthTypeReport"));
        contactSchedulesLabel.setText(resources.getString("contactSchedules"));
        contactInteractionLabel.setText(resources.getString("contactInteraction"));
        generateReportButton.setText(resources.getString("generateReport"));
    }

    /**
     * Initializes appointmentsTable.
     */
    private void initializeAppointmentsTableColumns() {
        TableColumn<Appointment, Integer> idColumn = new TableColumn<>(resources.getString("id"));
        TableColumn<Appointment, String> titleColumn = new TableColumn<>(resources.getString("title"));
        TableColumn<Appointment, String> descriptionColumn = new TableColumn<>(resources.getString("description"));
        TableColumn<Appointment, String> locationColumn = new TableColumn<>(resources.getString("location"));
        TableColumn<Appointment, String> contactColumn = new TableColumn<>(resources.getString("contact"));
        TableColumn<Appointment, String> typeColumn = new TableColumn<>(resources.getString("type"));
        TableColumn<Appointment, LocalDate> startDateColumn = new TableColumn<>(resources.getString("startDate"));
        TableColumn<Appointment, LocalTime> startTimeColumn = new TableColumn<>(resources.getString("startTime"));
        TableColumn<Appointment, LocalDate> endDateColumn = new TableColumn<>(resources.getString("endDate"));
        TableColumn<Appointment, LocalTime> endTimeColumn = new TableColumn<>(resources.getString("endTime"));
        TableColumn<Appointment, Integer> customerId = new TableColumn<>(resources.getString("customerId"));

        idColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        contactColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Appointment, String> p) {
                return new ReadOnlyObjectWrapper(appointmentDao.getContactName(p.getValue().getContactId()));
            }
        });
        startDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, LocalDate>, ObservableValue<LocalDate>>() {
            @Override
            public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<Appointment, LocalDate> appointmentLocalDateCellDataFeatures) {
                return new ReadOnlyObjectWrapper<>(appointmentLocalDateCellDataFeatures.getValue().getStart().toLocalDate());
            }
        });
        startTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, LocalTime>, ObservableValue<LocalTime>>() {
            @Override
            public ObservableValue<LocalTime> call(TableColumn.CellDataFeatures<Appointment, LocalTime> appointmentLocalTimeCellDataFeatures) {
                return new ReadOnlyObjectWrapper<>(appointmentLocalTimeCellDataFeatures.getValue().getStart().toLocalTime());
            }
        });
        endDateColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, LocalDate>, ObservableValue<LocalDate>>() {
            @Override
            public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<Appointment, LocalDate> appointmentLocalDateCellDataFeatures) {
                return new ReadOnlyObjectWrapper<>(appointmentLocalDateCellDataFeatures.getValue().getEnd().toLocalDate());
            }
        });
        endTimeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Appointment, LocalTime>, ObservableValue<LocalTime>>() {
            @Override
            public ObservableValue<LocalTime> call(TableColumn.CellDataFeatures<Appointment, LocalTime> appointmentLocalTimeCellDataFeatures) {
                return new ReadOnlyObjectWrapper<>(appointmentLocalTimeCellDataFeatures.getValue().getEnd().toLocalTime());
            }
        });
        typeColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        customerId.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerId"));

        appointmentsTable.getColumns().addAll(idColumn, titleColumn, descriptionColumn, typeColumn, locationColumn, contactColumn, startDateColumn, startTimeColumn, endDateColumn, endTimeColumn, customerId);
    }

    /**
     * Initializes the columns of the customersTable TableView and their values.
     */
    private void initializeCustomersTableColumns() {
        TableColumn<Customer, Integer> idColumn = new TableColumn<>(resources.getString("id"));
        TableColumn<Customer, String> nameColumn = new TableColumn<>(resources.getString("name"));
        TableColumn<Customer, String> phoneColumn = new TableColumn<>(resources.getString("phoneNumber"));
        TableColumn<Customer, String> addressColumn = new TableColumn<>(resources.getString("address"));
        TableColumn<Customer, String> postalColumn = new TableColumn<>(resources.getString("postalCode"));
        TableColumn<Customer, String> firstLevelDivColumn = new TableColumn<>(resources.getString("state"));

        idColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>( "phone"));
        firstLevelDivColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Customer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Customer, String> p) {
                return new ReadOnlyObjectWrapper(firstLevelDivs.get(p.getValue().getDivisionId()));
            }
        });

        customersTable.getColumns().addAll(idColumn, nameColumn, phoneColumn, addressColumn,  postalColumn, firstLevelDivColumn);
    }

    /**
     * Updates the customers in customersTable Table View.
     */
    private void updateCustomersTable() {
        customersTable.setItems(FXCollections.observableList(customerDao.getAllCustomers()));
    }

    /**
     * Filters the allAppointments field and returns the appointments set to start within 15 minutes of the current time.
     * A lambda is used within this method in order to simplify the code necessary for implementing the Predicate functional
     * interface used by the java.util.stream.filter method. The lambda takes one appointment argument and returns a boolean:
     * true if the appointment is set to start within the next MINUTES_DIFFERENCE minutes, false otherwise.
     * @return Arraylist of appointments set to start within 15 minutes.
     */
    private ArrayList<Appointment> getUpcomingAppointments() {
        final int MINUTES_DIFFERENCE = 15;

        updateAppointments();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        var appointments =  allAppointments.stream().filter(appointment -> {
            ZonedDateTime appointmentTime = appointment.getStart();

            return appointmentTime.isEqual(currentTime) ||
                    (appointmentTime.isAfter(currentTime) && appointmentTime.isBefore(currentTime.plusMinutes(MINUTES_DIFFERENCE))) ||
                    appointmentTime.isEqual(currentTime.plusMinutes(MINUTES_DIFFERENCE));

        }).collect(Collectors.toCollection(ArrayList::new));

        return appointments;
    }

    /**
     * Shows an alert displaying upcoming appointments.
     */
    private void displayUpcomingAppointments() {
        var upcomingAppointments = getUpcomingAppointments();

        if (upcomingAppointments.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, resources.getString("noUpcomingAppointments"));
            alert.setTitle(resources.getString("upcomingAppointments"));
            alert.setHeaderText(null);
            alert.showAndWait();
        } else  {
            try {
                Stage upcomingAppointmentsDisplay = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wgu/cristianreyes/upcomingAppointments.fxml"));
                Parent root = loader.load();

                UpcomingAppointments controller = loader.getController();
                controller.setTime(ZonedDateTime.now(ZoneId.systemDefault()));
                controller.setAppointments(upcomingAppointments);

                Scene appointmentsScene = new Scene(root);
                upcomingAppointmentsDisplay.setScene(appointmentsScene);
                upcomingAppointmentsDisplay.setTitle(resources.getString("upcomingAppointments"));
                upcomingAppointmentsDisplay.setAlwaysOnTop(true);
                upcomingAppointmentsDisplay.show();
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, resources.getString("upcomingAppointmentsFailure")).show();
            }
        }
    }

    /**
     * Updates the allAppointments field to current database state.
     */
    private void updateAppointments() {
        allAppointments = appointmentDao.getAllAppointments();
    }

    /**
     * Updates the data in appointmentsTable Table View.
     */
    private void updateAppointmentsTable() {
        updateAppointments();

        if(appointmentsFilter.getSelectedToggle() == monthlyAppointmentsRb) {
            setAppointmentFilters(startOfMonthFilter.toLocalDate(), startOfMonthFilter.plus(1, ChronoUnit.MONTHS).toLocalDate().minus(1, ChronoUnit.DAYS));
        } else if (appointmentsFilter.getSelectedToggle() == weeklyAppointmentsRb) {
            setAppointmentFilters(startOfWeekFilter.toLocalDate(), startOfWeekFilter.plus(1, ChronoUnit.WEEKS).toLocalDate().minus(1, ChronoUnit.DAYS));
        }
    }

    /**
     * Filters the appointmentsTable Table View according to the selected filter method: by month or week.
     * A lambda within this method takes the place of a Predicate interface implementation and is used by the FilteredList
     * constructor to create a FilteredList from the allAppointments field containing only those appointments within the current
     * filtered month/week being displayed. This FilteredList becomes the content of the appointmentsTable TableView.
     * @param startDate the date from which to begin displaying appointments.
     * @param endDate the latest date up to which appointments should be shown.
     */
    private void setAppointmentFilters(LocalDate startDate, LocalDate endDate) {
        if (appointmentsFilter.getSelectedToggle() == monthlyAppointmentsRb) {
            appointmentsShowingLabel.setText(resources.getString("monthOf") + " " +
                                             startDate.format(dateFormat) + " " +
                                             resources.getString("through") + " " +
                                             endDate.format(dateFormat));
        } else if (appointmentsFilter.getSelectedToggle() == weeklyAppointmentsRb){
            appointmentsShowingLabel.setText(resources.getString("weekOf") + " " +
                                             startDate.format(dateFormat) + " " +
                                             resources.getString("through") + " " +
                                             endDate.format(dateFormat));
        }

        appointmentsTable.setItems(new FilteredList(FXCollections.observableList(allAppointments), (o -> {
            Appointment appointment = (Appointment) o;

            LocalDate appointmentStartDate = appointment.getStart().toLocalDate();

            return appointmentStartDate.isEqual(startDate) || appointmentStartDate.isEqual(endDate) ||
                    (appointmentStartDate.isAfter(startDate) && appointmentStartDate.isBefore(endDate));

        })));
    }

    /**
     * Changes  data in the appointmentsTable Table View to display the previous month or week's appointments depending
     * on the filter currently applied to the appointmentsTable.
     */
    public void showPreviousFilteredAppointments() {
        if (appointmentsFilter.getSelectedToggle() == monthlyAppointmentsRb) {
            startOfMonthFilter = startOfMonthFilter.minus(1, ChronoUnit.MONTHS);
            setAppointmentFilters(startOfMonthFilter.toLocalDate(), startOfMonthFilter.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS).toLocalDate());
        } else {
            startOfWeekFilter = startOfWeekFilter.minus(1, ChronoUnit.WEEKS);
            setAppointmentFilters(startOfWeekFilter.toLocalDate(), startOfWeekFilter.plus(1, ChronoUnit.WEEKS).minus(1, ChronoUnit.DAYS).toLocalDate());
        }
    }

    /**
     * Changes  data in the appointmentsTable Table View to display the next month or week's appointments depending
     * on the filter currently applied to the appointmentsTable.
     */
    public void showNextFilteredAppointments() {
        if (appointmentsFilter.getSelectedToggle() == monthlyAppointmentsRb) {
            startOfMonthFilter = startOfMonthFilter.plus(1, ChronoUnit.MONTHS);
            setAppointmentFilters(startOfMonthFilter.toLocalDate(), startOfMonthFilter.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS).toLocalDate());
        } else {
            startOfWeekFilter = startOfWeekFilter.plus(1, ChronoUnit.WEEKS);
            setAppointmentFilters(startOfWeekFilter.toLocalDate(), startOfWeekFilter.plus(1, ChronoUnit.WEEKS).minus(1, ChronoUnit.DAYS).toLocalDate());
        }
    }

    /**
     * Changes various controls' actions depending on selectedTab.
     * If the customersTab is currently being viewed, the add, modify, and delete buttons will operate on customer data.
     * If the appointmentsTab is currently being viewed, the add, modify, and delete buttons will operate on appointment data.
     * If a tab other than these is selected, the add, modify, and delete buttons will disappear.
     * Lambdas are used within this method allowing single line statements for setting a control's action. This makes the code within more readable.
     * @param selectedTab The tab that is now selected.
     */
    private void updateTabs(Tab selectedTab) {
        if (selectedTab == customersTab) {
            addButton.setVisible(true);
            modifyButton.setVisible(true);
            deleteButton.setVisible(true);
            addButton.setOnAction(e -> addCustomer());
            modifyButton.setOnAction(e -> modifyCustomer());
            deleteButton.setOnAction(e -> deleteCustomer());
            if (customersTable.getSelectionModel().getSelectedItem() == null) {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        } else if (selectedTab == appointmentsTab) {
            addButton.setVisible(true);
            modifyButton.setVisible(true);
            deleteButton.setVisible(true);
            addButton.setOnAction(e -> addAppointment());
            modifyButton.setOnAction(e -> modifyAppointment());
            deleteButton.setOnAction(e -> deleteAppointment());
            if (appointmentsTable.getSelectionModel().getSelectedItem() == null) {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            } else {
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        } else {
            addButton.setVisible(false);
            modifyButton.setVisible(false);
            deleteButton.setVisible(false);
        }
    }

    /**
     * Launches a new window with a form to add a new appointment or update an existing appointment.
     * @param appointmentId The appointment ID of an appointment to be modified or null if a new appointment is to be added.
     */
    private void appointmentOperation(Integer appointmentId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wgu/cristianreyes/appointmentOperation.fxml"));

        AppointmentOperation controller = new AppointmentOperation();
        controller.setAppointmentsDb(appointmentDao);
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (appointmentId != null){
            controller.setOperation(AppointmentOperation.Operations.MODIFY);
            controller.setAppointment(appointmentId);
        } else {
            controller.setOperation(AppointmentOperation.Operations.ADD);
        }

        Scene scene = new Scene(root);
        Stage appointmentWindow = new Stage();
        appointmentWindow.setScene(scene);
        appointmentWindow.initModality(Modality.APPLICATION_MODAL);
        appointmentWindow.showAndWait();
    }

    /**
     * Obtains selected appointment from appointmentsTable and allows user to modify data in this appointment.
     */
    private void modifyAppointment() {
        Appointment selectedAppointment = (Appointment) appointmentsTable.getSelectionModel().getSelectedItem();
        appointmentOperation(selectedAppointment.getAppointmentId());
        updateAppointmentsTable();
    }

    /**
     * Obtains selected appointment from appointmentsTable and attempts to delete the appointment from the database.
     * An alert is shown to report the status of the delete.
     */
    private void deleteAppointment() {
        Appointment selectedAppointment = (Appointment) appointmentsTable.getSelectionModel().getSelectedItem();

        boolean deleteSuccess = false;

        if (confirmDelete()) {
            deleteSuccess = appointmentDao.deleteAppointment(selectedAppointment.getAppointmentId());
        }

        if (deleteSuccess) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(resources.getString("id") + ": " +
                                 selectedAppointment.getAppointmentId() + "\n" +
                                 resources.getString("type") + ": " +
                                 selectedAppointment.getType());
            alert.setHeaderText(resources.getString("appointmentDeleted"));
            alert.show();
            updateAppointmentsTable();
        } else {
            new Alert(Alert.AlertType.ERROR, resources.getString("appointmentDeleteError") +
                                                "\n" + resources.getString("id") + ": " +
                                                selectedAppointment.getAppointmentId()).show();
        }
    }

    /**
     * Displays a new window with a form for adding new appointments.
     */
    private void addAppointment() {
        appointmentOperation(null);
        updateAppointmentsTable();
    }
    
    /**
     * Displays a confirmation alert to confirm the deletion of a customer or appointment.
     * @return true if user clicks OK, false if user clicks Cancel for the confirmation alert.
     */
    private Boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (tabs.getSelectionModel().getSelectedItem() == customersTab) {
            alert.setTitle(resources.getString("delete") + " " + resources.getString("customer").toLowerCase());
            alert.setHeaderText(resources.getString("deleteCustomerNotification"));
            alert.setContentText(resources.getString("confirmationQuestion"));
        } else if (tabs.getSelectionModel().getSelectedItem() == appointmentsTab) {
             alert.setTitle(resources.getString("delete") + " " + resources.getString("appointment").toLowerCase());
             alert.setHeaderText(resources.getString("deleteAppointmentNotification"));
             alert.setContentText(resources.getString("confirmationQuestion"));
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Launches a new window with a form to add a new customer or update an existing customer.
     * @param customerId The customer ID of a customer to be modified or null if a new customer is to be added.
     */
    private void customerOperation(Integer customerId) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wgu/cristianreyes/customerOperation.fxml"));

        CustomerOperation controller = new CustomerOperation();
        controller.setCustomerDb(customerDao);
        loader.setController(controller);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (customerId != null){
            controller.setOperation(CustomerOperation.Operations.MODIFY);
            controller.setCustomer(customerId);
        } else {
            controller.setOperation(CustomerOperation.Operations.ADD);
        }

        Scene scene = new Scene(root);
        Stage customerWindow = new Stage();
        customerWindow.setScene(scene);
        customerWindow.initModality(Modality.APPLICATION_MODAL);
        customerWindow.showAndWait();

    }

    /**
     * Opens a window with a form for adding new customers.
     */
    public void addCustomer(){
        customerOperation(null);
        updateCustomersTable();
    }

    /**
     * Obtains selected customer from customersTable and allows user to modify the data for this customer.
     */
    private void modifyCustomer(){
        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        customerOperation(selectedCustomer.getId());
        updateCustomersTable();
    }

    /**
     * Obtains selected customer from customerTable and allows user to delete this customer. An alert notifies user of
     * the deletion operation's success or failure.
     */
    private void deleteCustomer() {
        boolean deleteSuccess = false;
        boolean deleteConfirmed = false;

        Customer selectedCustomer = (Customer) customersTable.getSelectionModel().getSelectedItem();
        deleteConfirmed = confirmDelete();

        if (deleteConfirmed) {
            if (customerDao.hasNoAppointments(selectedCustomer.getId())) {
                deleteSuccess = customerDao.deleteCustomer(selectedCustomer.getId());
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, resources.getString("customerWithAppointmentsDelete") +
                        "\n" + resources.getString("confirmationQuestion"));

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    for (Appointment appointment : customerDao.getCustomerAppointments(selectedCustomer.getId())) {
                        appointmentDao.deleteAppointment(appointment.getAppointmentId());
                    }
                    updateAppointmentsTable();
                    deleteSuccess = customerDao.deleteCustomer(selectedCustomer.getId());
                }
            }
        }

        if (deleteSuccess) {
            updateCustomersTable();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(resources.getString("id") + ": " + selectedCustomer.getId());
            alert.setHeaderText(resources.getString("customerDeleteSuccess"));
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(resources.getString("customerDeleteFailure"));
            alert.setContentText(resources.getString("id") + ": " + selectedCustomer.getId());
        }
    }

    /**
     * Clears the currently displayed reports from the reportsDisplay VBox and then generates a new report depending on
     * the user's report type selection using radioButtons.
     */
    @FXML
    public void generateReport() {
        clearReports();
        String title = "";

        if (monthTypeReportRb.isSelected()) {
            title = resources.getString("monthTypeReport");
            reportsDisplay.getChildren().add(reportTitle);
            displayMonthTypeReport();
        } else if (contactSchedulesRb.isSelected()) {
            title = resources.getString("contactSchedules");
            reportsDisplay.getChildren().add(reportTitle);
            displayContactSchedules();
        } else if (contactInteractionRb.isSelected()) {
            if (contactReportSelector.getValue() != null) {
                contactReportSelector.setStyle(null);
                title = resources.getString("contactInteraction");
                reportsDisplay.getChildren().add(reportTitle);
                displayContactCustomerInteraction(contactReportSelector.getValue().getId());
            } else {
                contactReportSelector.setStyle("-fx-border-color: red; -fx-border-radius: 2px");
            }
        }

        reportTitle.setText(title);
    }

    /**
     * Clears the reportsDisplay VBox.
     */
    private void clearReports() {
        reportsDisplay.getChildren().clear();
    }

    /**
     * Displays the schedule of each company contact in the reportsDisplayVBox
     */
    private void displayContactSchedules() {
        ArrayList<Contact> contacts = appointmentDao.getAllContacts();

        contacts.sort((contact1, contact2) -> contact1.getName().compareTo(contact2.getName()));

        for(Contact contact : contacts) {
            VBox scheduleDisplay = new VBox();
            var appointments = appointmentDao.getContactAppointments(contact.getId());

            appointments.sort((appointment1, appointment2) -> appointment1.getStart().compareTo(appointment2.getStart()));

            for (Appointment appointment : appointments) {
                if (appointment.getStart().isBefore(ZonedDateTime.now())) {
                    continue;
                }

                VBox appointmentDisplay = new VBox();

                appointmentDisplay.getChildren().addAll(
                    new Label(resources.getString("appointmentId") + ": " + appointment.getAppointmentId()),
                    new Label(resources.getString("title") + ": " + appointment.getTitle()),
                    new Label(resources.getString("type") + ": " + appointment.getType()),
                    new Label(resources.getString("description") + ": " + appointment.getDescription()),
                    new Label(resources.getString("startDate") + ": " + appointment.getStart().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))),
                    new Label(resources.getString("startTime") + ": " + appointment.getStart().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))),
                    new Label(resources.getString("endDate") + ": " + appointment.getEnd().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))),
                    new Label(resources.getString("endTime") + ": " + appointment.getEnd().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))),
                    new Label(resources.getString("customerId") + ": " + appointment.getCustomerId())
                );

                appointmentDisplay.setStyle("-fx-border-width: 2px; -fx-border-color: black");
                scheduleDisplay.getChildren().add(appointmentDisplay);
            }

            if (scheduleDisplay.getChildren().isEmpty()) {
                scheduleDisplay.getChildren().add(new Label(resources.getString("noAppointmentsForContact")));
            }
            TitledPane contactSchedule = new TitledPane(resources.getString("contactId") + ": " + contact.getId()
                                                        + ", " + resources.getString("contactName") + ": "
                                                        + contact.getName(), scheduleDisplay);
            contactSchedule.setExpanded(false);
            reportsDisplay.getChildren().add(contactSchedule);
        }
    }


    /**
     * Displays a report of appointment count by month and type in the reportsDisplay VBox.
     */
    private void displayMonthTypeReport() {
        updateAppointments();

        if (allAppointments.isEmpty()) {
            reportsDisplay.getChildren().add(new Label(resources.getString("noAppointmentsInDb")));
            return;
        }

        var report = monthTypeReport();

        ArrayList<LocalDate> months = new ArrayList(report.keySet());
        Collections.sort(months);

        for (LocalDate month : months) {
            TableView typeCounts = new TableView();
            typeCounts.setItems(FXCollections.observableList(report.get(month)));
            TableColumn typeColumn = new TableColumn(resources.getString("appointmentType"));
            TableColumn countColumn = new TableColumn(resources.getString("appointmentCount"));

            typeColumn.setCellValueFactory(new MapValueFactory("type"));
            countColumn.setCellValueFactory(new MapValueFactory("count"));
            typeCounts.getColumns().addAll(typeColumn, countColumn);

            TitledPane monthReport = new TitledPane(month.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)), typeCounts);
            monthReport.setPrefHeight(Region.USE_COMPUTED_SIZE);
            monthReport.setMaxHeight(Region.USE_PREF_SIZE);
            monthReport.setExpanded(false);
            reportsDisplay.getChildren().add(monthReport);
        }

    }

    /**
     * Generates a report of appointment counts by month and type
     * @return a HashMap containing the appointment counts by month and type.
     */
    private HashMap<LocalDate, ArrayList<Map>> monthTypeReport() {
        updateAppointments();

        HashMap<LocalDate, ArrayList<Map>> results = new HashMap<>();

        for (Appointment appointment : allAppointments) {
            LocalDate monthStart = appointment.getStart().withDayOfMonth(1).toLocalDate();
            String type = appointment.getType();


            if(results.containsKey(monthStart)) {
                Integer currentCount;
                ArrayList<Map> types = results.get(monthStart);

                var typeMatch = types.stream().filter(map -> map.get("type").equals(type)).findFirst().orElse(null);

                if (typeMatch != null) {
                    types.set(types.indexOf(typeMatch), Map.of("type", type, "count", ((Integer) typeMatch.get("count")) + 1));
                } else {
                    types.add(Map.of("type", type, "count", 1));
                }
            } else {
                results.put(monthStart, new ArrayList<>(Arrays.asList(Map.of("type", type, "count", 1))));
            }
        }

        return results;
    }

    /**
     * Generates a report on a selected contact based on their customer appointment data
     * @param contactId the contact ID of the contact whose customer interaction is being displayed.
     */
    private void displayContactCustomerInteraction(int contactId) {
        GridPane interactionDisplay = new GridPane();

        var contactAppointments = new ArrayList<Appointment>(
                                            appointmentDao.getContactAppointments(contactId).stream()
                                                                                            .filter(
                                                                                                appointment -> appointment.getStart().isBefore(ZonedDateTime.now(ZoneId.systemDefault())) ||
                                                                                                               appointment.getStart().isEqual(ZonedDateTime.now(ZoneId.systemDefault())))
                                                                                            .collect(Collectors.toList()));
        contactAppointments.sort((appointment1, appointment2) -> appointment1.getStart().compareTo(appointment2.getStart()));

        if (contactAppointments.isEmpty()) {
            reportsDisplay.getChildren().add(new Label(resources.getString("noAppointmentsForContact") + " " +
                                                          resources.getString("id") + ": " + contactId + ", " +
                                                          resources.getString("contactName") + ": " + appointmentDao.getContactName(contactId)));
            return;
        }

        HashMap<Integer, Integer> yearCounts = new HashMap<>();

        LocalDate firstAppointmentMonth = contactAppointments.get(0).getStart().with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        LocalDate firstAppointmentWeek = contactAppointments.get(0).getStart().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate();

        long monthsReported = firstAppointmentMonth.until(thisMonth, ChronoUnit.MONTHS) + 1;
        long weeksReported = firstAppointmentWeek.until(thisWeek, ChronoUnit.WEEKS) + 1;

        int yearOf;
        int thisMonthCount = 0, thisWeekCount = 0;

        for (Appointment appointment : contactAppointments) {
            if (appointment.getStart().isAfter(ZonedDateTime.now()))
                continue;

            ZonedDateTime start = appointment.getStart();
            yearOf = start.getYear();

            if (yearCounts.putIfAbsent(yearOf, 1) != null) {
                yearCounts.put(yearOf, yearCounts.get(yearOf) + 1);
            }

            if ((start.isAfter(thisMonth) || start.isEqual(thisMonth)) &&
                 start.isBefore(thisMonth.plusMonths(1))) {
                 thisMonthCount += 1;

                if ((start.isAfter(thisWeek) || start.isEqual(thisWeek)) &&
                     start.isBefore(thisWeek.plusWeeks(1)));
            }
        }

        Double monthlyAverage = (yearCounts.values().stream().reduce(0, (acc, count) -> acc += count)).doubleValue() / monthsReported;
        Double weeklyAverage = (yearCounts.values().stream().reduce(0, (acc, count) -> acc += count)).doubleValue() / weeksReported;

        interactionDisplay.add(new Label(resources.getString("appointmentsThisMonth")+ thisMonthCount), 0,0);
        interactionDisplay.add(new Label(resources.getString("monthlyAverage") + monthlyAverage), 1, 0);
        interactionDisplay.add(new Label(resources.getString("monthsReported") + monthsReported), 2, 0);
        interactionDisplay.add(new Label(resources.getString("appointmentsThisWeek") + thisWeekCount), 0, 1);
        interactionDisplay.add(new Label(resources.getString("weeklyAverage")+ weeklyAverage), 1, 1);
        interactionDisplay.add(new Label(resources.getString("weeksReported")+ weeksReported), 2, 1);
        interactionDisplay.setHgap(10);
        interactionDisplay.setVgap(10);
        interactionDisplay.setPrefWidth(Region.USE_COMPUTED_SIZE);
        interactionDisplay.setMaxWidth(Region.USE_PREF_SIZE);
        reportsDisplay.getChildren().add(interactionDisplay);
    }


}
