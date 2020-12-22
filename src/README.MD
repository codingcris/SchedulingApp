# Scheduling App

## Description
This is a GUI based scheduling application. It allows members of an organization to view, modify, delete and add customer and appointment data to their database. Currently, this application supports the English and French languages. 

## Usage
Upon application launch the user must log in to the program using a valid username and password as provided by their organization. If login is successful, the login window will close and an alert will notify you of any upcoming appointments (appointments set to start within 15 minutes of login). Once you close this alert, you will be able to interact with the home screen. 

The home screen design is simple. A group of tabs is shown under the application title: the Customers Tab, Appointments Tab, and the Reports Tab.The home screen by default is open to the Customers Tab. The Customer Tab displays a table with all of an organization's customer data underneath the group of tabs. Underneath this table, there are three buttons: add, modify and delete. The add button is always enabled andif you are in the Customers Tab, will allow you to add a new customer through a new customer form which opens in a new window. The modify and delete buttons are disabled until a customer row is selected from the customer table. The modify button will open a modify customer form populated with the selected Customer's data. The delete button will prompt for confirmation before deleting a customer.

Switching to the Appointments Tab allows you to see your organization's appointments. In the Appointments Tab directly underneath 
the group of tabs are two filter by buttons: filter by month and filter by week. Underneath these filter buttons is a heading 
which lets you know which date range of appointments is being displayed in the appointments table. The appointments table is directly underneath 
the heading. To the left of the heading is a previous button which allows you to view appointments in the previous month or week 
(depending on which filter by button is selected). To the right of the heading is a next button which allows you to view appointments in the next
month or week. By default, filter by month option is selected, and the appointments table will initially show the appointments for the current
month. Underneath the appointments table will be the same three buttons available in the Customers Tab: add, modify, and delete.
Again, the add button is always enabled and in the Appointments Tab will allow you to add a new appointment through an appointment 
form in a new window. The modify and delete buttons will become enabled only when there is an appointment row selected in the appointments table.
The modify button will open a modify appointment form populated with the selected appointment's data. The delete button will prompt for confirmation before deleting a customer.

Switching to the Reports Tab allows the user to generate various reports based on the organization's data. The type of report is selected
underneath the grouping of tabs. Three options are currently selectable: Appointment Counts by Month/Type, Contact Schedules, and Contact/Customer Interaction.
Underneath the report selection options is the generate report button. Underneath the generate report button is a large area for the report display.
Initially the report display will be empty. Once a selection is made, and the generate button is clicked, a report will be displayed in the report display area.
The Contact/Customer Interaction option differs from the other two because it requires an additional contact field selected underneath the option button.

### Reports
The Appointment Counts by Month/Type report will generate a report in the form of various collapsible tables. If no appointments are in the database, 
a message will be displayed instead. Each collapsible table represents a different month in which appointments were scheduled within the organization.
Tables are collapsed to begin with, click their labels, displaying the date of the month corresponding to the table to expand the tables. 
The tables have two columns: Appointment Type, Appointment Count. For each appointment type scheduled in a particular month, 
the number of appointments of that type will be displayed.

The Contact Schedules report will display a schedule for every appointment in the organization. Schedules will display upcoming appointments 
vertically in boxes. The appointments will be sorted from the closest appointment to the current date to the furthest from the current date.
Appointents which have already passed will not be displayed.

The Contact/Customer Interaction report will generate a textual report for one particular chosen contact. If the contact has appointments
in the database that correspond to them, these appointments will be used to generate various statistics: appointments this month, 
average appointments per month, months reported, appointments this week, weekly average appointments, and weeks reported. These statistics
can help a contact and the organization better understand the level of contact to customer interaction.

## Author
This application was developed by Cristian Reyes, a student at Western Governor's University.
Contact me: crey125@wgu.edu

## Version
Application Version: 1
Date: 12/21/2020

## Development Environment
IntelliJ IDEA 2020.2.4 (Community Edition)
Build #IC-202.8194.7, built on November 24, 2020

JDK: 11.0.9
JavaFX: 11.0.2