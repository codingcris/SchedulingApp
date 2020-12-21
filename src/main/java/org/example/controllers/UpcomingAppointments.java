package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.example.application.appointment.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class UpcomingAppointments {
    public class AppointmentFormatCell extends ListCell<Appointment> {

        public AppointmentFormatCell() {    }

        @Override protected void updateItem(Appointment item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                int id = item.getAppointmentId();
                LocalDate date = item.getStart().toLocalDate();
                LocalTime time = item.getStart().toLocalTime();

                VBox dataContainer = new VBox();
                dataContainer.getChildren().addAll(
                        new Label("Appointment ID: " + id),
                        new Label("Start Date: " + date),
                        new Label("Start Time: " + time)
                );

                setGraphic(dataContainer);
            }
        }
    }


    private ArrayList<Appointment> upcomingAppointments;
    private ZonedDateTime time;

    @FXML
    private ListView<Appointment> upcomingAppointmentsListView;

    public void initialize() {
        upcomingAppointmentsListView.setCellFactory(new Callback<ListView<Appointment>, ListCell<Appointment>>() {
            @Override
            public ListCell<Appointment> call(ListView<Appointment> appointmentListView) {
                return new AppointmentFormatCell();
            }
        });
    }


    public void setAppointments(ArrayList<Appointment> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;

        upcomingAppointmentsListView.setItems(FXCollections.observableList(upcomingAppointments));
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }
}
